package Model;

public class Staff {
    private int staffId;
    private String name;
    private String email;
    private String contact;
    private String position;
    private String managerPin;

    // Constructor
    public Staff(int staffId, String name, String email, String contact, String position, String managerPin) {
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.position = position;
        this.managerPin = managerPin;
    }

    // Getters
    public int getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getContact() {
        return contact;
    }

    public String getPosition() {
        return position;
    }

    public String getManagerPin() {
        return managerPin;
    }

    // Setters
    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setManagerPin(String managerPin) {
        this.managerPin = managerPin;
    }
}
