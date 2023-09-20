package com.esmo;

import java.io.File;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class App extends Application {
    protected static String folderPath = "";

    private TextField folderPathField;
    private PropertiesController properties;

    private Model model;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        properties = new PropertiesController();
        if (properties.getProperty("folderPath") == null) {
            properties.setProperty("folderPath", initFolderPath());
        }
        folderPath = properties.getProperty("folderPath");

        View view = new View();
        model = new Model(Path.of(folderPath));
        Controller controller = new Controller(view, model);

        view.getSettingsButton().setOnAction(e -> openSettings());

        Scene scene = new Scene(view.getPane(), 600, 300);
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

    private void openSettings() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        folderPathField = new TextField(folderPath);
        folderPathField.setPrefWidth(200);

        Button folderPathButton = new Button("...");
        folderPathButton.setOnAction(e -> folderPathField.setText(showDirectoryChooser()));

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            applySettings();
            settingsStage.close();
        });

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            applySettings();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> settingsStage.close());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(new Label("Folder Path:"), new HBox(folderPathField, folderPathButton));

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(okButton, applyButton, cancelButton);
        vbox.getChildren().add(buttonBox);

        Scene scene = new Scene(vbox);
        settingsStage.setScene(scene);
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
            model.setFolderPath(Path.of(folderPath));
        }
        properties.saveProperties();
    }

}