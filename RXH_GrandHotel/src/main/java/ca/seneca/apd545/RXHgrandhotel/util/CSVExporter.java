package ca.seneca.apd545.RXHgrandhotel.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVExporter {

    /**
     * Requirement: Export reports to CSV[cite: 102, 206].
     * @param fileName Name of the file (e.g., "revenue_report.csv")
     * @param headers Table column headers
     * @param data List of string arrays representing rows
     */
    public static void export(String fileName, String[] headers, List<String[]> data) {
        try (FileWriter writer = new FileWriter(fileName)) {
            // Write Headers
            writer.append(String.join(",", headers)).append("\n");

            // Write Data Rows
            for (String[] row : data) {
                writer.append(String.join(",", row)).append("\n");
            }

            LoggerUtil.info("REPORT_EXPORTED - File: " + fileName);
        } catch (IOException e) {
            LoggerUtil.error("EXPORT_FAILED - Could not write to " + fileName, e);
        }
    }
}