package ca.seneca.apd545.RXHgrandhotel.controller.kiosk;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.session.BookingSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class KioskSummaryController {

    @FXML private Label summaryName;
    @FXML private Label summaryPhone;
    @FXML private Label summaryEmail;
    @FXML private Label summaryAddress;
    @FXML private Label summaryCity;
    @FXML private Label summaryPostal;
    @FXML private Label summaryLoyaltyStatus;
    @FXML private Label summaryCheckIn;
    @FXML private Label summaryCheckOut;
    @FXML private Label summaryGuests;
    @FXML private Label summaryRooms;

    @FXML
    public void initialize() {
        loadGuestDetails();
        loadLoyaltyStatus();
        loadBookingDetails();
        loadRoomSummary();
    }

    private void loadGuestDetails() {
        summaryName.setText(BookingSession.getGuestFullName());
        summaryPhone.setText(BookingSession.getGuestPhone());
        summaryEmail.setText(BookingSession.getGuestEmail());
        summaryAddress.setText(BookingSession.getGuestAddress());
        summaryCity.setText(BookingSession.getGuestCity());
        summaryPostal.setText(BookingSession.getGuestPostal());
    }

    private void loadLoyaltyStatus() {
        if (BookingSession.isLoyaltyMember()) {
            summaryLoyaltyStatus.setText("Loyalty Status: " + BookingSession.getLoyaltyStatus());
        } else {
            summaryLoyaltyStatus.setText("Not a Loyalty Member");
        }
    }

    private void loadBookingDetails() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        summaryCheckIn.setText(BookingSession.getCheckIn().format(fmt));
        summaryCheckOut.setText(BookingSession.getCheckOut().format(fmt));
        summaryGuests.setText(String.valueOf(BookingSession.getGuestCount()));
    }

    private void loadRoomSummary() {
        Map<RoomType, Integer> rooms = BookingSession.getAllRoomQuantities();
        StringBuilder sb = new StringBuilder();
        rooms.forEach((type, qty) -> {
            if (qty > 0) {
                sb.append(type.name()).append(" x ").append(qty).append("\n");
            }
        });
        summaryRooms.setText(sb.toString().trim());
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
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_guest_details.fxml");
    }

    @FXML
    private void onConfirm(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_confirmation.fxml");
    }
}