package Controller;

import DAO.LoyaltymemberDAO;
import DAO.PaymentDAO;
import Model.Order;
import Model.Payment;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class PaymentControl {

    public static int initiatePayment(Order currentOrder, Integer customerId){
        if(currentOrder.getTotalCost().equals(BigDecimal.valueOf(0.00))) {
            return -1;
        }

        //creating the record for the payment
        Payment currPay = new Payment();
        currPay.setOrderId(currentOrder.getOrderId());
        currPay.setAmountPaid(currentOrder.getTotalCost().doubleValue());
        
        // Ensure payment method is set (default to DEBIT if null)
        Payment.PaymentMethod paymentMethod = currentOrder.getPaymentMethod();
        if (paymentMethod == null) {
            paymentMethod = Payment.PaymentMethod.DEBIT; // Default payment method
        }
        currPay.setPaymentMethod(paymentMethod);
        
        currPay.setPaymentDate(LocalDateTime.now());
        currPay.setStaffId(currentOrder.getStaffId());
        
        // Only set loyalCustomerId if customerId is not null and > 0
        // Pass null to Payment object if no valid customer ID (for non-members)
        if (customerId != null && customerId > 0) {
            currPay.setLoyalCustomerId(customerId);
        } else {
            currPay.setLoyalCustomerId(null);
        }
        
        currPay.setActive(true);
        
        PaymentDAO paymentDAO = new PaymentDAO();
        boolean success = paymentDAO.recordPayment(currPay);

        if (!success) {
            return 0;
        }

        // Only add loyalty points if there's a valid customer ID
        if (customerId != null && customerId > 0) {
            int points = calculateLoyaltyPoints(currentOrder.getTotalCost());
            LoyaltymemberDAO.addPoints(customerId, points);
        }
        return 1;
    }

    public static int calculateLoyaltyPoints(BigDecimal totalSpent){
        if(totalSpent == null || totalSpent.compareTo(BigDecimal.ZERO) <= 0){
            return 0;
        }
        return totalSpent.divide(BigDecimal.valueOf(1000), 0, RoundingMode.DOWN).intValue();
    }

}
