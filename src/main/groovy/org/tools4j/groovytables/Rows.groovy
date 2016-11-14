package org.tools4j.groovytables

/**
 * User: ben
 * Date: 4/11/2016
 * Time: 5:46 PM
 */
class Rows implements Iterable<Row>{
    public final static Rows EMPTY = new Rows(Collections.EMPTY_LIST)
    private final List<String> columnHeadings
    private final List<Row> rows

    Rows(final List<Row> rows) {
        this(Collections.EMPTY_LIST, rows)
    }

    Rows(final List<String> columnHeadings, final List<Row> rows) {
        this.rows = rows
        this.columnHeadings = columnHeadings
    }

    public <T> T execute(final Closure<T> closure){
        final ClosureMethod<T> closureMethod = new ClosureMethod<>(closure)

        for(final Row row: rows){
            final ClosureCallPrecursor<T> precursor;
            if(hasColumnHeadings()){
                precursor = closureMethod.getCallPrecursor(columnHeadings, row.values.toArray())
            } else {
                precursor = closureMethod.getCallPrecursor(row.values.toArray())
            }
            if(precursor.suitability == Suitability.NOT_SUITABLE){
                throw new IllegalArgumentException("Not Suitable!")
            } else {
                precursor.executeMethod()
            }
        }
    }

    public boolean hasColumnHeadings(){
        return !columnHeadings.isEmpty()
    }

    public boolean isEmpty(){
        return rows.isEmpty()
    }

    public static Rows with(final Closure tableContent) {
        final List<Row> rows = SimpleTableParser.createListOfRows(tableContent)
        return new Rows(rows)
    }

    public Iterator<Row> iterator(){
        return rows.iterator()
    }

    public int size() {
        return rows.size()
    }

    public Row get(final int index) {
        return rows.get(index)
    }

    List<String> getColumnHeadings() {
        return columnHeadings
    }

    Object[] toArray() {
        return rows.toArray()
    }
}
