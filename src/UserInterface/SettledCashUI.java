package UserInterface;

import Controller.PaymentControl;
import Controller.UserService;
import DAO.OrderDB;
import Model.Order;
import Model.OrderStatus;
import Model.Payment;
import Model.Staff;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.text.NumberFormat;

public class SettledCashUI {

    @FXML
    private Button cancelButton, paidButton, backButton;

    @FXML
    private TextField amountPaidField;

    @FXML
    private TextArea changeDueArea;

    @FXML
    private TextArea totalDueArea;

    private Order currentOrder;
    private BigDecimal totalDue;

    @FXML
    public void setData(Object data) {
        if (data instanceof Order) {
            currentOrder = (Order) data;
            loadOrderData();
        }
    }

    private void loadOrderData() {
        if (currentOrder == null) {
            SceneNavigator.showError("No order found.");
            return;
        }

        // Reload order to get latest data
        Order refreshedOrder = OrderDB.getWholeOrder(currentOrder.getOrderId());
        if (refreshedOrder != null) {
            currentOrder = refreshedOrder;
            // Recalculate total based on active items
            Controller.OrderController.updateTotal(currentOrder);
            // Update total in database
            OrderDB.updateOrderTotal(currentOrder.getOrderId(), currentOrder.getTotalCost());
        }

        // Get total due - only count completed items (status = false)
        totalDue = calculateTotalFromCompletedItems();
        
        if (totalDueArea != null) {
            totalDueArea.setText(String.format("$%.2f", totalDue));
        }

        // Set up listener for amount paid field to calculate change
        if (amountPaidField != null) {
            amountPaidField.textProperty().addListener((observable, oldValue, newValue) -> {
                calculateChange();
            });
        }
    }

    private void calculateChange() {
        if (amountPaidField == null || changeDueArea == null || totalDue == null) {
            return;
        }

        String amountPaidStr = amountPaidField.getText().trim();
        if (amountPaidStr.isEmpty()) {
            changeDueArea.setText("$0.00");
            return;
        }

        try {
            BigDecimal amountPaid = new BigDecimal(amountPaidStr);
            BigDecimal change = amountPaid.subtract(totalDue);

            if (change.compareTo(BigDecimal.ZERO) < 0) {
                changeDueArea.setText(String.format("$%.2f (Insufficient)", change.abs()));
                changeDueArea.setStyle("-fx-text-fill: #ff6b6b;");
            } else {
                changeDueArea.setText(String.format("$%.2f", change));
                changeDueArea.setStyle("-fx-text-fill: #51cf66;");
            }
        } catch (NumberFormatException e) {
            changeDueArea.setText("Invalid amount");
            changeDueArea.setStyle("-fx-text-fill: #ff6b6b;");
        }
    }

