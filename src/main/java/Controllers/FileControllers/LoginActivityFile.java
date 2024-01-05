package Controllers.FileControllers;

import LambdaInterfaces.LoginActivityInterface;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * A utility class for logging user login activity to a file.
 */
public class LoginActivityFile {

    public static LoginActivityInterface loginAttempt = (userName, attempt) -> "User: " + userName + " | " + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + " | Attempt: " + attempt;

    /**
     * Log user login activity to a file.
     * Lambda expression is used to get the users info and the status of the login attempt
     * Lambda expression simplifies the recording of the login attempt and improves readability where login attempts need to be tracked.
     * Output is then put into the LoginActivity.txt file
     * @param userInfo The user's information (e.g., username).
     * @param attempt  The status of the login attempt (e.g., "Successful" or "Failed").
     */
    public static void logUserActivity(String userInfo, String attempt) {

        String filename = "login_activity.txt";

        try (FileWriter writer = new FileWriter(filename, true);
             PrintWriter outputFile = new PrintWriter(writer)) {
            outputFile.println(loginAttempt.getLoginAttempt(userInfo, attempt));
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
        }
    }

    public static void getUserActivity(String userNameInput, String successful) {

        logUserActivity(userNameInput, successful);

    }
}
