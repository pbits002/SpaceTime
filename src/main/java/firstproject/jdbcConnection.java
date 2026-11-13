package firstproject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/** Single responsibility: create JDBC connections; callers must close them. */
public final class jdbcConnection {
    private jdbcConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv().getOrDefault("DB_URL",
                "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/SpaceTime*?sslMode=REQUIRED");
        String username = System.getenv().getOrDefault("DB_USER", "4EDq3eeeBSXPx94.root");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "x53P86lG7ZPe6ddx");
        return DriverManager.getConnection(url, username, password);
    }

    public static String esc(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    public static String q(String s) {
        return "\"" + esc(s) + "\"";
    }

    public static String list(ResultSet rs) throws SQLException {
        StringBuilder b = new StringBuilder("[");
        ResultSetMetaData md = rs.getMetaData();
        int count = md.getColumnCount();
        while (rs.next()) {
            if (b.length() > 1)
                b.append(',');
            b.append('{');
            for (int i = 1; i <= count; i++) {
                if (i > 1)
                    b.append(',');
                b.append(q(md.getColumnLabel(i))).append(':');
                Object v = rs.getObject(i);
                b.append(v == null ? "null" : q(v.toString()));
            }
            b.append('}');
        }
        return b.append(']').toString();
    }

    public static String query(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return list(rs);
            }
        }
    }

    public static int update(Connection c, String sql, Object... args) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++)
                ps.setObject(i + 1, args[i]);
            return ps.executeUpdate();
        }
    }

    public static void notifyDivision(Connection c, String division, String message) throws SQLException {
        update(c,
                "INSERT INTO notifications(recipient_id,message) SELECT u.id,? FROM users u JOIN students s ON u.id=s.id WHERE s.division=?",
                message, division);
    }

}
