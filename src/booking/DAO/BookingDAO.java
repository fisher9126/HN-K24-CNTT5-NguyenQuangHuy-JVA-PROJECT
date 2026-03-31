package booking.DAO;

import booking.entity.Booking;
import booking.enums.BookingStatus;
import booking.enums.RoomStatus;
import booking.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {


    public int insert(Booking booking) {
        String sql = "{CALL proc_add_booking(?,?,?,?,?,?)}";
        String sql2 = "SELECT * FROM Bookings WHERE booking_id = LAST_INSERT_ID()";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, booking.getUserId());
            cs.setInt(2, booking.getRoomId());

            if (booking.getSupportStaffId() == 0) {
                cs.setNull(3, Types.INTEGER);
            } else {
                cs.setInt(3, booking.getSupportStaffId());
            }

            cs.setTimestamp(4, Timestamp.valueOf(booking.getStartTime()));
            cs.setTimestamp(5, Timestamp.valueOf(booking.getEndTime()));
            cs.setDouble(6, booking.getTotalPrice());

            cs.execute();

            // 🔥 lấy lại object vừa insert
            try (PreparedStatement ps = conn.prepareStatement(sql2);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public Booking findById(int id) {
        String sql = "{CALL proc_find_booking_by_id(?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                return mapBooking(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        String sql = "{CALL proc_find_all_bookings()}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                list.add(mapBooking(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    public void update(Booking booking) {
        String sql = "{CALL proc_update_booking(?,?,?,?,?,?,?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, booking.getId());

            if (booking.getSupportStaffId() == 0) {
                cs.setNull(2, Types.INTEGER);
            } else {
                cs.setInt(2, booking.getSupportStaffId());
            }

            cs.setTimestamp(3, Timestamp.valueOf(booking.getStartTime()));
            cs.setTimestamp(4, Timestamp.valueOf(booking.getEndTime()));
            cs.setDouble(5, booking.getTotalPrice());
            cs.setString(6, booking.getStatus().name());
            cs.setString(7, booking.getRoomStatus().name());

            cs.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(int id) {
        String sql = "{CALL proc_delete_booking(?)}";

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ================= MAP =================
    private Booking mapBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking();

        booking.setId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setRoomId(rs.getInt("room_id"));

        int supportId = rs.getInt("support_staff_id");
        if (rs.wasNull()) {
            booking.setSupportStaffId(0);
        } else {
            booking.setSupportStaffId(supportId);
        }

        Timestamp start = rs.getTimestamp("start_time");
        if (start != null) {
            booking.setStartTime(start.toLocalDateTime());
        }

        Timestamp end = rs.getTimestamp("end_time");
        if (end != null) {
            booking.setEndTime(end.toLocalDateTime());
        }

        booking.setTotalPrice(rs.getDouble("total_price"));


        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setRoomStatus(RoomStatus.valueOf(rs.getString("room_status")));

        return booking;
    }
}