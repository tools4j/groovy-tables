package org.tools4j.groovytabledsl

import java.lang.reflect.Array

/**
 * User: ben
 * Date: 12/02/2016
 * Time: 6:25 AM
 */
class ValueCoersion {


    public static ValueCoercionResult coerceToType(final Object value, final Class clazz) {
        if(clazz == double){
            return coerceToDoublePrimitive(value)
        } else if(clazz == Double){
            return coerceToDouble(value)
        } else if(clazz == int){
            return coerceToIntPrimitive(value)
        } else if(clazz == Integer){
            return coerceToInteger(value)
        } else if(clazz == float){
            return coerceToFloatPrimitive(value)
        } else if(clazz == Float){
            return coerceToFloat(value)
        } else if(clazz == short){
            return coerceToShortPrimitive(value)
        } else if(clazz == Short){
            return coerceToShort(value)
        } else if(clazz == long){
            return coerceToLongPrimitive(value)
        } else if(clazz == Long){
            return coerceToLong(value)
        } else if(clazz == boolean){
            return coerceToBooleanPrimitive(value)
        } else if(clazz == Boolean){
            return coerceToBoolean(value)
        } else if(clazz == BigDecimal){
            return coerceToBigDecimal(value)
        } else if(clazz == String){
            return coerceToString(value)
        } else if(clazz == GString){
            return coerceToGString(value)
        } else if(clazz.isArray()){
            return coerceToArrayOfGivenType(value, clazz.getComponentType())
        } else if(clazz instanceof List){
            return coerceToListOfGivenType(value, clazz)
        } else if(value == null) {
            return new ValueCoercionResult(null, Suitability.SUITABLE)
        } else {
            return attemptSimpleAssignment(value, clazz)
        }
    }

    static <T> ValueCoercionResult<List<T>> coerceToListOfGivenType(final Object value, final Class clazz) {
        if(value == null) {
            return new ValueCoercionResult(null, Suitability.SUITABLE)
        } else if(value instanceof Collection){
            Logger.info("            Coercing collection $value to list of $clazz.simpleName")
            final Collection objCollection = (Collection) value
            final List<ValueCoercionResult<T>> coercionResults = new ArrayList<>()
            for(final Object object: objCollection){
                final ValueCoercionResult<T> coercionResult = coerceToType(object, clazz)
                coercionResults.add(coercionResult)
                Logger.info("                Coersion result: $coercionResult")
            }
            final Suitability worstSuitability = coercionResults.collect{ it.suitability }.inject { Suitability worst, Suitability it -> worst.worseOf(it) }
            if(worstSuitability.isMoreSuitableThan(Suitability.NOT_SUITABLE)){
                List<T> resultsAsList = coercionResults.collect{it.result}
                return new ValueCoercionResult<List<T>>(resultsAsList, worstSuitability)
            }
        } else if(value.class.isArray()){
            final ValueCoercionResult<List<T>> valueCoercionResult = coerceToListOfGivenType(Arrays.asList(value), clazz)
            //if suitability==SUITABLE downgrade to SUITABLE_WITH_EXPECTED_COERSION, as we've received an array, but have coerced to a List
            return new ValueCoercionResult<List<T>>(valueCoercionResult.result, valueCoercionResult.suitability.worseOf(Suitability.SUITABLE_WITH_EXPECTED_COERCION))
        }
        return ValueCoercionResult.NOT_SUITABLE
    }

    /**
     * There could be much optimization done here.
     *
     * The method I'm using is to loop through the given array, and attempt to coerce each and every member.
     *
     * There are some much (presumably) faster methods.  e.g. if coercing to an array of integers, and if given
     * an array of doubles, it is possible just to use a simple cast (Integer[]) or (int[]).
     *
     * Might do this later.
     */
    static <T> ValueCoercionResult<T[]> coerceToArrayOfGivenType(final Object value, final Class<T> clazz) {
        if(value == null) {
            return new ValueCoercionResult(null, Suitability.SUITABLE)
        } else if(value.class.isArray()){
            Logger.info("            Coercing array $value to array of $clazz.simpleName")
            final Object[] objArray = (Object[]) value
            final List<ValueCoercionResult<T>> coercionResults = new ArrayList<>()
            for(final Object object: objArray){
                final ValueCoercionResult<T> coercionResult = coerceToType(object, clazz)
                coercionResults.add(coercionResult)
                Logger.info("                Coersion result: $coercionResult")
            }
            final Suitability worstSuitability = coercionResults.collect{ it.suitability }.inject { Suitability worst, Suitability it -> worst.worseOf(it) }
            if(worstSuitability.isMoreSuitableThan(Suitability.NOT_SUITABLE)){
                List<T> resultsAsList = coercionResults.collect{it.result}
                // important not to try and cast this array to T.  Doing so will cast it to an Object[] if clazz is a primitive type
                Object array = Array.newInstance(clazz, resultsAsList.size())
                resultsAsList.eachWithIndex{ T entry, int i -> array[i] = entry}
                return new ValueCoercionResult(array, worstSuitability)
            }
        } else if(value instanceof Collection){
            final ValueCoercionResult<T[]> valueCoercionResult = coerceToArrayOfGivenType(((Collection) value).toArray(new T[value.size()]), clazz)
            //if suitability==SUITABLE downgrade to SUITABLE_WITH_EXPECTED_COERSION, as we've received a collecion, but have coerced to an array
            return new ValueCoercionResult<T[]>(valueCoercionResult.result, valueCoercionResult.suitability.worseOf(Suitability.SUITABLE_WITH_EXPECTED_COERCION))
        }
        return ValueCoercionResult.NOT_SUITABLE
    }

