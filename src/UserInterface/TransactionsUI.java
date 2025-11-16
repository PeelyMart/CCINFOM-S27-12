package UserInterface;

import Controller.TableActions;
import DAO.OrderDB;
import DAO.TableDAO;
import Model.Order;
import Model.OrderStatus;
import Model.Table;
import Model.Staff;
import Controller.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TransactionsUI {

    @FXML
    private FlowPane tableContainer;
    
    private static final int COLUMNS = 5;

    private int currentStaffId;

    @FXML
    public void initialize() {
        Staff currentUser = UserService.getCurrentUser();
        if (currentUser != null) {
            currentStaffId = currentUser.getStaffId();
        } else {
            System.err.println("No user logged in!");
            return;
        }

        loadTablesFromDB();
    }

    private void loadTablesFromDB() {
        ArrayList<Table> tables = new ArrayList<>(TableDAO.getAllTables());
        tableContainer.getChildren().clear();
        tableContainer.setHgap(10);
        tableContainer.setVgap(10);
        tableContainer.setPrefWrapLength(COLUMNS * 70); // 5 columns * (60 button + 10 gap)

        for (Table t : tables) {
            Button tableButton = new Button(String.valueOf(t.getTableId()));
            tableButton.setPrefSize(60, 60);
            tableButton.setMinSize(60, 60);
            tableButton.setMaxSize(60, 60);
            tableButton.setFont(javafx.scene.text.Font.font(16));

            // Check if table has an active order (OPEN status)
            Order activeOrder = OrderDB.getWholeOrderByTable(t.getTableId());
            boolean hasActiveOrder = (activeOrder != null && activeOrder.getStatus() == OrderStatus.OPEN);
            
            // Green if has active order, red if occupied but no active order, gray if available
            if (hasActiveOrder) {
                // Green = has active order
                tableButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-font-weight: bold;");
            } else if (!t.getTableStatus()) {
                // Red = occupied but no active order
                tableButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50;");
            } else {
                // Gray = available
                tableButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50;");
            }

            tableButton.setOnAction(e -> handleTableClick(t));
            tableContainer.getChildren().add(tableButton);
        }
    }

    private void handleTableClick(Table table) {
        Stage stage = (Stage) tableContainer.getScene().getWindow(); // get current stage

        if (!table.getTableStatus()) { // table occupied
            Order order = OrderDB.getWholeOrderByTable(table.getTableId());
            if (order != null) {
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/transactionMenu.fxml", order);
            } else {
                SceneNavigator.showInfo("No order found for Table " + table.getTableId());
            }
            return;
        }

        // table available → take it
        TableActions.initateTable(table, currentStaffId);
        loadTablesFromDB();

        // pass table to TransactionMenuUI
        SceneNavigator.switchNoButton(stage, "/Resources/Transactions/transactionMenu.fxml", table);
    }

}
