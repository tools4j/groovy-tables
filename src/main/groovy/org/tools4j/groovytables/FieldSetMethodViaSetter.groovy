package org.tools4j.groovytables

import java.lang.reflect.Method

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 6:36 AM
 */
class FieldSetMethodViaSetter<T> implements FieldSetMethod<T> {
    final Method method

    FieldSetMethodViaSetter(final Method method) {
        this.method = method
    }

    @Override
    void execute(final T object, Object coercedArg) {
        Object[] argAsSingleElementArrayToAllowNullArguments = [coercedArg]
        method.invoke(object, argAsSingleElementArrayToAllowNullArguments)
    }

    @Override
    public String toString() {
        return "Setter{${method.name}(${method.getParameterTypes().collect{it.simpleName}.toString().replaceAll("[\\[\\]]","")})}";
    }
}
