package booking.DAO;

import booking.entity.User;
import booking.enums.UserRole;
import booking.enums.UserStatus;
import booking.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {


    public void insert(User user) {
        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL proc_add_user(?,?,?,?,?,?,?,?)}")) {

            cs.setString(1, user.getUsername());
            cs.setString(2, user.getPassword());
            cs.setString(3, user.getFullName());
            cs.setString(4, user.getEmail());
            cs.setString(5, user.getPhone());
            cs.setString(6, user.getDepartment());
            cs.setString(7, user.getRole().name());
            cs.setString(8,user.getUserStatus().name());

            cs.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public User findByUsername(String username) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_find_by_username(?)}")) {

            cs.setString(1, username);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public User findById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_find_by_id(?)}")) {

            cs.setInt(1, id);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<User> findAll() {
        List<User> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_find_all_users()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                list.add(mapUser(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public void update(User user) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_update_user(?,?,?,?,?,?,?)}")) {

            cs.setInt(1, user.getId());
            cs.setString(2, user.getFullName());
            cs.setString(3, user.getEmail());
            cs.setString(4, user.getPhone());
            cs.setString(5, user.getDepartment());
            cs.setString(6, user.getRole().name());
            cs.setString(7, user.getUserStatus().name());


            cs.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_delete_user(?)}")) {

            cs.setInt(1, id);

            cs.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();

        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setDepartment(rs.getString("department"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setUserStatus(UserStatus.valueOf(rs.getString("status")));
        return user;
    }
}