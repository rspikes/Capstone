package Objects.ContactObjects;

public class Contact {

    private int id;
    private int u_id;
    private String name;
    private String email;

    public Contact(int id, String name, String email, int u_id){
        this.id = id;
        this.name = name;
        this.email = email;
        this.u_id = u_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
