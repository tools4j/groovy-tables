package org.tools4j.groovytables

import groovy.transform.ToString

import java.lang.reflect.Constructor
import java.lang.reflect.Executable

/**
 * User: ben
 * Date: 19/02/2016
 * Time: 5:26 PM
 */

@ToString
class ClassConstructorConstructionCall<T> implements ExecutableConstructionCall<T>{
    final Constructor<T> constructor;

    ClassConstructorConstructionCall(final Constructor<T> constructor) {
        this.constructor = constructor
    }

    @Override
    T construct(final Object[] args) {
        constructor.setAccessible(true)
        try {
            return constructor.newInstance(args)
        } catch( IllegalArgumentException e ){
            Logger.error("Error whilst constructing object.  This is an error within the DSL.  " +
                    "Arguments should have been coerced into acceptable types before this method was called.  " +
                    "Please notify developers.  " +
                    "Args:" + Arrays.asList(args) +
                    " Constructor:" + constructor +
                    " caught exception: " + e)
            e.printStackTrace(System.err)
            throw e
        }
    }

    @Override
    Executable getExecutable() {
        return constructor
    }
}
