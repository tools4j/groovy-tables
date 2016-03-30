package org.tools4j.groovytabledsl.model;

import org.tools4j.groovytabledsl.model.Product;

/**
 * User: ben
 * Date: 19/02/2016
 * Time: 5:06 PM
 */

public class Book implements Product {
    public String author;
    public String title;
    public Double cost;
    public long serialNumber;

    public Book() {
    }

    public Book(final String author, final String title, final Double cost, final long serialNumber) {
        this.author = author;
        this.title = title;
        this.cost = cost;
        this.serialNumber = serialNumber;
    }

    public Book(final String title, final Double cost, final long serialNumber) {
        this.title = title;
        this.cost = cost;
        this.serialNumber = serialNumber;
    }


    public Book(final String author) {
        this.author = author;
    }

    public Book(final String author, final String title) {
        this.author = author;
        this.title = title;
    }

    public static Book create(final String author, final String title, final Double cost, final long serialNumber) {
        return new Book(author, title, cost, serialNumber);
    }

    public static Book create(final String title, final Double cost, final long serialNumber) {
        return new Book(title, cost, serialNumber);
    }


    public static Book create(final String author) {
        return new Book(author);
    }

    public static Book create(final String author, final String title) {
        return new Book(author, title);
    }

    public static Book createFromAuthorAndTitle(final String author, final String title) {
        return new Book(author, title);
    }

    public static String notAStaticFactoryMethod(final String author, final String title) {
        return "boo!!";
    }

    public static class PictureBook extends Book {
        public final String coverPhotoUrl;

        public PictureBook(final String coverPhotoUrl){
            this.coverPhotoUrl = coverPhotoUrl;
        }

        public static PictureBook createPictureBook(final String coverPhotoUrl){
            return new PictureBook(coverPhotoUrl);
        }

        /*
         * This is here to see that the TypeCoercion class does _not_ pick it up as a static factory
         * method, as it returns a type which is not assignable to PictureBook class
         */
        public static Book createBook(){
            return new Book();
        }
    }

    public static PictureBook createPictureBook(final String coverPhotoUrl){
        return PictureBook.createPictureBook(coverPhotoUrl);
    }

    public void setSerialNumber(final int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setSerialNumber(final long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void setCost(final Double cost) {
        this.cost = cost;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public long getSerialNumber() {
        return serialNumber;
    }

    @Override
    public Double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", cost=" + cost +
                ", serialNumber=" + serialNumber +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;

        final Book book = (Book) o;

        if (serialNumber != book.serialNumber) return false;
        if (author != null ? !author.equals(book.author) : book.author != null) return false;
        if (title != null ? !title.equals(book.title) : book.title != null) return false;
        return cost != null ? cost.equals(book.cost) : book.cost == null;

    }

    @Override
    public int hashCode() {
        int result = author != null ? author.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (int) (serialNumber ^ (serialNumber >>> 32));
        return result;
    }
}
