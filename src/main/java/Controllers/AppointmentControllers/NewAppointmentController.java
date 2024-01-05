package Controllers.AppointmentControllers;

import Objects.ContactObjects.Contact;
import Objects.ContactObjects.ContactList;
import Objects.UserObjects.User;
import Objects.UserObjects.UsersList;
import dao.AppointmentQuery;
import Objects.AppointmentObjects.AppointmentList;
import Objects.AppointmentObjects.AppointmentInHouse;
import dao.CustomersQuery;
import dao.UsersQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;
import java.util.ResourceBundle;
import static dao.AppointmentQuery.checkForOverlaps;

/**
 * The NewAppointmentController allows the user to create a new appointment.
 * The Appointment ID field is auto generated using a temporary placeholder while the user is on this screen.
 * Users can enter title, description, location, type, start date and time, end date and time, contact id, customer id, and user id into the form fields.
 *
 */
public class NewAppointmentController implements Initializable {

    public TextField appointmentDesc_input;
    public TextField appointmentTitle_input;
    public TextField appointmentLoc_input;
    public TextField appointmentType_input;
    public TextField appointmentID_input;
    public ComboBox<String> contactDropDown_list;
    public ComboBox<Integer> CIDDropDown_list;
    public ComboBox<Integer> UIDDropDown_list;
    public DatePicker startDate_picker;
    public DatePicker endDate_picker;
    public ComboBox<String> startDateDropDown_list;
    public ComboBox<String> endDateDropDown_list;
    public static int autoGenAppointmentID;
    public String title, description, location, contact, type, startTime, endTime, visibleStart, visibleEnd;
    public int customerID, userID, contactID;
    LocalDate AppointmentStartDate, AppointmentEndDate;
    LocalTime AppointmentStartTime, AppointmentEndTime;
    ZonedDateTime AppointmentStartTimeInfo, AppointmentEndTimeInfo;
    ZonedDateTime businessStartTime, businessEndTime;
    Timestamp startTimeStamp, endTimeStamp;
    public Timestamp placeholder = Timestamp.valueOf(LocalDateTime.now().plusYears(1000)), placeholder2 = Timestamp.valueOf(LocalDateTime.now().plusHours(4).plusYears(1000));

    /**
     * Initialize the controller.
     * Creates a temporary appointment in the database to generate the appointment ID
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            //Creates a placeholder appointment on initialization, to generate the appointment ID.
            AppointmentQuery.insertAppointment("placeholder", "", "null", "null", placeholder, placeholder2, 1, 1, 1);

            //Gets the appointment ID that was created using the placeholder.
            autoGenAppointmentID = AppointmentQuery.getPlaceholderAppointment("placeholder");

            //Sets the dropdown list using data from the database.
            UIDDropDown_list.setItems(UsersQuery.getUserIDs());
            CIDDropDown_list.setItems(CustomersQuery.getCustomerIDs());


            ObservableList<User> user = UsersList.getLoggedInUser();
            int userID = user.get(0).getUser_ID();
            String userType = user.get(0).getUser_Type();



            if(Objects.equals(userType, "admin")) {
                contactDropDown_list.setItems(AppointmentQuery.getContacts());
            } else if (Objects.equals(userType,"contact")) {
                Contact contact = ContactList.lookupContact(userID);
                assert contact != null;
                String contactName = contact.getName();

                contactDropDown_list.setDisable(true);
                contactDropDown_list.setItems(AppointmentQuery.getContacts());
                contactDropDown_list.getSelectionModel().select(contactName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Initializes the Appointment ID field with the appointment ID that was generated from the database.
        appointmentID_input.setText(autoGenAppointmentID + " Auto-Generated ");

        //Lists used to hold UTC times from 00:00 - 23:49
        ObservableList<String> startTimes = FXCollections.observableArrayList();
        ObservableList<String> endTimes = FXCollections.observableArrayList();

        //Local time variables initialized to 00:00
        LocalTime startTimeInitial = LocalTime.of(0,0);
        LocalTime endtimeInitial = LocalTime.of(0,0);

        //Iterates through the start time observable list
        while(startTimes.size() < 96){

            //Adds the start time to the observable list.
            startTimes.add(startTimeInitial.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));

            //Iterates the time by 15 minutes.
            startTimeInitial = startTimeInitial.plusSeconds(900);
        }

        //Iterates th rough the end time observable list.
        while(endTimes.size() < 96){

            //Adds the end time to the observable list.
            endTimes.add(endtimeInitial.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));

            //Iterates the time by 15 minutes.
            endtimeInitial = endtimeInitial.plusSeconds(900);
        }
        //Populates the Start times and end times drop down lists.
        startDateDropDown_list.setItems(startTimes);
        endDateDropDown_list.setItems(endTimes);
    }

    /**
     * Handles a click event on the "Save" button.
     * Validates the user inputs to see if any were left empty.
     * Adds a new appointment to the database and appointment list
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException  If an I/O error occurs when saving the appointment.
     * @throws SQLException If a database error occurs during appointment validation.
     */
    public void onSaveBtn_click(ActionEvent actionEvent) throws IOException, SQLException {

        //Loads the appointment screen

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        //Get customer inputs from the form input fields
        title = appointmentTitle_input.getText();
        description = appointmentDesc_input.getText();
        location = appointmentLoc_input.getText();
        type = appointmentType_input.getText();

        try {
            //Get customer inputs from the form dropdown fields
            contactID = AppointmentQuery.getContactID(contactDropDown_list.getSelectionModel().getSelectedItem());
            customerID = CIDDropDown_list.getSelectionModel().getSelectedItem();
            userID = UIDDropDown_list.getSelectionModel().getSelectedItem();
            startTime = startDateDropDown_list.getSelectionModel().getSelectedItem();
            endTime = endDateDropDown_list.getSelectionModel().getSelectedItem();

        } catch (NullPointerException e) {

            //Displays an error message to the user using the alert object.
            alert.setContentText("Your must select a Contact, Customer ID, User ID, Start Time, and End Time");
            alert.show();
        }

        try {
            //Get customer inputs from the form date-picker fields.
            AppointmentStartDate = startDate_picker.getValue();
            AppointmentEndDate = endDate_picker.getValue();

            //Format the start time and end time submitted by the user.
            AppointmentStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
            AppointmentEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));

