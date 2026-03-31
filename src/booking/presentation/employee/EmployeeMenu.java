package booking.presentation.employee;

import booking.service.BookingService;

import java.util.Scanner;

public class EmployeeMenu {
    private static Scanner sc=new Scanner(System.in);
    private static BookingService bookingService=new BookingService();
    public static void employeeMenu() {
        while (true) {
            try {
                System.out.println("╔════════════════════════════════╗");
                System.out.println("║         EMPLOYEE MENU          ║");
                System.out.println("╠════════════════════════════════╣");
                System.out.println("║  1. BOOKING ROOM               ║");
                System.out.println("║  2. MY SCHEDULE                ║");
                System.out.println("║  3. CANCEL BOOKING             ║");
                System.out.println("║  4. MY ACCOUNT                 ║");
                System.out.println("║  0. LOG OUT                    ║");
                System.out.println("╚════════════════════════════════╝");
                System.out.print("Enter your choice : ");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        bookingService.booking();
                        break;
                    case 2:
                        bookingService.displaySchedule();
                        System.out.println("Press ENTER to exit...");
                        sc.nextLine();
                        break;
                    case 3:
                        bookingService.cancelBooking();
                        break;
                    case 4:

                        break;
                    case 0:
                        return;
                }

            } catch (NumberFormatException ex) {
                System.out.println("Please enter a number");
            }
        }

    }
    public static void booking() {

    }
}
