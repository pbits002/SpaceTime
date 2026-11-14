
package firstproject;

import java.sql.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import static firstproject.jdbcConnection.getConnection;
import static firstproject.jdbcConnection.notifyDivision;
import static firstproject.jdbcConnection.query;
import static firstproject.jdbcConnection.update;

public final class Notification 
{
        private final long id;
        private final String recipientId;
        private final String message;
        private final LocalDateTime createdAt;
        private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
        private static final Set<String> VALID_DIVISIONS= Set.of("CSE1","CSE2","AI","IT","ECE");
        private static final Set<String> sentReminders= ConcurrentHashMap.newKeySet();

        public Notification(long id,String recipientId,String message,LocalDateTime createdAt) 
        {
                this.id = id;
                this.recipientId = recipientId;
                this.message = message;
                this.createdAt = createdAt;
        }

        public long getId() { return id;}
        public String getRecipientId() { return recipientId;}
        public String getMessage() { return message;}
        public LocalDateTime getCreatedAt() { return createdAt;}

        // Send notification to selected division
        public static String sendTargetedNotification(Connection c,String id,String role,Map<String, String> data) throws Exception 
        {
                if (!"FACULTY".equals(role) && !"ADMIN".equals(role)) 
                {
                        throw new SecurityException("Only Faculty and Admin can send notifications");
                }
                String division = data.getOrDefault("division", "").trim().toUpperCase();
                String message = data.getOrDefault("message", "").trim();

                if (division.isEmpty()) 
                {
                        throw new IllegalArgumentException("Please select a division");
                }

                if (!VALID_DIVISIONS.contains(division)) 
                {
                        throw new IllegalArgumentException("Invalid division selected");
                }

                if (message.isEmpty()) 
                {
                        throw new IllegalArgumentException("Please enter a notification message");
                }

                if (message.length()>1000) 
                {
                        throw new IllegalArgumentException("Message cannot exceed 1000 characters");
                }

                String sql="INSERT INTO notifications (recipient_id, message) " +
                                "SELECT s.id, ? " +
                                "FROM students s " +
                                "JOIN users u ON u.id = s.id " +
                                "WHERE s.division = ?";

                try (PreparedStatement ps=c.prepareStatement(sql)) 
                {
                        ps.setString(1,message);
                        ps.setString(2,division);
                        int sent=ps.executeUpdate();
                        if (sent==0) 
                        {
                                throw new IllegalArgumentException("No students found in selected division");
                        }
                        return "{\"ok\":true,\"sent\":" + sent + "}";
                }
        }
        // Automatic class start and end reminders
        public static void sendClassReminders() 
        {
                LocalDate today= LocalDate.now(ZONE);
                LocalTime now= LocalTime.now(ZONE);

                String currentDay= today.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

                String currentTime= String.format("%02d:%02d",now.getHour(),now.getMinute());

                String sql="SELECT cs.id, cs.division, cs.faculty_id, " +
                                "cs.subject_code, cs.start_time, cs.end_time, " +
                                "GROUP_CONCAT(cr.room_code " +
                                "ORDER BY cr.room_code SEPARATOR ', ') AS rooms " +
                                "FROM class_sessions cs " +
                                "LEFT JOIN class_session_rooms cr " +
                                "ON cr.session_id = cs.id " +
                                "WHERE cs.day_of_week = ? " +
                                "AND cs.status = 'ACTIVE' " +
                                "AND (TIME_FORMAT(cs.start_time,'%H:%i') = ? " +
                                "OR TIME_FORMAT(cs.end_time,'%H:%i') = ?) " +
                                "GROUP BY cs.id, cs.division, cs.faculty_id, " +
                                "cs.subject_code, cs.start_time, cs.end_time";

                try (Connection c=getConnection();PreparedStatement ps=c.prepareStatement(sql)) 
                {
                        ps.setString(1,currentDay);
                        ps.setString(2,currentTime);
                        ps.setString(3,currentTime);
                        try(ResultSet rs=ps.executeQuery()) 
                        {
                                while(rs.next()) 
                                {
                                        LocalTime start= rs.getTime("start_time").toLocalTime();
                                        LocalTime end= rs.getTime("end_time").toLocalTime();
                                        boolean starting= start.getHour()==now.getHour() && start.getMinute()==now.getMinute();
                                        boolean ending = end.getHour()==now.getHour() && end.getMinute()==now.getMinute();

                                        if(!starting && !ending) { continue;}
                                        String event=starting ? "start" : "end";
                                        String key=today + ":" + rs.getLong("id") + ":" + event;

                                        if(!sentReminders.add(key)) { continue;}
                                        String rooms = rs.getString("rooms");

                                        if (rooms==null) 
                                        {
                                                rooms="Not assigned";
                                        }

                                        String message=rs.getString("subject_code") + (starting ? " started" : " ended") + " in room " + rooms;
                                        try 
                                        {
                                                notifyDivision(c,rs.getString("division"),message);
                                                update(c,"INSERT INTO notifications " + "(recipient_id, message) VALUES (?, ?)",
                                                        rs.getString("faculty_id"),message);
                                        } 
                                        catch(SQLException ex) 
                                        {
                                                sentReminders.remove(key);
                                                throw ex;
                                        }
                                }
                        }
                } 
                catch(Exception ex) 
                {
                        System.err.println("Reminder check: " + ex.getMessage());
                }
        }

        // Fetch only logged-in user's notifications
        public static String notifications(Connection c,String id,String role,String division,Map<String, String> data) throws Exception 
        {
                return query(c,"SELECT id, message, created_at " +
                                "FROM notifications " +
                                "WHERE recipient_id = ? " +
                                "ORDER BY id DESC LIMIT 30",id);
        }
}
