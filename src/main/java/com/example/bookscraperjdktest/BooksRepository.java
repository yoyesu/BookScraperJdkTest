package com.example.bookscraperjdktest;

public interface BooksRepository {

    Book getBookByUrl(String url);
    Book getAllFreeNovelBook(String url);
}
