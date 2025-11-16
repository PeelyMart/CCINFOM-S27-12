package DAO;
import Model.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDB{


    public static Staff findById(int id) {
        String query = "SELECT *  FROM staff WHERE staff_id = ?";

        try (Connection conn = DB.getConnection(); //joint try parameter these instances of a resource is closed once done with try block
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);//replaces the '?' with the id
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) { //loads it into a staff class
                return new Staff(
                        rs.getInt("staff_id"),
                        rs.getInt("staff_pin"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("contact_number")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int findAmountOfStaff() {
        String query = "SELECT COUNT(*) AS total FROM staff";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<Staff> returnAllStaff(){
        String query = "SELECT * from staff";
        List<Staff> staffs = new ArrayList<>();
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while(rs.next()){
                Staff currStaff = new Staff(
                        rs.getInt("staff_id"),
                        rs.getInt("staff_pin"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("contact_number"));
                staffs.add(currStaff);
            }
            return staffs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return staffs;
    }

    public static boolean addStaff(Staff staff) {
        String query = "INSERT INTO staff (first_name, last_name, contact_number, staff_pin) VALUES (?, ?, ?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, staff.getFirstName());
            stmt.setString(2, staff.getLastName());
            stmt.setString(3, staff.getContactNumber());
            stmt.setInt(4, staff.getStaffPin());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateStaff(Staff staff) {
        String query = "UPDATE staff SET first_name = ?, last_name = ?, contact_number = ?, staff_pin = ? WHERE staff_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, staff.getFirstName());
            stmt.setString(2, staff.getLastName());
            stmt.setString(3, staff.getContactNumber());
            stmt.setInt(4, staff.getStaffPin());
            stmt.setInt(5, staff.getStaffId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteStaff(int staffId) {
        String query = "DELETE FROM staff WHERE staff_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, staffId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}




