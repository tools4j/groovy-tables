package org.tools4j.groovytables

import groovy.transform.TypeChecked

import java.util.function.Predicate

/**
 * User: ben
 * Date: 5/04/2016
 * Time: 5:33 PM
 *
 * This class provides the public interface into the API
 */
@TypeChecked
class GroovyTables {
    static <T> CoercionTableParser.Builder<T> createListOf(final Class<T> clazz) {
        return CoercionTableParser.createListOf(clazz);
    }

    static List<Object[]> createListOfArrays(final Closure tableContent) {
        return SimpleTableParser.createListOfArrays(tableContent)
    }

    static List<Row> createListOfRowObjects(Closure tableContent) {
        return SimpleTableParser.createListOfRows(tableContent)
    }

    static <T> List<T> createFromTable(final Class<T> clazz, final Closure tableContent) {
        return CoercionTableParser.createFromTable(clazz, ConstructionMethodFilter.INCLUDE_ALL, tableContent)
    }

    static <T> List<T> createFromTable(final Class<T> clazz, final Predicate<ConstructionMethod> constructionMethodFilter, final Closure tableContent) {
        return CoercionTableParser.createFromTable(clazz, constructionMethodFilter, tableContent)
    }
}