package org.tools4j.groovytables

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * User: ben
 * Date: 15/02/2016
 * Time: 5:38 PM
 */
@ToString
@EqualsAndHashCode
public class TypeCoercionResult<T>{
    final Suitability suitability;
    final ConstructionMethod constructionMethod
    final T result;

    public static TypeCoercionResult NOT_SUITABLE = new TypeCoercionResult<Void>(null, Suitability.NOT_SUITABLE, null);

    TypeCoercionResult(final T result, final Suitability suitability, final ConstructionMethod constructionMethod) {
        this.result = result
        this.suitability = suitability
        this.constructionMethod = constructionMethod
    }
}