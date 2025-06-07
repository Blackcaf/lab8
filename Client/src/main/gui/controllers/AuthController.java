package main.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.gui.MainApp;
import main.gui.NetworkClient;
import models.Car;
import models.HumanBeing;
import utility.ExecutionResponse;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AuthController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private ComboBox<String> languageBox;
    @FXML private Label messageLabel;
    @FXML private Label welcomeLabel;
    @FXML private Label enterDetailsLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Button muteButton;
    @FXML private ImageView muteIcon;

    private NetworkClient networkClient;
    private Integer userId;
    private MediaPlayer mediaPlayer;
    private boolean isMuted = false;
    private int lastLanguageIndex = -1;

    private ResourceBundle bundle;

    @FXML
    private void initialize() {
        languageBox.getItems().setAll("Русский", "Македонский", "Latviešu", "Español (Colombia)");
        Locale current = MainApp.getLocale();
        int idx = 0;
        if (current.getLanguage().equals("ru")) idx = 0;
        else if (current.getLanguage().equals("mk")) idx = 1;
        else if (current.getLanguage().equals("lv")) idx = 2;
        else if (current.getLanguage().equals("es")) idx = 3;
        languageBox.getSelectionModel().select(idx);
        lastLanguageIndex = idx;

        bundle = MainApp.getBundle();
        updateTexts();

        languageBox.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            if (newVal.intValue() == lastLanguageIndex) return;
            lastLanguageIndex = newVal.intValue();
            Locale locale;
            switch (newVal.intValue()) {
                case 0: locale = new Locale("ru", "RU"); break;
                case 1: locale = new Locale("mk", "MK"); break;
                case 2: locale = new Locale("lv", "LV"); break;
                case 3: locale = new Locale("es", "CO"); break;
                default: locale = Locale.getDefault();
            }
            MainApp.setLocale(locale);
            java.util.ResourceBundle.clearCache();
            bundle = MainApp.getBundle();
            updateTexts();
        });
        initBackgroundMusic();
    }

    private void initBackgroundMusic() {
        String musicPath = getClass().getResource("/music/background.mp3").toExternalForm();
        Media media = new Media(musicPath);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Зацикливаем
        mediaPlayer.play();
    }

    @FXML
    private void onMuteMusic() {
        if (isMuted) {
            mediaPlayer.play();
            muteIcon.setImage(new Image(getClass().getResourceAsStream("/icons/volume_up.png")));
        } else {
            mediaPlayer.pause();
            muteIcon.setImage(new Image(getClass().getResourceAsStream("/icons/volume_off.png")));
        }
        isMuted = !isMuted;
    }

    private void updateTexts() {
        loginButton.setText(bundle.getString("auth.login"));
        registerButton.setText(bundle.getString("auth.register"));
        usernameLabel.setText(bundle.getString("auth.username"));
        passwordLabel.setText(bundle.getString("auth.password"));
        usernameField.setPromptText(bundle.getString("auth.username"));
        passwordField.setPromptText(bundle.getString("auth.password"));
        welcomeLabel.setText(bundle.getString("auth.welcome"));
        enterDetailsLabel.setText(bundle.getString("auth.enter_details"));
    }

    @FXML
    private void handleLogin(javafx.event.ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText(bundle.getString("auth.login.empty"));
            return;
        }

        if (networkClient != null) {
            try { networkClient.close(); } catch (IOException ignored) {}
        }
        networkClient = new NetworkClient();
        if (!networkClient.connect("localhost", 5000)) {
            messageLabel.setText(bundle.getString("auth.connection.error"));
            return;
        }
        HumanBeing user = new HumanBeing();
        user.setName(username);
        user.setCar(new Car(password));
        ExecutionResponse response = networkClient.sendCommand("login", user, null);
        if (response.isSuccess()) {
            try {
                userId = Integer.parseInt(response.getMessage());
                messageLabel.setText(bundle.getString("auth.login.success"));
                try {
                    MainApp.showMainWindow(networkClient, userId, username);
                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    stage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText(bundle.getString("auth.main.open.error") + ": " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                messageLabel.setText(bundle.getString("auth.userid.error"));
                try { networkClient.close(); } catch (IOException ignored) {}
            }
        } else {
            messageLabel.setText(bundle.getString("auth.login.fail") + " " + response.getMessage());
            try { networkClient.close(); } catch (IOException ignored) {}
        }
    }

    @FXML
    private void handleRegister(javafx.event.ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText(bundle.getString("auth.register.empty"));
            return;
        }

        if (networkClient != null) {
            try { networkClient.close(); } catch (IOException ignored) {}
        }
        networkClient = new NetworkClient();
        if (!networkClient.connect("localhost", 5000)) {
            messageLabel.setText(bundle.getString("auth.connection.error"));
            return;
        }
        HumanBeing user = new HumanBeing();
        user.setName(username);
        user.setCar(new Car(password));
        ExecutionResponse response = networkClient.sendCommand("register", user, null);
        if (response.isSuccess()) {
            messageLabel.setText(bundle.getString("auth.register.success"));
        } else {
            messageLabel.setText(bundle.getString("auth.register.fail") + " " + response.getMessage());
            try { networkClient.close(); } catch (IOException ignored) {}
        }
    }

    public NetworkClient getNetworkClient() {
        return networkClient;
    }
    public Integer getUserId() {
        return userId;
    }
}