package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A data access object for managing country and division information in the database.
 * Contains queries for retrieving countries and divisions.
 */
public abstract class CountryQuery {

    /**
     * Retrieves a list of countries from the database.
     *
     * @return An observable list of country names.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static ObservableList<String> getCountries() throws SQLException {

        //Observable list of countries
        ObservableList<String> observableCountriesList = FXCollections.observableArrayList();

        //Queries the database for counties.
        String sql = "SELECT * FROM countries";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Result set containing list of countries.
        ResultSet rs = ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Retrieves the country from the result set.
            String country = rs.getString("Country");

            //Adds the country to the observable list.
            observableCountriesList.add(country);
        }

        //Returns the observable list.
        return observableCountriesList;
    }

    /**
     * Retrieves a list of divisions for the specified country from the database.
     *
     * @param countryID The ID of the country for which divisions are to be retrieved.
     * @return An observable list of division names for the specified country.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static ObservableList<String> getDivisions(int countryID) throws SQLException {

        //Observable list of divisions.
        ObservableList<String> divisions = FXCollections.observableArrayList();

        //Queries the database for divisions by country ID.
        String sql = "SELECT * FROM first_level_divisions WHERE Country_ID = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the country ID for the prepare statement.
        ps.setInt(1, countryID);

        //Result set containing divisions by country ID.
        ResultSet rs =  ps.executeQuery();

        //Iterates through the result set.
        while(rs.next()){

            //Gets the division from the result set.
            String division = rs.getString("Division");

            //Adds the division to the observable list.
            divisions.add(division);
        }

        //Returns an observable list of divisions.
        return divisions;
    }

    /**
     * Retrieves the ID of a division by its name.
     *
     * @param division The name of the division.
     * @return The ID of the division or 0 if none is found.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static int getDivisionID(String division) throws SQLException {

        //Queries the database for divisions by division.
        String sql = "Select * FROM first_level_divisions WHERE Division = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the division for the prepared statement.
        ps.setString(1, division);

        //Result set containing divisions by division.
        ResultSet rs = ps.executeQuery();
        int id = 0;

        //Iterates through the reuslt set.
        while(rs.next()){

            //Gets the division id from the result set.
            id = rs.getInt("Division_ID");
        }

        //Returns the division id.
        return id;
    }

    /**
     * Retrieves the name of a division by its ID.
     *
     * @param divisionID The ID of the division.
     * @return The name of the division or null if none is found.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static String getDivision(int divisionID) throws SQLException{

        //Looks up a division by its division ID.
        String sql = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setInt(1, divisionID);

        //Result set containing divisions by division id.
        ResultSet rs = ps.executeQuery();
        String division = null;

        //Iterates through the result set.
        while(rs.next()){

            //Returns the division from the result set.
            division = rs.getString("Division");
        }

        //Returns the division.
        return division;
    }

    /**
     * Retrieves the name of the country by the division ID.
     *
     * @param divisionID The ID of the division.
     * @return The name of the country (e.g., "U.S", "UK", "Canada") or null if none is found.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public static String getCountry(int divisionID) throws SQLException{

        //Looks up a county by its division ID.
        String sql = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";

        //Loads the query into a prepared statement.
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        //Sets the division ID for the prepared statement.
        ps.setInt(1, divisionID);

        //Result set containing country
        ResultSet rs = ps.executeQuery();
        int countryID = 0;
        String country = null;

        //Iterates through the result set.
        while(rs.next()){

            //Gets the country id from the result set.
            countryID = rs.getInt("Country_ID");

            //Checks the country int and binds it to a country.
            if(countryID == 1){
                country = "U.S";
            }else if(countryID == 2){
                country = "UK";
            }else if(countryID == 3){
                country = "Canada";
            }
        }

        //Returns the country
        return country;

    }
}
