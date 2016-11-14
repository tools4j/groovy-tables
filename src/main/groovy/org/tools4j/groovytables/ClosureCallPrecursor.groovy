package org.tools4j.groovytables

import groovy.transform.ToString

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 5:45 PM
 */
@ToString
class ClosureCallPrecursor<T> implements CallPrecursor<T>{
    final Suitability suitability;
    final Object[] rawArgs;
    final Object[] coercedArgs;
    final ClosureMethod.ClosureCall<T> closureCall;

    final static NOT_SUITABLE = new ClosureCallPrecursor(Suitability.NOT_SUITABLE, null, null, null);

    ClosureCallPrecursor(final Suitability suitability, final Object[] rawArgs, final Object[] coercedArgs, final ClosureMethod.ClosureCall<T> closureCall) {
        this.suitability = suitability
        this.rawArgs = rawArgs
        this.coercedArgs = coercedArgs
        this.closureCall = closureCall
    }

    T executeMethod() {
        return closureCall.call(coercedArgs)
    }
}
