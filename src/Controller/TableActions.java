package Controller;

import Model.Table;

public class TableActions {


    public static void initiateTable(Table currTb, int staffInCharge){
        /*
            TODO:
            1. Access table
            2. Set as taken
            3. call openOrder
            4.
         */
        currTb.setTableStatus(false); //table is now taken
        OrderController.openOrder(currTb.getTableId(), staffInCharge);
        //TODO decide what to output if we need the actual openOrder
    }
}
