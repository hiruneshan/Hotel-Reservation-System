package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.ReservationRoom;
import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.service.ReservationService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class ReservationEditorController {

    @FXML private Label bookingIdLabel, paymentStatusLabel, validationLabel;
    @FXML private TextField guestNameField, phoneField, emailField;

    @FXML private DatePicker checkInPicker, checkOutPicker;
    @FXML private Spinner<Integer> adultsSpinner, childrenSpinner;

    @FXML private TableView<ReservationRoom> roomTable;
    @FXML private TableColumn<ReservationRoom, String> colRoomType;
    @FXML private TableColumn<ReservationRoom, Integer> colQuantity;
    @FXML private TableColumn<ReservationRoom, Integer> colOccupancy;
    @FXML private TableColumn<ReservationRoom, Double> colRate;

    @FXML private Button saveButton, cancelButton, closeButton;

    private final ReportingService reportingService;
    private final ReservationService reservationService;
    private Reservation currentReservation;

    public ReservationEditorController(ReservationService reservationService, ReportingService reportingService) {
        this.reservationService = reservationService;
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        setupTable();
        setupSpinners();

        saveButton.setOnAction(e -> handleSave());
        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());
        cancelButton.setOnAction(e -> handleCancelNavigation());
    }

    public void setReservation(Reservation res) {
        this.currentReservation = res;
        populateFields();
    }

    private void setupTable() {
        colRoomType.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getRoom() != null
                                ? cellData.getValue().getRoom().getType().toString()
                                : "N/A"
                ));

        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("nightlyRate"));

        colOccupancy.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cellData.getValue().getRoom() != null
                                ? cellData.getValue().getRoom().getMaxAdults()
                                : 0
                ));
    }

    private void setupSpinners() {
        adultsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1));
        childrenSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));
    }

    private void populateFields() {
        if (currentReservation != null) {
            bookingIdLabel.setText("Booking ID: " + currentReservation.getId());
            guestNameField.setText(currentReservation.getGuest().getFullName());
            phoneField.setText(currentReservation.getGuest().getPhone());
            emailField.setText(currentReservation.getGuest().getEmail());

            checkInPicker.setValue(currentReservation.getCheckIn());
            checkOutPicker.setValue(currentReservation.getCheckOut());
            adultsSpinner.getValueFactory().setValue(currentReservation.getAdults());
            childrenSpinner.getValueFactory().setValue(currentReservation.getChildren());

            roomTable.setItems(FXCollections.observableArrayList(currentReservation.getRooms()));

            paymentStatusLabel.setText(currentReservation.isPaid() ? "Status: PAID" : "Status: UNPAID");
        }
    }

    private void handleSave() {
        if (validateInput()) {
            try {

                currentReservation.setCheckIn(checkInPicker.getValue());
                currentReservation.setCheckOut(checkOutPicker.getValue());
                currentReservation.setAdults(adultsSpinner.getValue());
                currentReservation.setChildren(childrenSpinner.getValue());

                currentReservation.getGuest().setFullName(guestNameField.getText());
                currentReservation.getGuest().setPhone(phoneField.getText());
                currentReservation.getGuest().setEmail(emailField.getText());

                reservationService.save(currentReservation);
                reportingService.addLog("Admin", "MODIFY", "Reservation",
                        String.valueOf(currentReservation.getId()), "Admin updated guest details and stay dates.");

                LoggerUtil.info("ADMIN", "MODIFY_RESERVATION", "Reservation",
                        String.valueOf(currentReservation.getId()),
                        "Admin modified Reservation ID: " + currentReservation.getId());

                showAlert("Success", "Reservation updated successfully.");
                ((Stage) saveButton.getScene().getWindow()).close();

            } catch (Exception e) {
                validationLabel.setText("Error saving: " + e.getMessage());
                LoggerUtil.error("RESERVATION_EDIT_FAILED", e);
            }
        }
    }

    private boolean validateInput() {
        // 1. Basic Field Validation
        if (guestNameField.getText().isEmpty() || emailField.getText().isEmpty()) {
            validationLabel.setText("Name and Email are required.");
            return false;
        }

        // 2. Date Validation
        if (checkInPicker.getValue() == null || checkOutPicker.getValue() == null) {
            validationLabel.setText("Dates are required.");
            return false;
        }
        if (!checkOutPicker.getValue().isAfter(checkInPicker.getValue())) {
            validationLabel.setText("Check-out must be after check-in.");
            return false;
        }

        int totalCapacity = currentReservation.getRooms().stream()
                .mapToInt(rr -> rr.getRoom().getMaxAdults())
                .sum();

        if (adultsSpinner.getValue() > totalCapacity) {
            validationLabel.setText("Capacity Error: Assigned rooms only hold " + totalCapacity + " adults.");
            return false;
        }

        return true;
    }

    private void handleCancelNavigation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/ca/seneca/apd545/RXHgrandhotel/view/admin/cancel_reservation.fxml"));
            loader.setControllerFactory(AppConfig.getInstance()::createController);
            Parent root = loader.load();

            CancelReservationController controller = loader.getController();
            controller.setReservation(currentReservation);

            Stage stage = new Stage();
            stage.setTitle("Confirm Cancellation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();


            if (currentReservation.getStatus() == ReservationStatus.CANCELLED) {
                ((Stage) cancelButton.getScene().getWindow()).close();
            }

        } catch (IOException e) {
            LoggerUtil.severe("Navigation Failure: " + e.getMessage());
            validationLabel.setText("Error opening cancellation screen.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}