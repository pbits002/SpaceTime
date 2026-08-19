Team Id: 2  
Names: Bhatt Pooja Jignesh	(20251501046)  
	Aeshvi Kalathiya	(20251501014)  
	Aayushi Bharoliya	(20251501045)  
	Aditya Verma		(20251501013)  
	Ashish Anand		(20251501031)  
	  
**CRC Cards**

|                                            Class |  |
| :---- | :---- |
| Knows the subject being taught | Subject |
| Knows the assigned classroom | classrooms |
| Knows the scheduled time | Timetable |
| Is part of timetable entry | Timetable |
| Trigger a reminder before it starts | Reminder |
| Can be cancelled  | Timetable |

|                                            Timetable |  |
| :---- | :---- |
| Maintains the list of scheduled class  | Class |
| Display classes for a given day | Class,Subject |
| Provides scheduled data to generate reminders | Reminder |
| Updates when a class is added, changed, or cancelled | Class |
| Associated classes with classrooms | Classroom |

|               Classroom |  |
| :---- | :---- |
| Knows its own availability status  | Booking  |
| Knows its capacity/identity | Class |
| Gets booked for regular or extra classes  | Booking |
| Is linked to a scheduled class  | Class |
| Confirms or rejects a booking request  | Booking |
| Supplies location info for reminders  | Reminder |

|                          Subject |  |
| :---- | :---- |
| Knows the subject name/code  | Class |
| Is linked to one or more scheduled classes  | Timetable |
| Supplies subject details for timetable display  | Timetable |

|                     Booking |  |
| :---- | :---- |
| Records a request to reserve a classroom  | Classroom  |
| Stores booking details (implicitly, as its own attributes)  | Classroom  |
| Checks classroom availability before confirming  | Classroom  |
| Confirms or rejects the booking  | Classroom  |
| Notifies relevant parties of booking status  | Extra class |

|                     Extra class |  |
| :---- | :---- |
| Represents a class scheduled outside the regular timetable  | Booking  |
| Requests a classroom booking to be held  | Booking,classroom |
| Stores its own time/date once confirmed  | Booking  |
| Notifies students/faculty once scheduled  | Reminder |

|                     Reminder |  |
| :---- | :---- |
| Generates a notification before a class starts  | Class  |
| Sends the reminder to relevant recipients  | Class  |
| Knows the classroom/time info to include in the message  | Classroom |
| Updates or cancels itself if a class is rescheduled/cancelled  | TimeTable,Class  |

