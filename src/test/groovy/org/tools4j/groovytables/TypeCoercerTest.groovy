package org.tools4j.groovytables

import org.tools4j.groovytables.model.Book
import spock.lang.Specification

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * User: ben
 * Date: 8/03/2016
 * Time: 5:41 PM
 */
class TypeCoercerTest extends Specification {
    def "test GetConstructionMethods Book"() {
        when:
        final List<Callable<?>> constructionMethods = TypeCoercer.getConstructionMethods(Book)

        then:
        assert constructionMethods.count {it instanceof ClassConstructorConstructionCall} > 0
        assert constructionMethods.count {it instanceof StaticFactoryMethodConstructionCall} > 0
        assert constructionMethods.find { it.executable.name == "createPictureBook" } != null

        verifyConstructionMethods(constructionMethods)
    }

    def "test GetConstructionMethods PictureBook"() {
        when:
        final List<Callable<?>> constructionMethods = TypeCoercer.getConstructionMethods(Book.PictureBook)

        then:
        assert constructionMethods.count {it instanceof ClassConstructorConstructionCall} == 1
        assert constructionMethods.count {it instanceof StaticFactoryMethodConstructionCall} == 1
        assert constructionMethods.find { it instanceof StaticFactoryMethodConstructionCall && it.executable.name == "createPictureBook" } != null
        assert constructionMethods.find { it instanceof StaticFactoryMethodConstructionCall && it.executable.name == "createBook" } == null

        verifyConstructionMethods(constructionMethods)
    }

    static void verifyConstructionMethods(ArrayList<Callable> constructionMethods) {
        for (final Callable constructionMethod : constructionMethods) {
            if (constructionMethod instanceof ClassConstructorConstructionCall) {
                final ClassConstructorConstructionCall classConstructor = (ClassConstructorConstructionCall) constructionMethod
                assert classConstructor.executable instanceof Constructor

            }
            else if (constructionMethod instanceof StaticFactoryMethodConstructionCall) {
                final StaticFactoryMethodConstructionCall staticFactoryMethod = (StaticFactoryMethodConstructionCall) constructionMethod
                assert staticFactoryMethod.executable instanceof Method
                assert Book.class.isAssignableFrom(((Method) staticFactoryMethod.getExecutable()).getReturnType())
            }
        }
    }
}
