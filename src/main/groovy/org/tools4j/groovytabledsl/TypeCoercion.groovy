package org.tools4j.groovytabledsl

import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.function.Predicate

/**
 * User: ben
 * Date: 15/02/2016
 * Time: 5:37 PM
 */
public class TypeCoercion {

    public static <T> TypeCoercionResult<T> coerceToType(final Class<T> clazz, final ConstructionMethodFilter constructionMethodFilter, final Object ... rawArgs) {
        return coerceToType(clazz, constructionMethodFilter, [], rawArgs)
    }

    public static <T> TypeCoercionResult<T> coerceToType(final Class<T> clazz, final Predicate<ConstructionMethod> constructionMethodFilter, final List<String> columnHeadings, final Object ... rawArgs) {
        final long startTime = System.currentTimeMillis()
        Logger.info("Attempting coercion to class $clazz.simpleName from args: $rawArgs")
        ArrayList<? extends ConstructionMethod> constructionMethods = getConstructionMethods(clazz, constructionMethodFilter)
        ConstructionMethodPrecursor mostSuitableConstructor = ExecutableConstructionMethodPrecursor.NOT_SUITABLE
        for(final ConstructionMethod constructionMethod: constructionMethods){
            ConstructionMethodPrecursor constructionPrecursor = constructionMethod.getConstructionMethodPrecursor(columnHeadings, rawArgs)

            if(constructionPrecursor.getSuitability() == Suitability.SUITABLE){
                Logger.info("    Found SUITABLE construction method: $constructionMethod")
                return constructionPrecursor.executeConstructionMethod()
            }

            if(constructionPrecursor.isMoreSuitableThan(mostSuitableConstructor)){
                Logger.info("    Found potential constructor: $constructionMethod")
                mostSuitableConstructor = constructionPrecursor
            }
        }

        final long endTime = System.currentTimeMillis()
        final long executionTime = endTime - startTime
        if(mostSuitableConstructor.suitability == Suitability.NOT_SUITABLE){
            Logger.info("    Could NOT find suitable constructor.  Took: $executionTime ms")
            return TypeCoercionResult.NOT_SUITABLE
        } else {
            final TypeCoercionResult<T> coercionResult = mostSuitableConstructor.executeConstructionMethod()
            Logger.info("    Chosen and executed constructor as the most suitable: $mostSuitableConstructor. Took: $executionTime ms")
            return coercionResult
        }

    }

    protected static <T> ArrayList<ConstructionMethod> getConstructionMethods(final Class<T> clazz, final Predicate<ConstructionMethod> constructionMethodFilter) {
        ArrayList<? extends ConstructionMethod> constructionMethods = getConstructionMethods(clazz)
        constructionMethods = constructionMethods.findAll { constructionMethodFilter.test(it) }
        return constructionMethods
    }

    protected static <T> ArrayList<ConstructionMethod<T>> getConstructionMethods(final Class<T> clazz) {
        final Constructor[] constructors = clazz.getConstructors();
        final Method[] methods = clazz.getMethods();

        List<? extends ConstructionMethod> constructionMethods = new ArrayList<>()
        constructionMethods.addAll(constructors.collect { new ClassConstructor(it) })
        constructionMethods.addAll(methods.findAll({ Modifier.isStatic(it.getModifiers()) && clazz.isAssignableFrom(it.getReturnType())}).collect { new StaticFactoryMethod(it) })
        constructionMethods.add(new ReflectionConstructionMethod<>(clazz))
        return constructionMethods
    }
}