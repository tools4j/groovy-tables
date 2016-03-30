package org.tools4j.groovytabledsl

import org.tools4j.groovytabledsl.model.Book
import spock.lang.Specification

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * User: ben
 * Date: 8/03/2016
 * Time: 5:41 PM
 */
class TypeCoercionTest extends Specification {
    def "test GetConstructionMethods Book"() {
        when:
        final List<ConstructionMethod<?>> constructionMethods = TypeCoercion.getConstructionMethods(Book)

        then:
        assert constructionMethods.count {it instanceof ClassConstructor} > 0
        assert constructionMethods.count {it instanceof StaticFactoryMethod} > 0
        assert constructionMethods.find { it.executable.name == "createPictureBook" } != null

        verifyConstructionMethods(constructionMethods)
    }

    def "test GetConstructionMethods PictureBook"() {
        when:
        final List<ConstructionMethod<?>> constructionMethods = TypeCoercion.getConstructionMethods(Book.PictureBook)

        then:
        assert constructionMethods.count {it instanceof ClassConstructor} == 1
        assert constructionMethods.count {it instanceof StaticFactoryMethod} == 1
        assert constructionMethods.find { it instanceof StaticFactoryMethod && it.executable.name == "createPictureBook" } != null
        assert constructionMethods.find { it instanceof StaticFactoryMethod && it.executable.name == "createBook" } == null

        verifyConstructionMethods(constructionMethods)
    }

    static void verifyConstructionMethods(ArrayList<ConstructionMethod> constructionMethods) {
        for (final ConstructionMethod constructionMethod : constructionMethods) {
            if (constructionMethod instanceof ClassConstructor) {
                final ClassConstructor classConstructor = (ClassConstructor) constructionMethod
                assert classConstructor.executable instanceof Constructor

            }
            else if (constructionMethod instanceof StaticFactoryMethod) {
                final StaticFactoryMethod staticFactoryMethod = (StaticFactoryMethod) constructionMethod
                assert staticFactoryMethod.executable instanceof Method
                assert Book.class.isAssignableFrom(((Method) staticFactoryMethod.getExecutable()).getReturnType())
            }
        }
    }
}
