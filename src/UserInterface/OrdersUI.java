package UserInterface;

import DAO.MenuItemDAO;
import Model.MenuItem;
import Model.Order;
import Model.OrderItem;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;

public class OrdersUI {

    @FXML
    private Button addButton, searchButton, deleteButton, editButton, payButton, backButton;

    @FXML
    private TextField searchOrder;

    @FXML
    private TableView<OrderItemDisplay> orderItemsTable;

    @FXML
    private TableColumn<OrderItemDisplay, String> menuItemColumn;

    @FXML
    private TableColumn<OrderItemDisplay, String> quantityColumn;

    @FXML
    private TableColumn<OrderItemDisplay, String> activeColumn;

    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private Order currentOrder; // Store the current order

    @FXML
    private void initialize() {
        System.out.println("OrdersUI.initialize() called");
        System.out.println("orderItemsTable is null: " + (orderItemsTable == null));
        System.out.println("menuItemColumn is null: " + (menuItemColumn == null));
        System.out.println("quantityColumn is null: " + (quantityColumn == null));
        System.out.println("activeColumn is null: " + (activeColumn == null));
        
        // Setup TableView columns FIRST - this is critical
        setupTableViewColumns();

        // Non-functional buttons show test popup
        if (addButton != null) {
            addButton.setOnAction(e -> SceneNavigator.testClick("ADD"));
        }
        if (searchButton != null) {
            searchButton.setOnAction(e -> SceneNavigator.testClick("SEARCH"));
        }
        if (deleteButton != null) {
            deleteButton.setOnAction(e -> SceneNavigator.testClick("DELETE"));
        }
        if (editButton != null) {
            editButton.setOnAction(e -> SceneNavigator.testClick("EDIT"));
        }

        // PAY button -> open payment UI
        if (payButton != null) {
            payButton.setOnAction(e -> openPaymentChoice());
        }

        // BACK button -> go back to Transaction Menu
        if (backButton != null) {
            backButton.setOnAction(e ->
                    SceneNavigator.switchScene(backButton, "/Resources/Transactions/transactionMenu.fxml"));
        }

        // If there's an order already set, load it now
        if (currentOrder != null) {
            System.out.println("Loading currentOrder in initialize()");
            loadOrderItems(currentOrder);
        }
    }

