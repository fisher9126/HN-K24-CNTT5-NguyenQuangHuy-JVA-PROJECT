package booking.presentation.admin;

import booking.service.EquipmentService;

import java.util.Scanner;

public class EquipmentMenu {
    private static Scanner sc = new Scanner(System.in);
    private static EquipmentService service = new EquipmentService();

    public static void equipmentMenu() {
        do {
            try {
                System.out.println();
                System.out.println("╔════════════════════════════════╗");
                System.out.println("║         EQUIPMENT MENU         ║");
                System.out.println("╠════════════════════════════════╣");
                System.out.println("║  1. DISPLAY ALL EQUIPMENTS     ║");
                System.out.println("║  2. ADD NEW EQUIPMENT          ║");
                System.out.println("║  3. UPDATE EQUIPMENT           ║");
                System.out.println("║  4. DELETE EQUIPMENT           ║");
                System.out.println("║  5. FIND EQUIPMENT BY NAME     ║");
                System.out.println("║  6. FIND EQUIPMENT BY ID       ║");
                System.out.println("║  7. SORT EQUIPMENT BY NAME     ║");
                System.out.println("║  8. SORT EQUIPMENT BY TOTAL    ║");
                System.out.println("║  0. EXIT                       ║");
                System.out.println("╚════════════════════════════════╝");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1: service.displayAll(); break;
                    case 2: service.addEquipment(); break;
                    case 3: service.updateEquipment(); break;
                    case 4: service.deleteEquipment(); break;
                    case 5: service.findByName(); break;
                    case 6: service.findById(); break;
                    case 7: service.sortByName(); break;
                    case 8: service.sortByTotal(); break;
                    case 0: return;
                    default:
                        System.out.println("Your choice is invalid.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Please enter a number...");
            }
        } while (true);
    }
}
