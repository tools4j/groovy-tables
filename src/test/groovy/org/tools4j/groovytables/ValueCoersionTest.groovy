package org.tools4j.groovytables

import spock.lang.Specification
import spock.lang.Unroll

import static org.tools4j.groovytables.Suitability.*

/**
 * User: ben
 * Date: 12/02/2016
 * Time: 6:38 AM
 */
class ValueCoersionTest extends Specification {

    @Unroll
    def "CoerceToArray"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Object[]>(result, suitability)
        ValueCoercionResult actualResults = ValueCoersion.coerceToArrayOfGivenType(value, Double)

        then:
        assert actualResults == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults"

        where:
        value                           | result                           | suitability
        [1,2,3] as Object[]             | [1.0d, 2.0d, 3.0d] as Double[]   | SUITABLE_WITH_COERCION
        [1.1, 2.2, 3.3] as Object[]     | [1.1d, 2.2d, 3.3d] as Double[]   | SUITABLE_WITH_EXPECTED_COERCION
        [2.2, 3.3, 4.4]                 | [2.2d, 3.3d, 4.4d] as Double[]   | SUITABLE_WITH_EXPECTED_COERCION
        [1.1d, 2.2d, 3.3d] as Double[]  | [1.1d, 2.2d, 3.3d] as Double[]   | SUITABLE
        [2.2d, 3.3d, 4.4d]              | [2.2d, 3.3d, 4.4d] as Double[]   | SUITABLE_WITH_EXPECTED_COERCION
        2.2                             | null                             | NOT_SUITABLE
    }

    def "CoerceToArrayOfArrays upCastingFromDoubleToObject"() {
        given:
        final Object[][] value = [[2.2d, 2.3d] as Object[], [3.3d, 3.4d] as Object[], [4.4d, 4.5d] as Object[]] as Object[]

        when:
        final ValueCoercionResult<Object[][]> valueCoercionResult = ValueCoersion.coerceToArrayOfGivenType(value, value.class.getComponentType())

        then:
        assert valueCoercionResult.suitability == SUITABLE_BY_UP_CASTING
        assertArrayActualEqualsExpected(valueCoercionResult.result, [[2.2d, 2.3d] as Object[], [3.3d, 3.4d] as Object[], [4.4d, 4.5d] as Object[]] as Object[])
    }

    def "CoerceToArrayOfArrays Doubles"() {
        given:
        final Double[][] value = [[2.2d, 2.3d] as Double[], [3.3d, 3.4d] as Double[], [4.4d, 4.5d] as Double[]] as Double[][]

        when:
        final ValueCoercionResult<Double[][]> valueCoercionResult = ValueCoersion.coerceToArrayOfGivenType(value, Double[].class)

        then:
        assert valueCoercionResult.suitability == SUITABLE
        assertArrayActualEqualsExpected(valueCoercionResult.result, [[2.2d, 2.3d] as Double[], [3.3d, 3.4d] as Double[], [4.4d, 4.5d] as Double[]] as Double[][])
    }


    def "CoerceToListOfLists upCastingFromDoubleToObject"() {
        given:
        final List<List> value = [[2.2d, 2.3d], [3.3d, 3.4d], [4.4d, 4.5d]]

        when:
        final ValueCoercionResult<List<List>> valueCoercionResult = ValueCoersion.coerceToListOfGivenType(value, List)

        then:
        assert valueCoercionResult.suitability == SUITABLE_BY_UP_CASTING
        assert valueCoercionResult.result == [[2.2d, 2.3d], [3.3d, 3.4d], [4.4d, 4.5d]]
    }

    def "CoerceToListOfLists Doubles"() {
        given:
        final List<List<Double>> value = [[2.2d, 2.3d], [3.3d, 3.4d], [4.4d, 4.5d]]

        when:
        final ValueCoercionResult<List<List<Double>>> valueCoercionResult = ValueCoersion.coerceToListOfGivenType(value, List)

        then:
        assert valueCoercionResult.suitability == SUITABLE_BY_UP_CASTING
        assert valueCoercionResult.result == [[2.2d, 2.3d], [3.3d, 3.4d], [4.4d, 4.5d]]
    }

    private void assertArrayActualEqualsExpected(final Object[] actual, final Object[] expected) {
        assert actual != null
        assert expected != null
        assert actual.length == expected.length
        for(int i=0; i<expected.length; i++){
            Object expectedObj = expected[i]
            Object actualObj = actual[i]
            if(expectedObj.class.isArray()){
                assert actualObj.class.isArray(): "actualObj $actualObj is not of type array, is of type ${actualObj.class}"
                assertArrayActualEqualsExpected(actualObj, expectedObj)
            } else {
                assert actualObj == expectedObj
            }
        }
    }

    @Unroll
    def "CoerceToListofLists"() {
        given:
        final List value = [[2.2d, 2.3d], [3.3d, 3.4d], [4.4d, 4.5d]]

        when:
        final ValueCoercionResult<List> valueCoercionResult = ValueCoersion.coerceToListOfGivenType(value, List)

        then:
        assert valueCoercionResult.suitability == SUITABLE_BY_UP_CASTING
        assert valueCoercionResult.result == [[2.2d, 2.3d], [3.3d, 3.4d], [4.4d, 4.5d]]
    }

    @Unroll
    def "CoerceToList"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<List<Double>>(result, suitability)
        ValueCoercionResult actualResults = ValueCoersion.coerceToListOfGivenType(value, Double)

        then:
        assert actualResults == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults"

        where:
        value                        | result               | suitability
        [1,2,3] as Object[]          | [1.0d, 2.0d, 3.0d]   | SUITABLE_WITH_COERCION
        [1.1, 2.2, 3.3] as Object[]  | [1.1d, 2.2d, 3.3d]   | SUITABLE_WITH_EXPECTED_COERCION
        [2.2, 3.3, 4.4]              | [2.2d, 3.3d, 4.4d]   | SUITABLE_WITH_EXPECTED_COERCION
        [2.2d, 3.3d, 4.4d]           | [2.2d, 3.3d, 4.4d]   | SUITABLE
        2.2                          | null                 | NOT_SUITABLE
    }

    @Unroll
    def "CoerceToArray ofGivenType"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Object[]>(result, suitability)
        ValueCoercionResult actualResults = ValueCoersion.coerceToArrayOfGivenType(value, Double)

        then:
        assert actualResults == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults"

        where:
        value                        | result                          | suitability
        [1,2,3] as Object[]          | [1.0d, 2.0d, 3.0d] as Object[]  | SUITABLE_WITH_COERCION
        [1.1, 2.2, 3.3] as Object[]  | [1.1d, 2.2d, 3.3d] as Object[]  | SUITABLE_WITH_EXPECTED_COERCION
        [2.2, 3.3, 4.4]              | [2.2d, 3.3d, 4.4d] as Object[]  | SUITABLE_WITH_EXPECTED_COERCION
        2.2                          | null                            | NOT_SUITABLE
    }

    @Unroll
    def "CoerceToString"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<String>(result, suitability)
        ValueCoercionResult actualResults1 = ValueCoersion.coerceToString(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToType(value, String)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"

        where:
        value                   | result    | suitability
        "blah"                  | "blah"    | SUITABLE
        null                    | null      | SUITABLE
        "${'gstring'}"          | "gstring" | SUITABLE_WITH_EXPECTED_COERCION
        new StringBuilder("sb") | "sb"      | SUITABLE_WITH_COERCION
    }

    @Unroll
    def "CoerceToGString"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<GString>(result, suitability)
        ValueCoercionResult actualResults1 = ValueCoersion.coerceToGString(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToType(value, GString)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"

        where:
        value                   | result    | suitability
        "${'blah'}"             | "blah"    | SUITABLE
        null                    | null      | SUITABLE
        "string"                | "string"  | SUITABLE_WITH_EXPECTED_COERCION
        new StringBuilder("sb") | "sb"      | SUITABLE_WITH_COERCION
    }


    def "CoerceToBooleanWithNull"() {
        when:
        ValueCoercionResult actualResultsBoolean = ValueCoersion.coerceToBoolean(null)

        then:
        assert actualResultsBoolean.result == null
        assert actualResultsBoolean.suitability == SUITABLE
    }

    def "CoerceToBooleanPrimitiveWithNull"() {
        when:
        ValueCoercionResult actualResultsBoolean = ValueCoersion.coerceToBooleanPrimitive(null)

        then:
        assert actualResultsBoolean.suitability == NOT_SUITABLE
    }


    @Unroll
    def "CoerceToBooleanAndPrimitiveBoolean"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Boolean>(result, suitability)

        ValueCoercionResult actualResults1 = ValueCoersion.coerceToBoolean(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToBooleanPrimitive(value)
        ValueCoercionResult actualResults3 = ValueCoersion.coerceToType(value, Boolean)
        ValueCoercionResult actualResults4 = ValueCoersion.coerceToType(value, boolean.class)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"
        assert actualResults3 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults3"
        assert actualResults4 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults4"

        where:
        value       | result                | suitability
        true        | true      | SUITABLE
        false       | false     | SUITABLE
        "true"      | true      | SUITABLE_WITH_COERCION
        "false"     | false     | SUITABLE_WITH_COERCION
        "True"      | true      | SUITABLE_WITH_COERCION
        "False"     | false     | SUITABLE_WITH_COERCION
        "TRUE"      | true      | SUITABLE_WITH_COERCION
        "FALSE"     | false     | SUITABLE_WITH_COERCION
        "T"         | true      | SUITABLE_WITH_COERCION
        "F"         | false     | SUITABLE_WITH_COERCION
        "t"         | true      | SUITABLE_WITH_COERCION
        "f"         | false     | SUITABLE_WITH_COERCION
        "Yes"       | true      | SUITABLE_WITH_COERCION
        "No"        | false     | SUITABLE_WITH_COERCION
        "Y"         | true      | SUITABLE_WITH_COERCION
        "N"         | false     | SUITABLE_WITH_COERCION
        "y"         | true      | SUITABLE_WITH_COERCION
        "g"         | null      | NOT_SUITABLE
        "123"       | null      | NOT_SUITABLE
        "blah"      | null      | NOT_SUITABLE
        "true${''}" | true      | SUITABLE_WITH_COERCION
        "false${''}"| false     | SUITABLE_WITH_COERCION
        "True${''}" | true      | SUITABLE_WITH_COERCION
        "False${''}"| false     | SUITABLE_WITH_COERCION
        "TRUE${''}" | true      | SUITABLE_WITH_COERCION
        "FALSE${''}"| false     | SUITABLE_WITH_COERCION
        "T${''}"    | true      | SUITABLE_WITH_COERCION
        "F${''}"    | false     | SUITABLE_WITH_COERCION
        "t${''}"    | true      | SUITABLE_WITH_COERCION
        "f${''}"    | false     | SUITABLE_WITH_COERCION
        "Yes${''}"  | true      | SUITABLE_WITH_COERCION
        "No${''}"   | false     | SUITABLE_WITH_COERCION
        "Y${''}"    | true      | SUITABLE_WITH_COERCION
        "N${''}"    | false     | SUITABLE_WITH_COERCION
        "y${''}"    | true      | SUITABLE_WITH_COERCION
        "n${''}"    | false     | SUITABLE_WITH_COERCION
        "blah${''}" | null      | NOT_SUITABLE
        "g${''}"    | null      | NOT_SUITABLE
        "123${''}"  | null      | NOT_SUITABLE
        "0${''}"    | null      | NOT_SUITABLE
        "1${''}"    | null      | NOT_SUITABLE
        ((int) 1)   | null      | NOT_SUITABLE
        ((int) 0)   | null      | NOT_SUITABLE
        new StringBuilder() | null | NOT_SUITABLE
    }


    def "CoerceToIntegerWithNull"() {
        when:
        ValueCoercionResult actualResultsInteger = ValueCoersion.coerceToInteger(null)

        then:
        assert actualResultsInteger.result == null
        assert actualResultsInteger.suitability == SUITABLE
    }

    def "CoerceToIntPrimitiveWithNull"() {
        when:
        ValueCoercionResult actualResultsInteger = ValueCoersion.coerceToIntPrimitive(null)

        then:
        assert actualResultsInteger.suitability == NOT_SUITABLE
    }


    @Unroll
    def "CoerceToIntegerAndPrimitiveInt"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Integer>(result, suitability)

        ValueCoercionResult actualResults1 = ValueCoersion.coerceToInteger(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToIntPrimitive(value)
        ValueCoercionResult actualResults3 = ValueCoersion.coerceToType(value, Integer)
        ValueCoercionResult actualResults4 = ValueCoersion.coerceToType(value, int.class)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"
        assert actualResults3 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults3"
        assert actualResults4 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults4"

        where:
        value                           | result                | suitability
        ((int) 2)                       | 2                     | SUITABLE
        ((double) 2)                    | 2                     | SUITABLE_WITH_COERCION
        ((double) 0)                    | 0                     | SUITABLE_WITH_COERCION
        ((double) 2.1)                  | null                  | NOT_SUITABLE
        ((double) Integer.MAX_VALUE)    | Integer.MAX_VALUE     | SUITABLE_WITH_COERCION
        ((double) Integer.MIN_VALUE)    | Integer.MIN_VALUE     | SUITABLE_WITH_COERCION
        (((double) Integer.MAX_VALUE)+1)| null                  | NOT_SUITABLE
        (((double) Integer.MIN_VALUE)-1)| null                  | NOT_SUITABLE
        ((double) Double.POSITIVE_INFINITY)| null               | NOT_SUITABLE
        ((double) Double.NEGATIVE_INFINITY)| null               | NOT_SUITABLE
        ((float) 2)                     | 2                     | SUITABLE_WITH_COERCION
        ((float) 0)                     | 0                     | SUITABLE_WITH_COERCION
        ((float) 2.1)                   | null                  | NOT_SUITABLE
        ((float) Float.POSITIVE_INFINITY)| null                 | NOT_SUITABLE
        ((float) Float.NEGATIVE_INFINITY)| null                 | NOT_SUITABLE
        ((short) 2)                     | 2                     | SUITABLE_WITH_COERCION
        ((short) 0)                     | 0                     | SUITABLE_WITH_COERCION
        ((short) Short.MAX_VALUE)       | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        ((short) Short.MIN_VALUE)       | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        ((long) 2)                      | 2                     | SUITABLE_WITH_COERCION
        ((long) -2)                     | -2                    | SUITABLE_WITH_COERCION
        ((long) 0)                      | 0                     | SUITABLE_WITH_COERCION
        ((long) Integer.MAX_VALUE)      | Integer.MAX_VALUE     | SUITABLE_WITH_COERCION
        ((long) Integer.MIN_VALUE)      | Integer.MIN_VALUE     | SUITABLE_WITH_COERCION
        (((long) Integer.MAX_VALUE)+1)  | null                  | NOT_SUITABLE
        (((long) Integer.MIN_VALUE)-1)  | null                  | NOT_SUITABLE
        "23"                            | 23                    | SUITABLE_WITH_COERCION
        "23${''}"                       | 23                    | SUITABLE_WITH_COERCION
        bd(2)                           | 2                     | SUITABLE_WITH_COERCION
        BigDecimal.ZERO                 | 0                     | SUITABLE_WITH_COERCION
        bd(2.1)                         | null                  | NOT_SUITABLE
        bd(Integer.MAX_VALUE)           | Integer.MAX_VALUE     | SUITABLE_WITH_COERCION
        bd(Integer.MIN_VALUE)           | Integer.MIN_VALUE     | SUITABLE_WITH_COERCION
        true                            | null                  | NOT_SUITABLE
        new StringBuilder()             | null                  | NOT_SUITABLE
    }


    def "CoerceToLongWithNull"() {
        when:
        ValueCoercionResult actualResults = ValueCoersion.coerceToInteger(null)

        then:
        assert actualResults.result == null
        assert actualResults.suitability == SUITABLE
    }

    def "CoerceToLongPrimitiveWithNull"() {
        when:
        ValueCoercionResult actualResultsLong = ValueCoersion.coerceToLongPrimitive(null)

        then:
        assert actualResultsLong.suitability == NOT_SUITABLE
    }


    @Unroll
    def "CoerceToLongAndPrimitiveLong"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Long>(result, suitability)

        ValueCoercionResult actualResults1 = ValueCoersion.coerceToLong(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToLongPrimitive(value)
        ValueCoercionResult actualResults3 = ValueCoersion.coerceToType(value, Long)
        ValueCoercionResult actualResults4 = ValueCoersion.coerceToType(value, long.class)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"
        assert actualResults3 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults3"
        assert actualResults4 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults4"

        where:
        value                           | result                | suitability
        ((long) 2)                      | 2                     | SUITABLE
        ((long) -2)                     | -2                    | SUITABLE
        ((long) 0)                      | 0                     | SUITABLE
        ((long) Long.MAX_VALUE)         | Long.MAX_VALUE        | SUITABLE
        ((long) Long.MIN_VALUE)         | Long.MIN_VALUE        | SUITABLE
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((int) -2)                      | -2                    | SUITABLE_WITH_COERCION
        ((double) 2)                    | 2                     | SUITABLE_WITH_COERCION
        ((double) 0)                    | 0                     | SUITABLE_WITH_COERCION
        ((double) 2.1)                  | null                  | NOT_SUITABLE
        ((double) Double.POSITIVE_INFINITY)| null               | NOT_SUITABLE
        ((double) Double.NEGATIVE_INFINITY)| null               | NOT_SUITABLE
        ((float) 2)                     | 2                     | SUITABLE_WITH_COERCION
        ((float) 0)                     | 0                     | SUITABLE_WITH_COERCION
        ((float) 2.1)                   | null                  | NOT_SUITABLE
        ((float) Float.POSITIVE_INFINITY)| null                 | NOT_SUITABLE
        ((float) Float.NEGATIVE_INFINITY)| null                 | NOT_SUITABLE
        ((short) 2)                     | 2                     | SUITABLE_WITH_COERCION
        ((short) 0)                     | 0                     | SUITABLE_WITH_COERCION
        ((short) Short.MAX_VALUE)       | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        ((short) Short.MIN_VALUE)       | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        "23"                            | 23                    | SUITABLE_WITH_COERCION
        "23${''}"                       | 23                    | SUITABLE_WITH_COERCION
        bd(2)                           | 2                     | SUITABLE_WITH_COERCION
        BigDecimal.ZERO                 | 0                     | SUITABLE_WITH_COERCION
        bd(2.1)                         | null                  | NOT_SUITABLE
        bd(Long.MAX_VALUE)              | Long.MAX_VALUE        | SUITABLE_WITH_COERCION
        bd(Long.MIN_VALUE)              | Long.MIN_VALUE        | SUITABLE_WITH_COERCION
        true                            | null                  | NOT_SUITABLE
        new StringBuilder()             | null                  | NOT_SUITABLE
    }


    def "CoerceToShortWithNull"() {
        when:
        ValueCoercionResult actualResults = ValueCoersion.coerceToInteger(null)

        then:
        assert actualResults.result == null
        assert actualResults.suitability == SUITABLE
    }

    def "CoerceToShortPrimitiveWithNull"() {
        when:
        ValueCoercionResult actualResultsShort = ValueCoersion.coerceToIntPrimitive(null)

        then:
        assert actualResultsShort.suitability == NOT_SUITABLE
    }


    @Unroll
    def "CoerceToShortAndPrimitiveShort"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Short>(result, suitability)

        ValueCoercionResult actualResults1 = ValueCoersion.coerceToShort(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToShortPrimitive(value)
        ValueCoercionResult actualResults3 = ValueCoersion.coerceToType(value, Short)
        ValueCoercionResult actualResults4 = ValueCoersion.coerceToType(value, short.class)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"
        assert actualResults3 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults3"
        assert actualResults4 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults4"

        where:
        value                           | result                | suitability
        ((short) 2)                     | 2                     | SUITABLE
        ((short) 0)                     | 0                     | SUITABLE
        ((short) Short.MAX_VALUE)       | Short.MAX_VALUE       | SUITABLE
        ((short) Short.MIN_VALUE)       | Short.MIN_VALUE       | SUITABLE
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((double) 2)                    | 2                     | SUITABLE_WITH_COERCION
        ((double) 0)                    | 0                     | SUITABLE_WITH_COERCION
        ((double) 2.1)                  | null                  | NOT_SUITABLE
        ((double) Short.MAX_VALUE)      | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        ((double) Short.MIN_VALUE)      | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        (((double) Short.MAX_VALUE)+1)  | null                  | NOT_SUITABLE
        (((double) Short.MIN_VALUE)-1)  | null                  | NOT_SUITABLE
        ((double) Double.POSITIVE_INFINITY)| null               | NOT_SUITABLE
        ((double) Double.POSITIVE_INFINITY)| null               | NOT_SUITABLE
        ((float) 2)                     | 2                     | SUITABLE_WITH_COERCION
        ((float) 0)                     | 0                     | SUITABLE_WITH_COERCION
        ((float) 2.1)                   | null                  | NOT_SUITABLE
        ((float) Short.MAX_VALUE)       | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        ((float) Short.MIN_VALUE)       | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        (((float) Short.MAX_VALUE)+1)   | null                  | NOT_SUITABLE
        (((float) Short.MIN_VALUE)-1)   | null                  | NOT_SUITABLE
        ((float) Float.POSITIVE_INFINITY)| null                 | NOT_SUITABLE
        ((float) Float.POSITIVE_INFINITY)| null                 | NOT_SUITABLE
        ((long) 2)                      | 2                     | SUITABLE_WITH_COERCION
        ((long) -2)                     | -2                    | SUITABLE_WITH_COERCION
        ((long) 0)                      | 0                     | SUITABLE_WITH_COERCION
        ((long) Short.MAX_VALUE)        | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        ((long) Short.MIN_VALUE)        | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        (((long) Short.MAX_VALUE)+1)    | null                  | NOT_SUITABLE
        (((long) Short.MIN_VALUE)-1)    | null                  | NOT_SUITABLE
        "23"                            | 23                    | SUITABLE_WITH_COERCION
        "23${''}"                       | 23                    | SUITABLE_WITH_COERCION
        bd(2)                           | 2                     | SUITABLE_WITH_COERCION
        BigDecimal.ZERO                 | 0                     | SUITABLE_WITH_COERCION
        bd(2.1)                         | null                  | NOT_SUITABLE
        bd(Short.MAX_VALUE)             | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        bd(Short.MIN_VALUE)             | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        true                            | null                  | NOT_SUITABLE
        new StringBuilder()             | null                  | NOT_SUITABLE
    }

    def "CoerceToDoubleWithNull"() {
        when:
        ValueCoercionResult actualResults = ValueCoersion.coerceToDouble(null)

        then:
        assert actualResults.result == null
        assert actualResults.suitability == SUITABLE
    }

    def "CoerceToDoublePrimitiveWithNull"() {
        when:
        ValueCoercionResult actualResults = ValueCoersion.coerceToDoublePrimitive(null)

        then:
        assert actualResults.suitability == NOT_SUITABLE
    }

    @Unroll
    def "CoerceToDoubleAndPrimitiveDouble"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Double>(result, suitability)

        ValueCoercionResult actualResults1 = ValueCoersion.coerceToDouble(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToDoublePrimitive(value)
        ValueCoercionResult actualResults3 = ValueCoersion.coerceToType(value, Double)
        ValueCoercionResult actualResults4 = ValueCoersion.coerceToType(value, double.class)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"
        assert actualResults3 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults3"
        assert actualResults4 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults4"

        where:
        value                           | result                | suitability
        ((double) 2)                    | 2                     | SUITABLE
        ((double) 0)                    | 0                     | SUITABLE
        ((double) 2.1)                  | 2.1                   | SUITABLE
        ((double) -2.1)                 | -2.1                  | SUITABLE
        ((double) Double.MAX_VALUE)     | Double.MAX_VALUE      | SUITABLE
        ((double) Double.MIN_VALUE)     | Double.MIN_VALUE      | SUITABLE
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((short) 2)                     | 2                     | SUITABLE_WITH_COERCION
        ((short) 0)                     | 0                     | SUITABLE_WITH_COERCION
        ((short) Short.MAX_VALUE)       | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        ((short) Short.MIN_VALUE)       | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        ((long) 2)                      | 2                     | SUITABLE_WITH_COERCION
        ((long) -2)                     | -2                    | SUITABLE_WITH_COERCION
        ((long) 0)                      | 0                     | SUITABLE_WITH_COERCION
        Long.MAX_VALUE                  | (double) Long.MAX_VALUE | SUITABLE_WITH_COERCION
        Long.MIN_VALUE                  | (double) Long.MIN_VALUE | SUITABLE_WITH_COERCION
        "23"                            | 23                    | SUITABLE_WITH_COERCION
        "23${''}"                       | 23                    | SUITABLE_WITH_COERCION
        bd(2)                           | 2                     | SUITABLE_WITH_EXPECTED_COERCION
        BigDecimal.ZERO                 | 0                     | SUITABLE_WITH_EXPECTED_COERCION
        BigDecimal.valueOf(1.23456)     | 1.23456               | SUITABLE_WITH_EXPECTED_COERCION
        BigDecimal.ZERO                 | 0                     | SUITABLE_WITH_EXPECTED_COERCION
        bd(2.1)                         | 2.1                   | SUITABLE_WITH_EXPECTED_COERCION
        2.1                             | 2.1                   | SUITABLE_WITH_EXPECTED_COERCION
        bd(Double.MAX_VALUE)            | Double.MAX_VALUE      | SUITABLE_WITH_EXPECTED_COERCION
        bd(Double.MIN_VALUE)            | Double.MIN_VALUE      | SUITABLE_WITH_EXPECTED_COERCION
        true                            | null                  | NOT_SUITABLE
        new StringBuilder()             | null                  | NOT_SUITABLE
    }

    def "CoerceToFloatWithNull"() {
        when:
        ValueCoercionResult actualResults = ValueCoersion.coerceToFloat(null)

        then:
        assert actualResults.result == null
        assert actualResults.suitability == SUITABLE
    }

    def "CoerceToFloatPrimitiveWithNull"() {
        when:
        ValueCoercionResult actualResults = ValueCoersion.coerceToFloatPrimitive(null)

        then:
        assert actualResults.suitability == NOT_SUITABLE
    }

    @Unroll
    def "CoerceToFloatAndPrimitiveFloat"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<Float>(result, suitability)

        ValueCoercionResult actualResults1 = ValueCoersion.coerceToFloat(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToFloatPrimitive(value)
        ValueCoercionResult actualResults3 = ValueCoersion.coerceToType(value, Float)
        ValueCoercionResult actualResults4 = ValueCoersion.coerceToType(value, float.class)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"
        assert actualResults3 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults3"
        assert actualResults4 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults4"

        where:
        value                           | result                | suitability
        ((float) 2)                     | 2                     | SUITABLE
        ((float) 0)                     | 0                     | SUITABLE
        ((float) 2.1)                   | 2.1f                  | SUITABLE
        ((float) -2.1)                  | -2.1f                 | SUITABLE
        ((float) Float.MAX_VALUE)       | Float.MAX_VALUE       | SUITABLE
        ((float) Float.MIN_VALUE)       | Float.MIN_VALUE       | SUITABLE
        ((double) 2)                    | 2                     | SUITABLE_WITH_COERCION
        ((double) 0)                    | 0                     | SUITABLE_WITH_COERCION
        ((double) 2.1)                  | null                  | NOT_SUITABLE
        ((double) -2.1)                 | null                  | NOT_SUITABLE
        ((double) Double.MAX_VALUE)     | null                  | NOT_SUITABLE
        ((double) Double.MIN_VALUE)     | null                  | NOT_SUITABLE
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((int) 2)                       | 2                     | SUITABLE_WITH_COERCION
        ((short) 2)                     | 2                     | SUITABLE_WITH_COERCION
        ((short) 0)                     | 0                     | SUITABLE_WITH_COERCION
        ((short) Short.MAX_VALUE)       | Short.MAX_VALUE       | SUITABLE_WITH_COERCION
        ((short) Short.MIN_VALUE)       | Short.MIN_VALUE       | SUITABLE_WITH_COERCION
        ((long) 2)                      | 2                     | SUITABLE_WITH_COERCION
        ((long) -2)                     | -2                    | SUITABLE_WITH_COERCION
        ((long) 0)                      | 0                     | SUITABLE_WITH_COERCION
        Long.MAX_VALUE                  | (double) Long.MAX_VALUE | SUITABLE_WITH_COERCION
        Long.MIN_VALUE                  | (double) Long.MIN_VALUE | SUITABLE_WITH_COERCION
        "23"                            | 23                    | SUITABLE_WITH_COERCION
        "23${''}"                       | 23                    | SUITABLE_WITH_COERCION
        bd(2)                           | 2                     | SUITABLE_WITH_COERCION
        BigDecimal.ZERO                 | 0                     | SUITABLE_WITH_COERCION
        BigDecimal.valueOf(1.23456)     | 1.23456f              | SUITABLE_WITH_COERCION
        bd(2.1)                         | 2.1f                  | SUITABLE_WITH_COERCION
        2.1                             | 2.1f                  | SUITABLE_WITH_COERCION
        bd(Float.MAX_VALUE)             | Float.MAX_VALUE       | SUITABLE_WITH_COERCION
        bd(Float.MIN_VALUE)             | Float.MIN_VALUE       | SUITABLE_WITH_COERCION
        true                            | null                  | NOT_SUITABLE
        new StringBuilder()             | null                  | NOT_SUITABLE
    }


    @Unroll
    def "CoerceToBigDecimal"() {
        when:
        ValueCoercionResult expectedResults = new ValueCoercionResult<BigDecimal>(result, suitability)

        ValueCoercionResult actualResults1 = ValueCoersion.coerceToBigDecimal(value)
        ValueCoercionResult actualResults2 = ValueCoersion.coerceToType(value, BigDecimal)

        then:
        assert actualResults1 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults1"
        assert actualResults2 == expectedResults: "inputValue:$value, inputType:${value.getClass().simpleName}, expectedResult:$result, expectedSuitability:$suitability, actual:$actualResults2"


        where:
        value                           | result                | suitability
        BigDecimal.ZERO                 | 0                     | SUITABLE
        BigDecimal.valueOf(1.23456)     | 1.23456               | SUITABLE
        2.1                             | bd(2.1)               | SUITABLE
        null                            | null                  | SUITABLE
        bd(Double.MAX_VALUE)            | bd(Double.MAX_VALUE)  | SUITABLE
        bd(Double.MIN_VALUE)            | bd(Double.MIN_VALUE)  | SUITABLE
        ((double) 2)                    | bd(2)                 | SUITABLE_WITH_COERCION
        ((double) 0)                    | bd(0)                 | SUITABLE_WITH_COERCION
        ((double) 2.1)                  | bd(2.1)               | SUITABLE_WITH_COERCION
        ((double) -2.1)                 | bd(-2.1)              | SUITABLE_WITH_COERCION
        ((double) Double.MAX_VALUE)     | bd(Double.MAX_VALUE)  | SUITABLE_WITH_COERCION
        ((double) Double.MIN_VALUE)     | bd(Double.MIN_VALUE)  | SUITABLE_WITH_COERCION
        ((int) 2)                       | bd(2)                 | SUITABLE_WITH_COERCION
        ((int) 2)                       | bd(2)                 | SUITABLE_WITH_COERCION
        ((int) 2)                       | bd(2)                 | SUITABLE_WITH_COERCION
        ((short) 2)                     | bd(2)                 | SUITABLE_WITH_COERCION
        ((short) 0)                     | bd(0)                 | SUITABLE_WITH_COERCION
        ((short) Short.MAX_VALUE)       | bd(Short.MAX_VALUE)   | SUITABLE_WITH_COERCION
        ((short) Short.MIN_VALUE)       | bd(Short.MIN_VALUE)   | SUITABLE_WITH_COERCION
        ((long) 2)                      | bd(2)                 | SUITABLE_WITH_COERCION
        ((long) -2)                     | bd(-2)                | SUITABLE_WITH_COERCION
        ((long) 0)                      | bd(0)                 | SUITABLE_WITH_COERCION
        Long.MAX_VALUE                  | bd(Long.MAX_VALUE)    | SUITABLE_WITH_COERCION
        Long.MIN_VALUE                  | bd(Long.MIN_VALUE)    | SUITABLE_WITH_COERCION
        "23"                            | bd(23)                | SUITABLE_WITH_COERCION
        "1.2345"                        | bd(1.2345)            | SUITABLE_WITH_COERCION
        "abc"                           | null                  | NOT_SUITABLE
        "23${''}"                       | bd(23)                | SUITABLE_WITH_COERCION
        "1.2345${''}"                   | bd(1.2345)            | SUITABLE_WITH_COERCION
        "abc${''}"                      | null                  | NOT_SUITABLE
        true                            | null                  | NOT_SUITABLE
        new StringBuilder()             | null                  | NOT_SUITABLE
    }

    private static BigDecimal bd(final long num){
        return BigDecimal.valueOf(num)
    }

    private static BigDecimal bd(final double num){
        return BigDecimal.valueOf(num)
    }

    private static BigDecimal bd(final int num){
        return BigDecimal.valueOf(num)
    }
}
