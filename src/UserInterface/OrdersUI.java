package UserInterface;

import DAO.MenuItemDAO;
import DAO.OrderDB;
import DAO.OrderitemDAO;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

public class OrdersUI {

    @FXML
    private Button addButton, searchButton, deleteButton, editButton, payButton, backButton;

    @FXML
    private TextField searchOrder;

    @FXML
    private Text subtotalLabel;
    
    @FXML
    private Text tableNumberText;

    @FXML
    private TableView<OrderItemDisplay> orderItemsTable;

    @FXML
    private TableColumn<OrderItemDisplay, String> menuItemColumn;

    @FXML
    private TableColumn<OrderItemDisplay, String> quantityColumn;

    @FXML
    private TableColumn<OrderItemDisplay, String> activeColumn;

    private MenuItemDAO menuItemDAO = new MenuItemDAO();
    private OrderitemDAO orderitemDAO = new OrderitemDAO();
    private Order currentOrder; // Store the current order

    @FXML
    private void initialize() {
        System.out.println("OrdersUI.initialize() called");
        System.out.println("orderItemsTable is null: " + (orderItemsTable == null));
        System.out.println("menuItemColumn is null: " + (menuItemColumn == null));
        System.out.println("quantityColumn is null: " + (quantityColumn == null));
        System.out.println("activeColumn is null: " + (activeColumn == null));
        
        setupTableViewColumns();

        if (addButton != null) {
            addButton.setOnAction(e -> handleAdd());
        }
        if (searchButton != null) {
            searchButton.setOnAction(e -> handleSearch());
        }
        if (deleteButton != null) {
            deleteButton.setOnAction(e -> handleDelete());
        }
        if (editButton != null) {
            editButton.setOnAction(e -> handleEdit());
        }

        if (payButton != null) {
            payButton.setOnAction(e -> openPaymentChoice());
        }

        if (backButton != null) {
            backButton.setOnAction(e -> {
                javafx.stage.Stage stage = (javafx.stage.Stage) backButton.getScene().getWindow();
                SceneNavigator.switchNoButton(stage, "/Resources/Transactions/transactionMenu.fxml", currentOrder);
            });
        }

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
            
            // Update table number display
            if (tableNumberText != null) {
                tableNumberText.setText("Table: " + order.getTableId());
            }
            
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
            if (subtotalLabel != null) {
                subtotalLabel.setText("Subtotal: $0.00");
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
            if (subtotalLabel != null) {
                subtotalLabel.setText("Subtotal: $0.00");
            }
            return;
        }
        
        if (order.getOrderItems().isEmpty()) {
            System.out.println("WARNING: Order has no items (empty list)!");
            if (orderItemsTable != null) {
                orderItemsTable.setItems(FXCollections.observableArrayList());
            }
            if (subtotalLabel != null) {
                subtotalLabel.setText("Subtotal: $0.00");
            }
            return;
        }

        System.out.println("Loading " + order.getOrderItems().size() + " order items");
        
        Map<String, Integer> groupedItems = new LinkedHashMap<>();
        Map<String, OrderItem> representativeItems = new LinkedHashMap<>(); // Store one item per group
        BigDecimal totalSubtotal = BigDecimal.ZERO;
        
        for (OrderItem item : order.getOrderItems()) {
            MenuItem menuItem = menuItemDAO.getMenuItemById(item.getMenuId());
            String menuName;
            if (menuItem != null) {
                menuName = menuItem.getMenuName();
            } else {
                menuName = "Unknown (ID: " + item.getMenuId() + ")";
            }
            
            boolean isActive = item.getStatus() != null && item.getStatus();
            String status = isActive ? "active" : "completed";
            
            if (isActive) {
                totalSubtotal = totalSubtotal.add(item.getSubtotal());
            }
            
            String key = menuName + "_" + status;
            
            if (groupedItems.containsKey(key)) {
                groupedItems.put(key, groupedItems.get(key) + item.getQuantity());
            } else {
                groupedItems.put(key, item.getQuantity());
                representativeItems.put(key, item); // Store first item as representative
            }
            
            System.out.println("Added item: " + menuName + " x" + item.getQuantity() + " (" + status + ")");
        }
        
        List<OrderItemDisplay> displayItems = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : groupedItems.entrySet()) {
            String key = entry.getKey();
            int totalQuantity = entry.getValue();
            OrderItem representativeItem = representativeItems.get(key);
            
            int lastUnderscore = key.lastIndexOf("_");
            String menuName = key.substring(0, lastUnderscore);
            String status = key.substring(lastUnderscore + 1);
            
            OrderItemDisplay displayItem = new OrderItemDisplay(menuName, String.valueOf(totalQuantity), status, representativeItem);
            displayItems.add(displayItem);
            System.out.println("Added grouped display item: " + menuName + " x" + totalQuantity + " (" + status + ")");
        }

