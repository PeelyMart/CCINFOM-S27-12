package Controller;

import DAO.PaymentDAO;
import Model.Payment;
import Model.PaymentReport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class SalesReportController {

   public static void getTransactionReport(List<PaymentReport> list){
       if(!list.isEmpty()){
           for(PaymentReport row: list){
               System.out.println(row.getType() + "\n" + row.getTransactions() + "\n" + row.getTotal());
           }
       }else{
            System.out.print("list is empty");
       }

   }

    public static String getTransactionReportAsString(List<PaymentReport> list) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("              SALES REPORT\n");
        sb.append("═══════════════════════════════════════════════════\n\n");

        if (!list.isEmpty()) {
            // Calculate totals
            int totalTransactions = 0;
            BigDecimal grandTotal = BigDecimal.ZERO;
            
            sb.append(String.format("%-20s | %12s | %15s\n", "Payment Method", "Transactions", "Total Amount"));
            sb.append("───────────────────────────────────────────────────────\n");
            
            for (PaymentReport row : list) {
                totalTransactions += row.getTransactions();
                grandTotal = grandTotal.add(row.getTotal() != null ? row.getTotal() : BigDecimal.ZERO);
                sb.append(String.format("%-20s | %12d | $%14.2f\n", 
                    row.getType(), row.getTransactions(), 
                    row.getTotal() != null ? row.getTotal().doubleValue() : 0.0));
            }
            
            sb.append("───────────────────────────────────────────────────────\n");
            sb.append(String.format("%-20s | %12d | $%14.2f\n", "TOTAL", totalTransactions, grandTotal.doubleValue()));
            sb.append("═══════════════════════════════════════════════════\n");
        } else {
            sb.append("No transaction data available.\n");
        }

        return sb.toString();
    }

    // Example helpers to get start/end for day/month/year
    public static LocalDateTime getDayStart(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    public static LocalDateTime getDayEnd(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 23, 59, 59);
    }

    public static LocalDateTime getMonthStart(int year, int month) {
        return LocalDateTime.of(year, month, 1, 0, 0);
    }

    public static LocalDateTime getMonthEnd(int year, int month) {
        int lastDay = java.time.YearMonth.of(year, month).lengthOfMonth();
        return LocalDateTime.of(year, month, lastDay, 23, 59, 59);
    }

    public static LocalDateTime getYearStart(int year) {
        return LocalDateTime.of(year, 1, 1, 0, 0);
    }

    public static LocalDateTime getYearEnd(int year) {
        return LocalDateTime.of(year, 12, 31, 23, 59, 59);
    }
}