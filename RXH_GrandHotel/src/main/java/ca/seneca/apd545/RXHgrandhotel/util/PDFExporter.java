package ca.seneca.apd545.RXHgrandhotel.util;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;


public class PDFExporter {

    /**
     * Exports tabular data to a PDF file.
     * @param fileName The output path (e.g., "revenue_report.pdf")
     * @param title The report header [cite: 196, 199]
     * @param headers The column names for the table [cite: 101, 105]
     * @param data The rows of data to be printed
     */
    public static void export(String fileName, String title, String[] headers, List<String[]> data) {

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Add Report Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph p = new Paragraph(title, titleFont);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(new Paragraph(" ")); // Spacer

            // Requirement: Tabular only reporting (No Charts) [cite: 20, 98]
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            // Add Table Headers
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Add Table Data
            for (String[] row : data) {
                for (String cellData : row) {
                    table.addCell(cellData);
                }
            }

            document.add(table);


            LoggerUtil.info("REPORT_EXPORTED_PDF - File: " + fileName + " Title: " + title);

        } catch (DocumentException | FileNotFoundException e) {
            // Requirement: Log unexpected exceptions and persistence errors [cite: 183]
            LoggerUtil.severe("PDF_EXPORT_FAILED - " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }
}