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
        fileName = addTXT(fileName);
        Files.delete(folderPath.resolve(fileName));
        fileList.remove(fileName);
    }

    public void addFile(String fileName) throws IOException {
        fileName = addTXT(fileName);
        Files.createFile(folderPath.resolve(fileName));
        fileList.add(fileName);
    }

    public void renameFile(String deadName, String newName) throws IOException {
        deadName = addTXT(deadName);
        newName = addTXT(newName);
        Files.move(folderPath.resolve(deadName), folderPath.resolve(newName));
        fileList.remove(deadName);
        fileList.add(newName);
    }

    public void save(String fileName, String content) throws IOException {
        fileName = addTXT(fileName);
        Path filePath = folderPath.resolve(fileName);
        Files.writeString(filePath, content);
    }

    public String load(String fileName) throws Exception {
        fileName = addTXT(fileName);
        return Files.readString(folderPath.resolve(fileName));
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

    private static String addTXT(String name) {
        if (name.toLowerCase().endsWith(".txt")) {
            return name;
        }
        return name + ".txt";
    }
}
