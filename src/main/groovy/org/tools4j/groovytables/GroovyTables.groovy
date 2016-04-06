package org.tools4j.groovytables
/**
 * User: ben
 * Date: 5/04/2016
 * Time: 5:33 PM
 */
class GroovyTables {
    static <T> CreateFromTable.Builder<T> createListOf(final Class<T> clazz) {
        return CreateFromTable.createListOf(clazz);
    }

    static List<Object[]> createListOfArrays(final Closure tableContent) {
        return SimpleTableParser.createListOfArrays(tableContent)
    }

    static List<Row> createListOfRowObjects(Closure tableContent) {
        return SimpleTableParser.createListOfRows(tableContent)
    }
}
