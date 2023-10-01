package com.esmo;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;

public class Controller {
    private View view;
    private Model model;
    private boolean hasUnsavedChanges = false;

    public Controller(View view, Model model) {
        this.view = view;
        this.model = model;
        init();
    }

    public boolean hasUnsavedChanges() {
        return hasUnsavedChanges;
    }

    private void init() {
        view.getListView().setItems(model.getFileList());

        view.getSearchButton().setOnAction(e -> searchFiles());
        view.getSearchField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchFiles();
            }
        });

        view.getNameButton().setOnAction(e -> changeFileName());
        view.getNameField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                changeFileName();
            }
        });

        view.getDeleteButton().setOnAction(e -> deleteFile());
        view.getNewButton().setOnAction(e -> createNewFile());

        view.getSaveButton().setOnAction(e -> saveFile());

        view.getListView().getSelectionModel().selectedItemProperty().addListener((observable,
                oldfile, newfile) -> {
            changeSelectedListener(oldfile, newfile);
        });

        view.getTextArea().textProperty().addListener((observable) -> {
            hasUnsavedChanges = true;
        });
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

        if (searchPhrase.isEmpty()) {
            view.getListView().setItems(model.getFileList());
            return;
        }
        ObservableList<String> filteredList = model.getFileList()
                .filtered(fileName -> fileName.toLowerCase().contains(searchPhrase.toLowerCase()));
        view.getListView().setItems(filteredList);
        if (filteredList.size() == 1) {
            view.getListView().getSelectionModel().select(0);
        }
    }

    private void changeFileName() {
        String selectedFile = view.getListView().getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            String newFileName = addTXT(view.getNameField().getText());
            if (!newFileName.isEmpty()) {
                try {
                    model.renameFile(selectedFile, newFileName);
                    view.getNameField().setText(newFileName);
                    sortMoveSelect(newFileName);
                } catch (IOException e) {
                    Alerts.showErrorAlert(
                            "\"" + newFileName + "\" already exists. Please choose a different name. or something...");
                }
            }
        }
    }

    private void deleteFile() {
        String selectedFile = view.getListView().getSelectionModel().getSelectedItem();
        Alerts.confirmDelete(selectedFile).ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    model.deleteFile(selectedFile);
                    view.getTextArea().clear();
                    hasUnsavedChanges=false;
                } catch (Exception e) {
                    Alerts.showErrorAlert("File \"" + selectedFile + "\"could not be deleted");
                }
            }
        });
    }

    public void createNewFile() {

        String fileName = Alerts.askNewFileName();
        if (fileName == null) {
            return;
        }
        fileName = addTXT(fileName);

        try {
            model.addFile(fileName);
            view.getTextArea().clear();
            sortMoveSelect(fileName);

        } catch (FileAlreadyExistsException e) {
            Alerts.showErrorAlert("\"" + fileName + "\" already exists. Please choose a different name.");
        } catch (IOException e) {
            Alerts.showErrorAlert("Something went wrong...");
            e.printStackTrace();
        }
    }

    public void saveFile() {
        String selectedFile = view.getListView().getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            Path filePath = model.getFolderPath().resolve(selectedFile);
            try {
                Files.writeString(filePath, view.getTextArea().getText());
                hasUnsavedChanges = false;
            } catch (IOException e) {
                Alerts.showErrorAlert("File \"" + selectedFile + "\"could not be saved");
            }
        }
    }

    private void changeFile(String file) {
        Path filePath = model.getFolderPath().resolve(file);
        try {
            String fileContent = Files.readString(filePath);
            view.getTextArea().setText(fileContent);
            hasUnsavedChanges = false;
        } catch (IOException e) {
            System.err.println("file could not be read");
        }
    }

    private void sortMoveSelect(String fileName) {
        FXCollections.sort(model.getFileList(), (a, b) -> a.toLowerCase().compareTo(b.toLowerCase()));
        view.getListView().getSelectionModel().select(fileName);
        view.getListView().scrollTo(fileName);
    }

    private static String addTXT(String name) {
        if (name.toLowerCase().endsWith(".txt")) {
            return name;
        }
        return name + ".txt";
    }

}