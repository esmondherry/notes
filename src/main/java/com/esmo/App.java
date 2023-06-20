package com.esmo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private static final String FOLDER_PATH = "/path/to/predefined/folder";
    private ObservableList<String> fileList;
    private TextArea textArea;

    private ListView<String> fileListView = new ListView<>();

    private TextField fileNameField;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        textArea = new TextArea();
        fileList = FXCollections.observableArrayList();

        File folder = new File(FOLDER_PATH);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".txt")) {
                        fileList.add(file.getName());
                    }
                }
            }
        }

        fileListView.setItems(fileList);

        fileListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String filePath = FOLDER_PATH + File.separator + newValue;
                try {
                    String fileContent = readFile(filePath);
                    textArea.setText(fileContent);
                    fileNameField.setText(newValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        fileNameField = new TextField();
        fileNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                changeFileName();
            }
        });

        Button newFileButton = new Button("New");
        newFileButton.setOnAction(e -> createNewFile());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteFile());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveFile());

        Button changeButton = new Button("Change");
        changeButton.setOnAction(e -> changeFileName());

        HBox fileNameBox = new HBox(fileNameField, changeButton);
        HBox buttonBox = new HBox(newFileButton, saveButton, deleteButton);
        HBox hbox = new HBox(textArea, fileListView);
        VBox vbox = new VBox(fileNameBox, hbox, buttonBox);

        Scene scene = new Scene(vbox, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Some Things");
        primaryStage.show();
    }

    private String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    private void saveFile() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            String filePath = FOLDER_PATH + File.separator + selectedFile;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            createNewFile();

        }
    }

    private void deleteFile() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        String filePath = FOLDER_PATH + File.separator + selectedFile;
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected file?\n\t" + selectedFile);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    Files.delete(Path.of(filePath));
                    fileList.remove(selectedFile);
                    textArea.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createNewFile() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New File");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter file name:");

        String fileName = dialog.showAndWait().orElse("").trim();
        if (!fileName.isEmpty()) {
            String filePath = FOLDER_PATH + File.separator + addTXT(fileName);
            File newFile = new File(filePath);
            try {
                boolean created = newFile.createNewFile();
                if (created) {
                    fileList.add(addTXT(fileName));
                    textArea.clear();
                    sortMoveSelect(fileName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void changeFileName() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            String newFileName = addTXT(fileNameField.getText());
            if (!newFileName.isEmpty()) {
                String oldFilePath = FOLDER_PATH + File.separator + selectedFile;
                String newFilePath = FOLDER_PATH + File.separator + newFileName;
                File oldFile = new File(oldFilePath);
                File newFile = new File(newFilePath);
                if (oldFile.renameTo(newFile)) {
                    fileList.remove(selectedFile);
                    fileList.add(newFileName);
                    fileNameField.setText(newFileName);
                    sortMoveSelect();
                }
            }
        }
    }

    private void sortMoveSelect() {
        fileListView.getSelectionModel().selectLast();
        FXCollections.sort(fileListView.getItems());
        fileListView.scrollTo(fileListView.getSelectionModel().getSelectedItem());
    }

    private void sortMoveSelect(String fileName) {
        FXCollections.sort(fileListView.getItems());
        fileListView.getSelectionModel().select(fileName);
        fileListView.scrollTo(fileName);
    }

    private String addTXT(String name) {
        if (name.endsWith(".txt")) {
            return name;
        }
        return name + ".txt";
    }
}