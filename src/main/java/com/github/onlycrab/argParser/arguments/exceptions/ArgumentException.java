package com.github.onlycrab.argParser.arguments.exceptions;

/**
 * The common exception for errors while processing arguments.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class ArgumentException extends Exception {
    public ArgumentException() {
        super();
    }

    public ArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgumentException(String message) {
        super(message);
    }

    public ArgumentException(Throwable cause) {
        super(cause);
    }

}
