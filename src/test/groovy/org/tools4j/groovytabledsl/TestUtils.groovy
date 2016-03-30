package org.tools4j.groovytabledsl

/**
 * User: ben
 * Date: 9/02/2016
 * Time: 5:30 PM
 */
class TestUtils {
    public static void assertActualEqualsExpected(final Object[] actual, final Object[] expected) {
        if( actual == null && expected == null ) return;
        else if(actual == null) assert false: "Actual array is null whereas expected is not"
        else if(expected == null) assert false: "Expected array is null whereas actual is not"
        Arrays.equals(actual, expected)
    }
}
