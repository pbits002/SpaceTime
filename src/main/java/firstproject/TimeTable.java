
package firstproject;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import static firstproject.jdbcConnection.query;

public final class TimeTable 
{
        private final String ownerId;
        private final List<ClassSession> sessions;

        public TimeTable(String ownerId,List<ClassSession> sessions) 
        {
                this.ownerId = ownerId;
                this.sessions = List.copyOf(sessions);
        }

        public String getOwnerId() { return ownerId;}
        public List<ClassSession> getSessions() { return sessions;}

        public static String timetable(Connection c,String id,String role,String division,Map<String,String> data) throws Exception 
        {
                String sql="SELECT cs.id, " +
                                "cs.division, " +
                                "s.name AS subject, " +
                                "u.name AS faculty, " +
                                "GROUP_CONCAT(DISTINCT cr.room_code " +
                                "ORDER BY cr.room_code SEPARATOR ', ') AS room, " +
                                "cs.day_of_week AS day, " +
                                "cs.start_time AS start, " +
                                "cs.end_time AS end, " +
                                "cs.status " +
                                "FROM class_sessions cs " +
                                "JOIN subjects s ON s.code = cs.subject_code " +
                                "JOIN users u ON u.id = cs.faculty_id " +
                                "LEFT JOIN class_session_rooms cr " +
                                "ON cr.session_id = cs.id ";

                String groupBy=" GROUP BY cs.id, cs.division, s.name, " +
                                "u.name, cs.day_of_week, " +
                                "cs.start_time, cs.end_time, cs.status ";

                String orderBy=" ORDER BY FIELD(cs.day_of_week, " +
                                "'Monday','Tuesday','Wednesday'," +
                                "'Thursday','Friday','Saturday','Sunday'), " +
                                "cs.start_time";

                if("FACULTY".equals(role)) 
                {
                        return query(c,sql + "WHERE cs.faculty_id = ? " + groupBy + orderBy,id);
                } 
                else if("ADMIN".equals(role)) 
                {
                        return query(c,sql + groupBy + orderBy);
                } 
                else 
                {
                        return query(c,sql + "WHERE cs.division = ? " + groupBy + orderBy,division);
                }
        }
}
