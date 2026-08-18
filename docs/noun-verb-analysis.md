[noun-analysis.md](https://github.com/user-attachments/files/31195327/noun-analysis.md)
Team ID \- 02  
Pooja Bhatt \- 20251501046  
Aeshvi kalathiya \- 20251501014  
Aayushi bharoliya \- 20251501045  
Aditya verma \- 20251501013  
Ashish anand \- 20251501031

**Four Filters:**  
**Filter 1**: Duplicate: Removes nouns representing the same concept.  
**Filter 2**: Irrelevant/Out Of Scope: Removes nouns that are not important domain objects for the selected use case.  
**Filter 3**: Actor/Boundary: Remove users/ external roles or the whole system from the domain class list.  
**Filter 4**: Attribute: Removes nouns that are properties of another class.

- **Use Case 1: View Time Table**  
* Raw Candidates List:  
- **Nouns**  : Admin, Student, Faculty, Class, Timetable, System, Notification, Time, Classroom, Subject, Schedule.  
- **Verb**     : Manage, Report, View, Access, Display, Check, Notify, Update.  
* Surviving Classes:  
- Class  
- Timetable  
- Classroom  
- Subject  
* Discarded Candidates:   
- Admin \- Filter 3 (Actor)  
- Student \- Filter 3 (Actor)  
- Faculty \- Filter 3 (Actor)  
- Schedule \- Filter 1 (Overlapping concept with timetable)  
- System \- Filter 3 (Boundary)  
- Notification \- Filter 2 (not central to this use case)  
- Time- Filter 4 (Attribute of class)


- **Use Case 2: Book Classroom**  
* Raw Candidates List:  
- **Nouns**   : Faculty, System, Classroom, Class representative, Students,                      Booking, Booking details, Extra class, Time, Status, Available classroom, occupied classroom.  
- **Verb**      : Log-in, Display, Update, Book, Store, Notify, Start, Check, Select, Inform, Reject, Confirm.  
* Surviving Classes:  
- Classroom  
- Booking  
- Extra Class  
* Discarded Candidates:  
- Faculty \- Filter 3 (Actor)  
- System \- Filter 3 (Boundary)  
- Class representative \- Filter 3 (Actor)  
- Students \- Filter 3 (Actor)  
- Booking details \- Filter 4 (Attribute of classroom)  
- Time \- Filter 4 (Attribute of class)  
- Status \- Filter 4 (Attribute of class)  
- Available classroom \-Filter 4 (status value of classroom)  
- Occupied classroom \- Filter 4 (status value of classroom)


  

- **Use Case 3: Class Remainder**  
* Raw Candidates List:  
- **Nouns** : Student, System, Faculty, Class, Timetable, Schedule, Class, Reminder, Subject, Time, Classroom number, classroom, Class details, Cancellation, Room .  
- **Verb**    : Log-in, Check, Identify, Reach, Generate, Send, Receive, Open, View, Cancel, Change, Update .  
* Surviving Classes:  
- Timetable  
- Class  
- Classroom  
- Reminder  
* Discarded Candidates:  
- Student \- Filter 3 (Actor)  
- System \- Filter 3 (Boundary)  
- Faculty \- Filter 3 (Actor)  
- Schedule \- Filter 1 (Overlapping concept with timetable)   
- Subject \- Filter 4 (Attribute of class)  
- Time \- Filter 4 (Attribute of class)  
- Classroom number \- Filter 4 (Attribute of classroom)  
- Class details \- Filter 4 (Attribute of class)  
- Cancellation \- Filter 2 (Irrelevant)  
- Room \- Filter 1 (Duplicate of classroom)  
  	  
    
  	  
  	
