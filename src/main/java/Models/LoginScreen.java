package Models;

import Objects.UserObjects.UsersInHouse;
import Objects.UserObjects.UsersList;
import dao.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Main application class for the Login Screen.
 */
public class LoginScreen extends Application {


    /**
     * The main entry point for the application.
     *
     * @param stage Command line arguments.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginScreen.class.getResource("loginUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 300);
        stage.setTitle("Spikes Scheduler");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    /**
     * Start method for the JavaFX application.
     *
     * @param args The primary stage for this application.
     */
    public static void main(String[] args) throws SQLException {
        JDBC.openConnection();

        launch();
    }

    public static void loadDatabaseInfo() throws SQLException {

        //Deletes any leftover placeholder appointments.
        AppointmentQuery.deletePlaceHolderAppointments("placeholder");

        //Loads the user information from the database.
        UsersQuery.getUsers();

        // Loads the customer information from the database.
        CustomersQuery.getCustomers();

        // Loads the customer appointments from the database.
        AppointmentQuery.getAppointments();

        //Loads contact information from the database.
        ContactQuery.getContacts();
    }


}