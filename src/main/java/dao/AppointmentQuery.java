package dao;

import Controllers.ContactControllers.ContactScreenController;
import LambdaInterfaces.DateAndTimeInterface;
import Objects.AppointmentObjects.AppointmentInHouse;
import Objects.AppointmentObjects.AppointmentList;
import Objects.ContactObjects.Contact;
import Objects.ContactObjects.ContactAppointmentList;
import Objects.ContactObjects.ContactList;
import Objects.CustomerObjects.CustomersList;
import Objects.UserObjects.User;
import Objects.UserObjects.UsersList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

/**
 * A data access object for managing appointments in the database.
 * Contains queries for retrieving, creating, updating, and deleting appointments.
 * Contains queries for appointment validation.
 */
public class AppointmentQuery {

    /**
     * Retrieves all appointments from the database and populates the `AppointmentList` with the results.
     *
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static void getAppointments() throws SQLException {

        //Resets the appointment observable list.
        AppointmentList.resetAllAppointments();

        //Queries the database for all appointments.
        String sql = "SELECT * FROM appointments";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing appointments.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Variables used to hold appointment information from the result set.
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            Timestamp startTimeStamp = rs.getTimestamp("Start");
            Timestamp endTimeStamp = rs.getTimestamp("End");
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            //Looks up the contact by contact ID.
            String contact = getContact(contactID);

            //Formats the start and end date using the users localized date and time.
            String visibleStart = startTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
            String visibleEnd = endTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));


            //Creates a new appointment object to be placed into the Observable arraylist
            AppointmentInHouse newAppointment = new AppointmentInHouse(appointmentID, title, description, location, contact, type, startTimeStamp, endTimeStamp, customerID, userID, visibleStart, visibleEnd);

            //Adds the appointment from th database to a local Observable arraylist.
            AppointmentList.addAppointment(newAppointment);
        }
    }

    /**
     * Retrieves all appointments from the database ordered by current month and populates the `AppointmentList` with the results.
     *
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static void getAppointmentsByMonth() throws SQLException {

        // Query used to select all appointments by month.
        String sql = "SELECT * FROM appointments WHERE MONTH(Start) = ?";

        // Get the current month as an integer (1 for January, 2 for February, etc.).
        int currentMonth = LocalDate.now().getMonthValue();

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,  currentMonth);

        getAppointmentInfo(sql, ps);
    }


    public static void getAppointmentInfo(String sql, PreparedStatement ps) throws SQLException {

        AppointmentList.resetAllAppointments();
        ContactAppointmentList.resetAllAppointments();

        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            int id = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            Timestamp startTimeStamp = rs.getTimestamp("Start");
            Timestamp endTimeStamp = rs.getTimestamp("End");
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");
            String contact = getContact(contactID);

            String visibleStart = startTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
            String visibleEnd = endTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));

            AppointmentInHouse newAppointment = new AppointmentInHouse(id, title, description, location, contact, type, startTimeStamp, endTimeStamp, customerID, userID, visibleStart, visibleEnd);

            ObservableList<User> user = UsersList.getLoggedInUser();
            String userType = user.get(0).getUser_Type();
            Contact loggedInContact = ContactList.lookupContact(user.get(0).getUser_ID());

            if(Objects.equals(userType, "admin")) {

                AppointmentList.addAppointment(newAppointment);

            } else if (Objects.equals(userType, "contact")) {
                assert loggedInContact != null;
                if(contactID == loggedInContact.getId()) {
                    ContactAppointmentList.addAppointment(newAppointment);
                }

            }

        }
    }

    /**
     * Retrieves all appointments from the database ordered by current week and populates the `AppointmentList` with the results.
     *
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static void getAppointmentsByWeek() throws SQLException{
        //Query to select appointment by week.
        String sql = "SELECT * FROM appointments WHERE YEAR(Start) = YEAR(CURDATE()) AND WEEK(Start) = WEEK(CURDATE())";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        getAppointmentInfo(sql, ps);
    }

    public static void getAppointmentsByContact(int Contact_ID) throws SQLException{
        ContactAppointmentList.resetAllAppointments();
        //Query to look up contacts appointments
        String sql = "SELECT * FROM appointments WHERE CONTACT_ID = ?";

        //Loads query into a prepared statement for execution.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Adds contact ID to prepared statement.
        ps.setInt(1,Contact_ID);

        //Result set to hold appointments
        ResultSet rs = ps.executeQuery();

        //Iterate through result set
        while(rs.next()){

            //Variables used to hold appointment information
            int appointmentID = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String description = rs.getString("Description");
            String location = rs.getString("Location");
            String type = rs.getString("Type");
            Timestamp startTimeStamp = rs.getTimestamp("Start");
            Timestamp endTimeStamp = rs.getTimestamp("End");
            int customerID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contactID = rs.getInt("Contact_ID");

            //Formats the start and end date using the users localized date and time.
            String visibleStart = startTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
            String visibleEnd = endTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

            //Looks up contact by contact ID.
            String contact = getContact(contactID);

            //Creates a new appointment object to be placed into the Observable arraylist
            AppointmentInHouse newAppointment = new AppointmentInHouse(appointmentID, title, description, location, contact, type, startTimeStamp, endTimeStamp, customerID, userID, visibleStart, visibleEnd);

            //Adds the appointment from th database to a local Observable arraylist.
            ContactAppointmentList.addAppointment(newAppointment);
        }
    }

    /**
     * Checks for overlapping appointments based on the start time of a new appointment.
     *
     * @param startTimeStamp The start time of the new appointment.
     * @return `true` if an overlap is found, `false` if not.
     * @throws SQLException If a database error occurs during the check.
     */
    public static boolean checkForOverlaps(Timestamp startTimeStamp) throws SQLException{

        //Creates a new alert object.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        //Selects all appointments from the database
        String sql = "Select * FROM appointments";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing appointments from the database.
        ResultSet rs = ps.executeQuery();

        //Converts the local date time received to a local date.
        LocalDate localStartDate = startTimeStamp.toLocalDateTime().toLocalDate();

        //Iterates through the result set.
        while(rs.next()){

            //Variables used to hold information from the result set.
            LocalDate appointmentStartDate = rs.getDate("Start").toLocalDate();
            Timestamp appointmentStartTime = rs.getTimestamp("Start");
            Timestamp appointmentEndTime = rs.getTimestamp("End");

            //Loads appointment of the same day to be checked.
            if(localStartDate.equals(appointmentStartDate)){

                //Checks to see if new appointment starts during another appointment.
                if(startTimeStamp.after(appointmentStartTime) && startTimeStamp.before(appointmentEndTime)){

                    //Displays a message to the user using the alert object.
                    alert.setTitle("Error creating appointment");
                    alert.setContentText("Your appointment overlaps " + rs.getString("Title") + " appointment" +
                            "\n" + "Appointment Start Time: " + appointmentStartTime.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) +
                            "\n" + "Appointment End Time: " + appointmentEndTime.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                    alert.show();
                    return true;

                //Checks to see if the appointment start time is the same as another.
                } else if (startTimeStamp.equals(appointmentStartTime)) {

                    //Displays a message to the user using the alert object.
                    alert.setTitle("Error creating appointment");
                    alert.setContentText("Your appointment starts at the same time as " + rs.getString("Title") + " appointment" +
                            "\n" + "Appointment Start Time: " + appointmentStartTime.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) +
                            "\n" + "Appointment End Time: " + appointmentEndTime.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                    alert.show();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if an appointment with the specified start time exists, excluding the provided appointment ID.
     *
     * @param appointmentID   The ID of the appointment to exclude from the check.
     * @param startTimeStamp The start time of the appointment.
     * @return `true` if an appointment with the same start time exists, `false` if not.
     * @throws SQLException If a database error occurs during the check.
     */
    public static boolean checkAppointmentStartTime(int appointmentID, Timestamp startTimeStamp) throws SQLException {

        //Gets appointment by appointment ID
        String sql = "Select * FROM appointments WHERE Appointment_ID = ? ";

        //Loads query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the appointment ID for the query.
        ps.setInt(1, appointmentID);

        //Result set containing appointments by ID.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next())
        {
            //Checks to see if start time are equal.
            if(rs.getTimestamp("Start").equals(startTimeStamp)){
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the contact name by contact ID.
     *
     * @param contactID The ID of the contact.
     * @return The name of the contact.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static String getContact(int contactID) throws SQLException{

        //Get contacts by contact ID
        String sql = "SELECT * FROM contacts WHERE Contact_ID = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the contact ID for the query.
        ps.setInt(1, contactID);

        //Result set containing contacts by contact ID
        ResultSet rs = ps.executeQuery();

        String contactName = null;

        //Iterates through the result set.
        while(rs.next()){

            //Gets the contact name from the result set.
            contactName = rs.getString("Contact_Name");
        }

        //Returns the contact name.
        return contactName;
    }

    /**
     * Retrieves the contact ID by contact name.
     *
     * @param contactName The name of the contact.
     * @return The ID of the contact.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static int getContactID(String contactName) throws SQLException{
        //Gets contacts by Name
        String sql = "SELECT * FROM contacts where Contact_Name = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the contact name for the prepared statement.
        ps.setString(1, contactName);

        //Result set containing contacts by name.
        ResultSet rs = ps.executeQuery();

        int contactID = 0;

        //iterates through the result set.
        while(rs.next()){

            //Gets the contact ID from the result set.
            contactID = rs.getInt("Contact_ID");
        }

        //returns the contact ID
        return contactID;
    }

    /**
     * Retrieves a list of contact names and returns them as an observable list.
     *
     * @return An observable list of contact names.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static ObservableList<String> getContacts() throws SQLException{

        //Observable list used to store a list of contact names for a contact drop down list
        ObservableList<String> contacts = FXCollections.observableArrayList();

        //Queries the database for contacts.
        String sql = "SELECT * FROM contacts";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing contacts.
        ResultSet rs = ps.executeQuery();

        String contact;

        //Iterates through the result set.
        while(rs.next()){

            //CGets the contact name form the result set.
            contact = rs.getString("Contact_Name");

            //Adds the contact to the observable list.
            contacts.add(contact);
        }

        //Returns the observable list of contacts.
        return contacts;
    }

    /**
     * Retrieves and returns appointment alerts for the current date and time within a 15-minute window.
     *
     * This method queries the database for appointments that match the specified date and time criteria,
     * and it returns the appointment ID for any appointment that starts within the next 15 minutes from
     * the current time and on the current date.
     *
     * The use of lambda expressions for date and time checking criteria optimizes the code by making it
     * more modular and flexible. It allows for different date and time checking conditions to be defined
     * and passed into the method, enhancing code readability and maintainability. You can easily customize
     * the criteria for checking appointments without modifying the core method, making it adaptable to
     * various use cases.
     *
     * @return The appointment ID for the next appointment within a 15-minute window on the current date.
     *         If no such appointment is found, it returns 0.
     * @throws SQLException If there is an issue with the database connection or SQL query execution.
     */
    public static int getAlerts() throws SQLException {
        // Get the current date and time.
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // Calculate the start and end times for the 15-minute window.
        LocalTime endTime = currentTime.plusMinutes(15);

        // Define date and time checkers using lambda expressions.
        DateAndTimeInterface<LocalDate> dateChecker = date -> date.isEqual(currentDate);
        DateAndTimeInterface<LocalTime> timeChecker = time -> time.isAfter(currentTime) && time.isBefore(endTime);
        DateAndTimeInterface<LocalTime> sameTimeChecker = time -> time.equals(currentTime) && time.isBefore(endTime);

        // Queries the database for appointments based on date and time checkers.
        String sql = "SELECT * FROM appointments";

        // Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        // Result set containing appointments.
        ResultSet rs = ps.executeQuery();
        int appointmentID = 0;

        // Iterate through the result set.
        while (rs.next()) {

            Timestamp appointmentStartTimeStamp = rs.getTimestamp("Start");
            Timestamp appointmentEndTimeStamp = rs.getTimestamp("End");
            ZoneId databaseTimeZone = ZoneId.of("UTC"); // Replace "UTC" with the actual timezone of your database
            LocalDateTime appointmentLocalDateStartTime = appointmentStartTimeStamp.toLocalDateTime();
            LocalDateTime appointmentLocalDateStartTimeInSystemZone = appointmentLocalDateStartTime.atZone(databaseTimeZone).toLocalDateTime();

            LocalDateTime appointmentLocalDateEndTime = appointmentEndTimeStamp.toLocalDateTime();
            LocalDateTime appointmentLocalDateEndTimeInSystemZone = appointmentLocalDateEndTime.atZone(databaseTimeZone).toLocalDateTime();


            LocalTime appointmentStartTime = appointmentLocalDateStartTimeInSystemZone.toLocalTime();
            LocalTime appointmentEndTime = appointmentLocalDateEndTimeInSystemZone.toLocalTime();

            // Get the local date and time from the result set.
            LocalDate appointmentStartDate = appointmentStartTimeStamp.toLocalDateTime().toLocalDate();


            // Check appointments based on date and time checkers.
            if(dateChecker.check(appointmentStartDate)){
                if(timeChecker.check(appointmentStartTime)){
                    appointmentID = rs.getInt("Appointment_ID");
                } else if (appointmentStartTime.isBefore(currentTime) && appointmentEndTime.isAfter(currentTime)) {
                    appointmentID = rs.getInt("Appointment_ID");
                } else if (sameTimeChecker.check(appointmentStartTime)) {
                    appointmentID = rs.getInt("Appointment_ID");
                }
            }
        }

        // Returns the appointment ID.
        return appointmentID;
    }



    /**
     * Inserts a new appointment into the database.
     *
     * @param title      The title of the appointment.
     * @param description The description of the appointment.
     * @param location   The location of the appointment.
     * @param type       The type of the appointment.
     * @param start      The start time of the appointment.
     * @param end        The end time of the appointment.
     * @param customerID The ID of the customer associated with the appointment.
     * @param userID     The ID of the user creating the appointment.
     * @param contactID  The ID of the contact associated with the appointment.
     * @throws SQLException If a database error occurs during the insertion.
     */
    public static void insertAppointment(String title, String description, String location, String type, Timestamp start, Timestamp end, int customerID, int userID, int contactID) throws SQLException {

        //Insert query to add a new appointment to the database.
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the variables used in the prepared statement.
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, start);
        ps.setTimestamp(6, end);
        ps.setInt(7, customerID);
        ps.setInt(8, userID);
        ps.setInt(9, contactID);

        //Executes the prepared statement.
        ps.executeUpdate();
    }

    /**
     * Retrieves the ID of an appointment with the specified title.
     *
     * @param placeholder1 The title of the appointment.
     * @return The ID of the appointment or 0 if none are found.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static int getPlaceholderAppointment(String placeholder1) throws SQLException {

        //Queries the database for appointments by Title.
        String sql = "SELECT * FROM appointments WHERE Title = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the variable for the prepared statement.
        ps.setString(1, placeholder1);

        //Result set containing appointments by title.
        ResultSet rs = ps.executeQuery();
        int appointmentID = 0;

        //Iterates through the result set.
        while(rs.next()){

            //Get the appointment ID from the result set.
            appointmentID = rs.getInt("Appointment_ID");
        }

        //returns the appointment ID
        return appointmentID;
    }

    /**
     * Deletes appointments with the specified title.
     *
     * @param placeholder1 The title of the appointment to delete.
     * @throws SQLException If a database error occurs during the deletion.
     */
    public static void deletePlaceHolderAppointments(String placeholder1) throws SQLException {

        //Query used to delete appointments by title.
        String sql = "DELETE FROM appointments WHERE Title = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the title for the prepared statement.
        ps.setString(1, placeholder1);

        //Executes the prepared statement.
        ps.executeUpdate();
    }

    /**
     * Updates an appointment with the specified ID.
     *
     * @param appointmentID The ID of the appointment to update.
     * @param title         The title of the appointment.
     * @param description   The description of the appointment.
     * @param location      The location of the appointment.
     * @param type          The type of the appointment.
     * @param start         The start time of the appointment.
     * @param end           The end time of the appointment.
     * @param customerID    The ID of the customer associated with the appointment.
     * @param userID        The ID of the user creating the appointment.
     * @param contactID     The ID of the contact associated with the appointment.
     * @return The number of rows affected by the update.
     * @throws SQLException If a database error occurs during the update.
     */
    public static int updateAppointment(int appointmentID, String title, String description, String location, String type, Timestamp start, Timestamp end, int customerID, int userID, int contactID) throws SQLException {

        //Query used to update appointments by appointment ID.
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? where Appointment_ID = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Variables passed into the prepared statement for update.
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setTimestamp(5, start);
        ps.setTimestamp(6, end);
        ps.setInt(7, customerID);
        ps.setInt(8, userID);
        ps.setInt(9, contactID);
        ps.setInt(10, appointmentID);

        //Executes the prepared statement.
        return ps.executeUpdate();
    }

    /**
     * Deletes an appointment with the specified ID.
     *
     * @param aId The ID of the appointment to delete.
     * @return The number of rows affected by the deletion.
     * @throws SQLException If a database error occurs during the deletion.
     */
    public static int deleteAppointment(int aId) throws SQLException{

        //Query used to delete the appointment by appointment ID.
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the appointment ID for the prepared statement.
        ps.setInt(1, aId);

        //Executes the prepared statement.
        return ps.executeUpdate();
    }
}

