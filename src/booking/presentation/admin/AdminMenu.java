package booking.presentation.admin;

import booking.entity.Booking;
import booking.service.BookingService;
import booking.service.Statistics;

import java.util.Scanner;

public class AdminMenu {
    public static  Scanner sc=new Scanner(System.in);
    public static void adminMenu() {
        BookingService bookingService=new BookingService();

            while (true) {
                try {
                    System.out.println();
                    System.out.println("╔══════════════════════════════════════╗");
                    System.out.println("║               ADMIN MENU             ║");
                    System.out.println("╠══════════════════════════════════════╣");
                    System.out.println("║  1. Manage rooms                     ║");
                    System.out.println("║  2. Manage equipment                 ║");
                    System.out.println("║  3. Manage service                   ║");
                    System.out.println("║  4. Manage user                      ║");
                    System.out.println("║  5. Approve bookings                 ║");
                    System.out.println("║  6. Statistics                       ║");
                    System.out.println("║  0. Logout                           ║");
                    System.out.println("╚══════════════════════════════════════╝");
                    System.out.print("Enter your choice: ");
                    int choice = Integer.parseInt(sc.nextLine());

                    switch (choice) {
                        case 1:
                            RoomMenu.roomMenu();
                            break;
                        case 2:
                            EquipmentMenu.equipmentMenu();
                            break;
                        case 3:
                            ServiceMenu.serviceMenu();
                            break;
                        case 4:
                            UserMenu.userMenu();
                            break;
                        case 5:
                            bookingService.updateStatus();
                            break;
                        case 6:
                            Statistics statistics=new Statistics();
                            statistics.RoomStatistics();
                            break;
                        case 0:
                            return;
                        default:
                            System.out.println("Your choice is invalid.");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Please enter a number");

                }

            }
    }

}
