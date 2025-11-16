package Controller;

import DAO.MenuItemDAO;
import DAO.OrderitemDAO;
import Model.MenuItem;

import java.lang.invoke.MethodHandle;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenuPerformanceController {
    private final MenuItemDAO MenuItemDAO = new MenuItemDAO();

    /** 
    * returns the performance (qty sold and total sales) of all menu items in a date range
    * Key: MenuItem, Value: { total quantity sold, total sales } 
    */

   public Map<MenuItem, double[]> getMenuPerformance(LocalDate start, LocalDate end) {
        Map<Integer, double[]> raw = OrderitemDAO.MenuSales(start, end);
        Map<MenuItem, double[]> perf = new LinkedHashMap<>();
        for (Map.Entry<Integer, double[]> entry : raw.entrySet()) {
            MenuItem item = MenuItemDAO.getMenuItemById(entry.getKey());
            
            if (item != null) {
                perf.put(item, entry.getValue());
            }
        }
        return perf;
   }

    public String generateMenuPerformanceReport(LocalDate start, LocalDate end) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("            MENU PERFORMANCE REPORT\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append(String.format("Period: %s to %s\n\n", start, end));
        
        Map<MenuItem, double[]> report = getMenuPerformance(start, end);

        if (!report.isEmpty()) {
            // Determine the max length of menu names
            int maxNameLength = "Item".length();
            for (MenuItem item : report.keySet()) {
                maxNameLength = Math.max(maxNameLength, item.getMenuName().length());
            }

            // Header
            sb.append(String.format("%-" + maxNameLength + "s | %15s | %15s\n", "Item", "Quantity Sold", "Total Sales ($)"));
            sb.append("─".repeat(maxNameLength + 33)).append("\n");

            double totalSales = 0.0;
            int totalQuantity = 0;
            
            // Data rows
            for (Map.Entry<MenuItem, double[]> entry : report.entrySet()) {
                MenuItem item = entry.getKey();
                double[] result = entry.getValue();
                totalQuantity += (int) result[0];
                totalSales += result[1];
                sb.append(String.format("%-" + maxNameLength + "s | %15.0f | $%14.2f\n",
                        item.getMenuName(), result[0], result[1]));
            }
            
            sb.append("─".repeat(maxNameLength + 33)).append("\n");
            sb.append(String.format("%-" + maxNameLength + "s | %15d | $%14.2f\n", "TOTAL", totalQuantity, totalSales));
            sb.append("═══════════════════════════════════════════════════\n");
        } else {
            sb.append("No sales data available for this period.\n");
        }

        return sb.toString();
    }
}