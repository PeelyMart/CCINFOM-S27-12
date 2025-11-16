package Controller;

import DAO.OrderDB;
import DAO.PaymentDAO;
import DAO.StaffDB;
import DAO.StaffTrackDAO;
import Model.Order;
import Model.Payment;
import Model.Staff;
import Model.StaffTracker;
import java.util.List;

public class AuditReportController {

    public static String generateAuditReportForStaff(int staffId) {
        StringBuilder sb = new StringBuilder();

        Staff staff = StaffDB.findById(staffId);
        if (staff == null) {
            return "No staff member found with the ID: " + staffId;
        }

        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("              STAFF AUDIT REPORT\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append(String.format("Staff: %s %s (ID: %d)\n", 
            staff.getFirstName(), staff.getLastName(), staff.getStaffId()));
        sb.append("═══════════════════════════════════════════════════\n\n");

        // Sessions
        List<StaffTracker> sessions = StaffTrackDAO.getSessionsByStaffId(staffId);
        sb.append("╔═══════════════════════════════════════════════════╗\n");
        sb.append("║              SESSIONS                             ║\n");
        sb.append("╚═══════════════════════════════════════════════════╝\n");
        sb.append(String.format("%-10s | %-20s | %-20s | %-10s\n",
                "Staff ID", "Login Time", "Logout Time", "Minutes"));
        sb.append("───────────────────────────────────────────────────────\n");

        if (sessions.isEmpty()) {
            sb.append("No sessions found.\n");
        } else {
            for (StaffTracker tracker : sessions) {
                sb.append(String.format("%-10d | %-20s | %-20s | %10d\n",
                        tracker.getStaffId(),
                        tracker.getTimeIn() != null ? tracker.getTimeIn().toString() : "N/A",
                        tracker.getTimeOut() != null ? tracker.getTimeOut().toString() : "N/A",
                        tracker.getSessionMinutes()));
            }
        }

        // Orders
        List<Order> orders = OrderDB.getOrdersByStaffId(staffId);
        sb.append("\n╔═══════════════════════════════════════════════════╗\n");
        sb.append("║             ORDERS PROCESSED                       ║\n");
        sb.append("╚═══════════════════════════════════════════════════╝\n");
        sb.append(String.format("%-10s | %-25s | %12s\n", "Order ID", "Order Time", "Total"));
        sb.append("───────────────────────────────────────────────────────\n");

        double ordersTotal = 0.0;
        if (orders.isEmpty()) {
            sb.append("No orders found.\n");
        } else {
            for (Order order : orders) {
                double orderTotal = order.getTotalCost() != null ? order.getTotalCost().doubleValue() : 0.0;
                ordersTotal += orderTotal;
                sb.append(String.format("%-10d | %-25s | $%11.2f\n",
                        order.getOrderId(),
                        order.getOrderTime() != null ? order.getOrderTime().toString() : "N/A",
                        orderTotal));
            }
            sb.append("───────────────────────────────────────────────────────\n");
            sb.append(String.format("%-10s | %-25s | $%11.2f\n", "TOTAL", "", ordersTotal));
        }

        // Payments
        List<Payment> payments = PaymentDAO.getPaymentsByStaffId(staffId);
        sb.append("\n╔═══════════════════════════════════════════════════╗\n");
        sb.append("║             PAYMENTS PROCESSED                     ║\n");
        sb.append("╚═══════════════════════════════════════════════════╝\n");
        sb.append(String.format("%-10s | %-12s | %-20s | %12s\n", "Payment ID", "Amount", "Payment Time", "Method"));
        sb.append("───────────────────────────────────────────────────────\n");

        double paymentsTotal = 0.0;
        if (payments.isEmpty()) {
            sb.append("No payments found.\n");
        } else {
            for (Payment payment : payments) {
                double paymentAmount = payment.getAmountPaid();
                paymentsTotal += paymentAmount;
                sb.append(String.format("%-10d | $%11.2f | %-20s | %-12s\n",
                        payment.getTransactionId(),
                        paymentAmount,
                        payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : "N/A",
                        payment.getPaymentMethod() != null ? payment.getPaymentMethod().toString() : "N/A"));
            }
            sb.append("───────────────────────────────────────────────────────\n");
            sb.append(String.format("%-10s | $%11.2f | %-20s | %-12s\n", "TOTAL", paymentsTotal, "", ""));
        }
        
        sb.append("\n═══════════════════════════════════════════════════\n");
        sb.append(String.format("Total Orders Value:  $%.2f\n", ordersTotal));
        sb.append(String.format("Total Payments:      $%.2f\n", paymentsTotal));
        sb.append("═══════════════════════════════════════════════════\n");

        return sb.toString();
    }
}
