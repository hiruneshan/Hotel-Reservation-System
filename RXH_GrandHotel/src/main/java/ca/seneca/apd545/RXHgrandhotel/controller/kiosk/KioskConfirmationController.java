package ca.seneca.apd545.RXHgrandhotel.controller.kiosk;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import ca.seneca.apd545.RXHgrandhotel.model.Guest;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.ReservationRoom;
import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;
import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.service.GuestService;
import ca.seneca.apd545.RXHgrandhotel.service.PricingService;
import ca.seneca.apd545.RXHgrandhotel.service.ReservationService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KioskConfirmationController {

    @FXML private Label guestSummaryLabel;
    @FXML private Label bookingSummaryLabel;
    @FXML private Label loyaltyDiscountLabel;

    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;

    private final ReservationService reservationService;
    private final GuestService guestService;
    private final PricingService pricingService;

    public KioskConfirmationController(ReservationService reservationService, GuestService guestService, PricingService ps) {
        this.reservationService = reservationService;
        this.guestService = guestService;
        this.pricingService = ps;
    }

    @FXML
    public void initialize() {
        loadGuestSummary();
        loadBookingSummary();
        loadLoyaltyDiscount();
        loadPaymentSummary();
    }

    private void loadGuestSummary() {
        String summary = String.format(
                "%s\nPhone: %s\nEmail: %s\nAddress: %s, %s, %s",
                BookingSession.getGuestFullName(),
                BookingSession.getGuestPhone(),
                BookingSession.getGuestEmail(),
                BookingSession.getGuestAddress(),
                BookingSession.getGuestCity(),
                BookingSession.getGuestPostal()
        );
        guestSummaryLabel.setText(summary);
    }

    private void loadBookingSummary() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        StringBuilder roomList = new StringBuilder();
        for (Map.Entry<RoomType, Integer> entry : BookingSession.getAllRoomQuantities().entrySet()) {
            if (entry.getValue() > 0) {
                roomList.append(entry.getKey().name())
                        .append(" x ")
                        .append(entry.getValue())
                        .append("\n");
            }
        }

        String summary = String.format(
                "Check-In: %s\nCheck-Out: %s\nGuests: %d\nRooms:\n%s",
                BookingSession.getCheckIn().format(fmt),
                BookingSession.getCheckOut().format(fmt),
                BookingSession.getGuestCount(),
                roomList.toString().trim()
        );

        bookingSummaryLabel.setText(summary);
    }

    private void loadLoyaltyDiscount() {
        if (BookingSession.isLoyaltyMember()) {
            loyaltyDiscountLabel.setText("10% Loyalty Discount Applied");
        } else {
            loyaltyDiscountLabel.setText("No Loyalty Discount");
        }
    }

    private void loadPaymentSummary() {
        double subtotal = 0;
        for (Map.Entry<RoomType, Integer> entry : BookingSession.getAllRoomQuantities().entrySet()) {
            int quantity = entry.getValue();
            if (quantity > 0) {
                double baseRate = pricingService.getBasePrice(entry.getKey());
                double stayPrice = pricingService.calculateStayPrice(
                        baseRate,
                        BookingSession.getCheckIn(),
                        BookingSession.getCheckOut()
                );
                subtotal += (stayPrice * quantity);
            }
        }

        if (BookingSession.isLoyaltyMember()) {
            subtotal *= 0.90;
        }

        double tax = subtotal * 0.13;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalLabel.setText(String.format("$%.2f", total));

        BookingSession.setSubtotal(subtotal);
        BookingSession.setTax(tax);
        BookingSession.setTotal(total);
    }

    @FXML
    private void onRules(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_rules.fxml");
    }

    @FXML
    private void onBack(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_summaryscreen.fxml");
    }

    @FXML
    private void onCancel(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_welcome.fxml");
    }

    @FXML
    private void onConfirm(ActionEvent event) {
        Guest guest = guestService.findById(BookingSession.getGuestId());

        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setCheckIn(BookingSession.getCheckIn());
        reservation.setCheckOut(BookingSession.getCheckOut());
        reservation.setAdults(BookingSession.getGuestCount());
        reservation.setChildren(0);
        reservation.setSubtotal(BookingSession.getSubtotal());
        reservation.setTax(BookingSession.getTax());
        reservation.setTotal(BookingSession.getTotal());
        reservation.setStatus(ReservationStatus.BOOKED);

        List<ReservationRoom> roomList = new ArrayList<>();
        for (Map.Entry<RoomType, Integer> entry : BookingSession.getAllRoomQuantities().entrySet()) {
            if (entry.getValue() > 0) {
                ReservationRoom rr = new ReservationRoom();
                rr.setReservation(reservation);
                rr.setRoomType(entry.getKey());
                rr.setQuantity(entry.getValue());
                rr.setNightlyRate(100.0);
                roomList.add(rr);
            }
        }

        reservation.setRooms(roomList);
        Reservation saved = reservationService.save(reservation);
        BookingSession.setReservationId(saved.getId());

        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/KioskFinalScreen.fxml"); // ✅ FIXED
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
}