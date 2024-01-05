package dao;

import Objects.ContactObjects.ContactInHouse;
import Objects.ContactObjects.ContactList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactQuery {

    public static void insertContact(String contactName, String contactEmail, int userID) throws SQLException{

        //Query used to add a contact to the database.
        String sql = "INSERT INTO contacts (Contact_Name, Email, User_ID) VALUES(?, ?, ?)";

        //Loads query into a prepared statement for execution.
        PreparedStatement ps  = JDBC.connection.prepareStatement(sql);

        //Adds user info to prepared statement.
        ps.setString(1, contactName);
        ps.setString(2, contactEmail);
        ps.setInt(3, userID);

        ps.executeUpdate();
    }

    public static void getContacts() throws SQLException {

        String sql = "SELECT * FROM contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            int id = rs.getInt("Contact_ID");
            String name = rs.getString("Contact_Name");
            String email = rs.getString("Email");
            int u_id = rs.getInt("User_ID");

            ContactInHouse newContact = new ContactInHouse(id, name, email, u_id);
            ContactList.addContact(newContact);
        }

    }
}
