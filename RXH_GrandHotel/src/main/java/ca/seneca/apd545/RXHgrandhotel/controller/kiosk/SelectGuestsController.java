package ca.seneca.apd545.RXHgrandhotel.controller.kiosk;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import ca.seneca.apd545.RXHgrandhotel.model.LoyaltyAccount;
import ca.seneca.apd545.RXHgrandhotel.service.GuestService;
import ca.seneca.apd545.RXHgrandhotel.service.LoyaltyService;
import ca.seneca.apd545.RXHgrandhotel.session.BookingSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class SelectGuestsController {

    @FXML private DatePicker checkInDate;
    @FXML private DatePicker checkOutDate;
    @FXML private TextField guestCountField;

    // NEW: Add these fields to your FXML file
    @FXML private TextField guestNameField;
    @FXML private TextField phoneField;

    private final GuestService guestService;
    private final LoyaltyService loyaltyService;

    // Updated Constructor for Dependency Injection
    public SelectGuestsController(GuestService guestService, LoyaltyService loyaltyService) {
        this.guestService = guestService;
        this.loyaltyService = loyaltyService;
    }

    @FXML
    private void onContinue(ActionEvent event) {
        // 1. Existing Validation (Dates and Guest Count)
        LocalDate checkIn = checkInDate.getValue();
        LocalDate checkOut = checkOutDate.getValue();
        String guestCountText = guestCountField.getText();
        String enteredName = guestNameField.getText().trim();
        String enteredPhone = phoneField.getText().trim();

        if (checkIn == null || checkOut == null || enteredName.isEmpty() || enteredPhone.isEmpty()) {
            showError("All fields (Name, Phone, and Dates) are required.");
            return;
        }

        if (!checkOut.isAfter(checkIn)) {
            showError("Check-out date must be after check-in date.");
            return;
        }

        int guestCount;
        try {
            guestCount = Integer.parseInt(guestCountText);
            if (guestCount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showError("Please enter a valid number of guests.");
            return;
        }

        Guest guest = guestService.findByPhone(enteredPhone);

        if (guest != null) {

            BookingSession.setGuestId(guest.getId());

            Optional<LoyaltyAccount> account = loyaltyService.findAccountByGuestId(guest.getId());
            if (account.isPresent()) {
                BookingSession.setLoyaltyMember(true);
                BookingSession.setLoyaltyNumber(account.get().getLoyaltyNumber());
            } else {
                BookingSession.setLoyaltyMember(false);
            }
        } else {

            Guest newGuest = new Guest();
            newGuest.setFullName(enteredName);
            newGuest.setPhone(enteredPhone);
            newGuest.setEmail("N/A");

            Guest saved = guestService.save(newGuest);
            BookingSession.setGuestId(saved.getId());
            BookingSession.setLoyaltyMember(false);
        }

        BookingSession.setGuestFullName(enteredName);
        BookingSession.setGuestPhone(enteredPhone);
        BookingSession.setCheckIn(checkIn);
        BookingSession.setCheckOut(checkOut);
        BookingSession.setGuestCount(guestCount);

        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_room_selection.fxml");
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(type -> AppConfig.getInstance().createController(type));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load: " + fxmlPath);
        }
    }

    @FXML private void onBack(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_welcome.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid Input");
        alert.setContentText(message);
        alert.showAndWait();
    }
}