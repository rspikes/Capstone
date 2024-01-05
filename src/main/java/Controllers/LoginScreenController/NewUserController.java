package Controllers.LoginScreenController;

import Objects.UserObjects.UsersInHouse;
import Objects.UserObjects.UsersList;
import dao.ContactQuery;
import dao.UsersQuery;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class NewUserController implements Initializable {
    public ComboBox<String> userTypeDropDown_list;
    public TextField userID_input;
    public TextField userName_input;
    public PasswordField userPassword_input;
    public TextField contactName_input;
    public TextField contactEmail_input;

    ObservableList<String> userTypes = FXCollections.observableArrayList();

    public void newUserCancel_btn(ActionEvent actionEvent) throws IOException {
        Parent AppointmentScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/loginUI.fxml")));
        Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(AppointmentScreenLoader, 300, 300);
        stage.setTitle("Spikes Scheduler");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void newUserSave_btn(ActionEvent actionEvent) throws SQLException, IOException {

        //Variables to hold user information input into form.
        int userID = Integer.parseInt(userID_input.getText());
        String userName = userName_input.getText();
        String userPassword = userPassword_input.getText();
        String userType = userTypeDropDown_list.getSelectionModel().getSelectedItem();
        String contactName = contactName_input.getText();
        String contactEmail = contactEmail_input.getText();

        if(Objects.equals(userType, "admin")){

            //Display warning message that administrators cannot be created.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Unable to create user.");
            alert.setContentText("Administrators are unable to be created at this time. Please send an email to rspike3@wgu.edu. :)");
            alert.showAndWait();
        }else{
            if(UsersQuery.insertNewUser(userID, userName, userPassword, userType, contactName, contactEmail) > 0) {
                    //Display popup message confirming user creation.
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("User created");
                    alert.setContentText("User was successfully created. Welcome " + userName);
                    alert.showAndWait();

                    //Add new users to local list
                    UsersInHouse newUser = new UsersInHouse(userID, userName, userPassword, userType);
                    UsersList.addUser(newUser);

                    Parent AppointmentScreenLoader = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Models/loginUI.fxml")));
                    Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(AppointmentScreenLoader, 300, 300);
                    stage.setTitle("Spikes Scheduler");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userTypes.add("admin");
        userTypes.add("contact");

        userTypeDropDown_list.setItems(userTypes);
        userTypeDropDown_list.getSelectionModel().select(1);

        contactName_input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String name = contactName_input.getText();
                name = name.trim().replaceAll("\\s+", ""); // Remove leading/trailing spaces and all other spaces
                contactEmail_input.setText(name + "@company.com");
            }
        });

        try {

            //Gets the number of users from the database.
            int userCount = UsersQuery.getUserCount();

            //Generates a user ID based on the number of users currently in the database.
            userID_input.setText(String.valueOf(userCount + 1));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
