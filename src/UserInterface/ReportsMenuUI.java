package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ReportsMenuUI {

    @FXML
    private Button menuButton;

    @FXML
    private void initialize() {
        menuButton.setOnAction(e -> {
            SceneNavigator.switchScene(menuButton, "/Resources/Reports/reports-view.fxml");
        });
    }
}
