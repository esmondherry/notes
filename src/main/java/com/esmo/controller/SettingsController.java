package com.esmo.controller;

import java.io.File;

import com.esmo.view.SettingsView;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingsController {
    private SettingsView view;

    public SettingsController(SettingsView view) {
        this.view = view;

        actionHandlers();
    }

    private void actionHandlers() {
        view.getCancelButton().setOnAction(e -> handleCancel());
        view.getFolderPathButton().setOnAction(e -> handleChooseFolder());
    }

    private void handleCancel() {
        view.getStage().close();
    }

    private void handleChooseFolder() {
        var folder = showDirectoryChooser();
        if (folder == null) {
            return;
        }
        view.getFolderPathField().setText(folder);
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
}
