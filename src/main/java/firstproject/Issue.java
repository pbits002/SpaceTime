package firstproject;

import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import static firstproject.UserManager.field;
import static firstproject.jdbcConnection.query;
import static firstproject.jdbcConnection.update;

public final class Issue {
   private final long id;
   private final String roomCode, reporterId, description, status;

   public Issue(long id, String roomCode, String reporterId, String description, String status) {
      this.id = id;
      this.roomCode = roomCode;
      this.reporterId = reporterId;
      this.description = description;
      this.status = status;
   }

   public long getId() {
      return id;
   }

   public String getRoomCode() {
      return roomCode;
   }

   public String getReporterId() {
      return reporterId;
   }

   public String getDescription() {
      return description;
   }

   public String getStatus() {
      return status;
   }

   public static String issues_get(Connection c, String id, String role, String division, Map<String, String> data)
         throws Exception {
      String result;
      if (!role.equals("ADMIN"))
         throw new SecurityException("Admin only");
      result = query(c, "SELECT * FROM issues ORDER BY id DESC");
      return result;
   }

   public static String issues_post(Connection c, String id, String role, String division, Map<String, String> data)
         throws Exception {
      String result;
      if (!Set.of("CR", "FACULTY", "ADMIN").contains(role))
         throw new SecurityException("Only CR, faculty and admin may report");
      update(c, "INSERT INTO issues(room_code,reporter_id,description,status) VALUES(?,?,?,'OPEN')",
            field(data, "room"), id, field(data, "description"));
      result = "{\"ok\":true}";
      return result;
   }
}
