package Controller;

import DAO.LoyaltymemberDAO;
import java.time.LocalDate;
import java.util.Map;

public class LoyaltyReportController {
    private final LoyaltymemberDAO dao;

    public LoyaltyReportController(){
        this.dao = new LoyaltymemberDAO();
    }

    /**
    *  returns number of new members by month
    */
    public Map<LocalDate, Integer> getNewMembersByMonth (LocalDate start, LocalDate end) {
        return dao.countNewMembersByMonth(start, end);
    }

    /**
    * returns number of new members by year 
    */
    public Map<LocalDate, Integer> getNewMemberByYear (LocalDate start, LocalDate end) {
        return dao.countNewMembersByYear(start, end);
    }
    /**
     * Generates a string report of new members by month.
     *
     * @param monthlyNewMembers A map with LocalDate as key and new member count as value.
     * @return Formatted report string.
     */
    public static String generateMonthlyMembersReport(Map<LocalDate, Integer> monthlyNewMembers) {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("            LOYALTY MEMBERS REPORT\n");
        sb.append("═══════════════════════════════════════════════════\n\n");

        if (monthlyNewMembers == null || monthlyNewMembers.isEmpty()) {
            sb.append("No new members in this period.\n");
            return sb.toString();
        }

        sb.append(String.format("%-15s | %10s\n", "Month", "New Members"));
        sb.append("─────────────────────────────────────\n");

        int totalMembers = 0;
        for (Map.Entry<LocalDate, Integer> entry : monthlyNewMembers.entrySet()) {
            totalMembers += entry.getValue();
            sb.append(String.format("%-15s | %10d\n", entry.getKey(), entry.getValue()));
        }

        sb.append("─────────────────────────────────────\n");
        sb.append(String.format("%-15s | %10d\n", "TOTAL", totalMembers));
        sb.append("═══════════════════════════════════════════════════\n");
        
        return sb.toString();
    }

}