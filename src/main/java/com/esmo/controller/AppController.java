package com.esmo.controller;

import com.esmo.Alerts;
import com.esmo.model.Note;
import com.esmo.model.Storage;
import com.esmo.view.AppView;
import com.esmo.view.SettingsView;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;

public class AppController {

    private AppView view;
    private Storage storage;
    private boolean hasUnsavedChanges = false;

    private List<String> enabledTagsInFilter = new ArrayList<>();

    public AppController(AppView view, Storage model) {
        this.view = view;
        this.storage = model;

        try {
            view.getListView().getItems().setAll(storage.getNoteNames());
        } catch (IOException e) {
            Alerts.showErrorAlert("Could not read notes");
        }

        actionHandlers();
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    private void actionHandlers() {
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
                List<String> results = Alerts.setFilter(
                    listTags(),
                    enabledTagsInFilter
                );
                enabledTagsInFilter.clear();
                enabledTagsInFilter.addAll(results);
                searchFiles();
            });
    }

    private void removeTag() {
        String tag = view
            .getTagListView()
            .getSelectionModel()
            .getSelectedItem();
        view.getTagListView().getItems().remove(tag);
    }

    private void addTag() {
        String newTag = view.getAddTagField().getText().strip();
        if (newTag.isEmpty()) {
            return;
        }
        if (getSelectedFile() == null) {
            return;
        }

        var currentTags = view.getTagListView().getItems();
        if (!currentTags.contains(newTag)) {
            currentTags.add(newTag);
            view.getAddTagField().setText("");
            currentTags.sort(String::compareToIgnoreCase);
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

        try {
            if (searchPhrase.isEmpty() && enabledTagsInFilter.isEmpty()) {
                view.getListView().getItems().setAll(storage.getNoteNames());
                return;
            }
            List<String> filteredList = storage
                .getNoteNames()
                .stream()
                .filter(fileName ->
                    fileName.toLowerCase().contains(searchPhrase.toLowerCase())
                )
                .filter(this::containsAnyTag)
                .collect(Collectors.toList());
            view.getListView().getItems().setAll(filteredList);
            if (filteredList.size() == 1) {
                view.getListView().getSelectionModel().select(0);
            }
        } catch (IOException e) {
            Alerts.showErrorAlert("Could not read notes");
        }
    }

    private boolean containsAnyTag(String note) {
        try {
            Set<String> noteTags = storage.get(note).getTags();
            for (String tag : noteTags) {
                if (enabledTagsInFilter.contains(tag)) {
                    return true;
                }
            }
        } catch (IOException e) {
            Alerts.showErrorAlert("Could not read tags");
        }
        return false;
    }

    private boolean containsAllTag(String note) {
        try {
            Set<String> noteTags = storage.get(note).getTags();

            for (String tag : noteTags) {
                if (!enabledTagsInFilter.contains(tag)) {
                    return false;
                }
            }
        } catch (IOException e) {
            Alerts.showErrorAlert("Could not read tags");
        }
        return true;
    }

    private void changeFileName() {
        String selectedFile = getSelectedFile();
        if (selectedFile != null) {
            String newFileName = view.getNameField().getText();
            if (!newFileName.isEmpty()) {
                try {
                    storage.rename(selectedFile, newFileName);
                    view.getListView().getItems().remove(selectedFile);
                    view.getListView().getItems().add(newFileName);

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
                    storage.delete(selectedFile);
                    view.getListView().getItems().remove(selectedFile);
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
            storage.save(new Note(fileName));
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
                storage.save(getSelectedNote());
                hasUnsavedChanges = false;
            } catch (Exception e) {
                Alerts.showErrorAlert(
                    "Note \"" + selectedFile + "\"could not be saved"
                );
            }
        }
    }

    private String getSelectedFile() {
        return view.getListView().getSelectionModel().getSelectedItem();
    }

    private void changeFile(String file) {
        try {
            Note note = storage.get(file);
            view.getNameField().setText(note.getName());
            view.getTextArea().setText(note.getContent());
            view.getTagListView().getItems().clear();
            note
                .getTags()
                .stream()
                .sorted()
                .forEach(tag -> view.getTagListView().getItems().add(tag));
            hasUnsavedChanges = false;
        } catch (Exception e) {
            Alerts.showErrorAlert("file could not be read");
        }
    }

    private void sortMoveSelect(String fileName) {
        FXCollections.sort(view.getListView().getItems(), (a, b) ->
            a.compareToIgnoreCase(b)
        );
        view.getListView().getSelectionModel().select(fileName);
        view.getListView().scrollTo(fileName);
    }

    private void clearSearch() {
        view.getSearchField().setText("");
        searchFiles();
    }

    private List<String> listTags() {
        try {
            return storage.getTags();
        } catch (IOException e) {
            Alerts.showErrorAlert("Could not read tags");
        }
        return new ArrayList<>();
    }

    private Note getSelectedNote() {
        if (getSelectedFile() == null) {
            return null;
        }

        String name = getSelectedFile();
        Note note = new Note(name);
        note.setContent(view.getTextArea().getText());
        note.getTags().addAll(view.getTagListView().getItems());
        return note;
    }
}
