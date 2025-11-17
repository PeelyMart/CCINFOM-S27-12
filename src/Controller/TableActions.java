package Controller;

import DAO.OrderDB;
import DAO.OrderitemDAO;
import DAO.TableDAO;
import Model.OrderStatus;
import Model.Table;

public class TableActions {

    public static void closeTable(Table currTb, int customerID){
        int isPaid = PaymentControl.initiatePayment(OrderDB.getWholeOrderByTable(currTb.getTableId()), customerID);
        if(isPaid == 1) {
            currTb.setTableStatus(true);
        }
    }



    public static void initateTable(Table currTb, int staffInCharge){
        /*
            1. Access table
            2. Set as taken (update in database)
            3. call openOrder
         */
        currTb.setTableStatus(false); //table is now taken
        // Update table status in database
        TableDAO tableDAO = new TableDAO();
        tableDAO.updateTable(currTb);
        // Create the order
        OrderController.openOrder(currTb.getTableId(), staffInCharge);
    }
}
