package Controllers.CustomerControllers;

import dao.CountryQuery;
import dao.CustomersQuery;
import Objects.CustomerObjects.CustomersInHouse;
import Objects.CustomerObjects.CustomersList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The NewCustomer Controller allows the user to add a new customer to the database and observable list.
 * The customer ID field is auto generated using a placeholder customer from the database.
 */
public class NewCustomerController implements Initializable {

    public TextField customerID_input;
    public TextField customerName_input;
    public TextField customerAddress_input;
    public TextField customerPostal_input;
    public TextField customerPhone_input;
    public ComboBox<String> countryDropDown_list;
    public ComboBox<String> divisionDropDown_list;

    public static String name, address, postal, phone;
    public static int division;
    public static String placeholder1, placeholder2, placeholder3, placeholder4;
    public static int placeholder5;
    public static int autoGenCustomerID;


    /**
     * Handles a click event on the "Cancel" button.
     * Deletes the customer temporary customer placeholder from the database.
     * Cancel the creation of a new customer.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException  If an I/O error occurs when canceling the customer creation.
     * @throws SQLException If a database error occurs during cancellation.
     */
    public void onCancelBtnClick(ActionEvent actionEvent) throws IOException, SQLException {

        //Deletes placeholder appointments
        CustomersQuery.deletePlaceHolderCustomers(placeholder1);

        //Loads and displays the customer screen.
        Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/customerUI.fxml")));
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(CustomerScreenLoader, 1250, 650);
        stage.setTitle("Spikes Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles a click event on the "Save" button for creating a new customer.
     * Creates a new customer by updating the placeholder customer in the database.
     * Returns the user to the customer screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException  If an I/O error occurs when saving the customer.
     * @throws SQLException If a database error occurs during customer creation.
     */
    public void newCustomerSaveBtn_click(ActionEvent actionEvent) throws IOException, SQLException {

        //Loads the customer screen
        Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/customerUI.fxml")));
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();


        //Gets the user inputs from the form.
        name = customerName_input.getText();
        address = customerAddress_input.getText();
        postal = customerPostal_input.getText();
        phone = customerPhone_input.getText();
        division = CountryQuery.getDivisionID(divisionDropDown_list.getSelectionModel().getSelectedItem());

        //Updates the user in the database and returns an integer greater than 0 if successful.
        int rowsAffected = CustomersQuery.updateCustomer(autoGenCustomerID, name, address, postal, phone, division);

        //Checks integer to see if a user was successfully updated and proceeds if so.
        if(rowsAffected > 0){

            //Creates a new customer object.
            CustomersInHouse newCustomer = new  CustomersInHouse(autoGenCustomerID, name, address, postal, phone, division);

            //Adds customer object to the customer observable list.
            CustomersList.addCustomer(newCustomer);

            //Displays the customer screen.
            Scene scene = new Scene(CustomerScreenLoader, 1200, 600);
            stage.setTitle("Spikes Scheduler");
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Initialize the controller and set up the initial state of the form.
     * Creates a placeholder customer in the database, to auto generate a new customer ID.
     * Loads the drop down list information from the database.
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            //Creates a placeholder and sends it to the database to generate a new ID.
            placeholder1 = "placeholder";
            placeholder5 = 29;
            CustomersQuery.insertCustomer(placeholder1, placeholder2, placeholder3, placeholder4, placeholder5);

            //Adds the generated ID to a string for usage.
            autoGenCustomerID = CustomersQuery.getPlaceholderCustomer(placeholder1);

            //Sets the country list drop down after successfully running the get countries query.
            countryDropDown_list.setItems(CountryQuery.getCountries());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Sets the generated customer ID in the form.
        customerID_input.setText(String.valueOf(autoGenCustomerID));
    }

    /**
     * Handles a selection event in the country dropdown list.
     * Programmatically updates the division dropdown list based on country selected.
     * @param actionEvent The action event triggered by selecting an item in the dropdown list.
     * @throws SQLException If a database error occurs when fetching division data.
     */
    public void onItemCountry_selected(ActionEvent actionEvent) throws SQLException {

        //Gets the country selected in the drop down list.
        String selectedCountry = countryDropDown_list.getSelectionModel().getSelectedItem();

        if(Objects.equals(selectedCountry, "U.S")){

            //Sets the division drop down list based on the selected country.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(1));
        }else if(Objects.equals(selectedCountry, "UK")){

            //Sets the division drop down list based on the selected country.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(2));
        }else if(Objects.equals(selectedCountry, "Canada")){

            //Sets the division drop down list based on the selected country.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(3));
        }
    }
}
