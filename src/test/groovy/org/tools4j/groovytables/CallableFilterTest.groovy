package org.tools4j.groovytables

import org.tools4j.groovytables.model.Book
import spock.lang.Specification

import static ConstructionMethodFilter.filter
import static ConstructionMethodFilter.CONSTRUCTORS
import static ConstructionMethodFilter.INCLUDE_ALL
import static ConstructionMethodFilter.STATIC_FACTORY_METHODS
import static org.tools4j.groovytables.ConstructionMethodFilter.REFLECTION

/**
 * User: ben
 * Date: 8/03/2016
 * Time: 6:40 AM
 */
class CallableFilterTest extends Specification {
    static final List BOOK_CONSTRUCTION_METHODS = TypeCoercer.getConstructionMethods(Book)

    def "test ToUnmodifiable"() {
        given:
        ConstructionMethodFilter filter = CONSTRUCTORS

        when:
        filter.withName("blah")

        then:
        thrown UnsupportedOperationException
    }

    def "test WithConstructors"() {
        when:
        List results = filter().withConstructors().apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} > 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} == 0
        assert results.count {it instanceof ReflectionConstructionCall} == 0
    }

    def "test WithStaticFactoryMethods"() {
        when:
        List results = filter().withStaticFactoryMethods().apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} == 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} > 0
        assert results.count {it instanceof ReflectionConstructionCall} == 0
    }

    def "test WithReflection"() {
        when:
        List results = filter().withReflection().apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} == 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} == 0
        assert results.count {it instanceof ReflectionConstructionCall} == 1
    }

    def "test WithName 'create'"() {
        when:
        List results = filter().withName("create").apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} == 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} == 4
        assert results.count {it instanceof ReflectionConstructionCall} == 0
    }

    def "test WithName 'createFromAuthorAndTitle'"() {
        when:
        List results = filter().withName("createFromAuthorAndTitle").apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} == 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} == 1
        assert results.count {it instanceof ReflectionConstructionCall} == 0
    }

    def "test WithName that does not exist"() {
        when:
        List results = filter().withName("blah blah").apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.size() == 0
    }


    def "test constant CONSTRUCTORS"() {
        when:
        List results = CONSTRUCTORS.apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} > 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} == 0
        assert results.count {it instanceof ReflectionConstructionCall} == 0
    }

    def "test constant STATIC_FACTORY_METHODS"() {
        when:
        List results = STATIC_FACTORY_METHODS.apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} == 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} > 0
        assert results.count {it instanceof ReflectionConstructionCall} == 0
    }

    def "test constant REFLECTION"() {
        when:
        List results = REFLECTION.apply(BOOK_CONSTRUCTION_METHODS)

        then:
        assert results.count {it instanceof ClassConstructorConstructionCall} == 0
        assert results.count {it instanceof StaticFactoryMethodConstructionCall} == 0
        assert results.count {it instanceof ReflectionConstructionCall} == 1
    }

    def "test constant CONSTRUCTORS is immutable"() {
        when:
        CONSTRUCTORS.withName("blah")

        then:
        thrown UnsupportedOperationException
    }

    def "test constant STATIC_FACTORY_METHODS is immutable"() {
        when:
        STATIC_FACTORY_METHODS.withName("blah")

        then:
        thrown UnsupportedOperationException
    }

    def "test constant INCLUDE_ALL is immutable"() {
        when:
        INCLUDE_ALL.withName("blah")

        then:
        thrown UnsupportedOperationException
    }
}
