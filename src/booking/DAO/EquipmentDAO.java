package booking.DAO;

import booking.entity.Equipment;
import booking.enums.EquipmentStatus;
import booking.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {


    public void insert(Equipment eq) {
        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL proc_add_equipment(?,?,?,?,?)}")) {

            cs.setString(1, eq.getName());
            cs.setInt(2, eq.getTotalQuantity());
            cs.setInt(3, eq.getAvailableQuantity());
            cs.setString(4, eq.getStatus().name());
            cs.setString(5, eq.getDescription());

            cs.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Equipment findById(int id) {
        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL proc_find_equipment_by_id(?)}")) {

            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) return mapEquipment(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Equipment> findAll() {
        List<Equipment> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL proc_find_all_equipment()}")) {

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                list.add(mapEquipment(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public void update(Equipment eq) {
        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL proc_update_equipment(?,?,?,?,?,?)}")) {

            cs.setInt(1, eq.getId());
            cs.setString(2, eq.getName());
            cs.setInt(3, eq.getTotalQuantity());
            cs.setInt(4, eq.getAvailableQuantity());
            cs.setString(5, eq.getStatus().name());
            cs.setString(6, eq.getDescription());

            cs.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void delete(int id) {
        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL proc_delete_equipment(?)}")) {

            cs.setInt(1, id);
            cs.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Equipment mapEquipment(ResultSet rs) throws SQLException {
        Equipment eq = new Equipment();
        eq.setId(rs.getInt("equipment_id"));
        eq.setName(rs.getString("name"));
        eq.setTotalQuantity(rs.getInt("total_quantity"));
        eq.setAvailableQuantity(rs.getInt("available_quantity"));
        eq.setStatus(EquipmentStatus.valueOf(rs.getString("status")));
        eq.setDescription(rs.getString("description"));
        return eq;
    }
}