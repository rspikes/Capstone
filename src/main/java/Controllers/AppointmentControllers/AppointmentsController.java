package Controllers.AppointmentControllers;

import Objects.AppointmentObjects.Appointment;
import Objects.AppointmentObjects.AppointmentList;
import Objects.ContactObjects.ContactAppointmentList;
import dao.AppointmentQuery;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static dao.AppointmentQuery.getAppointments;

/**
 * The AppointmentsController is used to manage appointments.
 * This controller displays a list of appointments in the default order from the database.
 * The appointments can be sorted by month or week
 * New appointments can be added, updated, or deleted.
 * Report can be viewed by clicking the view reports button
 */
public class AppointmentsController implements Initializable {

    public TableView<Appointment> appointmentTable;
    public TableColumn<Appointment, Integer> appointmentID_col;
    public TableColumn<Appointment, String> appointmentTitle_col;
    public TableColumn<Appointment, String> appointmentDesc_col;
    public TableColumn<Appointment, String> appointmentLoc_col;
    public TableColumn<Appointment, String> appointmentContact_col;
    public TableColumn<Appointment, String> appointmentType_col;
    public TableColumn<Appointment, String> appointmentStartDate_col;
    public TableColumn<Appointment, String> appointmentEndDate_col;
    public TableColumn<Appointment, Integer> customerID_col;
    public TableColumn<Appointment, Integer> userID_col;
    public Button updateAppointment_btn;
    public Button ExitButton;
    public Button delAppointment_btn;
    public Button newAppointment;
    public ToggleGroup monthWeekGroup;
    public Label upcomingAppointment;
    public Button viewReport_btn;
    public Button refresh_btn;
    public TextField appointmentSearchTxt_field;

