package com.esmo.view;

import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AppView {

    private VBox pane;
    private ListView<String> listView;
    private TextField searchField;
    private Button nameButton;
    private TextField nameField;
    private TextArea textArea;
    private Button newButton;
    private Button saveButton;
    private Button deleteButton;
    private Button settingsButton;
    private ListView<String> tagListView;
    private TextField addTagField;
    private Button addTagButton;
    private HBox addTagBox;
    private Button removeTag;
    private Button tagfilters;

    public AppView() {
        VBox textPane = createTextPane();

        VBox filesPane = createFilesPane();

        VBox tagPane = createTagPane();

        SplitPane sidePane = new SplitPane(filesPane, tagPane);
        sidePane.setOrientation(Orientation.VERTICAL);
        sidePane.setDividerPositions(.7);

        SplitPane splitPane = new SplitPane(textPane, sidePane);
        splitPane.setDividerPositions(0.7);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        ToolBar toolbar = createToolbar();

        pane = new VBox(splitPane, toolbar);
    }

    public ListView<String> getListView() {
        return listView;
    }

    public VBox getPane() {
        return pane;
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

    public ListView<String> getTagListView() {
        return tagListView;
    }

    public TextField getAddTagField() {
        return addTagField;
    }

    public Button getAddTagButton() {
        return addTagButton;
    }

    public HBox getAddTagBox() {
        return addTagBox;
    }

    public Button getRemoveTag() {
        return removeTag;
    }

    public Button getFilterTagButton() {
        return tagfilters;
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();
        newButton = new Button("New");
        saveButton = new Button("Save");
        deleteButton = new Button("Delete");
        settingsButton = new Button("Settings");
        toolbar
            .getItems()
            .addAll(newButton, saveButton, deleteButton, settingsButton);
        return toolbar;
    }

    private VBox createFilesPane() {
        listView = new ListView<>();
        searchField = new TextField();
        searchField.setPromptText("Search...");

        HBox.setHgrow(searchField, Priority.ALWAYS);
        tagfilters = new Button("V");

        HBox searchBox = new HBox(searchField, tagfilters);
        VBox.setVgrow(listView, Priority.ALWAYS);

        VBox filesPane = new VBox(searchBox, listView);
        return filesPane;
    }

    private VBox createTextPane() {
        nameField = new TextField();
        nameButton = new Button("Change");
        HBox nameBox = new HBox(nameField, nameButton);

        textArea = new TextArea();
        VBox.setVgrow(textArea, Priority.ALWAYS);

        VBox textPane = new VBox(nameBox, textArea);
        return textPane;
    }

    private VBox createTagPane() {
        tagListView = new ListView<>();
        addTagField = new TextField();
        addTagButton = new Button("Add Tag");
        addTagBox = new HBox(addTagField, addTagButton);
        removeTag = new Button("Remove Selected Tag");
        removeTag.setMaxWidth(Double.MAX_VALUE);
        return new VBox(tagListView, removeTag, addTagBox);
    }
}
