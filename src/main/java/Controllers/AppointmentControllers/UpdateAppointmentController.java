package Controllers.AppointmentControllers;

import Objects.AppointmentObjects.Appointment;
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
import java.util.*;

import static dao.AppointmentQuery.checkAppointmentStartTime;
import static dao.AppointmentQuery.checkForOverlaps;

/**
 * The UpdateAppointmentController allows the user to update an appointment.
 * The fields are auto-populated from the users selection on the appointment screen.
 *
 */
public class UpdateAppointmentController implements Initializable {
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
    public String title, description, location, contact, type, startTime, endTime, visibleStart, visibleEnd;
    public int appointmentID, customerID, userID, contactID;
    LocalDate AppointmentStartDate, AppointmentEndDate;
    LocalTime AppointmentStartTime, AppointmentEndTime;
    Timestamp startTimeStamp, endTimeStamp;
    ZonedDateTime AppointmentStartTimeInfo, AppointmentEndTimeInfo;
    ZonedDateTime businessStartTime, businessEndTime;

    /**
     * Handles a click event on the "Update" button for updating an existing appointment.
     * Displays all fields in Local date and time.
     * Gets any update values from the input form.
     * Validates the appointment against EST business hours and checks for appointment overlaps.
     * Returns the user to the appointmnet screen.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException  If an I/O error occurs when updating the appointment.
     * @throws SQLException If a database error occurs during appointment update.
     */
    public void onUpdateBtn_click(ActionEvent actionEvent) throws IOException, SQLException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        //Get customer inputs from the form input fields
        appointmentID = Integer.parseInt(appointmentID_input.getText());
        title = appointmentTitle_input.getText();
        description = appointmentDesc_input.getText();
        location = appointmentLoc_input.getText();
        type = appointmentType_input.getText();

        //Get customer inputs from the form dropdown fields
        contactID = AppointmentQuery.getContactID(contactDropDown_list.getSelectionModel().getSelectedItem());
        customerID = CIDDropDown_list.getSelectionModel().getSelectedItem();
        userID = UIDDropDown_list.getSelectionModel().getSelectedItem();
        startTime = startDateDropDown_list.getSelectionModel().getSelectedItem();
        endTime = endDateDropDown_list.getSelectionModel().getSelectedItem();

        //Get customer inputs from the form date-picker fields.
        AppointmentStartDate = startDate_picker.getValue();
        AppointmentEndDate = endDate_picker.getValue();

        //Format the start time and end time submitted by the user.
        AppointmentStartTime = LocalTime.parse(startTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
        AppointmentEndTime = LocalTime.parse(endTime, DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));

        //Format appointment Start time and end time to send to the database.
        startTimeStamp = Timestamp.valueOf(LocalDateTime.of(AppointmentStartDate, AppointmentStartTime));
        endTimeStamp = Timestamp.valueOf(LocalDateTime.of(AppointmentEndDate, AppointmentEndTime));

        ZonedDateTime.of(LocalDateTime.of(AppointmentStartDate, AppointmentStartTime), ZoneId.of("US/Eastern"));

