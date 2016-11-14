package org.tools4j.groovytables

import spock.lang.Specification

/**
 * User: ben
 * Date: 9/11/2016
 * Time: 7:41 AM
 */
class WithTableExecuteTest extends Specification {
    def "with no headings"() {
        given:
        List<List<Object>> results = new ArrayList<>()
        GroovyTables.withTable {
            "one"         | 1
            "two"         | 2
            "three"       | 3
        }.execute{ final String myString, final int myNum ->
            results.add([myString, myNum])
        }
        assert results.size() == 3
        assert results.get(0) == ["one", 1]
        assert results.get(1) == ["two", 2]
        assert results.get(2) == ["three", 3]
    }

    def "with no headings - varargs"() {
        when:
        GroovyTables.withTable {
            "one"         | 1 | 2 | 3
            "two"         | 2 | 3 | 4
            "three"       | 3 | 4 | 5
        }.execute{ final String myString, final ... myNums ->
            assert false: "Code should not get here.  Exception should have already occurred"
            //results.add([myString, myNums])
        }

        then:
        IllegalArgumentException illegalArgumentException = thrown()
        assert illegalArgumentException.getMessage().contains("Mismatched number of args")
        // TODO Add support for varargs
        // assert results.size() == 3
        // assert results.get(0) == ["one",  [1, 2, 3]]
        // assert results.get(1) == ["two",  [2, 3, 4]]
        // assert results.get(2) == ["three",[3, 4, 5]]
    }

    def "with no headings - using int to long"() {
        given:
        List<List<Object>> results = new ArrayList<>()
        GroovyTables.withTable {
            "one"         | 1
            "two"         | 2
            "three"       | 3
        }.execute{ final String myString, final long myNum ->
            results.add([myString, myNum])
        }
        assert results.size() == 3
        assert results.get(0) == ["one", 1L]
        assert results.get(1) == ["two", 2L]
        assert results.get(2) == ["three", 3L]
    }

    def "with no headings - args not suitable"() {
        when:
        GroovyTables.withTable {
            "one"         | 1
            "two"         | 2
            "three"       | 3
        }.execute{ final String myString, final boolean shouldBeANumber ->
            assert false: "Code should not get here.  Exception should have already occurred"
        }

        then:
        IllegalArgumentException illegalArgumentException = thrown()
        assert illegalArgumentException.getMessage().contains("Error processing row [one, 1]. Could not coerce argument '1' of class <Integer> to closure argument of type <boolean.>")
    }

    def "empty"() {
        given:
        List<List<Object>> results = new ArrayList<>()
        GroovyTables.withTable {
        }.execute{ final String myString, final int myNum ->
            results.add([myString, myNum])
        }
        assert results.isEmpty()
    }

    def "with headings, playing with numbers"() {
        given:
        GroovyTables.withTable {
            num1  | num2 | product
            1     | 2    |  2
            2     | 3    |  6
            3     | 4    | 12
        }.execute {
            assert num1 * num2 == product
        }
    }

    def "with headings"(){
        given:
        List<List<Object>> results = new ArrayList<>()
        GroovyTables.withTable {
            myString      | myNum
            "one"         | 1
            "two"         | 2
            "three"       | 3
        }.execute {
            results.add([myString, myNum])
        }

        assert results.size() == 3
        assert results.get(0) == ["one", 1]
        assert results.get(1) == ["two", 2]
        assert results.get(2) == ["three", 3]
    }

    def "both headings and closure arguments specified"() {
        when:
        GroovyTables.withTable {
            myString      | myNum
            "one"         | 1
            "two"         | 2
            "three"       | 3
        }.execute{ final String myString, final int myNum ->
            assert false: "Code should not get here.  Exception should have already occurred"
        }

        then:
        Exception throwable = thrown()
        assert throwable.getMessage().contains("Cannot determine how to call closure")
    }

    def "neither headings nor closure arguments specified"() {
        when:
        GroovyTables.withTable {
            "one"         | 1
            "two"         | 2
            "three"       | 3
        }.execute{
            assert false: "Code should not get here.  Exception should have already occurred"
        }

        then:
        Exception throwable = thrown()
        assert throwable.getMessage().contains("Cannot call closure")
    }
}
