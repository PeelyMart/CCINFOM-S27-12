package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class PaymentMethodUI {

    @FXML
    private TextArea balanceArea;

    @FXML
    private Button cardButton, cashButton, onlineButton, backButton; // add backButton

    @FXML
    private void initialize() {
        balanceArea.setText("₱0.00 (mock balance)");

        cardButton.setOnAction(e -> SceneNavigator.switchScene(cardButton, "/Resources/Transactions/settledNC.fxml"));
        onlineButton.setOnAction(e -> SceneNavigator.switchScene(onlineButton, "/Resources/Transactions/settledNC.fxml"));
        cashButton.setOnAction(e -> SceneNavigator.switchScene(cashButton, "/Resources/Transactions/settledCash.fxml"));

        // BACK button → return to previous scene, for example transaction menu
        if (backButton != null) {
            backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/Transactions/orders.fxml"));
        }
    }
}
