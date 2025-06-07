package main.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.gui.controllers.MainWindowController;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    private static Locale currentLocale = new Locale("ru");
    private static ResourceBundle bundle = ResourceBundle.getBundle("main.gui.locale.messages", currentLocale);

    public static Locale getLocale() {
        return currentLocale;
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("main.gui.locale.messages", currentLocale);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        showAuthWindow();
    }

    public static void showAuthWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/main/gui/views/auth.fxml"), getBundle());
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("auth.title"));
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    public static void showMainWindow(NetworkClient client, Integer userId, String username) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/main/gui/views/main.fxml"), getBundle());
        Stage stage = new Stage();
        stage.setTitle(bundle.getString("main.title"));
        stage.setScene(new Scene(loader.load()));
        MainWindowController controller = loader.getController();
        controller.initSession(client, userId, username);
        stage.show();
    }
}