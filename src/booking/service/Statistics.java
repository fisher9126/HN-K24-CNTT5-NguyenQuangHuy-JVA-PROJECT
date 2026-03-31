package booking.service;

import booking.DAO.BookingDAO;
import booking.DAO.RoomDAO;
import booking.entity.Booking;
import booking.entity.Room;
import booking.enums.BookingStatus;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Statistics {
    public void RoomStatistics() {
        BookingDAO bookingDAO=new BookingDAO();
        RoomDAO roomDAO=new RoomDAO();
        List<Booking> bookings=bookingDAO.findAll().stream().filter(b->b.getStatus().equals(BookingStatus.APPROVED)).collect(Collectors.toList());
        List<Room> rooms=roomDAO.findAll().stream().filter(r->bookings.stream().anyMatch(b->b.getRoomId()==r.getId())).collect(Collectors.toList());

        Map<Integer, Double> roomUsageHours = new HashMap<>();

        for (Booking b : bookings) {
            long minutes = Duration.between(
                    b.getStartTime(),
                    b.getEndTime()
            ).toMinutes();
            double hours = minutes / 60.0;

            int roomId = b.getRoomId();
            double oldHours = roomUsageHours.getOrDefault(roomId, 0.0);
            roomUsageHours.put(roomId, oldHours + hours);
        }


        System.out.println("╔════╦════════════╦════════════════════╗");
        System.out.println("║ ID ║ Room Name  ║ Total Used (hours) ║");
        System.out.println("╠════╬════════════╬════════════════════╣");

        for (Room room : rooms) {
            double hours = roomUsageHours.getOrDefault(room.getId(), 0.0);
            System.out.printf("║ %-2d ║ %-10s ║ %-18.2f ║%n",
                    room.getId(),
                    room.getName(),
                    hours
            );
        }

        System.out.println("╚════╩════════════╩════════════════════╝");

        Optional<Map.Entry<Integer, Double>> maxEntry = roomUsageHours.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        Optional<Map.Entry<Integer, Double>> minEntry = roomUsageHours.entrySet().stream()
                .min(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            int roomIdMax = maxEntry.get().getKey();
            double hoursMax = maxEntry.get().getValue();
            Room roomMax = rooms.stream()
                    .filter(r -> r.getId() == roomIdMax)
                    .findFirst()
                    .orElse(null);

            System.out.println("Room used the most:");
            System.out.println(" - Room ID: " + roomIdMax
                    + ", name: " + (roomMax != null ? roomMax.getName() : "N/A")
                    + ", total hours: " + String.format("%.2f", hoursMax));
        }

        if (minEntry.isPresent()) {
            int roomIdMin = minEntry.get().getKey();
            double hoursMin = minEntry.get().getValue();
            Room roomMin = rooms.stream()
                    .filter(r -> r.getId() == roomIdMin)
                    .findFirst()
                    .orElse(null);

            System.out.println("Room used the least:");
            System.out.println(" - Room ID: " + roomIdMin
                    + ", name: " + (roomMin != null ? roomMin.getName() : "N/A")
                    + ", total hours: " + String.format("%.2f", hoursMin));
        }


    }
}
