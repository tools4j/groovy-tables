package org.tools4j.groovytables

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.function.Predicate

/**
 * User: ben
 * Date: 15/02/2016
 * Time: 5:37 PM
 */
public class TypeCoercer {

    public static <T> TypeCoercionResult<T> coerceToType(final Class<T> clazz, final ConstructionMethodFilter constructionMethodFilter, final Object ... rawArgs) {
        return coerceToType(clazz, constructionMethodFilter, [], rawArgs)
    }

    public static <T> TypeCoercionResult<T> coerceToType(final Class<T> clazz, final Predicate<Callable> constructionMethodFilter, final List<String> columnHeadings, final Object ... rawArgs) {
        final long startTime = System.currentTimeMillis()
        Logger.info("Attempting coercion to class $clazz.simpleName from args: $rawArgs")
        ArrayList<? extends Callable> constructionMethods = getConstructionMethods(clazz, constructionMethodFilter)
        CallPrecursor<TypeCoercionResult<T>> mostSuitableConstructor = ExecutableCallPrecursor.NOT_SUITABLE
        for(final Callable constructionMethod: constructionMethods){
            CallPrecursor<TypeCoercionResult<T>> constructionPrecursor = constructionMethod.getCallPrecursor(columnHeadings, rawArgs)

            if(constructionPrecursor.getSuitability() == Suitability.SUITABLE){
                Logger.info("    Found SUITABLE construction method: $constructionMethod")
                return constructionPrecursor.executeMethod()
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
            final TypeCoercionResult<T> coercionResult = mostSuitableConstructor.executeMethod()
            Logger.info("    Chosen and executed constructor as the most suitable: $mostSuitableConstructor. Took: $executionTime ms")
            return coercionResult
        }

    }

    protected static <T> ArrayList<Callable> getConstructionMethods(final Class<T> clazz, final Predicate<Callable> constructionMethodFilter) {
        ArrayList<? extends Callable> constructionMethods = getConstructionMethods(clazz)
        constructionMethods = constructionMethods.findAll { constructionMethodFilter.test(it) }
        return constructionMethods
    }

    protected static <T> ArrayList<Callable<T>> getConstructionMethods(final Class<T> clazz) {
        final Constructor[] constructors = clazz.getConstructors();
        final Method[] methods = clazz.getMethods();

        List<? extends Callable> constructionMethods = new ArrayList<>()
        constructionMethods.addAll(constructors.collect { new ClassConstructorConstructionCall(it) })
        constructionMethods.addAll(methods.findAll({ Modifier.isStatic(it.getModifiers()) && clazz.isAssignableFrom(it.getReturnType())}).collect { new StaticFactoryMethodConstructionCall(it) })
        constructionMethods.add(new ReflectionConstructionCall<>(clazz))
        return constructionMethods
    }
}