package com.esmo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatabaseModel implements Storage {
    private ObservableList<String> noteList = FXCollections.observableArrayList();
    private Connection database;

    public DatabaseModel(String database) throws SQLException {
        this.database = DriverManager.getConnection(database);
        updateNoteList();
    }

    private void updateNoteList() {
        List<String> notes = new ArrayList<>();
        try {
            var statement = database.createStatement()
                    .executeQuery("select name from notes;");
            while (statement.next()) {
                notes.add(statement.getString("name"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        noteList.setAll(notes);
        noteList.sort((a, b) -> a.compareToIgnoreCase(b));
    }

    @Override
    public ObservableList<String> getFileList() {
        return noteList;
    }

    @Override
    public void delete(String fileName) throws SQLException {
        var statement = database.prepareStatement("delete from notes where name = ?");
        statement.setString(1, fileName);
        statement.executeUpdate();
        statement.close();
        noteList.remove(fileName);
    }

    @Override
    public void add(String fileName) throws SQLException {
        var statement = database.prepareStatement("insert into notes (name) values (?)");
        statement.setString(1, fileName);
        statement.executeUpdate();
        statement.close();
        noteList.add(fileName);
    }

    @Override
    public void rename(String deadName, String newName) throws SQLException {
        var statement = database.prepareStatement("update notes set name = ? where name = ?");
        statement.setString(1, newName);
        statement.setString(2, deadName);
        statement.executeUpdate();
        statement.close();
        noteList.remove(deadName);
        noteList.add(newName);
    }

    @Override
    public void save(String name, String content) throws SQLException {
        var statement = database.prepareStatement("update notes set content = ? where name = ?");
        statement.setString(1, content);
        statement.setString(2, name);
        statement.executeUpdate();
        statement.close();
    }

    @Override
    public String load(String name) throws Exception {
        var statement = database.prepareStatement("select content from notes where name = ?");
        statement.setString(1, name);
        var content = statement.executeQuery().getString("content");
        statement.close();
        return content;
    }
}