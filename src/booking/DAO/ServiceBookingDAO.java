package booking.DAO;

import booking.entity.ServiceBooking;
import booking.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceBookingDAO {

    // =========================
    // INSERT
    // =========================
    public void insert(ServiceBooking sb) {
        String sql = "{CALL proc_add_booking_service(?,?,?,?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, sb.getBookingId());
            cs.setInt(2, sb.getServiceId());
            cs.setInt(3, sb.getQuantity());
            cs.setDouble(4, sb.getUnitPrice());
            System.out.println("add booking success");
             cs.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Insert ServiceBooking error: " + e.getMessage());
        }

    }

    // =========================
    // FIND BY ID
    // =========================
    public ServiceBooking findById(int id) {
        String sql = "{CALL proc_find_booking_service_by_id(?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);

            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("FindById ServiceBooking error: " + e.getMessage());
        }
        return null;
    }

    // =========================
    // FIND ALL
    // =========================
    public List<ServiceBooking> findAll() {
        List<ServiceBooking> list = new ArrayList<>();
        String sql = "{CALL proc_find_all_booking_service()}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql);
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            System.err.println("FindAll ServiceBooking error: " + e.getMessage());
        }

        return list;
    }

    // =========================
    // UPDATE
    // =========================
    public boolean update(ServiceBooking sb) {
        String sql = "{CALL proc_update_booking_service(?,?,?,?,?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, sb.getId());
            cs.setInt(2, sb.getBookingId());
            cs.setInt(3, sb.getServiceId());
            cs.setInt(4, sb.getQuantity());
            cs.setDouble(5, sb.getUnitPrice());

            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Update ServiceBooking error: " + e.getMessage());
        }
        return false;
    }

    // =========================
    // DELETE
    // =========================
    public boolean delete(int id) {
        String sql = "{CALL proc_delete_booking_service(?)}";

        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {

            cs.setInt(1, id);
            return cs.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Delete ServiceBooking error: " + e.getMessage());
        }
        return false;
    }

    // =========================
    // MAPPER
    // =========================
    private ServiceBooking map(ResultSet rs) throws SQLException {
        ServiceBooking sb = new ServiceBooking();

        sb.setId(rs.getInt("id"));
        sb.setBookingId(rs.getInt("booking_id"));
        sb.setServiceId(rs.getInt("service_id"));
        sb.setQuantity(rs.getInt("quantity"));
        sb.setUnitPrice(rs.getDouble("unit_price"));

        return sb;
    }
}