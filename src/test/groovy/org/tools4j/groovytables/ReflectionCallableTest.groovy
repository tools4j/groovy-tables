package org.tools4j.groovytables

import org.tools4j.groovytables.model.Book
import spock.lang.Specification
import spock.lang.Unroll

import static org.tools4j.groovytables.ConstructionMethodFilter.*
import static org.tools4j.groovytables.Suitability.*


/**
 * User: ben
 * Date: 16/02/2016
 * Time: 5:45 PM
 */
class ReflectionCallableTest extends Specification {
    @Unroll
    def "test GetSuitableSetterMethod"() {
        expect:
        ReflectionConstructionCall reflectionMethod = new ReflectionConstructionCall<>(Book)

        FieldSetPrecursor fieldSetPrecursor = reflectionMethod.getSuitableSetterMethod( Book, fieldName, arg )

        assert fieldSetPrecursor.suitability == suitability

        where:
        suitability                     | fieldName         |   arg
        NOT_SUITABLE                    | "author"          | "Bryce Courtenay"
        SUITABLE                        | "title"           | "Power of One"
        SUITABLE_WITH_EXPECTED_COERCION | "cost"            | 19.95
        SUITABLE                        | "cost"            | 19.95d
        SUITABLE                        | "serialNumber"    | 1234
    }

    @Unroll
    def "test GetSuitableDirectAccessMethod"() {
        expect:
        ReflectionConstructionCall reflectionMethod = new ReflectionConstructionCall<>(Book)

        FieldSetPrecursor fieldSetPrecursor = reflectionMethod.getSuitableDirectFieldAccessMethod( Book, fieldName, arg )

        assert fieldSetPrecursor.suitability == suitability

        where:
        suitability                     | fieldName         |   arg
        SUITABLE                        | "author"          | "Bryce Courtenay"
        SUITABLE                        | "title"           | "Power of One"
        SUITABLE_WITH_EXPECTED_COERCION | "cost"            | 19.95
        SUITABLE                        | "cost"            | 19.95d
        SUITABLE_WITH_COERCION          | "serialNumber"    | 1234
    }

    @Unroll
    def "test GetMostSuitableFieldSetMethod"() {
        expect:
        ReflectionConstructionCall reflectionMethod = new ReflectionConstructionCall<>(Book)

        FieldSetPrecursor fieldSetPrecursor = reflectionMethod.getMostSuitableFieldSetMethod( Book, fieldName, arg )

        assert fieldSetPrecursor.suitability == suitability

        where:
        suitability                     | fieldName         |   arg             | type
        SUITABLE                        | "author"          | "Bryce Courtenay" | FieldSetMethodViaDirectFieldAccess
        SUITABLE                        | "title"           | "Power of One"    | FieldSetMethodViaSetter
        SUITABLE_WITH_EXPECTED_COERCION | "cost"            | 19.95             | FieldSetMethodViaSetter
        SUITABLE                        | "cost"            | 19.95d            | FieldSetMethodViaSetter
        SUITABLE                        | "serialNumber"    | 1234              | FieldSetMethodViaSetter
    }


    @Unroll
    def "test GetReflectionPrecursor"() {
        expect:
        ReflectionConstructionCall reflectionMethod = new ReflectionConstructionCall<>(Book)

        final ReflectionCallPrecursor constructionPrecursor = reflectionMethod.getCallPrecursor(
                ["author", "title", "cost", "serialNumber"], "Bryce Courtenay", "Power of One", 19.95, 123456L)

        assert constructionPrecursor.suitability == SUITABLE_WITH_EXPECTED_COERCION
        //author
        assert constructionPrecursor.fieldSetPrecursors[0].fieldSetMethod instanceof FieldSetMethodViaDirectFieldAccess
        assert constructionPrecursor.fieldSetPrecursors[0].fieldSetMethod.field.name == "author"
        assert constructionPrecursor.fieldSetPrecursors[0].coercedArg == "Bryce Courtenay"

        assert constructionPrecursor.fieldSetPrecursors[1].fieldSetMethod instanceof FieldSetMethodViaSetter
        assert constructionPrecursor.fieldSetPrecursors[1].fieldSetMethod.method.name == "setTitle"
        assert constructionPrecursor.fieldSetPrecursors[1].coercedArg == "Power of One"

        assert constructionPrecursor.fieldSetPrecursors[2].fieldSetMethod instanceof FieldSetMethodViaSetter
        assert constructionPrecursor.fieldSetPrecursors[2].fieldSetMethod.method.name == "setCost"
        assert constructionPrecursor.fieldSetPrecursors[2].coercedArg == 19.95d

        assert constructionPrecursor.fieldSetPrecursors[3].fieldSetMethod instanceof FieldSetMethodViaSetter
        assert constructionPrecursor.fieldSetPrecursors[3].fieldSetMethod.method.name == "setSerialNumber"
        assert constructionPrecursor.fieldSetPrecursors[3].coercedArg == 123456L
    }

    @Unroll
    def "test Construct"() {
        expect:
        ReflectionConstructionCall reflectionMethod = new ReflectionConstructionCall<>(Book)

        final ReflectionCallPrecursor constructionPrecursor = reflectionMethod.getCallPrecursor(
                ["author", "title", "cost", "serialNumber"], "Bryce Courtenay", "Power of One", 19.95, 123456L)

        TypeCoercionResult<Book> typeCoercionResult = constructionPrecursor.executeMethod()
        with(typeCoercionResult) {
            assert suitability == SUITABLE_WITH_EXPECTED_COERCION
            assert result.author == "Bryce Courtenay"
            assert result.title == "Power of One"
            assert result.cost == 19.95d
            assert result.serialNumber == 123456
        }
    }

    @Unroll
    def "test CoerceToType Suitable"() {
        when:
        final TypeCoercionResult constructionResult = TypeCoercer.coerceToType(Book, REFLECTION, ["title", "cost", "serialNumber"], args)
        final Book actualBook = (Book) constructionResult.result

        then:
        assert actualBook == expectedBook

        where:
        args                                                | expectedBook
        ["Power of One", 19.95d, 123456L] as Object[]       | new Book("Power of One", 19.95, 123456)
        ["Power of One", null, 123456L] as Object[]         | new Book("Power of One", null, 123456)
        ["Power of One", bd(19.95d), 123456 ] as Object[]   | new Book("Power of One", 19.95, 123456)
        ["Power of One", 19.95, bd(123456)] as Object[]     | new Book("Power of One", 19.95, 123456)
    }

    @Unroll
    def "test CoerceToType Unsuitable"() {
        when:
        final TypeCoercionResult constructionResult = TypeCoercer.coerceToType(Book, CONSTRUCTORS, ["author", "title", "cost", "serialNumber"], args)

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