    /**
     * Initialize the controller.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            setTable();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        setLabels();
    }

    /**
     * Sets the appointment table using entries from the observable AppointmentList
     * Sets the columns using the data received by the appointment list
     * @throws SQLException If a database error occurs.
     */
    public void setTable() throws SQLException {

        System.out.println(ZonedDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
        getAppointments();

        //Populates the table with data from the database.
        appointmentTable.setItems(AppointmentList.getAllAppointments());
        //Populates the columns used in the table.
        appointmentID_col.setCellValueFactory(new PropertyValueFactory<>("a_id"));
        appointmentTitle_col.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDesc_col.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLoc_col.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentType_col.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentContact_col.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentStartDate_col.setCellValueFactory(new PropertyValueFactory<>("visibleStart"));
        appointmentEndDate_col.setCellValueFactory(new PropertyValueFactory<>("visibleEnd"));
        customerID_col.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userID_col.setCellValueFactory(new PropertyValueFactory<>("userID"));
    }

    /**
     * Updates the label on the screen to show if there is an appointment within 15 minutes.
     * Shows the default label otherwise, which says that there are no appointments within 15 minutes.
     */
    public void setLabels(){
        try {
            //Checks to see if there are any appointments within 15 minutes and returns the appointment ID.
            int appointmentID = AppointmentQuery.getAlerts();
            if(AppointmentQuery.getAlerts() > 0){
                //Looks up the appointment by ID
                Appointment appointmentInfo = AppointmentList.lookupAppointment(appointmentID);
                //Updates the labels on the appointment screen to show the appointment information.
                try {
                    assert appointmentInfo != null;
                    String appointmentStartTime = appointmentInfo.getStart().toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                    String appointmentEndTime = appointmentInfo.getEnd().toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                    upcomingAppointment.setText("Appointment ID: " + appointmentInfo.getA_id() + " | " + appointmentStartTime + " - " + appointmentEndTime + " starts within 15 minutes");
                    appointmentTable.getSelectionModel().select(appointmentInfo);

                    Platform.runLater(() ->{
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Appointments Alert");
                        alert.setContentText("Appointment ID: " + appointmentInfo.getA_id() + " | " + appointmentStartTime + " - " + appointmentEndTime + " starts within 15 minutes");
                        alert.showAndWait();
                    });

                }catch (NullPointerException ignored){}
            }else{
                //Updates the label on the appointment screen to inform the user that there are no appointments within 15 minutes.
                upcomingAppointment.setText("There are no appointments within 15 minutes.");

                Platform.runLater(() -> {

                    //Inline lambda expression
                    //Runs after the appointment screen is loaded

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointments Alert");
                    alert.setContentText("There are no upcoming appointments!");
                    alert.showAndWait();
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handle a click event on the "New Appointment" button.
     * Displays add appointment screen
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when loading the new appointment form.
     */
    public void onNewAppointment_click(ActionEvent actionEvent) throws IOException {
        //Displays the new appointment form.
        Parent newAppointment = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AppointmentModels/newAppointmentUI.fxml")));
        Stage stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        Scene newAppointmentScene = new Scene(newAppointment, 600, 400);
        stage.setTitle("New Appointment Screen");
        stage.setScene(newAppointmentScene);
        stage.show();
    }

    /**
     * Handle a click event on the "Update Appointment" button.
     * Validates whether a user has selected an appointment.
     * Ultimately displays the update appointment screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when loading the update appointment form.
     */
    public void onAppointmentUpdate_click(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    /**
     * Handle a click event on the "Delete Appointment" button.
     * Validates whether the user selected an appointment.
     * Displays a confirmation message to confirm which appointment is to be deleted.
     * Displays a confirmation message confirming which appoint was deleted.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws SQLException If a database error occurs during the appointment deletion.
     */
    public void onAppointmentDelete_click(ActionEvent actionEvent) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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
            alert.setTitle("Delete Appointment Confirmation");
            alert.setContentText("Are you sure you want to delete " + removeAppointment.getTitle() + " ? ");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                //Deletes the selected appointment after receiving confirmation from the user.
                int rowsAffected = AppointmentQuery.deleteAppointment(removeAppointment.getA_id());
                if(rowsAffected > 0){
                    //Displays a confirmation that the appointment was deleted.
                    alert2.setTitle("Appointment " + removeAppointment.getA_id() + " has been canceled");
                    alert2.setContentText("Appointment ID: " + removeAppointment.getA_id() + "\n" + "Appointment type: " + removeAppointment.getType() + " \nThis appointment has been canceled");
                    alert2.showAndWait();
                    AppointmentList.deleteAppointment(removeAppointment);
                }
            }
        }
    }

    /**
     * Handles a click event on the "Exit" button.
     * Exits the appointment screen and returns the user to the Customer Screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when returning to the customer screen.
     */
    public void onExitButtonClick(ActionEvent actionEvent) throws IOException {
        //Returns the user to the customer screen.
        Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/customerUI.fxml")));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(CustomerScreenLoader, 1250, 650);
        stage.setTitle("Spikes Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles a click event on the "Month" radio button.
     * Clears the current table and repopulates it from the database in Month order.
     * @param actionEvent The action event triggered by selecting the radio button.
     * @throws SQLException If a database error occurs during data retrieval.
     */
    public void onMonthRadio_click(ActionEvent actionEvent) throws SQLException {

        //Clears the tables current layout
        appointmentTable.getItems().clear();
        //Displays the appointments by month.
        AppointmentQuery.getAppointmentsByMonth();
        appointmentTable.setItems(AppointmentList.getAllAppointments());

    }

    /**
     * Handle a click event on the "Week" radio button.
     * Clears the current table and repopulates it from the database in Week order.
     * @param actionEvent The action event triggered by selecting the radio button.
     * @throws SQLException If a database error occurs during data retrieval.
     */
    public void onWeekRadio_click(ActionEvent actionEvent) throws SQLException {

        //Clears the tables current layout
        appointmentTable.getItems().clear();
        //Displays the appointments by week.
        AppointmentQuery.getAppointmentsByWeek();
        appointmentTable.setItems(AppointmentList.getAllAppointments());
    }

    /**
     * Handle a click event on the "View Report" button.
     * Displays the report Screen
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when loading the reports screen.
     */
    public void onViewReportBtn_click(ActionEvent actionEvent) throws IOException {
        //Displays the report screen
        Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/reportsUI.fxml")));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(CustomerScreenLoader, 1200, 600);
        stage.setTitle("Spikes Scheduler");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handle a click event on the "Refresh" button.
     * Refreshes the appointment screen, updating all tables, items and labels.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when refreshing the appointment list.
     */
    public void onRefresh_click(ActionEvent actionEvent) throws IOException {
        Parent newAppointmentLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AppointmentModels/appointmentsUI.fxml")));
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        Scene newAppointmentScene = new Scene(newAppointmentLoader, 1300, 600);
        stage.setTitle("Appointments Management Screen");
        stage.setScene(newAppointmentScene);
        stage.show();
    }

    public void onAppointmentSearch(ActionEvent actionEvent) {

        String appointmentSearch = appointmentSearchTxt_field.getText();
        ObservableList<Appointment> appointmentSearchList = AppointmentList.searchForAppointment(appointmentSearch);

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
