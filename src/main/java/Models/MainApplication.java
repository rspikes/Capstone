package Models;

import Models.LoginScreen;

import java.sql.SQLException;

public class MainApplication {
    public static void main(String[] args){
        try {
            LoginScreen.main(args);
        }catch (SQLException ignored){
        }
    }
}
