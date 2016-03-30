package org.tools4j.groovytabledsl

import static org.tools4j.groovytabledsl.GeneratorUtils.writeToFile

/**
 * User: ben
 * Date: 24/03/2016
 * Time: 5:14 PM
 */
class OrDelegateGenerator {
    static void main(String[] args) {
        List<Class> classes = [BigDecimal, Long, Double, Boolean, Integer, Float, Short, Byte, Object];
        final StringBuilder sb = new StringBuilder("package org.tools4j.groovytabledsl\n" +
                "\n" +
                "import static org.tools4j.groovytabledsl.SimpleTableParser.*\n" +
                "\n" +
                "/**\n" +
                " * GENERATED CLASS, do not update directly\n" +
                " */\n" +
                "class OrDelegate {\n");

        for (final Class class1 : classes) {
            for (final Class class2 : classes) {
                sb.append("    public static Row or($class1.simpleName argOne, $class2.simpleName argTwo) {\n" +
                        "        appendNewRow(argOne, argTwo)\n" +
                        "    }\n\n");
            }
        }

        sb.append("}")
        writeToFile("src/generatedmain/groovy/org/tools4j/groovytabledsl/OrDelegate.groovy", sb.toString())
    }
}