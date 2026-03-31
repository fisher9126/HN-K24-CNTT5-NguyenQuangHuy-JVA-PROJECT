package booking.service;

import booking.DAO.ServiceDAO;
import booking.entity.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class ServiceService {

    private ServiceDAO serviceDAO = new ServiceDAO();
    private Scanner sc = new Scanner(System.in);

    public void displayAll() {
        List<Service> list = serviceDAO.findAll();
        if (list.isEmpty()) {
            System.out.println("No services available.");
            return;
        }
        System.out.println("╔════╦══════════════════════╦════════════╦════════════════════════════════════════════════════╗");
        System.out.println("║ ID ║ NAME                 ║ PRICE      ║ DESCRIPTION                                        ║");
        System.out.println("╠════╬══════════════════════╬════════════╬════════════════════════════════════════════════════╣");

        for (Service s : list) {
            System.out.printf(
                    "║ %-2d ║ %-20s ║ %-10.2f ║ %-50s ║%n",
                    s.getId(),
                    s.getName(),
                    s.getPrice(),
                    s.getDescription()
            );
        }

        System.out.println("╚════╩══════════════════════╩════════════╩════════════════════════════════════════════════════╝");
        System.out.println("Press ENTER to exit...");
        sc.nextLine();
    }

    public void addService() {
        Service service = new Service();

        // Name
        do {
            System.out.print("Enter Name: ");
            service.setName(sc.nextLine());
            if (service.getName().trim().isEmpty()) {
                System.out.println("Name cannot be empty.");
            }
        } while (service.getName().trim().isEmpty());

        // Price
        while (true) {
            System.out.print("Enter Price: ");
            String input = sc.nextLine();
            try {
                service.setPrice(Double.parseDouble(input));
                if (service.getPrice() < 0) {
                    System.out.println("Price must be >= 0");
                } else break;
            } catch (NumberFormatException e) {
                System.out.println("Price must be a number.");
            }
        }

        // Description
        System.out.print("Enter Description: ");
        service.setDescription(sc.nextLine());

        serviceDAO.insert(service);
        System.out.println("Service added successfully.");
    }

    public void updateService() {
        int id;
        while (true) {
            try {
                System.out.print("Enter Service ID to update: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be a number.");
            }
        }

        Service service = serviceDAO.findById(id);
        if (service == null) {
            System.out.println("Service not found.");
            return;
        }

        System.out.println("\n=== CURRENT INFORMATION ===");
        System.out.printf("Name: %s%nPrice: %.2f%nDescription: %s%n",
                service.getName(), service.getPrice(), service.getDescription());
        System.out.println("(Press enter to keep current value)");

        // Name
        System.out.print("New Name: ");
        String name = sc.nextLine();
        if (!name.trim().isEmpty()) service.setName(name);

        // Price
        System.out.print("New Price: ");
        String priceInput = sc.nextLine();
        if (!priceInput.trim().isEmpty()) {
            try {
                double price = Double.parseDouble(priceInput);
                if (price >= 0) service.setPrice(price);
                else System.out.println("Invalid price, keeping current value.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, keeping current value.");
            }
        }

        // Description
        System.out.print("New Description: ");
        String desc = sc.nextLine();
        if (!desc.trim().isEmpty()) service.setDescription(desc);

        serviceDAO.update(service);
        System.out.println("Service updated successfully.");
    }

    public void deleteService() {
        int id;
        while (true) {
            try {
                System.out.print("Enter Service ID to delete: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be a number.");
            }
        }

        serviceDAO.delete(id);
        System.out.println("Service deleted successfully.");
    }

    public void findByName() {
        System.out.print("Enter Name to search: ");
        String keyword = sc.nextLine().toLowerCase();

        List<Service> list = serviceDAO.findAll();
        boolean found = false;
        for (Service s : list) {
            if (s.getName().toLowerCase().contains(keyword)) {
                System.out.printf("%d | %s | %.2f | %s%n",
                        s.getId(), s.getName(), s.getPrice(), s.getDescription());
                found = true;
            }
        }

        if (!found) System.out.println("No services found.");
    }

    public void findById() {
        int id;
        while (true) {
            try {
                System.out.print("Enter Service ID to find: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be a number.");
            }
        }

        Service s = serviceDAO.findById(id);
        if (s == null) System.out.println("Service not found.");
        else System.out.printf("%d | %s | %.2f | %s%n",
                s.getId(), s.getName(), s.getPrice(), s.getDescription());
    }
    public void sortByName() {
        List<Service> list = serviceDAO.findAll();
        Collections.sort(list, Comparator.comparing(Service::getName));
        displayList(list);
    }

    public void sortByPrice() {
        List<Service> list = serviceDAO.findAll();
        Collections.sort(list, Comparator.comparingDouble(Service::getPrice));
        displayList(list);
    }

    private void displayList(List<Service> list) {
        if (list.isEmpty()) {
            System.out.println("No services available.");
            return;
        }
        System.out.println("ID | Name | Price | Description");
        for (Service s : list) {
            System.out.printf("%d | %s | %.2f | %s%n", s.getId(), s.getName(), s.getPrice(), s.getDescription());
        }
    }
}