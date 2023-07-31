package fezas.decodez;

import com.dustinredmond.fxtrayicon.FXTrayIcon;
import fezas.decodez.controller.SettingController;
import fezas.decodez.entity.Setting;
import fezas.decodez.util.HibernateUtil;
import fezas.decodez.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import fezas.decodez.model.SettingModel;
import javafx.scene.control.Alert;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

public class MainApplication extends Application {
    private static final Logger logger = LogManager.getLogger();
    private static final String APPLICATION_NAME = "decodeZ";
    private SettingModel settingModel = new SettingModel();
    private static Scanner scanner;

    private static MainApplication instance;
    public MainApplication(){}
    public static synchronized MainApplication getInstance() {
        if (instance == null) {
            instance = new MainApplication();
        }
        return instance;
    }

    public void scannerReload() {
        if (scanner != null) {
            if (scanner.isAlive()) {
                scanner.interrupt();
            }
        } else {
            scanner = new Scanner();
            scanner.setDaemon(true);
            scanner.start();
        }
    }

    private void openCatalog(String catalog) {
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File(catalog));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage){
        createSystemTray();
        readSettings();
        //не даем начинать работать пока не настроена программа
        if (!validPath(readSettings().getInputDirectory()) | !validPath(readSettings().getOutputDirectory())) {
            sceneSetting(readSettings());
        }

        if (MainApplication.getInstance().readSettings().isAutoscan()) scannerReload();
        BorderPane root = new BorderPane();
        stage.setScene(new Scene(root));
        stage.setTitle("FXTrayIcon test!");

        FXTrayIcon trayIcon = new FXTrayIcon(stage, getClass().getResource("/image/icon.png"));
        trayIcon.show();
        // This method can override this
        trayIcon.setTrayIconTooltip("decodeZ");

        // We can now add JavaFX MenuItems to the menu
        MenuItem menuItemSetting = new MenuItem("Настройки");
        menuItemSetting.setOnAction(e -> sceneSetting(readSettings()));
        trayIcon.addMenuItem(menuItemSetting);

        MenuItem menuItemRecoding = new MenuItem("Перекодировать");
        menuItemRecoding.setOnAction(e -> {
            scannerReload();
        });
        trayIcon.addMenuItem(menuItemRecoding);

        MenuItem menuItemStat= new MenuItem("Статистика");
        menuItemStat.setOnAction(e ->
                new Alert(Alert.AlertType.INFORMATION, "Запущен процесс самоуничтожения!").showAndWait());
        trayIcon.addMenuItem(menuItemStat);

        MenuItem menuItemLog= new MenuItem("Лог");
        menuItemLog.setOnAction(e -> openCatalog("logs/"));
        trayIcon.addMenuItem(menuItemLog);

        // We can also nest menus, below is an Options menu with sub-items
        Menu     menuOptions = new Menu("Каталоги");
        MenuItem miOn        = new MenuItem("Исходники");
        miOn.setOnAction(e -> openCatalog(readSettings().getInputDirectory()));
        MenuItem miOff = new MenuItem("DOC файлы");
        miOff.setOnAction(e -> openCatalog(readSettings().getOutputDirectory()));
        menuOptions.getItems().addAll(miOn, miOff);
        trayIcon.addMenuItem(menuOptions);

        MenuItem menuItemExit= new MenuItem("Выход");
        menuItemExit.setOnAction(e -> showConfirmationExit());
        trayIcon.addMenuItem(menuItemExit);
    }

    private boolean validPath(String stringPath) {
        Path path = Paths.get(stringPath);
        if (path.isAbsolute() && Files.exists(path)) {
            return true;
        }
        else {
            return false;
        }
    }



    /**
     * Функция загрузки настроек
     */
    public Setting readSettings() {
        return settingModel.getSetting();
    }

    /**
     * Функция проверки поддержки System Tray  {@link SystemTray}
     */
    private void createSystemTray() {
        //Проверка поддрежки системой
        if(!SystemTray.isSupported()){
            String s = "System tray не поддерживается";
            showAlert("Ошибка", s);
            logger.error("ERROR: ", s);
            System.exit(0);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfirmationExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Выход из программы");
        alert.setHeaderText("Вы уверены что хотите выйти из программы и прекратить сканирование");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null) {
        } else if (option.get() == ButtonType.OK) {
            System.exit(0);
        }
    }

    private static String timeOperation() {
        Instant instant = Instant.ofEpochMilli(System.currentTimeMillis());
        Date date = Date.from(instant);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM HH:mm:ss");
        String formattedDate = formatter.format(date);
        return formattedDate;
    }

    public Stage sceneSetting(Setting setting) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/settings.fxml"));
        Stage stage = new Stage();
        try {
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Настройки");
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setResizable(false);
            SettingController controller = loader.getController();
            controller.setSetting(setting);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stage;
    }


    public static void main(String[] args) {
        logger.info("Старт программы: " + timeOperation());
        HibernateUtil.getSessionFactory().openSession();
        launch();
    }
}