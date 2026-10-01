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
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class KioskGuestDetailsController {


    @FXML private RadioButton loyaltyYes;
    @FXML private RadioButton loyaltyNo;


    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField cityField;
    @FXML private TextField postalField;


    @FXML private Label errorLabel;
    @FXML private Label loyaltyStatusLabel;


    @FXML private Button yesRegisterButton;
    @FXML private Button noRegisterButton;

    private ToggleGroup loyaltyGroup;

    private LoyaltyService loyaltyService;
    private GuestService guestService;

    @FXML
    public void initialize() {

        AppConfig config = AppConfig.getInstance();
        loyaltyService = config.getLoyaltyService();
        guestService = config.getGuestService();

        loyaltyGroup = new ToggleGroup();
        loyaltyYes.setToggleGroup(loyaltyGroup);
        loyaltyNo.setToggleGroup(loyaltyGroup);

        errorLabel.setText("");
        loyaltyStatusLabel.setText("");

        yesRegisterButton.setVisible(false);
        noRegisterButton.setVisible(false);

        // Prefill if returning
        fullNameField.setText(BookingSession.getGuestFullName());
        phoneField.setText(BookingSession.getGuestPhone());
        emailField.setText(BookingSession.getGuestEmail());
        addressField.setText(BookingSession.getGuestAddress());
        cityField.setText(BookingSession.getGuestCity());
        postalField.setText(BookingSession.getGuestPostal());

        if (BookingSession.isLoyaltyMember()) {
            loyaltyYes.setSelected(true);
            loyaltyStatusLabel.setText("Loyalty Status: " + BookingSession.getLoyaltyStatus());
        }
    }


    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load: " + fxmlPath);
        }
    }


    @FXML
    private void onLoyaltyYes(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Loyalty Verification");
        dialog.setHeaderText("Enter your loyalty phone number");
        dialog.setContentText("Phone:");

        String phone = dialog.showAndWait().orElse(null);

        if (phone == null || phone.trim().isEmpty()) {
            errorLabel.setText("Phone number required for loyalty lookup.");
            return;
        }

        LoyaltyAccount account = loyaltyService.findByLoyaltyNumber(phone.trim()).orElse(null);

        if (account == null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Not Found");
            alert.setContentText("Sorry, you are not a loyalty member.\nWould you like to register?");

            ButtonType register = new ButtonType("Register");
            ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(register, cancel);

            if (alert.showAndWait().orElse(cancel) == register) {
                saveGuestToSession();
                navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/loyaltyRegistration.fxml");
            }
            return;
        }


        Guest g = account.getGuest();

        fullNameField.setText(g.getFullName());
        phoneField.setText(g.getPhone());
        emailField.setText(g.getEmail());
        addressField.setText(g.getAddress());
        cityField.setText(g.getCity());
        postalField.setText(g.getPostal());

        loyaltyStatusLabel.setText("Loyalty Status: " + account.getStatus());

        BookingSession.setLoyaltyMember(true);
        BookingSession.setLoyaltyStatus(account.getStatus());
        BookingSession.setGuestId(g.getId());
    }


    @FXML
    private void onNext(ActionEvent event) {

        errorLabel.setText("");
        yesRegisterButton.setVisible(false);
        noRegisterButton.setVisible(false);

        if (loyaltyGroup.getSelectedToggle() == null) {
            showError("Please select whether you are a loyalty member.");
            return;
        }

        if (isEmpty(fullNameField) ||
                isEmpty(phoneField) ||
                isEmpty(emailField) ||
                isEmpty(addressField) ||
                isEmpty(cityField) ||
                isEmpty(postalField)) {

            showError("Please fill in all required fields.");
            return;
        }

        saveGuestToSession();

        if (loyaltyYes.isSelected()) {
            navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_summaryscreen.fxml");
        } else {
            errorLabel.setText("Would you like to join our Loyalty Program?");
            yesRegisterButton.setVisible(true);
            noRegisterButton.setVisible(true);
        }
    }

    private void saveGuestToSession() {
        BookingSession.setGuestFullName(fullNameField.getText());
        BookingSession.setGuestPhone(phoneField.getText());
        BookingSession.setGuestEmail(emailField.getText());
        BookingSession.setGuestAddress(addressField.getText());
        BookingSession.setGuestCity(cityField.getText());
        BookingSession.setGuestPostal(postalField.getText());
    }

    @FXML
    private void onYesRegister(ActionEvent event) {
        saveGuestToSession();
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/loyaltyRegistration.fxml");
    }

    @FXML
    private void onNoRegister(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_summaryscreen.fxml");
    }

    @FXML
    private void onBack(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_addons.fxml");
    }

    private boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        yesRegisterButton.setVisible(false);
        noRegisterButton.setVisible(false);
    }
}
