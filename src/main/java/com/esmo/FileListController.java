package com.esmo;

import java.io.File;
import java.io.IOException;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class FileListController {
    private ObservableList<String> allFiles;
    private ListView<String> listView;
    private StringProperty textContent;
    private String folderPath = App.folderPath;

    public FileListController(ObservableList<String> fileList) {
        this.textContent = new SimpleStringProperty();
        this.allFiles = FXCollections.observableArrayList(fileList);
        this.listView = new ListView<>(allFiles);
        listView.getSelectionModel().selectedItemProperty().addListener((observable,
                oldValue, newValue) -> {
            if (newValue != null) {
                String filePath = folderPath + File.separator + newValue;
                try {
                    String fileContent = FileController.readFile(filePath);
                    textContent.set(fileContent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        VBox.setVgrow(listView, Priority.ALWAYS);

    }

    /**
     * Updates the listView to only have values that contain the search phrase
     * 
     * @param searchPhrase the phrase to search for
     */
    public void searchFiles(String searchPhrase) {
        searchPhrase = searchPhrase.strip();

        if (searchPhrase.isEmpty()) {
            listView.setItems(allFiles);
            return;
        }
        final String sP = searchPhrase;
        ObservableList<String> filteredList = allFiles
                .filtered(fileName -> fileName.toLowerCase().contains(sP.toLowerCase()));
        listView.setItems(filteredList);
        if (filteredList.size() == 1) {
            listView.getSelectionModel().select(0);
        }
    }

    /**
     * Adds a file to the listview
     * 
     * @param file the name of the file to be added
     */
    public void addFile(String file) {
        allFiles.add(file);
    }

    /**
     * Removes file from the listview
     * 
     * @param file the name of the file to be removed
     */
    public void removeFile(String file) {
        allFiles.remove(file);
    }

    public ListView<String> getListView() {
        return listView;
    }

    /**
     * Returns the text content of the currently selected file
     * 
     * @return the current value
     */
    public String getTextContent() {
        return textContent.getValue();
    }

    public StringProperty getTextContentProperty() {
        return textContent;
    }

    /**
     * Returns the name of the currently selected file
     * 
     * @return the current value
     */
    public String getSelectedFile() {
        return listView.getSelectionModel().getSelectedItem();
    }

    public ReadOnlyObjectProperty<String> getSelectedFileProperty() {
        return listView.getSelectionModel().selectedItemProperty();
    }
}