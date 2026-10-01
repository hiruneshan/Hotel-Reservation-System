package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import ca.seneca.apd545.RXHgrandhotel.model.WaitlistEntry;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.service.WaitlistService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class WaitlistController {


    @FXML private TextField guestNameField, phoneField, emailField, notesField;
    @FXML private Label loyaltyStatusLabel, waitlistFeedbackLabel;
    @FXML private ComboBox<RoomType> roomTypeCombo;
    @FXML private ComboBox<String> flexibilityCombo;
    @FXML private Spinner<Integer> partySizeSpinner;
    @FXML private DatePicker fromDatePicker, toDatePicker;

    @FXML private Label detailGuestLabel, detailPhoneLabel, detailEmailLabel, detailRoomTypeLabel,
            detailPartySizeLabel, detailFromLabel, detailToLabel, detailFlexibilityLabel, detailNotesLabel;

    @FXML private TableView<WaitlistEntry> waitlistTable;
    @FXML private TableColumn<WaitlistEntry, String> colWLGuest, colWLPhone, colWLEmail, colWLFlexibility, colWLStatus;
    @FXML private TableColumn<WaitlistEntry, RoomType> colWLRoomType;
    @FXML private TableColumn<WaitlistEntry, Integer> colWLPartySize;
    @FXML private TableColumn<WaitlistEntry, LocalDate> colWLFrom, colWLTo;

    @FXML private Button addWaitlistButton, convertButton, closeButton;

    private final WaitlistService waitlistService;
    private final ReportingService reportingService;
    private WaitlistEntry selectedEntry;

    public WaitlistController(WaitlistService waitlistService, ReportingService reportingService) {
        this.waitlistService = waitlistService;
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        setupTable();
        setupControls();
        loadWaitlist();


        addWaitlistButton.setOnAction(e -> handleAddToWaitlist());
        convertButton.setOnAction(e -> handleConvertToReservation());

        closeButton.setOnAction(e -> ((Stage) closeButton.getScene().getWindow()).close());

        waitlistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedEntry = newVal;
                showDetails(newVal);
            }
        });
    }

    private void setupTable() {
        colWLGuest.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getGuest() != null ? cellData.getValue().getGuest().getFullName() : "N/A"
                ));

        colWLPhone.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getGuest() != null ? cellData.getValue().getGuest().getPhone() : "N/A"
                ));

        colWLEmail.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getGuest() != null ? cellData.getValue().getGuest().getEmail() : "N/A"
                ));

        colWLRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colWLPartySize.setCellValueFactory(new PropertyValueFactory<>("partySize"));

        colWLFrom.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        colWLTo.setCellValueFactory(new PropertyValueFactory<>("checkOut"));

        colWLFlexibility.setCellValueFactory(new PropertyValueFactory<>("flexibility"));
        colWLStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void setupControls() {
        roomTypeCombo.setItems(FXCollections.observableArrayList(RoomType.values()));
        flexibilityCombo.setItems(FXCollections.observableArrayList("Exact Dates", "+/- 1 Day", "Anytime"));
        partySizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
    }

    private void loadWaitlist() {
        waitlistTable.setItems(FXCollections.observableArrayList(waitlistService.getAllActiveEntries()));
    }

    private void handleAddToWaitlist() {
        if (validateInput()) {
            try {
                WaitlistEntry entry = new WaitlistEntry();
                Guest guest = new Guest();
                guest.setFullName(guestNameField.getText());
                guest.setPhone(phoneField.getText());
                guest.setEmail(emailField.getText());


                guest.setAddress("N/A");
                guest.setCity("N/A");
                guest.setProvince("N/A");
                guest.setPostal("N/A");
                guest.setCountry("Canada");

                entry.setGuest(guest);
                entry.setRoomType(roomTypeCombo.getValue());
                entry.setCheckIn(fromDatePicker.getValue());
                entry.setCheckOut(toDatePicker.getValue());
                entry.setTimestamp(LocalDateTime.now());
                entry.setPartySize(partySizeSpinner.getValue());
                entry.setFlexibility(flexibilityCombo.getValue());


                WaitlistEntry savedEntry = waitlistService.saveEntry(entry);

                if (savedEntry != null && savedEntry.getId() != null) {
                    String entryIdStr = String.valueOf(savedEntry.getId());

                    reportingService.addLog("Admin", "WAITLIST_ADD", "Waitlist",
                            entryIdStr, "Added " + savedEntry.getGuest().getFullName() + " to waitlist.");


                    LoggerUtil.info("ADMIN", "WAITLIST_ADD", "WaitlistEntry",
                            entryIdStr, "Guest " + savedEntry.getGuest().getFullName() + " added.");

                    loadWaitlist();
                    clearFields();
                    showFeedback("Guest added to waitlist. Entry ID: " + entryIdStr, false);
                } else {
                    showFeedback("Error: Database failed to generate an entry ID.", true);
                }

            } catch (Exception e) {
                e.printStackTrace();
                showFeedback("Error saving to waitlist: " + e.getMessage(), true);
            }
        }
    }

    private void handleConvertToReservation() {
        if (selectedEntry == null) {
            showFeedback("Please select a guest from the table.", true);
            return;
        }

        try {
            waitlistService.convertToReservation(selectedEntry);


            reportingService.addLog("Admin", "WAITLIST_CONVERT", "Waitlist",
                    String.valueOf(selectedEntry.getId()), "Converted waitlist entry to active reservation.");

            LoggerUtil.info("WAITLIST_CONVERTED - ID: " + selectedEntry.getId());
            loadWaitlist();
            showFeedback("Waitlist entry converted to active reservation.", false);
        } catch (Exception e) {
            showFeedback("Cannot convert: " + e.getMessage(), true);
        }
    }

    private void showDetails(WaitlistEntry entry) {
        if (entry.getGuest() != null) {
            detailGuestLabel.setText(entry.getGuest().getFullName());
            detailPhoneLabel.setText(entry.getGuest().getPhone());
            detailEmailLabel.setText(entry.getGuest().getEmail());
        }
        if (entry.getRoomType() != null) {
            detailRoomTypeLabel.setText(entry.getRoomType().toString());
        }
        if (entry.getCheckIn() != null) {
            detailFromLabel.setText(entry.getCheckIn().toString());
        }
        if (entry.getCheckOut() != null) {
            detailToLabel.setText(entry.getCheckOut().toString());
        }
    }

    private boolean validateInput() {
        if (guestNameField.getText().isEmpty() || roomTypeCombo.getValue() == null) {
            showFeedback("Name and Room Type are required.", true);
            return false;
        }
        if (roomTypeCombo.getValue() == RoomType.SINGLE && partySizeSpinner.getValue() > 2) {
            showFeedback("Single room max occupancy is 2.", true);
            return false;
        }
        return true;
    }

    private void clearFields() {
        guestNameField.clear();
        phoneField.clear();
        emailField.clear();
        notesField.clear();
        roomTypeCombo.getSelectionModel().clearSelection();
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        flexibilityCombo.getSelectionModel().clearSelection();
    }

    private void showFeedback(String msg, boolean isError) {
        waitlistFeedbackLabel.setText(msg);
        waitlistFeedbackLabel.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + ";");
    }
}