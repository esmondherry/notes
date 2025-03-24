package com.esmo.model;

import java.io.IOException;
import java.util.List;

public interface Storage {
    List<String> getNoteNames() throws IOException;
    void delete(String name) throws IOException;
    void rename(String deadName, String newName) throws IOException;
    void save(Note note) throws IOException;
    Note get(String name) throws IOException;
    List<String> getTags() throws IOException;
}
