package dao;

import Controllers.FileControllers.LoginActivityFile;
import Objects.ContactObjects.ContactInHouse;
import Objects.ContactObjects.ContactList;
import Objects.UserObjects.UsersInHouse;
import Objects.UserObjects.UsersList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * A data access object for managing user-related queries and credentials.
 * Contains users for retrieving user information.
 */
public abstract class UsersQuery {

    /**
     * Validates user credentials for login.
     *
     * @param userName     The username provided by the user.
     * @param passwordInput The password input provided by the user.
     * @return True if the credentials are valid; otherwise, false.
     * @throws SQLException If a database error occurs during the credential validation.
     */
    public static boolean getUserCredentials(String userName, String passwordInput) throws SQLException {
        String sql = "SELECT * FROM users WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();

        //Gets the users default language and country
        String lang = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();

        //Creates a new locale using the users language and country.
        Locale l = new Locale(lang, country);

        //Creates a new alert object
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        if (rs.next()) {
            String passwordDB = rs.getString("Password");

            //Displays messages in English if that is the user's language.
            if(Objects.equals(lang, "en")){

                //Loads the english language resource bundle
                ResourceBundle r = ResourceBundle.getBundle("Bundle", l);

                //Checks to see if the input username and password match the database.
                if(Objects.equals(passwordDB, passwordInput)){

                    //Displays a successful login alert
                    alert.setTitle(r.getString("en.loginSuccessfulTitle"));
                    alert.setContentText(r.getString("en.loginSuccessful"));
                    alert.showAndWait();
                    return true;
                } else {

                    //Displays an unsuccessful login alert.
                    alert.setTitle(r.getString("en.passwordInvalidTitle"));
                    alert.setContentText(r.getString("en.passwordIncorrect"));
                    alert.showAndWait();
                    LoginActivityFile.getUserActivity(userName, "Unsuccessful");
                    return false;
                }

            //Displays messages in French if that is the user's language.
            }else if((Objects.equals(lang, "fr"))){

                //Loads the French language resource bundle.
                ResourceBundle r = ResourceBundle.getBundle("Bundle_fr_FR", l);

                //Checks to see if the input username and password match the database.
                if(Objects.equals(passwordDB, passwordInput)){
                    alert.setTitle(r.getString("fr.loginSuccessful"));
                    alert.setContentText(r.getString("fr.loginSuccessful"));
                    alert.showAndWait();
                    return true;
                } else {

                    //Displays an unsuccessful login alert.
                    alert.setTitle(r.getString("fr.passwordInvalidTitle"));
                    alert.setContentText(r.getString("fr.passwordIncorrect"));
                    alert.showAndWait();
                    LoginActivityFile.getUserActivity(userName, "Unsuccessful");
                    return false;
                }
            }
        //If the username doesn't exist in the database, system will still display a valid alert.
        }else {

            //Displays messages in English.
            if(Objects.equals(lang, "en")) {

                //Loads the english resource bundle.
                ResourceBundle r = ResourceBundle.getBundle("Bundle", l);
                alert.setTitle(r.getString("en.passwordInvalidTitle"));
                alert.setContentText(r.getString("en.passwordIncorrect"));
                alert.showAndWait();
                LoginActivityFile.getUserActivity(userName, "Unsuccessful");
                return false;

            //Displays messages in French
            }else if(Objects.equals(lang, "fr")){

                //Loads the english resource bundle.
                ResourceBundle r = ResourceBundle.getBundle("Bundle_fr_FR", l);
                alert.setTitle(r.getString("fr.passwordInvalidTitle"));
                alert.setContentText(r.getString("fr.passwordIncorrect"));
                alert.showAndWait();
                LoginActivityFile.getUserActivity(userName, "Unsuccessful");
                return false;
            }
        }
        return false;
    }

    /**
     * Retrieves a list of user IDs.
     *
     * @return An observable list of user IDs.
     * @throws SQLException If a database error occurs during user ID retrieval.
     */
    public static ObservableList<Integer> getUserIDs() throws SQLException {

        //Observable list used to hold user IDs
        ObservableList<Integer> userIDs = FXCollections.observableArrayList();

        //Query to retrieve all users from the database.
        String sql = "SELECT * from users";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing all the users returned from the database.
        ResultSet rs = ps.executeQuery();

        int userID;

        //Iterates through the result set.
        while(rs.next()){

            //Gets the userID from the result set.
            userID = rs.getInt("User_ID");

            //Adds the userID to the observable list.
            userIDs.add(userID);
        }

        //Returns the observable list of user IDs
        return userIDs;
    }

    public static int getUserCount() throws SQLException {

        // Query to count the number of records in the user id column.
        String sql = "SELECT COUNT(User_ID) as userCount FROM users";

        // Load the query into a prepared statement for execution.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        // Result set containing the count
        ResultSet rs = ps.executeQuery();
        int userCount = 0;
        if (rs.next()) {
            userCount = rs.getInt("userCount");
        }
        return userCount;
    }

    public static int insertNewUser(int userID, String userName, String userPassword, String userType, String contactName, String contactEmail) throws SQLException{

        //Query to add a new user to the database.
        String sql = "INSERT INTO users (User_ID, User_Name, Password, Type) VALUES(?, ?, ?, ?)";

        //Load query into a prepared statement for execution.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Add users values to prepared statement.
        ps.setInt(1, userID);
        ps.setString(2, userName);
        ps.setString(3, userPassword);
        ps.setString(4, userType);
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected > 0){
            ContactQuery.insertContact(contactName, contactEmail, userID);
        }

        return rowsAffected;
    }

    public static String getUserType(String userNameInput) throws SQLException {

        //Query to look up user by name
        String sql = "SELECT * FROM users WHERE User_Name = ?";

        //Load query into a prepared statement for execution.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Add user name to prepared statement
        ps.setString(1,userNameInput);

        //Result set containing user information.
        ResultSet rs = ps.executeQuery();

        String userType = null;
        //Iterate through result set
        while(rs.next()){

            //Variables to hold user info
            int id = rs.getInt("User_ID");
            String name = rs.getString("User_Name");
            String password = rs.getString("Password");
            String type = rs.getString("Type");

            UsersInHouse loggedInUser = new UsersInHouse(id, name, password, type);
            UsersList.setLoggedInUser(loggedInUser);

            userType = type;

            if(Objects.equals(userType, "contact")){
                //Loads contact appointment info into the local table
                int contactID = getContactID(rs.getInt("User_ID"));
                AppointmentQuery.getAppointmentsByContact(contactID);
            }
        }

        return userType;
    }

    public static int getContactID(int userID) throws SQLException {
        //Query to look up user by name
        String sql = "SELECT * FROM contacts WHERE User_ID = ?";

        //Load query into a prepared statement for execution.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Add userID to prepared statement
        ps.setInt(1, userID);

        //Result set containing user information.
        ResultSet rs = ps.executeQuery();

        int contactID = 0;

        //Iterate through the result set
        while(rs.next()){
            contactID = rs.getInt("Contact_ID");
        }

        return  contactID;
    }

    public static void getUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            int id = rs.getInt("User_ID");
            String name = rs.getString("User_Name");
            String password = rs.getString("Password");
            String type = rs.getString("Type");

            UsersInHouse newUser = new UsersInHouse(id,name, password, type);
            UsersList.addUser(newUser);
        }
    }
}
