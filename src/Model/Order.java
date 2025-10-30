package Model;

import java.time.LocalDateTime;

public class Order {
    // Private attributes
    private int orderId;
    private int tableId;
    private int staffId;
    private int memberId;
    private LocalDateTime orderDatetime;
    private String orderStatus;
    private List<OrderItem> orderItems;

    // Constructor
    public Order(int orderId, int tableId, int staffId, int memberId,
                 LocalDateTime orderDatetime, String orderStatus, List<OrderItem> orderItems) {
        this.orderId = orderId;
        this.tableId = tableId;
        this.staffId = staffId;
        this.memberId = memberId;
        this.orderDatetime = orderDatetime;
        this.orderStatus = orderStatus;
        this.orderItems = orderItems;
    }

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public int getTableId() {
        return tableId;
    }

    public int getStaffId() {
        return staffId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDateTime getOrderDatetime() {
        return orderDatetime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setOrderDatetime(LocalDateTime orderDatetime) {
        this.orderDatetime = orderDatetime;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
