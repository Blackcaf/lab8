package main.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import main.gui.NetworkClient;
import models.HumanBeing;
import models.WeaponType;
import utility.ExecutionResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AddHumanController {
    @FXML private TextField nameField;
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private TextField impactSpeedField;
    @FXML private CheckBox realHeroCheckBox;
    @FXML private ComboBox<String> hasToothpickBox;
    @FXML private ComboBox<WeaponType> weaponTypeBox;
    @FXML private ComboBox<String> moodBox;
    @FXML private TextField carNameField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Label errorLabel;

    private NetworkClient networkClient;
    private Integer userId;
    private String lang;
    private Runnable reloadCallback;

    public void init(NetworkClient networkClient, Integer userId, String lang, Runnable reloadCallback) {
        this.networkClient = networkClient;
        this.userId = userId;
        this.lang = lang;
        this.reloadCallback = reloadCallback;
    }

    @FXML
    private void initialize() {
        // Init enums and nullables
        hasToothpickBox.setItems(FXCollections.observableArrayList("true", "false", "null"));
        weaponTypeBox.setItems(FXCollections.observableArrayList(WeaponType.values()));
        moodBox.setItems(FXCollections.observableArrayList("SADNESS", "LONGING", "GLOOM", "CALM"));
        hasToothpickBox.getSelectionModel().clearSelection();
        weaponTypeBox.getSelectionModel().clearSelection();
        moodBox.getSelectionModel().clearSelection();

        // No red for ComboBoxes at start, only for text fields
        markInvalid(nameField, true);
        markInvalid(xField, true);
        markInvalid(yField, true);
        markInvalid(impactSpeedField, true);
        markInvalid(carNameField, true);

        // Listeners for validation with color change
        nameField.textProperty().addListener((obs, oldVal, newVal) -> markInvalid(nameField, newVal.trim().isEmpty()));
        xField.textProperty().addListener((obs, oldVal, newVal) -> markInvalid(xField, !isValidDouble(newVal)));
        yField.textProperty().addListener((obs, oldVal, newVal) -> markInvalid(yField, !isValidFloat(newVal)));
        impactSpeedField.textProperty().addListener((obs, oldVal, newVal) -> markInvalid(impactSpeedField, !isValidLong(newVal)));
        carNameField.textProperty().addListener((obs, oldVal, newVal) -> markInvalid(carNameField, newVal.trim().isEmpty()));

        // ComboBoxes: highlight green if valid, but never red
        hasToothpickBox.valueProperty().addListener((obs, oldVal, newVal) -> markComboValid(hasToothpickBox, newVal != null));
        weaponTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> markComboValid(weaponTypeBox, newVal != null));
        moodBox.valueProperty().addListener((obs, oldVal, newVal) -> markComboValid(moodBox, newVal != null));

        // Checkbox doesn't need color

        // Save button only enabled when all fields valid
        saveButton.setDisable(true);
        Runnable validator = this::validateAll;
        nameField.textProperty().addListener((obs, o, n) -> validator.run());
        xField.textProperty().addListener((obs, o, n) -> validator.run());
        yField.textProperty().addListener((obs, o, n) -> validator.run());
        impactSpeedField.textProperty().addListener((obs, o, n) -> validator.run());
        carNameField.textProperty().addListener((obs, o, n) -> validator.run());
        hasToothpickBox.valueProperty().addListener((obs, o, n) -> validator.run());
        weaponTypeBox.valueProperty().addListener((obs, o, n) -> validator.run());
        moodBox.valueProperty().addListener((obs, o, n) -> validator.run());

        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> cancelButton.getScene().getWindow().hide());
    }

    private void markInvalid(TextField f, boolean invalid) {
        f.getStyleClass().removeAll("valid", "invalid");
        f.getStyleClass().add(invalid ? "invalid" : "valid");
    }
    private void markComboValid(ComboBox<?> box, boolean valid) {
        box.getStyleClass().removeAll("valid", "invalid");
        if (valid) box.getStyleClass().add("valid");
        else box.getStyleClass().remove("valid"); // no red
    }

    private boolean isValidDouble(String val) {
        try { Double.parseDouble(val); return true; } catch (Exception e) { return false; }
    }
    private boolean isValidFloat(String val) {
        try { Float.parseFloat(val); return true; } catch (Exception e) { return false; }
    }
    private boolean isValidLong(String val) {
        try { Long.parseLong(val); return true; } catch (Exception e) { return false; }
    }

    private void validateAll() {
        boolean valid =
                !nameField.getText().trim().isEmpty() &&
                        isValidDouble(xField.getText()) &&
                        isValidFloat(yField.getText()) &&
                        isValidLong(impactSpeedField.getText()) &&
                        !carNameField.getText().trim().isEmpty() &&
                        hasToothpickBox.getValue() != null &&
                        weaponTypeBox.getValue() != null &&
                        moodBox.getValue() != null;
        saveButton.setDisable(!valid);
    }

    private void handleSave() {
        if (saveButton.isDisabled()) return;

        String name = nameField.getText().trim();
        double x = Double.parseDouble(xField.getText().trim());
        float y = Float.parseFloat(yField.getText().trim());
        boolean realHero = realHeroCheckBox.isSelected();
        String hasToothpickVal = hasToothpickBox.getValue();
        Boolean hasToothpick = hasToothpickVal.equals("null") ? null : Boolean.parseBoolean(hasToothpickVal);
        long impactSpeed = Long.parseLong(impactSpeedField.getText().trim());
        WeaponType weaponType = weaponTypeBox.getValue();
        String mood = moodBox.getValue();
        String carName = carNameField.getText().trim();

        HumanBeing human = new HumanBeing();
        human.setName(name);
        human.setCoordinates(new models.Coordinates(x, y));
        human.setImpactSpeed(impactSpeed);
        human.setRealHero(realHero);
        human.setHasToothpick(hasToothpick);
        human.setWeaponType(weaponType);
        human.setMood(mood);
        human.setCar(new models.Car(carName));

        ExecutionResponse response = networkClient.sendCommand("add", human, userId);
        if (response.isSuccess()) {
            Long id = response.getHumanBeing() != null ? response.getHumanBeing().getId() : null;
            showSuccessDialog("Element successfully added with id: " + id);
            if (reloadCallback != null) reloadCallback.run();
        } else {
            errorLabel.setText(response.getMessage());
        }
    }

    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
        saveButton.getScene().getWindow().hide();
    }
}