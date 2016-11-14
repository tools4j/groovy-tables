package org.tools4j.groovytables

import spock.lang.Specification

/**
 * User: ben
 * Date: 7/11/2016
 * Time: 5:16 PM
 */
class RowsTest extends Specification {
    def "test With"() {
        when:
        final Iterator<Row> rows = Rows.with {
            symbol    | price   | quantity
            "AUD/USD" | 1.0023  | 1200000
            "AUD/USD" | 1.0024  | 1400000
            "AUD/USD" | 1.0026  | 2000000
            "AUD/USD" | 1.0029  | 5000000
        }.iterator()

        then:
        assert rows.next() == new Row([new SimpleTableParser.Var("symbol"), new SimpleTableParser.Var("price"), new SimpleTableParser.Var("quantity")])
        assert rows.next() == new Row(["AUD/USD", 1.0023, 1200000] as List<Object>)
        assert rows.next() == new Row(["AUD/USD", 1.0024, 1400000] as List<Object>)
        assert rows.next() == new Row(["AUD/USD", 1.0026, 2000000] as List<Object>)
        assert rows.next() == new Row(["AUD/USD", 1.0029, 5000000] as List<Object>)
        assert !rows.hasNext()
    }

    def "test With just headings"() {
        when:
        final Iterator<Row> rows = Rows.with {
            symbol    | price   | quantity
        }.iterator()

        then:
        assert rows.next() == new Row([new SimpleTableParser.Var("symbol"), new SimpleTableParser.Var("price"), new SimpleTableParser.Var("quantity")])
        assert !rows.hasNext()
    }

    def "test With empty"() {
        when:
        final Iterator<Row> rows = Rows.with {
        }.iterator()

        then:
        assert !rows.hasNext()
    }

    def "test With no heading"() {
        when:
        final Iterator<Row> rows = Rows.with {
            "AUD/USD" | 1.0023  | 1200000
            "AUD/USD" | 1.0024  | 1400000
            "AUD/USD" | 1.0026  | 2000000
            "AUD/USD" | 1.0029  | 5000000
        }.iterator()

        then:
        assert rows.next() == new Row(["AUD/USD", 1.0023, 1200000] as List<Object>)
        assert rows.next() == new Row(["AUD/USD", 1.0024, 1400000] as List<Object>)
        assert rows.next() == new Row(["AUD/USD", 1.0026, 2000000] as List<Object>)
        assert rows.next() == new Row(["AUD/USD", 1.0029, 5000000] as List<Object>)
        assert !rows.hasNext()
    }
}
