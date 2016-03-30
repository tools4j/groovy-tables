package org.tools4j.groovytabledsl

/**
 * User: ben
 * Date: 16/02/2016
 * Time: 5:39 PM
 */
class Logger {
    static{
        final String configuredLevelStr = System.getProperty("org.tools4j.groovytabledsl.loggingLevel")
        if(configuredLevelStr != null){
            Level configuredLevel = Level.valueOf(configuredLevelStr)
            setCurrentLevel(configuredLevel)
        }
    }

    static enum Level{
        DEBUG(0),
        INFO(1),
        WARNING(2),
        ERROR(3);

        final int levelIndex;

        Level(final int levelIndex){
            this.levelIndex = levelIndex
        }

        boolean isLowerThanOrEqualTo(final Level level){
            return levelIndex <= level.levelIndex
        }
    }

    static Level currentLevel = Level.WARNING

    static debug(final String message){
        if(currentLevel.isLowerThanOrEqualTo(Level.DEBUG)){
            println "groovytabledsl DEBUG:" + message;
        }
    }
    static info(final String message){
        if(currentLevel.isLowerThanOrEqualTo(Level.INFO)){
            println "groovytabledsl INFO:" + message;
        }
    }
    static warning(final String message){
        if(currentLevel.isLowerThanOrEqualTo(Level.WARNING)){
            println "groovytabledsl WARN:" + message;
        }
    }
    static error(final String message){
        if(currentLevel.isLowerThanOrEqualTo(Level.ERROR)){
            println "groovytabledsl ERROR:" + message;
        }
    }
}
