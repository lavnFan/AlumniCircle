package com.seu.wufan.alumnicircle.api.entity.item;

public class Friend {

    private String name;
    private String letters;

    public Friend(String name) {
        this.name = name;
    }

    public Friend() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }
}
