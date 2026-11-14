
package firstproject;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import static firstproject.UserManager.field;
import static firstproject.jdbcConnection.query;
import static firstproject.jdbcConnection.update;

public final class BookingRoomRequest {

    private final long id;
    private final String facultyId;
    private final String roomCode;
    private final LocalDate date;
    private final LocalTime start;
    private final LocalTime end;
    private final String status;

    public BookingRoomRequest(
            long id,
            String facultyId,
            String roomCode,
            LocalDate date,
            LocalTime start,
            LocalTime end,
            String status
    ) {
        this.id = id;
        this.facultyId = facultyId;
        this.roomCode = roomCode;
        this.date = date;
        this.start = start;
        this.end = end;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public String getFacultyId() {
        return facultyId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getStatus() {
        return status;
    }

    public static String bookings_get(
            Connection c,
            String id,
            String role,
            String division,
            Map<String, String> data
    ) throws Exception {

        if ("ADMIN".equals(role)) {
            return query(
                c,
                "SELECT * FROM bookings ORDER BY id DESC"
            );
        }

        if ("FACULTY".equals(role)) {
            return query(
                c,
                "SELECT * FROM bookings " +
                "WHERE faculty_id=? ORDER BY id DESC",
                id
            );
        }

        throw new SecurityException("Not allowed");
    }

    public static String bookings_post(
            Connection c,
            String id,
            String role,
            String division,
            Map<String, String> data
    ) throws Exception {

        if (!"FACULTY".equals(role)) {
            throw new SecurityException("Faculty only");
        }

        String room = field(data, "room");
        String reason = field(data, "reason");

        LocalDate bookingDate = LocalDate.parse(
            field(data, "date")
        );

        LocalTime startTime = LocalTime.parse(
            field(data, "start")
        );

        LocalTime endTime = LocalTime.parse(
            field(data, "end")
        );

        if (bookingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                "Cannot book a room for a past date"
            );
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                "Start time must be before end time"
            );
        }

        String existingRoom = query(
            c,
            "SELECT room_code FROM classrooms " +
            "WHERE room_code=?",
            room
        );

        if (existingRoom.equals("[]")) {
            throw new IllegalArgumentException(
                "Room does not exist"
            );
        }

        update(
            c,
            "INSERT INTO bookings " +
            "(faculty_id,room_code,booking_date," +
            "start_time,end_time,reason,status) " +
            "VALUES (?,?,?,?,?,?,'PENDING')",
            id,
            room,
            bookingDate,
            startTime,
            endTime,
            reason
        );

        return "{\"ok\":true}";
    }
}
