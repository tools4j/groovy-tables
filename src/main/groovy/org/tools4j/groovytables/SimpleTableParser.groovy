package org.tools4j.groovytables

import groovy.transform.*

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

    public static List<Row> asListOfRows(Closure tableData) {
        context.set([])
        use(OrDelegate) {
            tableData.delegate = new PropertyVarConvertor()
            tableData.resolveStrategy = Closure.DELEGATE_FIRST
            tableData()
        }
        context.get()
    }

    public static List<Object[]> asListOfArrays(Closure tableData) {
        final List<Row> rows = asListOfRows(tableData)
        rows.collect { Row row -> row.asArray() }
    }
}



