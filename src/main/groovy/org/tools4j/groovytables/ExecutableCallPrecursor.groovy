package org.tools4j.groovytables

import groovy.transform.ToString

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 5:45 PM
 */
@ToString
class ExecutableCallPrecursor<T> implements CallPrecursor<TypeCoercionResult<T>>{
    final Suitability suitability;
    final Object[] rawArgs;
    final Object[] coercedArgs;
    final ExecutableConstructionCall<T> constructionCall;

    final static NOT_SUITABLE = new ExecutableCallPrecursor(Suitability.NOT_SUITABLE, null, null, null);

    ExecutableCallPrecursor(final Suitability suitability, final Object[] rawArgs, final Object[] coercedArgs, final ExecutableConstructionCall<T> constructionCall) {
        this.suitability = suitability
        this.rawArgs = rawArgs
        this.coercedArgs = coercedArgs
        this.constructionCall = constructionCall
    }

    TypeCoercionResult<T> executeMethod() {
        return new TypeCoercionResult<T>(constructionCall.construct(coercedArgs), suitability, constructionCall);
    }
}
