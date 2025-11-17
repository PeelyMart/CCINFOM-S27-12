package UserInterface;

import DAO.OrderDB;
import Model.Order;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.math.BigDecimal;

public class PaymentNormalUI {

    @FXML
    private Button pwdButton;

    @FXML
    private Button backButton;

    @FXML
    private Button payButton;

    @FXML
    private TextArea orderSummaryArea;

    @FXML
    private TextArea totalArea;

    private Order currentOrder;
    private BigDecimal finalTotal;

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
        }

        // Build order summary
        StringBuilder summary = new StringBuilder();
        summary.append("Order ID: ").append(currentOrder.getOrderId()).append("\n");
        summary.append("Table ID: ").append(currentOrder.getTableId()).append("\n");
        summary.append("Order Time: ").append(currentOrder.getOrderTime()).append("\n\n");
        summary.append("Items:\n");
        
        if (currentOrder.getOrderItems() != null) {
            for (var item : currentOrder.getOrderItems()) {
                summary.append(String.format("  - Item: %d, Qty: %d, Subtotal: $%.2f\n", 
                    item.getMenuId(), item.getQuantity(), item.getSubtotal()));
            }
        }
        
        if (orderSummaryArea != null) {
            orderSummaryArea.setText(summary.toString());
        }

        // Calculate total from completed items only (status = false means completed)
        finalTotal = calculateTotalFromCompletedItems();
        
        if (totalArea != null) {
            totalArea.setText(String.format("$%.2f", finalTotal));
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

    @FXML
    private void initialize() {

        if (payButton != null) {
            payButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                
                // Reload order to get latest data
                Order refreshedOrder = OrderDB.getWholeOrder(currentOrder.getOrderId());
                if (refreshedOrder != null) {
                    currentOrder = refreshedOrder;
                }
                
                // Check if all items are completed before allowing payment
                if (!areAllItemsCompleted()) {
                    SceneNavigator.showError("Cannot proceed to payment: Not all order items are completed.\nPlease mark all items as completed before payment.");
                    return;
                }
                
                // Recalculate total from completed items
                finalTotal = calculateTotalFromCompletedItems();
                
                if (finalTotal.compareTo(BigDecimal.ZERO) <= 0) {
                    SceneNavigator.showError("Cannot proceed to payment: Order total is zero.");
                    return;
                }
                
                // Update order total in database
                currentOrder.setTotalCost(finalTotal);
                OrderDB.updateOrderTotal(currentOrder.getOrderId(), finalTotal);
                
                javafx.stage.Stage stage = (javafx.stage.Stage) payButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/paymentMethod.fxml", currentOrder);
            });
        }

        if (backButton != null) {
            backButton.setOnAction(e -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", currentOrder);
            });
        }

        if (pwdButton != null) {
            pwdButton.setOnAction(e -> {
                SceneNavigator.showInfo("PWD discount feature coming soon.");
            });
        }
    }
}