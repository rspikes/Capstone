package Models;

import LambdaInterfaces.ReportInterface;
import dao.ReportQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import static dao.ReportQuery.getContacts;
import static dao.ReportQuery.getCountries;

/**
 * The ReportScreen controller class is responsible for displaying reports by Type, Contact Schedule, and Division.
 * The Appointment Report button will show appointments by type and month.
 * The Contact Report button will show contacts by name and what appointments they have coming up.
 * The Division Report button will show Countries by name and which divisions fall under those countries.
 * Lambda expressions were used to update code and display the reports.
 */
public class ReportScreen implements Initializable {

    public Label genericLabel;
    public Button appointmentReport_btn;
    public Button contactReport_btn;
    public Button divisionReport_btn;
    public TextArea reportArea;

    // Lambda expression that takes two reports and adds them together.
    ReportInterface newReport = (reportX, reportY) -> reportX + "\n\n" + reportY; // Lambda expression

    /**
     * Initializes the controller.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object, or null if none.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Uses the lambda expression to combine the two reports for display on load.
            reportArea.setText(newReport.displayReport(ReportQuery.countAppointmentsByType(), ReportQuery.countAppointmentsByMonth()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Handles the event when the "Appointment Report" button is clicked.
     * Uses a Lambda expression that combines the two report types into one. Simplifying the code used.
     * Code is further simplified using a common method for report display with lambda expressions.
     * Lambda expression joins the report with new lines and displays them in the text area.
     * @param actionEvent The event triggered by the button click.
     * @throws SQLException If a database-related error occurs.
     */
    public void onAppointment_click(ActionEvent actionEvent) throws SQLException {
        // Simplified by using a common method for report display with lambda expressions
        displayReport("Appointment Report", ReportQuery.countAppointmentsByType(), ReportQuery.countAppointmentsByMonth());
    }

    /**
     * Handles the event when the "Contact Report" button is clicked.
     * Code is simplified using a common method for report display with lambda expressions
     * Lambda expression joins the report with new lines and displays them in the text area.
     * @param actionEvent The event triggered by the button click.
     * @throws SQLException If a database-related error occurs.
     */
    public void onContact_click(ActionEvent actionEvent) throws SQLException {
        // Simplified by using a common method for report display with lambda expressions
        displayReport("Contact Report", getContacts());
    }

    /**
     * Handles the event when the "Division Report" button is clicked.
     * Simplified by using a common method for report display with lambda expressions
     * @param actionEvent The event triggered by the button click.
     * @throws SQLException If a database-related error occurs.
     */
    public void onDivision_click(ActionEvent actionEvent) throws SQLException {
        displayReport("Division Report", getCountries());
    }

    // Common method to display reports with a title and lambda expressions
    private void displayReport(String title, String... reports) {
        genericLabel.setText(title);
        // Join the reports with double newlines and set in the report area
        reportArea.setText(String.join("\n\n", reports));
    }

    /**
     * Handles the event when the "Exit" button is clicked.
     * Loads the appointment screen.
     * @param actionEvent The event triggered by the button click.
     * @throws IOException If an I/O error occurs.
     */
    public void onExitBtn_click(ActionEvent actionEvent) throws IOException {
        // Loads the appointment screen.
        Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/AppointmentModels/appointmentsUI.fxml")));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(CustomerScreenLoader, 1250, 650);
        stage.setTitle("Spikes Scheduler");
        stage.setScene(scene);
        stage.show();
    }
}
