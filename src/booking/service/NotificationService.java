package booking.service;

import booking.DAO.BookingDAO;
import booking.DAO.RoomDAO;
import booking.entity.Booking;
import booking.entity.CurrentUser;
import booking.entity.Room;
import booking.enums.BookingStatus;
import booking.enums.RoomStatus;
import booking.enums.UserRole;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class NotificationService {
    BookingDAO bookingDAO = new BookingDAO();
    RoomDAO roomDAO = new RoomDAO();

    public void noticeBookingApprove(){
        int id=CurrentUser.getCurrentUser().getId();
        List<Booking> bookings= bookingDAO.findAll().stream().filter(b->b.getUserId()== id).filter(b->b.getStatus().equals(BookingStatus.APPROVED)).collect(Collectors.toList());
        List<Room> room2=roomDAO.findAll().stream().filter(r->bookings.stream().anyMatch(b->b.getRoomId()==r.getId())).collect(Collectors.toList());
        if(CurrentUser.getCurrentUser().getRole().equals(UserRole.EMPLOYEE)){
            System.out.println("╔════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                         USER NOTIFICATION                                              ║");
            System.out.println("╠════════════════════════════════════════════════════════════════════════════════════════════════════════╣");

            for(Booking booking:bookings){
                for(Room room:room2){
                    if(booking.getRoomId()==room.getId()){
                         String BOOKING_APPROVED_SHORT =
                                " \033[0;36mBookingID %d, room %d and location is %s will be start at %s is APPROVED\033[0;37m ";

                        String timeFormatted = booking.getStartTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        String msg = String.format(
                                BOOKING_APPROVED_SHORT,
                                booking.getId(),
                                room.getId(),
                                room.getLocation(),
                                timeFormatted
                        );



                        System.out.printf("%s%n", msg);

                    }
                }
            }
            System.out.println("══════════════════════════════════════════════════════════════════════════════════════════════════════════");

            for(Booking booking:bookings){
                for(Room room:room2){
                    if(booking.getRoomId()==room.getId()){
                        if(booking.getRoomStatus().equals(RoomStatus.READY)) {
                            String BOOKING_APPROVED_SHORT =
                                    " \033[0;36mBookingID %d, room %d and location is %s will be start at %s is READY TO START\033[0;37m ";

                            String timeFormatted = booking.getStartTime()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                            String msg = String.format(
                                    BOOKING_APPROVED_SHORT,
                                    booking.getId(),
                                    room.getId(),
                                    room.getLocation(),
                                    timeFormatted
                            );


                            System.out.printf("%s%n", msg);
                        }
                    }
                }
            }
            System.out.println("╚════════════════════════════════════════════════════════════════════════════════════════════════════════╝");

            System.out.println("Press enter to exit.");
            Scanner sc = new Scanner(System.in);
            sc.nextLine();
        }

    }
    public void noticeNewBookingToAdmin(){
        List<Booking> bookings=bookingDAO.findAll().stream().filter(b->b.getStatus().equals(BookingStatus.PENDING)).collect(Collectors.toList());
        List<Room> room2=roomDAO.findAll().stream().filter(r->bookings.stream().anyMatch(b->b.getRoomId()==r.getId())).collect(Collectors.toList());
        if(CurrentUser.getCurrentUser().getRole().equals(UserRole.ADMIN)){

            System.out.println("╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                          ADMIN NOTIFICATION                                               ║");
            System.out.println("╠═══════════════════════════════════════════════════════════════════════════════════════════════════════════╣");
            for(Booking booking:bookings){
                for(Room room:room2){
                    String BOOKING_APPROVED_FORMAT =
                            " \033[0;36mBookingID %d, room %d and location is %s will be start at %s was BOOKED by UserID %d\033[0;37m%n";

                    if(booking.getRoomId()==room.getId()){
                        String timeFormatted = booking.getStartTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        String fullMessage = String.format(
                                BOOKING_APPROVED_FORMAT,
                                booking.getId(),
                                room.getId(),
                                room.getLocation(),
                                timeFormatted,
                                booking.getUserId()
                        );

                        System.out.print(fullMessage);
                    }
                }
            }

            List<Booking> bookings2=bookingDAO.findAll().stream().filter(b->b.getStatus().equals(BookingStatus.CANCELLED)).collect(Collectors.toList());

            List<Room> room3=roomDAO.findAll().stream().filter(r->bookings2.stream().anyMatch(b->b.getRoomId()==r.getId())).collect(Collectors.toList());
            for(Booking booking:bookings2){
                for(Room room:room3){
                    String BOOKING_APPROVED_FORMAT =
                            " \033[0;36mBookingID %d, room %d and location is %s will be start at %s was CANCELLED by UserID %d\033[0;37m%n";

                    if(booking.getRoomId()==room.getId()){
                        String timeFormatted = booking.getStartTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        String fullMessage = String.format(
                                BOOKING_APPROVED_FORMAT,
                                booking.getId(),
                                room.getId(),
                                room.getLocation(),
                                timeFormatted,
                                booking.getUserId()
                        );

                        System.out.print(fullMessage);
                    }
                }
            }
            System.out.println("╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════╝");

            System.out.println("Press enter to exit.");
            Scanner sc = new Scanner(System.in);
            sc.nextLine();
        }
    }
    public void noticeSupportStaff(){
        List<Booking> bookings=bookingDAO.findAll().stream().filter(b->b.getSupportStaffId()==CurrentUser.getCurrentUser().getId())
                .filter(b->b.getStatus().equals(BookingStatus.APPROVED))
                .collect(Collectors.toList());
        List<Room> room2=roomDAO.findAll().stream()
                .filter(r->bookings.stream().filter(b->b.getRoomStatus().equals(RoomStatus.PREPARING))


                .anyMatch(b->b.getRoomId()==r.getId())).collect(Collectors.toList());
        if(CurrentUser.getCurrentUser().getRole().equals(UserRole.SUPPORT)){
            String BOOKING_APPROVED_FORMAT =
                    " \033[0;36mBookingID %d, room %d and location is %s will be start at %s has been ASSIGNED to YOU\033[0;37m%n";

            System.out.println("╔═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                                 SUPPORT NOTIFICATION                                                        ║");
            System.out.println("╠═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╣");
            for(Booking booking:bookings) {
                for (Room room : room2) {
                    if (booking.getRoomId() == room.getId()) {

                        String timeFormatted = booking.getStartTime()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        String fullMessage = String.format(
                                BOOKING_APPROVED_FORMAT,
                                booking.getId(),
                                room.getId(),
                                room.getLocation(),
                                timeFormatted

                        );


                        System.out.print(fullMessage);

                    }
                }
            }
            System.out.println("╚═════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════╝");
            System.out.println("Press enter to exit.");
            Scanner sc = new Scanner(System.in);
            sc.nextLine();

        }
    }
}