    static ValueCoercionResult attemptSimpleAssignment(final Object value, final Class clazz) {
        if(value.class == clazz){
            return new ValueCoercionResult(value, Suitability.SUITABLE)
        } else if(clazz.isAssignableFrom(value.class)){
            return new ValueCoercionResult(value, Suitability.SUITABLE_BY_UP_CASTING)
        } else {
            return ValueCoercionResult.NOT_SUITABLE
        }
    }

    public static ValueCoercionResult<Double> coerceToDouble(final Object value){
        if(value == null){
            return new ValueCoercionResult<Double>(null, Suitability.SUITABLE)

        } else if(value instanceof Double){
            return new ValueCoercionResult<Double>(value, Suitability.SUITABLE);

        } else if(value instanceof Integer){
            return new ValueCoercionResult<Double>((double) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Short){
            return new ValueCoercionResult<Double>((double) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Float
                && (((float) value) - ((int) value)) == 0.0d){
            //Am putting the conversion of floating point numbers from float to double in the too-hard-basket
            return new ValueCoercionResult<Double>((double) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Long
                && value <= Double.MAX_VALUE
                && value >= -Double.MAX_VALUE){
            return new ValueCoercionResult<Double>((double) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof BigDecimal) {
            double doubleValue = ((BigDecimal) value).doubleValue()
            if(doubleValue != Double.NEGATIVE_INFINITY && doubleValue != Double.POSITIVE_INFINITY) {
                /**
                 * This has been marked with SUITABLE_WITH_EXPECTED_COERCION.  I've done this because in groovy, if you
                 * write a decimal value, e.g. 2.3 groovy will by default create a BigDecimal to store a value.  To save
                 * developers having to put a 'd' after each decimal, I'm ranking this coercion higher.
                 */
                return new ValueCoercionResult<Double>(doubleValue, Suitability.SUITABLE_WITH_EXPECTED_COERCION);
            }

        } else if(value instanceof String){
            try {
                final int num = Double.parseDouble((String) value)
                return new ValueCoercionResult<Double>(num, Suitability.SUITABLE_WITH_COERCION)

            } catch (NumberFormatException e) {}

        } else if(value instanceof GString){
            return coerceToDouble(value.toString())

        }
        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<Double> coerceToDoublePrimitive(final Object value){
        if(value == null){
            return ValueCoercionResult.NOT_SUITABLE
        } else {
            return coerceToDouble(value);
        }
    }

    public static ValueCoercionResult<Float> coerceToFloat(final Object value){
        if(value == null){
            return new ValueCoercionResult<Float>(null, Suitability.SUITABLE)

        } else if(value instanceof Float){
            return new ValueCoercionResult<Float>(value, Suitability.SUITABLE);

        } else if(value instanceof Double
                && value <= Float.MAX_VALUE
                && value >= -Float.MAX_VALUE
                && (((double) value) - ((int) value)) == 0.0d){
            //Am putting the conversion of floating point numbers from double to float in the too-hard-basket
            return new ValueCoercionResult<Float>(value, Suitability.SUITABLE_WITH_COERCION)

        } else if(value instanceof Integer
                && value <= Float.MAX_VALUE
                && value >= -Float.MAX_VALUE){
            return new ValueCoercionResult<Float>((float) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Short){
            return new ValueCoercionResult<Float>((float) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Long
                && value <= Float.MAX_VALUE
                && value >= -Float.MAX_VALUE){
            return new ValueCoercionResult<Float>((float) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof BigDecimal ){
            final BigDecimal bd = (BigDecimal) value;
            if(bd.compareTo(-Float.MAX_VALUE) >= 0 && bd.compareTo(Float.MAX_VALUE) <= 1){
                float floatValue = bd.floatValue()
                if(floatValue != Float.NEGATIVE_INFINITY && floatValue != Float.POSITIVE_INFINITY) {
                    return new ValueCoercionResult<Float>(floatValue, Suitability.SUITABLE_WITH_COERCION);
                }
            }

        } else if(value instanceof String){
            try {
                final int num = Float.parseFloat((String) value)
                return new ValueCoercionResult<Float>(num, Suitability.SUITABLE_WITH_COERCION)

            } catch (NumberFormatException e) {}

        } else if(value instanceof GString){
            return coerceToFloat(value.toString())

        }
        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<Float> coerceToFloatPrimitive(final Object value){
        if(value == null){
            return ValueCoercionResult.NOT_SUITABLE
        } else {
            return coerceToFloat(value);
        }
    }

    public static ValueCoercionResult<Integer> coerceToInteger(final Object value){
        if(value == null){
            return new ValueCoercionResult<Integer>(null, Suitability.SUITABLE)

        } else if(value instanceof Integer){
            return new ValueCoercionResult<Integer>(value, Suitability.SUITABLE);

        } else if(value instanceof Short){
            return new ValueCoercionResult<Integer>((int) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Double
                && value <= Integer.MAX_VALUE
                && value >= Integer.MIN_VALUE
                && (((double) value) - ((int) value)) == 0.0d){
            return new ValueCoercionResult<Integer>((int) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Float
                && (((float) value) - ((int) value)) == 0.0d){
            return new ValueCoercionResult<Integer>((int) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Long
                && value <= Integer.MAX_VALUE
                && value >= Integer.MIN_VALUE){
            return new ValueCoercionResult<Integer>((int) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof BigDecimal) {
            final BigDecimal bd = (BigDecimal) value;
            if ((bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0)
                    && bd.compareTo(Integer.MIN_VALUE) >= 0 && bd.compareTo(Integer.MAX_VALUE) <= 1) {
                return new ValueCoercionResult<Integer>(bd.intValue(), Suitability.SUITABLE_WITH_COERCION);
            }

        } else if(value instanceof String){
            try {
                final int num = Integer.parseInt((String) value)
                return new ValueCoercionResult<Integer>(num, Suitability.SUITABLE_WITH_COERCION)

            } catch (NumberFormatException e) {}

        } else if(value instanceof GString){
            return coerceToInteger(value.toString())
        }

        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<Integer> coerceToIntPrimitive(final Object value){
        if(value == null){
            return ValueCoercionResult.NOT_SUITABLE
        } else {
            return coerceToInteger(value);
        }
    }

    public static ValueCoercionResult<Long> coerceToLong(final Object value){
        if(value == null){
            return new ValueCoercionResult<Long>(null, Suitability.SUITABLE)

        } else if(value instanceof Long){
            return new ValueCoercionResult<Long>(value, Suitability.SUITABLE);

        } else if(value instanceof Integer){
            return new ValueCoercionResult<Long>((long) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Short){
            return new ValueCoercionResult<Long>((long) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Double
                && (((double) value) - ((long) value)) == 0.0d){
            return new ValueCoercionResult<Long>((long) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Float
                && (((float) value) - ((long) value)) == 0.0d){
            return new ValueCoercionResult<Long>((long) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof BigDecimal) {
            final BigDecimal bd = (BigDecimal) value;
            if ((bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0)) {
                return new ValueCoercionResult<Long>(bd.longValueExact(), Suitability.SUITABLE_WITH_COERCION);
            }

        } else if(value instanceof String){
            try {
                final int num = Long.parseLong((String) value)
                return new ValueCoercionResult<Long>(num, Suitability.SUITABLE_WITH_COERCION)

            } catch (NumberFormatException e) {}

        } else if(value instanceof GString){
            return coerceToLong(value.toString())
        }

        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<Long> coerceToLongPrimitive(final Object value){
        if(value == null){
            return ValueCoercionResult.NOT_SUITABLE
        } else {
            return coerceToLong(value);
        }
    }

    public static ValueCoercionResult<Short> coerceToShort(final Object value){
        if(value == null){
            return new ValueCoercionResult<Short>(null, Suitability.SUITABLE)

        } else if(value instanceof Short){
            return new ValueCoercionResult<Short>(value, Suitability.SUITABLE);

        } else if(value instanceof Integer
            && value <= Short.MAX_VALUE
            && value >= Short.MIN_VALUE){
            return new ValueCoercionResult<Short>((short) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Short){
            return new ValueCoercionResult<Short>((short) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Double
                && value <= Short.MAX_VALUE
                && value >= Short.MIN_VALUE
                && (((double) value) - ((short) value)) == 0.0d){
            return new ValueCoercionResult<Short>((short) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Float
                && value <= Short.MAX_VALUE
                && value >= Short.MIN_VALUE
                && (((float) value) - ((short) value)) == 0.0d){
            return new ValueCoercionResult<Short>((short) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Long
                && value <= Short.MAX_VALUE
                && value >= Short.MIN_VALUE){
            return new ValueCoercionResult<Short>((short) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof BigDecimal) {
            final BigDecimal bd = (BigDecimal) value;
            if ((bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0)
                    && bd.compareTo(Short.MIN_VALUE) >= 0 && bd.compareTo(Short.MAX_VALUE) <= 1) {
                return new ValueCoercionResult<Short>(bd.shortValueExact(), Suitability.SUITABLE_WITH_COERCION);
            }

        } else if(value instanceof String){
            try {
                final short num = Short.parseShort((String) value)
                return new ValueCoercionResult<Short>(num, Suitability.SUITABLE_WITH_COERCION)

            } catch (NumberFormatException e) {}

        } else if(value instanceof GString){
            return coerceToShort(value.toString())
        }

        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<Short> coerceToShortPrimitive(final Object value){
        if(value == null){
            return ValueCoercionResult.NOT_SUITABLE
        } else {
            return coerceToShort(value);
        }
    }

    public static ValueCoercionResult<BigDecimal> coerceToBigDecimal(final Object value){
        if(value == null){
            return new ValueCoercionResult<BigDecimal>(null, Suitability.SUITABLE)

        } else if(value instanceof BigDecimal){
            return new ValueCoercionResult<BigDecimal>(value, Suitability.SUITABLE);

        } else if(value instanceof Double){
            return new ValueCoercionResult<BigDecimal>(BigDecimal.valueOf(value), Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Integer){
            return new ValueCoercionResult<BigDecimal>(BigDecimal.valueOf((long) value), Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Short){
            return new ValueCoercionResult<BigDecimal>(BigDecimal.valueOf((long) value), Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Float
                && (((float) value) - ((int) value)) == 0.0d){
            //Am putting the conversion of floating point numbers from float to double in the too-hard-basket
            return new ValueCoercionResult<BigDecimal>((double) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof Long){
            return new ValueCoercionResult<BigDecimal>((long) value, Suitability.SUITABLE_WITH_COERCION);

        } else if(value instanceof String){
            try {
                return new ValueCoercionResult<BigDecimal>(new BigDecimal((String) value), Suitability.SUITABLE_WITH_COERCION)

            } catch (NumberFormatException e) {}

        } else if(value instanceof GString){
            return coerceToBigDecimal(value.toString())

        }
        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<Boolean> coerceToBoolean(final Object value){
        if(value == null){
            return new ValueCoercionResult<Boolean>(null, Suitability.SUITABLE)

        } else if(value instanceof Boolean){
            return new ValueCoercionResult<Boolean>(value, Suitability.SUITABLE);


        } else if(value instanceof String){
            if(value.equalsIgnoreCase("true")
                    || value.equalsIgnoreCase("T")
                    || value.equalsIgnoreCase("yes")
                    || value.equalsIgnoreCase("Y")){
                return new ValueCoercionResult<Boolean>(true, Suitability.SUITABLE_WITH_COERCION);
            } else if(value.equalsIgnoreCase("false")
                    || value.equalsIgnoreCase("F")
                    || value.equalsIgnoreCase("no")
                    || value.equalsIgnoreCase("N")){
                return new ValueCoercionResult<Boolean>(false, Suitability.SUITABLE_WITH_COERCION);
            }

        } else if(value instanceof GString){
            return coerceToBoolean(value.toString())
        }

        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<Boolean> coerceToBooleanPrimitive(final Object value){
        if(value == null){
            return ValueCoercionResult.NOT_SUITABLE
        } else {
            return coerceToBoolean(value);
        }
    }

    public static ValueCoercionResult<String> coerceToString(final Object value){
        if(value == null){
            return new ValueCoercionResult<String>(null, Suitability.SUITABLE)

        } else if(value instanceof String){
            return new ValueCoercionResult<String>(value, Suitability.SUITABLE);


        } else if(value instanceof GString) {
            return new ValueCoercionResult<String>(value.toString(), Suitability.SUITABLE_WITH_EXPECTED_COERCION);

        } else if(value instanceof StringBuilder) {
            return new ValueCoercionResult<String>(value.toString(), Suitability.SUITABLE_WITH_COERCION);

        }
        return ValueCoercionResult.NOT_SUITABLE;
    }

    public static ValueCoercionResult<GString> coerceToGString(final Object value){
        if(value == null){
            return new ValueCoercionResult<GString>(null, Suitability.SUITABLE)

        } else if(value instanceof GString){
            return new ValueCoercionResult<GString>(value, Suitability.SUITABLE);

        } else if(value instanceof String){
            return new ValueCoercionResult<GString>("$value", Suitability.SUITABLE_WITH_EXPECTED_COERCION);

        } else if(value instanceof StringBuilder){
            return new ValueCoercionResult<String>("${value}", Suitability.SUITABLE_WITH_COERCION);

        }

        return ValueCoercionResult.NOT_SUITABLE;
    }
}

