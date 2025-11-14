package UserInterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class reservationsUI {

    @FXML
    private Button addButton, searchButton, editButton, deleteButton, backButton;

    @FXML
    private void initialize() {
        addButton.setOnAction(e -> openAddReservation());
        searchButton.setOnAction(e -> openSearchReservation());
        editButton.setOnAction(e -> openEditReservation());
        deleteButton.setOnAction(e -> openDeleteReservation());

        // Back button -> go back to main menu (or whatever previous scene)
        if (backButton != null) {
            backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/Transactions/transactionMenu.fxml"));
        }
    }

    private void openAddReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/Transactions/reservationConfirmation.fxml"));
            AnchorPane root = loader.load();
            Stage stage = (Stage) addButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void openSearchReservation() {
        // TODO: Implement search logic
    }

    private void openEditReservation() {
        // TODO: Implement edit logic
    }

    private void openDeleteReservation() {
        // TODO: Implement delete logic
    }
}
