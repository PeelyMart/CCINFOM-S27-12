package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class OptionsUI {

    @FXML
    private Button menuButton, staffButton, loyaltyMemberButton, tableButton, backButton;

    @FXML
    private void initialize() {
        // Optional debug line
        System.out.println("OptionsUI loaded.");

        // Button actions
        if (menuButton != null) {
            menuButton.setOnAction(e -> SceneNavigator.testClick("MENU button clicked"));
        }

        if (staffButton != null) {
            staffButton.setOnAction(e -> SceneNavigator.testClick("STAFF button clicked"));
        }

        if (loyaltyMemberButton != null) {
            loyaltyMemberButton.setOnAction(e -> SceneNavigator.testClick("LOYALTY MEMBER button clicked"));
        }

        if (tableButton != null) {
            tableButton.setOnAction(e -> SceneNavigator.testClick("TABLE button clicked"));
        }

        if (backButton != null) {
            backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/Transactions/transactionMenu.fxml"));
        }
    }
}
