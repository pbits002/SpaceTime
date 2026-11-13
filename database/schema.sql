CREATE DATABASE IF NOT EXISTS spacetime;
USE spacetime;

-- 1. TEMPORARY WEEKLY SCHEDULE
DROP TEMPORARY TABLE IF EXISTS weekly_schedule;

CREATE TEMPORARY TABLE weekly_schedule (
    division VARCHAR(30),
    subject_code VARCHAR(30),
    faculty_id VARCHAR(30),
    room_code VARCHAR(20),
    weekday_offset INT,
    start_time TIME,
    end_time TIME
);

INSERT INTO weekly_schedule VALUES
('CSE1','EC261 LAB','f001','9002B',0,'09:00:00','11:00:00'),
('CSE1','EC261 LAB','f001','9003',0,'09:00:00','11:00:00'),
('CSE1','SC201','f002','9001',0,'11:15:00','13:15:00'),
('CSE1','EC201','f001','9001',0,'13:45:00','15:15:00'),
('CSE1','MA201','f003','9101',1,'11:15:00','12:45:00'),
('CSE1','CS203','f004','9101',1,'13:45:00','15:15:00'),
('CSE1','CS263 LAB','f004','9102A',2,'09:00:00','11:00:00'),
('CSE1','CS263 LAB','f004','9102B',2,'09:00:00','11:00:00'),
('CSE1','HS201','f005','9011',2,'11:15:00','13:15:00'),
('CSE1','CS201','f006','9011',2,'13:45:00','15:15:00'),
('CSE1','MA201 TUT','f003','9101',2,'15:30:00','16:30:00'),
('CSE1','CS261 LAB','f006','9102A',3,'08:00:00','11:00:00'),
('CSE1','CS261 LAB','f006','9102B',3,'08:00:00','11:00:00'),
('CSE1','EC201','f001','9001',3,'11:15:00','12:45:00'),
('CSE1','MA201','f003','9101',3,'13:45:00','15:15:00'),
('CSE1','CS201','f006','9101',3,'15:30:00','16:45:00'),
('CSE1','CS203','f004','9011',4,'11:15:00','12:45:00'),

('CSE2','HS201','f005','9011',0,'11:15:00','13:15:00'),
('CSE2','EC201','f007','9011',0,'13:45:00','15:15:00'),
('CSE2','MA201 TUT','f008','9001',0,'15:30:00','16:30:00'),
('CSE2','SC201','f002','9001',1,'11:15:00','13:15:00'),
('CSE2','CS203','f009','9001',1,'13:45:00','15:15:00'),
('CSE2','CS263 LAB','f009','9103',2,'09:00:00','11:00:00'),
('CSE2','MA201','f008','9101',2,'11:15:00','12:45:00'),
('CSE2','CS201','f010','9101',2,'13:45:00','15:15:00'),
('CSE2','CS261 LAB','f010','9103',3,'08:00:00','11:00:00'),
('CSE2','EC201','f007','9101',3,'11:15:00','12:45:00'),
('CSE2','CS201','f010','9001',3,'15:30:00','16:45:00'),
('CSE2','EC261 LAB','f007','9002B',4,'09:00:00','11:00:00'),
('CSE2','EC261 LAB','f007','9003',4,'09:00:00','11:00:00'),
('CSE2','CS203','f009','9101',4,'11:15:00','12:45:00'),
('CSE2','MA201','f008','9101',4,'13:45:00','15:15:00'),

('AI','CS263 LAB','f009','9102A',0,'09:00:00','11:00:00'),
('AI','MA201','f008','9101',0,'11:15:00','12:45:00'),
('AI','AI201','f011','8101',0,'15:15:00','16:45:00'),
('AI','HS201','f005','9011',1,'11:15:00','13:15:00'),
('AI','CS203','f009','9001',1,'13:45:00','15:15:00'),
('AI','SC201','f002','9001',2,'11:15:00','13:15:00'),
('AI','MA201','f008','9001',2,'13:45:00','15:15:00'),
('AI','AI201','f011','8101',2,'16:45:00','18:00:00'),
('AI','AI261 LAB','f011','9114',3,'08:00:00','11:00:00'),
('AI','AI203','f012','9001',3,'13:15:00','15:15:00'),
('AI','MA201 TUT','f008','9001',3,'16:45:00','17:45:00'),
('AI','CS203','f009','9101',4,'11:15:00','12:45:00'),
('AI','AI203 LAB','f012','9101',4,'15:15:00','17:00:00'),

('IT','HS201','f005','9011',0,'11:15:00','13:15:00'),
('IT','EC201','f013','9011',0,'13:45:00','15:15:00'),
('IT','SC201','f002','9001',1,'11:15:00','13:15:00'),
('IT','CS203','f009','9001',1,'13:45:00','15:15:00'),
('IT','CS263 LAB','f009','9103',2,'09:00:00','11:00:00'),
('IT','MA201','f014','9101',2,'11:15:00','12:45:00'),
('IT','CS201','f010','9101',2,'13:45:00','15:15:00'),
('IT','CS261 LAB','f010','9103',3,'08:00:00','11:00:00'),
('IT','EC201','f013','9101',3,'11:15:00','12:45:00'),
('IT','CS201','f010','9001',3,'15:15:00','16:45:00'),
('IT','MA201 TUT','f014','9001',3,'16:45:00','17:45:00'),
('IT','EC261 LAB','f013','9002B',4,'09:00:00','11:00:00'),
('IT','EC261 LAB','f013','9003',4,'09:00:00','11:00:00'),
('IT','CS203','f009','9101',4,'11:15:00','12:45:00'),
('IT','MA201','f014','9101',4,'13:45:00','15:15:00'),

