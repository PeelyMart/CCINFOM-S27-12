package UserInterface;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginUI {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Text statusText; // optional

    private Stage loginStage;  // popup stage
    private Stage mainStage;   // main application stage

    // Method to set stage references
    public void setStages(Stage loginStage, Stage mainStage) {
        this.loginStage = loginStage;
        this.mainStage = mainStage;
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.equals("admin") && password.equals("password")) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Login Success");
            alert.setHeaderText(null);
            alert.setContentText("Welcome, " + username + "!");
            alert.showAndWait();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Resources/Dashboard/dashboard.fxml"));
                AnchorPane root = loader.load();
                Scene scene = new Scene(root);

                // Optionally, you can hide login stage and show main stage
                loginStage.close();
                mainStage.setScene(scene);
                mainStage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
                Alert errorAlert = new Alert(AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to load dashboard.");
                errorAlert.showAndWait();
            }

        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid username or password.");
            alert.showAndWait();
        }
    }
}