    private void setupTableViewColumns() {
        System.out.println("setupTableViewColumns() called");
        if (menuItemColumn != null) {
            menuItemColumn.setCellValueFactory(new PropertyValueFactory<>("menuItemName"));
            System.out.println("menuItemColumn cellValueFactory set");
        } else {
            System.out.println("ERROR: menuItemColumn is null!");
        }
        if (quantityColumn != null) {
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            System.out.println("quantityColumn cellValueFactory set");
        } else {
            System.out.println("ERROR: quantityColumn is null!");
        }
        if (activeColumn != null) {
            activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));
            System.out.println("activeColumn cellValueFactory set");
        } else {
            System.out.println("ERROR: activeColumn is null!");
        }
        
        // Verify columns are added to TableView
        if (orderItemsTable != null) {
            System.out.println("TableView has " + orderItemsTable.getColumns().size() + " columns");
        }
    }

    /**
     * Called by SceneNavigator to set order data when navigating from table selection
     */
    public void setData(Object data) {
        System.out.println("OrdersUI.setData() called with: " + (data != null ? data.getClass().getName() : "null"));
        if (data instanceof Order) {
            Order order = (Order) data;
            currentOrder = order;
            System.out.println("Order received - Order ID: " + order.getOrderId() + ", Table ID: " + order.getTableId());
            System.out.println("Order items count: " + (order.getOrderItems() != null ? order.getOrderItems().size() : 0));
            
            // If TableView columns are set up, load immediately
            if (orderItemsTable != null) {
                setupTableViewColumns(); // Ensure columns are set up
                loadOrderItems(order);
            }
        } else {
            System.out.println("Data is not an Order instance: " + (data != null ? data.getClass().getName() : "null"));
        }
    }

    private void loadOrderItems(Order order) {
        System.out.println("========== loadOrderItems() called ==========");
        if (order == null) {
            System.out.println("ERROR: Order is null!");
            if (orderItemsTable != null) {
                orderItemsTable.setItems(FXCollections.observableArrayList());
            }
            return;
        }

        System.out.println("Order ID: " + order.getOrderId() + ", Table ID: " + order.getTableId());
        System.out.println("Order items list is null: " + (order.getOrderItems() == null));
        
        if (order.getOrderItems() == null) {
            System.out.println("ERROR: order.getOrderItems() returned null!");
            if (orderItemsTable != null) {
                orderItemsTable.setItems(FXCollections.observableArrayList());
            }
            return;
        }
        
        if (order.getOrderItems().isEmpty()) {
            System.out.println("WARNING: Order has no items (empty list)!");
            if (orderItemsTable != null) {
                orderItemsTable.setItems(FXCollections.observableArrayList());
            }
            return;
        }

        System.out.println("Loading " + order.getOrderItems().size() + " order items");
        List<OrderItemDisplay> displayItems = new ArrayList<>();
        
        for (OrderItem item : order.getOrderItems()) {
            System.out.println("Processing OrderItem - MenuID: " + item.getMenuId() + ", Qty: " + item.getQuantity() + ", Status: " + item.getStatus());
            
            MenuItem menuItem = menuItemDAO.getMenuItemById(item.getMenuId());
            String menuName;
            if (menuItem != null) {
                menuName = menuItem.getMenuName();
                System.out.println("Found menu item: " + menuName);
            } else {
                menuName = "Unknown (ID: " + item.getMenuId() + ")";
                System.out.println("WARNING: Menu item not found for menu_id: " + item.getMenuId());
            }
            
            String quantity = String.valueOf(item.getQuantity());
            
            // Get status string - ensure setStatus was called
            String active;
            if (item.getStatusAsAtring() != null && !item.getStatusAsAtring().isEmpty()) {
                active = item.getStatusAsAtring();
            } else {
                // Fallback - setStatus might not have been called
                active = item.getStatus() != null && item.getStatus() ? "active" : "inactive";
            }

            OrderItemDisplay displayItem = new OrderItemDisplay(menuName, quantity, active);
            displayItems.add(displayItem);
            System.out.println("Added display item: " + menuName + " x" + quantity + " (" + active + ")");
        }

        System.out.println("Total display items created: " + displayItems.size());

        if (orderItemsTable != null) {
            System.out.println("Setting items on TableView...");
            // Use Platform.runLater to ensure UI updates on JavaFX thread
            Platform.runLater(() -> {
                try {
                    orderItemsTable.setItems(FXCollections.observableArrayList(displayItems));
                    System.out.println("✓ TableView updated with " + displayItems.size() + " items");
                    System.out.println("✓ TableView.items.size() = " + orderItemsTable.getItems().size());
                    
                    // Force refresh
                    orderItemsTable.refresh();
                    
                    // Verify columns
                    System.out.println("✓ TableView has " + orderItemsTable.getColumns().size() + " columns");
                    if (orderItemsTable.getColumns().size() > 0) {
                        System.out.println("✓ First column: " + orderItemsTable.getColumns().get(0).getText());
                    }
                    
                    // Check if items are visible
                    if (orderItemsTable.getItems().size() > 0) {
                        System.out.println("✓ First item: " + orderItemsTable.getItems().get(0).getMenuItemName());
                    }
                } catch (Exception e) {
                    System.err.println("ERROR setting TableView items: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("ERROR: orderItemsTable is null!");
        }
        System.out.println("========== loadOrderItems() finished ==========");
    }

    // Match the TextField onAction in FXML
    @FXML
    private void searchStaffEntries() {
        SceneNavigator.testClick("SEARCH TEXTFIELD: " + searchOrder.getText());
    }

    private void openPaymentChoice() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Payment Type");
            alert.setHeaderText("Are you a loyalty member?");
            alert.setContentText("Choose your payment type:");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            ButtonType result = alert.showAndWait().orElse(noButton);

            String fxml = (result == yesButton) ?
                    "/Resources/Transactions/paymentLM.fxml" :
                    "/Resources/Transactions/paymentNormal.fxml";

            SceneNavigator.switchScene(payButton, fxml);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Simple display model for order items in the TableView
     */
    public static class OrderItemDisplay {
        private final SimpleStringProperty menuItemName;
        private final SimpleStringProperty quantity;
        private final SimpleStringProperty active;

        public OrderItemDisplay(String menuItemName, String quantity, String active) {
            this.menuItemName = new SimpleStringProperty(menuItemName);
            this.quantity = new SimpleStringProperty(quantity);
            this.active = new SimpleStringProperty(active);
        }

        public String getMenuItemName() {
            return menuItemName.get();
        }

        public String getQuantity() {
            return quantity.get();
        }

        public String getActive() {
            return active.get();
        }
    }
}
