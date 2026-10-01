package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.app.AppConfig;
import ca.seneca.apd545.RXHgrandhotel.model.Reservation;
import ca.seneca.apd545.RXHgrandhotel.model.enums.ReservationStatus;
import ca.seneca.apd545.RXHgrandhotel.service.ReservationService;
import ca.seneca.apd545.RXHgrandhotel.security.AuthService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.List;

import java.io.IOException;
import java.time.LocalDate;

public class AdminDashboardController {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Long> colReservationId;
    @FXML private TableColumn<Reservation, String> colGuestName;
    @FXML private TableColumn<Reservation, ReservationStatus> colStatus;
    @FXML private TableColumn<Reservation, Double> colDiscount;
    @FXML private TableColumn<Reservation, Double> colTotal;
    @FXML private TableColumn<Reservation, String> colPhone;
    @FXML private TableColumn<Reservation, LocalDate> colCheckIn;
    @FXML private TableColumn<Reservation, LocalDate> colCheckOut;
    @FXML private ComboBox<ReservationStatus> statusFilterCombo;
    @FXML private DatePicker fromDatePicker, toDatePicker;
    @FXML private TextField nameFilterField, phoneFilterField;
    @FXML private Button searchButton, logoutButton, newReservationButton, activityLogButton,
            reportsButton, applyDiscountButton, editReservationButton, cancelReservationButton,
            paymentsButton, waitlistButton, loyaltyButton, feedbackButton, seasonalPromoButton;

    private final ReservationService reservationService;
    private final AuthService authService;

    public AdminDashboardController(ReservationService reservationService, AuthService authService) {
        this.reservationService = reservationService;
        this.authService = authService;
    }


    @FXML private TableColumn<Reservation, Double> colBalance;
    @FXML private TableColumn<Reservation, String> colPaymentStatus;
    @FXML private TableColumn<Reservation, String> colLoyalty;

    @FXML private TextField bookingIdFilterField;
    @FXML private ComboBox<String> loyaltyFilterCombo;

    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;

    @FXML
    public void initialize() {
        setupTable();
        loadData();

        logoutButton.setOnAction(e -> navigate("/ca/seneca/apd545/RXHgrandhotel/view/admin/admin_login.fxml", "Admin Login"));
        newReservationButton.setOnAction(e -> navigate("/ca/seneca/apd545/RXHgrandhotel/view/admin/admin_newreservation.fxml", "New Reservation"));

        statusFilterCombo.setItems(FXCollections.observableArrayList(ReservationStatus.values()));
        searchButton.setOnAction(e -> handleSearch());

        applyDiscountButton.setOnAction(e -> handleSelectedAction(this::openDiscountWindow));
        editReservationButton.setOnAction(e -> handleSelectedAction(this::openEditorWindow));
        cancelReservationButton.setOnAction(e -> handleSelectedAction(this::openCancelWindow));
        paymentsButton.setOnAction(e -> handleSelectedAction(this::openPaymentWindow));

        // Tool Actions
        activityLogButton.setOnAction(e -> navigate("/ca/seneca/apd545/RXHgrandhotel/view/admin/activitylog.fxml", "Activity Logs"));
        reportsButton.setOnAction(e -> navigate("/ca/seneca/apd545/RXHgrandhotel/view/admin/reports.fxml", "System Reports"));
        waitlistButton.setOnAction(e -> openModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/waitlist.fxml", "Waitlist"));
        loyaltyButton.setOnAction(e -> openModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/loyalty.fxml", "Loyalty"));
        feedbackButton.setOnAction(e -> openModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/feedback_viewer.fxml", "Feedback"));
        seasonalPromoButton.setOnAction(e -> openModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/seasonal_promotion.fxml", "Seasonal Promotions"));
    }

    private void handleSelectedAction(java.util.function.Consumer<Reservation> action) {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            action.accept(selected);
            loadData(); // REFRESH table after modal closes
        } else {
            showAlert("Selection Required", "Please select a reservation from the table first.");
        }
    }

    private void setupTable() {

        colReservationId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));


        colPaymentStatus.setCellValueFactory(cellData -> {
            Reservation res = cellData.getValue();
            double total = res.getTotalAmount();
            double outstanding = res.getOutstandingBalance();
            double paid = res.getAmountPaid();

            if (total <= 0) {
                return new javafx.beans.property.SimpleStringProperty("PENDING (No Rate)");
            }

            if (outstanding <= 0) {
                return new javafx.beans.property.SimpleStringProperty("SETTLED");
            }
            if (paid > 0) {
                return new javafx.beans.property.SimpleStringProperty("PARTIAL");
            }
            return new javafx.beans.property.SimpleStringProperty("PENDING");
        });


        colLoyalty.setCellValueFactory(cellData -> {
            var account = cellData.getValue().getGuest().getLoyaltyAccount();
            return new javafx.beans.property.SimpleStringProperty(account != null ? account.getStatus() : "N/A");
        });

        colGuestName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGuest().getFullName()));
        colPhone.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getGuest().getPhone()));


        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));


        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discountAmount"));

        colBalance.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getOutstandingBalance())
        );


        colBalance.setCellFactory(tc -> new TableCell<Reservation, Double>() {
            @Override
            protected void updateItem(Double balance, boolean empty) {
                super.updateItem(balance, empty);
                if (empty || balance == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", balance));
                }
            }
        });
    }

    private void loadData() {
        List<Reservation> latestData = reservationService.findAll();
        reservationTable.setItems(FXCollections.observableArrayList(latestData));
        reservationTable.refresh();
    }


    private void handleSearch() {
        var filteredResults = reservationService.searchReservations(
                nameFilterField.getText(), phoneFilterField.getText(),
                statusFilterCombo.getValue(), fromDatePicker.getValue(), toDatePicker.getValue());
        reservationTable.setItems(FXCollections.observableArrayList(filteredResults));
    }

    private void navigate(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(AppConfig.getInstance()::createController);
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openDiscountWindow(Reservation res) {
        openGenericModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/discount.fxml", "Apply Discount", res);
    }

    private void openEditorWindow(Reservation res) {
        openGenericModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/reservation_editor.fxml", "Edit Reservation", res);
    }

    private void openCancelWindow(Reservation res) {
        openGenericModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/cancel_reservation.fxml", "Cancel Reservation", res);
    }

    private void openPaymentWindow(Reservation res) {
        openGenericModal("/ca/seneca/apd545/RXHgrandhotel/view/admin/payment.fxml", "Process Payment", res);
    }

    private void openGenericModal(String fxml, String title, Reservation res) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setControllerFactory(AppConfig.getInstance()::createController);
            Parent root = loader.load();


            Object controller = loader.getController();
            if (controller instanceof DiscountController) ((DiscountController)controller).setReservation(res);
            else if (controller instanceof ReservationEditorController) ((ReservationEditorController)controller).setReservation(res);
            else if (controller instanceof CancelReservationController) ((CancelReservationController)controller).setReservation(res);
            else if (controller instanceof PaymentController) ((PaymentController)controller).setReservation(res);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void openModal(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(AppConfig.getInstance()::createController);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadData();
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}