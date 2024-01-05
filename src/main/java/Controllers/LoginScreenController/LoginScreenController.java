package Controllers.LoginScreenController;

import Controllers.FileControllers.LoginActivityFile;
import Models.LoginScreen;
import Objects.ContactObjects.Contact;
import Objects.ContactObjects.ContactList;
import Objects.UserObjects.User;
import Objects.UserObjects.UsersList;
import dao.AppointmentQuery;
import dao.ContactQuery;
import dao.CustomersQuery;
import dao.UsersQuery;
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
import java.time.ZoneId;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Controller for managing the login screen and user authentication.
 */
public class LoginScreenController implements Initializable {

    public Label userLocation_lbl;
    public Label password_lbl;
    public Label location_lbl;
    public Label userName_lbl;
    public Button login_btn;
    public TextField userName_input;
    public TextField password_input;
    public Dialog<String> newDialogScreen = new Dialog<>();

    /**
     * The login screen controller allows the user to login.
     * This controller can display all information in English or French depending on the system default language.
     * Validates that a user has entered something into all input fields.
     * Validates user credentials once all fields have been completed.
     * Outputs login attempts to a log file.
     * Grants or denies access to the rest of the application.
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources for the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Creates the OK button for the dialog screen.
        newDialogScreen.getDialogPane().getButtonTypes().add(ButtonType.OK);

        // Gets the user's default Zone ID.
        ZoneId zoneID = ZoneId.systemDefault();

        // Sets the user's Zone ID to the label.
        userLocation_lbl.setText(zoneID.toString());

        // Gets the user's default language and country.
        String lang = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();

        // Creates a new locale from the user's language and country.
        Locale l = new Locale(lang, country);

        // Changes display language to English.
        if (Objects.equals(lang, "en")) {
            // Loads the English language resource bundle.
            ResourceBundle r = ResourceBundle.getBundle("Bundle", l);

            // Sets the text fields using the strings from the resource bundle.
            userName_lbl.setText(r.getString("en.userName"));
            password_lbl.setText(r.getString("en.password"));
            login_btn.setText(r.getString("en.login"));
            location_lbl.setText(r.getString("en.localeLabel"));

            // Changes display language to French.
        } else if (Objects.equals(lang, "fr")) {
            // Loads the French language resource bundle.
            ResourceBundle r = ResourceBundle.getBundle("Bundle_fr_FR", l);

            // Sets the text fields using the strings from the resource bundle.
            userName_lbl.setText(r.getString("fr.userName"));
            password_lbl.setText(r.getString("fr.password"));
            login_btn.setText(r.getString("fr.login"));
            location_lbl.setText(r.getString("fr.localeLabel"));
        }
    }

    /**
     * Handles a click event on the "Login" button.
     * Validates that input fields have been filled out.
     * Validates user credentials after input fields have been filled out.
     * Outputs login attempts to a log file.
     * Grants access to the application if validation is successful.
     * @param actionEvent The action event triggered by clicking the button.
     * @throws SQLException If a database error occurs during user validation.
     * @throws IOException  If an I/O error occurs when loading the customer screen.
     */
    public void onLoginBtn_click(ActionEvent actionEvent) throws SQLException, IOException {
        // Gets the user inputs from the form.
        String userNameInput = userName_input.getText();
        String passwordInput = password_input.getText();

        // Checks to see if the user left a form entry blank and displays an error in the appropriate language.
        if (setErrorDialogLanguage(userNameInput, passwordInput)) {
            // Validates the username and password input by the user.
            if (credentialsValidation(userNameInput, passwordInput)) {

                //Loads information from database and sets up observable lists.
                LoginScreen.loadDatabaseInfo();

                //Gets the user type of the user that is logging in.
                String userType = UsersQuery.getUserType(userNameInput);

                if(Objects.equals(userType, "admin")) {

                    // Updates the Login Activity file with a successful entry.
                    LoginActivityFile.getUserActivity(userNameInput, "Successful");
                    // Loads the customer screen.
                    Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/customerUI.fxml")));
                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(CustomerScreenLoader, 1250, 650);
                    stage.setTitle("Spikes Scheduler");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                } else if (Objects.equals(userType, "contact")) {

                    // Updates the Login Activity file with a successful entry.
                    LoginActivityFile.getUserActivity(userNameInput, "Successful");
                    // Loads the customer screen.
                    Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ContactModels/contactUI.fxml")));
                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(CustomerScreenLoader, 1500, 850);
                    stage.setTitle("Spikes Scheduler");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                } else if (Objects.equals(userType, "test")) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Unable to load.");
                    alert.setContentText("Test user has been disabled for this application. Please contact the system administrator if you need access to this account.");
                    alert.showAndWait();
                }
            }
        }
    }

    /**
     * Display error dialogs with appropriate language based on the user's locale.
     *
     * @param userNameInput The user's input for the username.
     * @param passwordInput The user's input for the password.
     * @return True if no input fields are empty, false otherwise.
     * @throws IOException If an I/O error occurs when displaying the error dialog.
     */
    public boolean setErrorDialogLanguage(String userNameInput, String passwordInput) throws IOException {
        // Gets the user's default language and country.
        String lang = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();

        // Combines the default language and country into a new locale.
        Locale l = new Locale(lang, country);

        // Displays messages in English.
        if (Objects.equals(lang, "en")) {
            // Displays an error if username and password fields are blank.
            if (userNameInput.isBlank() && passwordInput.isBlank()) {
                ResourceBundle r = ResourceBundle.getBundle("Bundle", l);
                newDialogScreen.setTitle(r.getString("en.userDialogTitle") + " and " + r.getString("en.passwordDialogTitle"));
                newDialogScreen.setContentText(r.getString("en.userNameEmpty") + " and \n" + r.getString("en.passwordEmpty"));
                newDialogScreen.showAndWait();

                // Updates the Login Activity file with an unsuccessful login entry.
                LoginActivityFile.getUserActivity(userNameInput, "Unsuccessful");
            } else if (userNameInput.isBlank()) {
                // Displays an error if username field is blank.
                ResourceBundle r = ResourceBundle.getBundle("Bundle", l);
                newDialogScreen.setTitle(r.getString("en.userDialogTitle"));
                newDialogScreen.setContentText(r.getString("en.userNameEmpty"));
                newDialogScreen.showAndWait();

                // Updates the Login Activity file with an unsuccessful login entry.
                LoginActivityFile.getUserActivity(userNameInput, "Unsuccessful");
            } else if (passwordInput.isBlank()) {
                // Displays an error if password field is blank.
                ResourceBundle r = ResourceBundle.getBundle("Bundle", l);
                newDialogScreen.setTitle(r.getString("en.passwordDialogTitle"));
                newDialogScreen.setContentText(r.getString("en.passwordEmpty"));
                newDialogScreen.showAndWait();

                // Updates the Login Activity file with an unsuccessful login entry.
                LoginActivityFile.getUserActivity(userNameInput, "Unsuccessful");
            } else {
                return true;
            }
        } else if (Objects.equals(lang, "fr")) {
            // Displays messages in French.
            if (userNameInput.isBlank()) {
                // Displays an error in French if username field is blank.
                ResourceBundle r = ResourceBundle.getBundle("Bundle_fr_FR", l);
                newDialogScreen.setTitle(r.getString("fr.userDialogTitle"));
                newDialogScreen.setContentText(r.getString("fr.userNameEmpty"));
                newDialogScreen.showAndWait();

                // Updates the Login Activity file with an unsuccessful login entry.
                LoginActivityFile.getUserActivity(userNameInput, "Unsuccessful");
            }
            if (passwordInput.isBlank()) {
                // Displays an error in French if password field is blank.
                ResourceBundle r = ResourceBundle.getBundle("Bundle_fr_FR", l);
                newDialogScreen.setTitle(r.getString("fr.passwordDialogTitle"));
                newDialogScreen.setContentText(r.getString("fr.passwordEmpty"));
                newDialogScreen.showAndWait();

                // Updates the Login Activity file with an unsuccessful login entry.
                LoginActivityFile.getUserActivity(userNameInput, "Unsuccessful");
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Validate user credentials by checking them in the database.
     *
     * @param userNameInput The user's input for the username.
     * @param passwordInput The user's input for the password.
     * @return True if user credentials are valid, false otherwise.
     * @throws SQLException If a database error occurs during validation.
     * @throws IOException  If an I/O error occurs when recording user activity.
     */
    public boolean credentialsValidation(String userNameInput, String passwordInput) throws SQLException, IOException {

        // Validates the user information in the database.
        return UsersQuery.getUserCredentials(userNameInput, passwordInput);
    }

    public void onNewUserBtn_click(ActionEvent actionEvent) throws IOException {
        Parent CustomerScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ContactModels/newUserUI.fxml")));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(CustomerScreenLoader, 350, 285);
        stage.setTitle("Spikes Scheduler");
        stage.setScene(scene);
        stage.show();
    }
}