package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.enums.Role;
import ca.seneca.apd545.RXHgrandhotel.security.AuthService;
import ca.seneca.apd545.RXHgrandhotel.service.ReservationService;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class DiscountController {

    @FXML private Label maxDiscountLabel, roleLabel, discountFeedbackLabel;
    @FXML private TextField discountField;
    @FXML private Button applyButton, closeButton;

    @FXML private Label guestNameLabel, phoneLabel, bookingIdLabel,
            checkInLabel, checkOutLabel, roomTypeLabel, totalAmountLabel;

    private Role currentRole;
    private Reservation reservation;
    private final AuthService authService;
    private final ReservationService reservationService;
    private final ReportingService reportingService;

    public DiscountController(AuthService authService, ReservationService reservationService, ReportingService reportingService) {
        this.authService = authService;
        this.reservationService = reservationService;
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        currentRole = authService.getCurrentUser().getRole();
        roleLabel.setText(currentRole.toString());

        int max = (currentRole == Role.MANAGER) ? 30 : 15;
        maxDiscountLabel.setText(max + "%");

        applyButton.setOnAction(e -> handleApply(max));

        if (closeButton != null) {
            closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
        }

        populateFields();
    }

    public void setReservation(Reservation res) {
        this.reservation = res;
        populateFields();
    }

    private void populateFields() {
        if (reservation == null || phoneLabel == null) {
            return;
        }

        guestNameLabel.setText(reservation.getGuest().getFullName());
        phoneLabel.setText(reservation.getGuest().getPhone());
        bookingIdLabel.setText("#" + reservation.getId());
        checkInLabel.setText(reservation.getCheckIn().toString());
        checkOutLabel.setText(reservation.getCheckOut().toString());

        String roomTypes = reservation.getRooms().stream()
                .map(rr -> rr.getRoom().getType().toString())
                .distinct()
                .collect(java.util.stream.Collectors.joining(", "));
        roomTypeLabel.setText(roomTypes);

        totalAmountLabel.setText(String.format("$%.2f", reservation.getTotal()));
    }

    private void handleApply(int max) {
        try {
            double percent = Double.parseDouble(discountField.getText());

            if (percent < 0 || percent > max) {
                showError("Limit exceeded! Your max is " + max + "%");
                return;
            }

            double originalSubtotal = reservation.getSubtotal();
            double discountVal = originalSubtotal * (percent / 100);
            double discountedSubtotal = originalSubtotal - discountVal;

            double newTax = discountedSubtotal * 0.13;
            double newTotal = discountedSubtotal + newTax;

            reservation.setDiscountPercent(percent);
            reservation.setDiscountAmount(discountVal);
            reservation.setTax(newTax);
            reservation.setTotal(newTotal);
            reservation.setDiscountReason(discountField.getText() + "% " + currentRole + " Discount");
            reservation.setAppliedBy(authService.getCurrentUser().getUsername());

            reservationService.save(reservation);

            reportingService.addLog(authService.getCurrentUser().getUsername(),
                    "DISCOUNT_APPLIED", "Reservation", String.valueOf(reservation.getId()),
                    "Applied " + percent + "%. New Total: $" + String.format("%.2f", newTotal));

            LoggerUtil.info(authService.getCurrentUser().getUsername(),
                    "DISCOUNT_APPLIED", "Reservation", String.valueOf(reservation.getId()),
                    "Applied " + percent + "%. New Total: $" + String.format("%.2f", newTotal));

            discountFeedbackLabel.setText("Discount Applied Successfully!");
            discountFeedbackLabel.setStyle("-fx-text-fill: green;");

            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
            delay.setOnFinished(event -> ((Stage) applyButton.getScene().getWindow()).close());
            delay.play();

        } catch (NumberFormatException e) {
            showError("Please enter a valid number.");
        }
    }

    private void showError(String msg) {
        discountFeedbackLabel.setText(msg);
        discountFeedbackLabel.setStyle("-fx-text-fill: red;");
    }
}