package com.esmo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileController {
    /**
     * Read a files contents as <code>String</code>
     * 
     * @param filePath path to the file
     * @return the content of the file as a string
     * @throws IOException if an IO error occurs
     */
    public static String readFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    /**
     * Save a <code>String</code> to file
     * 
     * @param filePath the path to the file to save to
     * @param data     the <code>String</code> to be written to the file
     * @throws IOException if an IO error occurs
     */
    public static void saveFile(String filePath, String data) throws IOException {
        Files.writeString(Path.of(filePath), data);
    }

    public static void deleteFile(String filePath) throws IOException {
        Files.delete(Path.of(filePath));
    }

    public static void createFile(String filePath) throws IOException {
        Files.createFile(Path.of(filePath));
    }

    /**
     * Changes the name of the file
     * 
     * @param filePath the path to the file
     * @param newName  the new name of the file
     * @return <code>true</code> if the renaming was successful;
     */
    public static boolean changeFileName(String filePath, String newName) {
        try {
            Files.move(Path.of(filePath), Path.of(filePath).resolveSibling(newName),
                    StandardCopyOption.COPY_ATTRIBUTES);
            return true;
        } catch (IOException | UnsupportedOperationException e) {
            return false;
        }
    }
}
