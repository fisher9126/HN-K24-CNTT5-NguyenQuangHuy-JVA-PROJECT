package booking.DAO;

import booking.entity.Room;
import booking.enums.RoomType;
import booking.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {


    public void insert(Room room) {
        try (Connection con = DatabaseConnection.getConnection();
             CallableStatement cs = con.prepareCall("{CALL proc_add_room(?,?,?,?,?,?)}")) {

            cs.setString(1, room.getName());
            cs.setString(2, room.getType().name());
            cs.setInt(3, room.getCapacity());
            cs.setString(4, room.getLocation());
            cs.setDouble(5, room.getHourlyPrice());
            cs.setString(6, room.getDescription());

            cs.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Room findById(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_find_room_by_id(?)}")) {

            cs.setInt(1, id);

            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                return mapRoom(rs);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<Room> findAll() {
        List<Room> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_find_all_rooms()}");
             ResultSet rs = cs.executeQuery()) {

            while (rs.next()) {
                list.add(mapRoom(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    public void update(Room room) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_update_room(?,?,?,?,?,?,?)}")) {

            cs.setInt(1, room.getId());
            cs.setString(2, room.getName());
            cs.setString(3, room.getType().name());
            cs.setInt(4, room.getCapacity());
            cs.setString(5, room.getLocation());
            cs.setDouble(6, room.getHourlyPrice());
            cs.setString(7, room.getDescription());

            cs.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void delete(int id) {
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL proc_delete_room(?)}")) {

            cs.setInt(1, id);

            cs.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Room mapRoom(ResultSet rs) throws SQLException {
        Room room = new Room();

        room.setId(rs.getInt("room_id"));
        room.setName(rs.getString("room_name"));
        room.setCapacity(rs.getInt("capacity"));
        room.setLocation(rs.getString("location"));
        room.setHourlyPrice(rs.getDouble("hourly_price"));
        room.setDescription(rs.getString("description"));

        room.setType(RoomType.valueOf(rs.getString("type")));

        return room;
    }
}