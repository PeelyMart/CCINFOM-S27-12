package DAO;

import Model.Order;
import Model.OrderItem;
import Model.OrderStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

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

    public static Order getOrderHeader(int headerId) {
        String headerQuery = "SELECT * FROM order_header WHERE order_id = ?";
        String itemsQuery = "SELECT * FROM order_item WHERE order_id = ?";

        try (Connection conn = DB.getConnection();
             PreparedStatement headerStmt = conn.prepareStatement(headerQuery);
             PreparedStatement itemsStmt = conn.prepareStatement(itemsQuery)) {

            // Fetch header
            headerStmt.setInt(1, headerId);
            ResultSet rsHeader = headerStmt.executeQuery();

            if (rsHeader.next()) {
                int orderId = rsHeader.getInt("order_id");
                int tableId = rsHeader.getInt("table_id");
                int staffId = rsHeader.getInt("staff_id");
                Timestamp ts = rsHeader.getTimestamp("order_time");
                BigDecimal totalCost = rsHeader.getBigDecimal("total_cost");
                String statusStr = rsHeader.getString("status");

                Order order = new Order(orderId, tableId, staffId, ts.toLocalDateTime(), totalCost,
                        OrderStatus.fromString(statusStr));

                // Fetch order items
                itemsStmt.setInt(1, orderId);
                ResultSet rsItems = itemsStmt.executeQuery();

                List<OrderItem> items = new java.util.ArrayList<>();
                while (rsItems.next()) {
                    int orderItemId = rsItems.getInt("order_item_id");
                    int menuId = rsItems.getInt("menu_id");
                    int quantity = rsItems.getInt("quantity");
                    double subtotal = rsItems.getDouble("subtotal");

                    boolean active = rsItems.getBoolean("is_active"); // reads 0/1 as boolean
                    String itemStatus = active ? "active" : "inactive"; // map to String for model

                    OrderItem item = new OrderItem(orderItemId, orderId, menuId, quantity, subtotal, itemStatus);
                    items.add(item);
                }

                // Only set the list if items exist
                if (!items.isEmpty()) {
                    order.setOrderItems(items);
                }

                return order;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}