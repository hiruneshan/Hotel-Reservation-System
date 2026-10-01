package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.ActivityLog;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class ActivityLogController {

    @FXML private TableView<ActivityLog> logTable;
    @FXML private TableColumn<ActivityLog, LocalDateTime> colLogTimestamp;
    @FXML private TableColumn<ActivityLog, String> colLogActor;
    @FXML private TableColumn<ActivityLog, String> colLogAction;
    @FXML private TableColumn<ActivityLog, String> colLogEntityType;
    @FXML private TableColumn<ActivityLog, String> colLogEntityId;
    @FXML private TableColumn<ActivityLog, String> colLogMessage;
    @FXML private Button closeButton;
    @FXML private Button searchButton;
    @FXML private TextField actorFilterField;
    @FXML private TextField actionFilterField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private Button exportCsvButton, exportTxtButton;

    private final ReportingService reportingService;

    public ActivityLogController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        colLogTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colLogActor.setCellValueFactory(new PropertyValueFactory<>("actor"));
        colLogAction.setCellValueFactory(new PropertyValueFactory<>("action"));
        colLogEntityType.setCellValueFactory(new PropertyValueFactory<>("entityType"));
        colLogEntityId.setCellValueFactory(new PropertyValueFactory<>("entityId"));
        colLogMessage.setCellValueFactory(new PropertyValueFactory<>("message"));


        logTable.setItems(FXCollections.observableArrayList(reportingService.getActivityLogs()));

        searchButton.setOnAction(e -> {
            String actorText = actorFilterField.getText() == null ? "" : actorFilterField.getText().toLowerCase();
            String actionText = actionFilterField.getText() == null ? "" : actionFilterField.getText().toLowerCase();

            java.time.LocalDate startDate = fromDatePicker.getValue();
            java.time.LocalDate endDate = toDatePicker.getValue();

            var logs = reportingService.getActivityLogs().stream()
                    .filter(log -> actorText.isEmpty() || log.getActor().toLowerCase().contains(actorText))
                    .filter(log -> actionText.isEmpty() || log.getAction().toLowerCase().contains(actionText))
                    .filter(log -> startDate == null || !log.getTimestamp().toLocalDate().isBefore(startDate))
                    .filter(log -> endDate == null || !log.getTimestamp().toLocalDate().isAfter(endDate))
                    .toList();

            logTable.setItems(FXCollections.observableArrayList(logs));
        });

        closeButton.setOnAction(e -> {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ca/seneca/apd545/RXHgrandhotel/view/admin/admin_dashboard.fxml"));
                loader.setControllerFactory(ca.seneca.apd545.RXHgrandhotel.app.AppConfig.getInstance()::createController);

                javafx.scene.Parent root = loader.load();
                javafx.stage.Stage stage = (javafx.stage.Stage) closeButton.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Admin Dashboard");
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        });

        exportCsvButton.setOnAction(e -> handleExportCSV());
        exportTxtButton.setOnAction(e -> handleExportTXT());
    }
    private void handleExportTXT() {
        String fileName = "ActivityLogs_" + java.time.LocalDate.now() + ".txt";
        StringBuilder sb = new StringBuilder("ACTIVITY LOG EXPORT\n===================\n");
        for (ActivityLog log : logTable.getItems()) {
            sb.append(String.format("[%s] %s - %s: %s\n",
                    log.getTimestamp(), log.getActor(), log.getAction(), log.getMessage()));
        }

        reportingService.addLog("Admin", "EXPORT", "Logs", "N/A", "Exported logs to TXT");
    }

    private void handleExportCSV() {
        String fileName = "ActivityLogs_" + java.time.LocalDate.now() + ".csv";
        String[] headers = {"Timestamp", "Actor", "Action", "Entity", "ID", "Message"};

        var data = logTable.getItems().stream()
                .map(log -> new String[]{
                        log.getTimestamp().toString(),
                        log.getActor(),
                        log.getAction(),
                        log.getEntityType(),
                        log.getEntityId(),
                        log.getMessage()
                }).toList();

        ca.seneca.apd545.RXHgrandhotel.util.CSVExporter.export(fileName, headers, data);

        reportingService.addLog("Admin", "EXPORT", "Logs", "N/A", "Exported activity logs to CSV");
    }
}