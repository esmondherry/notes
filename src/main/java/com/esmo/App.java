package com.esmo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
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
    protected static String folderPath = "";

    private TextArea textArea;

    private TextField fileNameField;

    private TextField folderPathField;
    private Properties properties = new Properties();
    private FileListController fl;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        loadProperties();
        folderPath = properties.getProperty("folderPath");
        updateFileList();

        SplitPane splitPane = new SplitPane(buildTextPane(), buildFilesPane());
        splitPane.setDividerPositions(0.7);

        VBox vbox = new VBox(splitPane, buildToolbar());
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        Scene scene = new Scene(vbox, 600, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Some Things");
        primaryStage.show();

    }

    private VBox buildTextPane() {

        fileNameField = new TextField();
        fileNameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                changeFileName();
            }
        });
        fl.getSelectedFileProperty().addListener((observable, oldValue, newValue) -> {
            fileNameField.setText(newValue);
        });

        Button changeButton = new Button("Change");
        changeButton.setOnAction(e -> changeFileName());

        HBox fileNameBox = new HBox(fileNameField, changeButton);
        // could be split here
        textArea = new TextArea();
        fl.getTextContentProperty().addListener((observable, oldValue, newValue) -> {
            textArea.setText(newValue);
        });
        VBox.setVgrow(textArea, Priority.ALWAYS);

        return new VBox(fileNameBox, textArea);
    }

    private void changeFileName() {
        String selectedFile = fl.getListView().getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            String newFileName = addTXT(fileNameField.getText());
            if (!newFileName.isEmpty()) {
                String oldFilePath = folderPath + File.separator + selectedFile;
                if (FileController.changeFileName(oldFilePath, newFileName)) {
                    fl.removeFile(selectedFile);
                    fl.addFile(newFileName);
                    fileNameField.setText(newFileName);
                    sortMoveSelect();
                } else {
                    fileAlreadyExistsAlert(newFileName).showAndWait();
                }
            }
        }
    }

    private VBox buildFilesPane() {

        TextField searchField = new TextField();
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                fl.searchFiles(searchField.getText());
            }
        });

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> fl.searchFiles(searchField.getText()));

        HBox searchBox = new HBox(searchField, searchButton);

        return new VBox(searchBox, fl.getListView());
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
        vbox.getChildren().addAll(new Label("Folder Path:"), new HBox(textField,
                button));

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
        String selectedFile = fl.getSelectedFile();
        if (selectedFile != null) {
            String filePath = folderPath + File.separator + selectedFile;
            try {
                FileController.saveFile(filePath, textArea.getText());
            } catch (IOException e) {
                showErrorAlert("File \"" + selectedFile + "\"could not be saved");
            }
        }
    }

    private void showErrorAlert(String string) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An Unexpected Error Has Occured");
        alert.setContentText(string);
        alert.showAndWait();
    }

    private void deleteFile() {
        String selectedFile = fl.getSelectedFile();
        String filePath = folderPath + File.separator + selectedFile;
        confirmDelete(selectedFile).ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    FileController.deleteFile(filePath);
                    fl.removeFile(selectedFile);
                    textArea.clear();
                } catch (IOException e) {
                    showErrorAlert("File \"" + selectedFile + "\"could not be deleted");
                }
            }
        });
    }

    private Optional<ButtonType> confirmDelete(String selectedFile) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected file?\n\t" + selectedFile);
        return confirmation.showAndWait();
    }

    private String askNewFileName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New File");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter file name:");

        String fileName = dialog.showAndWait().orElse("").strip();
        if (fileName.isEmpty()) {
            return null;
        }

        return fileName;
    }

    private void createNewFile() {

        String fileName = askNewFileName();
        if (fileName == null) {
            return;
        }
        fileName = addTXT(fileName);
        
        String filePath = folderPath + File.separator + fileName;
        File newFile = new File(filePath);

        if (newFile.exists()) {
            fileAlreadyExistsAlert(fileName).showAndWait();
            return;
        }

        try {
            FileController.createFile(filePath);
            fl.addFile(addTXT(fileName));
            textArea.clear();
            sortMoveSelect(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Alert fileAlreadyExistsAlert(String fileName) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("File Already Exists");
        alert.setHeaderText(null);
        alert.setContentText("\"" + fileName + "\" already exists. Please choose a different name.");
        return alert;
    }

    private void updateFileList() {
        ObservableList<String> fileList = FXCollections.observableArrayList();
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
        fl = new FileListController(fileList);
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

        File selectedDirectory = directoryChooser.showDialog(new Stage());

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
        fl.getListView().getSelectionModel().selectLast();
        FXCollections.sort(fl.getListView().getItems(), (a, b) -> a.toLowerCase().compareTo(b.toLowerCase()));
        fl.getListView().scrollTo(fl.getListView().getSelectionModel().getSelectedItem());
    }

    private void sortMoveSelect(String fileName) {
        FXCollections.sort(fl.getListView().getItems(), (a, b) -> a.toLowerCase().compareTo(b.toLowerCase()));
        fl.getListView().getSelectionModel().select(fileName);
        fl.getListView().scrollTo(fileName);
    }

    private ToolBar buildToolbar() {
        ToolBar toolbar = new ToolBar();

        Button newButton = new Button("New");
        newButton.setOnAction(e -> createNewFile());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> saveFile());

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteFile());

        Button settingsButton = new Button("Settings");
        settingsButton.setOnAction(e -> openSettings());

        toolbar.getItems().addAll(newButton, saveButton, deleteButton, settingsButton);

        return toolbar;
    }

    private static String addTXT(String name) {
        if (name.toLowerCase().endsWith(".txt")) {
            return name;
        }
        return name + ".txt";
    }

}