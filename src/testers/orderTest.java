package testers;

import Controller.OrderController;
import DAO.OrderDB;

public class orderTest {
    public static void main(String[] args) {
        int orderId = OrderDB.openOrder(1, 2);
        if (orderId != -1){
            System.out.println("Your order_header is successfully uploaded: " +  orderId);

        }
        else{
            System.out.println("Something went wrong check db");
        }
    }
}