('ECE','MA201','f008','9101',0,'11:15:00','12:45:00'),
('ECE','EC201','f007','9101',0,'15:15:00','16:45:00'),
('ECE','EC261 LAB','f007','9002A',1,'09:00:00','11:00:00'),
('ECE','EC261 LAB','f007','9002B',1,'09:00:00','11:00:00'),
('ECE','HS201','f005','9011',1,'11:15:00','13:15:00'),
('ECE','MA201 TUT','f008','9001',1,'15:15:00','16:15:00'),
('ECE','SC201','f002','9001',2,'11:15:00','13:15:00'),
('ECE','MA201','f008','9001',2,'13:45:00','15:15:00'),
('ECE','EC201','f007','9011',3,'11:15:00','12:45:00'),
('ECE','EC203','f012','9001',3,'13:15:00','15:15:00'),
('ECE','EC203 LAB','f012','9101',4,'15:15:00','18:00:00');

-- 2. ADD MISSING SUBJECTS
CREATE TABLE IF NOT EXISTS subjects (
    code VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
INSERT INTO subjects (code, name)
SELECT DISTINCT w.subject_code, w.subject_code
FROM weekly_schedule w
LEFT JOIN subjects s ON s.code = w.subject_code
WHERE s.code IS NULL;

-- 3. INSERT WEEKLY CLASS SESSIONS
CREATE TABLE IF NOT EXISTS class_sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    division VARCHAR(30) NOT NULL,
    subject_code VARCHAR(30) NOT NULL,
    faculty_id VARCHAR(30) NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(30) DEFAULT 'Scheduled'
);
INSERT INTO class_sessions
    (division, subject_code, faculty_id, day_of_week, start_time, end_time)

SELECT DISTINCT
    w.division,
    w.subject_code,
    w.faculty_id,
    ELT(
        w.weekday_offset + 1,
        'Monday','Tuesday','Wednesday',
        'Thursday','Friday','Saturday','Sunday'
    ),
    w.start_time,
    w.end_time

FROM weekly_schedule w

WHERE NOT EXISTS (
    SELECT 1
    FROM class_sessions cs
    WHERE cs.division = w.division
      AND cs.subject_code = w.subject_code
      AND cs.faculty_id = w.faculty_id
      AND cs.day_of_week = ELT(
          w.weekday_offset + 1,
          'Monday','Tuesday','Wednesday',
          'Thursday','Friday','Saturday','Sunday'
      )
      AND cs.start_time = w.start_time
      AND cs.end_time = w.end_time
);

-- 4. ASSIGN ROOMS TO SESSIONS
CREATE TABLE IF NOT EXISTS class_session_rooms (
    session_id BIGINT NOT NULL,
    room_code VARCHAR(20) NOT NULL,
    PRIMARY KEY (session_id, room_code),
    FOREIGN KEY (session_id) REFERENCES class_sessions(id)
);
INSERT IGNORE INTO class_session_rooms (session_id, room_code)

SELECT DISTINCT
    cs.id,
    w.room_code

FROM weekly_schedule w

JOIN class_sessions cs
  ON cs.division = w.division
 AND cs.subject_code = w.subject_code
 AND cs.faculty_id = w.faculty_id
 AND cs.day_of_week = ELT(
     w.weekday_offset + 1,
     'Monday','Tuesday','Wednesday',
     'Thursday','Friday','Saturday','Sunday'
 )
 AND cs.start_time = w.start_time
 AND cs.end_time = w.end_time;

DROP TEMPORARY TABLE weekly_schedule;

-- 5. WEEKLY TIMETABLE VIEW
CREATE OR REPLACE VIEW weekly_timetable AS

SELECT
    cs.id,
    cs.division,
    cs.subject_code,
    cs.faculty_id,
    cs.day_of_week,
    cs.start_time,
    cs.end_time,
    cs.status,
    GROUP_CONCAT(
        csr.room_code
        ORDER BY csr.room_code
        SEPARATOR ', '
    ) AS rooms

FROM class_sessions cs

LEFT JOIN class_session_rooms csr
    ON csr.session_id = cs.id

GROUP BY
    cs.id,
    cs.division,
    cs.subject_code,
    cs.faculty_id,
    cs.day_of_week,
    cs.start_time,
    cs.end_time,
    cs.status;

-- 6. NOTIFICATIONS TABLE
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id VARCHAR(30) NOT NULL,
    message VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notification_recipient (recipient_id)
);

-- 7. VERIFICATION
SELECT COUNT(*) AS total_lectures
FROM class_sessions;

SELECT division, COUNT(*) AS lectures_per_division
FROM class_sessions
GROUP BY division
ORDER BY division;

SELECT
    id,
    day_of_week,
    start_time,
    end_time,
    subject_code,
    faculty_id,
    rooms
FROM weekly_timetable
WHERE division = 'CSE1'
ORDER BY
    FIELD(
        day_of_week,
        'Monday','Tuesday','Wednesday',
        'Thursday','Friday','Saturday','Sunday'
    ),
    start_time;

SELECT id, name, role
FROM users
WHERE role = 'CR';

DESCRIBE notifications;
