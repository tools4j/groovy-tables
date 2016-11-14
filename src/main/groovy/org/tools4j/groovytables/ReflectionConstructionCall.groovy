package org.tools4j.groovytables

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * User: ben
 * Date: 19/02/2016
 * Time: 5:26 PM
 */

class ReflectionConstructionCall<T> implements Callable<T>{
    final Class<T> clazz
    final Constructor<T> zeroArgConstructor;

    ReflectionConstructionCall(final Class<T> clazz) {
        this.clazz = clazz
        this.zeroArgConstructor = getZeroArgConstructor()
    }

    T construct(final List<FieldSetPrecursor<T>> fieldSetPrecursors) {
        Logger.info("    Calling zero arg constructor for class $clazz")
        T object = zeroArgConstructor.newInstance()
        for(final FieldSetPrecursor<T> fieldSetPrecursor: fieldSetPrecursors){
            Logger.info("        Calling field set method $fieldSetPrecursor.fieldSetMethod with arg '$fieldSetPrecursor.coercedArg'")
            fieldSetPrecursor.fieldSetMethod.execute(object, fieldSetPrecursor.coercedArg)
        }
        return object;
    }

    @Override
    ReflectionCallPrecursor<T> getCallPrecursor(final Object... rawArgs) {
        throw new UnsupportedOperationException("Method not supported.  Please use method of the same name, but with columnHeadings being passed.")
    }

    @Override
    ReflectionCallPrecursor<T> getCallPrecursor(final List<String> fieldNames, final Object... rawArgs) {
        Logger.info("    Getting reflection based CreationMethodPrecursor for class $clazz")

        if(fieldNames.isEmpty()){
            return new ReflectionCallPrecursor(Suitability.NOT_SUITABLE, rawArgs, null, null)
        }

        if(fieldNames.size() != rawArgs.length){
            Logger.info("        Mismatched number of columnHeadings, columnHeadings:${fieldNames.size()} != args:$rawArgs.length")
            return new ReflectionCallPrecursor<T>(Suitability.NOT_SUITABLE, rawArgs, null, null)
        }

        if(zeroArgConstructor == null){
            Logger.info("        No zero-arg constructor found")
            return new ReflectionCallPrecursor(Suitability.NOT_SUITABLE, rawArgs, null, null)
        }

        Suitability worstSuitability = Suitability.SUITABLE;
        final List<FieldSetPrecursor<T>> fieldSetMethods = new ArrayList<>(fieldNames.size());
        for (int i = 0; i < fieldNames.size(); i++) {
            final String fieldName = fieldNames.get(i)
            FieldSetPrecursor<T> mostSuitableSetMethod = getMostSuitableFieldSetMethod(clazz, fieldName, rawArgs[i])
            if (mostSuitableSetMethod.suitability == Suitability.NOT_SUITABLE) {
                return ReflectionCallPrecursor.NOT_SUITABLE
            } else {
                worstSuitability = worstSuitability.worseOf(mostSuitableSetMethod.suitability)
                fieldSetMethods.add(mostSuitableSetMethod)
            }
        }

        return new ReflectionCallPrecursor(
                worstSuitability,
                rawArgs,
                this,
                fieldSetMethods
        );
    }


    private Constructor getZeroArgConstructor(){
        final List<Constructor> zeroArgConstructors = clazz.getConstructors().findAll({ Constructor method -> method.parameterCount == 0 })
        if(zeroArgConstructors.isEmpty()){
            return null;
        } else if(zeroArgConstructors.size() > 1){
            throw new IllegalStateException("Found more than one zero-arg constructors, have not coded for this: $zeroArgConstructors")
        }
        return zeroArgConstructors.first()
    }

    protected FieldSetPrecursor<T> getMostSuitableFieldSetMethod(Class<?> clazz, String fieldName, Object arg) {
        final FieldSetPrecursor<T> setterMethod = getSuitableSetterMethod(clazz, fieldName, arg)
        if (setterMethod.suitability.isLessSuitableThan(Suitability.SUITABLE_WITH_EXPECTED_COERCION)) {
            final FieldSetPrecursor<T> directAccessMethod = getSuitableDirectFieldAccessMethod(clazz, fieldName, arg)
            if (directAccessMethod.isMoreSuitableThan(setterMethod)) {
                return directAccessMethod
            }
        }
        return setterMethod
    }

