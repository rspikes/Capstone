package Controllers.ContactControllers;

import Controllers.AppointmentControllers.UpdateAppointmentController;
import Controllers.CustomerControllers.UpdateCustomerController;
import Objects.AppointmentObjects.Appointment;
import Objects.AppointmentObjects.AppointmentList;
import Objects.ContactObjects.Contact;
import Objects.ContactObjects.ContactAppointmentList;
import Objects.ContactObjects.ContactList;
import Objects.CustomerObjects.Customer;
import Objects.CustomerObjects.CustomersList;
import Objects.UserObjects.User;
import Objects.UserObjects.UsersList;
import dao.AppointmentQuery;
import dao.UsersQuery;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

public class ContactScreenController implements Initializable {

    public ToggleGroup monthWeekGroup;
    public TableView<Appointment> appointmentTable;
    public TableColumn<Appointment, String> appointmentTitle_col;
    public TableColumn<Appointment, String> appointmentDesc_col;
    public TableColumn<Appointment, String> appointmentLoc_col;
    public TableColumn<Appointment, String> appointmentType_col;
    public TableColumn<Appointment, String> appointmentStartDate_col;
    public TableColumn<Appointment, String> appointmentEndDate_col;
    public TableView<Customer> customerTable;
    public TableColumn<Customer, String> customerName_col;
    public TableColumn<Customer, String> customerAddress_col;
    public TableColumn<Customer, String> customerPostal_col;
    public TableColumn<Customer, String> customerPhone_col;
    public Label contactName_label;
    public TextField appointmentSearchTxt_field;

    ObservableList<User> user = UsersList.getLoggedInUser();
    int userID = user.get(0).getUser_ID();
    Contact contact = ContactList.lookupContact(userID);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Populates the table with data from the database.
        appointmentTable.setItems(ContactAppointmentList.getAllAppointments());
        //Populates the columns used in the table.
        appointmentTitle_col.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDesc_col.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLoc_col.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentType_col.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartDate_col.setCellValueFactory(new PropertyValueFactory<>("visibleStart"));
        appointmentEndDate_col.setCellValueFactory(new PropertyValueFactory<>("visibleEnd"));

        assert contact != null;
        String contactName = contact.getName();

        contactName_label.setText(contactName);

