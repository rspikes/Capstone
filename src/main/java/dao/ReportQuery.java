package dao;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * A data access object for generating reports related to appointments and contacts.
 * Contains queries that generate reports for appointments, contacts, and divisions.
 */
public class ReportQuery {

    /**
     * Generates a report of the count of appointments by type.
     *
     * @return A report showing the count of appointments for each appointment type.
     * @throws SQLException If a database error occurs during the report generation.
     */
    public static String countAppointmentsByType() throws SQLException{

        //Builds a string that will contain appointments by type.
        StringBuilder appointmentJunk = new StringBuilder("Appointment report by type\n");

        //Queries the database for distinct appointment types.
        String sql = "SELECT Distinct Type FROM appointments";

        //Query to count the number of types by type.
        String sql2 = "SELECT COUNT(Type) FROM appointments WHERE Type = ?";

        //Loads the query into a prepared statement for usage.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        PreparedStatement ps2 = JDBC.connection.prepareStatement(sql2);

        //Result set containing the distinct appointment types.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Retrieves the appointment type from the result set.
            String type =  rs.getString("Type");

            //Sets the appointment type for the count query.
            ps2.setString(1, type);

            //Result set containing counted type.
            ResultSet rs2 = ps2.executeQuery();

            //Iterates through the result set.
            while(rs2.next()){

                //Counts the number of a type.
                String newCount = rs2.getString("COUNT(Type)");

                //Updates the string builder with this type and number of appointments by this type.
                appointmentJunk.append("Total Number of ").append(type).append(" appointments. | ").append(newCount).append("\n");
            }
        }

        //Returns the appointments by type.
        return appointmentJunk.toString();
    }

    /**
     * Generates a report of the count of appointments by month.
     *
     * @return A report showing the count of appointments for each month.
     * @throws SQLException If a database error occurs during the report generation.
     */
    public static String countAppointmentsByMonth() throws SQLException{

        //Builds a string that will contain appointments by month.
        StringBuilder appointmentJunk = new StringBuilder("Appointment report by month\n");

        //Query used to select distinct months using the Start column
        String sql = "SELECT Distinct Month(Start) FROM appointments";

        //Query used to count the number of appointments by Month.
        String sql2 = "SELECT COUNT(Start) FROM appointments WHERE Month(Start) = ?";

        //Loads the query into a prepared statement for usage.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        PreparedStatement ps2 = JDBC.connection.prepareStatement(sql2);

        //Result set containing the distinct months.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Gets the month from the result set.
            int month = rs.getInt("MONTH(Start)");

            //Sets the month for the count query.
            ps2.setInt(1, month);

            //Result set containing counted months
            ResultSet rs2 = ps2.executeQuery();

            //Iterates through the count result set.
            while(rs2.next()){

                //Gets the number of months by month.
                String newCount = rs2.getString("COUNT(Start)");

                //Adds the number of appointment by months to the string
                appointmentJunk.append("Total Number of appointments in ").append(Month.of(month)).append(" | ").append(newCount).append("\n");
            }
        }

        //Returns a report of the number of appointments by month.
        return appointmentJunk.toString();
    }

    /**
     * Generates a report of contact schedules.
     *
     * @return A report showing the schedules of all contacts and their associated appointments.
     * @throws SQLException If a database error occurs during the report generation.
     */
    public static String getContacts() throws SQLException{

        //Builds a string that will contain contacts
        StringBuilder contactSchedule = new StringBuilder("Contact Schedules \n\n");

        //Queries the database for all contacts.
        String sql = "SELECT * FROM contacts";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set that will contain the contacts from the database.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Assigns the contact ID from the result set to an integer.
            int contactID = rs.getInt("Contact_ID");

            //Adds the contacts schedule to the string.
            contactSchedule.append("Contact Name: ").append(rs.getString("Contact_Name")).append(getSchedule(contactID)).append("\n");
        }

        //Returns the schedules for each contact.
        return  contactSchedule.toString();
    }

    /**
     * Gets the schedule for a specific contact by their ID.
     *
     * @param contactID The ID of the contact.
     * @return A schedule of appointments for the specified contact.
     * @throws SQLException If a database error occurs during the schedule retrieval.
     */
    public static StringBuilder getSchedule(int contactID) throws SQLException{

        //Builds a string that wil contain appointment schedules
        StringBuilder schedule = new StringBuilder("\n");

        //Queries the database for appointments by contact ID
        String sql = "Select * from appointments where Contact_ID = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the contact ID to be used in the query.
        ps.setInt(1, contactID);

        //Result set containing the appointments by contact.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Variables used to hold appointment information contained in the result set.
            int appointmentID = rs.getInt("Appointment_ID");
            int customerID = rs.getInt("Customer_ID");
            String title = rs.getString("Title");
            String type = rs.getString("Type");
            String description = rs.getString("Description");
            Timestamp start = rs.getTimestamp("Start");
            Timestamp end = rs.getTimestamp("End");

            //Adds the appointment information to the string.
            schedule.append("Appointment ID: ").append(appointmentID).append(" | ").
                    append(title).append(" | ").
                    append(type).append(" | ").
                    append(description).append(" | ").
                    append(start.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))).append(" - ").
                    append(end.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM))).append(" | ").
                    append("Customer ID: ").append(customerID).append(" \n");
        }

        //Returns the contact schedules.
        return schedule;
    }

    /**
     * Generates a report of divisions for a given country.
     *
     * @param Country_ID The ID of the country for which divisions are to be reported.
     * @return A report showing the divisions for the specified country.
     * @throws SQLException If a database error occurs during the report generation.
     */
    public static String getDivisions(int Country_ID) throws SQLException{

        //Builds a string to contain the divisions
        StringBuilder divisions = new StringBuilder();

        //Queries the database for divisions by country ID
        String sql = "SELECT * FROM first_level_divisions where Country_ID = ?";

        //Loads the query into a prepare statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the country ID for the query.
        ps.setInt(1, Country_ID);

        //Result set containing the divisions by country ID
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Adds the division to the string
            divisions.append(rs.getString("Division")).append(" Division").append("\n");
        }

        //Returns the divisions string
        return  divisions.toString();
    }

    /**
     * Generates a report of countries and their associated divisions.
     *
     * @return A report showing the list of countries and their associated divisions.
     * @throws SQLException If a database error occurs during the report generation.
     */
    public static String getCountries() throws SQLException{

        //Builds a string containing countries.
        StringBuilder countries = new StringBuilder("Divisions by Country\n\n");

        //Queries the database for all countries.
        String sql = "SELECT * FROM countries";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing countries.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Adds the country to the string.
            countries.append("Country: ").append(rs.getString("Country")).append("\n").append(getDivisions(rs.getInt("Country_ID"))).append("\n");
        }

        //Returns the countries string.
        return  countries.toString();
    }

}
