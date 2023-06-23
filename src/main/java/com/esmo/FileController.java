package com.esmo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileController {
    public String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    public void saveFile(String filePath, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(data);
        }
    }

    public void deleteFile(String filePath) throws IOException {
        Files.delete(Path.of(filePath));
    }

    public void createFile(String filePath) throws IOException {
        File file = new File(filePath);
        int num = 1;
        while (file.exists()) {
            file = new File(filePath + " (" + num++ + ")");
        }
        file.createNewFile();
    }

    public boolean changeFileName(String filePath, String newName) {
        File file = new File(filePath);
        File newFile = new File(file.getParent(), newName);
        return file.renameTo(newFile);
    }
}
