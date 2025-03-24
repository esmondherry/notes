package com.esmo.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.json.JSONObject;

public class FileStorage implements Storage {

    private Path folderPath;
    private Path tagFile;
    private Map<String, Set<String>> tags;

    public FileStorage(Path folderPath) throws IOException {
        this.folderPath = folderPath;
        this.tagFile = folderPath.resolve("tags.json");

        loadTags();
    }

    @Override
    public Note get(String name) throws IOException {
        Path notePath = getNotePath(name);
        Note note = new Note(name);
        note.setContent(Files.readString(notePath));
        note
            .getTags()
            .addAll(tags.getOrDefault(note.getName(), new HashSet<>()));
        return note;
    }

    @Override
    public void save(Note note) throws IOException {
        Path notePath = getNotePath(note.getName());
        Files.writeString(notePath, note.getContent());

        tags.put(note.getName(), note.getTags());
        saveTags();
    }

    @Override
    public void delete(String name) throws IOException {
        Path notePath = getNotePath(name);
        Files.delete(notePath);
        tags.remove(name);
        saveTags();
    }

    @Override
    public List<String> getNoteNames() throws IOException {
        try (Stream<Path> paths = Files.list(folderPath)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".txt"))
                .map(Path::getFileName)
                .map(Path::toString)
                .map(name -> name.substring(0, name.length() - 4))
                .collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getTags() throws IOException {
        return this.tags.values()
            .stream()
            .flatMap(tagGroup -> tagGroup.stream())
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public void rename(String deadName, String newName) throws IOException {
        Files.move(getNotePath(deadName), getNotePath(newName));
    }

    private Path getNotePath(String noteName) {
        return folderPath.resolve(noteName + ".txt");
    }

    private void loadTags() throws IOException {
        tags = new HashMap<>();
        if (Files.exists(tagFile)) {
            String json = Files.readString(tagFile);
            JSONObject jsonObject = new JSONObject(json);

            jsonObject
                .keySet()
                .forEach(key ->
                    this.tags.put(
                            key,
                            jsonObject
                                .getJSONArray(key)
                                .toList()
                                .stream()
                                .map(tag -> (String) tag)
                                .collect(Collectors.toSet())
                        )
                );
        }
    }

    private void saveTags() throws IOException {
        JSONObject jsonObject = new JSONObject(tags);
        Files.writeString(tagFile, jsonObject.toString());
    }
}
