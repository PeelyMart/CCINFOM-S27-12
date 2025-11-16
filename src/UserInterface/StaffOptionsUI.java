package UserInterface;

import DAO.StaffDB;
import Model.Staff;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;
import java.util.Optional;

public class StaffOptionsUI {

    @FXML
    private Button searchStaffButton;
    @FXML
    private Button addStaffButton;
    @FXML
    private Button removeStaffButton;
    @FXML
    private Button updateStaffButton;
    @FXML
    private TextField searchStaff;
    @FXML
    private Button backButton;
    
    @FXML
    private TableView<Staff> staffTable;
    @FXML
    private TableColumn<Staff, Integer> staffIdColumn;
    @FXML
    private TableColumn<Staff, String> firstNameColumn;
    @FXML
    private TableColumn<Staff, String> lastNameColumn;
    @FXML
    private TableColumn<Staff, String> contactColumn;

    private ObservableList<Staff> staffList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableView();
        loadAllStaff();

        searchStaffButton.setOnAction(e -> handleSearch());
        addStaffButton.setOnAction(e -> handleAdd());
        removeStaffButton.setOnAction(e -> handleDelete());
        updateStaffButton.setOnAction(e -> handleUpdate());
        backButton.setOnAction(e -> goBack());
        
        if (searchStaff != null) {
            searchStaff.setOnAction(e -> handleSearch());
        }
    }

    private void setupTableView() {
        staffIdColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        
        staffTable.setItems(staffList);
    }

    private void loadAllStaff() {
        staffList.clear();
        List<Staff> allStaff = StaffDB.returnAllStaff();
        staffList.addAll(allStaff);
    }

    @FXML
    public void goBack() {
        SceneNavigator.switchScene(backButton, "/Resources/MainMenu/dashboard.fxml");
    }

    @FXML
    private void searchStaffButtonClick() {
        handleSearch();
    }

    @FXML
    private void searchStaffEntries() {
        handleSearch();
    }

    private void handleSearch() {
        String input = searchStaff.getText().trim();
        if (input.isEmpty()) {
            loadAllStaff();
            return;
        }

        // Try parsing as ID first
        try {
            int staffId = Integer.parseInt(input);
            Staff staff = StaffDB.findById(staffId);
            if (staff != null) {
                staffList.clear();
                staffList.add(staff);
                return;
            }
        } catch (NumberFormatException e) {
            // Not a number, search by name or other fields
        }
        
        // Search by name, contact (partial match)
        List<Staff> allStaff = StaffDB.returnAllStaff();
        staffList.clear();
        String searchLower = input.toLowerCase();
        
        for (Staff staff : allStaff) {
            boolean matches = false;
            
            // Check staff ID
            if (String.valueOf(staff.getStaffId()).contains(input)) {
                matches = true;
            }
            // Check first name (partial)
            else if (staff.getFirstName() != null && staff.getFirstName().toLowerCase().contains(searchLower)) {
                matches = true;
            }
            // Check last name (partial)
            else if (staff.getLastName() != null && staff.getLastName().toLowerCase().contains(searchLower)) {
                matches = true;
            }
            // Check contact (partial)
            else if (staff.getContactNumber() != null && staff.getContactNumber().toLowerCase().contains(searchLower)) {
                matches = true;
            }
            
            if (matches) {
                staffList.add(staff);
            }
        }
        
        if (staffList.isEmpty()) {
            SceneNavigator.showInfo("No staff found matching: " + input);
        }
    }

    @FXML
    private void addStaffButtonClick() {
        handleAdd();
    }

    private void handleAdd() {
        TextInputDialog firstNameDialog = new TextInputDialog();
        firstNameDialog.setTitle("Add Staff");
        firstNameDialog.setHeaderText("Enter First Name:");
        Optional<String> firstNameInput = firstNameDialog.showAndWait();

        firstNameInput.ifPresent(firstName -> {
            TextInputDialog lastNameDialog = new TextInputDialog();
            lastNameDialog.setTitle("Add Staff");
            lastNameDialog.setHeaderText("Enter Last Name:");
            Optional<String> lastNameInput = lastNameDialog.showAndWait();

            lastNameInput.ifPresent(lastName -> {
                TextInputDialog contactDialog = new TextInputDialog();
                contactDialog.setTitle("Add Staff");
                contactDialog.setHeaderText("Enter Contact Number:");
                Optional<String> contactInput = contactDialog.showAndWait();

                contactInput.ifPresent(contact -> {
                    TextInputDialog pinDialog = new TextInputDialog();
                    pinDialog.setTitle("Add Staff");
                    pinDialog.setHeaderText("Enter Staff PIN:");
                    Optional<String> pinInput = pinDialog.showAndWait();

                    pinInput.ifPresent(pinStr -> {
                        try {
                            int pin = Integer.parseInt(pinStr);
                            
                            // Create a temporary Staff object with ID 0 (will be set by database)
                            Staff newStaff = new Staff(0, pin, firstName, lastName, contact);
                            
                            if (StaffDB.addStaff(newStaff)) {
                                SceneNavigator.showInfo("Staff added successfully!");
                                loadAllStaff();
                            } else {
                                SceneNavigator.showError("Failed to add staff.");
                            }
                        } catch (NumberFormatException e) {
                            SceneNavigator.showError("PIN must be a valid number.");
                        }
                    });
                });
            });
        });
    }

    @FXML
    private void removeStaffButtonClick() {
        handleDelete();
    }

    private void handleDelete() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a staff member to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Staff");
        confirmAlert.setHeaderText("Are you sure you want to delete this staff member?");
        confirmAlert.setContentText("ID: " + selected.getStaffId() + "\nName: " + selected.getFirstName() + " " + selected.getLastName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (StaffDB.deleteStaff(selected.getStaffId())) {
                SceneNavigator.showInfo("Staff deleted successfully.");
                loadAllStaff();
            } else {
                SceneNavigator.showError("Failed to delete staff.");
            }
        }
    }

    @FXML
    private void updateStaffButtonClick() {
        handleUpdate();
    }

    private void handleUpdate() {
        Staff selected = staffTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a staff member to update.");
            return;
        }

        // Reload from database
        Staff staff = StaffDB.findById(selected.getStaffId());
        if (staff == null) {
            SceneNavigator.showError("Staff not found.");
            return;
        }

        TextInputDialog firstNameDialog = new TextInputDialog(staff.getFirstName());
        firstNameDialog.setTitle("Update Staff");
        firstNameDialog.setHeaderText("Enter First Name:");
        Optional<String> firstNameInput = firstNameDialog.showAndWait();

        firstNameInput.ifPresent(firstName -> {
            TextInputDialog lastNameDialog = new TextInputDialog(staff.getLastName());
            lastNameDialog.setTitle("Update Staff");
            lastNameDialog.setHeaderText("Enter Last Name:");
            Optional<String> lastNameInput = lastNameDialog.showAndWait();

            lastNameInput.ifPresent(lastName -> {
                TextInputDialog contactDialog = new TextInputDialog(staff.getContactNumber());
                contactDialog.setTitle("Update Staff");
                contactDialog.setHeaderText("Enter Contact Number:");
                Optional<String> contactInput = contactDialog.showAndWait();

                contactInput.ifPresent(contact -> {
                    TextInputDialog pinDialog = new TextInputDialog(String.valueOf(staff.getStaffPin()));
                    pinDialog.setTitle("Update Staff");
                    pinDialog.setHeaderText("Enter Staff PIN:");
                    Optional<String> pinInput = pinDialog.showAndWait();

                    pinInput.ifPresent(pinStr -> {
                        try {
                            int pin = Integer.parseInt(pinStr);
                            
                            staff.changeFirstName(firstName);
                            staff.changeLastName(lastName);
                            staff.changeContactNumber(contact);
                            staff.changePin(pin);

                            if (StaffDB.updateStaff(staff)) {
                                SceneNavigator.showInfo("Staff updated successfully.");
                                loadAllStaff();
                            } else {
                                SceneNavigator.showError("Failed to update staff.");
                            }
                        } catch (NumberFormatException e) {
                            SceneNavigator.showError("PIN must be a valid number.");
                        }
                    });
                });
            });
        });
    }
}
