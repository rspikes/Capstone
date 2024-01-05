package LambdaInterfaces;

import javafx.scene.Scene;

import java.sql.SQLException;

public interface ReportInterface {

    //Lambda expression that will concatenate different reports together.
    String displayReport(String reportX, String reportY) throws SQLException;
}
