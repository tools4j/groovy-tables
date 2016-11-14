package org.tools4j.groovytables

import groovy.transform.ToString

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 5:45 PM
 */
@ToString
class ReflectionCallPrecursor<T> implements CallPrecursor<TypeCoercionResult<T>>{
    final Suitability suitability;
    final Object[] rawArgs;
    final ReflectionConstructionCall<T> constructionMethod
    final List<FieldSetPrecursor<T>> fieldSetPrecursors

    final static NOT_SUITABLE = new ReflectionCallPrecursor(Suitability.NOT_SUITABLE, null, null, null);

    ReflectionCallPrecursor(final Suitability suitability, final Object[] rawArgs, final ReflectionConstructionCall<T> constructionMethod, List<FieldSetPrecursor> fieldSetPrecursors) {
        this.suitability = suitability
        this.rawArgs = rawArgs
        this.constructionMethod = constructionMethod
        this.fieldSetPrecursors = fieldSetPrecursors
    }

    TypeCoercionResult<T> executeMethod() {
        return new TypeCoercionResult<T>(constructionMethod.construct(fieldSetPrecursors), suitability, constructionMethod);
    }
}
