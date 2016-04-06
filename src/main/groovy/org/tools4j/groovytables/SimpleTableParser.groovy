package org.tools4j.groovytables

import groovy.transform.*

/**
 * Inspired by Christian Baranowski's blog post http://tux2323.blogspot.co.uk/2013/04/simple-table-dsl-in-groovy.html
 */
class SimpleTableParser {
    private static ThreadLocal<List> context = new ThreadLocal<List>()

    @EqualsAndHashCode
    @ToString
    static class Var {
        final String name

        Var(final String name) {
            this.name = name
        }
    }

    static class PropertyVarConvertor {
        public Var getProperty(String property) {
            new Var(property)
        }

    }

    static Row appendNewRow(argOne, argTwo) {
        def row = new Row(values: [argOne])
        context.get().add(row)
        row.doOr(argTwo)
    }

    public static List<Row> createListOfRows(Closure tableData) {
        context.set([])
        use(OrDelegate) {
            tableData.delegate = new PropertyVarConvertor()
            tableData.resolveStrategy = Closure.DELEGATE_FIRST
            tableData()
        }
        context.get()
    }

    public static List<Object[]> createListOfArrays(Closure tableData) {
        final List<Row> rows = createListOfRows(tableData)
        rows.collect { Row row -> row.asArray() }
    }
}



