package ca.seneca.apd545.RXHgrandhotel.controller.kiosk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;

public class KioskRulesController {

    @FXML
    private TextArea rulesText;

    @FXML
    public void initialize() {
        rulesText.setText("""
                • Check-in time is 3:00 PM.
                • Check-out time is 11:00 AM.
                • No smoking inside the hotel.
                • Pets are not allowed unless stated otherwise.
                • Please respect quiet hours after 10:00 PM.
                """);
    }

    @FXML
    private void onBack(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_welcome.fxml");
    }

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
