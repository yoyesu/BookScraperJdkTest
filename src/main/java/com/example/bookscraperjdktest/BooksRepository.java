package com.example.bookscraperjdktest;

public interface BooksRepository {

    Book getBookFromEpubPubSite(String url);
    Book getBookFromAllFreeNovelSite(String url);
}
