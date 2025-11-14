package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PaymentNormalUI {

    @FXML
    private Button pwdButton;

    @FXML
    private Button backButton;

    @FXML
    private Button payButton;

    @FXML
    private void initialize() {
        // PAY button → go to payment method
        if (payButton != null) {
            payButton.setOnAction(e -> SceneNavigator.switchScene(payButton, "/Resources/Transactions/paymentMethod.fxml"));
        }

        // BACK button → go back to main transaction menu
        if (backButton != null) {
            backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/Transactions/orders.fxml"));
        }

        if (pwdButton != null) {
            pwdButton.setOnAction(e -> SceneNavigator.testClick("BUTTON WORKS"));
        }

    }
}
