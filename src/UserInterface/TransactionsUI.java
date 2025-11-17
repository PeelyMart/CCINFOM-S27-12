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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        
        // Refresh buttons when scene becomes visible (using Platform.runLater to ensure scene is set)
        javafx.application.Platform.runLater(() -> {
            if (tableContainer != null && tableContainer.getScene() != null) {
                tableContainer.getScene().windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null && newWindow instanceof Stage) {
                        Stage stage = (Stage) newWindow;
                        // Refresh when window is shown
                        stage.setOnShown(e -> loadTablesFromDB());
                    }
                });
                // Also set for current window if already set
                if (tableContainer.getScene().getWindow() != null && tableContainer.getScene().getWindow() instanceof Stage) {
                    Stage stage = (Stage) tableContainer.getScene().getWindow();
                    stage.setOnShown(e -> loadTablesFromDB());
                }
            }
        });
    }
    
    /**
     * Public method to refresh table buttons - can be called when returning to this screen
     */
    public void refreshTables() {
        loadTablesFromDB();
    }

    private void loadTablesFromDB() {
        ArrayList<Table> tables = new ArrayList<>(TableDAO.getAllTables());
        
        // Sort tables by ID to ensure proper ordering (1, 2, 3, 4, 5, 6, 7, etc.)
        Collections.sort(tables, Comparator.comparingInt(Table::getTableId));
        
        tableContainer.getChildren().clear();
        // Force wrap at exactly 5 columns: 5 buttons * 90px + 4 gaps * 20px = 450 + 80 = 530px
        // Set both prefWidth and maxWidth to force wrapping at 5 columns
        double wrapWidth = COLUMNS * 90 + (COLUMNS - 1) * 20; // 530px for exactly 5 columns with 20px gaps
        tableContainer.setHgap(20);
        tableContainer.setVgap(20);
        tableContainer.setPrefWidth(wrapWidth);
        tableContainer.setMaxWidth(wrapWidth);
        
        // Debug: Print table info
        System.out.println("=== Loading Tables ===");
        for (Table t : tables) {
            Order activeOrder = OrderDB.getWholeOrderByTable(t.getTableId());
            System.out.println("Table " + t.getTableId() + " - Status: " + t.getTableStatus());
            if (activeOrder != null) {
                System.out.println("  Order ID: " + activeOrder.getOrderId() + ", Status: " + activeOrder.getStatus());
                if (activeOrder.getOrderItems() != null) {
                    long activeItems = activeOrder.getOrderItems().stream()
                        .filter(item -> item.getStatus() != null && item.getStatus())
                        .count();
                    System.out.println("  Active Items: " + activeItems);
                }
            } else {
                System.out.println("  No order found");
            }
        }

        for (Table t : tables) {
            Button tableButton = new Button(String.valueOf(t.getTableId()));
            tableButton.setPrefSize(90, 90);
            tableButton.setMinSize(90, 90);
            tableButton.setMaxSize(90, 90);
            tableButton.setFont(javafx.scene.text.Font.font(18));
            tableButton.setWrapText(false);

            // Check if table has an OPEN order (unavailable)
            // Logic: IF TABLE HAS ORDER_ID with OPEN status = UNAVAILABLE = RED
            //        IF TABLE HAS CLOSED ORDER/NO ACTIVE ORDER = AVAILABLE = GREEN
            Order order = OrderDB.getWholeOrderByTable(t.getTableId());
            boolean hasOpenOrder = false;
            if (order != null && order.getStatus() == OrderStatus.OPEN) {
                hasOpenOrder = true;
            }
            
            // Red = has OPEN order (unavailable), Green = CLOSED order or no order (available)
            if (hasOpenOrder) {
                // Red = has OPEN order (table is unavailable)
                tableButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-font-weight: bold;");
            } else {
                // Green = CLOSED order or no order (table is available)
                tableButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 50; -fx-border-radius: 50; -fx-font-weight: bold;");
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
