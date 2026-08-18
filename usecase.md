[use-cases (3).md](https://github.com/user-attachments/files/31195455/use-cases.3.md)
# Team ID- 02

Bhatt Pooja Jignesh       (20251501046)  
Aayushi Bharoliya	       (20251501045)  
Aeshvi Kalathiya	       (20251501014)  
Aditya Verma		       (20251501013)  
Ashish Verma	       (20251501031)

# **Use case-1: View TimeTable** 

* **Primary actor**: Student.  
* **Stakeholders**: Admin, faculty, Students, CR.  
* **Preconditions**:   
1) Students are authenticated and enrolled in the current academic term.  
2) The student's course/section has been mapped to a timetable in the system  
*  **Postconditions:**   
1) Students are shown their current day’s schedule: subject, faculty, room and time slot.  
2) If any last-minute changes exist, it’s reflected and visually flagged.  
* **Trigger**: 

      	     The student opens the dashboard and selects “My Timetable”.

* **Main flow:**  
1) Student logs into the app.  
2) System identifies the student’s enrolled section.  
3) System display the schedule.  
* **Alternate flows:**  
1) No classes scheduled today.  
- System finds no time table entries for the student’s section on the selected day  
- System display no classes schedule today instead of empty grid.  
2) A class has been cancelled or moved.  
- System detect that one of the today’s entries has an active cancellation/ room-changed flag.  
- System displays that entry with a visual indicator(e.g. “Cancelled” or “Room changed:204-\>310”) instead of the original static entry.


	  
**Use case: 2 Book a room**

* **Primary actor:** faculties**.**  
* **Stakeholder**: Students, faculties,CR.  
* **Precondition:**  
1) Faculty must be logged into the system.  
2) Available classrooms and their schedules must be updated in the system.  
* **Postcondition:**  
1) The classroom is successfully booked for the extra class.  
2) The booking details are stored and students are notified.  
* **Trigger:**   
       Faculty needs to conduct an extra class and starts the “Book Extra class” option.  
* **Mainflow:**  
1) Faculty log-in the system.  
2) System displays all classrooms with green (available) and red(occupied) status.  
3) Faculty/Cr checks the available classrooms and selects a suitable green classroom.  
* **Alternate flow:**  
1) No classroom Available:  
- If no classroom is available for he selected time,the system informs the faculty.  
2) booking conflict:  
- If another faculty members books the selected classrooms before confirmation, the system rejects the booking.

**Use case-3 :Class Remainder**

* **Primary actor:** students**,** faculty  
* **Stakeholders**: students, faculty,administrations  
* **Preconditions:**   
1) Student is logged into the system.  
2) The student’s class timetable is available in the system.  
* **Postconditions**:  
1) Student receives a remainder about the upcoming class.

2) The reminder contains relevant details such as subject,time and classroom.  
* **Trigger**:

                The schedules remainder time for an upcoming class is reached.

* **Main flow:**  
1)  system checks the student’s upcoming class schedule.  
2)  system identifies the next scheduled class.  
3) At the predefined reminder time, the system generates a notification.  
4) student receives the class reminder.  
5) student opens the notification and views the class details.  
* **Alternate flows:**  
1) Class cancelled:  
-  if the class has been cancelled before the reminder is sent,the  system sends a class cancelled notification instead.  
2) Classroom changed:   
- if the classroom is changed, the system sends the reminder with updated classroom number.

        
