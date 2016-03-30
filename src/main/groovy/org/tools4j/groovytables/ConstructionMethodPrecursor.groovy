package org.tools4j.groovytables

/**
 * User: ben
 * Date: 16/02/2016
 * Time: 5:23 PM
 */
trait ConstructionMethodPrecursor<T> {
    abstract TypeCoercionResult<T> executeConstructionMethod();
    abstract Suitability getSuitability()

    boolean isMoreSuitableThan(final ConstructionMethodPrecursor other) {
        return suitability.isMoreSuitableThan(other.suitability)
    }
}
