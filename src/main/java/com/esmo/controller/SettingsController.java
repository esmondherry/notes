package com.esmo.controller;

import java.io.File;

import com.esmo.InfoCenter;
import com.esmo.view.SettingsView;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingsController {
    private SettingsView view;
    private InfoCenter infoCenter;

    public SettingsController(SettingsView view) {
        infoCenter = InfoCenter.getInfoCenter();
        this.view = view;

        actionHandlers();
    }

    private void actionHandlers() {
        view.getCancelButton().setOnAction(e -> handleCancel());
        view.getApplyButton().setOnAction(e -> handleApply());
        view.getOkButton().setOnAction(e -> handleOK());
        view.getFolderPathButton().setOnAction(e -> handleChooseFolder());
    }

    private void handleCancel() {
        view.getStage().close();
    }

    private void handleApply() {
        infoCenter.setFolderPath(view.getFolderPathField().getText().trim());
        changeTheme();
        changeOnTop();
        infoCenter.save();
    }

    private void changeOnTop() {
        infoCenter.setOnTop(view.getOnTopCheckBox().isSelected());
        ((Stage) view.getStage().getOwner()).setAlwaysOnTop(view.getOnTopCheckBox().isSelected());
    }

    private void handleOK() {
        handleApply();
        view.getStage().close();
    }

    private void handleChooseFolder() {
        var folder = showDirectoryChooser();
        if (folder == null) {
            return;
        }
        view.getFolderPathField().setText(folder);
    }

    private void changeTheme() {
        infoCenter.setTheme(view.getComboBox().getSelectionModel().getSelectedItem());
    }

    private String showDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        File selectedDirectory = directoryChooser.showDialog(view.getStage());

        if (selectedDirectory != null) {
            String folderPath = selectedDirectory.getAbsolutePath();
            return folderPath;
        }
        return null;
    }
}
