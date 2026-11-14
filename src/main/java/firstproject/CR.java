package firstproject;
public final class CR extends Student 
{
    public CR(String id,String name,String division,int semester)
    {
        super(id,name,division,semester);
    }
    @Override public String getRole()
    {
        return "CR";
    }
    @Override public String dashboardTitle()
    {
        return "CR room management";
    }
}
