package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import ca.seneca.apd545.RXHgrandhotel.model.LoyaltyAccount;
import ca.seneca.apd545.RXHgrandhotel.model.LoyaltyHistory;
import ca.seneca.apd545.RXHgrandhotel.service.LoyaltyService;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class LoyaltyController {


    @FXML private TextField guestNameField, loyaltyNumberField;
    @FXML private Button searchButton;


    @FXML private Label guestLabel, guestPhoneLabel, guestEmailLabel, guestCityLabel;

    @FXML private Label loyaltyNumberLabel, loyaltyTierLabel, pointsBalanceLabel,
            earningRateLabel, totalStaysLabel, totalSpendLabel, lastStayLabel;

    @FXML private TableView<LoyaltyHistory> loyaltyHistoryTable;
    @FXML private TableColumn<LoyaltyHistory, LocalDate> colLHDate;
    @FXML private TableColumn<LoyaltyHistory, String> colLHType;
    @FXML private TableColumn<LoyaltyHistory, Integer> colLHPoints;
    @FXML private TableColumn<LoyaltyHistory, String> colLHDescription;


    @FXML private TextField redeemPointsField;
    @FXML private Button redeemButton, closeButton;
    @FXML private Label loyaltyFeedbackLabel;

    private final LoyaltyService loyaltyService;
    private LoyaltyAccount currentAccount;
    private final ReportingService reportingService;

    public LoyaltyController(LoyaltyService loyaltyService, ReportingService reportingService) {
        this.loyaltyService = loyaltyService;
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        setupTable();

        searchButton.setOnAction(e -> handleSearch());
        redeemButton.setOnAction(e -> handleRedemption());
        closeButton.setOnAction(e -> handleClose());
    }

    private void setupTable() {
        colLHDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colLHType.setCellValueFactory(new PropertyValueFactory<>("type")); // Earning or Redemption
        colLHPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colLHDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void handleSearch() {
        String name = guestNameField.getText().trim();
        String number = loyaltyNumberField.getText().trim();

        try {

            currentAccount = loyaltyService.findAccount(name, number);

            if (currentAccount != null) {
                populateUI(currentAccount);
                showFeedback("Account found.", false);
            } else {
                showFeedback("No loyalty account located.", true);
            }
        } catch (Exception e) {
            showFeedback("Search error: " + e.getMessage(), true);
        }
    }

    private void populateUI(LoyaltyAccount account) {
        Guest guest = account.getGuest();

        guestLabel.setText(guest.getFullName());
        guestPhoneLabel.setText(guest.getPhone());
        guestEmailLabel.setText(guest.getEmail());
        guestCityLabel.setText(guest.getCity());

        loyaltyNumberLabel.setText(account.getLoyaltyNumber());
        loyaltyTierLabel.setText(account.getStatus());
        pointsBalanceLabel.setText(String.valueOf(account.getPoints()));
        earningRateLabel.setText(account.getEarningRate() + " pts/$");
        List<LoyaltyHistory> history = loyaltyService.getHistoryForAccount(account.getId());
        loyaltyHistoryTable.setItems(FXCollections.observableArrayList(history));
    }

    private void handleRedemption() {
        if (currentAccount == null) {
            showFeedback("Please select an account first.", true);
            return;
        }

        try {
            int pointsToRedeem = Integer.parseInt(redeemPointsField.getText());

            double discountValue = loyaltyService.redeemPoints(currentAccount.getId(), pointsToRedeem);


            reportingService.addLog("Admin", "LOYALTY_REDEMPTION", "LoyaltyAccount",
                    currentAccount.getLoyaltyNumber(),
                    "Redeemed " + pointsToRedeem + " points for $" + discountValue + " discount.");


            LoggerUtil.info("LOYALTY", "REDEMPTION", "LoyaltyAccount",
                    currentAccount.getLoyaltyNumber(),
                    "Redeemed " + pointsToRedeem + " points for $" + discountValue + " discount.");

            handleSearch();
            redeemPointsField.clear();
            showFeedback("Redeemed successfully! Discount: $" + discountValue, false);

        } catch (NumberFormatException e) {
            showFeedback("Enter a valid point amount.", true);
        } catch (Exception e) {
            showFeedback(e.getMessage(), true);
        }
    }

    private void handleClose() {
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
    }


    private void showFeedback(String msg, boolean isError) {
        loyaltyFeedbackLabel.setText(msg);
        loyaltyFeedbackLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + ";");
    }
}