            //Format appointment Start time and end time to send to the database.
            startTimeStamp = Timestamp.valueOf(LocalDateTime.of(AppointmentStartDate, AppointmentStartTime));
            endTimeStamp = Timestamp.valueOf(LocalDateTime.of(AppointmentEndDate, AppointmentEndTime));

            //Convert the UTC time to the users local time to be displayed in the dropdown lists.
            visibleStart = startTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
            visibleEnd = endTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

            //Gets the appointment start and end time using the zone defaults.
            AppointmentStartTimeInfo = LocalDateTime.of(AppointmentStartDate, AppointmentStartTime).atZone(ZoneId.systemDefault());
            AppointmentEndTimeInfo = LocalDateTime.of(AppointmentEndDate, AppointmentEndTime).atZone(ZoneId.systemDefault());

            //New zone date time object containing business hours for EST.
            businessStartTime = LocalDateTime.of(AppointmentStartDate, LocalTime.of(8, 0)).atZone(ZoneId.of("US/Eastern"));
            businessEndTime = LocalDateTime.of(AppointmentEndDate, LocalTime.of(22, 0)).atZone(ZoneId.of("US/Eastern"));

            //Checks to see if appointment start time is before  8:00 AM EST
            if (AppointmentStartTimeInfo.isBefore(businessStartTime)) {
                alert.setContentText("Your appointment start time can't be set before 8:00 AM EST");
                alert.show();
                return;
            }
            //Checks to see if appointment start time is after 10:00 PM EST
            if (AppointmentEndTimeInfo.isAfter(businessEndTime)) {
                alert.setContentText("Your appointment end time can't be set after 10:00 PM EST");
                alert.show();

                return;
            }
            //Checks to see if appointment end time is before appointment start time.
            if (AppointmentEndTimeInfo.isBefore(AppointmentStartTimeInfo)) {
                alert.setContentText("Appointment end time can't be before the appointment start time");
                alert.show();
                return;
            }
            //Checks to see if new appointment overlaps any appointments on the same day.
            if (checkForOverlaps(startTimeStamp)) {
            }else{

                //Updates the placeholder appointment, creating a new appointment.
                int rowsAffected = AppointmentQuery.updateAppointment(autoGenAppointmentID, title, description, location, type, startTimeStamp, endTimeStamp, customerID, userID, contactID);

                //If the placeholder was successfully updated, this adds it to the list stored in memory.
                if (rowsAffected > 0) {

                    //Searches for a contact by their contact ID
                    contact = AppointmentQuery.getContact(contactID);

                    //Creates a new appointment object
                    AppointmentInHouse newAppointment = new AppointmentInHouse(autoGenAppointmentID, title, description, location, contact, type, startTimeStamp, endTimeStamp, customerID, userID, visibleStart, visibleEnd);

                    //Adds the appointment object to the observable list.
                    AppointmentList.addAppointment(newAppointment);

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
        } catch (NullPointerException e) {

            //Displays an error message to the user using the alert object.
            alert.setContentText("Select a start and end date");
            alert.show();
        }
    }

    /**
     * Handles a click event on the "Cancel" button.
     * Deletes the placeholder appointment from the database.
     * Cancel the creation of a new appointment.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException  If an I/O error occurs when canceling the appointment.
     * @throws SQLException If a database error occurs during cancellation.
     */
    public void onCancelBtn_click(ActionEvent actionEvent) throws IOException, SQLException {

        ObservableList<User> user = UsersList.getLoggedInUser();
        String userName = user.get(0).getUser_Name();
        String userType = UsersQuery.getUserType(userName);

        if(Objects.equals(userType, "admin")) {
            //Deletes placeholder appointment
            AppointmentQuery.deletePlaceHolderAppointments("placeholder");

            //Displays the appointment screen.
            Parent newAppointmentLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AppointmentModels/appointmentsUI.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene newAppointmentScene = new Scene(newAppointmentLoader, 1300, 600);
            stage.setTitle("Appointments Management Screen");
            stage.setScene(newAppointmentScene);
            stage.show();
        } else if (Objects.equals(userType, "contact")) {

            //Returns user to the contact landing page
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
