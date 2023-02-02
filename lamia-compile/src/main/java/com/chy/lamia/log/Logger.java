package com.chy.lamia.log;


public class Logger {

    static LamiaSimpleLogger lamiaSimpleLogger = new LamiaSimpleLogger();

    public static void log(String txt) {
        lamiaSimpleLogger.log(txt);
    }

    public static void throwableLog(Throwable throwable) {
        lamiaSimpleLogger.throwableLog(throwable);
    }

    public static void push() {
        lamiaSimpleLogger.push();
    }
}
