package booking.presentation;

import booking.service.BookingService;

import java.util.Scanner;

public class SupportMenu {
    private  static Scanner sc=new Scanner(System.in);
    public static void supportMenu() {
        BookingService bookingService = new BookingService();
        Scanner sc=new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("╔════════════════════════════════╗");
            System.out.println("║           SUPPORT MENU         ║");
            System.out.println("╠════════════════════════════════╣");
            System.out.println("║ 1. View tasks                  ║");
            System.out.println("║ 2. Update status               ║");
            System.out.println("║ 0. Logout                      ║");
            System.out.println("╚════════════════════════════════╝");
            System.out.print  ("Your choice: ");


            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    bookingService.supportTask();
                    System.out.println("Press enter to exit...");
                    sc.nextLine();
                    break;
                case 2:
                    bookingService.updateRoomStatus();
                    break;
                case 0:
                    return;
            }
        }
    }

}
