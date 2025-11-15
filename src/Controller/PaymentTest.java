import Model.Payment;
import Model.Payment.PaymentMethod;
import DAO.PaymentDAO;

import java.time.LocalDateTime;
import java.util.Map;


public class PaymentTest {
    public static void main(String[] args) {
        // Create and insert a payment
        Payment p = new Payment();
        p.setOrderId(123);
        p.setAmountPaid(50.00);
        p.setPaymentMethod(Payment.PaymentMethod.CASH);
        p.setPaymentDate(LocalDateTime.now());
        p.setStaffId(1);
        p.setLoyalCustomerId(55);
        p.setUnknownCustomerName(null);
        p.setActive(true);
        
        PaymentDAO dao = new PaymentDAO();
        boolean inserted = dao.recordPayment(p);
        System.out.println("Inserted: " + inserted);

        // Read payment back
        Payment loaded = dao.getPaymentById(p.getTransactionId());
        System.out.println("Loaded: " + loaded.getAmountPaid());

        // Generate report
        LocalDateTime start = SalesReportController.getDayStart(2025, 11, 15);
        LocalDateTime end = SalesReportController.getDayEnd(2025, 11, 15);
        Map<Payment.PaymentMethod, double[]> report = SalesReportController.getSalesByPaymentMethod(start, end);
        report.forEach((method, arr) -> 
            System.out.printf("%s: Total=%.2f Avg=%.2f Count=%.0f\n", method, arr[0], arr[1], arr[2]));
    }
}
