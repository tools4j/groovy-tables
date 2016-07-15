#Groovy Tables
##Overview
Groovy Tables is a groovy library which allows you to create lists of objects using a table like grammar.  It was written primarily for use when writing test cases.
##Examples
The following example creates a list of Book objects
```
List<Book> books = GroovyTables.createListOf(Book.class).fromTable {
    author                | title                    | cost  | year
    "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
    "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
    "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
    "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
    "George Orwell"       | "1984"                   |  8.95 | 1949
    "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
    "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
}
```
If you wish to, you can create your own reusable constructor method, giving you an even more concise syntax, e.g:
```
private List<Book> books(Closure closure){
    return GroovyTables.createListOf(Book.class).fromTable(closure)
}
```
...and in your test method:
```
List<Book> books = books {
    author                | title                    | cost  | year
    "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
    "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
    "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
    "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
    "George Orwell"       | "1984"                   |  8.95 | 1949
    "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
    "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
}
```
Here is another example creating a list of quotes:
```
List<Quote> quotes = GroovyTables.createListOf(Quote).fromTable {
    symbol    | price   | quantity
    "AUD/USD" | 1.0023  | 1200000
    "AUD/USD" | 1.0024  | 1400000
    "AUD/USD" | 1.0026  | 2000000
    "AUD/USD" | 1.0029  | 5000000
}
```
By default groovytabledsl finds a suitable constructor or static factory method to create instances of the given class.  You can give the api a filter to 'force' a certain mode of construction.  This example passes a filter which only accepts constructors. e.g.
```
List<Book> books = GroovyTables.createFromTable(Book.class, ConstructionMethodFilter.CONSTRUCTORS, {
    author                | title                    | cost  | year
    "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
    "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
    "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
    "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
    "George Orwell"       | "1984"                   |  8.95 | 1949
    "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
    "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
});
```
The filter is a just a Predicate<ConstructionMethod> so you are free to create your own filters.  The ConstructionMethodFilter also provides some method handy 'chainable' methods to help build filters.
```
List<Book> books = GroovyTables.createFromTable(Book.class, ConstructionMethodFilter.filter().withStaticFactoryMethods().withName("create"), {
    author                | title                    | cost  | year
    "Jane Austen"         | "Pride and Prejudice"    | 12.95 | 1813
    "Harper Lee"          | "To Kill a Mockingbird"  | 14.95 | 1960
    "F. Scott Fitzgerald" | "The Great Gatsby"       | 12.95 | 1925
    "Charlotte Brontë"    | "Jane Eyre"              |  6.95 | 1847
    "George Orwell"       | "1984"                   |  8.95 | 1949
    "J.D. Salinger"       | "The Catcher in the Rye" |  6.95 | 1951
    "William Shakespeare" | "Romeo and Juliet"       |  5.95 | 1597
});
```
The field names (column headings) are only used when the api attempts to call setter methods after constructing an object.  So if field names are omitted, the API will simply not attempt to construct an instance using reflection.

You can also create simple lists of arrays.  e.g.
```
final List<Object[]> listOfArrays = GroovyTables.createListOfArrays {
    1  | 2 | 3
    2  | 3 | 5
    55 | 5 | 60
}

println listOfArrays

Output:
[[1, 2, 3], [2, 3, 5], [55, 5, 60]]

```
###Methods of construction
There are three methods of construction.  Class Constructors, Static Factory Methods, and Reflection
  
1. Class Constructors - The API takes constructors as a preference compared to the other two methods.  The API will look at each constructor and will compare the parameters of the constructor, with the given arguments.  If the given arguments can be coerced into the list of parameters in the constructor, then that constructor is deemed a candidate.
2. Static Factory Methods - The API first builds a list of static class methods, which return a type which matches the class we are constructing.  Then, the same as for contructors, the method's parameters are compared with the given arguments to discover matches.
3. Reflection - The API will first look to see if a zero arg constructor exists.  If it does, it will then see if a suitable setter exists for each argument given (this is why field names are required when the Reflection method is used).  If a setter cannot be found for a field, then the API checks whether a field can be accessed directly.  Once it has confirmed that each column has a corresponding field that can be accessed, reflection is deemed a construction candidate.

####How a construction method is selected
Suitable construction methods are analyzed before construction takes place.  A decision is then made regarding the most suitable construction method.  This decision is made based on:

1. Any construction method filter that the caller has passed.  (No filter is passed by default).
2. The type of construction.  Class Constructors take precedence over Static Factory Methods which take precedence over straight Reflection
3. Whether any argument coercion is required.  For example, a static factory method whose parameters _exactly_ match the types passed as arguments in the table, will take precedence over a constructor which requires that an Integer argument be cast to a Long constructor parameter.

A construction method is selected separately for each 'line' of the table.  In the future we might cache last used construction methods but initial performance testing deemed little benefit was gained in terms of milliseconds of execution.
##How to use
###Gradle
```
repositories {
    mavenCentral()
}

dependencies {
    testCompile group: 'org.tools4j', name: 'groovy-tables', version: '1.2'
}
```
###Maven
```
<dependency>
    <groupId>org.tools4j</groupId>
    <artifactId>groovy-tables</artifactId>
    <version>1.2</version>
    <scope>test</scope>
</dependency>
```
##Acknowledgments
Thanks to Christian Baranowski whose blog post here: http://tux2323.blogspot.co.uk/2013/04/simple-table-dsl-in-groovy.html, inspired the fancy usage of operator overloading to achieve the table like grammar.

