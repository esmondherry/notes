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
    /**
     * Read a files contents
     * 
     * @param filePath path to the file
     * @return the content of the file as a string
     * @throws IOException if an IO error occurs
     */
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        }
        return content.toString();
    }

    /**
     * Save a string to file
     * 
     * @param filePath the path to the file to save to
     * @param data     the <code>String</code> to be written to the file
     * @throws IOException if an IO error occurs
     */
    public static void saveFile(String filePath, String data) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(data);
        }
    }

    public static void deleteFile(String filePath) throws IOException {
        Files.delete(Path.of(filePath));
    }

    public static void createFile(String filePath) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
    }

    /**
     * Changes the name of the file
     * 
     * @param filePath the path to the file
     * @param newName  the new name of the file
     * @return <code>true</code> if the renaming was successful;
     */
    public static boolean changeFileName(String filePath, String newName) {
        File file = new File(filePath);
        File newFile = new File(file.getParent(), newName);
        return file.renameTo(newFile);
    }
}
