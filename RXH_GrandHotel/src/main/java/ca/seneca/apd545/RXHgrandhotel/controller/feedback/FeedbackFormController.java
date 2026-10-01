package ca.seneca.apd545.RXHgrandhotel.controller.feedback;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import ca.seneca.apd545.RXHgrandhotel.session.BookingSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FeedbackFormController {

    @FXML private Label guestNameLabel;
    @FXML private Label guestEmailLabel;
    @FXML private Label guestPhoneLabel;
    @FXML private Label bookingIdLabel;

    @FXML private Button star1;
    @FXML private Button star2;
    @FXML private Button star3;
    @FXML private Button star4;
    @FXML private Button star5;

    @FXML private TextArea commentsField;
    @FXML private Label confirmationLabel;

    private int rating = 0;

    @FXML
    public void initialize() {
        loadGuestInfo();
        setupStarButtons();
    }

    private void loadGuestInfo() {
        guestNameLabel.setText(BookingSession.getGuestFullName());
        guestEmailLabel.setText(BookingSession.getGuestEmail());
        guestPhoneLabel.setText(BookingSession.getGuestPhone());
        bookingIdLabel.setText("BK-" + System.currentTimeMillis());
    }

    private void setupStarButtons() {
        star1.setOnAction(e -> setRating(1));
        star2.setOnAction(e -> setRating(2));
        star3.setOnAction(e -> setRating(3));
        star4.setOnAction(e -> setRating(4));
        star5.setOnAction(e -> setRating(5));
    }

    private void setRating(int value) {
        rating = value;
        Button[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            if (i < value) {
                stars[i].setText((i + 1) + " *");
                stars[i].setStyle("-fx-font-size: 16px; -fx-background-color: #f0c040; -fx-font-weight: bold;");
            } else {
                stars[i].setText((i + 1) + "");
                stars[i].setStyle("-fx-font-size: 16px; -fx-background-color: #e0e0e0;");
            }
        }
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
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_welcome.fxml");
    }

    @FXML
    private void onCancel(ActionEvent event) {
        navigate(event, "/ca/seneca/apd545/RXHgrandhotel/view/kiosk/kiosk_welcome.fxml");
    }

    @FXML
    private void onClear(ActionEvent event) {
        commentsField.clear();
        Button[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            stars[i].setText((i + 1) + "");
            stars[i].setStyle("-fx-font-size: 16px; -fx-background-color: #e0e0e0;");
        }
        rating = 0;
        confirmationLabel.setText("");
    }

    @FXML
    private void onSubmit(ActionEvent event) {
        if (rating == 0) {
            showError("Please select a star rating before submitting.");
            return;
        }

        if (commentsField.getText().trim().isEmpty()) {
            showError("Please enter your comments before submitting.");
            return;
        }

        confirmationLabel.setText("Thank you! Your feedback has been submitted successfully.");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Missing Information");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}