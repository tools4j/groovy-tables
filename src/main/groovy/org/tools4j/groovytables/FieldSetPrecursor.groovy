package org.tools4j.groovytables

import groovy.transform.ToString;

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 6:35 AM
 */
@ToString
class FieldSetPrecursor<T> {
    final Suitability suitability
    final Object coercedArg
    final FieldSetMethod<T> fieldSetMethod
    final static FieldSetPrecursor NOT_SUITABLE = new FieldSetPrecursor(Suitability.NOT_SUITABLE, null, null)

    FieldSetPrecursor(final Suitability suitability, final Object coercedArg, FieldSetMethod<T> fieldSetMethod) {
        this.suitability = suitability
        this.coercedArg = coercedArg
        this.fieldSetMethod = fieldSetMethod
    }

    boolean isMoreSuitableThan(FieldSetPrecursor<T> fieldSetMethod){
        return getSuitability().isMoreSuitableThan(fieldSetMethod.suitability)
    }
}
