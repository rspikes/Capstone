package dao;

import Objects.CustomerObjects.CustomersInHouse;
import Objects.CustomerObjects.CustomersList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A data access object for managing customer information in the database.
 * Contains queries for retrieving, creating, updating, deleting, and validating customers.
 */
public abstract class CustomersQuery {

    /**
     * Inserts a new customer into the database.
     *
     * @param customerName   The name of the customer.
     * @param customerAddress The address of the customer.
     * @param customerPCode   The postal code of the customer.
     * @param customerPhone   The phone number of the customer.
     * @param customerDivID   The division ID of the customer.
     * @throws SQLException If a database error occurs during the insertion.
     */
    public static void insertCustomer(String customerName, String customerAddress, String customerPCode, String customerPhone, int customerDivID) throws SQLException {

        //Query used to insert a new customer into the database.
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES(?, ?, ?, ?, ?)";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Customer information being put into the prepared statement.
        ps.setString(1, customerName);
        ps.setString(2, customerAddress);
        ps.setString(3, customerPCode);
        ps.setString(4, customerPhone);
        ps.setInt(5, customerDivID);

        //Executed the prepared statement.
        ps.executeUpdate();
    }

    /**
     * Updates an existing customer in the database by customer ID.
     *
     * @param customerID     The ID of the customer to update.
     * @param customerName   The new name of the customer.
     * @param customerAddress The new address of the customer.
     * @param customerPCode   The new postal code of the customer.
     * @param customerPhone   The new phone number of the customer.
     * @param customerDivID   The new division ID of the customer.
     * @return The number of records affected by the update operation.
     * @throws SQLException If a database error occurs during the update.
     */
    public static int updateCustomer(int customerID, String customerName, String customerAddress, String customerPCode, String customerPhone, int customerDivID) throws SQLException {

        //Query used to update a customer in the database by customer ID.
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";

        //Loads the query into the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Customer information being put into the prepared statement.
        ps.setString(1, customerName);
        ps.setString(2, customerAddress);
        ps.setString(3, customerPCode);
        ps.setString(4, customerPhone);
        ps.setInt(5, customerDivID);
        ps.setInt(6, customerID);

        //Execute the prepared statement.
        return ps.executeUpdate();
    }

    /**
     * Deletes a customer from the database by customer ID.
     *
     * @param customerID The ID of the customer to delete.
     * @return The number of records affected by the delete operation.
     * @throws SQLException If a database error occurs during the deletion.
     */
    public static int deleteCustomer(int customerID) throws SQLException{

        //Query used to delete customer by customer ID.
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";

        //Loads query into the a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Customer ID being put into the prepared statement.
        ps.setInt(1, customerID);

        //Executes the prepared statement and returns the number of records affected by the prepared statement.
        int rowsAffected = ps.executeUpdate();

        //returns number of records affected.
        return rowsAffected;
    }

    /**
     * Checks the appointment table for appointments by customer ID
     * Deletes any appointments the customer has before deleting the customer due to foreign key constraints.
     *
     * @param customerID The ID of the customer
     *
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static void checkForAppointments(int customerID) throws SQLException{

        //Query used to check if the customer has any appointments.
        String sql = "SELECT * FROM appointments WHERE Customer_ID = ?";
        String sql2 = "DELETE FROM appointments WHERE Customer_ID = ?";

        //Loads query into the prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        PreparedStatement ps2 = JDBC.connection.prepareStatement(sql2);

        //Adds the customer ID to the prepared statement.
        ps.setInt(1, customerID);

        //Result set containing appointments.
        ResultSet rs = ps.executeQuery();

        //Iterates through the resultSet
        while(rs.next()){
            ps2.setInt(1, rs.getInt("Customer_ID"));
            ps2.executeUpdate();
        }
    }

    /**
     * Retrieves the ID of a customer by their name.
     *
     * @param placeholder The name of the customer.
     * @return The ID of the customer or 0 if none is found.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static int getPlaceholderCustomer(String placeholder) throws SQLException {

        //Queries the database for a customer by customer name.
        String sql = "SELECT * FROM customers WHERE Customer_Name = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Customer name loaded into the prepared statement.
        ps.setString(1, placeholder);

        //Result set containing customer information.
        ResultSet rs = ps.executeQuery();
        int customerID = 0;

        //Iterates through the result set.
        while(rs.next()){

            //Gets the customer ID from the result set.
            customerID = rs.getInt("Customer_ID");
        }

        //Returns the customer ID.
        return customerID;
    }

    /**
     * Deletes placeholder customers from the database by name.
     *
     * @param placeholder The name of the customer to delete.
     * @throws SQLException If a database error occurs during the deletion.
     */
    public static void deletePlaceHolderCustomers(String placeholder) throws SQLException{

        //Query used to delete a customer from the database by name.
        String sql = "DELETE FROM customers WHERE Customer_Name = ?";

        //Loads the query into a prepared statement
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Loads the customer name into the prepared statement.
        ps.setString(1, placeholder);

        //Executes the prepared statement.
        ps.executeUpdate();
    }

    /**
     * Retrieves a list of customers from the database and populates the CustomersList.
     *
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static void getCustomers() throws SQLException{

        //Queries the database for all customer.s
        String sql = "SELECT * FROM customers";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing customers.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Gets the customer information from the result set.
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String customerAddress = rs.getString("Address");
            String customerPostalCode = rs.getString("Postal_Code");
            String customerPhone = rs.getString("Phone");
            int customerDivisionID = rs.getInt("Division_ID");

            //Creates a new customer object.
            CustomersInHouse newCustomer = new CustomersInHouse(customerID, customerName, customerAddress, customerPostalCode, customerPhone, customerDivisionID);

            //Adds new customer object ot the customer observable list.
            CustomersList.addCustomer(newCustomer);
        }
    }

    public static void getCustomersByAppointment(int customer_ID) throws SQLException{

        //Queries the database for customer information using their customer ID
        String sql = "SELECT * FROM customers WHERE Customer_ID = ? ";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Adds customer ID to the prepared statement
        ps.setInt(1, customer_ID);

        //Result set containing customer information.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set
        while(rs.next()){

            //Variables to hold customer information
            int customerID = rs.getInt("Customer_ID");
            String customerName = rs.getString("Customer_Name");
            String customerAddress = rs.getString("Address");
            String customerPostalCode = rs.getString("Postal_Code");
            String customerPhone = rs.getString("Phone");
            int customerDivisionID = rs.getInt("Division_ID");

            //Creates a new customer object.
            CustomersInHouse newCustomer = new CustomersInHouse(customerID, customerName, customerAddress, customerPostalCode, customerPhone, customerDivisionID);

            //Adds new customer object ot the customer observable list.
            CustomersList.addCustomer(newCustomer);
        }
    }

    /**
     * Retrieves a list of customer IDs from the database.
     *
     * @return An observable list of customer IDs.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static ObservableList<Integer> getCustomerIDs() throws SQLException {

        //Observable list containing customer ids.
        ObservableList<Integer> customerIDs = FXCollections.observableArrayList();

        //Queries the database for all customers.
        String sql = "SELECT * from customers";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing customers.
        ResultSet rs = ps.executeQuery();
        int customerID = 0;

        //Iterates through the result set.
        while(rs.next()){

            //Gets the customer ID from the result set.
            customerID = rs.getInt("Customer_ID");

            //Adds the customer ID to the observable list.
            customerIDs.add(customerID);
        }

        //Returns the observable list.
        return customerIDs;
    }
}
