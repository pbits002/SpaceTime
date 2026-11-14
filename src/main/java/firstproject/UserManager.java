
package firstproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.mindrot.jbcrypt.BCrypt;

import com.sun.net.httpserver.HttpExchange;

import static firstproject.jdbcConnection.q;
import static firstproject.jdbcConnection.query;

public final class UserManager {
    public boolean signup(String id, String password, String activationCode) throws SQLException {
        if (id == null || id.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Enter valid ID and password");
        }
        if (activationCode == null || !activationCode.equals("IIITVADODARA")) {
            throw new SecurityException("Invalid activation code");
        }

        String sql = "UPDATE users SET password_hash=? " + "WHERE id=? AND password_hash IS NULL AND role<>'ADMIN'";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(11));

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hash);
            statement.setString(2, id);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean login(String id, String password) throws SQLException {
        if (id == null || password == null) {
            return false;
        }
        String sql = "SELECT password_hash FROM users WHERE id=?";

        try (Connection connection = jdbcConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                String hash = rs.getString("password_hash");
                return hash != null && BCrypt.checkpw(password, hash);
            }
        }
    }

    private static final Map<String, String> tokens = new ConcurrentHashMap<>();

    public static String field(Map<String, String> m, String k) {
        return m.getOrDefault(k, "").trim();
    }

    private static Map<String, String> form(HttpExchange e) throws IOException {
        String raw = new String(e.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> m = new HashMap<>();
        for (String pair : raw.split("&")) {
            String x[] = pair.split("=", 2);
            if (x.length > 0) {
                m.put(URLDecoder.decode(x[0], StandardCharsets.UTF_8),
                        x.length > 1 ? URLDecoder.decode(x[1], StandardCharsets.UTF_8) : "");
            }
        }
        return m;
    }

    public static void staticFile(HttpExchange e) throws IOException {
        String path = e.getRequestURI().getPath();
        if (path.equals("/")) {
            path = "/index.html";
        }
        if (!Set.of("/index.html", "/style.css", "/app.js").contains(path)) {
            send(e, 404, "text/plain", "Not found");
            return;
        }
        try (InputStream in = UserManager.class.getResourceAsStream("/web" + path)) {
            if (in == null) {
                send(e, 404, "text/plain", "Not found");
                return;
            }
            byte[] bytes = in.readAllBytes();
            e.getResponseHeaders().set("Content-Type", path.endsWith(".css") ? "text/css; charset=utf-8"
                    : path.endsWith(".js") ? "text/javascript; charset=utf-8" : "text/html; charset=utf-8");
            e.sendResponseHeaders(200, bytes.length);

            try (OutputStream out = e.getResponseBody()) {
                out.write(bytes);
            }
        }
    }

    public static void send(HttpExchange e, int status, String type, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        e.getResponseHeaders().set("Content-Type", type);
        e.getResponseHeaders().set("Cache-Control", "no-store");
        e.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = e.getResponseBody()) {
            out.write(bytes);
        }
    }

    public static void api(HttpExchange e) throws IOException {
        String path = e.getRequestURI().getPath().substring(5);
        String method = e.getRequestMethod();
        try (Connection c = jdbcConnection.getConnection()) {
            Map<String, String> data = method.equals("POST") ? form(e) : Map.of();

            // CREATE PASSWORD
            if (path.equals("signup") && method.equals("POST")) {
                String activation = field(data, "activation");
                if (activation.isEmpty()) {
                    activation = field(data, "activationCode");
                }

                boolean created = new UserManager().signup(field(data, "id"), field(data, "password"), activation);

                if (!created) {
                    throw new IllegalArgumentException("Unknown ID or account already activated");
                }

                send(e, 200, "application/json", "{\"ok\":true}");
                return;
            }

            // LOGIN
            if (path.equals("login") && method.equals("POST")) {
                String uid = field(data, "id");
                if (!new UserManager().login(uid, field(data, "password"))) {
                    send(e, 401, "application/json", "{\"error\":\"Wrong ID or password, or account not activated\"}");
                    return;
                }

                String json = query(c,
                        "SELECT u.id,u.name,u.role," +
                                "COALESCE(s.division,'') division," +
                                "COALESCE(s.semester,0) semester " +
                                "FROM users u " +
                                "LEFT JOIN students s ON s.id=u.id " +
                                "WHERE u.id=?",
                        uid);
                if (json.equals("[]")) {
                    send(e, 401, "application/json", "{\"error\":\"Wrong ID or password\"}");
                    return;
                }
                String token = UUID.randomUUID().toString();
                tokens.put(token, uid);
                send(e, 200, "application/json",
                        "{\"token\":" + q(token) + ",\"user\":" + json.substring(1, json.length() - 1) + "}");
                return;
            }

            // AUTHENTICATION
            String auth = e.getRequestHeaders().getFirst("Authorization");

            String id = auth != null && auth.startsWith("Bearer ") ? tokens.get(auth.substring(7)) : null;
            if (id == null) {
                send(e, 401, "application/json", "{\"error\":\"Login required\"}");
                return;
            }
            String role = "";
            String division = "";

            try (PreparedStatement ps = c.prepareStatement("SELECT u.role,COALESCE(s.division,'') " +
                    "FROM users u " +
                    "LEFT JOIN students s ON s.id=u.id " +
                    "WHERE u.id=?")) {
                ps.setString(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new SecurityException("Unknown user");
                    }
                    role = rs.getString(1);
                    division = rs.getString(2);
                }
            }
            String result;
            switch (path + " " + method) {
                case "timetable GET":
                    result = TimeTable.timetable(c, id, role, division, data);
                    break;

                case "room-status POST":
                    result = Classroom.room_status(c, id, role, division, data);
                    break;

                case "rooms GET":
                    result = Classroom.rooms(c, id, role, division, data);
                    break;

                case "notifications GET":
                    result = Notification.notifications(c, id, role, division, data);
                    break;

                case "send-notification POST":
                    result = Notification.sendTargetedNotification(c, id, role, data);
                    break;

                case "issues GET":
                    result = Issue.issues_get(c, id, role, division, data);
                    break;

                case "issues POST":
                    result = Issue.issues_post(c, id, role, division, data);
                    break;

                case "bookings GET":
                    result = BookingRoomRequest.bookings_get(c, id, role, division, data);
                    break;

                case "bookings POST":
                    result = BookingRoomRequest.bookings_post(c, id, role, division, data);
                    break;

                case "approve POST":
                    result = Administration.approve(c, id, role, division, data);
                    break;

                case "cancel POST":
                    result = ClassSession.cancel(c, id, role, division, data);
                    break;

                case "shift POST":
                    result = ClassSession.shift(c, id, role, division, data);
                    break;

                case "logout POST":
                    if (auth != null && auth.startsWith("Bearer ")) {
                        tokens.remove(auth.substring(7));
                    }
                    result = "{\"ok\":true}";
                    break;

                default:
                    send(e, 404, "application/json", "{\"error\":\"Unknown endpoint\"}");
                    return;
            }
            send(e, 200, "application/json", result);
        } catch (SecurityException ex) {
            send(e, 403, "application/json", "{\"error\":" + q(ex.getMessage()) + "}");
        } catch (Exception ex) {
            ex.printStackTrace();
            send(e, 400, "application/json", "{\"error\":" +
                    q(ex instanceof SQLException ? "Database operation failed: " + ex.getMessage() : ex.getMessage())
                    + "}");
        }
    }
}
