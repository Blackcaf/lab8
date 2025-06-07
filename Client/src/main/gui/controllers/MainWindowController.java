package main.gui.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.gui.MainApp;
import main.gui.NetworkClient;
import models.HumanBeing;
import utility.ExecutionResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainWindowController {
    @FXML private ComboBox<String> languageBox;
    @FXML private ListView<String> commandListView;
    @FXML private Label commandTitle;
    @FXML private TextArea commandOutput;
    @FXML private Button executeButton;
    @FXML private TextField argumentField;
    @FXML private Button registerButton;
    @FXML private Button exitButton;
    @FXML private Label usernameLabel;

    private String username;
    private NetworkClient networkClient;
    private Integer userId;

    private ResourceBundle bundle;

    private int lastLanguageIndex = -1;

    private final List<String> commands = Arrays.asList(
            "add", "update", "remove_head", "clear", "info", "show",
            "help", "filter_starts", "print_unique",
            "count_less", "execute_script", "removebyid"
    );

    public void initSession(NetworkClient networkClient, Integer userId, String username) {
        this.networkClient = networkClient;
        this.userId = userId;
        this.username = username;
        usernameLabel.setText("Пользователь: " + username);
        List<String> visibleCommands = new ArrayList<>(commands);
        commandListView.setItems(FXCollections.observableArrayList(visibleCommands));
    }

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

        commandListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            commandTitle.setText(newVal == null ? "" : newVal);
            commandOutput.clear();
            argumentField.clear();
        });
        commandListView.getSelectionModel().selectFirst();

        executeButton.setOnAction(event -> executeSelectedCommand());
        argumentField.setOnAction(event -> executeSelectedCommand());
        registerButton.setOnAction(event -> goToRegister());
        exitButton.setOnAction(event -> exitApp());
    }

    /** Обновляет все тексты на языке bundle */
    private void updateTexts() {
        executeButton.setText(bundle.getString("main.execute"));
        registerButton.setText(bundle.getString("main.register"));
        exitButton.setText(bundle.getString("main.exit"));
        commandTitle.setText(bundle.getString("main.commandTitle"));
        argumentField.setPromptText(bundle.getString("main.argument"));
    }

    private void executeSelectedCommand() {
        String command = commandListView.getSelectionModel().getSelectedItem();
        if (command == null) return;
        String arg = argumentField.getText().trim();
        Object argument = null;

        try {
            switch (command) {
                case "add":
                    openAddHumanDialog(null);
                    return;
                case "update":
                    if (arg.isEmpty()) {
                        commandOutput.setText(bundle.getString("main.update.need_id"));
                        return;
                    }
                    try {
                        Long id = Long.parseLong(arg);
                        HumanBeing humanBeing = new HumanBeing();
                        humanBeing.setId(id);
                        ExecutionResponse getResp = networkClient.sendCommand("get_by_id", humanBeing, userId);
                        if (!getResp.isSuccess() || getResp.getHumanBeing() == null) {
                            commandOutput.setText(bundle.getString("main.update.not_found"));
                            return;
                        }
                        openAddHumanDialog(getResp.getHumanBeing());
                    } catch (NumberFormatException e) {
                        commandOutput.setText(bundle.getString("main.update.id_number"));
                    }
                    return;
                case "removebyid":
                    if (arg.isEmpty()) {
                        commandOutput.setText(bundle.getString("main.removebyid.need_id"));
                        return;
                    }
                    try {
                        Long id = Long.parseLong(arg);
                        HumanBeing removeHuman = new HumanBeing();
                        removeHuman.setId(id);
                        argument = removeHuman;
                    } catch (NumberFormatException e) {
                        commandOutput.setText(bundle.getString("main.removebyid.id_number"));
                        return;
                    }
                    break;
                case "count_less":
                    if (arg.isEmpty()) {
                        commandOutput.setText(bundle.getString("main.count_less.need_value"));
                        return;
                    }
                    try {
                        long impactSpeed = Long.parseLong(arg);
                        HumanBeing countHuman = new HumanBeing();
                        countHuman.setImpactSpeed(impactSpeed);
                        argument = countHuman;
                    } catch (NumberFormatException e) {
                        commandOutput.setText(bundle.getString("main.count_less.value_number"));
                        return;
                    }
                    break;
                case "filter_starts":
                    if (arg.isEmpty()) {
                        commandOutput.setText(bundle.getString("main.filter_starts.need_value"));
                        return;
                    }
                    HumanBeing filterHuman = new HumanBeing();
                    filterHuman.setName(arg);
                    argument = filterHuman;
                    break;
                case "execute_script":
                    if (arg.isEmpty()) {
                        commandOutput.setText(bundle.getString("main.execute_script.need_file"));
                        return;
                    }
                    HumanBeing scriptHuman = new HumanBeing();
                    scriptHuman.setName(arg);
                    argument = scriptHuman;
                    break;
                default:
                    argument = null;
            }
        } catch (Exception e) {
            commandOutput.setText(bundle.getString("main.error") + ": " + e.getMessage());
            return;
        }

        if (command.equals("help") || command.equals("info") || command.equals("show") || command.equals("clear")) {
            String currentLang = MainApp.getLocale().getLanguage();
            HumanBeing langArg = new HumanBeing();
            langArg.setName(currentLang);
            ExecutionResponse response = networkClient.sendCommand(command, langArg, userId);

            if ("help".equals(command)) {
                String filtered = Arrays.stream(response.getMessage().split("\\r?\\n"))
                        .filter(line -> !line.trim().startsWith("login") && !line.trim().startsWith("register"))
                        .collect(Collectors.joining("\n"));
                commandOutput.setText(filtered);
            } else {
                commandOutput.setText(response.getMessage());
            }
        } else {
            ExecutionResponse response = networkClient.sendCommand(command, (HumanBeing) argument, userId);
            commandOutput.setText(response.getMessage());
        }
    }

    private void openAddHumanDialog(HumanBeing humanBeing) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/gui/views/add_human.fxml"));
            loader.setResources(MainApp.getBundle());
            Parent root = loader.load();
            AddHumanController controller = loader.getController();
            
            if (humanBeing == null) {
                controller.init(networkClient, userId, MainApp.getLocale().getLanguage(), this::refreshData);
            } else {
                controller.initForUpdate(networkClient, userId, humanBeing, MainApp.getLocale().getLanguage(), this::refreshData);
            }

            Stage stage = new Stage();
            stage.setTitle(humanBeing == null ? bundle.getString("main.add.title") : bundle.getString("main.update.title"));
            stage.setScene(new Scene(root));
            stage.initOwner(executeButton.getScene().getWindow());
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToRegister() {
        try {
            if (networkClient != null) {
                networkClient.close();
            }
        } catch (Exception ignored) {}
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();
            MainApp.showAuthWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exitApp() {
        try {
            networkClient.sendCommand("exit", null, userId);
            networkClient.close();
        } catch (Exception ignored) {}
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void refreshData() {
        // Место для обновления данных после добавления/редактирования HumanBeing
    }
}