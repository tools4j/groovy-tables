package org.tools4j.groovytables

import groovy.transform.TypeChecked

import java.util.function.Predicate

import static org.tools4j.groovytables.SimpleTableParser.Var

/**
 * User: ben
 * Date: 10/02/2016
 * Time: 5:46 PM
 */
@TypeChecked
class CoercionTableParser {
    protected static <T> List<T> createFromTable(final Class<T> clazz, final Closure tableContent) {
        return createFromTable(clazz, ConstructionMethodFilter.INCLUDE_ALL, tableContent)
    }

    protected static <T> List<T> createFromTable(final Class<T> clazz, final Predicate<Callable> constructionMethodFilter, final Closure tableContent) {
        final long startTime = System.currentTimeMillis()
        final List<Row> rows = SimpleTableParser.createListOfRows(tableContent);
        final List<T> rowsAsObjects = new ArrayList<T>(rows.size())

        if(rows.size() == 0){
            return rowsAsObjects
        }

        boolean firstRowContainsColumnHeadings = rows.get(0).asArray().every{it instanceof Var}
        List<String> columnHeadings;
        if(firstRowContainsColumnHeadings){
            columnHeadings = rows.get(0).asArray().collect{ Var var -> var.name}
            rows.remove(0)
        } else {
            columnHeadings = [];
        }

        for(final Row row: rows){
            final Object[] args = row.asArray()
            final TypeCoercionResult<T> coercionResult = TypeCoercer.coerceToType(clazz, constructionMethodFilter, columnHeadings, args)
            if(coercionResult.suitability == Suitability.NOT_SUITABLE){
                throw new IllegalArgumentException("Could not coerce to type [$clazz.simpleName] row with args $args");
            }
            T rowAsObject = coercionResult.result
            rowsAsObjects.add(rowAsObject)
        }
        final long endTime = System.currentTimeMillis()
        final long duration = endTime - startTime
        Logger.info("createFromTable took: $duration ms")
        return rowsAsObjects;
    }

    protected static class Builder<T>{
        final Class<T> clazz
        Predicate<Callable> constructionMethodFilter = null

        Builder(final Class<T> clazz) {
            this.clazz = clazz
        }

        Builder withFilter(final Predicate<Callable> constructionMethodFilter){
            if(this.constructionMethodFilter == null){
                this.constructionMethodFilter = constructionMethodFilter
            } else {
                throw new IllegalArgumentException("Construction method filter has already been set.  It is not possible to set this twice.  Existing filter: ${this.constructionMethodFilter} New filter trying to set: ${constructionMethodFilter}")
            }
            return this;
        }

        Builder usingJustConstructors(){
            return withFilter(ConstructionMethodFilter.CONSTRUCTORS)
        }

        Builder usingJustStaticFactoryMethods(){
            return withFilter(ConstructionMethodFilter.STATIC_FACTORY_METHODS)
        }

        Builder usingJustReflection(){
            return withFilter(ConstructionMethodFilter.REFLECTION)
        }

        List<T> fromTable(final Closure tableContent){
            return createFromTable(clazz, tableContent)
        }
    }

    protected static <T> Builder<T> createListOf(final Class<T> clazz) {
        return new Builder(clazz)
    }
}
