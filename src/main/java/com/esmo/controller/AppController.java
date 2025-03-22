package com.esmo.controller;

import com.esmo.Alerts;
import com.esmo.model.Storage;
import com.esmo.view.AppView;
import com.esmo.view.SettingsView;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;

public class AppController {

    private AppView view;
    private Storage model;
    private boolean hasUnsavedChanges = false;

    private Map<String, Set<String>> tags = new HashMap<>();
    private List<String> enabledTagInFilter = new ArrayList<>();

    public AppController(AppView view, Storage model) {
        this.view = view;
        this.model = model;
        actionHandlers();
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    private void actionHandlers() {
        view.getListView().setItems(model.getFileList());

        view
            .getSearchField()
            .setOnKeyReleased(e -> {
                searchFiles();
            });

        view.getNameButton().setOnAction(e -> changeFileName());
        view
            .getNameField()
            .setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    changeFileName();
                }
            });

        view.getDeleteButton().setOnAction(e -> deleteFile());
        view.getNewButton().setOnAction(e -> createNewFile());

        view.getSaveButton().setOnAction(e -> saveFile());

        view
            .getListView()
            .getSelectionModel()
            .selectedItemProperty()
            .addListener((observable, oldfile, newfile) -> {
                changeSelectedListener(oldfile, newfile);
            });

        view
            .getTextArea()
            .textProperty()
            .addListener(observable -> {
                hasUnsavedChanges = true;
            });
        view.getSettingsButton().setOnAction(e -> openSettings());

        view.getAddTagButton().setOnAction(e -> addTag());
        view
            .getAddTagField()
            .setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    addTag();
                }
            });

        view.getRemoveTag().setOnAction(e -> removeTag());
        view
            .getFilterTagButton()
            .setOnAction(e -> {
                var results = Alerts.setFilter(
                    new ArrayList<>(listTags()),
                    enabledTagInFilter
                );
                enabledTagInFilter.clear();
                enabledTagInFilter.addAll(results);
                searchFiles();
            });
    }

    private void removeTag() {
        var tag = view.getTagListView().getSelectionModel().getSelectedItem();
        view.getTagListView().getItems().remove(tag);
        tags.get(getSelectedFile()).remove(tag);
    }

    private void addTag() {
        var currentTags = view.getTagListView().getItems();
        var newTag = view.getAddTagField().getText().strip();
        if (newTag.isEmpty()) {
            return;
        }
        if (getSelectedFile() == null) {
            return;
        }
        if (!currentTags.contains(newTag)) {
            currentTags.add(newTag);
            view.getAddTagField().setText("");
            currentTags.sort(String::compareToIgnoreCase);
            if (!tags.containsKey(getSelectedFile())) {
                tags.put(getSelectedFile(), new HashSet<>());
            }
            tags.get(getSelectedFile()).add(newTag);
        } else {
            Alerts.showErrorAlert("Tag already exists");
        }
    }

    private void openSettings() {
        SettingsView settingsView = new SettingsView(
            view.getPane().getScene().getWindow()
        );
        SettingsController settingsController = new SettingsController(
            settingsView
        );
        settingsView.getStage().show();
    }

    private void changeSelectedListener(String oldfile, String newfile) {
        if (newfile == null) {
            return;
        }
        if (oldfile == null || !hasUnsavedChanges) {
            changeFile(newfile);
            return;
        }
        Alerts.askSave(oldfile).ifPresent(response -> {
            if (response == ButtonType.OK) {
                changeFile(newfile);
                hasUnsavedChanges = false;
                return;
            } else {
                hasUnsavedChanges = false;
                String text = view.getTextArea().getText();
                view.getListView().getSelectionModel().select(oldfile);
                view.getTextArea().setText(text);
                hasUnsavedChanges = true;
            }
        });
    }

    public void searchFiles() {
        String searchPhrase = view.getSearchField().getText().strip();

        if (searchPhrase.isEmpty() && enabledTagInFilter.isEmpty()) {
            view.getListView().setItems(model.getFileList());
            return;
        }
        ObservableList<String> filteredList = model
            .getFileList()
            .filtered(fileName ->
                fileName.toLowerCase().contains(searchPhrase.toLowerCase())
            )
            .filtered(this::containsAnyTag);
        view.getListView().setItems(filteredList);
        if (filteredList.size() == 1) {
            view.getListView().getSelectionModel().select(0);
        }
    }

    private boolean containsAnyTag(String note) {
        var noteTags = tags.getOrDefault(note, new HashSet<>());
        for (var tag : noteTags) {
            if (enabledTagInFilter.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAllTag(String note) {
        var noteTags = tags.getOrDefault(note, new HashSet<>());
        for (var tag : noteTags) {
            if (!enabledTagInFilter.contains(tag)) {
                return false;
            }
        }
        return true;
    }

    private void changeFileName() {
        String selectedFile = getSelectedFile();
        if (selectedFile != null) {
            String newFileName = view.getNameField().getText();
            if (!newFileName.isEmpty()) {
                try {
                    model.rename(selectedFile, newFileName);
                    view.getNameField().setText(newFileName);
                    sortMoveSelect(newFileName);
                } catch (Exception e) {
                    Alerts.showErrorAlert(
                        "\"" +
                        newFileName +
                        "\" already exists. Please choose a different name. or something..."
                    );
                }
            }
        }
    }

    private void deleteFile() {
        String selectedFile = getSelectedFile();
        Alerts.confirmDelete(selectedFile).ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    model.delete(selectedFile);
                    view.getTextArea().clear();
                    hasUnsavedChanges = false;
                    if (getSelectedFile() == null) {
                        clearSearch();
                    } else {
                        changeFile(getSelectedFile());
                    }
                } catch (Exception e) {
                    Alerts.showErrorAlert(
                        "File \"" + selectedFile + "\"could not be deleted"
                    );
                }
            }
        });
    }

    public void createNewFile() {
        String fileName = Alerts.askNewFileName();
        if (fileName == null) {
            return;
        }

        try {
            model.add(fileName);
            view.getTextArea().clear();
            clearSearch();
            sortMoveSelect(fileName);
        } catch (FileAlreadyExistsException e) {
            Alerts.showErrorAlert(
                "\"" +
                fileName +
                "\" already exists. Please choose a different name."
            );
        } catch (Exception e) {
            Alerts.showErrorAlert("Something went wrong...");
            e.printStackTrace();
        }
    }

    public void saveFile() {
        String selectedFile = getSelectedFile();
        if (selectedFile != null) {
            try {
                model.save(selectedFile, view.getTextArea().getText());
                hasUnsavedChanges = false;
            } catch (Exception e) {
                Alerts.showErrorAlert(
                    "File \"" + selectedFile + "\"could not be saved"
                );
            }
        }
    }

    private String getSelectedFile() {
        return view.getListView().getSelectionModel().getSelectedItem();
    }

    private void changeFile(String file) {
        try {
            String fileContent = model.load(file);
            view.getTextArea().setText(fileContent);

            view.getTagListView().getItems().clear();
            tags
                .getOrDefault(file, new HashSet<String>())
                .stream()
                .sorted()
                .forEach(tag -> view.getTagListView().getItems().add(tag));
            hasUnsavedChanges = false;
        } catch (Exception e) {
            Alerts.showErrorAlert("file could not be read");
        }
    }

    private void sortMoveSelect(String fileName) {
        FXCollections.sort(model.getFileList(), (a, b) ->
            a.compareToIgnoreCase(b)
        );
        view.getListView().getSelectionModel().select(fileName);
        view.getListView().scrollTo(fileName);
    }

    private void clearSearch() {
        view.getSearchField().setText("");
        searchFiles();
    }

    private Set<String> listTags() {
        Set<String> set = new HashSet<>();
        for (var tag : tags.values()) {
            set.addAll(tag);
        }
        return set;
    }
}
