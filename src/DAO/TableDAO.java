package DAO;

import java.sql.*;
import java.util.ArrayList;
import Model.Table;

public class TableDAO {

    public boolean addTable(Table table) {
        String sql = "INSERT INTO tables (capacity, is_available) VALUES (?, ?)";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, table.getCapacity());
            stmt.setBoolean(2, table.getTableStatus());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    table.setTableId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Table getTableById(int tableId) {
        String sql = "SELECT * FROM tables WHERE table_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int capacity = rs.getInt("capacity");
                boolean tableStatus = rs.getBoolean("is_available");

                return new Table(tableId, capacity, tableStatus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateTable(Table table) {
        String sql = "UPDATE tables SET capacity = ?, is_available = ? WHERE table_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, table.getCapacity());
            stmt.setBoolean(2, table.getTableStatus());
            stmt.setInt(3, table.getTableId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteTable(int tableId) {
        String sql = "DELETE FROM tables WHERE table_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tableId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Table> getAllTables() {
        ArrayList<Table> tables = new ArrayList<>();
        String sql = "SELECT * FROM tables ORDER BY table_id";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Table t = new Table();

                t.setTableId(rs.getInt("table_id"));
                t.setCapacity(rs.getInt("capacity"));
                t.setTableStatus(rs.getBoolean("is_available"));

                tables.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }
}

