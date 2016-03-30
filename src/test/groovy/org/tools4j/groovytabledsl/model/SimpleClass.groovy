package org.tools4j.groovytabledsl.model

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * User: ben
 * Date: 9/02/2016
 * Time: 5:29 PM
 */
@ToString
@EqualsAndHashCode
public class SimpleClass {
    final public String value;

    SimpleClass(final String value) {
        this.value = value
    }
}
