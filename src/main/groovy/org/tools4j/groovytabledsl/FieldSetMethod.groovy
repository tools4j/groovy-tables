package org.tools4j.groovytabledsl;

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 6:35 AM
 */
trait FieldSetMethod<T> {
    abstract void execute(T object, Object coercedArg)
}
