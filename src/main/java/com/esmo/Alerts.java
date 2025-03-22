package com.esmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Alerts {

    public static void showErrorAlert(String string) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An Unexpected Error Has Occured");
        alert.setContentText(string);
        alert.showAndWait();
        loadTheme(alert);
    }

    public static String askNewFileName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create New File");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter file name:");
        loadTheme(dialog);

        String fileName = dialog.showAndWait().orElse("").strip();
        if (fileName.isEmpty()) {
            return null;
        }

        return fileName;
    }

    public static Optional<ButtonType> confirmDelete(String selectedFile) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
            "Are you sure you want to delete the selected file?\n\t" +
            selectedFile
        );
        loadTheme(confirmation);
        return confirmation.showAndWait();
    }

    public static Optional<ButtonType> askSave(String selectedFile) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Unsaved Info");
        confirmation.setHeaderText(null);
        confirmation.setContentText(
            "Continue without saving " + selectedFile + " ?"
        );
        loadTheme(confirmation);
        return confirmation.showAndWait();
    }

    private static void loadTheme(Dialog<?> dialog) {
        InfoCenter.getInfoCenter()
            .loadTheme(dialog.getDialogPane().getStylesheets());
    }

    public static List<String> setFilter(
        List<String> tags,
        List<String> enabled
    ) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Select Tags");
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (var tag : tags) {
            CheckBox check = new CheckBox(tag);
            checkBoxes.add(check);
            if (enabled.contains(tag)) {
                check.setSelected(true);
            }
        }

        FlowPane flowPane = new FlowPane();
        flowPane.setVgap(8);
        flowPane.setHgap(8);
        flowPane.getChildren().addAll(checkBoxes);
        Button clearTags = new Button("Clear Selected");
        clearTags.setOnAction(e -> {
            checkBoxes.forEach(checkBox -> checkBox.setSelected(false));
        });
        flowPane.getChildren().add(clearTags);
        alert.getDialogPane().setContent(flowPane);
        var buttonType = alert.showAndWait();
        if (buttonType.isPresent() && buttonType.get() == ButtonType.OK) {
            return checkBoxes
                .stream()
                .filter(checkBox -> checkBox.isSelected())
                .map(checkBox -> checkBox.getText())
                .collect(Collectors.toList());
        }
        return enabled;
    }
}
