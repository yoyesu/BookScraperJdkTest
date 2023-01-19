package com.example.bookscraperjdktest;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String name;
    private List<String> sentences = new ArrayList<>();

    public Book(String name, List<String> sentences) {
        this.name = name;
        this.sentences = sentences;
    }

    public Book() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSentences() {
        return sentences;
    }

    public void setSentences(List<String> sentences) {
        this.sentences = sentences;
    }

    @Override
    public String toString() {
        return "Book{" +
                "name='" + name + '\'' +
                ", sentences=" + sentences +
                '}';
    }
}
