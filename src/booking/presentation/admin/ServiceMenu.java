package booking.presentation.admin;

import booking.service.ServiceService;

import java.util.Scanner;

public class ServiceMenu {
    private static Scanner sc = new Scanner(System.in);
    private static ServiceService service = new ServiceService();

    public static void serviceMenu() {
        do {
            try {
                System.out.println();
                System.out.println("╔════════════════════════════════╗");
                System.out.println("║           SERVICE MENU         ║");
                System.out.println("╠════════════════════════════════╣");
                System.out.println("║  1. DISPLAY ALL SERVICES       ║");
                System.out.println("║  2. ADD NEW SERVICE            ║");
                System.out.println("║  3. UPDATE SERVICE             ║");
                System.out.println("║  4. DELETE SERVICE             ║");
                System.out.println("║  5. FIND SERVICE BY NAME       ║");
                System.out.println("║  6. FIND SERVICE BY ID         ║");
                System.out.println("║  7. SORT SERVICES BY NAME      ║");
                System.out.println("║  8. SORT SERVICES BY PRICE     ║");
                System.out.println("║  0. EXIT                       ║");
                System.out.println("╚════════════════════════════════╝");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1: service.displayAll(); break;
                    case 2: service.addService(); break;
                    case 3: service.updateService(); break;
                    case 4: service.deleteService(); break;
                    case 5: service.findByName(); break;
                    case 6: service.findById(); break;
                    case 7: service.sortByName(); break;
                    case 8: service.sortByPrice(); break;
                    case 0: return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        } while (true);
    }
}