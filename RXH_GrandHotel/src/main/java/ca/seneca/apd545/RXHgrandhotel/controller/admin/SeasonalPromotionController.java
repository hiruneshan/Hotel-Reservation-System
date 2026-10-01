package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Discount;
import ca.seneca.apd545.RXHgrandhotel.service.DiscountService;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;

public class SeasonalPromotionController {

    @FXML private TextField promoNameField, discountField, conditionsField;
    @FXML private ComboBox<String> promoTypeCombo;
    @FXML private DatePicker startDatePicker, endDatePicker;
    @FXML private Label promoFeedbackLabel;

    @FXML private TableView<Discount> promotionTable;
    @FXML private TableColumn<Discount, String> colPromoName;
    @FXML private TableColumn<Discount, Double> colPromoDiscount;
    @FXML private TableColumn<Discount, LocalDate> colPromoStart, colPromoEnd;

    @FXML private Button savePromoButton, clearPromoButton, deleteButton, closeButton;

    private final DiscountService discountService;
    private Discount selectedDiscount;
    private final ReportingService reportingService;

    public SeasonalPromotionController(DiscountService discountService, ReportingService reportingService) {
        this.discountService = discountService;
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        setupTable();
        loadDiscounts();

        savePromoButton.setOnAction(e -> handleSaveDiscount());
        clearPromoButton.setOnAction(e -> clearFields());
        deleteButton.setOnAction(e -> handleDelete());
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
        promotionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDiscount = newVal;
                populateFields(newVal);
            }
        });
    }

    private void handleSaveDiscount() {
        try {
            double percent = Double.parseDouble(discountField.getText());

            Discount discount = (selectedDiscount != null) ? selectedDiscount : new Discount();
            discount.setName(promoNameField.getText());
            discount.setPercentage(percent);
            discount.setStartDate(startDatePicker.getValue());
            discount.setEndDate(endDatePicker.getValue());
            discount.setAppliedBy("Admin_User");
            discountService.save(discount, "Manager");
            LoggerUtil.info("SEASONAL_DISCOUNT_SAVED - Name: " + discount.getName());

            loadDiscounts();
            showFeedback("Discount saved successfully.", false);
            clearFields();
            reportingService.addLog("Admin", "PROMO_SAVE", "Discount",
                    String.valueOf(discount.getId()), "Saved seasonal promotion: " + discount.getName());

        } catch (NumberFormatException e) {
            showFeedback("Invalid percentage format.", true);
        } catch (IllegalArgumentException e) {
            showFeedback(e.getMessage(), true);
        }
    }

    private void setupTable() {
        colPromoName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPromoDiscount.setCellValueFactory(new PropertyValueFactory<>("percentage"));
        colPromoStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colPromoEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));
    }

    private void loadDiscounts() {
        promotionTable.setItems(FXCollections.observableArrayList(discountService.getAllSeasonalDiscounts()));
    }

    private void populateFields(Discount d) {
        promoNameField.setText(d.getName());
        discountField.setText(String.valueOf(d.getPercentage()));
        startDatePicker.setValue(d.getStartDate());
        endDatePicker.setValue(d.getEndDate());
    }

    private void clearFields() {
        selectedDiscount = null;
        promoNameField.clear();
        discountField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
    }

    private void handleDelete() {
        if (selectedDiscount != null) {
            discountService.delete(selectedDiscount);
            reportingService.addLog("Admin", "PROMO_DELETE", "Discount",
                    String.valueOf(selectedDiscount.getId()), "Deleted promotion: " + selectedDiscount.getName());


            LoggerUtil.info("SEASONAL_DISCOUNT_DELETED - ID: " + selectedDiscount.getId());
            loadDiscounts();
            clearFields();
        }
    }

    private void showFeedback(String msg, boolean isError) {
        promoFeedbackLabel.setText(msg);
        promoFeedbackLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + ";");
    }
}