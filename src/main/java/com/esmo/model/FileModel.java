package com.esmo.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.esmo.Alerts;
import com.esmo.InfoCenter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FileModel implements Storage {
    private Path folderPath;
    private ObservableList<String> fileList;

    public FileModel(Path folderPath) {
        this.folderPath = folderPath;
        fileList = FXCollections.observableArrayList();
        updateFileList();
        InfoCenter.getInfoCenter().addListener(e -> {
            setFolderPath(Path.of(InfoCenter.getInfoCenter().getFolderPath()));
        });
    }

    public void setFolderPath(Path folderPath) {
        this.folderPath = folderPath;
        updateFileList();
    }

    @Override
    public ObservableList<String> getFileList() {
        return fileList;
    }

    public Path getFolderPath() {
        return folderPath;
    }

    @Override
    public void delete(String fileName) throws IOException {
        Files.delete(folderPath.resolve(addTXT(fileName)));
        fileList.remove(fileName);
    }

    @Override
    public void add(String fileName) throws IOException {
        Files.createFile(folderPath.resolve(addTXT(fileName)));
        fileList.add(fileName);
    }

    @Override
    public void rename(String deadName, String newName) throws IOException {
        Files.move(folderPath.resolve(addTXT(deadName)), folderPath.resolve(addTXT(newName)));
        fileList.remove(deadName);
        fileList.add(newName);
    }

    @Override
    public void save(String fileName, String content) throws IOException {
        fileName = addTXT(fileName);
        Path filePath = folderPath.resolve(fileName);
        Files.writeString(filePath, content);
    }

    @Override
    public String load(String fileName) throws Exception {
        fileName = addTXT(fileName);
        return Files.readString(folderPath.resolve(fileName));
    }

    private void updateFileList() {
        List<String> files = new ArrayList<>();
        try {
            Files.newDirectoryStream(folderPath, "*.txt").forEach(e -> {
                String file = e.getFileName().toString();
                files.add(file.substring(0, file.length()-4));
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
