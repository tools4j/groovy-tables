package org.tools4j.groovytables
/**
 * User: ben
 * Date: 8/11/2016
 * Time: 6:42 AM
 */
class ClosureMethod<T> implements Callable<T> {
    final Closure<T> closure

    ClosureMethod(final Closure<T> closure) {
        this.closure = closure
    }

    interface ClosureCall<T>{
        T call(Object[] coercedArgs)
    }

    static class ClosureCallUsingDelegateProperties<T> implements ClosureCall<T>{
        private final Closure<T> closure
        private final List<String> columnHeadings

        ClosureCallUsingDelegateProperties(Closure<T> closure, final List<String> columnHeadings) {
            this.columnHeadings = columnHeadings
            this.closure = closure
        }

        def call(Object[] coercedArgs) {
            final Object delegate = new Object()
            int i = 0
            columnHeadings.each { final String heading ->
                delegate.metaClass[heading] = coercedArgs[i++]
            }
            closure.setDelegate(delegate)
            closure.setResolveStrategy(Closure.DELEGATE_FIRST)
            closure.call()
        }
    }

    static class ClosureCallUsingClosureArguments<T> implements ClosureCall<T> {
        private final Closure<T> closure

        ClosureCallUsingClosureArguments(final Closure<T> closure) {
            this.closure = closure
        }

        T call(Object[] coercedArgs) {
            return closure.curry(coercedArgs).call()
        }
    }

    @Override
    ClosureCallPrecursor<T> getCallPrecursor(final List<String> columnHeadings, final Object... rawArgs) {
        if(columnHeadings == null || columnHeadings.size() == 0){
            return getCallPrecursor(rawArgs)
        }
        if(closure.getMaximumNumberOfParameters() > 1){
            throw new IllegalArgumentException("Cannot determine how to call closure.  You have specified column headings" +
                    " " + columnHeadings + " AND you have specified " + closure.getMaximumNumberOfParameters() + " closure parameters." +
                    " Please choose just one approach. i.e. specify column headings, and no closure parameters, or specify closure" +
                    " parameters and no column headings.")
        }
        return new ClosureCallPrecursor<T>(Suitability.SUITABLE, rawArgs, rawArgs, new ClosureCallUsingDelegateProperties<T>(closure, columnHeadings))
    }

    @Override
    ClosureCallPrecursor<T> getCallPrecursor(final Object... rawArgs) {
        Logger.info("    Getting ClosureCallPrecursor with parameters ${closure.getParameterTypes()}")
        if(closure.getMaximumNumberOfParameters() <= 1){
            throw new IllegalArgumentException("Cannot call closure.  Please either specify column headings" +
                    " for each of your " + rawArgs.size() + " columns.  Or, declare " + rawArgs.size() + " arguments" +
                    " in your closure to accept the row values: " + rawArgs)

        } else if(closure.getMaximumNumberOfParameters() > 1 && closure.getMaximumNumberOfParameters() != rawArgs.length){
            throw new IllegalArgumentException("Mismatched number of args, closure has:$closure.maximumNumberOfParameters != table has:$rawArgs.length.  (Please note, use of varargs is not supported).")
        }

        Logger.info("        Correct number of args: $rawArgs.length")
        Suitability worstSuitabilityOfThisExecutable = Suitability.SUITABLE;
        final List<ValueCoercionResult> coercedResults = new ArrayList<>(closure.getMaximumNumberOfParameters());
        for (int i = 0; i < closure.getParameterTypes().size(); i++) {
            final Class closureParamType = closure.getParameterTypes()[i];
            Object rawArg = rawArgs[i]
            final ValueCoercionResult coercionResult = ValueCoerser.coerceToType(rawArg, closureParamType);
            worstSuitabilityOfThisExecutable = worstSuitabilityOfThisExecutable.worseOf(coercionResult.getSuitability())
            Logger.info("        Coercion result for arg:${rawArg.getClass().simpleName}[$rawArg] to:$closureParamType.simpleName : $coercionResult")
            coercedResults.add(coercionResult);
            if (coercionResult.getSuitability() == Suitability.NOT_SUITABLE) {
                throw new IllegalArgumentException("Error processing row $rawArgs. Could not coerce argument '$rawArg' of class <${rawArg.getClass().simpleName}>" +
                        " to closure argument of type <$closureParamType.>")
            }
        }
        return new ClosureCallPrecursor<T>(
                worstSuitabilityOfThisExecutable,
                rawArgs,
                coercedResults.collect{it.result}.toArray(),
                new ClosureCallUsingClosureArguments<T>(closure)
        );
    }
}
