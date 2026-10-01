package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import ca.seneca.apd545.RXHgrandhotel.model.enums.Role;
import ca.seneca.apd545.RXHgrandhotel.security.AuthService;
import ca.seneca.apd545.RXHgrandhotel.security.AuthService.LoginResult;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminLoginController {

    @FXML private ComboBox<Role> roleCombo;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label loginFeedbackLabel;

    private AuthService authService;

    public AdminLoginController(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    public void initialize() {
        authService = AppConfig.getInstance().getAuthService();

        roleCombo.getItems().addAll(Role.ADMIN, Role.MANAGER);

        loginButton.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());

        usernameField.textProperty().addListener((o, old, val) -> clearFeedback());
        passwordField.textProperty().addListener((o, old, val) -> clearFeedback());
        roleCombo.valueProperty().addListener((o, old, val) -> clearFeedback());
    }

    private void handleLogin() {
        String username     = usernameField.getText();
        String password     = passwordField.getText();
        Role   selectedRole = roleCombo.getValue();

        LoginResult result = authService.login(username, password, selectedRole);

        if (result.isSuccess()) {
            showFeedback("Welcome, " + result.getUser().getFullName() + "!", false);
            navigateToDashboard();
        } else {
            showFeedback(result.getMessage(), true);
            passwordField.clear();
        }
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/ca/seneca/apd545/RXHgrandhotel/view/admin/admin_dashboard.fxml"));

            loader.setControllerFactory(type -> AppConfig.getInstance().createController(type)); // ✅ FIXED

            Parent root  = loader.load();
            Stage  stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("RXH Grand Hotel - Admin Dashboard ["
                    + authService.getCurrentUser().getRole() + "]");

        } catch (Exception e) {
            showFeedback("Failed to load dashboard. Contact support.", true);
            e.printStackTrace();
        }
    }

    private void showFeedback(String message, boolean isError) {
        loginFeedbackLabel.setText(message);
        loginFeedbackLabel.setStyle(
                "-fx-font-family: 'Arial'; -fx-text-fill: " + (isError ? "red" : "green") + ";");
    }

    private void clearFeedback() {
        loginFeedbackLabel.setText("");
    }
}