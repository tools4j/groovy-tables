package org.tools4j.groovytabledsl

import java.lang.reflect.Field

/**
 * User: ben
 * Date: 14/03/2016
 * Time: 6:36 AM
 */
class FieldSetMethodViaDirectFieldAccess<T> implements FieldSetMethod<T> {
    final Field field

    FieldSetMethodViaDirectFieldAccess(final Field field) {
        this.field = field
    }

    @Override
    void execute(final T object, final Object coercedArg) {
        field.set(object, coercedArg)
    }


    @Override
    public String toString() {
        return "DirectFieldAccess{$field.name}";
    }
}
