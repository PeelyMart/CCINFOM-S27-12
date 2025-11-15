package UserInterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TransactionsUI {

    @FXML
    private Button oneButton;

    @FXML
    public void initialize() {
        // Delay setting the action to ensure button is injected
        if (oneButton != null) {
            oneButton.setOnAction(e -> openTransactionMenu());
        }
    }

    private void openTransactionMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/Transactions/transactionMenu.fxml"));
            AnchorPane root = loader.load();

            Stage stage = (Stage) oneButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
