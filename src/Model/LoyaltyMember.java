package Model;

import java.time.LocalDate;

public class LoyaltyMember {

    // Private attributes
    private int memberId;
    private String name;
    private String contact;
    private LocalDate joinDate;
    private int points;
    private String status;

    // Constructor
    public LoyaltyMember(int memberId, String name, String contact, LocalDate joinDate, int points, String status) {
        this.memberId = memberId;
        this.name = name;
        this.contact = contact;
        this.joinDate = joinDate;
        this.points = points;
        this.status = status;
    }

    // Getters
    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public int getPoints() {
        return points;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
