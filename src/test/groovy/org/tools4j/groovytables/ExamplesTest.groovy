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
        List<Book> books = GroovyTables.createListOf(Book.class).fromTable {
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
        return GroovyTables.createListOf(Book.class).fromTable(closure)
    }

    def "create list of quotes"() {
        when:
        List<Quote> quotes = GroovyTables.createListOf(Quote).fromTable {
            symbol    | price   | quantity
            "AUD/USD" | 1.0023  | 1200000
            "AUD/USD" | 1.0024  | 1400000
            "AUD/USD" | 1.0026  | 2000000
            "AUD/USD" | 1.0029  | 5000000
        }

        then:
        assert quotes.size() == 4
    }

    def "create list of quotes - using withTableExecute and closure args"() {
        when:
        QuoteBook quoteBook = new QuoteBook()

        GroovyTables.withTable {
            Side.BUY  | "AUD/USD" | 1.0023  | 1200000
            Side.BUY  | "AUD/USD" | 1.0022  | 1400000
            Side.BUY  | "AUD/USD" | 1.0020  | 2000000
            Side.BUY  | "AUD/USD" | 1.0019  | 5000000
            Side.SELL | "AUD/USD" | 1.0025  | 1100000
            Side.SELL | "AUD/USD" | 1.0026  | 1600000
            Side.SELL | "AUD/USD" | 1.0028  | 2020000

        }.forEachRow { Side side, String symbol, double price, int qty ->
            quoteBook.getSide(side).add(new Quote(symbol: symbol, price: price, quantity: qty))
        }

        then:
        assert quoteBook.bids.size() == 4
        assert quoteBook.asks.size() == 3
    }

    def "create list of quotes - using withTableExecute and column headings"() {
        when:
        QuoteBook quoteBook = new QuoteBook()

        GroovyTables.withTable {
            side      | symbol    | price   | qty
            Side.BUY  | "AUD/USD" | 1.0023  | 1200000
            Side.BUY  | "AUD/USD" | 1.0022  | 1400000
            Side.BUY  | "AUD/USD" | 1.0020  | 2000000
            Side.BUY  | "AUD/USD" | 1.0019  | 5000000
            Side.SELL | "AUD/USD" | 1.0025  | 1100000
            Side.SELL | "AUD/USD" | 1.0026  | 1600000
            Side.SELL | "AUD/USD" | 1.0028  | 2020000

        }.forEachRow {
            quoteBook.getSide(side).add(new Quote(symbol: symbol, price: price, quantity: qty))
        }

        then:
        assert quoteBook.bids.size() == 4
        assert quoteBook.asks.size() == 3
    }

    def "create list of books - using constructor only"() {
        when:
        List<Book> books = GroovyTables.createFromTable(Book.class, ConstructionMethodFilter.CONSTRUCTORS, {
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
        List<Book> books = GroovyTables.createFromTable(Book.class, ConstructionMethodFilter.filter().withStaticFactoryMethods().withName("create"), {
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


    def "data to xml"() {
        when:
        def xml = toXml {
            a | b | c
            1 | 3 | 3
            7 | 4 | 4
            0 | 0 | 0
        }

        then:
        assert xml ==   "<row>\n" +
                        "    <a>1</a>\n" +
                        "    <b>3</b>\n" +
                        "    <c>3</c>\n" +
                        "</row>\n" +
                        "<row>\n" +
                        "    <a>7</a>\n" +
                        "    <b>4</b>\n" +
                        "    <c>4</c>\n" +
                        "</row>\n" +
                        "<row>\n" +
                        "    <a>0</a>\n" +
                        "    <b>0</b>\n" +
                        "    <c>0</c>\n" +
                        "</row>\n"
    }

    private String toXml(Closure closure) {
        List<Object[]> rows = GroovyTables.createListOfArrays(closure)
        Iterator<Object[]> rowsIterator = rows.iterator()
        Object[] headingRow = rowsIterator.next()
        StringBuilder sb = new StringBuilder()
        while (rowsIterator.hasNext()) {
            Object[] row = rowsIterator.next()
            sb.append("<row>\n")
            for (int i = 0; i < row.length; i++) {
                sb.append("    <${headingRow[i].name}>${row[i]}</${headingRow[i].name}>\n")
            }
            sb.append("</row>\n")
        }
        return sb.toString()
    }
}