    @FXML
    private void initialize() {
        // PAID button → complete payment
        if (paidButton != null) {
            paidButton.setOnAction(e -> handlePayment());
        }

        // CANCEL button → cancel payment
        if (cancelButton != null) {
            cancelButton.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cancel Payment");
                alert.setHeaderText("Are you sure you want to cancel?");
                alert.setContentText("This will return you to the order screen without processing payment.");

                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                alert.getButtonTypes().setAll(yesButton, noButton);

                ButtonType result = alert.showAndWait().orElse(noButton);
                if (result == yesButton) {
                    javafx.stage.Stage stage = (javafx.stage.Stage) cancelButton.getScene().getWindow();
                    SceneNavigator.switchNoButton(stage, "/Resources/Transactions/paymentMethod.fxml", currentOrder);
                }
            });
        }

        // BACK button → return to payment method selection
        if (backButton != null) {
            backButton.setOnAction(e -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/paymentMethod.fxml", currentOrder);
            });
        }
    }

    private BigDecimal calculateTotalFromCompletedItems() {
        if (currentOrder == null || currentOrder.getOrderItems() == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (Model.OrderItem item : currentOrder.getOrderItems()) {
            // Only count completed items (status = false means completed)
            if (item.getStatus() != null && !item.getStatus()) {
                total = total.add(item.getSubtotal());
            }
        }
        return total;
    }
    
    private boolean areAllItemsCompleted() {
        if (currentOrder == null || currentOrder.getOrderItems() == null || currentOrder.getOrderItems().isEmpty()) {
            return false;
        }
        
        // Check if all items are completed (status = false)
        for (Model.OrderItem item : currentOrder.getOrderItems()) {
            if (item.getStatus() != null && item.getStatus()) {
                // Found an active item, not all are completed
                return false;
            }
        }
        return true;
    }
    
    private void handlePayment() {
        if (currentOrder == null) {
            SceneNavigator.showError("No order data available.");
            return;
        }
        
        // Preserve payment method before reloading (it's not stored in database)
        Payment.PaymentMethod savedPaymentMethod = currentOrder.getPaymentMethod();
        
        // Reload order to get latest data
        Order refreshedOrder = OrderDB.getWholeOrder(currentOrder.getOrderId());
        if (refreshedOrder != null) {
            currentOrder = refreshedOrder;
            // Restore payment method (it's not stored in database, so restore from original)
            if (savedPaymentMethod != null) {
                currentOrder.setPaymentMethod(savedPaymentMethod);
            }
        }

        // Check if all items are completed before allowing payment
        if (!areAllItemsCompleted()) {
            SceneNavigator.showError("Cannot process payment: Not all order items are completed.\nPlease mark all items as completed before payment.");
            return;
        }

        // Recalculate total from completed items
        totalDue = calculateTotalFromCompletedItems();
        
        if (totalDue.compareTo(BigDecimal.ZERO) <= 0) {
            SceneNavigator.showError("Cannot process payment: Order total is zero.");
            return;
        }

        // Validate amount paid
        String amountPaidStr = amountPaidField.getText().trim();
        if (amountPaidStr.isEmpty()) {
            SceneNavigator.showError("Please enter the amount paid.");
            return;
        }

        BigDecimal amountPaid;
        try {
            amountPaid = new BigDecimal(amountPaidStr);
        } catch (NumberFormatException e) {
            SceneNavigator.showError("Please enter a valid amount.");
            return;
        }

        if (amountPaid.compareTo(totalDue) < 0) {
            SceneNavigator.showError("Amount paid is less than total due. Cannot complete payment.");
            return;
        }

        // Get current staff
        Staff currentStaff = UserService.getCurrentUser();
        if (currentStaff == null) {
            SceneNavigator.showError("Staff information not found. Please login again.");
            return;
        }

        // Ensure payment method is set (should be CASH from PaymentMethodUI)
        if (currentOrder.getPaymentMethod() == null) {
            // Default to CASH if not set (since we're in SettledCashUI)
            currentOrder.setPaymentMethod(Payment.PaymentMethod.CASH);
        }
        
        // Update order total cost in database
        currentOrder.setTotalCost(totalDue);
        currentOrder.setStaffId(currentStaff.getStaffId());
        OrderDB.updateOrderTotal(currentOrder.getOrderId(), totalDue);

        // Record payment (as non-member - pass null for customerId)
        int paymentResult = PaymentControl.initiatePayment(currentOrder, null); // null = non-member

        if (paymentResult == -1) {
            SceneNavigator.showError("Cannot process payment: Order total is zero.");
            return;
        } else if (paymentResult == 0) {
            SceneNavigator.showError("Payment processing failed. Please try again.");
            return;
        }

        // Close order (update status to CLOSED and table status)
        closeOrder();

        // Show success message with change
        BigDecimal change = amountPaid.subtract(totalDue);
        String message = String.format(
            "Payment Successful!\n\n" +
            "Total: $%.2f\n" +
            "Amount Paid: $%.2f\n" +
            "Change: $%.2f\n\n" +
            "Order has been closed.",
            totalDue, amountPaid, change
        );

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Payment Complete");
        successAlert.setHeaderText("Payment Successful");
        successAlert.setContentText(message);
        successAlert.showAndWait();

        // Return to transaction menu
        javafx.stage.Stage stage = (javafx.stage.Stage) paidButton.getScene().getWindow();
        SceneNavigator.switchScene(paidButton, "/Resources/Transactions/transactionMenu.fxml");
    }

    private void closeOrder() {
        // Update order status to CLOSED in database
        boolean orderClosed = OrderDB.updateOrderStatus(currentOrder.getOrderId(), OrderStatus.CLOSED);
        
        if (!orderClosed) {
            System.err.println("Error closing order: Failed to update order status");
            return;
        }
        
        // Update table status to available
        DAO.TableDAO tableDAO = new DAO.TableDAO();
        Model.Table table = tableDAO.getTableById(currentOrder.getTableId());
        if (table != null) {
            table.setTableStatus(true); // Table is now available
            tableDAO.updateTable(table);
        }
    }
}