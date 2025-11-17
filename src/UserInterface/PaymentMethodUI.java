package UserInterface;

import DAO.OrderDB;
import Model.Order;
import Model.Payment;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

import java.math.BigDecimal;

public class PaymentMethodUI {

    @FXML
    private TextArea balanceArea;

    @FXML
    private Button cardButton, cashButton, onlineButton, backButton;

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

        // Calculate total from completed items only (status = false means completed)
        finalTotal = calculateTotalFromCompletedItems();
        
        if (balanceArea != null) {
            balanceArea.setText(String.format("$%.2f", finalTotal));
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

    @FXML
    private void initialize() {
        if (cardButton != null) {
            cardButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                currentOrder.setPaymentMethod(Payment.PaymentMethod.DEBIT);
                javafx.stage.Stage stage = (javafx.stage.Stage) cardButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/settledNC.fxml", currentOrder);
            });
        }

        if (onlineButton != null) {
            onlineButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                currentOrder.setPaymentMethod(Payment.PaymentMethod.CREDIT);
                javafx.stage.Stage stage = (javafx.stage.Stage) onlineButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/settledNC.fxml", currentOrder);
            });
        }

        if (cashButton != null) {
            cashButton.setOnAction(e -> {
                if (currentOrder == null) {
                    SceneNavigator.showError("No order data available.");
                    return;
                }
                currentOrder.setPaymentMethod(Payment.PaymentMethod.CASH);
                javafx.stage.Stage stage = (javafx.stage.Stage) cashButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/settledCash.fxml", currentOrder);
            });
        }

        if (backButton != null) {
            backButton.setOnAction(e -> {

                javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/orders.fxml", currentOrder);
            });
        }
    }
}