package ca.seneca.apd545.RXHgrandhotel.controller.kiosk;

import ca.seneca.apd545.RXHgrandhotel.model.enums.RoomType;
import ca.seneca.apd545.RXHgrandhotel.session.BookingSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class KioskRoomSelectionController {

    @FXML private TextField singleCount;
    @FXML private TextField doubleCount;
    @FXML private TextField suiteCount;

    private void navigate(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load: " + fxmlPath);
        }
    }

    private int getCount(TextField field) {
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setCount(TextField field, int value) {
        if (value < 0) value = 0;
        field.setText(String.valueOf(value));
    }

    @FXML private void onSinglePlus() { setCount(singleCount, getCount(singleCount) + 1); }
    @FXML private void onSingleMinus() { setCount(singleCount, getCount(singleCount) - 1); }

    @FXML private void onDoublePlus() { setCount(doubleCount, getCount(doubleCount) + 1); }
    @FXML private void onDoubleMinus() { setCount(doubleCount, getCount(doubleCount) - 1); }

    @FXML private void onSuitePlus() { setCount(suiteCount, getCount(suiteCount) + 1); }
    @FXML private void onSuiteMinus() { setCount(suiteCount, getCount(suiteCount) - 1); }

    @FXML
    private void onBack(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_guestselection.fxml");
    }

    @FXML
    private void onNext(ActionEvent event) {
        int singles = getCount(singleCount);
        int doubles = getCount(doubleCount);
        int suites = getCount(suiteCount);

        if (singles + doubles + suites == 0) {
            showError("Please select at least one room.");
            return;
        }

        BookingSession.setRoomQuantity(RoomType.SINGLE, singles);
        BookingSession.setRoomQuantity(RoomType.DOUBLE, doubles);
        BookingSession.setRoomQuantity(RoomType.SUITE, suites);


        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_addons.fxml");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid Selection");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
