package backendUtilities;

import DAO.DB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
/*
---Testing purposes only---
 BE VERY CAREFUL RUNNING THIS PLEASE,
 THIS WILL DELETE EVERYTHING!
 FROM ORDER HEADERS AND ORDER ITEMS
 */
public class clearOrders {
    public static void main(String[] args) {
        String[] tables = {"order_item", "order_header"};
        try (Connection conn = DB.getConnection();
             Statement stmt = conn.createStatement()) {

            for(String t : tables){
                stmt.execute("DELETE FROM " + t);
                stmt.execute("ALTER TABLE " + t + " AUTO_INCREMENT = 1;");
            }

            System.out.println("✅ All tables cleared.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
