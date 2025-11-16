package Controller;


import Model.Reservations;
import DAO.ReservationDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ReservationController {

    public static Reservations addReservation(int tableID, String name, LocalDateTime time){
        Reservations rs = new Reservations(-1, tableID, name, time, true);
        boolean result = ReservationDAO.addReservation(rs);
        if(result && rs.getRequestId() != -1){
            System.out.println("[DEBUG] Reservation complete request id: " + rs.getRequestId());
            return rs;
        }
        System.out.println("[DEBUG] Reservation failed" + rs.getRequestId()); //
        return null;
    }

    // For instance methods in DAO, create a DAO instance
    public static Reservations getReservation(int requestId) {
        ReservationDAO dao = new ReservationDAO();
        return dao.getReservationById(requestId);
    }

    public static boolean editReservation(int requestId, String newName, LocalDateTime newTime) {
        ReservationDAO dao = new ReservationDAO(); // create DAO instance
        Reservations existing = dao.getReservationById(requestId); // now this works
        if (existing == null) return false;

        existing.setReserveName(newName);

        return dao.updateReservation(existing);
    }


    public static boolean deleteReservation(int requestId) {
        ReservationDAO dao = new ReservationDAO();
        return dao.deleteReservation(requestId);
    }

    public static ArrayList<Reservations> getAllReservations() {
        ReservationDAO dao = new ReservationDAO();
        return dao.getAllReservations();
    }

    /**
     * This will check all active reservations and check if they are still valid.
     * If the rsvp time already passed, mark it inactive
     */
    public static void updateValidity(List<ArrayList> activeReservations){

    }




}