    protected FieldSetPrecursor<T> getSuitableSetterMethod(final Class clazz, final String fieldName, final Object arg){
        final List<Method> methods = clazz.getMethods()
                .findAll({ Method method -> !Modifier.isStatic(method.getModifiers()) })
                .findAll({ Method method -> method.getName().equals("set" + fieldName.capitalize()) })
                .findAll({ Method method -> method.getParameterTypes().size() == 1 })

        FieldSetPrecursor<T> mostSuitableSetter = null;

        for(final Method method: methods){
            final Class parameterClass = method.getParameterTypes()[0]
            final FieldSetPrecursor<T> setter

            if(arg != null && parameterClass == arg.class){
                Logger.info("        Field: $fieldName[${arg.class.getSimpleName()}]: Found suitable setter $method.name.  Exact type match to: $parameterClass.simpleName")
                setter = new FieldSetPrecursor<>(Suitability.SUITABLE, arg, new FieldSetMethodViaSetter(method))
                return setter
            } else if(arg != null && parameterClass.isAssignableFrom(clazz)){
                Logger.info("        Field: $fieldName[${arg.class.getSimpleName()}]: Found suitable setter $method.name.  Can be up-casted to type $parameterClass.simpleName")
                setter = new FieldSetPrecursor<>(Suitability.SUITABLE_BY_UP_CASTING, arg, new FieldSetMethodViaSetter(method))
            } else {
                final ValueCoercionResult valueCoercionResult = ValueCoerser.coerceToType(arg, parameterClass)
                final String argClass = arg == null ? "null" : arg.class.simpleName
                Logger.info("        Field: $fieldName[${argClass}]: Setter $method.name.  Coercion result to:$parameterClass.simpleName: $valueCoercionResult")
                setter = new FieldSetPrecursor<>(valueCoercionResult.suitability, valueCoercionResult.result, new FieldSetMethodViaSetter(method))
            }
            if(mostSuitableSetter == null || setter.isMoreSuitableThan(mostSuitableSetter)){
                mostSuitableSetter = setter
            }
            if(mostSuitableSetter.suitability == Suitability.SUITABLE){
                return mostSuitableSetter
            }
        }
        if(mostSuitableSetter == null){
            mostSuitableSetter = FieldSetPrecursor.NOT_SUITABLE
        }
        return mostSuitableSetter
    }


    protected FieldSetPrecursor<T> getSuitableDirectFieldAccessMethod(final Class clazz, final String fieldName, final Object arg){
        final List<Field> fields = clazz.getFields()
                .findAll({ Field method -> !Modifier.isStatic(method.getModifiers()) })
                .findAll({ Field method -> !Modifier.isFinal(method.getModifiers()) })
                .findAll({ Field method -> method.getName().equals(fieldName) })

        if(fields.size() == 0){
            return FieldSetPrecursor.NOT_SUITABLE
        } else if(fields.size() > 1){
            throw new IllegalStateException("Found more than one field with the name '$fieldName', have not expected this: $fields")
        }
        final Field field = fields.first()

        if(field.getType() == arg.class){
            Logger.info("        Field: $fieldName[${arg.class.getSimpleName()}]: Found suitable direct field access.  Exact type match to: $field.type.simpleName")
            return new FieldSetPrecursor<>(Suitability.SUITABLE, arg, new FieldSetMethodViaDirectFieldAccess(field))
        } else if(field.getType().isAssignableFrom(clazz)){
            Logger.info("        Field: $fieldName[${arg.class.getSimpleName()}]: Found suitable direct field access.  Arg type can be up-cast to $field.type.simpleName")
            return new FieldSetPrecursor<>(Suitability.SUITABLE_BY_UP_CASTING, arg, new FieldSetMethodViaDirectFieldAccess(field))
        } else {
            final ValueCoercionResult valueCoercionResult = ValueCoerser.coerceToType(arg, field.getType())
            Logger.info("        Field: $fieldName[${arg.class.getSimpleName()}]: Coercion result for field $field.type.simpleName: $valueCoercionResult")
            return new FieldSetPrecursor<>(valueCoercionResult.suitability, valueCoercionResult.result, new FieldSetMethodViaDirectFieldAccess(field))
        }
    }


    @Override
    public String toString() {
        return "ReflectionConstructionMethod{" +
                "clazz=" + clazz +
                '}';
    }
}