        System.out.println("Total display items created: " + displayItems.size());
        
        final BigDecimal finalTotalSubtotal = totalSubtotal;
        final List<OrderItemDisplay> finalDisplayItems = displayItems;

        if (orderItemsTable != null) {
            System.out.println("Setting items on TableView...");
            Platform.runLater(() -> {
                try {
                    orderItemsTable.setItems(FXCollections.observableArrayList(finalDisplayItems));
                    System.out.println("✓ TableView updated with " + finalDisplayItems.size() + " items");
                    System.out.println("✓ TableView.items.size() = " + orderItemsTable.getItems().size());
                    
                    if (subtotalLabel != null) {
                        subtotalLabel.setText("Subtotal: $" + String.format("%.2f", finalTotalSubtotal));
                    }
                    
                    orderItemsTable.refresh();
                    
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

    @FXML
    private void searchStaffEntries() {
        handleSearch();
    }
    
    private void handleAdd() {
        if (currentOrder == null) {
            SceneNavigator.showError("No order selected. Please search for an order first.");
            return;
        }
        
        ArrayList<MenuItem> allMenuItems = menuItemDAO.getAllMenuItems();
        if (allMenuItems == null || allMenuItems.isEmpty()) {
            SceneNavigator.showError("No menu items available.");
            return;
        }
        
        List<String> menuItemOptions = new ArrayList<>();
        for (MenuItem item : allMenuItems) {
            if (item.getStatus() != null && item.getStatus()) { // Only show available items
                menuItemOptions.add(item.getMenuName() + " - $" + String.format("%.2f", item.getPrice()));
            }
        }
        
        if (menuItemOptions.isEmpty()) {
            SceneNavigator.showError("No available menu items.");
            return;
        }
        
        ChoiceDialog<String> menuDialog = new ChoiceDialog<>(menuItemOptions.get(0), menuItemOptions);
        menuDialog.setTitle("Add Order Item");
        menuDialog.setHeaderText("Select Menu Item:");
        menuDialog.setContentText("Choose a menu item:");
        Optional<String> menuResult = menuDialog.showAndWait();
        
        menuResult.ifPresent(selectedMenu -> {
            String menuName = selectedMenu.split(" - ")[0];
            
            MenuItem selectedMenuItem = null;
            for (MenuItem item : allMenuItems) {
                if (item.getMenuName().equals(menuName)) {
                    selectedMenuItem = item;
                    break;
                }
            }
            
            if (selectedMenuItem == null) {
                SceneNavigator.showError("Menu item not found.");
                return;
            }
            
            // Check if menu item is available
            if (selectedMenuItem.getStatus() == null || !selectedMenuItem.getStatus()) {
                SceneNavigator.showError("Cannot add unavailable menu item: " + selectedMenuItem.getMenuName());
                return;
            }
            
            final MenuItem finalSelectedMenuItem = selectedMenuItem;
            
            TextInputDialog qtyDialog = new TextInputDialog("1");
            qtyDialog.setTitle("Add Order Item");
            qtyDialog.setHeaderText("Enter quantity:");
            Optional<String> qtyInput = qtyDialog.showAndWait();
            
            qtyInput.ifPresent(qtyStr -> {
                int quantity;
                try {
                    quantity = Integer.parseInt(qtyStr);
                    if (quantity <= 0) {
                        SceneNavigator.showError("Quantity must be greater than 0.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    SceneNavigator.showError("Quantity must be a number.");
                    return;
                }
                
                BigDecimal subtotal = BigDecimal.valueOf(finalSelectedMenuItem.getPrice()).multiply(BigDecimal.valueOf(quantity));
                
                OrderItem newItem = new OrderItem();
                newItem.setOrderId(currentOrder.getOrderId());
                newItem.setMenuId(finalSelectedMenuItem.getMenuId());
                newItem.setQuantity(quantity);
                newItem.setSubtotal(subtotal);
                newItem.setStatus(true); // Active by default
                
                boolean success = orderitemDAO.addOrderItem(newItem);
                if (success) {
                    SceneNavigator.showInfo("Order item added successfully!");
                    // Reload order from database
                    refreshCurrentOrder();
                } else {
                    SceneNavigator.showError("Failed to add order item. Please try again.");
                }
            });
        });
    }
    
    private void handleSearch() {
        String input = searchOrder.getText().trim();
        if (input.isEmpty()) {
            // If empty, reload all items
            if (currentOrder != null) {
                loadOrderItems(currentOrder);
            }
            return;
        }
        
        if (currentOrder == null || currentOrder.getOrderItems() == null || currentOrder.getOrderItems().isEmpty()) {
            SceneNavigator.showError("No order loaded. Please select a table first.");
            return;
        }
        
        String searchLower = input.toLowerCase();
        List<OrderItemDisplay> filteredItems = new ArrayList<>();
        
        Map<String, Integer> groupedItems = new LinkedHashMap<>();
        Map<String, OrderItem> representativeItems = new LinkedHashMap<>();
        BigDecimal totalSubtotal = BigDecimal.ZERO;
        
        for (OrderItem item : currentOrder.getOrderItems()) {
            MenuItem menuItem = menuItemDAO.getMenuItemById(item.getMenuId());
            String menuName;
            if (menuItem != null) {
                menuName = menuItem.getMenuName();
            } else {
                menuName = "Unknown (ID: " + item.getMenuId() + ")";
            }
            
            if (!menuName.toLowerCase().contains(searchLower)) {
                continue; // Skip items that don't match
            }
            
            boolean isActive = item.getStatus() != null && item.getStatus();
            String status = isActive ? "active" : "completed";
            
            if (isActive) {
                totalSubtotal = totalSubtotal.add(item.getSubtotal());
            }
            
            String key = menuName + "_" + status;
            
            if (groupedItems.containsKey(key)) {
                groupedItems.put(key, groupedItems.get(key) + item.getQuantity());
            } else {
                groupedItems.put(key, item.getQuantity());
                representativeItems.put(key, item);
            }
        }
        
        for (Map.Entry<String, Integer> entry : groupedItems.entrySet()) {
            String key = entry.getKey();
            int totalQuantity = entry.getValue();
            OrderItem representativeItem = representativeItems.get(key);
            
            // Extract menu name and status from key
            // Key format: "menuName_status" - find last underscore to separate
            int lastUnderscore = key.lastIndexOf("_");
            String menuName = key.substring(0, lastUnderscore);
            String status = key.substring(lastUnderscore + 1);
            
            OrderItemDisplay displayItem = new OrderItemDisplay(menuName, String.valueOf(totalQuantity), status, representativeItem);
            filteredItems.add(displayItem);
        }
        
        // Update table with filtered items
        final List<OrderItemDisplay> finalFilteredItems = filteredItems;
        final BigDecimal finalTotalSubtotal = totalSubtotal;
        
        Platform.runLater(() -> {
            orderItemsTable.setItems(FXCollections.observableArrayList(finalFilteredItems));
            if (subtotalLabel != null) {
                subtotalLabel.setText("Subtotal: $" + String.format("%.2f", finalTotalSubtotal));
            }
            orderItemsTable.refresh();
            
            if (filteredItems.isEmpty()) {
                SceneNavigator.showInfo("No items found matching: " + input);
            } else {
                SceneNavigator.showInfo("Found " + filteredItems.size() + " item(s) matching: " + input);
            }
        });
    }
    
    // ===================== DELETE =====================
    private void handleDelete() {
        OrderItemDisplay selected = orderItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select an order item to delete.");
            return;
        }
        
        OrderItem itemToDelete = selected.getOrderItem();
        if (itemToDelete == null || itemToDelete.getOrderItemId() <= 0) {
            SceneNavigator.showError("Could not find order item to delete.");
            return;
        }
        
        int currentQuantity = itemToDelete.getQuantity();
        
        // Ask for quantity to delete
        TextInputDialog qtyDialog = new TextInputDialog(String.valueOf(currentQuantity));
        qtyDialog.setTitle("Delete Order Item");
        qtyDialog.setHeaderText("Delete quantity for: " + selected.getMenuItemName());
        qtyDialog.setContentText("Current quantity: " + currentQuantity + "\nEnter quantity to delete:");
        Optional<String> qtyInput = qtyDialog.showAndWait();
        
        qtyInput.ifPresent(qtyStr -> {
            int deleteQuantity;
            try {
                deleteQuantity = Integer.parseInt(qtyStr);
                if (deleteQuantity <= 0) {
                    SceneNavigator.showError("Quantity must be greater than 0.");
                    return;
                }
                if (deleteQuantity > currentQuantity) {
                    SceneNavigator.showError("Cannot delete more than current quantity (" + currentQuantity + ").");
                    return;
                }
            } catch (NumberFormatException e) {
                SceneNavigator.showError("Quantity must be a number.");
                return;
            }
            
            boolean success = false;
            
            if (deleteQuantity == currentQuantity) {
                // Delete entire item
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Order Item");
                confirm.setHeaderText("Delete entire order item?");
                confirm.setContentText("Are you sure you want to delete all " + currentQuantity + " of " + selected.getMenuItemName() + "?");
                Optional<ButtonType> result = confirm.showAndWait();
                
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    success = orderitemDAO.deleteOrderItem(itemToDelete.getOrderItemId());
                } else {
                    return; // User cancelled
                }
            } else {
                // Partial deletion - update quantity
                int newQuantity = currentQuantity - deleteQuantity;
                
                // Get menu item to recalculate subtotal
                MenuItem menuItem = menuItemDAO.getMenuItemById(itemToDelete.getMenuId());
                if (menuItem == null) {
                    SceneNavigator.showError("Menu item not found.");
                    return;
                }
                
                BigDecimal newSubtotal = BigDecimal.valueOf(menuItem.getPrice()).multiply(BigDecimal.valueOf(newQuantity));
                
                // Update order item with new quantity
                itemToDelete.setQuantity(newQuantity);
                itemToDelete.setSubtotal(newSubtotal);
                success = orderitemDAO.updateOrderItem(itemToDelete);
            }
            
            if (success) {
                SceneNavigator.showInfo("Order item updated successfully!");
                refreshCurrentOrder();
            } else {
                SceneNavigator.showError("Failed to update order item.");
            }
        });
    }
    
    // ===================== EDIT =====================
    private void handleEdit() {
        OrderItemDisplay selected = orderItemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select an order item to edit.");
            return;
        }
        
        OrderItem representativeItem = selected.getOrderItem();
        if (representativeItem == null || representativeItem.getOrderItemId() <= 0) {
            SceneNavigator.showError("Could not find order item to edit.");
            return;
        }
        
        // Get all order items that match this menu item and status (since they're grouped)
        String menuName = selected.getMenuItemName();
        String currentStatus = selected.getActive();
        int totalQuantity = Integer.parseInt(selected.getQuantity());
        boolean currentIsActive = currentStatus.equals("active");
        
        // Find all OrderItems that match this menu item and status
        List<OrderItem> matchingItems = new ArrayList<>();
        if (currentOrder != null && currentOrder.getOrderItems() != null) {
            for (OrderItem item : currentOrder.getOrderItems()) {
                MenuItem menuItem = menuItemDAO.getMenuItemById(item.getMenuId());
                if (menuItem != null && menuItem.getMenuName().equals(menuName)) {
                    boolean itemIsActive = item.getStatus() != null && item.getStatus();
                    String itemStatus = itemIsActive ? "active" : "completed";
                    if (itemStatus.equals(currentStatus)) {
                        matchingItems.add(item);
                    }
                }
            }
        }
        
        if (matchingItems.isEmpty()) {
            SceneNavigator.showError("Could not find matching order items.");
            return;
        }
        
        // First, ask for quantity to toggle
        TextInputDialog quantityDialog = new TextInputDialog("1");
        quantityDialog.setTitle("Edit Order Item Status");
        quantityDialog.setHeaderText("Change status for: " + menuName + " (Current Qty: " + totalQuantity + ")");
        quantityDialog.setContentText("Enter quantity to toggle status:");
        Optional<String> quantityResult = quantityDialog.showAndWait();
        
        quantityResult.ifPresent(quantityStr -> {
            int quantityToToggle;
            try {
                quantityToToggle = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                SceneNavigator.showError("Please enter a valid number.");
                return;
            }
            
            if (quantityToToggle <= 0) {
                SceneNavigator.showError("Quantity must be greater than 0.");
                return;
            }
            
            if (quantityToToggle > totalQuantity) {
                SceneNavigator.showError("Quantity cannot exceed current quantity (" + totalQuantity + ").");
                return;
            }
            
            // Show dialog to select new status
            List<String> statusOptions = new ArrayList<>();
            statusOptions.add("active");
            statusOptions.add("completed");
            
            String newStatusDefault = currentIsActive ? "completed" : "active";
            
            ChoiceDialog<String> statusDialog = new ChoiceDialog<>(newStatusDefault, statusOptions);
            statusDialog.setTitle("Edit Order Item Status");
            statusDialog.setHeaderText("Change " + quantityToToggle + " of " + menuName + " to:");
            statusDialog.setContentText("Select status:");
            Optional<String> statusResult = statusDialog.showAndWait();
            
            statusResult.ifPresent(newStatus -> {
                boolean isActive = newStatus.equals("active");
                
                // Get menu item for price calculation
                MenuItem menuItem = menuItemDAO.getMenuItemById(representativeItem.getMenuId());
                if (menuItem == null) {
                    SceneNavigator.showError("Menu item not found.");
                    return;
                }
                
                BigDecimal itemPrice = BigDecimal.valueOf(menuItem.getPrice());
                
                // Process items to toggle
                int remainingToToggle = quantityToToggle;
                boolean allSuccess = true;
                
                for (OrderItem item : matchingItems) {
                    if (remainingToToggle <= 0) break;
                    
                    int itemQuantity = item.getQuantity();
                    
                    if (itemQuantity <= remainingToToggle) {
                        // Toggle entire item
                        item.setStatus(isActive);
                        item.setSubtotal(itemPrice.multiply(BigDecimal.valueOf(itemQuantity)));
                        if (!orderitemDAO.updateOrderItem(item)) {
                            allSuccess = false;
                        }
                        remainingToToggle -= itemQuantity;
                    } else {
                        // Partial toggle - split the item
                        int remainingQuantity = itemQuantity - remainingToToggle;
                        BigDecimal remainingSubtotal = itemPrice.multiply(BigDecimal.valueOf(remainingQuantity));
                        BigDecimal newItemSubtotal = itemPrice.multiply(BigDecimal.valueOf(remainingToToggle));
                        
                        // Update existing item
                        item.setQuantity(remainingQuantity);
                        item.setSubtotal(remainingSubtotal);
                        // Keep original status
                        
                        // Create new item with toggled status
                        OrderItem newItem = new OrderItem();
                        newItem.setOrderId(item.getOrderId());
                        newItem.setMenuId(item.getMenuId());
                        newItem.setQuantity(remainingToToggle);
                        newItem.setSubtotal(newItemSubtotal);
                        newItem.setStatus(isActive);
                        
                        if (!orderitemDAO.updateOrderItem(item)) {
                            allSuccess = false;
                        } else if (!orderitemDAO.addOrderItem(newItem)) {
                            allSuccess = false;
                        }
                        remainingToToggle = 0;
                    }
                }
                
                if (allSuccess) {
                    SceneNavigator.showInfo("Updated " + quantityToToggle + " item(s) to " + newStatus + "!");
                    refreshCurrentOrder();
                } else {
                    SceneNavigator.showError("Failed to update some order items.");
                }
            });
        });
    }
    
    private void refreshCurrentOrder() {
        if (currentOrder != null) {
            // Reload order from database
            Order refreshedOrder = OrderDB.getWholeOrder(currentOrder.getOrderId());
            if (refreshedOrder != null) {
                currentOrder = refreshedOrder;
                loadOrderItems(refreshedOrder);
            }
        }
    }

    private void openPaymentChoice() {
        if (currentOrder == null) {
            SceneNavigator.showError("No order selected. Please select a table first.");
            return;
        }
        
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

            // Pass order data to payment screen
            javafx.stage.Stage stage = (javafx.stage.Stage) payButton.getScene().getWindow();
            SceneNavigator.switchNoButton(stage, fxml, currentOrder);

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
        private final OrderItem orderItem; // Store reference to original OrderItem

        public OrderItemDisplay(String menuItemName, String quantity, String active, OrderItem orderItem) {
            this.menuItemName = new SimpleStringProperty(menuItemName);
            this.quantity = new SimpleStringProperty(quantity);
            this.active = new SimpleStringProperty(active);
            this.orderItem = orderItem;
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
        
        public OrderItem getOrderItem() {
            return orderItem;
        }
    }
}
