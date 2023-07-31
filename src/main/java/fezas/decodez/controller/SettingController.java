package fezas.decodez.controller;

import fezas.decodez.MainApplication;
import fezas.decodez.entity.Setting;
import fezas.decodez.model.SettingModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SettingController implements Initializable {
    @FXML    private Button btnInputDir, btnOutputDir, btnSave, btnCancel;
    @FXML    private TextField tfInputDir, tfOutputDir, tfTimeRecoding;
    @FXML    private CheckBox switchScan;
    @FXML
    void save() {
        SettingModel settingModel = new SettingModel();
        Setting setting = new Setting();
        setting.setId(0L);
        setting.setInputDirectory(tfInputDir.getText());
        setting.setOutputDirectory(tfOutputDir.getText());
        setting.setTimeDecode(Integer.parseInt(tfTimeRecoding.getText()));
        setting.setAutoscan(switchScan.isSelected());
        settingModel.saveOrUpdateCategory(setting);
        MainApplication.getInstance().readSettings();
        if(switchScan.isSelected()) MainApplication.getInstance().scannerReload();
        cancel();
    }

    @FXML
    void cancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void selectCatWhithInputFile() {
        selectCat(btnInputDir, tfInputDir, "Выбор каталога для исходных файлов");
    }

    @FXML
    void selectCatWhithDoc() {
        selectCat(btnOutputDir, tfOutputDir, "Выбор каталога для Doc файлов");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnInputDir.setGraphic(new FontIcon("anto-folder-open:16"));
        btnOutputDir.setGraphic(new FontIcon("anto-folder-open:16"));
        btnSave.setGraphic(new FontIcon("anto-save:16"));
        switchScan.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) tfTimeRecoding.setDisable(false);
                else tfTimeRecoding.setDisable(true);
            }
        });
        tfTimeRecoding.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        tfTimeRecoding.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                    if (newValue.equals("")) btnSave.setDisable(true);
                    else btnSave.setDisable(false);
                });
    }

    void selectCat(Button button, TextField textField, String str) {
        DirectoryChooser directoryTlgChooser = new DirectoryChooser();
        directoryTlgChooser.setTitle(str);
        String path = textField.getText();
        if (Paths.get(path).isAbsolute() && Files.exists(Paths.get(path))) {
            File dir = new File(textField.getText());
            directoryTlgChooser.setInitialDirectory(dir);
        } else directoryTlgChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File dir = directoryTlgChooser.showDialog(button.getScene().getWindow());
        if (dir != null) {
            textField.setText(dir.getAbsolutePath());
        } else textField.setText(path);
    }

    public void setSetting (Setting setting) {
        tfInputDir.setText(setting.getInputDirectory());
        tfOutputDir.setText(setting.getOutputDirectory());
        tfTimeRecoding.setText(String.valueOf(setting.getTimeDecode()));
        switchScan.setSelected(setting.isAutoscan());
        if (setting.isAutoscan()) tfTimeRecoding.setDisable(false);
        else tfTimeRecoding.setDisable(true);
    }
}
