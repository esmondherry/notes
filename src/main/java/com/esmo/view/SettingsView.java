package com.esmo.view;

import com.esmo.InfoCenter;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private Button okButton;
    private Button applyButton;
    private Button cancelButton;
    private TextField folderPathField;
    private Button folderPathButton;

    public SettingsView() {
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        borderPane.setCenter(gridPane);

        folderPathField = new TextField();
        folderPathField.setText(InfoCenter.getInfoCenter().getFolderPath());
        folderPathButton = new Button("...");
        gridPane.add(new Label("Folder Path:"), 0, 0);
        gridPane.add(new HBox(folderPathField, folderPathButton), 1, 0);

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

}
