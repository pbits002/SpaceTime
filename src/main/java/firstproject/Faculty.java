package firstproject;
public final class Faculty extends User 
{
    private final String department;
    public Faculty(String id,String name,String department)
    {
        super(id,name,"FACULTY");
        this.department=department;
    }
    public String getDepartment()
    {
        return department;
    }
    @Override public String dashboardTitle()
    {
        return "Faculty lectures";
    }
}
