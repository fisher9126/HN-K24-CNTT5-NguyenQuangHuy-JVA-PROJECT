package booking.service;

import booking.DAO.EquipmentDAO;
import booking.entity.Equipment;
import booking.enums.EquipmentStatus;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class EquipmentService {

    private EquipmentDAO equipmentDAO = new EquipmentDAO();
    private Scanner sc = new Scanner(System.in);

    // DISPLAY ALL
    public void displayAll() {
        List<Equipment> list = equipmentDAO.findAll();

        if (list.isEmpty()) {
            System.out.println("No equipment found.");
            return;
        }

        System.out.println("╔════╦══════════════════════╦════════╦═══════════╦════════════╦════════════════════════════════════════════════════╗");
        System.out.println("║ ID ║ NAME                 ║ TOTAL  ║ AVAILABLE ║ STATUS     ║ DESCRIPTION                                        ║");
        System.out.println("╠════╬══════════════════════╬════════╬═══════════╬════════════╬════════════════════════════════════════════════════╣");

        for (Equipment eq : list) {
            System.out.printf(
                    "║ %-2d ║ %-20s ║ %-6d ║ %-9d ║ %-10s ║ %-50s ║%n",
                    eq.getId(),
                    eq.getName(),
                    eq.getTotalQuantity(),
                    eq.getAvailableQuantity(),
                    eq.getStatus(),
                    eq.getDescription()
            );
        }

        System.out.println("╚════╩══════════════════════╩════════╩═══════════╩════════════╩════════════════════════════════════════════════════╝");
        System.out.println("Press ENTER to exit...");
        sc.nextLine();
    }


    public void addEquipment() {
        Equipment eq = new Equipment();


        do {
            System.out.print("Name: ");
            eq.setName(sc.nextLine().trim());
            if (eq.getName().isEmpty()) {
                System.out.println("Name cannot be empty.");
            } else if (equipmentDAO.findAll().stream().anyMatch(e -> e.getName().equalsIgnoreCase(eq.getName()))) {
                System.out.println("Equipment with this name already exists.");
                eq.setName("");
            }
        } while (eq.getName().isEmpty());


        do {
            System.out.print("Total Quantity: ");
            try {
                eq.setTotalQuantity(Integer.parseInt(sc.nextLine()));
                if (eq.getTotalQuantity() < 0) System.out.println("Total quantity cannot be negative.");
            } catch (Exception e) {
                System.out.println("Must be a number.");
                eq.setTotalQuantity(-1);
            }
        } while (eq.getTotalQuantity() < 0);


        do {
            System.out.print("Available Quantity: ");
            try {
                eq.setAvailableQuantity(Integer.parseInt(sc.nextLine()));
                if (eq.getAvailableQuantity() < 0) System.out.println("Available quantity cannot be negative.");
                if(eq.getAvailableQuantity()>eq.getTotalQuantity()){
                    System.out.println("Available quantity cannot be greater than total quantity.");
                }
            } catch (Exception e) {
                System.out.println("Must be a number.");
                eq.setAvailableQuantity(-1);
            }
        } while (eq.getAvailableQuantity() < 0);


        eq.setStatus(inputStatus());


        do {
            System.out.print("Description: ");
            eq.setDescription(sc.nextLine().trim());
            if (eq.getDescription().isEmpty()) {
                System.out.println("Description cannot be empty.");
            }
        } while (eq.getDescription().isEmpty());

        equipmentDAO.insert(eq);
        System.out.println("Equipment added successfully.");
    }


    public void updateEquipment() {
        int id;

        while (true) {
            try {
                System.out.print("Enter Equipment ID: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be a number.");
            }
        }

        Equipment eq = equipmentDAO.findById(id);
        if (eq == null) {
            System.out.println("Equipment not found.");
            return;
        }


        System.out.println("\n=== CURRENT INFORMATION ===");
        System.out.println("Name: " + eq.getName());
        System.out.println("Total Quantity: " + eq.getTotalQuantity());
        System.out.println("Available Quantity: " + eq.getAvailableQuantity());
        System.out.println("Status: " + eq.getStatus());
        System.out.println("Description: " + eq.getDescription());
        System.out.println("\n(Press Enter to keep current information)");


        while (true) {
            System.out.print("New Name: ");
            String name = sc.nextLine().trim();
            if (name.isEmpty()) break;
            boolean exists = equipmentDAO.findAll().stream()
                    .anyMatch(e -> e.getName().equalsIgnoreCase(name) && e.getId() != eq.getId());
            if (exists) {
                System.out.println("Name already exists.");
            } else {
                eq.setName(name);
                break;
            }
        }


        System.out.print("New Total Quantity: ");
        String totalInput = sc.nextLine().trim();
        if (!totalInput.isEmpty()) {
            try {
                int total = Integer.parseInt(totalInput);
                if (total >= 0) eq.setTotalQuantity(total);
                else System.out.println("Invalid total quantity. Keeping current.");
            } catch (Exception e) {
                System.out.println("Invalid number. Keeping current total quantity.");
            }
        }


        System.out.print("New Available Quantity: ");
        String availInput = sc.nextLine().trim();
        if (!availInput.isEmpty()) {
            try {
                int avail = Integer.parseInt(availInput);
                if (avail >= 0) eq.setAvailableQuantity(avail);
                else System.out.println("Invalid available quantity. Keeping current.");
            } catch (Exception e) {
                System.out.println("Invalid number. Keeping current available quantity.");
            }
        }


        System.out.println("New Status (Press Enter to keep current):");
        System.out.println("1. AVAILABLE");
        System.out.println("2. MAINTENANCE");
        System.out.println("3. BROKEN");
        String statusInput = sc.nextLine().trim();
        if (!statusInput.isEmpty()) {
            try {
                int choice = Integer.parseInt(statusInput);
                switch (choice) {
                    case 1: eq.setStatus(EquipmentStatus.AVAILABLE); break;
                    case 2: eq.setStatus(EquipmentStatus.MAINTENANCE); break;
                    case 3: eq.setStatus(EquipmentStatus.BROKEN); break;
                    default: System.out.println("Invalid status. Keeping current.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Keeping current status.");
            }
        }


        System.out.print("New Description: ");
        String desc = sc.nextLine().trim();
        if (!desc.isEmpty()) eq.setDescription(desc);

        equipmentDAO.update(eq);
        System.out.println("Equipment updated successfully.");
    }


    public void deleteEquipment() {
        int id;
        while (true) {
            try {
                System.out.print("Enter Equipment ID: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be a number.");
            }
        }

        equipmentDAO.delete(id);
        System.out.println("Equipment deleted successfully.");
    }


    public void findByName() {
        String keyword;
        do {
            System.out.print("Enter Equipment Name: ");
            keyword = sc.nextLine().trim().toLowerCase();
            if (keyword.isEmpty()) System.out.println("Name cannot be empty.");
        } while (keyword.isEmpty());

        List<Equipment> list = equipmentDAO.findAll();
        boolean found = false;
        for (Equipment eq : list) {
            if (eq.getName().toLowerCase().contains(keyword)) {
                System.out.println(eq.getId() + " | " + eq.getName() + " | " + eq.getStatus());
                found = true;
            }
        }

        if (!found) System.out.println("No equipment found.");
    }


    public void findById() {
        int id;
        while (true) {
            try {
                System.out.print("Enter Equipment ID: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be a number.");
            }
        }

        Equipment eq = equipmentDAO.findById(id);
        if (eq == null) System.out.println("No equipment found.");
        else System.out.println(eq.getId() + " | " + eq.getName() + " | " + eq.getStatus());
    }


    public void sortByName() {
        List<Equipment> list = equipmentDAO.findAll();
        Collections.sort(list, Comparator.comparing(Equipment::getName));
        displayList(list);
    }


    public void sortByTotal() {
        List<Equipment> list = equipmentDAO.findAll();
        Collections.sort(list, Comparator.comparingInt(Equipment::getTotalQuantity));
        displayList(list);
    }


    private void displayList(List<Equipment> list) {
        for (Equipment eq : list) {
            System.out.println(eq.getId() + " | " + eq.getName() + " | " + eq.getTotalQuantity() +
                    " | " + eq.getAvailableQuantity() + " | " + eq.getStatus());
        }
    }

    private EquipmentStatus inputStatus() {
        while (true) {
            System.out.println("Choose status:");
            System.out.println("1. AVAILABLE");
            System.out.println("2. MAINTENANCE");
            System.out.println("3. BROKEN");

            try {
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1: return EquipmentStatus.AVAILABLE;
                    case 2: return EquipmentStatus.MAINTENANCE;
                    case 3: return EquipmentStatus.BROKEN;
                    default: System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Must be a number.");
            }
        }
    }
}