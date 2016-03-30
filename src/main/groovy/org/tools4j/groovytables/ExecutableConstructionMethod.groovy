package org.tools4j.groovytables;

import java.lang.reflect.Executable;

/**
 * User: ben
 * Date: 11/03/2016
 * Time: 9:42 AM
 */
trait ExecutableConstructionMethod<T> extends ConstructionMethod<T> {
    abstract T construct(Object[] args);
    abstract Executable getExecutable();

    ExecutableConstructionMethodPrecursor<T> getConstructionMethodPrecursor(final List<String> columnHeadings, final Object ... rawArgs){
        //column headings are not used so disregard...
        return getConstructionMethodPrecursor(rawArgs)
    }

    ExecutableConstructionMethodPrecursor<T> getConstructionMethodPrecursor(final Object ... rawArgs){
        final Executable executable = getExecutable()

        Logger.info("    Getting CreationMethodPrecursor for exectuable of name $executable.name with args ${executable.parameterTypes.collect{it.simpleName}}")
        if(executable.getParameterCount() != rawArgs.size()){
            Logger.info("        Mismatched number of args, executable:$executable.parameterCount != args:$rawArgs.length")
            return new ExecutableConstructionMethodPrecursor(Suitability.NOT_SUITABLE, rawArgs, null, null)
        } else {
            Logger.info("        Correct number of args: $rawArgs.length")
        }
        Suitability worstSuitabilityOfThisExecutable = Suitability.SUITABLE;
        final List<ValueCoercionResult> coercedResults = new ArrayList<>(executable.getParameterCount());
        for (int i = 0; i < executable.getParameterCount(); i++) {
            final Class constructorParamType = executable.getParameterTypes()[i];
            Object rawArg = rawArgs[i]
            final ValueCoercionResult coercionResult = ValueCoersion.coerceToType(rawArg, constructorParamType);
            worstSuitabilityOfThisExecutable = worstSuitabilityOfThisExecutable.worseOf(coercionResult.getSuitability())
            Logger.info("        Coercion result for arg:${rawArg.getClass().simpleName}[$rawArg] to:$constructorParamType.simpleName : $coercionResult")
            coercedResults.add(coercionResult);
            if (coercionResult.getSuitability() == Suitability.NOT_SUITABLE) {
                break
            }
        }
        return new ExecutableConstructionMethodPrecursor(
                worstSuitabilityOfThisExecutable,
                rawArgs,
                coercedResults.collect{it.result}.toArray(),
                this
        );
    }
}
