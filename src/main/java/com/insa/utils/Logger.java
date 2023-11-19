package com.insa.utils;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

public class Logger {
    private static volatile Logger instance;

    private static java.util.logging.Logger myLog = null;

    private final UUID sessionUUID;

    private Logger() {
        this.sessionUUID = UUID.randomUUID();
        myLog = java.util.logging.Logger.getAnonymousLogger();
        if(instance != null) {
            throw new RuntimeException("Logger already instanced");
        }
        this.log("Logger created", Level.INFO);
    }

    synchronized static public Logger getInstance() {
        Logger result = instance;
        if(result != null) {
            return result;
        }
        synchronized (Logger.class) {
            if(instance == null) {
                instance = new Logger();
            }
            return instance;
        }
    }

    public void log(String message) {
        this.log(message, Level.INFO);
    }

    public void log(String message, Level level) {
        myLog.log(level, message + "\n");
    }
}
