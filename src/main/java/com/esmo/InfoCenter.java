package com.esmo;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.stage.Window;

public class InfoCenter {
    private static InfoCenter infoStation;

    public static InfoCenter getInfoCenter() {
        if (infoStation == null) {
            infoStation = new InfoCenter();
        }
        return infoStation;
    }

    private ObservableMap<String, String> settings;
    private PropertiesController p;

    private InfoCenter() {
        p = new PropertiesController();
        settings = FXCollections.observableHashMap();
        settings.put("folderPath", p.getProperty("folderPath"));
        settings.put("theme", p.getProperty("theme", "Default"));
        settings.put("alwaysOnTop", p.getProperty("alwaysOnTop", "false"));
    }

    public void addListener(MapChangeListener<? super String, ? super String> listener) {
        settings.addListener(listener);
    }

    public String getFolderPath() {
        return settings.get("folderPath");
    }

    public void setFolderPath(String folderPath) {
        settings.put("folderPath", folderPath);
    }

    public String getTheme() {
        return settings.get("theme");
    }

    public void setTheme(String theme) {
        settings.put("theme", theme);
        reloadTheme();
    }

    public boolean isOnTop() {
        return Boolean.parseBoolean(settings.get("alwaysOnTop"));
    }

    public void setOnTop(boolean value) {
        settings.put("alwaysOnTop", value + "");
    }

    public void setOnTop(String value) {
        settings.put("alwaysOnTop", value);
    }

    private void reloadTheme() {
        for (Window w : Window.getWindows()) {
            loadTheme(w);
        }
    }

    public void loadTheme(Window w) {
        loadTheme(w.getScene().getStylesheets());
    }

    public void loadTheme(ObservableList<String> sheet) {
        sheet.clear();
        switch (settings.get("theme")) {
            case "Dawn":
                sheet.add("css/dawn.css");
                break;
            case "Day":
                sheet.add("css/day.css");
                break;
            case "Dusk":
                sheet.add("css/dusk.css");
                break;
            case "Dark":
                sheet.add("css/dark.css");
                break;
            case "Default":
            default:
                break;
        }
    }

    public void save() {
        for (String s : settings.keySet()) {
            p.setProperty(s, settings.get(s));
        }
        p.saveProperties();
    }

}
