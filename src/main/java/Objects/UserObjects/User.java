package Objects.UserObjects;

public abstract class User {

    private int User_ID;
    private String User_Name;
    private String User_Password;
    private String User_Type;

    public User(int User_ID, String User_Name, String User_Password, String User_Type){
        this.User_ID = User_ID;
        this.User_Name = User_Name;
        this.User_Password = User_Password;
        this.User_Type = User_Type;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getUser_Password() {
        return User_Password;
    }

    public void setUser_Password(String user_Password) {
        User_Password = user_Password;
    }

    public String getUser_Type() {
        return User_Type;
    }

    public void setUser_Type(String user_Type) {
        User_Type = user_Type;
    }
}
