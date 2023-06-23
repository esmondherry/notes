package com.esmo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class App extends Application {

    final private static String CONFIG_PATH = "config.properties";
    private static String folderPath = "";
    private ObservableList<String> fileList;
    private TextArea textArea;

    private ListView<String> fileListView = new ListView<>();

    private TextField fileNameField;
    private TextField searchField;
    private Button searchButton;

    private TextField folderPathField;
    private Properties properties = new Properties();

    private FileController fileController = new FileController();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        textArea = new TextArea();
        fileList = FXCollections.observableArrayList();

        fileListView.setItems(fileList);
        fileListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String filePath = folderPath + File.separator + newValue;
                try {
                    String fileContent = fileController.readFile(filePath);
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

        searchField = new TextField();
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                searchFiles();
            }
        });
        searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchFiles());

        Button changeButton = new Button("Change");
        changeButton.setOnAction(e -> changeFileName());

        HBox fileNameBox = new HBox(fileNameField, changeButton);
        HBox searchBox = new HBox(searchField, searchButton);

        VBox textPane = new VBox(fileNameBox, textArea);
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox files = new VBox(searchBox, fileListView);
        VBox.setVgrow(fileListView, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane(textPane, files);
        splitPane.setDividerPositions(0.7);

        VBox vbox = new VBox(splitPane, createToolbar());
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        Scene scene = new Scene(vbox, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Some Things");
        primaryStage.show();

        loadProperties();
        folderPath = properties.getProperty("folderPath");
        updateFileList();
    }

    private void loadProperties() {
        try (FileReader fileReader = new FileReader(CONFIG_PATH)) {
            properties.load(fileReader);
        } catch (FileNotFoundException e) {
            createPropertiesFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPropertiesFile() {
        try (FileWriter fileWriter = new FileWriter(CONFIG_PATH)) {
            properties.setProperty("folderPath", initFolderPath());

            properties.store(fileWriter, "Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String initFolderPath() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Folder Not Found");

        TextField textField = new TextField();
        textField.setPrefWidth(200);

        Button button = new Button("...");
        button.setOnAction(e -> textField.setText(showDirectoryChooser()));

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(new Label("Folder Path:"), new HBox(textField, button));

        alert.setHeaderText("Enter a Folder to Open:");
        alert.getDialogPane().setContent(vbox);

        alert.showAndWait();

        String value = textField.getText();
        System.out.println("Entered value: " + value);
        return value;
    }

    private void saveProperties() {
        try {
            FileWriter fileWriter = new FileWriter(CONFIG_PATH);
            properties.store(fileWriter, "Config");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFile() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            String filePath = folderPath + File.separator + selectedFile;
            try {
                fileController.saveFile(filePath, textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // createNewFile();

        }
    }

    private void deleteFile() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        String filePath = folderPath + File.separator + selectedFile;
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected file?\n\t" + selectedFile);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    fileController.deleteFile(filePath);
                    fileList.remove(selectedFile);
                    textArea.clear();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createFileAlert() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New File");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter file name:");

        String fileName = dialog.showAndWait().orElse("").trim();
        if (fileName.isEmpty()) {
            return;
        }

        createNewFile(fileName);
    }

    private void createNewFile(String fileName) {

        fileName = addTXT(fileName);
        String filePath = folderPath + File.separator + fileName;
        File newFile = new File(filePath);

        if (newFile.exists()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("File Already Exists");
            alert.setHeaderText(null);
            alert.setContentText("\"" + fileName + "\" already exists. Please choose a different name.");
            alert.showAndWait();
            return;
        }

        try {
            fileController.createFile(filePath);
            fileList.add(addTXT(fileName));
            textArea.clear();
            sortMoveSelect(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void changeFileName() {
        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            String newFileName = addTXT(fileNameField.getText());
            if (!newFileName.isEmpty()) {
                String oldFilePath = folderPath + File.separator + selectedFile;
                if (fileController.changeFileName(oldFilePath, newFileName)) {
                    fileList.remove(selectedFile);
                    fileList.add(newFileName);
                    fileNameField.setText(newFileName);
                    sortMoveSelect();
                }
            }
        }
    }

    private void searchFiles() {
        String searchPhrase = searchField.getText().trim();

        if (searchPhrase.isEmpty()) {
            fileListView.setItems(fileList);
            return;
        }

        ObservableList<String> filteredList = fileList.filtered(fileName -> fileName.contains(searchPhrase));
        fileListView.setItems(filteredList);
    }

    private void updateFileList() {
        fileList.clear();
        File folder = new File(folderPath);
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
    }

    private void openSettings() {
        Stage settingsStage = new Stage();
        settingsStage.setTitle("Settings");

        folderPathField = new TextField(folderPath);
        folderPathField.setPrefWidth(200);

        Button folderPathButton = new Button("...");
        folderPathButton.setOnAction(e -> folderPathField.setText(showDirectoryChooser()));

        Button okButton = new Button("OK");
        okButton.setOnAction(e -> {
            applySettings();
            settingsStage.close();
        });

        Button applyButton = new Button("Apply");
        applyButton.setOnAction(e -> {
            applySettings();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> settingsStage.close());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(new Label("Folder Path:"), new HBox(folderPathField, folderPathButton));

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(okButton, applyButton, cancelButton);
        vbox.getChildren().add(buttonBox);

        Scene scene = new Scene(vbox);
        settingsStage.setScene(scene);
        settingsStage.show();
    }

    private String showDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        Stage sesttingStage = (Stage) textArea.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(sesttingStage);

        if (selectedDirectory != null) {
            String folderPath = selectedDirectory.getAbsolutePath();
            return folderPath;
        }
        return null;
    }

    private void applySettings() {
        String newFolderPath = folderPathField.getText().trim();

        if (!newFolderPath.isEmpty()) {
            folderPath = newFolderPath;
            properties.setProperty("folderPath", newFolderPath);
            updateFileList();
        }

        saveProperties();

    }

    private void sortMoveSelect() {
        fileListView.getSelectionModel().selectLast();
        FXCollections.sort(fileListView.getItems(), (a, b) -> a.toLowerCase().compareTo(b.toLowerCase()));
        fileListView.scrollTo(fileListView.getSelectionModel().getSelectedItem());
    }

    private void sortMoveSelect(String fileName) {
        FXCollections.sort(fileListView.getItems(), (a, b) -> a.toLowerCase().compareTo(b.toLowerCase()));
        fileListView.getSelectionModel().select(fileName);
        fileListView.scrollTo(fileName);
    }

    private String addTXT(String name) {
        if (name.toLowerCase().endsWith(".txt")) {
            return name;
        }
        return name + ".txt";
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        Button newButton = new Button("New");
        newButton.setOnAction(e -> createFileAlert());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveFile());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteFile());

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> openSettings());

        toolbar.getItems().addAll(newButton, saveButton, deleteButton, settingsButton);

        return toolbar;
    }

}