package com.esmo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import com.esmo.controller.SettingsController;
import com.esmo.model.FileModel;
import com.esmo.model.Storage;
import com.esmo.view.AppView;
import com.esmo.view.SettingsView;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class App extends Application {
    final private int WINDOW_WIDTH = 600;
    final private int WINDOW_HEIGHT = 300;

    protected static String folderPath = "";

    private TextField folderPathField;
    private PropertiesController properties;

    private Storage model;
    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        properties = new PropertiesController();
        if (properties.getProperty("folderPath") == null) {
            properties.setProperty("folderPath", initFolderPath());
        }
        folderPath = properties.getProperty("folderPath");

        AppView view = new AppView();
        // model = new DatabaseModel("jdbc:sqlite:notes.db");
        model = new FileModel(Path.of(folderPath));
        controller = new Controller(view, model);

        view.getSettingsButton().setOnAction(e -> openSettings(primaryStage));

        Scene scene = buildScene(view.getPane());
        primaryStage.setScene(scene);
        primaryStage.setTitle("esmonotes");
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            if (controller.hasUnsavedChanges() && view.getListView().getSelectionModel().getSelectedItem() != null) {
                Alerts.askSave(view.getNameField().getText()).ifPresent(response -> {
                    if (response != ButtonType.OK) {
                        e.consume();
                    }
                });
            }
            properties.saveProperties();
        });

    }

    private Scene buildScene(Pane pane) {
        Scene scene = new Scene(pane, WINDOW_WIDTH, WINDOW_HEIGHT);
        KeyCombination controlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        KeyCombination controlN = new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN);
        scene.setOnKeyPressed(keyPressed -> {
            if (controlS.match(keyPressed)) {
                System.out.println("shortcut \"Save\" used");
                controller.saveFile();
            } else if (controlN.match(keyPressed)) {
                System.out.println("shortcut \"New File\" used");
                controller.createNewFile();
            }
        });
        return scene;
    }

    private String initFolderPath() {

        TextField textField = new TextField();
        textField.setPrefWidth(200);

        Button button = new Button("...");
        button.setOnAction(e -> textField.setText(showDirectoryChooser()));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(new Label("Folder Path:"), new HBox(textField,
                button));

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Folder Not Found");
        alert.setHeaderText("Enter a Folder to Open:");
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        String value = textField.getText();
        System.out.println("Entered value: " + value);
        return value;
    }

    private void openSettings(Stage stage) {
        var view = new SettingsView(stage);
        var controller = new SettingsController(view);
        Stage settingsStage = view.getStage();
        view.getFolderPathField().setText(folderPath);
        view.getOkButton().setOnAction(e -> {
            applySettings();
            settingsStage.close();
        });
        view.getApplyButton().setOnAction(e -> {
            applySettings();
        });
        settingsStage.show();
    }

    private String showDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            String folderPath = selectedDirectory.getAbsolutePath();
            return folderPath;
        }
        return null;
    }

    private void applySettings() {
        String newFolderPath = folderPathField.getText().trim();

        if (!newFolderPath.isEmpty()) {
            folderPath = newFolderPath;
            properties.setProperty("folderPath", newFolderPath);
            if (model instanceof FileModel) {
                ((FileModel) model).setFolderPath(Path.of(folderPath));
            }
        }
        properties.saveProperties();
    }

}