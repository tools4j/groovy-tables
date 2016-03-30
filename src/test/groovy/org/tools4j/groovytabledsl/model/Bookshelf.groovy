package org.tools4j.groovytabledsl.model
/**
 * User: ben
 * Date: 17/03/2016
 * Time: 5:33 PM
 */
class Bookshelf {
    final String bookshelfOwner
    final Book[] books

    Bookshelf(final String bookshelfOwner, final Book[] books) {
        this.books = books
        this.bookshelfOwner = bookshelfOwner
    }
}
