package org.tools4j.groovytables

import static org.tools4j.groovytables.GeneratorUtils.writeToFile

/**
 * User: ben
 * Date: 24/03/2016
 * Time: 5:14 PM
 */
class OrDelegateGenerator {
    static void main(String[] args) {
        List<Class> classes = [BigDecimal, Long, Double, Boolean, Integer, Float, Short, Byte, Object];
        final StringBuilder sb = new StringBuilder("package org.tools4j.groovytables\n" +
                "\n" +
                "import static org.tools4j.groovytables.SimpleTableParser.*\n" +
                "\n" +
                "/**\n" +
                " * Inspired by Christian Baranowski's blog post http://tux2323.blogspot.co.uk/2013/04/simple-table-dsl-in-groovy.html \n" +
                " * \n" +
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
        writeToFile("src/generatedmain/groovy/org/tools4j/groovytables/OrDelegate.groovy", sb.toString())
    }
}