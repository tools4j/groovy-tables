package org.tools4j.groovytables

import org.tools4j.groovytables.model.*
import spock.lang.Specification

/**
 * User: ben
 * Date: 11/02/2016
 * Time: 6:39 AM
 */
class ExamplesTest extends Specification {

    def "create list of books"() {
        when:
        List<Book> books = CreateFromTable.createListOf(Book.class).fromTable {
            author                | title                    | cost  | year
            "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
            "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
            "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
            "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
            "George Orwell"       | "1984"                   |  8.95 | 1949
            "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
            "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
        }

        then:
        assert books.size() == 7
    }

    def "create list of books - more concise syntax"() {
        when:
        List<Book> books = books {
            author                | title                    | cost  | year
            "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
            "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
            "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
            "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
            "George Orwell"       | "1984"                   |  8.95 | 1949
            "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
            "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
        }

        then:
        assert books.size() == 7
    }

    private List<Book> books(Closure closure){
        return CreateFromTable.createListOf(Book.class).fromTable(closure)
    }

    def "create list of quotes"() {
        when:
        List<Quote> quotes = CreateFromTable.createListOf(Quote).fromTable {
            symbol    | price   | quantity
            "AUD/USD" | 1.0023  | 1200000
            "AUD/USD" | 1.0024  | 1400000
            "AUD/USD" | 1.0026  | 2000000
            "AUD/USD" | 1.0029  | 5000000
        }

        then:
        assert quotes.size() == 4
    }

    def "create list of books - using constructor only"() {
        when:
        List<Book> books = CreateFromTable.createFromTable(Book.class, ConstructionMethodFilter.CONSTRUCTORS, {
            author                | title                    | cost  | year
            "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
            "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
            "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
            "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
            "George Orwell"       | "1984"                   |  8.95 | 1949
            "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
            "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
        });

        then:
        assert books.size() == 7
    }

    def "create list of books - using static factory methods with given name"() {
        when:
        List<Book> books = CreateFromTable.createFromTable(Book.class, ConstructionMethodFilter.filter().withStaticFactoryMethods().withName("create"), {
            author                | title                    | cost  | year
            "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
            "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
            "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
            "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
            "George Orwell"       | "1984"                   |  8.95 | 1949
            "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
            "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
        });

        then:
        assert books.size() == 7
    }

}
