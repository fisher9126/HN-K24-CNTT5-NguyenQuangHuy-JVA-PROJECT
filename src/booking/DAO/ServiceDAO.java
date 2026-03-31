package booking.DAO;

import booking.entity.Service;
import booking.utils.DatabaseConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public void insert(Service service) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call proc_add_service(?,?,?)}")) {

            cs.setString(1, service.getName());
            cs.setDouble(2, service.getPrice());
            cs.setString(3, service.getDescription());

            cs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Service service) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call proc_update_service(?,?,?,?)}")) {

            cs.setInt(1, service.getId());
            cs.setString(2, service.getName());
            cs.setDouble(3, service.getPrice());
            cs.setString(4, service.getDescription());

            cs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call proc_delete_service(?)}")) {

            cs.setInt(1, id);
            cs.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Service findById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call proc_find_service_by_id(?)}")) {

            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                return new Service(
                        rs.getInt("service_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Service> findAll() {
        List<Service> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{call proc_find_all_service()}")) {

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                list.add(new Service(
                        rs.getInt("service_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}