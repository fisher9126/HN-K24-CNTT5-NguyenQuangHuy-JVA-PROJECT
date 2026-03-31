package booking.presentation.admin;

import booking.service.NotificationService;
import booking.service.UserService;

import java.util.Scanner;

public class UserMenu {

    private static Scanner sc = new Scanner(System.in);
    private static UserService service = new UserService();

    public static void userMenu() {

        do {
            try {
                System.out.println();
                System.out.println("╔════════════════════════════════╗");
                System.out.println("║            USER MENU           ║");
                System.out.println("╠════════════════════════════════╣");
                System.out.println("║  1. DISPLAY ALL USERS          ║");
                System.out.println("║  2. UPDATE USER                ║");
                System.out.println("║  3. FIND USER BY ID            ║");
                System.out.println("║  4. FIND USER BY NAME          ║");
                System.out.println("║  5. SORT USERS BY ID           ║");
                System.out.println("║  6. SORT USERS BY NAME         ║");
                System.out.println("║  0. EXIT                       ║");
                System.out.println("╚════════════════════════════════╝");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> service.displayAll();
                    case 2 -> service.updateUser();
                    case 3 -> service.findById();
                    case 4 -> service.findByName();
                    case 5 -> service.sortById();
                    case 6 -> service.sortByName();
                    case 0 -> { return; }
                    default -> System.out.println("Your choice is invalid.");
                }

            } catch (NumberFormatException e) {
                System.err.println("Please enter a number...");
            }

        } while (true);
    }
}