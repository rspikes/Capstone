module com.spikes.scheduler {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens Models to javafx.fxml;
    opens Controllers.AppointmentControllers to javafx.fxml;
    opens dao to javafx.fxml;
    exports Models;
    exports Controllers.AppointmentControllers;
    exports dao;
    exports Controllers.CustomerControllers;
    opens Controllers.CustomerControllers to javafx.fxml;
    exports Controllers.LoginScreenController;
    opens Controllers.LoginScreenController to javafx.fxml;
    exports Objects.AppointmentObjects;
    opens Objects.AppointmentObjects to javafx.fxml;
    exports Objects.CustomerObjects;
    opens Objects.CustomerObjects to javafx.fxml;
    exports Controllers.ContactControllers;
    opens Controllers.ContactControllers to javafx.fxml;

}