package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Feedback;
import ca.seneca.apd545.RXHgrandhotel.service.FeedbackService;
import ca.seneca.apd545.RXHgrandhotel.util.CSVExporter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FeedbackViewerController {

    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> colFBGuest, colFBComment;
    @FXML private TableColumn<Feedback, Integer> colFBRating;
    @FXML private Button exportButton, closeButton, searchButton;
    @FXML private ComboBox<Integer> ratingFilterCombo;

    private final FeedbackService feedbackService;


    public FeedbackViewerController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @FXML
    public void initialize() {
        setupTable();
        loadFeedback();

        ratingFilterCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));

        searchButton.setOnAction(e -> handleFilter());
        exportButton.setOnAction(e -> handleExport());
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
    }

    private void setupTable() {
        colFBGuest.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGuest().getFullName()));
        colFBRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        colFBComment.setCellValueFactory(new PropertyValueFactory<>("comment"));
    }

    private void loadFeedback() {

        feedbackTable.setItems(FXCollections.observableArrayList(feedbackService.findAll()));
    }

    private void handleFilter() {
        Integer rating = ratingFilterCombo.getValue();
        if (rating != null) {
            feedbackTable.setItems(FXCollections.observableArrayList(feedbackService.findByRating(rating)));
        }
    }

    private void handleExport() {
        CSVExporter.export("feedback_summary.csv",
                new String[]{"Guest", "Rating", "Comment"},
                feedbackTable.getItems().stream()
                        .map(f -> new String[]{f.getGuest().getFullName(), String.valueOf(f.getRating()), f.getComment()})
                        .toList()
        );
    }
}