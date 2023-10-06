package com.esmo.view;

import com.esmo.InfoCenter;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

public class SettingsView {
    private Stage stage;
    private ComboBox<String> comboBox;
    private Button okButton;
    private Button applyButton;
    private Button cancelButton;
    private TextField folderPathField;
    private Button folderPathButton;
    private InfoCenter infoCenter;
    private CheckBox onTopCheckBox;

    public SettingsView() {
        infoCenter = InfoCenter.getInfoCenter();

        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(gridPane);

        Label folderLabel = new Label("Folder Path:");
        folderPathField = new TextField();
        folderPathField.setText(infoCenter.getFolderPath());
        folderPathButton = new Button("...");
        gridPane.add(folderLabel, 0, 0);
        gridPane.add(new HBox(folderPathField, folderPathButton), 1, 0);

        Label themeLabel = new Label("Theme:");
        comboBox = new ComboBox<>(FXCollections.observableArrayList("Default", "Dawn", "Day", "Dusk", "Dark"));
        comboBox.getSelectionModel().select(infoCenter.getTheme());
        gridPane.add(themeLabel, 0, 1);
        gridPane.add(comboBox, 1, 1);

        Label onTopLabel = new Label("Always On Top:");
        onTopCheckBox = new CheckBox();
        onTopCheckBox.setSelected(infoCenter.isOnTop());
        gridPane.addRow(2, onTopLabel, onTopCheckBox);

        ToolBar toolBar = new ToolBar();
        okButton = new Button("OK");
        applyButton = new Button("Apply");
        cancelButton = new Button("Cancel");
        toolBar.getItems().addAll(okButton, applyButton, cancelButton);
        borderPane.setBottom(toolBar);

        Scene scene = new Scene(borderPane);
        stage = new Stage();
        stage.setTitle("Settings");
        stage.setScene(scene);
        infoCenter.loadTheme(stage);

    }

    public SettingsView(Window primaryStage) {
        this();
        stage.initOwner(primaryStage);
    }

    public Stage getStage() {
        return stage;
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getApplyButton() {
        return applyButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public TextField getFolderPathField() {
        return folderPathField;
    }

    public Button getFolderPathButton() {
        return folderPathButton;
    }

    public ComboBox<String> getComboBox() {
        return comboBox;
    }

    public CheckBox getOnTopCheckBox() {
        return onTopCheckBox;
    }
}