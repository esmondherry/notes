package com.esmo;


public class InfoCenter {
    private static InfoCenter infoStation;
    public static InfoCenter getInfoCenter() {
        if (infoStation == null) {
            infoStation = new InfoCenter();
        }
        return infoStation;
    }
    private String folderPath;
    private PropertiesController p;

    private InfoCenter() {
        p = new PropertiesController();
        folderPath = p.getProperty("folderPath");
    }

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void save() {
        p.setProperty("folderPath", folderPath);
        p.saveProperties();
    }
}
