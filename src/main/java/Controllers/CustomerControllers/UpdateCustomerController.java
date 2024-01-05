package Controllers.CustomerControllers;

import Objects.CustomerObjects.Customer;
import Objects.UserObjects.User;
import Objects.UserObjects.UsersList;
import dao.CountryQuery;
import dao.CustomersQuery;
import Objects.CustomerObjects.CustomersInHouse;
import Objects.CustomerObjects.CustomersList;
import dao.UsersQuery;
import javafx.collections.ObservableList;
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
 * THe UpdateCustomerController allows the user to update customer information.
 * The fields and dropdown lists are auto-populated using information from the customer screen.
 */
public class UpdateCustomerController implements Initializable {

    public TextField customerName_input;
    public TextField customerAddress_input;
    public TextField customerPostal_input;
    public TextField customerPhone_input;
    public TextField customerID_input;
    public ComboBox<String> countryDropDown_list;
    public ComboBox<String> divisionDropDown_list;
    public static String name, address, postal, phone;

    public static int id, division;

    /**
     * Initialize the form fields with information from the selected customer.
     * Sets the form fields using information received from the customer screen.
     * Populates all drop down lists.
     * @param customer The customer to load into the form.
     * @throws SQLException If a database error occurs when loading the customer information.
     */
    public void transferCustomerInfo(Customer customer) throws SQLException {

        //Sets the form fields using information received from the customer screen.
        customerID_input.setText(String.valueOf(customer.getId()));
        customerName_input.setText(customer.getName());
        customerAddress_input.setText(customer.getAddress());
        customerPostal_input.setText(customer.getPostalCode());
        customerPhone_input.setText(customer.getPhone());
        String country = CountryQuery.getCountry(customer.getDivisionID());
        String division = CountryQuery.getDivision(customer.getDivisionID());


        if(Objects.equals(country, "U.S")){

            //Preselects the division drop down list using info received from the customer screen.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(1));
        }else if(Objects.equals(country, "UK")){

            //Preselects the division drop down list using info received from the customer screen.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(2));
        }else if(Objects.equals(country, "Canada")){

            //Preselects the country drop down list using info received from the customer screen.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(3));
        }

        //Preselects the country and division drop downs.
        countryDropDown_list.getSelectionModel().select(country);
        divisionDropDown_list.getSelectionModel().select(division);
    }

    /**
     * Handles a click event on the "Save" button for updating customer information.
     * Validates the updated form fields and drop down lists.
     * Returns the user to the customer screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException  If an I/O error occurs when updating customer information.
     * @throws SQLException If a database error occurs during the update.
     */
    public void updateCustomerSaveBtn_click(ActionEvent actionEvent) throws IOException, SQLException {

        //Variables used to hold what the user input into the form.
        id = Integer.parseInt(customerID_input.getText());
        name = customerName_input.getText();
        address = customerAddress_input.getText();
        postal = customerPostal_input.getText();
        phone = customerPhone_input.getText();
        division = CountryQuery.getDivisionID(divisionDropDown_list.getSelectionModel().getSelectedItem());

        //Attempts to update the selected customer and returns an integer greater than 0 if successful.
        int rowsAffected = CustomersQuery.updateCustomer(id, name, address, postal, phone, division);

        //Proceeds if integer was greater than 0;
        if(rowsAffected > 0){

            //Creates a new customer object using updated information.
            CustomersInHouse updateCustomer = new CustomersInHouse(id, name, address, postal, phone, division);

            //Looks for the old customer object by customer ID
            int index = CustomersList.getAllCustomers().indexOf(CustomersList.lookupCustomer(id));

            //Updates the customer object using its index.
            CustomersList.updateCustomer(index, updateCustomer);

            ObservableList<User> user = UsersList.getLoggedInUser();
            String userName = user.get(0).getUser_Name();
            String userType = UsersQuery.getUserType(userName);

            if(Objects.equals(userType, "admin")){

                //Loads the customer screen.
                Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/customerUI.fxml")));
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                //Displays the customer screen.
                Scene scene = new Scene(CustomerScreenLoader, 1200, 600);
                stage.setTitle("Spikes Scheduler");
                stage.setScene(scene);
                stage.show();
            } else if (Objects.equals(userType, "contact")) {

                //Returns user to the contact landing page.
                Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ContactModels/contactUI.fxml")));
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(CustomerScreenLoader, 1500, 850);
                stage.setTitle("Spikes Scheduler");
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            }
        }
    }

    /**
     * Handle a click event on the "Cancel" button for canceling the update.
     * Cancels the update activity.
     * Returns the user to the customer screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when canceling the update.
     */
    public void onCancelBtnClick(ActionEvent actionEvent) throws IOException, SQLException {

        ObservableList<User> user = UsersList.getLoggedInUser();
        String userName = user.get(0).getUser_Name();
        String userType = UsersQuery.getUserType(userName);

        if(Objects.equals(userType, "admin")) {
            //Displays the customer screen.
            Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/customerUI.fxml")));
            Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(CustomerScreenLoader, 1250, 650);
            stage.setTitle("Spikes Scheduler");
            stage.setScene(scene);
            stage.show();
        } else if (Objects.equals(userType, "contact")) {

            //Returns user to the contact landing page.
            Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ContactModels/contactUI.fxml")));
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(CustomerScreenLoader, 1500, 850);
            stage.setTitle("Spikes Scheduler");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        }
    }

    /**
     * Handles the selection of a country in the dropdown list and update the division dropdown accordingly.
     * Programmatically updates the division drop down based on which country is selected in the country drop down.
     * @param actionEvent The action event triggered by selecting a country.
     * @throws SQLException If a database error occurs when updating the division dropdown.
     */
    public void onItemCountry_selected(ActionEvent actionEvent) throws SQLException {

        //Gets the selected country from the dropdown list.
        String selectedCountry = countryDropDown_list.getSelectionModel().getSelectedItem();

        if(Objects.equals(selectedCountry, "U.S")){

            //Updates the division drop down list in real time based on the selected country.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(1));
        }else if(Objects.equals(selectedCountry, "UK")){

            //Updates the division drop down list in real time based on the selected country.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(2));
        }else if(Objects.equals(selectedCountry, "Canada")){

            //Updates the division drop down list in real time based on the selected country.
            divisionDropDown_list.setItems(CountryQuery.getDivisions(3));
        }
    }

    /**
     * Initialize the controller and set up the initial state of the form.
     * Populates the country drop down list.
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        try {

            //Queries the database for countries and sets those to the drop down list.
            countryDropDown_list.setItems(CountryQuery.getCountries());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
