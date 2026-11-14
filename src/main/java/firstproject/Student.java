package firstproject;

public class Student extends User 
{
    private final String division;
    private final int semester;

    public Student(String id,String name,String division,int semester) 
    {
        super(id,name,"STUDENT");
        this.division = division;
        this.semester = semester;
    }
    public String getDivision() 
    {
        return division;
    }
    public int getSemester() 
    {
        return semester;
    }
    @Override
    public String dashboardTitle() 
    {
        return "Student timetable";
    }
}
