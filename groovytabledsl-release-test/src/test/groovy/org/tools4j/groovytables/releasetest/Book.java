package org.tools4j.groovytables.releasetest;


import groovy.transform.EqualsAndHashCode;

/**
 * User: ben
 * Date: 19/02/2016
 * Time: 5:06 PM
 */
@EqualsAndHashCode
public class Book {
    String author;
    String title;
    Double cost;
    long year;

    public Book(final String author, final String title, final Double cost, final long year) {
        this.author = author;
        this.title = title;
        this.cost = cost;
        this.year = year;
    }

    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", cost=" + cost +
                ", year=" + year +
                '}';
    }
}
