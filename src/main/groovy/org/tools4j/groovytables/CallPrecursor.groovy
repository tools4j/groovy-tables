package org.tools4j.groovytables

/**
 * User: ben
 * Date: 16/02/2016
 * Time: 5:23 PM
 */
trait CallPrecursor<T> {
    abstract Suitability getSuitability()
    abstract T executeMethod();

    boolean isMoreSuitableThan(final CallPrecursor other) {
        return suitability.isMoreSuitableThan(other.suitability)
    }
}
