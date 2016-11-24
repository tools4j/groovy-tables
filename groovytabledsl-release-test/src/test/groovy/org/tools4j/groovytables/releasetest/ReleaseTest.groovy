package org.tools4j.groovytables.releasetest

import org.tools4j.groovytables.GroovyTables
import spock.lang.Specification

/**
 * User: ben
 * Date: 5/04/2016
 * Time: 5:22 PM
 */
class ReleaseTest extends Specification{
    def "test"() {
        when:
        def books = GroovyTables.createListOf(Book.class).fromTable {
            author         | title                  | cost  | year
            "Terry Denton" | "Gasp"                 | 15.50 | 1990
            "Terry Denton" | "Flying Man"           | 15.23 | 1991
            "Terry Denton" | "Felix and Alexander"  |   0.0 | 1992
            "Terry Denton" | "Spooner or Later"     |  0.50 | 1993
            "Terry Denton" | "12 Storey Treehouse"  |     3 | 1994
            "Terry Denton" | "Gasp"                 |     5 | 1995
        }

        then:
        assert books != null;
        assert books.size() == 6;
        assert books.count {it.author == "Terry Denton"} == 6
        assert books.count {it.title == "Gasp"} == 2
    }
}
