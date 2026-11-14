package firstproject;

public abstract class User {
    private final String id, name, role;

    protected User(String id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public abstract String dashboardTitle();

    @Override
    public final boolean equals(Object o) {
        User u = (User) o;
        return id.equals(u.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
