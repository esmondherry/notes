package com.esmo;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class Alerts {
    public static void showErrorAlert(String string) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An Unexpected Error Has Occured");
        alert.setContentText(string);
        alert.showAndWait();
    }

    public static String askNewFileName() {
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

    public static Optional<ButtonType> confirmDelete(String selectedFile) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Delete");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete the selected file?\n\t" + selectedFile);
        return confirmation.showAndWait();
    }

    public static Optional<ButtonType> askSave(String selectedFile) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Unsaved Info");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Continue without saving " + selectedFile + " ?");
        return confirmation.showAndWait();
    }
}