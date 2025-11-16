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

        Order refreshedOrder = OrderDB.getWholeOrder(currentOrder.getOrderId());
        if (refreshedOrder != null) {
            currentOrder = refreshedOrder;
        }

        finalTotal = currentOrder.getTotalCost() != null ? currentOrder.getTotalCost() : BigDecimal.ZERO;
        
        if (balanceArea != null) {
            balanceArea.setText(String.format("₱%.2f", finalTotal));
        }
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