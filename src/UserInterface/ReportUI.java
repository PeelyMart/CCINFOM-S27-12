package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ReportUI {

    @FXML
    private Button tablesButton, staffButton, loyaltyButton, menuButton;

    @FXML
    private void initialize() {
        // assign the same handler to all buttons
        if (tablesButton != null)
            tablesButton.setOnAction(e -> SceneNavigator.switchScene(tablesButton, "/Resources/StaffOptions/staff-options.fxml"));

        if (staffButton != null)
            staffButton.setOnAction(e -> SceneNavigator.switchScene(staffButton, "/Resources/StaffOptions/staff-options.fxml"));

        if (loyaltyButton != null)
            loyaltyButton.setOnAction(e -> SceneNavigator.switchScene(loyaltyButton, "/Resources/LoyaltyMemberOptions/loyalty-options.fxml"));

        if (menuButton != null)
            menuButton.setOnAction(e -> SceneNavigator.switchScene(menuButton, "/Resources/MenuOptions/menu-options.fxml"));

    }

}
