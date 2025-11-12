package DAO;

import Model.OrderStatus;

import java.math.BigDecimal;
import java.sql.*;

public class OrderDB {


    /*
        @return - id of newly generated record
        @return - -1 if failed row insert
     */
    public static int newOrder(int tableId, int staffId) {
        String sql = "INSERT INTO order_header (table_id, staff_id, total_cost, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, tableId);
            stmt.setInt(2, staffId);
            stmt.setBigDecimal(3, BigDecimal.valueOf(0.00));                 // total cost (BigDecimal)
            stmt.setString(4, OrderStatus.OPEN.toSqlString());          // enum -> SQL string

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int orderId = rs.getInt(1);              // get auto-generated order_id
                    System.out.println("Order opened with ID: " + orderId);
                    return orderId;
                }
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void getOrderHeader(int headerId){
        String query =  "SELECT FROM order_header WHERE id = ";
    }
}