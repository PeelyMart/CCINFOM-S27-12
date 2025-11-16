package UserInterface;

import DAO.TableDAO;
import Model.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Optional;

public class TableOptionsUI {

    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button backButton;
    @FXML
    private TextField searchField;

    @FXML
    private TableView<Table> tableTableView;
    @FXML
    private TableColumn<Table, Integer> tableIdColumn;
    @FXML
    private TableColumn<Table, Integer> capacityColumn;
    @FXML
    private TableColumn<Table, Boolean> isAvailableColumn;

    private TableDAO tableDAO = new TableDAO();
    private ObservableList<Table> tableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableView();
        loadAllTables();

        searchButton.setOnAction(e -> handleSearch());
        addButton.setOnAction(e -> handleAdd());
        removeButton.setOnAction(e -> handleDelete());
        updateButton.setOnAction(e -> handleUpdate());
        backButton.setOnAction(e -> goBack());
        
        if (searchField != null) {
            searchField.setOnAction(e -> handleSearch());
        }
    }

    private void setupTableView() {
        tableIdColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        isAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("tableStatus"));
        
        // Custom cell factory for boolean status
        isAvailableColumn.setCellFactory(column -> new TableCell<Table, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Available" : "Occupied");
                }
            }
        });
        
        tableTableView.setItems(tableList);
    }

    private void loadAllTables() {
        tableList.clear();
        List<Table> allTables = TableDAO.getAllTables();
        tableList.addAll(allTables);
    }

    @FXML
    public void goBack() {
        SceneNavigator.switchScene(backButton, "/Resources/MainMenu/dashboard.fxml");
    }

    @FXML
    private void searchButtonClick() {
        handleSearch();
    }

    @FXML
    private void searchFieldEnter() {
        handleSearch();
    }

    private void handleSearch() {
        String input = searchField.getText().trim();
        if (input.isEmpty()) {
            loadAllTables();
            return;
        }

        // Search by table ID, capacity, or status (partial match)
        List<Table> allTables = TableDAO.getAllTables();
        tableList.clear();
        String searchLower = input.toLowerCase();
        
        for (Table table : allTables) {
            boolean matches = false;
            
            // Check table ID
            if (String.valueOf(table.getTableId()).contains(input)) {
                matches = true;
            }
            // Check capacity
            else if (String.valueOf(table.getCapacity()).contains(input)) {
                matches = true;
            }
            // Check status
            else if ((table.getTableStatus() && "available".contains(searchLower)) ||
                     (!table.getTableStatus() && "occupied".contains(searchLower))) {
                matches = true;
            }
            
            if (matches) {
                tableList.add(table);
            }
        }
        
        if (tableList.isEmpty()) {
            SceneNavigator.showInfo("No tables found matching: " + input);
        }
    }

    @FXML
    private void addButtonClick() {
        handleAdd();
    }

    private void handleAdd() {
        TextInputDialog capacityDialog = new TextInputDialog();
        capacityDialog.setTitle("Add Table");
        capacityDialog.setHeaderText("Enter Table Capacity:");
        Optional<String> capacityInput = capacityDialog.showAndWait();

        capacityInput.ifPresent(capacityStr -> {
            try {
                int capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    SceneNavigator.showError("Capacity must be greater than 0.");
                    return;
                }

                Table newTable = TableDAO.addTable(capacity);
                if (newTable != null) {
                    SceneNavigator.showInfo("Table added successfully!");
                    loadAllTables();
                } else {
                    SceneNavigator.showError("Failed to add table.");
                }
            } catch (NumberFormatException e) {
                SceneNavigator.showError("Capacity must be a valid number.");
            }
        });
    }

    @FXML
    private void removeButtonClick() {
        handleDelete();
    }

    private void handleDelete() {
        Table selected = tableTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a table to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Table");
        confirmAlert.setHeaderText("Are you sure you want to delete this table?");
        confirmAlert.setContentText("Table ID: " + selected.getTableId() + "\nCapacity: " + selected.getCapacity());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (tableDAO.deleteTable(selected.getTableId())) {
                SceneNavigator.showInfo("Table deleted successfully.");
                loadAllTables();
            } else {
                SceneNavigator.showError("Failed to delete table.");
            }
        }
    }

    @FXML
    private void updateButtonClick() {
        handleUpdate();
    }

    private void handleUpdate() {
        Table selected = tableTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a table to update.");
            return;
        }

        // Reload from database
        Table table = tableDAO.getTableById(selected.getTableId());
        if (table == null) {
            SceneNavigator.showError("Table not found.");
            return;
        }
        // Set the table ID
        table.setTableId(selected.getTableId());

        TextInputDialog capacityDialog = new TextInputDialog(String.valueOf(table.getCapacity()));
        capacityDialog.setTitle("Update Table");
        capacityDialog.setHeaderText("Enter Table Capacity:");
        Optional<String> capacityInput = capacityDialog.showAndWait();

        capacityInput.ifPresent(capacityStr -> {
            try {
                int capacity = Integer.parseInt(capacityStr);
                if (capacity <= 0) {
                    SceneNavigator.showError("Capacity must be greater than 0.");
                    return;
                }

                ChoiceDialog<String> statusDialog = new ChoiceDialog<>(
                    table.getTableStatus() ? "Available" : "Occupied",
                    "Available", "Occupied");
                statusDialog.setTitle("Update Table");
                statusDialog.setHeaderText("Select Table Status:");
                Optional<String> statusInput = statusDialog.showAndWait();

                statusInput.ifPresent(statusStr -> {
                    table.setCapacity(capacity);
                    table.setTableStatus("Available".equals(statusStr));

                    if (tableDAO.updateTable(table)) {
                        SceneNavigator.showInfo("Table updated successfully.");
                        loadAllTables();
                    } else {
                        SceneNavigator.showError("Failed to update table.");
                    }
                });
            } catch (NumberFormatException e) {
                SceneNavigator.showError("Capacity must be a valid number.");
            }
        });
    }
}
