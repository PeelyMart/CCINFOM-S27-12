package Controller;

import DAO.StaffDB;
import Model.Staff;

public class UserService {
    private static Staff current_user = null ;
    private static boolean isLoggedIn = false;

    /*
        Login method,
        @return 1 = incorrect username/user cannot be found
        @return 2 = incorrect password
        @return 0 = login
    */
    public static int logIn(int userID, int userPass){
        Staff retrieved = StaffDB.findById(userID);
        if(retrieved != null){
            if(retrieved.getStaffPin() == userPass) {
                current_user = retrieved;
                return 0;
            }
        }
        else{
            return 1 ;
        }
        return 2;
    }

    public static void logOut(){
        current_user = null;
        isLoggedIn = false;
    }

    public static Staff getCurrentUser(){
        return current_user;
    }



}
