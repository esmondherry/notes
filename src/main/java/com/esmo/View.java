package com.esmo;

import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class View {
    private VBox pane;
    private ListView<String> listView;
    private Button searchButton;
    private TextField searchField;
    private Button nameButton;
    private TextField nameField;
    private TextArea textArea;
    private Button newButton;
    private Button saveButton;
    private Button deleteButton;
    private Button settingsButton;

    public View() {

        nameField = new TextField();
        nameButton = new Button("Change");
        HBox nameBox = new HBox(nameField, nameButton);

        textArea = new TextArea();
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox textPane = new VBox(nameBox, textArea);

        searchField = new TextField();
        searchButton = new Button("Search");
        HBox searchBox = new HBox(searchField, searchButton);

        listView = new ListView<>();
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            nameField.setText(newValue);
        });
        VBox.setVgrow(listView, Priority.ALWAYS);

        VBox filesPane = new VBox(searchBox, listView);

        SplitPane splitPane = new SplitPane(textPane, filesPane);
        splitPane.setDividerPositions(0.7);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        ToolBar toolbar = new ToolBar();
        newButton = new Button("New");
        saveButton = new Button("Save");
        deleteButton = new Button("Delete");
        settingsButton = new Button("Settings");
        toolbar.getItems().addAll(newButton, saveButton, deleteButton, settingsButton);

        pane = new VBox(splitPane, toolbar);
    }

    public ListView<String> getListView() {
        return listView;
    }

    public VBox getPane() {
        return pane;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public TextField getSearchField() {
        return searchField;
    }

    public Button getNameButton() {
        return nameButton;
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public Button getNewButton() {
        return newButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getSettingsButton() {
        return settingsButton;
    }
    
}