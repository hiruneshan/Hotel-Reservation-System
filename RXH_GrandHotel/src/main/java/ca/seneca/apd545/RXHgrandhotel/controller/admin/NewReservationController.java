package ca.seneca.apd545.RXHgrandhotel.controller.admin;

import ca.seneca.apd545.RXHgrandhotel.model.Room;
import ca.seneca.apd545.RXHgrandhotel.repository.RoomRepository;
import ca.seneca.apd545.RXHgrandhotel.service.BookingService;
import ca.seneca.apd545.RXHgrandhotel.service.ReportingService;
import ca.seneca.apd545.RXHgrandhotel.util.LoggerUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class NewReservationController {

    private final BookingService bookingService;
    private final RoomRepository roomRepository;
    private final ReportingService reportingService;

    @FXML private TextField guestNameField, phoneField, emailField, loyaltyNumberField;
    @FXML private TextField addressField, cityField, provinceField, postalField;
    @FXML private DatePicker checkInPicker, checkOutPicker;
    @FXML private Spinner<Integer> adultsSpinner, childrenSpinner;
    @FXML private Label stayValidationLabel;
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String> colRoomType;
    @FXML private TableColumn<Room, Integer> colQuantity;
    @FXML private TableColumn<Room, Integer> colOccupancy;
    @FXML private TableColumn<Room, Double> colRate;
    @FXML private Label subtotalLabel, taxLabel, discountLabel, totalAmountLabel;
    @FXML private Button addRoomButton, removeRoomButton, createReservationButton, cancelButton;

    private final ObservableList<Room> selectedRooms = FXCollections.observableArrayList();


    public NewReservationController(BookingService bookingService,
                                    RoomRepository roomRepository,
                                    ReportingService reportingService) {
        this.bookingService = bookingService;
        this.roomRepository = roomRepository;
        this.reportingService = reportingService;
    }

    @FXML
    public void initialize() {
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colOccupancy.setCellValueFactory(new PropertyValueFactory<>("maxAdults"));
        colRate.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        colQuantity.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(1));

        checkInPicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePricing());
        checkOutPicker.valueProperty().addListener((obs, oldVal, newVal) -> updatePricing());

        roomTable.setItems(selectedRooms);

        adultsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        childrenSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));

        addRoomButton.setOnAction(e -> handleAddRoom());
        removeRoomButton.setOnAction(e -> handleRemoveRoom());
        createReservationButton.setOnAction(e -> handleCreateReservation());
        cancelButton.setOnAction(e -> navigateToDashboard());
    }

    private void handleAddRoom() {

        if (checkInPicker.getValue() == null || checkOutPicker.getValue() == null) {
            showAlert("Select Dates First", "Please select check-in and check-out dates before adding rooms.");
            return;
        }


        List<Room> allAvailableRooms = roomRepository.findAvailableRooms(
                checkInPicker.getValue(),
                checkOutPicker.getValue()
        );

        List<Room> validOptions = allAvailableRooms.stream()
                .filter(r -> !selectedRooms.contains(r))
                .collect(java.util.stream.Collectors.toList());

        if (validOptions.isEmpty()) {
            showAlert("No Availability", "No rooms available for the selected dates.");
            return;
        }

        List<String> choices = validOptions.stream()
                .map(r -> "Room " + r.getRoomNumber() + " (" + r.getType() + ") - $" + r.getBasePrice() + "/night")
                .collect(java.util.stream.Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Select Room");
        dialog.setHeaderText("Available rooms for " + checkInPicker.getValue() + " to " + checkOutPicker.getValue());
        dialog.setContentText("Room:");

        dialog.showAndWait().ifPresent(chosenString -> {
            Room chosen = validOptions.get(choices.indexOf(chosenString));
            selectedRooms.add(chosen);
            updatePricing();
            LoggerUtil.info("ADMIN", "ADD_ROOM", "Room", chosen.getRoomNumber(),
                    "Added room to group booking.");
        });
    }

    private void handleRemoveRoom() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selectedRooms.remove(selected);
            updatePricing();
        }
    }

    private void updatePricing() {
        if (checkInPicker.getValue() == null || checkOutPicker.getValue() == null) {
            return;
        }

        double subtotal = bookingService.calculateRoomsSubtotal(
                selectedRooms,
                checkInPicker.getValue(),
                checkOutPicker.getValue()
        );

        double tax = subtotal * 0.13;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("$%.2f", subtotal));
        taxLabel.setText(String.format("$%.2f", tax));
        totalAmountLabel.setText(String.format("$%.2f", total));
    }



    private void handleCreateReservation() {
        int totalCapacity = selectedRooms.stream().mapToInt(Room::getMaxAdults).sum();
        int requestedAdults = adultsSpinner.getValue();

        if (requestedAdults > totalCapacity) {
            showAlert("Occupancy Error", "Selected rooms cannot accommodate " + requestedAdults + " adults.");
            return;
        }
        try {

            if (guestNameField.getText().isEmpty() ||
                    phoneField.getText().isEmpty() ||
                    emailField.getText().isEmpty() ||
                    addressField.getText().isEmpty() ||
                    cityField.getText().isEmpty() ||
                    provinceField.getText().isEmpty() ||
                    postalField.getText().isEmpty()) {
                showAlert("Validation Error", "Please fill in all required guest details.");
                return;
            }

            if (selectedRooms.isEmpty()) {
                showAlert("Validation Error", "Please select at least one room.");
                return;
            }

            if (checkInPicker.getValue() == null || checkOutPicker.getValue() == null) {
                showAlert("Validation Error", "Please select check-in and check-out dates.");
                return;
            }


            bookingService.createAdminReservation(
                    guestNameField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    addressField.getText(),
                    cityField.getText(),
                    provinceField.getText(),
                    postalField.getText(),
                    "Canada",
                    checkInPicker.getValue(),
                    checkOutPicker.getValue(),
                    adultsSpinner.getValue(),
                    childrenSpinner.getValue(),
                    selectedRooms
            );


            LoggerUtil.info("ADMIN", "CREATE_RESERVATION", "Reservation", "NEW",
                    "Admin created booking for: " + guestNameField.getText());

            showAlert("Success", "Reservation has been saved successfully.");
            navigateToDashboard();

            reportingService.addLog("Admin", "CREATE", "Reservation", "NEW",
                    "Admin created booking for: " + guestNameField.getText());

        } catch (Exception e) {
            LoggerUtil.error("RESERVATION_SAVE_FAILED", e);
            showAlert("System Error", "Error saving reservation: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToDashboard() {
        try {

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/ca/seneca/apd545/RXHgrandhotel/view/admin/admin_dashboard.fxml"));


            loader.setControllerFactory(ca.seneca.apd545.RXHgrandhotel.app.AppConfig.getInstance()::createController);

            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) createReservationButton.getScene().getWindow();

            stage.getScene().setRoot(root);
            stage.setTitle("Admin Dashboard - RXH Grand Hotel");

        } catch (java.io.IOException e) {
            LoggerUtil.error("NAVIGATION_ERROR", e);
            showAlert("Navigation Error", "Could not return to Dashboard.");
        }
    }
}