        try {
            // Set up a listener for mouse click events on the appointment table
            appointmentTable.setOnMouseClicked(event -> {
                try {
                    handleAppointmentClick();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            // Set a default selected item (e.g., the first item in the list)
            if (!appointmentTable.getItems().isEmpty()) {
                appointmentTable.getSelectionModel().select(0);
                String contactNames = appointmentTable.getSelectionModel().getSelectedItem().getContactName();
                contactName_label.setText(contactNames);
                // Trigger the mouse click event for the default selected item
                handleAppointmentClick();
            }
        }catch (SQLException ignored){
        }
    }

    public void onLogOutBtn_click(ActionEvent actionEvent) {
        Platform.exit();
    }

    private void handleAppointmentClick() throws SQLException {
        Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            int customerInfo = selectedAppointment.getCustomerID();
            // Update the customer table with customer information
            customerTable.setItems(CustomersList.getCustomerInfo(customerInfo));

            // Set the columns used in the customer table
            customerName_col.setCellValueFactory(new PropertyValueFactory<>("name"));
            customerAddress_col.setCellValueFactory(new PropertyValueFactory<>("address"));
            customerPostal_col.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
            customerPhone_col.setCellValueFactory(new PropertyValueFactory<>("phone"));
        }
    }

    public void onCreateBtn_click(ActionEvent actionEvent) throws IOException {
        //Displays the new appointment form.
        Parent newAppointment = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AppointmentModels/newAppointmentUI.fxml")));
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        Scene newAppointmentScene = new Scene(newAppointment, 600, 400);
        stage.setTitle("New Appointment Screen");
        stage.setScene(newAppointmentScene);
        stage.show();
    }

    public void onUpdateBtn_click(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/AppointmentModels/updateAppointmentUI.fxml"));
        loader.load();

        UpdateAppointmentController appointmentInfo = loader.getController();

        try{
            //Gets the appointment selected by the user and transfers it to the next screen.
            appointmentInfo.transferAppointmentInfo(appointmentTable.getSelectionModel().getSelectedItem());
        }catch(NullPointerException e){
            //Informs the user that they have not selected an appointment to be updated.
            alert.setContentText("You have not selected an Appointment");
            alert.show();
            return;
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Creates and loads the update appointment screen.
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        Parent newAppointmentScene = loader.getRoot();
        stage.setTitle("Update Appointment");
        stage.setScene(new Scene(newAppointmentScene));
        stage.show();
    }

    public void onCancelBtn_click(ActionEvent actionEvent) throws SQLException, IOException {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);

        //Gets the appointment selected by the user.
        Appointment removeAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if(removeAppointment == null){
            //Informs the user that they have not selected an appointment to be deleted.
            alert.setTitle("Invalid Action");
            alert.setContentText("You have not selected an appointment");
            alert.showAndWait();
        }else{
            //Prompts the user to confirm deletion of the appointment.
            alert1.setTitle("Delete Appointment Confirmation");
            alert1.setContentText("Are you sure you want to delete " + removeAppointment.getTitle() + " ? ");
            Optional<ButtonType> result = alert1.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                //Deletes the selected appointment after receiving confirmation from the user.
                int rowsAffected = AppointmentQuery.deleteAppointment(removeAppointment.getA_id());
                if(rowsAffected > 0){
                    //Displays a confirmation that the appointment was deleted.
                    alert2.setTitle("Appointment " + removeAppointment.getA_id() + " has been canceled");
                    alert2.setContentText("Appointment ID: " + removeAppointment.getA_id() + "\n" + "Appointment type: " + removeAppointment.getType() + " \nThis appointment has been canceled");
                    alert2.showAndWait();
                    AppointmentList.deleteAppointment(removeAppointment);

                    ObservableList<User> user = UsersList.getLoggedInUser();
                    String userName = user.get(0).getUser_Name();
                    String userType = UsersQuery.getUserType(userName);

                    if(Objects.equals(userType, "admin")) {
                        //Displays the appointment screen.
                        Parent AppointmentScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AppointmentModels/appointmentsUI.fxml")));
                        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                        Scene scene = new Scene(AppointmentScreenLoader, 1300, 600);
                        stage.setTitle("Appointments Management Screen");
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
        }
    }

    public void onUpdateCustomerBtn_click(ActionEvent actionEvent) throws IOException {

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

    public void onWeeklyReport_click(ActionEvent actionEvent) {
    }

    public void onMonthRadio_click(ActionEvent actionEvent) throws SQLException {

        //Clears the tables current layout
        appointmentTable.getItems().clear();
        customerTable.getItems().clear();
        //Displays the appointments by month.
        AppointmentQuery.getAppointmentsByMonth();
        appointmentTable.setItems(ContactAppointmentList.getAllAppointments());

    }
    public void onWeekRadio_click(ActionEvent actionEvent) throws SQLException {

        //Clears the tables current layout
        appointmentTable.getItems().clear();
        customerTable.getItems().clear();
        //Displays the appointments by week.
        AppointmentQuery.getAppointmentsByWeek();
        appointmentTable.setItems(ContactAppointmentList.getAllAppointments());
    }

    public void onAppointmentSearch(ActionEvent actionEvent) throws SQLException {
        ObservableList<Appointment> currentAppointmentList = ContactAppointmentList.getAllAppointments();
        String appointmentSearch = appointmentSearchTxt_field.getText();
        ObservableList<Appointment> appointmentSearchList = ContactAppointmentList.searchForAppointment(appointmentSearch, currentAppointmentList);

        appointmentTable.setItems(appointmentSearchList);
        if(appointmentSearchList.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid search");
            alert.setContentText("No appointments founds");
            alert.showAndWait();
            appointmentSearchTxt_field.setText("");
        }
        appointmentSearchTxt_field.setText("");
    }
}
