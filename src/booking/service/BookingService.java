package booking.service;

import booking.DAO.*;
import booking.entity.*;
import booking.enums.*;
import booking.utils.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class BookingService {
    private BookingDAO bookingDAO = new BookingDAO();
    private RoomService roomService = new RoomService();
    private UserDAO userDAO = new UserDAO();
    private UserService userService = new UserService();
    private RoomDAO roomDAO = new RoomDAO();
    private Scanner sc = new Scanner(System.in);
    public  void booking(){
        roomService.displayAll();
        List<Room> rooms = roomDAO.findAll();
        List<Booking> bookings=bookingDAO.findAll();
        if(rooms.size()==0){
            System.out.println("Don't have any room!");
            return;
        }
        int roomId=-1;


        do {
            try {
                System.out.print("Enter RoomID: ");
                roomId = Integer.parseInt(sc.nextLine());
                int flag=0;
                for(int i=0;i<rooms.size();i++){
                    if(rooms.get(i).getId()==roomId){
                        flag=1;
                        break;
                    }
                }
                if(flag==0){
                    System.out.println("Not Found Any RoomID...");
                }else{

                    int finalRoomId = roomId;
                    bookings=bookings.stream().filter(b->b.getRoomId()== finalRoomId).filter(b->b.getStatus().equals(BookingStatus.PENDING)||b.getStatus().equals(BookingStatus.APPROVED)).collect(Collectors.toList());
                    Collections.sort(bookings, Comparator.comparing(Booking::getStartTime));
                    System.out.println("╔════════════════════════════════════════════════════╗");
                    System.out.println("║                      BUSY TIME                     ║");
                    System.out.println("╠════════╦═════════════════════╦═════════════════════╣");
                    System.out.println("║ RoomID ║     Start Time      ║       End Time      ║");
                    System.out.println("╠════════╬═════════════════════╬═════════════════════╣");
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    for (Booking booking : bookings) {
                        String roomId2    = String.valueOf(booking.getRoomId());
                        String startTime = booking.getStartTime().format(fmt);
                        String endTime   = booking.getEndTime().format(fmt);

                        System.out.printf(
                                "║ %-6s ║ %-18s ║ %-18s ║%n",
                                roomId2, startTime, endTime
                        );
                        System.out.println("╠════════╬═════════════════════╬═════════════════════╣");

                    }
                    System.out.println("╚════════╩═════════════════════╩═════════════════════╝");

                    break;
                }

            } catch (NumberFormatException e) {
                System.out.println("RoomID must be a number!");

            }
        } while (true);






        List<ScheduleTime> scheduleTimeList = new ArrayList<>();
        Collections.sort(scheduleTimeList);
        List<Booking> temp=bookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.PENDING
                        || b.getStatus() == BookingStatus.APPROVED).collect(Collectors.toList());

                temp.forEach(booking -> {scheduleTimeList.add(new ScheduleTime(booking.getStartTime(), booking.getEndTime()));});
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime startTime;
        LocalDateTime endTime;

        while (true) {
            try {

                System.out.print("Enter Start Time (yyyy-MM-dd HH:mm:ss): ");
                String time1 = sc.nextLine();

                if (!time1.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}")) {
                    System.out.println("Invalid Start Time format!");
                    continue;
                }

                startTime = LocalDateTime.parse(time1, dft);

                if (startTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Start time must be after now!");
                    continue;
                }
                System.out.print("Enter End Time (yyyy-MM-dd HH:mm:ss): ");
                String time2 = sc.nextLine();

                if (!time2.matches("\\d{4}-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}")) {
                    System.out.println("Invalid End Time format!");
                    continue;
                }

                endTime = LocalDateTime.parse(time2, dft);

                if (!endTime.isAfter(startTime)) {
                    System.out.println("End time must be after Start time!");
                    continue;
                }
                boolean isValid = true;
                for (ScheduleTime s : scheduleTimeList) {
                    boolean overlap = !(endTime.isBefore(s.getStart()) || startTime.isAfter(s.getEnd()));

                    if (overlap) {
                        isValid = false;
                        break;
                    }
                }

                if (isValid) {
                    System.out.println("Booking time OK!");
                    break;
                } else {
                    System.out.println("This time is busy! Choose another.");
                }

            } catch (Exception e) {
                System.out.println("Invalid input! Try again.");
            }
        }
        int supportStaffId = 0;
        User current=CurrentUser.getCurrentUser();
        Booking booking = new Booking();
        booking.setUserId(current.getId());
        booking.setRoomId(roomId);
        booking.setSupportStaffId(supportStaffId);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        int currentBooking=bookingDAO.insert(booking);

        EquipmentDAO equipmentDAO = new EquipmentDAO();
        EquipmentBookingDAO equipmentBookingDAO = new EquipmentBookingDAO();
        List<Equipment>  equipmentList = equipmentDAO.findAll().stream().filter(e->e.getStatus().equals(EquipmentStatus.AVAILABLE)).collect(Collectors.toList());





        boolean continueInput=true;
        do {
            System.out.println("╔════╦════════════════╦════════════╦════════════╦════════════════════════════════════════════════════╗");
            System.out.println("║ ID ║      Name      ║ Total Qty  ║ Available  ║                 Description                        ║");
            System.out.println("╠════╬════════════════╬════════════╬════════════╬════════════════════════════════════════════════════╣");

            for (Equipment e : equipmentList) {
                String desc = e.getDescription();
                if (desc.length() > 50) desc = desc.substring(0, 50);

                System.out.printf(
                        "║ %-2d ║ %-14s ║ %-10d ║ %-10d ║ %-50s ║%n",
                        e.getId(),
                        e.getName(),
                        e.getTotalQuantity(),
                        e.getAvailableQuantity(),
                        desc
                );

            }
            System.out.println("╚════╩════════════════╩════════════╩════════════╩════════════════════════════════════════════════════╝");

            int equipID;
            EquipmentBooking equipmentBooking = new EquipmentBooking();
            Equipment equipment = new Equipment();
            do {
                try{

                    System.out.print("Enter EquipID: ");
                    equipID = Integer.parseInt(sc.nextLine());
                    int flag=0;
                    for(Equipment e : equipmentList){
                        if(e.getId()==equipID){
                            flag=1;
                            equipment=e;
                            break;
                        }
                    }
                    if(flag==0){
                        System.out.println("Not Found EquipID...");
                    }
                    else{
                        break;
                    }
                }catch (NumberFormatException e){
                    System.out.println("Invalid EquipmentID!");
                }
            }while (true);
            if(equipment!=null){
                int quantity;
                do {
                    try{
                        if (equipment.getAvailableQuantity()==0){
                            System.out.println("Not Available Quantity!");
                            break;
                        }
                        System.out.print("Enter Quantity: ");
                        quantity = Integer.parseInt(sc.nextLine());
                        if(quantity<0){
                            System.out.println("Invalid Quantity!");
                        }
                        if(quantity>equipment.getAvailableQuantity()){
                            System.out.println("Not enough quantity available !");
                        }
                        else{
                            List<EquipmentBooking>list=equipmentBookingDAO.findAll().stream().filter(e->e.getBookingId()==currentBooking).collect(Collectors.toList());

                            int flag=0;
                            for(EquipmentBooking e : list){
                                if(e.getEquipmentId()==equipID){
                                    flag=1;
                                    equipmentBooking=e;
                                    break;
                                }
                            }
                            if(flag==0){

                                equipmentBooking.setQuantity(quantity);
                                equipmentBooking.setBookingId(currentBooking);
                                equipmentBooking.setEquipmentId(equipment.getId());
                                equipmentBookingDAO.insert(equipmentBooking);
                            }else{

                                equipmentBooking.setQuantity(equipmentBooking.getQuantity()+quantity);
                                equipmentBookingDAO.update(equipmentBooking);

                            }

                            equipment.setAvailableQuantity(equipment.getAvailableQuantity()-quantity);
                            equipmentDAO.update(equipment);


                            break;
                        }

                    }catch (NumberFormatException e){
                        System.out.println("Invalid Quantity!");
                    }
                }while (true);
            }


            System.out.println("Continue to add (Y/N): ");
            String input = sc.nextLine();
            if(input.equalsIgnoreCase("Y")){
                continueInput = true;
            }else if(input.equalsIgnoreCase("N")){
                continueInput = false;
                break;
            }else{
                System.out.println("Invalid input! Try again.");
            }




        }while (true);


        boolean continue2=true;

        do {


            ServiceDAO serviceDAO = new ServiceDAO();
            List<Service> serviceList = serviceDAO.findAll();
            System.out.println("╔════╦════════════════╦════════════╦════════════════════════════════════════════════════╗");
            System.out.println("║ ID ║      Name      ║ Price      ║                 Description                        ║");
            System.out.println("╠════╬════════════════╬════════════╬════════════════════════════════════════════════════╣");

            for (Service s : serviceList) {
                String desc = s.getDescription();
                if (desc.length() > 50) desc = desc.substring(0, 50);

                System.out.printf(
                        "║ %-2d ║ %-14s ║ %-10f ║ %-50s ║%n",
                        s.getId(),
                        s.getName(),
                        s.getPrice(),
                        desc
                );

            }
            System.out.println("╚════╩════════════════╩════════════╩════════════════════════════════════════════════════╝");
            int serviceID = -1;
            int quantity = -1;
            Service service = new Service();
            do {
                try {
                    System.out.print("Enter ServiceID: ");
                    serviceID = Integer.parseInt(sc.nextLine());
                    if (serviceID < 0) {
                        System.out.println("Invalid ServiceID!");
                    } else {
                        int flag = 0;
                        for (Service s : serviceList) {
                            if (s.getId() == serviceID) {
                                service = s;
                                flag = 1;
                            }
                        }
                        if (flag == 0) {
                            System.out.println("Not Found ServiceID...");
                        } else {

                            do {
                                try {
                                    System.out.print("Enter Quantity: ");
                                    quantity = Integer.parseInt(sc.nextLine());
                                    if (quantity < 0) {
                                        System.out.println("Invalid Quantity!");
                                    } else {
                                        ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
                                        ServiceBooking serviceBooking = new ServiceBooking();
                                        int flag2 = 0;

                                        for (ServiceBooking s : serviceBookingDAO.findAll().stream().filter(e->e.getBookingId()==currentBooking).collect(Collectors.toList())) {
                                            if(s.getServiceId()==serviceID){
                                                flag2=1;
                                                serviceBooking=s;
                                                break;
                                            }
                                        }
                                        if (flag2 == 0) {

                                            serviceBooking.setServiceId(serviceID);
                                            serviceBooking.setBookingId(currentBooking);
                                            serviceBooking.setQuantity(quantity);
                                            serviceBooking.setUnitPrice(service.getPrice()*quantity);
                                            System.out.println(serviceBooking);
                                            serviceBookingDAO.insert(serviceBooking);
                                        }else{
                                            serviceBooking.setQuantity(serviceBooking.getQuantity()+quantity);
                                            serviceBooking.setUnitPrice(service.getPrice()*serviceBooking.getQuantity());
                                            serviceBookingDAO.update(serviceBooking);

                                        }
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid ServiceID!");
                                }
                            } while (true);
                            break;
                        }
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Invalid ServiceID!");
                }

            } while (true);

            System.out.println("Continue to add (Y/N): ");
            String input = sc.nextLine();
            if(input.equalsIgnoreCase("Y")){
                continue2 = true;
            }else if(input.equalsIgnoreCase("N")){
                continue2 = false;
                break;
            }else{
                System.out.println("Invalid input! Try again.");
            }

        }while (true);

        BookingDAO bookingDAO = new BookingDAO();
        Booking done=bookingDAO.findById(currentBooking);
        ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
        List<ServiceBooking> list=serviceBookingDAO.findAll().stream().filter(e->e.getBookingId()==done.getId()).collect(Collectors.toList());
        double total = list.stream().mapToDouble(b -> b.getUnitPrice()).sum();

        double hours = Duration.between(
                done.getStartTime(),
                done.getEndTime()
        ).toMinutes();

        RoomDAO roomDAO = new RoomDAO();
        Room room=roomDAO.findById(done.getRoomId());
        double totalPrice=hours*room.getHourlyPrice()/60;

        done.setTotalPrice(totalPrice+total);
        bookingDAO.update(done);

        System.out.println("Booking OK!");
        System.out.println("This booking will be notice to ADMIN!");










    }
    public void displaySchedule(){
        try(Connection con= DatabaseConnection.getConnection()){
            List<Booking> bookings = new ArrayList<>();
            List<Room> rooms = roomDAO.findAll();
            CallableStatement cs=con.prepareCall("{call proc_find_booking_by_userId(?)}");
            cs.setInt(1,CurrentUser.getCurrentUser().getId());
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt(1));
                booking.setUserId(rs.getInt(2));
                booking.setRoomId(rs.getInt(3));
                booking.setSupportStaffId(rs.getInt(4));
                booking.setStartTime(rs.getTimestamp(5).toLocalDateTime());
                booking.setEndTime(rs.getTimestamp(6).toLocalDateTime());
                booking.setTotalPrice(rs.getDouble(7));
                booking.setStatus(BookingStatus.valueOf(rs.getString(8)));
                booking.setRoomStatus(RoomStatus.valueOf(rs.getString(10)));
                bookings.add(booking);
            }
            bookings.sort(Comparator.comparing(Booking::getStartTime));

            List<Room> list = rooms.stream()
                    .filter(r -> bookings.stream().anyMatch(b -> b.getRoomId() == r.getId()))
                    .toList();

            if(list.size()==0){
                System.out.println("Don't have any schedule !");
            }else{
                System.out.println("╔════════════╦══════════╦═══════════════╦══════════════════════╦══════════════════════╦═══════════════╗");
                System.out.println("║ BookingID  ║ RoomID   ║ Location      ║ Start Time           ║ End Time             ║ Status        ║");
                System.out.println("╠════════════╬══════════╬═══════════════╬══════════════════════╬══════════════════════╬═══════════════╣");
                for (int i = 0; i < bookings.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).getId() == bookings.get(i).getRoomId()) {


                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


                            System.out.printf(
                                    "║ %-10d ║ %-8s ║ %-13s ║ %-20s ║ %-20s ║ %-13s ║%n",
                                    bookings.get(i).getId(),
                                    bookings.get(i).getRoomId(),
                                    list.get(j).getLocation(),
                                    bookings.get(i).getStartTime().format(formatter),
                                    bookings.get(i).getEndTime().format(formatter),
                                    bookings.get(i).getStatus()
                            );


                        }
                    }
                }
                System.out.println("╚════════════╩══════════╩═══════════════╩══════════════════════╩══════════════════════╩═══════════════╝");

            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void cancelBooking(){
        try(Connection con= DatabaseConnection.getConnection()){
            List<Booking> bookings = new ArrayList<>();
            List<Room> rooms = roomDAO.findAll();
            CallableStatement cs=con.prepareCall("{call proc_find_booking_by_userId(?)}");
            cs.setInt(1,CurrentUser.getCurrentUser().getId());
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt(1));
                booking.setUserId(rs.getInt(2));
                booking.setRoomId(rs.getInt(3));
                booking.setSupportStaffId(rs.getInt(4));
                booking.setStartTime(rs.getTimestamp(5).toLocalDateTime());
                booking.setEndTime(rs.getTimestamp(6).toLocalDateTime());
                booking.setTotalPrice(rs.getDouble(7));
                booking.setStatus(BookingStatus.valueOf(rs.getString(8)));
                booking.setRoomStatus(RoomStatus.valueOf(rs.getString(10)));
                bookings.add(booking);
            }
            List<Booking> pendingBookings = bookings.stream()
                    .filter(b -> b.getStatus() == BookingStatus.PENDING)
                    .collect(Collectors.toList());
            List<Room> list = rooms.stream()
                    .filter(r -> pendingBookings.stream().anyMatch(b -> b.getRoomId() == r.getId()))
                    .toList();
            if(pendingBookings.size()==0){
                System.out.println("Don't have any schedule !");

            }else {


                System.out.println("╔════════════╦══════════╦═══════════════╦══════════════════════╦══════════════════════╦═══════════════╗");
                System.out.println("║ BookingID  ║ RoomID   ║ Location      ║ Start Time           ║ End Time             ║ Status        ║");
                System.out.println("╠════════════╬══════════╬═══════════════╬══════════════════════╬══════════════════════╬═══════════════╣");
                for (int i = 0; i < pendingBookings.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).getId() == pendingBookings.get(i).getRoomId()) {


                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


                            System.out.printf(
                                    "║ %-10d ║ %-8s ║ %-13s ║ %-20s ║ %-20s ║ %-13s ║%n",
                                    pendingBookings.get(i).getId(),
                                    pendingBookings.get(i).getRoomId(),
                                    list.get(j).getLocation(),
                                    pendingBookings.get(i).getStartTime().format(formatter),
                                    pendingBookings.get(i).getEndTime().format(formatter),
                                    pendingBookings.get(i).getStatus()
                            );


                        }
                    }
                }
                System.out.println("╚════════════╩══════════╩═══════════════╩══════════════════════╩══════════════════════╩═══════════════╝");
                int id;
                do {
                    try {
                        try{
                            System.out.println("Enter Booking ID: ");
                            id = Integer.parseInt(sc.nextLine());
                            int flag = 0;
                            Booking cancel=null;
                            for (int i = 0; i < bookings.size(); i++) {
                                if (pendingBookings.get(i).getId() == id) {
                                    flag = 1;
                                    cancel = pendingBookings.get(i);
                                    break;
                                }
                            }
                            if (flag == 0) {
                                System.out.println("Not Found Any Booking ID...");
                            } else {

                                cancel.setStatus(BookingStatus.CANCELLED);
                                bookingDAO.update(cancel);
                                System.out.println(cancel.getId());


                                ServiceBooking serviceBooking = new ServiceBooking();
                                ServiceBookingDAO serviceBookingDAO = new ServiceBookingDAO();
                                int flag4=0;
                                for(ServiceBooking sb:serviceBookingDAO.findAll()){
                                    if(sb.getBookingId()==cancel.getId()){
                                        flag4=1;
                                        serviceBooking=sb;
                                    }
                                }
                                if(flag4==0){
                                    System.out.println("Not Found Any Booking ID...");
                                }else{

                                    Service service = new Service();
                                    ServiceDAO serviceDAO = new ServiceDAO();
                                    int flag5=0;
                                    for(Service sb:serviceDAO.findAll()){
                                        if(sb.getId()==serviceBooking.getServiceId()){
                                            flag5=1;
                                            service=sb;
                                        }
                                    }
                                    if(flag5==0){
                                        System.out.println("Not Found Any Booking ID...");
                                    }else{
                                        System.out.println(service);
                                        serviceBookingDAO.delete(serviceBooking.getBookingId());
                                    }
                                }


                                EquipmentBooking equipmentBooking = new EquipmentBooking();
                                EquipmentBookingDAO equipmentBookingDAO = new EquipmentBookingDAO();
                                int flag2=0;
                                for(EquipmentBooking eb : equipmentBookingDAO.findAll()){
                                    if(eb.getBookingId()==cancel.getId()){
                                        flag2=1;
                                        equipmentBooking=eb;
                                    }
                                }
                                if(flag2==0){
                                    System.out.println("Not Found Any Booking ID...");
                                }else{
                                    System.out.println(equipmentBooking);
                                    Equipment equipment = new Equipment();
                                    EquipmentDAO equipmentDAO = new EquipmentDAO();
                                    int flag3=0;
                                    for(Equipment equip : equipmentDAO.findAll()){
                                        if(equip.getId()==equipmentBooking.getEquipmentId()){
                                            flag3=1;
                                            equipment=equip;
                                        }
                                    }
                                    if(flag3==0){
                                        System.out.println("Not Found Any Booking ID...");
                                    }else{
                                        System.out.println(equipment);
                                        equipment.setAvailableQuantity(equipment.getAvailableQuantity()+equipmentBooking.getQuantity());
                                        equipmentDAO.update(equipment);
                                        equipmentBookingDAO.delete(equipmentBooking.getBookingId());
                                        System.out.println("This cancel will be notice to ADMIN!");
                                        break;
                                    }
                                }
                            }
                        }catch (IndexOutOfBoundsException e){
                            System.out.println("ID Not Found...");
                        }


                    } catch (NumberFormatException e) {
                        System.out.println("Booking ID must be a number!");
                    }

                } while (true);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    public void updateStatus(){
        List<Booking> bookings=bookingDAO.findAll().stream().filter(b->b.getStatus().equals(BookingStatus.PENDING)).sorted(Comparator.comparing(Booking::getStartTime)).collect(Collectors.toList());

        if(bookings.size()==0){
            System.out.println("Don't have any bookings !");
        }else{
            List<Room> rooms = roomDAO.findAll();
            List<Room> list = rooms.stream()
                    .filter(r -> bookings.stream().anyMatch(b -> b.getRoomId() == r.getId()))
                    .toList();

            if(list.size()==0){
                System.out.println("Don't have any schedule !");
            }else {
                System.out.println("╔════════════╦══════════╦══════════╦═══════════════╦══════════════════════╦══════════════════════╦═══════════════╗");
                System.out.println("║ BookingID  ║ RoomID   ║ UserID   ║ Location      ║ Start Time           ║ End Time             ║ Status        ║");
                System.out.println("╠════════════╬══════════╬══════════╬═══════════════╬══════════════════════╬══════════════════════╬═══════════════╣");
                for (int i = 0; i < bookings.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (list.get(j).getId() == bookings.get(i).getRoomId()) {


                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


                            System.out.printf(
                                    "║ %-10d ║ %-8s ║ %-8s ║ %-13s ║ %-20s ║ %-20s ║ %-13s ║%n",
                                    bookings.get(i).getId(),
                                    bookings.get(i).getRoomId(),
                                    bookings.get(i).getUserId(),
                                    list.get(j).getLocation(),
                                    bookings.get(i).getStartTime().format(formatter),
                                    bookings.get(i).getEndTime().format(formatter),
                                    bookings.get(i).getStatus()
                            );


                        }
                    }
                }
                System.out.println("╚════════════╩══════════╩══════════╩═══════════════╩══════════════════════╩══════════════════════╩═══════════════╝");
                int id;
                do {
                    try {
                        System.out.println("Enter Booking ID: ");
                        id = Integer.parseInt(sc.nextLine());
                        int flag = 0;
                        Booking approve = new Booking();
                        for (int i = 0; i < bookings.size(); i++) {
                            if (bookings.get(i).getId() == id) {
                                flag = 1;
                                approve = bookings.get(i);
                                break;
                            }
                        }
                        if (flag == 0) {
                            System.out.println("Not Found Any Booking ID...");
                        } else {
                            int choice=0;
                            do {
                                try
                                {
                                    System.out.println("1.APPROVE Booking ID...");
                                    System.out.println("2.REJECTED Booking ID...");
                                    System.out.println("Enter your choice");
                                     choice = Integer.parseInt(sc.nextLine());
                                    switch (choice) {
                                        case 1: approve.setStatus(BookingStatus.APPROVED); break;
                                        case 2: approve.setStatus(BookingStatus.REJECTED); break;
                                        default:
                                            System.out.println("Choice is invalid"); break;
                                    }
                                }catch (NumberFormatException e) {
                                    System.out.println("Booking ID must be a number!");
                                }
                            }while (choice<1||choice>2);

                            if(approve.getStatus().equals(BookingStatus.APPROVED)){
                                List<User> users = userDAO.findAll().stream().filter(u->u.getRole().equals(UserRole.SUPPORT)).filter(u->u.getUserStatus().equals(UserStatus.ACTIVE)).toList();
                                int supportStaffId=-1;
                                if(users.size()==0){
                                    System.out.println("Don't have any support staff!");
                                    supportStaffId = 0;
                                }else{
                                    System.out.println("╔════════════════════════════════════════════════════════════════════════════╗");
                                    System.out.println("║                        SUPPORT STAFF                                       ║");
                                    System.out.println("╠═════╦════════════════════╦════════════════════════════════════╦════════════╣");
                                    System.out.println("║ ID  ║ Username           ║ Email                              ║ Role       ║");
                                    System.out.println("╠═════╬════════════════════╬════════════════════════════════════╬════════════╣");
                                    users.forEach(user -> {
                                        System.out.printf("║ %-3d ║ %-18s ║ %-34s ║ %-10s ║%n",
                                                user.getId(),
                                                user.getUsername(),
                                                user.getEmail(),
                                                user.getRole());

                                    });
                                    System.out.println("╚═════╩════════════════════╩════════════════════════════════════╩════════════╝");
                                    do {
                                        try{
                                            System.out.println("Enter Support Staff ID: ");
                                            supportStaffId = Integer.parseInt(sc.nextLine());
                                            if (supportStaffId==0) {
                                                System.out.println("Don't have support for this booking...");
                                                break;
                                            }
                                            int flag2=0;
                                            for(int i=0;i<users.size();i++){
                                                if(users.get(i).getRole().equals(UserRole.SUPPORT)){
                                                    flag2=1;
                                                    break;
                                                }
                                            }
                                            if(flag2==0){
                                                System.out.println("Don't have support for this booking...");
                                            }
                                            else{
                                                break;
                                            }
                                        }catch (NumberFormatException e) {
                                            System.out.println("Support Staff ID must be a number!");
                                        }


                                    }while(true);
                                }
                                approve.setSupportStaffId(supportStaffId);
                                System.out.println("Approved Booking!");
                            }else{
                                System.out.println("Rejected Booking!");
                                EquipmentBooking equipmentBooking = new EquipmentBooking();
                                Equipment equipment = new Equipment();
                                EquipmentBookingDAO equipmentBookingDAO = new EquipmentBookingDAO();
                                EquipmentDAO equipmentDAO = new EquipmentDAO();


                            }




                            bookingDAO.update(approve);
                            System.out.println("This update will be notice to User have ID "+approve.getUserId() );
                            System.out.println("This update will be notice to SUPPORT STAFF have ID "+approve.getSupportStaffId() );
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Booking ID must be a number!");
                    }
                }while (true);

            }
        }


    }




    public void supportTask(){
        BookingDAO bookingDAO = new BookingDAO();

        List<Booking> bookings = bookingDAO.findAll().stream().filter(b->b.getSupportStaffId()==CurrentUser.getCurrentUser().getId()).collect(Collectors.toList());
        List<Room> rooms = roomDAO.findAll();
        List<Room> list = rooms.stream()
                .filter(r -> bookings.stream().anyMatch(b -> b.getRoomId() == r.getId()))
                .toList();

        if(list.size()==0){
            System.out.println("Don't have any schedule !");
        }else {
            System.out.println("╔════════════╦══════════╦══════════╦═══════════════╦══════════════════════╦══════════════════════╦═══════════════╦═══════════════╗");
            System.out.println("║ BookingID  ║ RoomID   ║ UserID   ║ Location      ║ Start Time           ║ End Time             ║ Status        ║ Room Status   ║");
            System.out.println("╠════════════╬══════════╬══════════╬═══════════════╬══════════════════════╬══════════════════════╬═══════════════╬═══════════════╣");
            for (int i = 0; i < bookings.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getId() == bookings.get(i).getRoomId()) {


                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


                        System.out.printf(
                                "║ %-10d ║ %-8s ║ %-8s ║ %-13s ║ %-20s ║ %-20s ║ %-13s ║ %-13s ║%n",
                                bookings.get(i).getId(),
                                bookings.get(i).getRoomId(),
                                bookings.get(i).getUserId(),
                                list.get(j).getLocation(),
                                bookings.get(i).getStartTime().format(formatter),
                                bookings.get(i).getEndTime().format(formatter),
                                bookings.get(i).getStatus(),
                                bookings.get(i).getRoomStatus()
                        );


                    }
                }
            }
            System.out.println("╚════════════╩══════════╩══════════╩═══════════════╩══════════════════════╩══════════════════════╩═══════════════╩═══════════════╝");
        }
    }
    public void updateRoomStatus(){
        BookingDAO bookingDAO = new BookingDAO();

        List<Booking> bookings = bookingDAO.findAll().stream().filter(b->b.getSupportStaffId()==CurrentUser.getCurrentUser().getId()).collect(Collectors.toList());
        List<Room> rooms = roomDAO.findAll();
        List<Room> list = rooms.stream()
                .filter(r -> bookings.stream().anyMatch(b -> b.getRoomId() == r.getId()))
                .toList();

        if(list.size()==0){
            System.out.println("Don't have any schedule !");
        }else {
            System.out.println("╔════════════╦══════════╦══════════╦═══════════════╦══════════════════════╦══════════════════════╦═══════════════╦═══════════════╗");
            System.out.println("║ BookingID  ║ RoomID   ║ UserID   ║ Location      ║ Start Time           ║ End Time             ║ Status        ║ Room Status   ║");
            System.out.println("╠════════════╬══════════╬══════════╬═══════════════╬══════════════════════╬══════════════════════╬═══════════════╬═══════════════╣");
            for (int i = 0; i < bookings.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).getId() == bookings.get(i).getRoomId()) {


                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


                        System.out.printf(
                                "║ %-10d ║ %-8s ║ %-8s ║ %-13s ║ %-20s ║ %-20s ║ %-13s ║ %-13s ║%n",
                                bookings.get(i).getId(),
                                bookings.get(i).getRoomId(),
                                bookings.get(i).getUserId(),
                                list.get(j).getLocation(),
                                bookings.get(i).getStartTime().format(formatter),
                                bookings.get(i).getEndTime().format(formatter),
                                bookings.get(i).getStatus(),
                                bookings.get(i).getRoomStatus()
                        );


                    }
                }
            }
            System.out.println("╚════════════╩══════════╩══════════╩═══════════════╩══════════════════════╩══════════════════════╩═══════════════╩═══════════════╝");
        }
        int bookingId;
        Booking booking=null;
        do {
            try{
                try{
                    System.out.println("Enter Booking ID: ");
                    bookingId = Integer.parseInt(sc.nextLine());
                    int flag=0;
                    for(int i=0;i<bookings.size();i++){
                        if(bookings.get(i).getId()==bookingId){
                            flag=1;
                            booking=bookings.get(i);
                            break;
                        }
                    }
                    if(flag==0){
                        System.out.println("Don't have booking !");
                    }else{
                        RoomStatus roomStatus=null;
                        do {
                            try{
                                System.out.println("ROOM STATUS: ");
                                System.out.println("1.PREPARING");
                                System.out.println("2.READY");
                                System.out.println("3.LACKING");
                                System.out.println("Enter your choice: ");
                                int choice = Integer.parseInt(sc.nextLine());
                                switch (choice) {
                                    case 1:
                                        roomStatus=RoomStatus.PREPARING;
                                        break;
                                    case 2:
                                        roomStatus=RoomStatus.READY;
                                        break;
                                    case 3:
                                        roomStatus=RoomStatus.LACKING;
                                        break;
                                    default:
                                        System.out.println("Invalid choice");
                                        break;

                                }
                            }catch(NumberFormatException e){
                                System.out.println("Invalid choice");
                            }

                        }while(roomStatus==null);
                        booking.setRoomStatus(roomStatus);
                        bookingDAO.update(booking);
                        System.out.println("This update will be notice to user have ID"+booking.getUserId());


                        break;
                    }
                }catch (IndexOutOfBoundsException e) {
                    System.out.println("Not Found!");
                }

            }catch (NumberFormatException e) {
                System.out.println("Booking ID must be a number!");
            }
        }while (true);
    }
}
