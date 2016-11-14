package org.tools4j.groovytables

import org.tools4j.groovytables.model.SimpleClass
import spock.lang.Specification

import static org.tools4j.groovytables.TestUtils.assertActualEqualsExpected
import static org.tools4j.groovytables.SimpleTableParser.Var


/**
 * User: ben
 * Date: 4/02/2016
 * Time: 6:28 AM
 */
class SimpleTableParserTest extends Specification {
    def "test createListOfArrays with var headings"() {
        when:
        final List<Object[]> listOfArrays = GroovyTables.createListOfArrays {
            one | two   | three
            1   | 2     | 3
            2   | 3     | 5
            55  | 5     | 60
        }

        then:
        assert listOfArrays.size() == 4
        assertActualEqualsExpected(listOfArrays.get(0), [new Var("one"), new Var("two"), new Var("three")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(1), [(int) 1, (int) 2, (int) 3] as Object[])
        assertActualEqualsExpected(listOfArrays.get(2), [(int) 2, (int) 3, (int) 5] as Object[])
        assertActualEqualsExpected(listOfArrays.get(3), [(int) 55, (int) 5, (int) 60] as Object[])
    }

    def "test simple createListOfArrays"() {
        when:
        final List<Object[]> listOfArrays = GroovyTables.createListOfArrays {
            1  | 2 | 3
            2  | 3 | 5
            55 | 5 | 60
        }

        then:
        assert listOfArrays.size() == 3
        assertActualEqualsExpected(listOfArrays.get(0), [(int) 1, (int) 2, (int) 3] as Object[])
        assertActualEqualsExpected(listOfArrays.get(1), [(int) 2, (int) 3, (int) 5] as Object[])
        assertActualEqualsExpected(listOfArrays.get(2), [(int) 55, (int) 5, (int) 60] as Object[])
    }

    def "test createRows with var headings"() {
        when:
        final Rows rows = GroovyTables.createRows {
            one | two   | three
            1   | 2     | 3
            2   | 3     | 5
            55  | 5     | 60
        }

        then:
        assert rows.size() == 3
        assert rows.getColumnHeadings() == ["one", "two", "three"] as List
        assert rows.get(1), new Row([(int) 1, (int) 2, (int) 3])
        assert rows.get(1), new Row([(int) 2, (int) 3, (int) 5])
        assert rows.get(1), new Row([(int) 55, (int) 5, (int) 60])
        assert !rows.isEmpty()
        assert rows.hasColumnHeadings()
    }

    def "test createRows with no var headings"() {
        when:
        final Rows rows = GroovyTables.createRows {
            1   | 2     | 3
            2   | 3     | 5
            55  | 5     | 60
        }

        then:
        assert rows.size() == 3
        assert rows.get(1), new Row([(int) 1, (int) 2, (int) 3])
        assert rows.get(1), new Row([(int) 2, (int) 3, (int) 5])
        assert rows.get(1), new Row([(int) 55, (int) 5, (int) 60])
        assert !rows.isEmpty()
        assert !rows.hasColumnHeadings()
    }

    def "test createRows with just var headings"() {
        when:
        final Rows rows = GroovyTables.createRows {
            one | two   | three
        }

        then:
        assert rows.size() == 0
        assert rows.getColumnHeadings() == ["one", "two", "three"] as List
        assert rows.isEmpty()
        assert rows.hasColumnHeadings()
    }

    def "test createRows with empty input"() {
        when:
        final Rows rows = GroovyTables.createRows {
        }

        then:
        assert rows.size() == 0
        assert rows.isEmpty()
        assert !rows.hasColumnHeadings()
    }

    def "test createListOfArrays with different types"() {
        given:
        final SimpleClass myInstance = new SimpleClass("hi")
        when:
        final List<Object[]> listOfArrays = GroovyTables.createListOfArrays {

            1.9d | (int) 2 | 3
            1.9 | 2 | myInstance
            2 | 3.5 | "blah"
            55 | "blah" | 60.1
            "blah" | "blah" | 60.1
            false | "blah" | true

        }

        then:
        assert listOfArrays.size() == 6
        assertActualEqualsExpected(listOfArrays.get(0), [(double) 1.9, (int) 2, (int) 3] as Object[])
        assertActualEqualsExpected(listOfArrays.get(1), [BigDecimal.valueOf(1.9), (int) 2, myInstance] as Object[])
        assertActualEqualsExpected(listOfArrays.get(2), [(int) 2, BigDecimal.valueOf(3.5), "blah"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(3), [(int) 55, "blah", BigDecimal.valueOf(60.1)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(4), ["blah", "blah", BigDecimal.valueOf(60.1)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(5), [false, "blah", true] as Object[])
    }

    def "test createListOfArrays two first elements of same type"() {
        given:
        final SimpleClass myInstance = new SimpleClass("hi")

        when:
        final List<Object[]> listOfArrays = GroovyTables.createListOfArrays {
            1.9 | 1.8d
            4 | 2
            "blah1" | "blah2"
            myInstance | myInstance
            false | true
            'c' | 'd'

        }

        then:
        assertActualEqualsExpected(listOfArrays.get(0), [BigDecimal.valueOf(1.9), (double) 1.8] as Object[])
        assertActualEqualsExpected(listOfArrays.get(1), [(int) 4, (int) 2] as Object[])
        assertActualEqualsExpected(listOfArrays.get(2), ["blah1", "blah2"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(3), [myInstance, myInstance] as Object[])
        assertActualEqualsExpected(listOfArrays.get(4), [false, true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(5), ['c', 'd'] as Object[])
    }

    def "test createListOfArrays two first elements of differing types"() {
        when:
        final List<Object[]> listOfArrays = GroovyTables.createListOfArrays {
            Integer.valueOf(2) | BigDecimal.valueOf(1.9)
            Integer.valueOf(2) | Double.MAX_VALUE
            Integer.valueOf(2) | true
            Integer.valueOf(2) | new SimpleClass("hi")
            Integer.valueOf(2) | Long.MAX_VALUE
            Integer.valueOf(2) | "blah"
            Integer.valueOf(2) | 'c'
            BigDecimal.valueOf(1.9) | Integer.valueOf(2)
            BigDecimal.valueOf(1.9) | Double.MAX_VALUE
            BigDecimal.valueOf(1.9) | true
            BigDecimal.valueOf(1.9) | new SimpleClass("hi")
            BigDecimal.valueOf(1.9) | Long.MAX_VALUE
            BigDecimal.valueOf(1.9) | "blah"
            BigDecimal.valueOf(1.9) | 'c'
            Double.MAX_VALUE | Integer.valueOf(2)
            Double.MAX_VALUE | BigDecimal.valueOf(1.9)
            Double.MAX_VALUE | true
            Double.MAX_VALUE | new SimpleClass("hi")
            Double.MAX_VALUE | Long.MAX_VALUE
            Double.MAX_VALUE | "blah"
            Double.MAX_VALUE | 'c'
            true | Integer.valueOf(2)
            true | BigDecimal.valueOf(1.9)
            true | Double.MAX_VALUE
            true | new SimpleClass("hi")
            true | Long.MAX_VALUE
            true | "blah"
            true | 'c'
            new SimpleClass("hi") | Integer.valueOf(2)
            new SimpleClass("hi") | BigDecimal.valueOf(1.9)
            new SimpleClass("hi") | Double.MAX_VALUE
            new SimpleClass("hi") | true
            new SimpleClass("hi") | Long.MAX_VALUE
            new SimpleClass("hi") | "blah"
            new SimpleClass("hi") | 'c'
            Long.MAX_VALUE | Integer.valueOf(2)
            Long.MAX_VALUE | BigDecimal.valueOf(1.9)
            Long.MAX_VALUE | Double.MAX_VALUE
            Long.MAX_VALUE | true
            Long.MAX_VALUE | new SimpleClass("hi")
            Long.MAX_VALUE | "blah"
            Long.MAX_VALUE | 'c'
            "blah" | Integer.valueOf(2)
            "blah" | BigDecimal.valueOf(1.9)
            "blah" | Double.MAX_VALUE
            "blah" | true
            "blah" | new SimpleClass("hi")
            "blah" | Long.MAX_VALUE
            "blah" | 'c'
            'c' | Integer.valueOf(2)
            'c' | BigDecimal.valueOf(1.9)
            'c' | Double.MAX_VALUE
            'c' | true
            'c' | new SimpleClass("hi")
            'c' | Long.MAX_VALUE
            'c' | "blah"
        }

        then:
        assertActualEqualsExpected(listOfArrays.get(0), [Integer.valueOf(2), BigDecimal.valueOf(1.9)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(1), [Integer.valueOf(2), Double.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(2), [Integer.valueOf(2), true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(3), [Integer.valueOf(2), new SimpleClass("hi")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(4), [Integer.valueOf(2), Long.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(5), [Integer.valueOf(2), "blah"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(6), [Integer.valueOf(2), 'c'] as Object[])
        assertActualEqualsExpected(listOfArrays.get(7), [BigDecimal.valueOf(1.9), Integer.valueOf(2)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(8), [BigDecimal.valueOf(1.9), Double.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(9), [BigDecimal.valueOf(1.9), true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(10), [BigDecimal.valueOf(1.9), new SimpleClass("hi")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(11), [BigDecimal.valueOf(1.9), Long.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(12), [BigDecimal.valueOf(1.9), "blah"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(13), [BigDecimal.valueOf(1.9), 'c'] as Object[])
        assertActualEqualsExpected(listOfArrays.get(14), [Double.MAX_VALUE, Integer.valueOf(2)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(15), [Double.MAX_VALUE, BigDecimal.valueOf(1.9)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(16), [Double.MAX_VALUE, true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(17), [Double.MAX_VALUE, new SimpleClass("hi")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(18), [Double.MAX_VALUE, Long.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(19), [Double.MAX_VALUE, "blah"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(20), [Double.MAX_VALUE, 'c'] as Object[])
        assertActualEqualsExpected(listOfArrays.get(21), [true, Integer.valueOf(2)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(22), [true, BigDecimal.valueOf(1.9)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(23), [true, Double.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(24), [true, new SimpleClass("hi")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(25), [true, Long.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(26), [true, "blah"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(27), [true, 'c'] as Object[])
        assertActualEqualsExpected(listOfArrays.get(28), [new SimpleClass("hi"), Integer.valueOf(2)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(29), [new SimpleClass("hi"), BigDecimal.valueOf(1.9)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(30), [new SimpleClass("hi"), Double.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(31), [new SimpleClass("hi"), true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(32), [new SimpleClass("hi"), Long.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(33), [new SimpleClass("hi"), "blah"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(34), [new SimpleClass("hi"), 'c'] as Object[])
        assertActualEqualsExpected(listOfArrays.get(35), [Long.MAX_VALUE, Integer.valueOf(2)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(36), [Long.MAX_VALUE, BigDecimal.valueOf(1.9)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(37), [Long.MAX_VALUE, Double.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(38), [Long.MAX_VALUE, true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(39), [Long.MAX_VALUE, new SimpleClass("hi")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(40), [Long.MAX_VALUE, "blah"] as Object[])
        assertActualEqualsExpected(listOfArrays.get(41), [Long.MAX_VALUE, 'c'] as Object[])
        assertActualEqualsExpected(listOfArrays.get(42), ["blah", Integer.valueOf(2)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(43), ["blah", BigDecimal.valueOf(1.9)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(44), ["blah", Double.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(45), ["blah", true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(46), ["blah", new SimpleClass("hi")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(47), ["blah", Long.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(48), ["blah", 'c'] as Object[])
        assertActualEqualsExpected(listOfArrays.get(49), ['c', Integer.valueOf(2)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(50), ['c', BigDecimal.valueOf(1.9)] as Object[])
        assertActualEqualsExpected(listOfArrays.get(51), ['c', Double.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(52), ['c', true] as Object[])
        assertActualEqualsExpected(listOfArrays.get(53), ['c', new SimpleClass("hi")] as Object[])
        assertActualEqualsExpected(listOfArrays.get(54), ['c', Long.MAX_VALUE] as Object[])
        assertActualEqualsExpected(listOfArrays.get(55), ['c', "blah"] as Object[])
    }

    def "test createListOfArrays second_and_third_items of differing types"() {
        when:
        final List<Object[]> listOfArrays = GroovyTables.createListOfArrays {
            "blah!" | Integer.valueOf(2) | BigDecimal.valueOf(1.9)
            "blah!" | Integer.valueOf(2) | Double.MAX_VALUE
            "blah!" | Integer.valueOf(2) | true
            "blah!" | Integer.valueOf(2) | new SimpleClass("hi")
            "blah!" | Integer.valueOf(2) | Long.MAX_VALUE
            "blah!" | Integer.valueOf(2) | "blah"
            "blah!" | Integer.valueOf(2) | 'c'
            "blah!" | BigDecimal.valueOf(1.9) | Integer.valueOf(2)
            "blah!" | BigDecimal.valueOf(1.9) | Double.MAX_VALUE
            "blah!" | BigDecimal.valueOf(1.9) | true
            "blah!" | BigDecimal.valueOf(1.9) | new SimpleClass("hi")
            "blah!" | BigDecimal.valueOf(1.9) | Long.MAX_VALUE
            "blah!" | BigDecimal.valueOf(1.9) | "blah"
            "blah!" | BigDecimal.valueOf(1.9) | 'c'
            "blah!" | Double.MAX_VALUE | Integer.valueOf(2)
            "blah!" | Double.MAX_VALUE | BigDecimal.valueOf(1.9)
            "blah!" | Double.MAX_VALUE | true
            "blah!" | Double.MAX_VALUE | new SimpleClass("hi")
            "blah!" | Double.MAX_VALUE | Long.MAX_VALUE
            "blah!" | Double.MAX_VALUE | "blah"
            "blah!" | Double.MAX_VALUE | 'c'
            "blah!" | true | Integer.valueOf(2)
            "blah!" | true | BigDecimal.valueOf(1.9)
            "blah!" | true | Double.MAX_VALUE
            "blah!" | true | new SimpleClass("hi")
            "blah!" | true | Long.MAX_VALUE
            "blah!" | true | "blah"
            "blah!" | true | 'c'
            "blah!" | new SimpleClass("hi") | Integer.valueOf(2)
            "blah!" | new SimpleClass("hi") | BigDecimal.valueOf(1.9)
            "blah!" | new SimpleClass("hi") | Double.MAX_VALUE
            "blah!" | new SimpleClass("hi") | true
            "blah!" | new SimpleClass("hi") | Long.MAX_VALUE
            "blah!" | new SimpleClass("hi") | "blah"
            "blah!" | new SimpleClass("hi") | 'c'
            "blah!" | Long.MAX_VALUE | Integer.valueOf(2)
            "blah!" | Long.MAX_VALUE | BigDecimal.valueOf(1.9)
            "blah!" | Long.MAX_VALUE | Double.MAX_VALUE
            "blah!" | Long.MAX_VALUE | true
            "blah!" | Long.MAX_VALUE | new SimpleClass("hi")
            "blah!" | Long.MAX_VALUE | "blah"
            "blah!" | Long.MAX_VALUE | 'c'
            "blah!" | "blah" | Integer.valueOf(2)
            "blah!" | "blah" | BigDecimal.valueOf(1.9)
            "blah!" | "blah" | Double.MAX_VALUE
            "blah!" | "blah" | true
            "blah!" | "blah" | new SimpleClass("hi")
            "blah!" | "blah" | Long.MAX_VALUE
            "blah!" | "blah" | 'c'
            "blah!" | 'c' | Integer.valueOf(2)
            "blah!" | 'c' | BigDecimal.valueOf(1.9)
            "blah!" | 'c' | Double.MAX_VALUE
            "blah!" | 'c' | true
            "blah!" | 'c' | new SimpleClass("hi")
            "blah!" | 'c' | Long.MAX_VALUE
            "blah!" | 'c' | "blah"
        }

        then:
        assertActualEqualsExpected(listOfArrays.get(0), ["blah!", Integer.valueOf(2), BigDecimal.valueOf(1.9)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(1), ["blah!", Integer.valueOf(2), Double.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(2), ["blah!", Integer.valueOf(2), true]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(3), ["blah!", Integer.valueOf(2), new SimpleClass("hi")]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(4), ["blah!", Integer.valueOf(2), Long.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(5), ["blah!", Integer.valueOf(2), "blah"]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(6), ["blah!", Integer.valueOf(2), 'c']  as Object[])
        assertActualEqualsExpected(listOfArrays.get(7), ["blah!", BigDecimal.valueOf(1.9), Integer.valueOf(2)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(8), ["blah!", BigDecimal.valueOf(1.9), Double.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(9), ["blah!", BigDecimal.valueOf(1.9), true]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(10), ["blah!", BigDecimal.valueOf(1.9), new SimpleClass("hi")]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(11), ["blah!", BigDecimal.valueOf(1.9), Long.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(12), ["blah!", BigDecimal.valueOf(1.9), "blah"]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(13), ["blah!", BigDecimal.valueOf(1.9), 'c']  as Object[])
        assertActualEqualsExpected(listOfArrays.get(14), ["blah!", Double.MAX_VALUE, Integer.valueOf(2)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(15), ["blah!", Double.MAX_VALUE, BigDecimal.valueOf(1.9)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(16), ["blah!", Double.MAX_VALUE, true]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(17), ["blah!", Double.MAX_VALUE, new SimpleClass("hi")]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(18), ["blah!", Double.MAX_VALUE, Long.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(19), ["blah!", Double.MAX_VALUE, "blah"]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(20), ["blah!", Double.MAX_VALUE, 'c']  as Object[])
        assertActualEqualsExpected(listOfArrays.get(21), ["blah!", true, Integer.valueOf(2)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(22), ["blah!", true, BigDecimal.valueOf(1.9)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(23), ["blah!", true, Double.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(24), ["blah!", true, new SimpleClass("hi")]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(25), ["blah!", true, Long.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(26), ["blah!", true, "blah"]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(27), ["blah!", true, 'c']  as Object[])
        assertActualEqualsExpected(listOfArrays.get(28), ["blah!", new SimpleClass("hi"), Integer.valueOf(2)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(29), ["blah!", new SimpleClass("hi"), BigDecimal.valueOf(1.9)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(30), ["blah!", new SimpleClass("hi"), Double.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(31), ["blah!", new SimpleClass("hi"), true]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(32), ["blah!", new SimpleClass("hi"), Long.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(33), ["blah!", new SimpleClass("hi"), "blah"]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(34), ["blah!", new SimpleClass("hi"), 'c']  as Object[])
        assertActualEqualsExpected(listOfArrays.get(35), ["blah!", Long.MAX_VALUE, Integer.valueOf(2)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(36), ["blah!", Long.MAX_VALUE, BigDecimal.valueOf(1.9)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(37), ["blah!", Long.MAX_VALUE, Double.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(38), ["blah!", Long.MAX_VALUE, true]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(39), ["blah!", Long.MAX_VALUE, new SimpleClass("hi")]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(40), ["blah!", Long.MAX_VALUE, "blah"]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(41), ["blah!", Long.MAX_VALUE, 'c']  as Object[])
        assertActualEqualsExpected(listOfArrays.get(42), ["blah!", "blah", Integer.valueOf(2)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(43), ["blah!", "blah", BigDecimal.valueOf(1.9)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(44), ["blah!", "blah", Double.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(45), ["blah!", "blah", true]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(46), ["blah!", "blah", new SimpleClass("hi")]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(47), ["blah!", "blah", Long.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(48), ["blah!", "blah", 'c']  as Object[])
        assertActualEqualsExpected(listOfArrays.get(49), ["blah!", 'c', Integer.valueOf(2)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(50), ["blah!", 'c', BigDecimal.valueOf(1.9)]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(51), ["blah!", 'c', Double.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(52), ["blah!", 'c', true]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(53), ["blah!", 'c', new SimpleClass("hi")]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(54), ["blah!", 'c', Long.MAX_VALUE]  as Object[])
        assertActualEqualsExpected(listOfArrays.get(55), ["blah!", 'c', "blah"]  as Object[])
    }
}
