package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SettledNCUI {

    @FXML
    private Button cancelButton, paidButton, backButton;

    @FXML
    private void initialize() {

        cancelButton.setOnAction(e -> SceneNavigator.testClick("button works"));
        paidButton.setOnAction(e -> SceneNavigator.testClick("Buttom works"));
        backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/Transactions/paymentMethod.fxml"));

        // BACK button → return to previous scene, for example transaction menu
        if (backButton != null) {
            backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/Transactions/paymentMethod.fxml"));
        }
    }
}
