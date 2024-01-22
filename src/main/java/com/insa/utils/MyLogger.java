package com.insa.utils;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;

/**
 * Custom logger class
 */
public class MyLogger extends Logger {

    /**
     * Constructor
     * @param className name of the class to log
     */
    public MyLogger(String className) {
        super(className, null);

        // Set custom formatter
        ConsoleHandler handler = new ConsoleHandler();
        Formatter formatter = new LogFormatter();
        handler.setFormatter(formatter);
        this.addHandler(handler);
        this.setUseParentHandlers(false);
    }
}
