package com.github.onlycrab.argParser.arguments.exceptions;

/**
 * This exception is thrown when an argument is not found for some specified search parameters.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class ArgumentNotFoundException extends RuntimeException {
    private final String keyName;
    private final String keyValue;

    public ArgumentNotFoundException(String keyName, String keyValue) {
        this.keyName = keyName;
        this.keyValue = keyValue;
    }

    @Override
    public String getMessage() {
        return String.format("No argument found by <%s> = <%s>.", keyName, keyValue);
    }
}
