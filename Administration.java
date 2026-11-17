package firstproject;
import java.sql.*;
import java.util.*;
import java.time.*;
import static firstproject.jdbcConnection.*;
import static firstproject.UserManager.field;
import com.sun.net.httpserver.*;
import java.net.*;
import java.util.concurrent.*;
/** Admin approvals and application startup. Other workflows live in their domain classes. */
public final class Administration extends User {
    public Administration(String id, String name) { super(id, name, "ADMIN"); }
    @Override public String dashboardTitle() { return "Administration"; }
 public static String approve(Connection c, String id, String role, String division, Map<String,String> data) throws Exception {
  String result;
  if(!role.equals("ADMIN"))throw new SecurityException("Admin only");
     synchronized(Classroom.ROOM_LOCK){c.setAutoCommit(false);try{
      String booking=field(data,"id");String conflict=query(c,"SELECT b.id FROM bookings b JOIN bookings target ON target.id=? WHERE b.id<>target.id AND b.room_code=target.room_code AND b.booking_date=target.booking_date AND b.status='APPROVED' AND b.start_time<target.end_time AND b.end_time>target.start_time UNION SELECT cs.id FROM class_sessions cs JOIN bookings target ON target.id=? WHERE cs.room_code=target.room_code AND cs.class_date=target.booking_date AND cs.status='ACTIVE' AND cs.start_time<target.end_time AND cs.end_time>target.start_time",booking,booking);
      if(!conflict.equals("[]"))throw new IllegalArgumentException("Room already occupied at this time");
      if(update(c,"UPDATE bookings SET status='APPROVED' WHERE id=? AND status='PENDING'",booking)!=1)throw new IllegalArgumentException("Booking not pending");c.commit();
     }catch(Exception ex){c.rollback();throw ex;}finally{c.setAutoCommit(true);}}
     result="{\"ok\":true}";
  return result;
 }
 public static void main(String[] args) throws Exception {
  HttpServer server=HttpServer.create(new InetSocketAddress(8080),0);
  server.createContext("/api/", UserManager::api);
  server.createContext("/", UserManager::staticFile);
  server.setExecutor(Executors.newFixedThreadPool(12));
  server.start();
  Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(Notification::sendClassReminders,0,60,TimeUnit.SECONDS);
  System.out.println("SpaceTime running at http://localhost:8080");
 }
}
