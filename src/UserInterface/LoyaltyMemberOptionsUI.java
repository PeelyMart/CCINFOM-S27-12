package UserInterface;

import DAO.LoyaltymemberDAO;
import Model.LoyaltyMember;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Pair;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class LoyaltyMemberOptionsUI {

    @FXML
    private Button searchLoyaltyButton;
    @FXML
    private Button addLoyaltyButton;
    @FXML
    private Button removeLoyaltyButton;
    @FXML
    private Button updateLoyaltyButton;
    @FXML
    private TextField searchMemberID;
    @FXML
    private Button backButton;
    
    @FXML
    private TableView<LoyaltyMemberDisplay> loyaltyTable;
    @FXML
    private TableColumn<LoyaltyMemberDisplay, String> customerIdColumn;
    @FXML
    private TableColumn<LoyaltyMemberDisplay, String> firstNameColumn;
    @FXML
    private TableColumn<LoyaltyMemberDisplay, String> lastNameColumn;
    @FXML
    private TableColumn<LoyaltyMemberDisplay, String> contactColumn;
    @FXML
    private TableColumn<LoyaltyMemberDisplay, String> joinDateColumn;
    @FXML
    private TableColumn<LoyaltyMemberDisplay, String> pointsColumn;
    @FXML
    private TableColumn<LoyaltyMemberDisplay, String> statusColumn;

    private LoyaltymemberDAO loyaltyDAO = new LoyaltymemberDAO();
    private ObservableList<LoyaltyMemberDisplay> loyaltyList = FXCollections.observableArrayList();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        setupTableView();
        loadAllLoyaltyMembers();

        searchLoyaltyButton.setOnAction(e -> handleSearch());
        addLoyaltyButton.setOnAction(e -> handleAdd());
        removeLoyaltyButton.setOnAction(e -> handleDelete());
        updateLoyaltyButton.setOnAction(e -> handleUpdate());
        backButton.setOnAction(e -> goBack());
    }

    private void setupTableView() {
        customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        joinDateColumn.setCellValueFactory(new PropertyValueFactory<>("joinDate"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        loyaltyTable.setItems(loyaltyList);
    }

    private void loadAllLoyaltyMembers() {
        loyaltyList.clear();
        ArrayList<LoyaltyMember> allMembers = loyaltyDAO.getAllLoyaltyMembers();
        
        for (LoyaltyMember member : allMembers) {
            String[] nameParts = member.getName().trim().split("\\s+", 2);
            String firstName = nameParts.length > 0 ? nameParts[0] : "";
            String lastName = nameParts.length > 1 ? nameParts[1] : "";
            
            LoyaltyMemberDisplay display = new LoyaltyMemberDisplay(
                String.valueOf(member.getCustomerId()),
                firstName,
                lastName,
                member.getContact() != null ? member.getContact() : "",
                member.getJoinDate() != null ? member.getJoinDate().format(dateFormatter) : "",
                String.valueOf(member.getPoints()),
                member.getStatus() != null ? member.getStatus() : "inactive",
                member
            );
            loyaltyList.add(display);
        }
    }

    @FXML
    public void goBack() {
        SceneNavigator.switchScene(backButton, "/Resources/MainMenu/dashboard.fxml");
    }

    @FXML
    private void searchLoyaltyButtonClick() {
        handleSearch();
    }

    @FXML
    private void searchMemberID() {
        handleSearch();
    }

    private void handleSearch() {
        String input = searchMemberID.getText().trim();
        if (input.isEmpty()) {
            loadAllLoyaltyMembers();
            return;
        }

        try {
            int memberId = Integer.parseInt(input);
            LoyaltyMember member = loyaltyDAO.getLoyaltyMemberById(memberId);
            if (member != null) {
                loyaltyList.clear();
                String[] nameParts = member.getName().trim().split("\\s+", 2);
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                
                LoyaltyMemberDisplay display = new LoyaltyMemberDisplay(
                    String.valueOf(member.getCustomerId()),
                    firstName,
                    lastName,
                    member.getContact() != null ? member.getContact() : "",
                    member.getJoinDate() != null ? member.getJoinDate().format(dateFormatter) : "",
                    String.valueOf(member.getPoints()),
                    member.getStatus() != null ? member.getStatus() : "inactive",
                    member
                );
                loyaltyList.add(display);
                return;
            }
        } catch (NumberFormatException e) {
        }
        
        ArrayList<LoyaltyMember> allMembers = loyaltyDAO.getAllLoyaltyMembers();
        loyaltyList.clear();
        String searchLower = input.toLowerCase();
        
        for (LoyaltyMember member : allMembers) {
            boolean matches = false;
            
            if (String.valueOf(member.getCustomerId()).contains(input)) {
                matches = true;
            }
            else if (member.getName() != null && member.getName().toLowerCase().contains(searchLower)) {
                matches = true;
            }
            else if (member.getContact() != null && member.getContact().toLowerCase().contains(searchLower)) {
                matches = true;
            }
            else if (member.getStatus() != null && member.getStatus().toLowerCase().contains(searchLower)) {
                matches = true;
            }
            
            if (matches) {
                String[] nameParts = member.getName().trim().split("\\s+", 2);
                String firstName = nameParts.length > 0 ? nameParts[0] : "";
                String lastName = nameParts.length > 1 ? nameParts[1] : "";
                
                LoyaltyMemberDisplay display = new LoyaltyMemberDisplay(
                    String.valueOf(member.getCustomerId()),
                    firstName,
                    lastName,
                    member.getContact() != null ? member.getContact() : "",
                    member.getJoinDate() != null ? member.getJoinDate().format(dateFormatter) : "",
                    String.valueOf(member.getPoints()),
                    member.getStatus() != null ? member.getStatus() : "inactive",
                    member
                );
                loyaltyList.add(display);
            }
        }
        
        if (loyaltyList.isEmpty()) {
            SceneNavigator.showInfo("No loyalty members found matching: " + input);
        }
    }

    @FXML
    private void addLoyaltyButtonClick() {
        handleAdd();
    }

    private void handleAdd() {
        TextInputDialog firstNameDialog = new TextInputDialog();
        firstNameDialog.setTitle("Add Loyalty Member");
        firstNameDialog.setHeaderText("Enter First Name:");
        Optional<String> firstNameInput = firstNameDialog.showAndWait();

        firstNameInput.ifPresent(firstName -> {
            TextInputDialog lastNameDialog = new TextInputDialog();
            lastNameDialog.setTitle("Add Loyalty Member");
            lastNameDialog.setHeaderText("Enter Last Name:");
            Optional<String> lastNameInput = lastNameDialog.showAndWait();

            lastNameInput.ifPresent(lastName -> {
                TextInputDialog contactDialog = new TextInputDialog();
                contactDialog.setTitle("Add Loyalty Member");
                contactDialog.setHeaderText("Enter Contact Number:");
                Optional<String> contactInput = contactDialog.showAndWait();

                contactInput.ifPresent(contact -> {
                    LoyaltyMember newMember = new LoyaltyMember();
                    newMember.setName(firstName + " " + lastName);
                    newMember.setContact(contact);
                    newMember.setJoinDate(LocalDate.now());
                    newMember.setPoints(0);
                    newMember.setStatus("active");

                    if (loyaltyDAO.addLoyaltyMember(newMember)) {
                        SceneNavigator.showInfo("Loyalty member added successfully!");
                        loadAllLoyaltyMembers();
                    } else {
                        SceneNavigator.showError("Failed to add loyalty member.");
                    }
                });
            });
        });
    }

    @FXML
    private void removeLoyaltyButtonClick() {
        handleDelete();
    }

    private void handleDelete() {
        LoyaltyMemberDisplay selected = loyaltyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a loyalty member to delete.");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Loyalty Member");
        confirmAlert.setHeaderText("Are you sure you want to delete this loyalty member?");
        confirmAlert.setContentText("ID: " + selected.getCustomerId() + "\nName: " + selected.getFirstName() + " " + selected.getLastName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (loyaltyDAO.deleteLoyaltyMember(selected.getLoyaltyMember().getCustomerId())) {
                SceneNavigator.showInfo("Loyalty member deleted successfully.");
                loadAllLoyaltyMembers();
            } else {
                SceneNavigator.showError("Failed to delete loyalty member.");
            }
        }
    }

    @FXML
    private void updateLoyaltyButtonClick() {
        handleUpdate();
    }

    private void handleUpdate() {
        LoyaltyMemberDisplay selected = loyaltyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            SceneNavigator.showError("Please select a loyalty member to update.");
            return;
        }

        LoyaltyMember member = selected.getLoyaltyMember();
        // Reload from database
        LoyaltyMember updatedMember = loyaltyDAO.getLoyaltyMemberById(member.getCustomerId());
        if (updatedMember == null) {
            SceneNavigator.showError("Loyalty member not found.");
            return;
        }

        TextInputDialog firstNameDialog = new TextInputDialog(selected.getFirstName());
        firstNameDialog.setTitle("Update Loyalty Member");
        firstNameDialog.setHeaderText("Enter First Name:");
        Optional<String> firstNameInput = firstNameDialog.showAndWait();

        firstNameInput.ifPresent(firstName -> {
            TextInputDialog lastNameDialog = new TextInputDialog(selected.getLastName());
            lastNameDialog.setTitle("Update Loyalty Member");
            lastNameDialog.setHeaderText("Enter Last Name:");
            Optional<String> lastNameInput = lastNameDialog.showAndWait();

            lastNameInput.ifPresent(lastName -> {
                TextInputDialog contactDialog = new TextInputDialog(updatedMember.getContact());
                contactDialog.setTitle("Update Loyalty Member");
                contactDialog.setHeaderText("Enter Contact Number:");
                Optional<String> contactInput = contactDialog.showAndWait();

                contactInput.ifPresent(contact -> {
                    TextInputDialog pointsDialog = new TextInputDialog(String.valueOf(updatedMember.getPoints()));
                    pointsDialog.setTitle("Update Loyalty Member");
                    pointsDialog.setHeaderText("Enter Points:");
                    Optional<String> pointsInput = pointsDialog.showAndWait();

                    pointsInput.ifPresent(pointsStr -> {
                        try {
                            int points = Integer.parseInt(pointsStr);
                            
                            DatePicker datePicker = new DatePicker(
                                updatedMember.getJoinDate() != null ? updatedMember.getJoinDate() : LocalDate.now());
                            datePicker.setPromptText("Select Join Date");
                            
                            GridPane dateGrid = new GridPane();
                            dateGrid.setHgap(10);
                            dateGrid.setVgap(10);
                            dateGrid.setPadding(new Insets(20, 150, 10, 10));
                            dateGrid.add(new Label("Join Date:"), 0, 0);
                            dateGrid.add(datePicker, 1, 0);
                            
                            Dialog<Pair<LocalDate, String>> dateDialog = new Dialog<>();
                            dateDialog.setTitle("Update Loyalty Member");
                            dateDialog.setHeaderText("Select Join Date:");
                            dateDialog.getDialogPane().setContent(dateGrid);
                            
                            ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                            dateDialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
                            
                            dateDialog.setResultConverter(dialogButton -> {
                                if (dialogButton == okButtonType) {
                                    LocalDate joinDate = datePicker.getValue();
                                    if (joinDate == null) {
                                        SceneNavigator.showError("Please select a join date.");
                                        return null;
                                    }
                                    return new Pair<>(joinDate, null);
                                }
                                return null;
                            });
                            
                            Optional<Pair<LocalDate, String>> dateResult = dateDialog.showAndWait();
                            
                            dateResult.ifPresent(datePair -> {
                                LocalDate joinDate = datePair.getKey();
                                if (joinDate == null) return;
                                
                                ChoiceDialog<String> statusDialog = new ChoiceDialog<>(
                                    updatedMember.getStatus() != null && updatedMember.getStatus().equalsIgnoreCase("active") ? "active" : "inactive",
                                    "active", "inactive");
                                statusDialog.setTitle("Update Loyalty Member");
                                statusDialog.setHeaderText("Select Status:");
                                Optional<String> statusInput = statusDialog.showAndWait();
                                
                                statusInput.ifPresent(status -> {
                                    updatedMember.setName(firstName + " " + lastName);
                                    updatedMember.setContact(contact);
                                    updatedMember.setPoints(points);
                                    updatedMember.setJoinDate(joinDate);
                                    updatedMember.setStatus(status);

                                    if (loyaltyDAO.updateLoyaltyMember(updatedMember)) {
                                        SceneNavigator.showInfo("Loyalty member updated successfully.");
                                        loadAllLoyaltyMembers();
                                    } else {
                                        SceneNavigator.showError("Failed to update loyalty member.");
                                    }
                                });
                            });
                        } catch (NumberFormatException e) {
                            SceneNavigator.showError("Points must be a valid number.");
                        }
                    });
                });
            });
        });
    }

    public static class LoyaltyMemberDisplay {
        private String customerId;
        private String firstName;
        private String lastName;
        private String contact;
        private String joinDate;
        private String points;
        private String status;
        private LoyaltyMember loyaltyMember;

        public LoyaltyMemberDisplay(String customerId, String firstName, String lastName, 
                                   String contact, String joinDate, String points, String status,
                                   LoyaltyMember loyaltyMember) {
            this.customerId = customerId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.contact = contact;
            this.joinDate = joinDate;
            this.points = points;
            this.status = status;
            this.loyaltyMember = loyaltyMember;
        }

        public String getCustomerId() { return customerId; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getContact() { return contact; }
        public String getJoinDate() { return joinDate; }
        public String getPoints() { return points; }
        public String getStatus() { return status; }
        public LoyaltyMember getLoyaltyMember() { return loyaltyMember; }
    }
}
