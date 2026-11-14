
package firstproject;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static firstproject.UserManager.field;
import static firstproject.jdbcConnection.query;
import static firstproject.jdbcConnection.update;

public final class Classroom {

    public static final Object ROOM_LOCK = new Object();

    private static final ZoneId ZONE =
            ZoneId.of("Asia/Kolkata");

    private final String code;
    private final int capacity;

    // =====================================
    // CONSTRUCTOR
    // =====================================

    public Classroom(String code, int capacity) {
        this.code = Objects.requireNonNull(code);
        this.capacity = capacity;
    }

    public String getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Classroom
                && code.equals(((Classroom) o).code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    // =====================================
    // CHECK CURRENT ROOM OCCUPANCY
    // =====================================

    private static boolean isRoomOccupied(
            Connection c,
            String room
    ) throws SQLException {

        ZonedDateTime now = ZonedDateTime.now(ZONE);

        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        String day = date.getDayOfWeek()
                .getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                );

        // Check scheduled lectures.
        // Room code belongs to class_session_rooms,
        // not directly to class_sessions.

        String lectureSql =
                "SELECT 1 " +
                "FROM class_sessions cs " +
                "JOIN class_session_rooms csr " +
                "ON csr.session_id = cs.id " +
                "WHERE csr.room_code = ? " +
                "AND cs.status = 'ACTIVE' " +
                "AND cs.day_of_week = ? " +
                "AND cs.start_time <= ? " +
                "AND cs.end_time > ? " +
                "LIMIT 1";

        if (!query(
                c,
                lectureSql,
                room,
                day,
                time,
                time
        ).equals("[]")) {
            return true;
        }

        // Check approved room bookings.

        String bookingSql =
                "SELECT 1 " +
                "FROM bookings b " +
                "WHERE b.room_code = ? " +
                "AND b.booking_date = ? " +
                "AND b.status = 'APPROVED' " +
                "AND b.start_time <= ? " +
                "AND b.end_time > ? " +
                "LIMIT 1";

        return !query(
                c,
                bookingSql,
                room,
                date,
                time,
                time
        ).equals("[]");
    }

    // =====================================
    // UPDATE ROOM STATUS
    // =====================================

    public static String room_status(
            Connection c,
            String id,
            String role,
            String division,
            Map<String, String> data
    ) throws Exception {

        // Only CR, Faculty and Admin can update.

        if (!Set.of(
                "CR",
                "FACULTY",
                "ADMIN"
        ).contains(role)) {

            throw new SecurityException(
                    "Not allowed"
            );
        }

        String room = field(data, "room");
        String status = field(data, "status")
                .toUpperCase(Locale.ROOT);

        if (!Set.of(
                "EMPTY",
                "OCCUPIED"
        ).contains(status)) {

            throw new IllegalArgumentException(
                    "Invalid room status"
            );
        }

        synchronized (ROOM_LOCK) {

            // Verify that classroom exists.

            String existingRoom = query(
                    c,
                    "SELECT code FROM classrooms " +
                    "WHERE code = ?",
                    room
            );

            if (existingRoom.equals("[]")) {
                throw new IllegalArgumentException(
                        "Unknown room: " + room
                );
            }

            // Do not mark a scheduled or booked
            // classroom as empty.

            if ("EMPTY".equals(status)
                    && isRoomOccupied(c, room)) {

                throw new IllegalArgumentException(
                        "Room is occupied by a lecture " +
                        "or approved booking"
                );
            }

            // Store manual status override.

            String updateSql =
                    "INSERT INTO room_overrides " +
                    "(room_code, status, updated_by) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "status = VALUES(status), " +
                    "updated_by = VALUES(updated_by), " +
                    "updated_at = CURRENT_TIMESTAMP";

            update(
                    c,
                    updateSql,
                    room,
                    status,
                    id
            );
        }

        return "{\"ok\":true}";
    }

    // =====================================
    // DISPLAY ALL CLASSROOMS
    // =====================================

    public static String rooms(
            Connection c,
            String id,
            String role,
            String division,
            Map<String, String> data
    ) throws Exception {

        ZonedDateTime now = ZonedDateTime.now(ZONE);

        LocalDate date = now.toLocalDate();
        LocalTime time = now.toLocalTime();

        String day = date.getDayOfWeek()
                .getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                );

        // Automatically mark room occupied if:
        // 1. A scheduled lecture is happening.
        // 2. An approved booking is happening.
        // Otherwise use manual override or EMPTY.

        String sql =
                "SELECT " +
                "r.code, " +
                "r.capacity, " +

                "CASE " +

                // Current lecture occupancy.
                "WHEN EXISTS ( " +
                "SELECT 1 " +
                "FROM class_sessions cs " +
                "JOIN class_session_rooms csr " +
                "ON csr.session_id = cs.id " +
                "WHERE csr.room_code = r.code " +
                "AND cs.status = 'ACTIVE' " +
                "AND cs.day_of_week = ? " +
                "AND cs.start_time <= ? " +
                "AND cs.end_time > ? " +
                ") " +

                // Approved booking occupancy.
                "OR EXISTS ( " +
                "SELECT 1 " +
                "FROM bookings b " +
                "WHERE b.room_code = r.code " +
                "AND b.status = 'APPROVED' " +
                "AND b.booking_date = ? " +
                "AND b.start_time <= ? " +
                "AND b.end_time > ? " +
                ") " +

                "THEN 'OCCUPIED' " +

                // Manual status override.
                "ELSE COALESCE( " +
                "(SELECT o.status " +
                "FROM room_overrides o " +
                "WHERE o.room_code = r.code), " +
                "'EMPTY' " +
                ") " +

                "END AS status " +

                "FROM classrooms r " +
                "ORDER BY r.code";

        return query(
                c,
                sql,
                day,
                time,
                time,
                date,
                time,
                time
        );
    }
}
