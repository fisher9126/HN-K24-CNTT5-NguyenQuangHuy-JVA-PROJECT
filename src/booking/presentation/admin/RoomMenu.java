package booking.presentation.admin;

import booking.service.RoomService;

import java.util.Scanner;

public class RoomMenu {
    private static Scanner sc = new Scanner(System.in);
    private static RoomService service = new RoomService();
    public static void roomMenu() {
        do {
            try{
                System.out.println();
                System.out.println("╔════════════════════════════════╗");
                System.out.println("║            ROOM MENU           ║");
                System.out.println("╠════════════════════════════════╣");
                System.out.println("║  1. DISPLAY ALL ROOMS          ║");
                System.out.println("║  2. ADD NEW ROOM               ║");
                System.out.println("║  3. UPDATE ROOM                ║");
                System.out.println("║  4. DELETE ROOM                ║");
                System.out.println("║  5. FIND ROOM BY NAME          ║");
                System.out.println("║  6. FIND ROOM BY ID            ║");
                System.out.println("║  7. SORT ROOM BY PRICE         ║");
                System.out.println("║  8. SORT ROOM BY NAME          ║");
                System.out.println("║  0. EXIT                       ║");
                System.out.println("╚════════════════════════════════╝");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1:
                        service.displayAll();
                        System.out.println("Press ENTER to exit...");
                        sc.nextLine();
                        break;
                    case 2: service.addRoom(); break;
                    case 3: service.updateRoom(); break;
                    case 4: service.deleteRoom(); break;
                    case 5: service.findByName(); break;
                    case 6: service.findById(); break;
                    case 7: service.sortByPrice(); break;
                    case 8: service.sortByName(); break;
                    case 0: return;
                    default:
                        System.out.println("Your choice is invalid.");
                }
            }catch(NumberFormatException e){
                System.err.println("Please enter a number...");
            }

        }while (true);
    }
}
