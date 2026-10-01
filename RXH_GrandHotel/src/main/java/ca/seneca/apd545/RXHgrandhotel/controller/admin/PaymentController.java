package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Payment;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.enums.PaymentType;
import ca.seneca.apd545.RXHgrandhotel.service.LoyaltyService;
import ca.seneca.apd545.RXHgrandhotel.service.PaymentService;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.service.ReservationService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;

public class PaymentController {

    @FXML private Label guestNameLabel, guestPhoneLabel, guestEmailLabel, loyaltyStatusLabel, pointsAvailableLabel;
    @FXML private Label bookingIdLabel, checkInLabel, checkOutLabel, roomTypeLabel, discountAppliedLabel, paymentStatusLabel;
    @FXML private Label totalAmountLabel, paidAmountLabel, outstandingAmountLabel;
    @FXML private ComboBox<PaymentType> paymentTypeCombo;
    @FXML private TextField amountField;
    @FXML private CheckBox usePointsCheck;
    @FXML private Label paymentFeedbackLabel;
    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, LocalDate> colPayDate;
    @FXML private TableColumn<Payment, PaymentType> colPayType;
    @FXML private TableColumn<Payment, Double> colPayAmount;


    @FXML private Button applyPaymentButton, refundButton, closeButton;

    private final PaymentService paymentService;
    private final ReservationService reservationService;
    private Reservation currentReservation;
    private final LoyaltyService loyaltyService;
    private final ReportingService reportingService;


    public PaymentController(PaymentService ps, ReservationService rs, LoyaltyService ls, ReportingService reportingService) {
        this.paymentService = ps;
        this.reservationService = rs;
        this.loyaltyService = ls;
        this.reportingService = reportingService;
    }


    public void setReservation(Reservation res) {
        this.currentReservation = res;
        populateDetails();
    }

    @FXML
    public void initialize() {
        paymentTypeCombo.setItems(FXCollections.observableArrayList(PaymentType.values()));
        setupTable();

        applyPaymentButton.setOnAction(e -> handleApplyPayment());
        refundButton.setOnAction(e -> handleRefund());
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
    }

    private void setupTable() {
        colPayDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colPayType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colPayAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
    }

    private void populateDetails() {
        if (currentReservation != null) {
            // 1. Basic Reservation Details [cite: 66]
            guestNameLabel.setText(currentReservation.getGuest().getFullName());
            guestPhoneLabel.setText(currentReservation.getGuest().getPhone());
            guestEmailLabel.setText(currentReservation.getGuest().getEmail());
            bookingIdLabel.setText(String.valueOf(currentReservation.getId()));

            checkInLabel.setText(currentReservation.getCheckIn().toString());
            checkOutLabel.setText(currentReservation.getCheckOut().toString());

            if (currentReservation.getGuest().getLoyaltyAccount() != null) {
                var account = currentReservation.getGuest().getLoyaltyAccount();
                loyaltyStatusLabel.setText(account.getStatus());
                pointsAvailableLabel.setText(account.getPoints() + " pts");
            } else {
                loyaltyStatusLabel.setText("Not Enrolled");
                pointsAvailableLabel.setText("0 pts");
            }

            // 4. Financial Summary [cite: 76, 101]
            totalAmountLabel.setText(String.format("$%.2f", currentReservation.getTotalAmount()));

            refreshBalances();
        }
    }

    private void refreshBalances() {
        double paid = currentReservation.getPayments().stream().mapToDouble(Payment::getAmount).sum();
        double outstanding = currentReservation.getTotalAmount() - paid;

        paidAmountLabel.setText(String.format("$%.2f", paid));
        outstandingAmountLabel.setText(String.format("$%.2f", outstanding));
        paymentTable.setItems(FXCollections.observableArrayList(currentReservation.getPayments()));
    }

    private void handleApplyPayment() {
        try {

            String input = amountField.getText().trim();
            if (input.isEmpty()) {
                showFeedback("Please enter an amount.", true);
                return;
            }

            double amount = Double.parseDouble(input);
            PaymentType type = paymentTypeCombo.getValue();

            if (type == null) {
                showFeedback("Please select a payment type.", true);
                return;
            }

            if (amount <= 0) {
                showFeedback("Amount must be greater than zero.", true);
                return;
            }

            if (usePointsCheck.isSelected() && type == PaymentType.LOYALTY_POINTS) {
                if (currentReservation.getGuest().getLoyaltyAccount() == null) {
                    showFeedback("Guest is not enrolled in loyalty program.", true);
                    return;
                }

                Long accountId = currentReservation.getGuest().getLoyaltyAccount().getId();


                double pointValueInDollars = loyaltyService.redeemPoints(accountId, (int) amount);


                amount = pointValueInDollars;
            }


            paymentService.processPayment(currentReservation, amount, type);


            if (currentReservation.getGuest().getLoyaltyAccount() != null && type != PaymentType.LOYALTY_POINTS) {
                loyaltyService.earnPoints(currentReservation.getGuest().getLoyaltyAccount().getId(), amount);
            }

            LoggerUtil.info("ADMIN", "PAYMENT_SUCCESS", "Reservation",
                    String.valueOf(currentReservation.getId()),
                    "Payment of $" + amount + " (" + type + ") processed.");

            refreshBalances();
            populateDetails();
            amountField.clear();
            usePointsCheck.setSelected(false);
            showFeedback("Payment of $" + String.format("%.2f", amount) + " applied successfully.", false);
            reportingService.addLog("Admin", "PAYMENT", "Reservation",
                    String.valueOf(currentReservation.getId()),
                    "Processed payment of $" + amount + " via " + type);

        } catch (NumberFormatException e) {
            showFeedback("Invalid number format. Please enter a valid amount.", true);
        } catch (IllegalStateException e) {
            showFeedback(e.getMessage(), true);
        } catch (Exception e) {
            LoggerUtil.error("PAYMENT_CRASH", e);
            showFeedback("System error: Could not process payment.", true);
        }
    }

    private void handleRefund() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            paymentService.processPayment(currentReservation, -amount, PaymentType.REFUND);

            LoggerUtil.info("REFUND_PROCESSED - Reservation ID: " + currentReservation.getId() + " Amount: -" + amount);
            refreshBalances();
            reportingService.addLog("Admin", "REFUND", "Reservation",
                    String.valueOf(currentReservation.getId()), "Processed refund of $" + amount);

        } catch (Exception e) {
            showFeedback("Error processing refund.", true);
        }
    }

    private void showFeedback(String msg, boolean isError) {
        paymentFeedbackLabel.setText(msg);
        paymentFeedbackLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + ";");
    }
}