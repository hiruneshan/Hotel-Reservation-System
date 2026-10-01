package ca.seneca.apd545.RXHgrandhotel.controller.admin;
import ca.seneca.apd545.RXHgrandhotel.model.dto.OccupancyReportDTO;
import ca.seneca.apd545.RXHgrandhotel.model.dto.RevenueReportDTO;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.util.CSVExporter;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import ca.seneca.apd545.RXHgrandhotel.util.PDFExporter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportsController {


    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private ComboBox<String> viewRangeCombo;
    @FXML private ComboBox<RoomType> roomTypeCombo;
    @FXML private DatePicker fromDatePicker, toDatePicker;
    @FXML private Button generateButton, exportCsvButton, exportPdfButton, closeButton;
    @FXML private TableView<RevenueReportDTO> revenueTable;
    @FXML private TableColumn<RevenueReportDTO, String> colRevPeriod;
    @FXML private TableColumn<RevenueReportDTO, Integer> colRevReservations;
    @FXML private TableColumn<RevenueReportDTO, Double> colRevSubtotal, colRevTax, colRevDiscounts, colRevTotal;
    @FXML private TableView<OccupancyReportDTO> occupancyTable;
    @FXML private TableColumn<OccupancyReportDTO, String> colOccDate;
    @FXML private TableColumn<OccupancyReportDTO, Integer> colOccAvailable, colOccOccupied;
    @FXML private TableColumn<OccupancyReportDTO, Double> colOccPercent;

    private final ReportingService reportingService;
    public ReportsController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        setupComboBoxes();
        setupTableColumns();

        generateButton.setOnAction(e -> handleGenerateReport());

        exportCsvButton.setOnAction(e -> handleExportCSV());
        exportPdfButton.setOnAction(e -> handleExportPDF());

        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());


        revenueTable.setVisible(true);
        occupancyTable.setVisible(false);
        closeButton.setOnAction(e -> {
            try {
                String fxmlPath = "/ca/seneca/apd545/RXHgrandhotel/view/admin/admin_dashboard.fxml";

                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
                loader.setControllerFactory(ca.seneca.apd545.RXHgrandhotel.app.AppConfig.getInstance()::createController);

                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage) closeButton.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Admin Dashboard");
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            } catch (NullPointerException npe) {
                System.err.println("Error: Could not find the FXML file. Check the path string.");
            }
        });
    }

    private void setupComboBoxes() {
        reportTypeCombo.setItems(FXCollections.observableArrayList("Revenue", "Occupancy"));
        viewRangeCombo.setItems(FXCollections.observableArrayList("Daily", "Weekly", "Monthly"));
        roomTypeCombo.setItems(FXCollections.observableArrayList(RoomType.values()));
    }

    private void setupTableColumns() {
        colRevPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
        colRevReservations.setCellValueFactory(new PropertyValueFactory<>("count"));
        colRevSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colRevTax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        colRevDiscounts.setCellValueFactory(new PropertyValueFactory<>("discounts"));
        colRevTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colOccDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOccAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
        colOccOccupied.setCellValueFactory(new PropertyValueFactory<>("occupied"));
        colOccPercent.setCellValueFactory(new PropertyValueFactory<>("percent"));
    }

    private void handleGenerateReport() {
        String type = reportTypeCombo.getValue();
        LocalDate start = fromDatePicker.getValue();
        LocalDate end = toDatePicker.getValue();

        if (type == null || start == null || end == null) {
            showAlert("Input Error", "Please select a report type and date range.");
            return;
        }

        if ("Revenue".equals(type)) {
            revenueTable.setVisible(true);
            occupancyTable.setVisible(false);
            revenueTable.setItems(FXCollections.observableArrayList(
                    reportingService.getRevenueData(start, end, roomTypeCombo.getValue())));
        } else {
            revenueTable.setVisible(false);
            occupancyTable.setVisible(true);
            occupancyTable.setItems(FXCollections.observableArrayList(
                    reportingService.getOccupancyData(start, end, roomTypeCombo.getValue())));
        }

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("Report Generated");
        success.setHeaderText(null);
        success.setContentText(type + " report for " + start + " to " + end + " has been loaded successfully.");
        success.show();

        LoggerUtil.info("REPORT_GENERATED - Type: " + type + " Range: " + start + " to " + end);
    }

    @FXML
    private void handleExportCSV() {
        String fileName;
        String[] headers;
        List<String[]> data;

        String type = reportTypeCombo.getValue();

        if ("Revenue".equals(type) && !revenueTable.getItems().isEmpty()) {
            fileName = "Revenue_Report_" + LocalDate.now() + ".csv";
            headers = getRevenueHeaders();
            data = getRevenueDataStrings();
        } else if ("Occupancy".equals(type) && !occupancyTable.getItems().isEmpty()) {
            fileName = "Occupancy_Report_" + LocalDate.now() + ".csv";
            headers = getOccupancyHeaders();
            data = getOccupancyDataStrings();
        } else {
            showAlert("Export Error", "Please generate a report before exporting.");
            return;
        }

        try {
            CSVExporter.export(fileName, headers, data);

            LoggerUtil.info("Admin", "EXPORT", "Report", type, "Exported " + fileName);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setContentText("Report exported successfully to " + fileName);
            success.show();
        } catch (Exception e) {
            LoggerUtil.error("Failed to export CSV", e);
            showAlert("System Error", "Could not write file. Check folder permissions.");
        }
    }

    @FXML
    private void handleExportPDF() {
        String fileName;
        String title;
        String[] headers;
        List<String[]> data;
        String type = reportTypeCombo.getValue();

        if (revenueTable.isVisible() && !revenueTable.getItems().isEmpty()) {
            fileName = "Revenue_Report_" + LocalDate.now() + ".pdf";
            title = "Monthly Revenue Summary";
            headers = getRevenueHeaders();
            data = getRevenueDataStrings();
        } else if (occupancyTable.isVisible() && !occupancyTable.getItems().isEmpty()) {
            fileName = "Occupancy_Report_" + LocalDate.now() + ".pdf";
            title = "Occupancy Status Report";
            headers = getOccupancyHeaders();
            data = getOccupancyDataStrings();
        } else {
            showAlert("Export Error", "No data loaded in the active table to export.");
            return;
        }

        try {

            PDFExporter.export(fileName, title, headers, data);


            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Export Successful");
            success.setHeaderText(null);
            success.setContentText("PDF Report '" + fileName + "' has been generated successfully.");
            success.show();

            LoggerUtil.info("Admin", "EXPORT", "PDF_Report", type, "File: " + fileName);
        } catch (Exception e) {
            LoggerUtil.error("Failed to export PDF", e);
            showAlert("System Error", "Could not create PDF. Ensure you have the required libraries (iText) in your pom.xml.");
        }
    }

    private String[] getRevenueHeaders() {
        return new String[]{"Period", "Count", "Subtotal", "Tax", "Discounts", "Total"};
    }

    private List<String[]> getRevenueDataStrings() {
        List<String[]> data = new ArrayList<>();
        for (RevenueReportDTO item : revenueTable.getItems()) {
            data.add(new String[]{
                    item.getPeriod(),
                    String.valueOf(item.getCount()),
                    String.format("%.2f", item.getSubtotal()),
                    String.format("%.2f", item.getTax()),
                    String.format("%.2f", item.getDiscounts()),
                    String.format("%.2f", item.getTotal())
            });
        }
        return data;
    }

    private String[] getOccupancyHeaders() {
        return new String[]{"Date", "Available", "Occupied", "Occupancy %"};
    }

    private List<String[]> getOccupancyDataStrings() {
        List<String[]> data = new ArrayList<>();
        for (OccupancyReportDTO item : occupancyTable.getItems()) {
            data.add(new String[]{
                    item.getDate(),
                    String.valueOf(item.getAvailable()),
                    String.valueOf(item.getOccupied()),
                    String.format("%.1f%%", item.getPercent())
            });
        }
        return data;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}