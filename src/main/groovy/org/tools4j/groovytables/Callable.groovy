package org.tools4j.groovytables
/**
 * User: ben
 * Date: 16/02/2016
 * Time: 6:51 AM
 */
interface Callable<T> {
    CallPrecursor<T> getCallPrecursor(List<String> columnHeadings, Object ... rawArgs);
    CallPrecursor<T> getCallPrecursor(Object ... rawArgs);
}