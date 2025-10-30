package Model;

import java.time.LocalDateTime;

public class Payment {
    // Private attributes
    private int paymentId;
    private int orderId;
    private double amountPaid;
    private String paymentMethod;
    private LocalDateTime paymentDate;
    private int staffId;
    private int customerId;
    private String customerName;

    // Constructor
    public Payment(int paymentId, int orderId, double amountPaid, String paymentMethod,
                   LocalDateTime paymentDate, int staffId, int customerId, String customerName) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.staffId = staffId;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    // Getters
    public int getPaymentId() {
        return paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public int getStaffId() {
        return staffId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    // Setters
    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
