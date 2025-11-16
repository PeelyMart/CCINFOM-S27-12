package DAO;

import Model.LoyaltyMember;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoyaltymemberDAO {
    /**
     * Increment loyalty points for a member by a given amount.
     *
     * @param customerId The ID of the loyalty member.
     * @param points     The number of points to add (must be positive).
     * @return true if the update succeeded, false otherwise.
     */
    public static boolean addPoints(int customerId, int points) {
        if (points <= 0) return false; // nothing to add

        String sql = "UPDATE loyalty_members SET points = points + ? WHERE customer_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, points);
            stmt.setInt(2, customerId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Decrement loyalty points for a member by a given amount.
     * Points will not go below 0.
     *
     * @param customerId The ID of the loyalty member.
     * @param points     The number of points to subtract (must be positive).
     * @return true if the update succeeded, false otherwise.
     */
    public boolean subtractPoints(int customerId, int points) {
        if (points <= 0) return false; // nothing to subtract

        String sql = "UPDATE loyalty_members SET points = GREATEST(points - ?, 0) WHERE customer_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, points);
            stmt.setInt(2, customerId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }



    public boolean addLoyaltyMember(LoyaltyMember member) {
        String sql = "INSERT INTO loyalty_members (first_name, last_name, contact_number, join_date, points, is_active) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Split name into first_name and last_name
            String[] nameParts = member.getName().trim().split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, member.getContact());
            stmt.setDate(4, Date.valueOf(member.getJoinDate()));
            stmt.setInt(5, member.getPoints());
            // Convert string status to boolean is_active
            boolean isActive = "active".equalsIgnoreCase(member.getStatus());
            stmt.setBoolean(6, isActive);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    member.setCustomerId(keys.getInt(1));
                }

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public LoyaltyMember getLoyaltyMemberById(int memberId) {
        String sql = "SELECT * FROM loyalty_members WHERE customer_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String name = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "").trim();
                String contact = rs.getString("contact_number");
                Date joinDate = rs.getDate("join_date");
                LocalDate joinDateLocal = joinDate != null ? joinDate.toLocalDate() : LocalDate.now();
                int points = rs.getInt("points");
                boolean isActive = rs.getBoolean("is_active");
                String status = isActive ? "active" : "inactive";

                return new LoyaltyMember(memberId, name, contact, joinDateLocal, points, status);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean updateLoyaltyMember(LoyaltyMember member) {
        String sql = "UPDATE loyalty_members SET first_name = ?, last_name = ?, contact_number = ?, join_date = ?, points = ?, is_active = ? WHERE customer_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Split name into first_name and last_name
            String[] nameParts = member.getName().trim().split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, member.getContact());
            stmt.setDate(4, Date.valueOf(member.getJoinDate()));
            stmt.setInt(5, member.getPoints());
            // Convert string status to boolean is_active
            boolean isActive = "active".equalsIgnoreCase(member.getStatus());
            stmt.setBoolean(6, isActive);
            stmt.setInt(7, member.getCustomerId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteLoyaltyMember(int memberId) {
        String sql = "DELETE FROM loyalty_members WHERE customer_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ArrayList<LoyaltyMember> getAllLoyaltyMembers() {
        ArrayList<LoyaltyMember> members = new ArrayList<>();
        String sql = "SELECT * FROM loyalty_members ORDER BY customer_id";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LoyaltyMember m = new LoyaltyMember();

                m.setCustomerId(rs.getInt("customer_id"));
                // Combine first_name and last_name into name field
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                m.setName((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "").trim());
                m.setContact(rs.getString("contact_number"));
                Date joinDate = rs.getDate("join_date");
                m.setJoinDate(joinDate != null ? joinDate.toLocalDate() : LocalDate.now());
                m.setPoints(rs.getInt("points"));
                // Convert boolean is_active to string status
                boolean isActive = rs.getBoolean("is_active");
                m.setStatus(isActive ? "active" : "inactive");

                members.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    /**
     * counts new members by month 
     * returns a map keyed by the first day of each month with counts.
     */
    public Map<LocalDate, Integer> countNewMembersByMonth(LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT YEAR(join_date) AS year, MONTH(join_date) AS month, COUNT(*) AS cnt " +
                 "FROM loyalty_members " +
                 "WHERE join_date BETWEEN ? AND ? " +
                 "GROUP BY year, month " +
                 "ORDER BY year, month";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int year = rs.getInt("year");
                int month = rs.getInt("month");
                int count = rs.getInt("cnt");
                
                LocalDate key = LocalDate.of(year, month, 1); // first day of month as key
                result.put(key, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * count new members by year
     * returns a map keyed by the first day of each year with counts.
     */
    public Map<LocalDate, Integer> countNewMembersByYear(LocalDate start, LocalDate end) {
        Map<LocalDate, Integer> result = new LinkedHashMap<>();
        String sql = "SELECT YEAR(join_date) AS year, COUNT(*) AS cnt " +
                 "FROM loyalty_members " +
                 "WHERE join_date BETWEEN ? AND ? " +
                 "GROUP BY year " +
                 "ORDER BY year";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(start));
            stmt.setDate(2, Date.valueOf(end));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int year = rs.getInt("year");
                int count = rs.getInt("cnt");
                
                LocalDate key = LocalDate.of(year, 1, 1); //first day of the year as key
                result.put(key, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean resetAutoIncrement(){
        String sql = "ALTER TABLE loyalty_members AUTO_INCREMENT = 1";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
