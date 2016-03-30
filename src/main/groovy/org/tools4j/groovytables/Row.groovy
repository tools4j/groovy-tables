package org.tools4j.groovytables

import groovy.transform.ToString

/**
 * User: ben
 * Date: 4/02/2016
 * Time: 5:43 PM
 */
@ToString
class Row {
    List<Object> values = []

    public Row or(Object arg) {
        return doOr(arg)
    }

    public Row or(Object[] arg) {
        return doOr(arg)
    }

    public Row or(Double arg) {
        return doOr(arg)
    }

    public Row or(Long arg) {
        return doOr(arg)
    }

    public Row or(Integer arg) {
        return doOr(arg)
    }


    public Row or(Boolean arg) {
        return doOr(arg)
    }

    public Row or(Float arg) {
        return doOr(arg)
    }

    public Row or(Short arg) {
        return doOr(arg)
    }

    public Row or(Byte arg) {
        return doOr(arg)
    }

    public Row or(BigDecimal arg) {
        return doOr(arg)
    }

    public Row doOr(arg) {
        values.add(arg)
        return this
    }

    public Object[] asArray() {
        return values as Object[]
    }

    public List<Object> asList() {
        return values
    }
}