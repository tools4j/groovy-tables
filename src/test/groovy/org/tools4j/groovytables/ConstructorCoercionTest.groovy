package org.tools4j.groovytables

import org.tools4j.groovytables.model.Book
import spock.lang.Specification
import spock.lang.Unroll

import static ConstructionMethodFilter.CONSTRUCTORS
import static org.tools4j.groovytables.Suitability.*
import static org.tools4j.groovytables.TestUtils.assertActualEqualsExpected

/**
 * User: ben
 * Date: 16/02/2016
 * Time: 5:45 PM
 */
class ConstructorCoercionTest extends Specification {
    @Unroll
    def "GetConstructionPrecursor"() {
        expect:
        ExecutableConstructionMethod constructionMethod = new ClassConstructor(Book.getConstructor(String, Double, long))
        ExecutableConstructionMethodPrecursor constructionPrecursor = constructionMethod.getConstructionMethodPrecursor([], rawArgs)
        assert constructionPrecursor.suitability == suitability
        assertActualEqualsExpected(constructionPrecursor.coercedArgs, coercedArgs)

        where:
        suitability             |   rawArgs                                             | coercedArgs
        SUITABLE                | ["Power of One", 19.95d, 123456L] as Object[]         | ["Power of One", 19.95d, 123456L] as Object[]
        SUITABLE_WITH_COERCION  | ["Power of One", 19.95, 123456] as Object[]           | ["Power of One", 19.95d, 123456L] as Object[]
        SUITABLE_WITH_COERCION  | ["${'Power of One'}", 19.95d, 123456d] as Object[]    | ["Power of One", 19.95d, 123456L] as Object[]
        NOT_SUITABLE            | ["only on arg"] as Object[]                           | null
    }

    def "GetConstructionPrecursor then execute constructor"() {
        when:
        ExecutableConstructionMethod constructionMethod = new ClassConstructor(Book.getConstructor(String, Double, long))
        ExecutableConstructionMethodPrecursor constructionPrecursor = constructionMethod.getConstructionMethodPrecursor([], "Power of One", 19.95d, 123456L)
        final Book actualBook = (Book) constructionPrecursor.executeConstructionMethod().result
        final Book expectedBook = new Book("Power of One", 19.95, 123456)

        then:
        assert actualBook == expectedBook

    }

    def "CoerceToType Suitable"() {
        when:
        final TypeCoercionResult constructionResult = TypeCoercion.coerceToType(Book, CONSTRUCTORS, [], args)
        final Book actualBook = (Book) constructionResult.result

        then:
        assert actualBook == expectedBook

        where:
        args                                                | expectedBook
        ["Power of One", 19.95d, 123456L] as Object[]       | new Book("Power of One", 19.95, 123456)
        ["Power of One", null, 123456L] as Object[]         | new Book("Power of One", null, 123456)
        ["Power of One", bd(19.95d), 123456 ] as Object[]   | new Book("Power of One", 19.95, 123456)
        ["Power of One", 19.95, bd(123456)] as Object[]     | new Book("Power of One", 19.95, 123456)
        ["Power of One"] as Object[]                        | new Book("Power of One")
        [null] as Object[]                                  | new Book(null)
    }

    def "CoerceToType Unsuitable"() {
        when:
        final TypeCoercionResult constructionResult = TypeCoercion.coerceToType(Book, CONSTRUCTORS, args)

        then:
        assert constructionResult.suitability == NOT_SUITABLE

        where:
        args                                                | _
        ["Power of One", 19.95f, 123456d] as Object[]       | _ //Cannot coerce float with decimal places to double
        ["Power of One", 19.95, null] as Object[]           | _ //The third parameter is a long primitive.  Cannot coerce null to a primitive
        ["Power of One", "blah", "blah"] as Object[]        | _ //There is no signature like this

    }

    BigDecimal bd(final long value) {
        return BigDecimal.valueOf(value)
    }

    BigDecimal bd(final double value) {
        return BigDecimal.valueOf(value)
    }
}
