package org.tools4j.groovytabledsl

import groovy.transform.ToString

import java.awt.print.Book

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 5:45 PM
 */
@ToString
class ReflectionConstructionMethodPrecursor<T> implements ConstructionMethodPrecursor<T>{
    final Suitability suitability;
    final Object[] rawArgs;
    final ReflectionConstructionMethod<T> constructionMethod
    final List<FieldSetPrecursor<T>> fieldSetPrecursors

    final static NOT_SUITABLE = new ReflectionConstructionMethodPrecursor(Suitability.NOT_SUITABLE, null, null, null);

    ReflectionConstructionMethodPrecursor(final Suitability suitability, final Object[] rawArgs, final ReflectionConstructionMethod<T> constructionMethod, List<FieldSetPrecursor> fieldSetPrecursors) {
        this.suitability = suitability
        this.rawArgs = rawArgs
        this.constructionMethod = constructionMethod
        this.fieldSetPrecursors = fieldSetPrecursors
    }

    TypeCoercionResult<T> executeConstructionMethod() {
        return new TypeCoercionResult<T>(constructionMethod.construct(fieldSetPrecursors), suitability, constructionMethod);
    }
}
