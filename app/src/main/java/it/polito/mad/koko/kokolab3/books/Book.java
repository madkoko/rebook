package it.polito.mad.koko.kokolab3.books;

import java.io.Serializable;

import it.polito.mad.koko.kokolab3.profile.Profile;

/**
 * Created by Francesco on 10/04/2018.
 */

public class Book implements Serializable {

    private static final String TAG = "Book";

    private String ISBN;
    private String title;
    private String author;
    private String publisher;
    private String editionYear;
    private String bookConditions;
    private String image;
    private String uid;
    private String borrowedTo;
    private String sharable;
    private Profile bookOwner;

    public Book(){};

    public Book(String ISBN, String title, String author, String publisher, String editionYear, String bookConditions, String image, String uid, String borrowedTo, String sharable, Profile bookOwner) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.editionYear = editionYear;
        this.bookConditions = bookConditions;
        this.image = image;
        this.uid=uid;
        this.borrowedTo = borrowedTo;
        this.sharable = sharable;
        this.bookOwner=new Profile(bookOwner.getName(),null,null,bookOwner.getLocation(),null,bookOwner.getImage(),bookOwner.getPosition(),bookOwner.getTokenMessage());
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEditionYear() {
        return editionYear;
    }

    public void setEditionYear(String editionYear) {
        this.editionYear = editionYear;
    }

    public String getBookConditions() {
        return bookConditions;
    }

    public void setBookConditions(String bookConditions) {
        this.bookConditions = bookConditions;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBorrowedTo() {
        return borrowedTo;
    }

    public void setBorrowedTo(String borrowedTo) {
        this.borrowedTo = borrowedTo;
    }

    public String getSharable() {
        return sharable;
    }

    public void setSharable(String sharable) {
        this.sharable = sharable;
    }

    public Profile getBookOwner() {
        return bookOwner;
    }

    public void setBookOwner(Profile bookOwner) {
        this.bookOwner = bookOwner;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + ISBN + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", editionYear='" + editionYear + '\'' +
                ", bookConditions='" + bookConditions + '\'' +
                ", image='" + image + '\'' +
                ", uid='" + uid + '\'' +
                ", borrowedTo='" + borrowedTo + '\'' +
                ", sharable='" + sharable + '\'' +
                ", bookOwner=" + bookOwner +
                '}';
    }

}

