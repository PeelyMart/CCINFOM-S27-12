package Model;

public class Staff {
    private int staff_id;
    private int staff_pin;
    private String first_name;
    private String last_name;
    private String contact_number;




    // Constructor
    public Staff(int staffId, int staff_pin, String first_name, String last_name, String contact_number) {
        this.staff_id = staffId;
        this.staff_pin = staff_pin;
        this.first_name = first_name;
        this.last_name = last_name;
        this.contact_number = contact_number;
    }

    // Getters
    public int getStaffId() {
        return staff_id;
    }

    public int getStaffPin() {
        return staff_pin;
    }

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }


    public String getContactNumber() {
        return contact_number;
    }



    // Setters
    public void changeFirstName(String firstName){
        this.first_name = firstName;
    }
    public void changeLastName(String lastName){
        this.last_name = lastName;
    }
    public void changePin(int newPin){
        this.staff_pin = newPin;
    }
    public void changeContactNumber(String contactNumber){
        this.contact_number = contactNumber;
    }
}
