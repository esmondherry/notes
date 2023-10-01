package com.esmo;

import javafx.collections.ObservableList;

public interface Storage {

    ObservableList<String> getFileList();

    void delete(String fileName) throws Exception;

    void add(String fileName) throws Exception;

    void rename(String deadName, String newName) throws Exception;

    void save(String fileName, String content) throws Exception;

    String load(String fileName) throws Exception;

}