package org.tools4j.groovytables

import groovy.transform.EqualsAndHashCode

/**
 * User: ben
 * Date: 15/02/2016
 * Time: 5:38 PM
 */
@EqualsAndHashCode
public class ValueCoercionResult<T>{
    final Suitability suitability;
    final T result;

    public static ValueCoercionResult NOT_SUITABLE = new ValueCoercionResult<Void>(null, Suitability.NOT_SUITABLE);

    ValueCoercionResult(final T result, final Suitability suitability) {
        this.result = result
        this.suitability = suitability
    }


    @Override
    public String toString() {
        final String resultType = result != null ? "[${result.class.simpleName}]": ""
        return "ValueCoercionResult{" +
                "suitability=" + suitability +
                ", result${resultType}=" + result +
                '}';
    }
}