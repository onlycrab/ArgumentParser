package com.github.onlycrab.argParser.arguments.exceptions;

import com.github.onlycrab.argParser.arguments.dependencies.Rule;

/**
 * This exception is thrown when an error of value converting has occurred.
 *
 * <p>This is a RuntimeException because you can create a custom rule to check the type of the argument's value
 * if necessary. For example, create a rule {@code IsIntegerRule}. If the argument satisfies the rule, then the method
 * {@code toInt()} will definitely not throw an exception, and it does not need to be handled.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 * @see Rule
 */
@SuppressWarnings("WeakerAccess")
public class ConverterException extends RuntimeException {
    private final String value;
    private final String type;
    private final String explanation;

    public ConverterException(String type, String value) {
        this(type, value, null, null);
    }

    public ConverterException(String type, String value, Throwable cause) {
        this(type, value, null, cause);
    }

    public ConverterException(String type, String value, String explanation) {
        this.value = value;
        this.type = type;
        this.explanation = explanation;
    }

    public ConverterException(String type, String value, String explanation, Throwable cause) {
        super(cause);
        this.value = value;
        this.type = type;
        this.explanation = explanation;
    }

    @Override
    public String getMessage() {
        if (explanation != null) {
            return String.format("Cant convert value <%s> to type <%s> : %s.", value, type, explanation);
        } else {
            return String.format("Cant convert value <%s> to type <%s>.", value, type);
        }
    }
}
