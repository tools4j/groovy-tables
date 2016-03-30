package org.tools4j.groovytables

/**
 * User: ben
 * Date: 24/03/2016
 * Time: 5:29 PM
 */
class GeneratorUtils {
    static void writeToFile(String filePath, String str) {
        final File outputFile = new File(filePath);
        println "Generating file: $outputFile.absolutePath"
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs()
        } else if (outputFile.exists()) {
            outputFile.delete()
        }
        outputFile.write(str)
    }
}
