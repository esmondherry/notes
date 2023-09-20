package com.esmo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Model {
    private Path folderPath;
    private ObservableList<String> fileList;

    public Model(Path folderPath) {
        this.folderPath = folderPath;
        fileList = FXCollections.observableArrayList();
        updateFileList();
    }

    public void setFolderPath(Path folderPath) {
        this.folderPath = folderPath;
        updateFileList();
    }

    public ObservableList<String> getFileList() {
        return fileList;
    }

    public Path getFolderPath() {
        return folderPath;
    }

    public void deleteFile(String fileName) throws IOException {
        Files.delete(folderPath.resolve(fileName));
        fileList.remove(fileName);
    }

    public void addFile(String fileName) throws IOException {
        Files.createFile(folderPath.resolve(fileName));
        fileList.add(fileName);
    }

    public void renameFile(String deadName, String newName) throws IOException {
        Files.move(folderPath.resolve(deadName), folderPath.resolve(newName));
        fileList.remove(deadName);
        fileList.add(newName);
    }

    private void updateFileList() {
        List<String> files = new ArrayList<>();
        try {
            Files.newDirectoryStream(folderPath, "*.txt").forEach(e -> {
                files.add(e.getFileName().toString());
            });
        } catch (NotDirectoryException e) {
            Alerts.showErrorAlert("The currently selected path is not a directory");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        fileList.setAll(files);
    }

}
