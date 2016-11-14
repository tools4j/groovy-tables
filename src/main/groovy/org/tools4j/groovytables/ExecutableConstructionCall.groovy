package org.tools4j.groovytables;

import java.lang.reflect.Executable;

/**
 * User: ben
 * Date: 11/03/2016
 * Time: 9:42 AM
 */
trait ExecutableConstructionCall<T> implements Callable<T> {
    abstract T construct(Object[] args);
    abstract Executable getExecutable();

    @Override
    ExecutableCallPrecursor<T> getCallPrecursor(final List<String> columnHeadings, final Object ... rawArgs){
        //column headings are not used so disregard...
        return getCallPrecursor(rawArgs)
    }

    @Override
    ExecutableCallPrecursor<T> getCallPrecursor(final Object ... rawArgs){
        final Executable executable = getExecutable()

        Logger.info("    Getting CreationMethodPrecursor for exectuable of name $executable.name with args ${executable.parameterTypes.collect{it.simpleName}}")
        if(executable.getParameterCount() != rawArgs.size()){
            Logger.info("        Mismatched number of args, executable:$executable.parameterCount != args:$rawArgs.length")
            return new ExecutableCallPrecursor(Suitability.NOT_SUITABLE, rawArgs, null, null)
        } else {
            Logger.info("        Correct number of args: $rawArgs.length")
        }
        Suitability worstSuitabilityOfThisExecutable = Suitability.SUITABLE;
        final List<ValueCoercionResult> coercedResults = new ArrayList<>(executable.getParameterCount());
        for (int i = 0; i < executable.getParameterCount(); i++) {
            final Class constructorParamType = executable.getParameterTypes()[i];
            Object rawArg = rawArgs[i]
            final ValueCoercionResult coercionResult = ValueCoerser.coerceToType(rawArg, constructorParamType);
            worstSuitabilityOfThisExecutable = worstSuitabilityOfThisExecutable.worseOf(coercionResult.getSuitability())
            Logger.info("        Coercion result for arg:${rawArg.getClass().simpleName}[$rawArg] to:$constructorParamType.simpleName : $coercionResult")
            coercedResults.add(coercionResult);
            if (coercionResult.getSuitability() == Suitability.NOT_SUITABLE) {
                break
            }
        }
        return new ExecutableCallPrecursor(
                worstSuitabilityOfThisExecutable,
                rawArgs,
                coercedResults.collect{it.result}.toArray(),
                this
        );
    }
}
