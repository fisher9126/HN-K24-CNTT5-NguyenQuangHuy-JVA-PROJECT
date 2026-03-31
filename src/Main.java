import booking.presentation.MainMenu;
import booking.utils.DatabaseConnection;

import java.sql.Connection;

public class Main {
    static void main(String[] args) {

        System.out.println();
        Connection con= DatabaseConnection.getConnection();
        System.out.println("Connected to database successfully");
        MainMenu menu = new MainMenu();
        menu.menu();
    }
}