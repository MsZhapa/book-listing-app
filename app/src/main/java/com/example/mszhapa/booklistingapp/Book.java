package com.example.mszhapa.booklistingapp;

/**
 * Created by MsZhapa on 11/07/2017.
 */

public class Book {

    private String mTitle;
    private StringBuilder mAuthors;
    private String mUrl;

    public Book(String title, StringBuilder authors, String url){
        mTitle = title;
        mAuthors = authors;
        mUrl = url;
    }
    public String getTitle() {
        return mTitle;
    }

    public StringBuilder getAuthor() {
        return mAuthors;
    }

    public String getUrl(){
        return mUrl;
    }
}
