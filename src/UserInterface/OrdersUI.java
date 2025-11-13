package UserInterface;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

public class OrdersUI {

    @FXML
    private Button addButton, searchButton, deleteButton, editButton, payButton, backButton;

    @FXML
    private void initialize() {
        // All non-functional buttons show test popup
        addButton.setOnAction(e -> SceneNavigator.testClick("ADD"));
        searchButton.setOnAction(e -> SceneNavigator.testClick("SEARCH"));
        deleteButton.setOnAction(e -> SceneNavigator.testClick("DELETE"));
        editButton.setOnAction(e -> SceneNavigator.testClick("EDIT"));

        // PAY button -> load Payment UI as usual
        payButton.setOnAction(e -> openPaymentChoice());

        // BACK button -> go to previous scene (e.g., main menu)
        backButton.setOnAction(e -> SceneNavigator.switchScene(backButton, "/Resources/Transactions/transactionMenu.fxml"));
    }

    private void openPaymentChoice() {
        try {
            // Create a confirmation alert asking if user is a loyalty member
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Payment Type");
            alert.setHeaderText("Are you a loyalty member?");
            alert.setContentText("Choose your payment type:");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            // Show the alert and wait for user choice
            ButtonType result = alert.showAndWait().orElse(noButton);

            String fxml;
            if (result == yesButton) {
                fxml = "/Resources/Transactions/paymentLM.fxml";
            } else {
                fxml = "/Resources/Transactions/paymentNormal.fxml";
            }

            // Use payButton to get the current Stage
            SceneNavigator.switchScene(payButton, fxml);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
