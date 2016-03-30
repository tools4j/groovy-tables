package org.tools4j.groovytables

import static org.tools4j.groovytables.GeneratorUtils.writeToFile

/**
 * User: ben
 * Date: 9/02/2016
 * Time: 5:35 PM
 */
class TestClassGenerator {
    private static Map<Class, String> classesAndDeclarations = [
            Integer      : "Integer.MAX_VALUE",
            BigDecimal   : "BigDecimal.valueOf(1.9)",
            Double       : "Double.MAX_VALUE",
            Short        : "Short.MAX_VALUE",
            Float        : "Float.MAX_VALUE",
            Byte         : "Byte.MAX_VALUE",
            Boolean      : "true",
            SimpleClass  : "new SimpleClass(\"hi\")",
            Long         : "Long.MAX_VALUE",
            GString      : "\"gString\${}\"",    //Put a useless ${} in the string to force it into a GString
            String       : "new String(\"str\")",
            Character    : "'c'"
    ]

    static void main(String[] args) {
        generateSimpleTableParserFirstTwoArgsTest()
        generateSimpleTableParserSubsequentTwoArgsTest()
        generateSimpleTableParserSubsequentTwoArrayArgsTest()
    }

    static void generateSimpleTableParserFirstTwoArgsTest() {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder tableSb = new StringBuilder();
        final StringBuilder assertSb = new StringBuilder();
        int line = 0
        for (final Map.Entry<Class, String> classAndDeclaration1 : classesAndDeclarations) {
            for (final Map.Entry<Class, String> classAndDeclaration2 : classesAndDeclarations) {
                tableSb.append("        ").append(classAndDeclaration1.value).append(" | ").append(classAndDeclaration2.value).append("\n")
                assertSb.append("        assertActualEqualsExpected(listOfArrays.get($line), [$classAndDeclaration1.value, $classAndDeclaration2.value]  as Object[])\n")
                line++
            }
        }

        sb.append("package org.tools4j.groovytables\n" +
                "\n" +
                "import spock.lang.Specification\n" +
                "import org.tools4j.groovytables.model.SimpleClass\n" +
                "\n" +
                "import static org.tools4j.groovytables.TestUtils.assertActualEqualsExpected\n" +
                "\n" +
                "\n" +
                "class SimpleTableParserFirstTwoArgsTest extends Specification {\n" +
                "    public void \"test asListOfArrays two first elements of differing types\"() {\n" +
                "        when:\n" +
                "        final List<Object[]> listOfArrays = SimpleTableParser.asListOfArrays {\n");

        sb.append(tableSb.toString())
        sb.append("        }\n\n        then:\n")
        sb.append(assertSb.toString())
        sb.append("    }\n}");
        writeToFile("src/generatedtest/groovy/org/tools4j/groovytables/SimpleTableParserFirstTwoArgsTest.groovy", sb.toString())
    }


    static void generateSimpleTableParserSubsequentTwoArgsTest() {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder tableSb = new StringBuilder();
        final StringBuilder assertSb = new StringBuilder();
        int line = 0
        for (final Map.Entry<Class, String> classAndDeclaration1 : classesAndDeclarations) {
            for (final Map.Entry<Class, String> classAndDeclaration2 : classesAndDeclarations) {
                tableSb.append("        \"blah!\" | ").append(classAndDeclaration1.value).append(" | ").append(classAndDeclaration2.value).append("\n")
                assertSb.append("        assertActualEqualsExpected(listOfArrays.get($line), [\"blah!\", $classAndDeclaration1.value, $classAndDeclaration2.value]  as Object[])\n")
                line++
            }
        }

        sb.append("package org.tools4j.groovytables\n" +
                "\n" +
                "import spock.lang.Specification\n" +
                "import org.tools4j.groovytables.model.SimpleClass\n" +
                "\n" +
                "import static org.tools4j.groovytables.TestUtils.assertActualEqualsExpected\n" +
                "\n" +
                "\n" +
                "class SimpleTableParserSubsequentTwoArgsTest extends Specification {\n" +
                "    public void \"test asListOfArrays two subsequent elements of differing types\"() {\n" +
                "        when:\n" +
                "        final List<Object[]> listOfArrays = SimpleTableParser.asListOfArrays {\n");

        sb.append(tableSb.toString())
        sb.append("        }\n\n        then:\n")
        sb.append(assertSb.toString())
        sb.append("    }\n}");
        writeToFile("src/generatedtest/groovy/org/tools4j/groovytables/SimpleTableParserSubsequentTwoArgsTest.groovy", sb.toString())
    }


    static void generateSimpleTableParserSubsequentTwoArrayArgsTest() {
        [ Integer.MAX_VALUE, Integer.MAX_VALUE ] as int[]

        final StringBuilder sb = new StringBuilder();
        final StringBuilder arraysSb = new StringBuilder();
        final StringBuilder tableSb = new StringBuilder();
        final StringBuilder assertSb = new StringBuilder();
        int line = 0

        for (final Map.Entry<Class, String> classAndDeclaration : classesAndDeclarations) {
            arraysSb.append("    $classAndDeclaration.key[] arrayOf$classAndDeclaration.key = [ $classAndDeclaration.value, $classAndDeclaration.value, $classAndDeclaration.value ] as $classAndDeclaration.key[]\n")
        }

        for (final Map.Entry<Class, String> classAndDeclaration1 : classesAndDeclarations) {
            for (final Map.Entry<Class, String> classAndDeclaration2 : classesAndDeclarations) {
                tableSb.append("        \"blah!\" | ").append("arrayOf$classAndDeclaration1.key").append(" | ").append("arrayOf$classAndDeclaration2.key").append("\n")
                assertSb.append("        assertActualEqualsExpected(listOfArrays.get($line), [\"blah!\", arrayOf$classAndDeclaration1.key, arrayOf$classAndDeclaration2.key]  as Object[])\n")
                line++
            }
        }

        sb.append("package org.tools4j.groovytables\n" +
                "\n" +
                "import spock.lang.Specification\n" +
                "import org.tools4j.groovytables.model.SimpleClass\n" +
                "\n" +
                "import static org.tools4j.groovytables.TestUtils.assertActualEqualsExpected\n" +
                "\n" +
                "\n" +
                "class SimpleTableParserSubsequentTwoArrayArgsTest extends Specification {\n" +
                "    public void \"test asListOfArrays two subsequent elements of differing array types\"() {\n")

        sb.append("    given:\n")
        sb.append(arraysSb.toString()).append("\n")
        sb.append("        when:\n" +
                "        final List<Object[]> listOfArrays = SimpleTableParser.asListOfArrays {\n");
        sb.append(tableSb.toString())
        sb.append("        }\n\n        then:\n")
        sb.append(assertSb.toString())
        sb.append("    }\n}");
        writeToFile("src/generatedtest/groovy/org/tools4j/groovytables/SimpleTableParserSubsequentTwoArrayArgsTest.groovy", sb.toString())
    }
}
