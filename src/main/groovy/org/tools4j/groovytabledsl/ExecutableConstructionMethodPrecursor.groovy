package org.tools4j.groovytabledsl

import groovy.transform.ToString

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 5:45 PM
 */
@ToString
class ExecutableConstructionMethodPrecursor<T> implements ConstructionMethodPrecursor<T>{
    final Suitability suitability;
    final Object[] rawArgs;
    final Object[] coercedArgs;
    final ExecutableConstructionMethod<T> constructionMethod;

    final static NOT_SUITABLE = new ExecutableConstructionMethodPrecursor(Suitability.NOT_SUITABLE, null, null, null);

    ExecutableConstructionMethodPrecursor(final Suitability suitability, final Object[] rawArgs, final Object[] coercedArgs, final ExecutableConstructionMethod<T> constructionMethod) {
        this.suitability = suitability
        this.rawArgs = rawArgs
        this.coercedArgs = coercedArgs
        this.constructionMethod = constructionMethod
    }

    TypeCoercionResult<T> executeConstructionMethod() {
        return new TypeCoercionResult<T>(constructionMethod.construct(coercedArgs), suitability, constructionMethod);
    }
}
