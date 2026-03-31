package booking.service;

import booking.DAO.RoomDAO;
import booking.entity.Room;
import booking.enums.RoomType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class RoomService {

    private RoomDAO roomDAO = new RoomDAO();
    private Scanner sc = new Scanner(System.in);

    public void displayAll() {
        List<Room> list = roomDAO.findAll();

        if (list.isEmpty()) {
            System.out.println("Không có phòng nào ");
            return;
        }


        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              ROOM LIST                         ║");
        System.out.println("╠═════╦════════════════════╦════════════╦════════════╦═══════════╣");
        System.out.println("║ ID  ║ NAME               ║  TYPE      ║  CAPACITY  ║  PRICE    ║");
        System.out.println("╠═════╬════════════════════╬════════════╬════════════╬═══════════╣");


        for (Room r : list) {
            System.out.printf("║ %-3d ║ %-18s ║ %-10s ║ %-10d ║ %-9.2f ║%n",
                    r.getId(),
                    r.getName(),
                    r.getType(),
                    r.getCapacity(),
                    r.getHourlyPrice(),
                    r.getType());

        }

        System.out.println("╚═════╩════════════════════╩════════════╩════════════╩═══════════╝");



    }


    public void addRoom() {
        RoomDAO roomDAO = new RoomDAO();
        Room room = new Room();

        do {
            System.out.print("Name: ");
            room.setName(sc.nextLine());
            if(roomDAO.findAll().stream().filter(r->r.getName().equals(room.getName())).findFirst().isPresent()){
                System.out.println("There is already a room with that name");
            }
        }while(roomDAO.findAll().stream().filter(r->r.getName().equals(room.getName())).findFirst().isPresent());


        room.setType(inputType());
        do {
            System.out.print("Capacity: ");
            room.setCapacity(Integer.parseInt(sc.nextLine()));
            if(room.getCapacity()<=0){
                System.out.println("Capacity must be greater than 0");
            }
        }while(room.getCapacity()<=0);

        do {
            System.out.print("Location: ");
            room.setLocation(sc.nextLine());
            if(room.getLocation().trim().isEmpty()){
                System.out.println("Location cannot empty");
            }
        }while(room.getLocation().isEmpty());

        do {
            try{
                System.out.print("Price/hour: ");
                room.setHourlyPrice(Double.parseDouble(sc.nextLine()));
                if(room.getHourlyPrice()<=0){
                    System.out.println("Price must be greater than 0");
                }
            }catch(NumberFormatException e){
                System.err.println("Price must be number");
            }
        }while(room.getHourlyPrice()<=0);

        do {
            System.out.print("Description: ");
            room.setDescription(sc.nextLine());
            if(room.getDescription().trim().isEmpty()){
                System.out.println("Description cannot empty");
            }
        }while(room.getDescription().isEmpty());


        roomDAO.insert(room);
        System.out.println("Add successfully ");
    }


    public void updateRoom() {
        int id;


        while (true) {
            try {
                System.out.print("Enter Room ID: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be an number ");
            }
        }

        Room room = roomDAO.findById(id);

        if (room == null) {
            System.out.println("Not Found Any Room ! ");
            return;
        }


        System.out.println("\n=== CURRENT INFORMATION ===");
        System.out.println("Name: " + room.getName());
        System.out.println("Type: " + room.getType());
        System.out.println("Capacity: " + room.getCapacity());
        System.out.println("Location: " + room.getLocation());
        System.out.println("Price: " + room.getHourlyPrice());
        System.out.println("Description: " + room.getDescription());

        System.out.println("\n(Press enter to keep the current information)");


        do {
            System.out.print("New Name: ");
            String name = sc.nextLine();
            if (name.trim().isEmpty()) {
                break;
            }

            boolean exists = roomDAO.findAll()
                    .stream()
                    .anyMatch(r -> r.getName().equalsIgnoreCase(name));

            if (exists) {
                System.out.println("Name already exists ...");
            } else {
                room.setName(name);
                break;
            }

        } while (true);



        System.out.println("New Type (Press enter to keep the current information):");
        System.out.println("1. MEETING");
        System.out.println("2. CONFERENCE");
        System.out.println("3. TRAINING");
        System.out.println("4. VIP");

        String typeInput = sc.nextLine();
        if (!typeInput.trim().isEmpty()) {
            try {
                int choice = Integer.parseInt(typeInput);
                switch (choice) {
                    case 1: room.setType(RoomType.MEETING); break;
                    case 2: room.setType(RoomType.CONFERENCE); break;
                    case 3: room.setType(RoomType.TRAINING); break;
                    case 4: room.setType(RoomType.VIP); break;
                    default: System.out.println("Type is invalid  ");
                }
            } catch (Exception e) {
                System.out.println("Type is invalid  ");
            }
        }

        System.out.print("New Capacity: ");
        String capacity = sc.nextLine();
        if (!capacity.trim().isEmpty()) {
            try {
                room.setCapacity(Integer.parseInt(capacity));
            } catch (Exception e) {
                System.out.println("Capacity is invalid, will be keep current information ");
            }
        }

        System.out.print("New Location: ");
        String location = sc.nextLine();
        if (!location.trim().isEmpty()) {
            room.setLocation(location);
        }

        System.out.print("New Price: ");
        String price = sc.nextLine();
        if (!price.trim().isEmpty()) {
            try {
                room.setHourlyPrice(Double.parseDouble(price));
            } catch (Exception e) {
                System.out.println("Price is invalid, will be keep current information ");
            }
        }

        System.out.print("New Description: ");
        String desc = sc.nextLine();
        if (!desc.trim().isEmpty()) {
            room.setDescription(desc);
        }

        roomDAO.update(room);

        System.out.println("Update successfully");
    }


    public void deleteRoom() {
        int id;
        while (true) {
            try {
                System.out.print("Enter Room ID: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be an number ");
            }
        }

        roomDAO.delete(id);
        System.out.println("Delete successfully");
    }


    public void findByName() {
        String keyword=null;
       do {
           System.out.print("Enter Room Name: ");
            keyword = sc.nextLine().toLowerCase();
           if(keyword.trim().isEmpty()){
               System.out.println("Name cannot be empty");;
           }
       }while (keyword.trim().isEmpty());
        List<Room> list = roomDAO.findAll();

        boolean found = false;
        for (Room r : list) {
            if (r.getName().toLowerCase().contains(keyword)) {
                System.out.println(r.getId() + " | " + r.getName() + " | " + r.getHourlyPrice());
                found = true;
            }
        }

        if (!found) {
            System.out.println("Not Found Any Room ! ");
        }
    }


    public void findById() {
        int id;
        while (true) {
            try {
                System.out.print("Enter Room ID: ");
                id = Integer.parseInt(sc.nextLine());
                break;
            } catch (Exception e) {
                System.out.println("ID must be an number ");
            }
        }

        Room r = roomDAO.findById(id);

        if (r == null) {
            System.out.println("Not Found Any Room ! ");
        } else {
            System.out.println(r.getId() + " | " + r.getName() + " | " + r.getHourlyPrice());
        }
    }


    public void sortByPrice() {
        List<Room> list = roomDAO.findAll();

        Collections.sort(list, Comparator.comparingDouble(Room::getHourlyPrice));

        displayList(list);
    }


    public void sortByName() {
        List<Room> list = roomDAO.findAll();

        Collections.sort(list, Comparator.comparing(Room::getName));

        displayList(list);
    }


    private void displayList(List<Room> list) {
        for (Room r : list) {
            System.out.println(r.getId() + " | " + r.getName() + " | " + r.getHourlyPrice());
        }
    }


    private RoomType inputType() {
        while (true) {
            System.out.println("CHOOSE ROOM TYPE:");
            System.out.println("1. MEETING");
            System.out.println("2. CONFERENCE");
            System.out.println("3. TRAINING");
            System.out.println("4. VIP");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1: return RoomType.MEETING;
                    case 2: return RoomType.CONFERENCE;
                    case 3: return RoomType.TRAINING;
                    case 4: return RoomType.VIP;
                    default:
                        System.out.println("YOUR CHOOSE IS INVALID ");
                }
            } catch (Exception e) {
                System.out.println("MUST BE AN NUMBER ");
            }
        }
    }
}