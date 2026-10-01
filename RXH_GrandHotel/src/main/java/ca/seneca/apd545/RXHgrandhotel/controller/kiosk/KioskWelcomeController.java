package ca.seneca.apd545.RXHgrandhotel.controller.kiosk;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KioskWelcomeController {

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
            System.err.println("Failed to load: " + fxmlPath);
        }
    }

    @FXML
    private void onStartBooking(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_guestselection.fxml");
    }

    @FXML
    private void onFeedback(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/feedback/feedback_form.fxml");
    }

    @FXML
    private void onRules(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_rules.fxml");
    }

    @FXML
    private void onAdminLogin(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/admin/admin_login.fxml");
    }
}