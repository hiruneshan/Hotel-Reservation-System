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

public class LoyaltyRegisterationController {

    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private DatePicker dobField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField provinceField;
    @FXML private TextField postalField;
    @FXML private TextField countryField;

    private final GuestService guestService;
    private final LoyaltyService loyaltyService;

    public LoyaltyRegisterationController() {
        this.guestService = AppConfig.getInstance().getGuestService();
        this.loyaltyService = AppConfig.getInstance().getLoyaltyService();
    }

    @FXML
    public void initialize() {
        fullNameField.setText(BookingSession.getGuestFullName());
        phoneField.setText(BookingSession.getGuestPhone());
        emailField.setText(BookingSession.getGuestEmail());
        addressField.setText(BookingSession.getGuestAddress());
        cityField.setText(BookingSession.getGuestCity());
        postalField.setText(BookingSession.getGuestPostal());
        provinceField.setText(BookingSession.getGuestProvince());
        countryField.setText(BookingSession.getGuestCountry());
        dobField.setValue(BookingSession.getGuestDob());
    }


    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load: " + fxmlPath);
        }
    }

    @FXML
    private void onContinue(ActionEvent event) {

        if (isEmpty(fullNameField) ||
                isEmpty(phoneField) ||
                isEmpty(emailField) ||
                dobField.getValue() == null ||
                isEmpty(addressField) ||
                isEmpty(cityField) ||
                isEmpty(provinceField) ||
                isEmpty(postalField) ||
                isEmpty(countryField)) {

            showError("Please fill in all fields before continuing.");
            return;
        }


        BookingSession.setGuestFullName(fullNameField.getText());
        BookingSession.setGuestPhone(phoneField.getText());
        BookingSession.setGuestEmail(emailField.getText());
        BookingSession.setGuestDob(dobField.getValue());
        BookingSession.setGuestAddress(addressField.getText());
        BookingSession.setGuestCity(cityField.getText());
        BookingSession.setGuestProvince(provinceField.getText());
        BookingSession.setGuestPostal(postalField.getText());
        BookingSession.setGuestCountry(countryField.getText());

        // 1. Save Guest
        Guest guest = new Guest();
        guest.setFullName(fullNameField.getText());
        guest.setPhone(phoneField.getText());
        guest.setEmail(emailField.getText());
        guest.setDob(dobField.getValue());
        guest.setAddress(addressField.getText());
        guest.setCity(cityField.getText());
        guest.setProvince(provinceField.getText());
        guest.setPostal(postalField.getText());
        guest.setCountry(countryField.getText());

        Guest savedGuest = guestService.save(guest);
        BookingSession.setGuestId(savedGuest.getId());

        // 2. Create Loyalty Account
        LoyaltyAccount account = loyaltyService.registerGuest(savedGuest);

        BookingSession.setLoyaltyMember(true);
        BookingSession.setLoyaltyStatus(account.getStatus());

        // 3. Navigate
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_summaryscreen.fxml");
    }

    @FXML
    private void onBack(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_guest_details.fxml");
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Missing Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
