package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;
import ca.seneca.apd545.RXHgrandhotel.service.ReservationService;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService; // Added import
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CancelReservationController {

    @FXML private Label guestNameLabel, bookingIdLabel, statusLabel, totalAmountLabel;
    @FXML private TextArea cancelReasonArea;
    @FXML private Button confirmCancelButton, closeButton;

    private final ReservationService reservationService;
    private final ReportingService reportingService;
    private Reservation currentReservation;


    public CancelReservationController(ReservationService reservationService, ReportingService reportingService) {
        this.reservationService = reservationService;
        this.reportingService = reportingService;
    }

    public void setReservation(Reservation res) {
        this.currentReservation = res;
        populateDetails();
    }

    @FXML
    public void initialize() {
        confirmCancelButton.setOnAction(e -> handleCancellation());
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
    }

    private void populateDetails() {
        if (currentReservation != null) {
            guestNameLabel.setText(currentReservation.getGuest().getFullName());
            bookingIdLabel.setText(String.valueOf(currentReservation.getId()));
            statusLabel.setText(currentReservation.getStatus().toString());
            totalAmountLabel.setText(String.format("$%.2f", currentReservation.getTotalAmount()));
        }
    }

    private void handleCancellation() {

        if (currentReservation.getStatus() == ReservationStatus.CANCELLED) {
            showAlert("Already Cancelled", "This reservation is already cancelled.");
            return;
        }
        currentReservation.setStatus(ReservationStatus.CANCELLED);
        reservationService.save(currentReservation);


        reportingService.addLog("Admin", "CANCEL", "Reservation",
                String.valueOf(currentReservation.getId()), "Reason: " + cancelReasonArea.getText());


        LoggerUtil.info(String.format("RESERVATION_CANCELLED - ID: %d, Reason: %s",
                currentReservation.getId(), cancelReasonArea.getText()));

        showAlert("Success", "Reservation has been cancelled.");
        ((Stage) confirmCancelButton.getScene().getWindow()).close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}