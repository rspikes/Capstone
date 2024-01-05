package Controllers.CustomerControllers;

import Objects.AppointmentObjects.Appointment;
import Objects.AppointmentObjects.AppointmentList;
import Objects.CustomerObjects.Customer;
import Objects.CustomerObjects.CustomersList;
import dao.CustomersQuery;
import dao.JDBC;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * The CustomerScreenController allows the user to view a list of customers from the database.
 * The user can add, update or delete customers using this controller.
 */
public class CustomerScreenController implements Initializable {

    public TableView<Customer> customerTable;
    public TableColumn<Customer, Integer> customerID_col;
    public TableColumn<Customer, String> customerName_col;
    public TableColumn<Customer, String> customerAddress_col;
    public TableColumn<Customer, String> customerPostal_col;
    public TableColumn<Customer, String> customerPhone_col;
    public TableColumn<Customer, String> customerDiv_col;

    public Button ExitButton;
    public Button newCustomer_btn;
    public Button updateCustomer_btn;
    public Button delCustomer_btn;
    public Button vAppointments_btn;
    public TextField customerSearch_Input;


    /**
     * Initialize the controller.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //sets the table items using the observable customer list.
        customerTable.setItems(CustomersList.getAllCustomers());

        //Sets the columns used in the table.
        customerID_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerName_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddress_col.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostal_col.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhone_col.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDiv_col.setCellValueFactory(new PropertyValueFactory<>("divisionID"));
    }

    /**
     * Handles a click event on the "Exit" button.
     * Closes the connection the database and exits the application.
     * @param actionEvent The action event triggered by clicking the button.
     */
    public void onExitButtonClick(ActionEvent actionEvent){

        //Closes connection to the database on close
        JDBC.closeConnection();

        //Closes the program.
        Platform.exit();}

    /**
     * Handles a click event on the "New Customer" button.
     * Displays the new customer screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when loading the new customer form.
     */
    public void onNewCustomerBtn_click(ActionEvent actionEvent) throws IOException {

        //Loads the add customer screen.
        Parent newCustomerLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/newCustomerUI.fxml")));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene newCustomerScene = new Scene(newCustomerLoader, 600, 250);
        stage.setTitle("New Customer");
        stage.setScene(newCustomerScene);
        stage.show();
    }

    /**
     * Handles a click event on the "Update Customer" button.
     * Loads selected customers information and passes it to the update customer screen.
     * Validates that a user was selected.
     * Displays the update customer screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when loading the update customer form.
     */
    public void onUpdateCustomer_click(ActionEvent actionEvent) throws IOException {

        //Creates the alert object
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        //Creates a new loader to pass information to the update customer screen.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/Models/updateCustomerUI.fxml"));
        loader.load();

        //Loads the update customer screen.
        UpdateCustomerController customerInfo = loader.getController();
        try {

            //transfers data from the customer screen to the update customer screen.
            customerInfo.transferCustomerInfo(customerTable.getSelectionModel().getSelectedItem());
        }catch (NullPointerException e){

            //Updates the content fo the alert object and displays it.
            alert.setContentText("You have not selected a customer");
            alert.showAndWait();

            return;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Loads the update customer screen and shows it.
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        Parent newCustomerScene = loader.getRoot();
        stage.setTitle("Update Customer");
        stage.setScene(new Scene(newCustomerScene));
        stage.show();
    }

    /**
     * Handles a click event on the "Delete Customer" button.
     * Validates that a customer has been selected.
     * Confirms the delete customer action and proceeds with deletion if confirmation is received.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws SQLException If a database error occurs during the customer deletion.
     */
    public void onDelCustomer_click(ActionEvent actionEvent) throws SQLException {

        //Create the alert object.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);

        //Gets the selected customer from the table.
        Customer removeCustomer = customerTable.getSelectionModel().getSelectedItem();

        //Checks to see if the user selected a customer.
        if(removeCustomer == null){

            //Displays a message to the user using the alert object.
            alert.setTitle("Invalid Action");
            alert.setContentText("You have not selected a customer");
            alert.showAndWait();
        }else {

            //Prompts the user for confirmation .
            alert.setTitle("Delete Customer Confirmation");
            alert.setContentText("Are you sure you want to delete " + removeCustomer.getName() + " ? ");

            //Adds the prompt to the alert object and waits for user selection.
            Optional<ButtonType> result = alert.showAndWait();

            //Proceeds if the user selects yes.
            if (result.isPresent() && result.get() == ButtonType.OK) {

                //Checks to see if customer has appointments and deletes those first.
                CustomersQuery.checkForAppointments(removeCustomer.getId());

                //Passes the selection to the database and attempts to delete the customer, returns an int greater than 0 if the deletion was successful.
                int rowsAffected = CustomersQuery.deleteCustomer(removeCustomer.getId());

                //Checks to see if integer is greater than 0 before displaying a confirmation message.
                if (rowsAffected > 0) {

                    //Displays the confirmation to the user using the alert object.
                    alert.setTitle("Delete Customer Confirmation");
                    alert.setContentText("Customer " + removeCustomer.getName() + " and their appointments have been deleted.");
                    alert.showAndWait();

                    //Deletes the customer from the table.
                    CustomersList.deleteCustomer(removeCustomer);
                }
            }
        }
    }

    /**
     * Handles a click event on the "View Appointments" button.
     * Displays the appointment screen
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when loading the appointments screen.
     */
    public void onViewAppsBtn_click(ActionEvent actionEvent) throws IOException {

        //Displays the appointment screen.
        Parent newAppointmentLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AppointmentModels/appointmentsUI.fxml")));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene newAppointmentScene = new Scene(newAppointmentLoader, 1300, 600);
        stage.setTitle("Appointments Management Screen");
        stage.setScene(newAppointmentScene);
        stage.show();
    }

    public void onCustomerSearch(ActionEvent actionEvent) {

        String customerSearch = customerSearch_Input.getText();
        ObservableList<Customer> customerSearchList = CustomersList.searchForCustomer(customerSearch);

        customerTable.setItems(customerSearchList);
        if(customerSearchList.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid search");
            alert.setContentText("No customers found");
            alert.showAndWait();
            customerSearch_Input.setText("");
        }
        customerSearch_Input.setText("");

    }
}
