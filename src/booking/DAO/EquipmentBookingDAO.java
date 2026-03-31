package booking.DAO;

import booking.entity.EquipmentBooking;
import booking.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentBookingDAO {

    public boolean insert(EquipmentBooking eb) {
        String sql = "{CALL proc_add_equipment_booking(?,?,?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, eb.getBookingId());
            cs.setInt(2, eb.getEquipmentId());
            cs.setInt(3, eb.getQuantity());

            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting EquipmentBooking: " + e.getMessage());
        }
        return false;
    }

    public EquipmentBooking findById(int id) {
        String sql = "{CALL proc_find_equipment_booking_by_id(?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error finding EquipmentBooking by ID: " + e.getMessage());
        }
        return null;
    }


    public List<EquipmentBooking> findAll() {
        List<EquipmentBooking> list = new ArrayList<>();
        String sql = "{CALL proc_find_all_equipment_booking()}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all EquipmentBooking: " + e.getMessage());
        }

        return list;
    }

    public boolean update(EquipmentBooking eb) {
        String sql = "{CALL proc_update_equipment_booking(?,?,?,?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, eb.getId());
            cs.setInt(2, eb.getBookingId());
            cs.setInt(3, eb.getEquipmentId());
            cs.setInt(4, eb.getQuantity());

            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating EquipmentBooking: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "{CALL proc_delete_equipment_booking(?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting EquipmentBooking: " + e.getMessage());
        }
        return false;
    }


    private EquipmentBooking map(ResultSet rs) throws SQLException {
        EquipmentBooking eb = new EquipmentBooking();

        eb.setId(rs.getInt("id"));
        eb.setBookingId(rs.getInt("booking_id"));
        eb.setEquipmentId(rs.getInt("equipment_id"));
        eb.setQuantity(rs.getInt("quantity"));

        return eb;
    }


}