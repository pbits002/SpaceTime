
package firstproject;

import java.sql.*;
import java.time.*;
import java.util.*;
import static firstproject.UserManager.field;
import static firstproject.jdbcConnection.notifyDivision;
import static firstproject.jdbcConnection.query;
import static firstproject.jdbcConnection.update;

public final class ClassSession 
{
    private final long id;
    private final String division;
    private final String subjectCode;
    private final String facultyId;
    private final List<String> roomCodes;
    private final DayOfWeek dayOfWeek;
    private final LocalTime start;
    private final LocalTime end;

    public ClassSession(long id,String division,String subjectCode,String facultyId,List<String> roomCodes,DayOfWeek dayOfWeek,LocalTime start,LocalTime end) 
    {
        this.id= id;
        this.division= division;
        this.subjectCode= subjectCode;
        this.facultyId= facultyId;
        this.roomCodes= List.copyOf(roomCodes);
        this.dayOfWeek= dayOfWeek;
        this.start= start;
        this.end= end;
    }

    public long getId() { return id;}
    public String getDivision() { return division;}
    public String getSubjectCode() { return subjectCode;}
    public String getFacultyId() { return facultyId;}
    public List<String> getRoomCodes() { return roomCodes;}
    public DayOfWeek getDayOfWeek() { return dayOfWeek;}
    public LocalTime getStart() { return start;}
    public LocalTime getEnd() { return end;}

    public static String cancel(Connection c,String id,String role,String division,Map<String, String> data) throws Exception 
    {
        if(!Set.of("FACULTY", "ADMIN").contains(role)) 
        {
            throw new SecurityException("Not allowed");
        }
        String sessionId = field(data, "id");

        if (!role.equals("ADMIN")) 
        {
            String owner=query(c,"SELECT id FROM class_sessions " + "WHERE id=? AND faculty_id=?",sessionId,id);
            if (owner.equals("[]")) 
            {
                throw new SecurityException("Not your class");
            }
        }
        c.setAutoCommit(false);
        try 
        {
            String div = getDivision(c, sessionId);
            int updated = update(c,
                                "UPDATE class_sessions " +
                                "SET status='CANCELLED' " +
                                "WHERE id=? AND status='ACTIVE'",sessionId);
            if (updated!=1) 
            {
                throw new IllegalArgumentException("Session unavailable");
            }
            notifyDivision(c,div,"Weekly class #" + sessionId + " has been cancelled.");
            c.commit();
            return "{\"ok\":true}";
        } 
        catch(Exception ex) 
        {
            c.rollback();
            throw ex;
        } 
        finally 
        {
            c.setAutoCommit(true);
        }
    }

    public static String shift(Connection c,String id,String role,String division,Map<String, String> data)throws Exception 
    {
        if (!Set.of("FACULTY", "ADMIN").contains(role)) 
        {
            throw new SecurityException("Not allowed");
        }
        String sessionId = field(data, "id");
        String newRoom = field(data, "room");

        if (!role.equals("ADMIN")) 
        {
            String owner=query(c,"SELECT id FROM class_sessions " + "WHERE id=? AND faculty_id=?",sessionId,id);
            if (owner.equals("[]")) 
            {
                throw new SecurityException("Not your class");
            }
        }
        synchronized(Classroom.ROOM_LOCK) 
        {
            c.setAutoCommit(false);
            try 
            {
                String collision = query(c,
                                        "SELECT other.id " +
                                        "FROM class_sessions other " +
                                        "JOIN class_session_rooms r " +
                                        "ON r.session_id=other.id " +
                                        "JOIN class_sessions target " +
                                        "ON target.id=? " +
                                        "WHERE other.id<>target.id " +
                                        "AND r.room_code=? " +
                                        "AND other.day_of_week=target.day_of_week " +
                                        "AND other.status='ACTIVE' " +
                                        "AND other.start_time<target.end_time " +
                                        "AND other.end_time>target.start_time",
                                        sessionId,newRoom);
                if (!collision.equals("[]")) 
                {
                    throw new IllegalArgumentException("New room is occupied");
                }
                String div=getDivision(c, sessionId);
                String active=query(c,"SELECT id FROM class_sessions " + "WHERE id=? AND status='ACTIVE'",sessionId);

                if (active.equals("[]")) 
                {
                    throw new IllegalArgumentException("Session unavailable");
                }
                update(c,"DELETE FROM class_session_rooms " +"WHERE session_id=?",sessionId);
                update(c,"INSERT INTO class_session_rooms " +"(session_id,room_code) VALUES (?,?)",sessionId,newRoom);
                notifyDivision(c,div,"Weekly class #" + sessionId +" shifted to room " + newRoom);
                c.commit();
                return "{\"ok\":true}";
            } 
            catch(Exception ex) 
            {
                c.rollback();
                throw ex;
            } 
            finally 
            {
                c.setAutoCommit(true);
            }
        }
    }

    private static String getDivision(Connection c,String sessionId)throws SQLException 
    {
        try (PreparedStatement ps = c.prepareStatement("SELECT division FROM class_sessions WHERE id=?")) 
        {
            ps.setString(1,sessionId);
            try (ResultSet rs=ps.executeQuery()) 
            {
                if (!rs.next()) 
                {
                    throw new IllegalArgumentException("Session not found");
                }
                return rs.getString("division");
            }
        }
    }
}
