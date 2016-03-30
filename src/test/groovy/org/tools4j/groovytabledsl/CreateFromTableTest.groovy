package org.tools4j.groovytabledsl

import groovy.transform.ToString
import org.tools4j.groovytabledsl.model.Book
import org.tools4j.groovytabledsl.model.Bookshelf
import org.tools4j.groovytabledsl.model.Product
import org.tools4j.groovytabledsl.model.QuoteHistory
import org.tools4j.groovytabledsl.model.QuoteHistoryUsingList
import org.tools4j.groovytabledsl.model.Side
import spock.lang.Specification

/**
 * User: ben
 * Date: 11/02/2016
 * Time: 6:39 AM
 */
class CreateFromTableTest extends Specification {

    def "CreateTable with lists"() {
        given:
        def books1 = CreateFromTable.createListOf(Book.class).fromTable {
            author         | title                  | cost  | year
            "Terry Denton" | "Gasp"                 | 15.50 | 1990
            "Terry Denton" | "Flying Man"           | 15.23 | 1991
        }

        def books2 = CreateFromTable.createListOf(Book.class).fromTable {
            author         | title                  | cost  | year
            "Terry Denton" | "Felix and Alexander"  |   0.0 | 1992
            "Terry Denton" | "Spooner or Later"     |  0.50 | 1993
        }

        def books3 = CreateFromTable.createListOf(Book.class).fromTable {
            author         | title                  | cost  | year
            "Terry Denton" | "12 Storey Treehouse"  |     3 | 1994
            "Terry Denton" | "Gasp"                 |     5 | 1995
        }

        when:
        def bookshelves = CreateFromTable.createListOf(Bookshelf).fromTable {
            bookshelfOwner  | title
            "Ben Warner"    | books1
            "Beno Warner"   | books2
            "Benny Warner"  | books3
        }

        then:
        assert bookshelves.size() == 3;
    }

    def "CreateTable with array"() {
        when:
        List<QuoteHistory> history = CreateFromTable.createListOf(QuoteHistory).fromTable {
            symbol    | side      | prices
            "AUD/USD" | Side.BUY  | [15.50, 15.51, 15.52, 15.53 ]
            "GBP/USD" | Side.BUY  | [ 10.02, 10.01, 10.02, 10.05, 10.06 ]
        }

        then:
        assert history.size() == 2

        and:
        assert history.get(0).symbol == "AUD/USD"
        assert history.get(0).side == Side.BUY
        assert history.get(0).prices == [ 15.50, 15.51, 15.52, 15.53 ] as double[]

        and:
        assert history.get(1).symbol == "GBP/USD"
        assert history.get(1).side == Side.BUY
        assert history.get(1).prices == [ 10.02, 10.01, 10.02, 10.05, 10.06 ] as double[]
    }

    def "CreateTable with list"() {
        when:
        List<QuoteHistoryUsingList> history = CreateFromTable.createListOf(QuoteHistoryUsingList).fromTable {
            symbol    | side      | prices
            "AUD/USD" | Side.BUY  | [ 15.50, 15.51, 15.52, 15.53 ]
            "GBP/USD" | Side.BUY  | [ 10.02, 10.01, 10.02, 10.05, 10.06 ]
        }

        then:
        assert history.size() == 2

        and:
        assert history.get(0).symbol == "AUD/USD"
        assert history.get(0).side == Side.BUY
        assert history.get(0).prices == [ 15.50, 15.51, 15.52, 15.53 ] as List<Double>

        and:
        assert history.get(1).symbol == "GBP/USD"
        assert history.get(1).side == Side.BUY
        assert history.get(1).prices == [ 10.02, 10.01, 10.02, 10.05, 10.06 ] as List<Double>
    }


    def "CreateTable_usingChainedMethod"() {
        when:

        def books = CreateFromTable.createListOf(Book.class).fromTable {
            author         | title                  | cost  | year
            "Terry Denton" | "Gasp"                 | 15.50 | 1990
            "Terry Denton" | "Flying Man"           | 15.23 | 1991
            "Terry Denton" | "Felix and Alexander"  |   0.0 | 1992
            "Terry Denton" | "Spooner or Later"     |  0.50 | 1993
            "Terry Denton" | "12 Storey Treehouse"  |     3 | 1994
            "Terry Denton" | "Gasp"                 |     5 | 1995
        }

        then:
        assert books != null;
        assert books.size() == 6;
        assert books.count {it.author == "Terry Denton"} == 6
        assert books.count {it.title == "Gasp"} == 2
    }

    enum PaymentMethod{
        CASH, CARD
    }

    @ToString
    static class Receipt<T extends Product>{
        final T product
        final PaymentMethod paymentMethod

        Receipt(final T product, final PaymentMethod paymentMethod) {
            this.product = product
            this.paymentMethod = paymentMethod
        }
    }


    def "CreateTable_includingNestedClassAndGenerics"() {
        when:
        final List<Receipt<Book>> receipts = CreateFromTable.createListOf(Receipt.class).fromTable {
            new Book("Terry Denton", "Gasp"                , 15.50, 1990) | PaymentMethod.CARD
            new Book("Terry Denton", "Flying Man"          , 15.23, 1991) | PaymentMethod.CASH
            new Book("Terry Denton", "Felix and Alexander" ,   0.0, 1992) | PaymentMethod.CARD
            new Book("Terry Denton", "Spooner or Later"    ,  0.50, 1993) | PaymentMethod.CARD
            new Book("Terry Denton", "12 Storey Treehouse" ,     3, 1994) | PaymentMethod.CASH
            new Book("Terry Denton", "Gasp"                ,     5, 1995) | PaymentMethod.CASH
        }

        then:
        assert receipts.size() == 6;
        assert receipts.count {it.product.author == "Terry Denton"} == 6
    }
}
