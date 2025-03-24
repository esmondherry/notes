package com.esmo.model;

import java.util.HashSet;
import java.util.Set;

public class Note {

    private String name;
    private String content;
    private Set<String> tags;

    public Note(String name) {
        this.name = name;
        this.content = "";
        this.tags = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
