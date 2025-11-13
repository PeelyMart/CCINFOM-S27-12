package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ReportUI {

    @FXML
    private Button tablesButton, staffButton, loyaltyButton, menuButton;

    @FXML
    private void initialize() {
        // assign the same handler to all buttons
        if (tablesButton != null) tablesButton.setOnAction(e -> SceneNavigator.testClick("BUTTON WORKS"));
        if (staffButton != null) staffButton.setOnAction(e -> SceneNavigator.testClick("BUTTON WORKS"));
        if (loyaltyButton != null)  loyaltyButton.setOnAction(e -> SceneNavigator.testClick("BUTTON WORKS"));
        if (menuButton != null)  menuButton.setOnAction(e -> SceneNavigator.testClick("BUTTON WORKS"));

    }

}
