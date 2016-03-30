package org.tools4j.groovytables
/**
 * User: ben
 * Date: 16/02/2016
 * Time: 6:51 AM
 */
trait ConstructionMethod<T> {
    abstract ConstructionMethodPrecursor<T> getConstructionMethodPrecursor(List<String> columnHeadings, Object ... rawArgs);
    abstract ConstructionMethodPrecursor<T> getConstructionMethodPrecursor(Object ... rawArgs);
}