        //Convert the UTC time to the users local time to be displayed in the dropdown lists.
        visibleStart = startTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
        visibleEnd = endTimeStamp.toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));

        //New zone date time object using the system default zone.
        AppointmentStartTimeInfo = LocalDateTime.of(AppointmentStartDate, AppointmentStartTime).atZone(ZoneId.systemDefault());
        AppointmentEndTimeInfo = LocalDateTime.of(AppointmentEndDate, AppointmentEndTime).atZone(ZoneId.systemDefault());

        //New zone date time representing business hours using EST.
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
        if(checkAppointmentStartTime(appointmentID, startTimeStamp)){

            //Attempts to update the appointment and returns an integer greater than 0 if successful.
            int rowsAffected = AppointmentQuery.updateAppointment(appointmentID, title, description, location, type, startTimeStamp, endTimeStamp, customerID, userID, contactID);

            //Proceeds if the integer was greater than 0.
            if (rowsAffected > 0) {

                //Looks up the contact by contact ID
                contact = AppointmentQuery.getContact(contactID);

                //Creates a new appointment object.
                AppointmentInHouse newAppointment = new AppointmentInHouse(appointmentID, title, description, location, contact, type, startTimeStamp, endTimeStamp, customerID, userID, visibleStart, visibleEnd);

                //Finds the index of the new appointment object.
                int index = AppointmentList.getAllAppointments().indexOf(AppointmentList.lookupAppointment(appointmentID));

                //Updates the appointment in the observable list using its index.
                AppointmentList.updateAppointment(index, newAppointment);

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
        }else {

            //Checks to see if appointment overlaps with another.
            if(checkForOverlaps(startTimeStamp)) {
            }else{

                //Attempts to update the appointment in the database and returns an integer greater than 0 if successful.
                int rowsAffected = AppointmentQuery.updateAppointment(appointmentID, title, description, location, type, startTimeStamp, endTimeStamp, customerID, userID, contactID);

                //Proceeds if integer was greater 0.
                if (rowsAffected > 0) {

                    //Looks up contact by contact ID.
                    contact = AppointmentQuery.getContact(contactID);

                    //Creates a new appointment object.
                    AppointmentInHouse newAppointment = new AppointmentInHouse(appointmentID, title, description, location, contact, type, startTimeStamp, endTimeStamp, customerID, userID, visibleStart, visibleEnd);

                    //Looks up appointment by appointment ID
                    int index = AppointmentList.getAllAppointments().indexOf(AppointmentList.lookupAppointment(appointmentID));

                    //Updates the appointment by its index.
                    AppointmentList.updateAppointment(index, newAppointment);

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

    /**
     * Handles a click event on the "Cancel" button for discarding the appointment update.
     * Cancel the update activity.
     * Displays appointment screen
     * @param actionEvent The action event triggered by clicking the button.
     * @throws IOException If an I/O error occurs when canceling the appointment update.
     */
    public void onCancelBtn_click(ActionEvent actionEvent) throws IOException, SQLException {

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

    /**
     * Initialize the controller and set up the initial state of the form.
     * Populates all fields and drop downs.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Creates a start and end time observable list.
        ObservableList<String> startTimes = FXCollections.observableArrayList();
        ObservableList<String> endTimes = FXCollections.observableArrayList();

        //Creates a start and end time in UTC 00:00
        LocalTime startTimeInitial = LocalTime.of(0,0);
        LocalTime endtimeInitial = LocalTime.of(0,0);

        //Iterates through start time obserable list.
        while(startTimes.size() < 96){

            //Adds start time to observable list.
            startTimes.add(startTimeInitial.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));

            //Iterates start time by 15 minutes.
            startTimeInitial = startTimeInitial.plusSeconds(900);
        }

        //Iterates through the end time observable list.
        while(endTimes.size() < 96){

            //Adds end time to the observable list.
            endTimes.add(endtimeInitial.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));

            //Iterates the end time by 15 minutes.
            endtimeInitial = endtimeInitial.plusSeconds(900);
        }

        //Sets the start date and end date drop down lists.
        startDateDropDown_list.setItems(startTimes);
        endDateDropDown_list.setItems(endTimes);

        try {

            //Sets the contactID, userID, and contactID drop down lists.
            contactDropDown_list.setItems(AppointmentQuery.getContacts());
            CIDDropDown_list.setItems(CustomersQuery.getCustomerIDs());
            UIDDropDown_list.setItems(UsersQuery.getUserIDs());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set up the form with information from an existing appointment.
     * Retrieves selected appointment from the appointment screen.
     * Auto-populates form fields using information received.
     * @param appointment The appointment to load into the form.
     * @throws SQLException If a database error occurs when loading the appointment information.
     */
    public void transferAppointmentInfo(Appointment appointment) throws SQLException {

        //Sets the form input using information passed from the appointment screen.
        appointmentID_input.setText(String.valueOf(appointment.getA_id()));
        appointmentTitle_input.setText(appointment.getTitle());
        appointmentDesc_input.setText(appointment.getDescription());
        appointmentLoc_input.setText(appointment.getLocation());
        appointmentType_input.setText(appointment.getType());
        startDate_picker.setValue(appointment.getStart().toLocalDateTime().toLocalDate());
        endDate_picker.setValue(appointment.getEnd().toLocalDateTime().toLocalDate());

        //Formats the time received from the appointment screen to the users local format.
        String passedStartTime = appointment.getStart().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
        String passedEndStartTime = appointment.getEnd().toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));

        //Selects the start and end date received from the appointment screen.
        startDateDropDown_list.getSelectionModel().select(passedStartTime);
        endDateDropDown_list.getSelectionModel().select(passedEndStartTime);

        //Preselects the dropdown lists based on the selected objects from the appointment screen.
        contactDropDown_list.getSelectionModel().select(appointment.getContactName());
        CIDDropDown_list.getSelectionModel().select((Integer) appointment.getCustomerID());
        UIDDropDown_list.getSelectionModel().select((Integer) appointment.getUserID());
    }


}
