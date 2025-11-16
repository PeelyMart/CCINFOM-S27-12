package UserInterface;

import Controller.ReservationController;
import DAO.ReservationDAO;
import Model.Reservations;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class reservationsUI {

    @FXML
    private Button addButton, searchButton, editButton, deleteButton, backButton;

    @FXML
    private TextField searchReservations;
    
    @FXML
    private TableView<ReservationDisplay> tableView;
    
    @FXML
    private TableColumn<ReservationDisplay, String> idColumn;
    
    @FXML
    private TableColumn<ReservationDisplay, String> tableIdColumn;
    
    @FXML
    private TableColumn<ReservationDisplay, String> reserveNameColumn;
    
    @FXML
    private TableColumn<ReservationDisplay, String> dateAndTimeColumn;
    
    @FXML
    private TableColumn<ReservationDisplay, String> isActiveColumn;
    
    private ObservableList<ReservationDisplay> reservationsList = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Integer currentTableId = null; // Store current table filter

    @FXML
    private void initialize() {
        // Setup TableView columns
        setupTableViewColumns();
        
        // Load all reservations
        loadAllReservations();

        addButton.setOnAction(e -> {
            handleAdd();
            loadAllReservations(); // Refresh table after add
        });
        searchButton.setOnAction(e -> handleSearch());
        editButton.setOnAction(e -> {
            handleEdit();
            loadAllReservations(); // Refresh table after edit
        });
        deleteButton.setOnAction(e -> {
            handleDelete();
            loadAllReservations(); // Refresh table after delete
        });

        if (backButton != null) {
            backButton.setOnAction(e ->
                    SceneNavigator.switchScene(backButton, "/Resources/Transactions/transactionMenu.fxml"));
        }
    }
    
    private void setupTableViewColumns() {
        if (idColumn != null) {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        }
        if (tableIdColumn != null) {
            tableIdColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        }
        if (reserveNameColumn != null) {
            reserveNameColumn.setCellValueFactory(new PropertyValueFactory<>("reserveName"));
        }
        if (dateAndTimeColumn != null) {
            dateAndTimeColumn.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));
        }
        if (isActiveColumn != null) {
            isActiveColumn.setCellValueFactory(new PropertyValueFactory<>("isActive"));
        }
        if (tableView != null) {
            tableView.setItems(reservationsList);
        }
    }
    
    private void loadAllReservations() {
        reservationsList.clear();
        ArrayList<Reservations> allReservations = ReservationController.getAllReservations();
        
        for (Reservations r : allReservations) {
            // Filter by table if currentTableId is set
            if (currentTableId != null && r.getTableId() != currentTableId) {
                continue;
            }
            
            String id = String.valueOf(r.getRequestId());
            String tableId = String.valueOf(r.getTableId());
            String reserveName = r.getReserveName();
            String dateAndTime = r.getDateAndTime().format(DATE_FORMATTER);
            String isActive = r.getIsActive() ? "Active" : "Inactive";
            
            reservationsList.add(new ReservationDisplay(id, tableId, reserveName, dateAndTime, isActive, r));
        }
    }
    
    /**
     * Called by SceneNavigator to set table filter
     */
    public void setData(Object data) {
        if (data instanceof Model.Table) {
            Model.Table table = (Model.Table) data;
            currentTableId = table.getTableId();
            loadAllReservations(); // Reload with table filter
        } else {
            currentTableId = null; // Show all reservations
            loadAllReservations();
        }
    }

    // ===================== ADD =====================
    @FXML
    private void handleAdd() {
        // Get all tables for dropdown
        List<Model.Table> allTables = DAO.TableDAO.getAllTables();
        if (allTables == null || allTables.isEmpty()) {
            SceneNavigator.showError("No tables available.");
            return;
        }
        
        // Create list of table options
        List<String> tableOptions = new ArrayList<>();
        for (Model.Table table : allTables) {
            tableOptions.add("Table " + table.getTableId() + " (Capacity: " + table.getCapacity() + ")");
        }
        
        // Show dropdown for table selection
        ChoiceDialog<String> tableDialog = new ChoiceDialog<>(tableOptions.get(0), tableOptions);
        tableDialog.setTitle("Add Reservation");
        tableDialog.setHeaderText("Select Table:");
        tableDialog.setContentText("Choose a table:");
        Optional<String> tableResult = tableDialog.showAndWait();

        tableResult.ifPresent(selectedTable -> {
            // Extract table ID from selection
            int tableID = Integer.parseInt(selectedTable.replaceAll("[^0-9]", "").split(" ")[0]);
            
            // Store in final variable for use in nested lambda
            final int finalTableID = tableID;

            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setTitle("Add Reservation");
            nameDialog.setHeaderText("Enter Customer Name:");
            Optional<String> nameInput = nameDialog.showAndWait();

            nameInput.ifPresent(name -> {
                // Get date and time with dropdowns
                LocalDateTime time = LocalDateTime.now().plusHours(1);
                
                // For simplicity, use current time + 1 hour, but you could add date/time pickers
                Reservations r = ReservationController.addReservation(finalTableID, name, time);

                if (r != null) {
                    SceneNavigator.showInfo(
                            "Reservation created successfully!\n" +
                                    "Reservation ID: " + r.getRequestId() + "\n" +
                                    "Name: " + r.getReserveName() + "\n" +
                                    "Table: " + r.getTableId() + "\n" +
                                    "Time: " + r.getDateAndTime().format(DATE_FORMATTER)
                    );
                } else {
                    SceneNavigator.showError("Reservation creation failed. Please try again.");
                }
            });
        });
    }

    // ===================== SEARCH =====================
    @FXML
    private void handleSearch() {
        String input = searchReservations.getText().trim();
        if (input.isEmpty()) {
            SceneNavigator.showError("Please enter a name or date to search.");
            return;
        }
        
        ArrayList<Reservations> allReservations = ReservationController.getAllReservations();
        ArrayList<Reservations> matchingReservations = new ArrayList<>();
        
        // Search by name or date
        String searchLower = input.toLowerCase();
        for (Reservations r : allReservations) {
            // Filter by table if set
            if (currentTableId != null && r.getTableId() != currentTableId) {
                continue;
            }
            
            // Check if name matches
            if (r.getReserveName().toLowerCase().contains(searchLower)) {
                matchingReservations.add(r);
                continue;
            }
            
            // Check if date matches
            String dateStr = r.getDateAndTime().format(DATE_FORMATTER);
            if (dateStr.contains(input)) {
                matchingReservations.add(r);
            }
        }
        
        if (matchingReservations.isEmpty()) {
            SceneNavigator.showError("No reservations found matching: " + input);
            return;
        }
        
        // Filter table view to show only matching reservations
        reservationsList.clear();
        for (Reservations r : matchingReservations) {
            String id = String.valueOf(r.getRequestId());
            String tableId = String.valueOf(r.getTableId());
            String reserveName = r.getReserveName();
            String dateAndTime = r.getDateAndTime().format(DATE_FORMATTER);
            String isActive = r.getIsActive() ? "Active" : "Inactive";
            
            reservationsList.add(new ReservationDisplay(id, tableId, reserveName, dateAndTime, isActive, r));
        }
        
        SceneNavigator.showInfo("Found " + matchingReservations.size() + " reservation(s) matching: " + input);
    }

    // ===================== EDIT =====================
    @FXML
    private void handleEdit() {
        ReservationDisplay selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a reservation to edit.");
            return;
        }
        
        Reservations reservation = selected.getReservation();
        if (reservation == null) {
            SceneNavigator.showError("Could not find reservation to edit.");
            return;
        }

        TextInputDialog nameDialog = new TextInputDialog(reservation.getReserveName());
        nameDialog.setTitle("Edit Reservation");
        nameDialog.setHeaderText("Enter new Customer Name:");
        Optional<String> nameInput = nameDialog.showAndWait();

        nameInput.ifPresent(newName -> {
            LocalDateTime newTime = reservation.getDateAndTime(); // Keep original time
            boolean success = ReservationController.editReservation(reservation.getRequestId(), newName, newTime);

            if (success) {
                SceneNavigator.showInfo("Reservation " + reservation.getRequestId() + " updated successfully.");
            } else {
                SceneNavigator.showError("Update failed. Reservation may not exist.");
            }
        });
    }

    // ===================== DELETE =====================
    @FXML
    private void handleDelete() {
        ReservationDisplay selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a reservation to delete.");
            return;
        }
        
        Reservations reservation = selected.getReservation();
        if (reservation == null) {
            SceneNavigator.showError("Could not find reservation to delete.");
            return;
        }

        // Confirm deletion
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Reservation");
        confirm.setHeaderText("Delete reservation?");
        confirm.setContentText("Are you sure you want to delete reservation for: " + reservation.getReserveName() + "?");
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = ReservationController.deleteReservation(reservation.getRequestId());

            if (success) {
                SceneNavigator.showInfo("Reservation " + reservation.getRequestId() + " deleted successfully.");
            } else {
                SceneNavigator.showError("Deletion failed. Reservation may not exist.");
            }
        }
    }
    
    /**
     * Display model for reservations in the TableView
     */
    public static class ReservationDisplay {
        private final String id;
        private final String tableId;
        private final String reserveName;
        private final String dateAndTime;
        private final String isActive;
        private final Reservations reservation; // Store reference to original Reservation
        
        public ReservationDisplay(String id, String tableId, String reserveName, String dateAndTime, String isActive, Reservations reservation) {
            this.id = id;
            this.tableId = tableId;
            this.reserveName = reserveName;
            this.dateAndTime = dateAndTime;
            this.isActive = isActive;
            this.reservation = reservation;
        }
        
        public String getId() { return id; }
        public String getTableId() { return tableId; }
        public String getReserveName() { return reserveName; }
        public String getDateAndTime() { return dateAndTime; }
        public String getIsActive() { return isActive; }
        public Reservations getReservation() { return reservation; }
    }
}
