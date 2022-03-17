package com.github.onlycrab.argParser.arguments.dependencies;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that stores result of some operation and message explaining its
 * meaning. It also stores a list of objects on which the operation has already
 * been performed to avoid re-execution.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
@SuppressWarnings("UnusedReturnValue")
public class ReturnedBoolean {
    /**
     * The list of some explanatory messages (f.e. error message)
     */
    private final List<String> msg;

    /**
     * The list of objects on which the operation has already been performed
     */
    private final List<Object> returnees;

    /**
     * Flag that that describes the success of some operation
     */
    private boolean result;

    /**
     * Create new {@code ReturnedBoolean} object.
     *
     * @param resultDefault value of the original result
     */
    public ReturnedBoolean(boolean resultDefault) {
        this.result = resultDefault;
        msg = new ArrayList<>();
        returnees = new ArrayList<>();
    }

    /**
     * Returns the boolean representation of the result.
     *
     * @return the boolean representation of the result
     */
    public boolean getResult() {
        return result;
    }

    /**
     * Returns all explanatory messages as one {@code String} object.
     *
     * @return explanatory messages as {@code String}
     */
    public String getMessage() {
        StringBuilder builder = new StringBuilder();
        if (msg.size() == 1) {
            builder.append(msg.get(0));
        } else if (msg.size() > 1) {
            for (String value : msg) {
                builder.append(value).append(" ");
            }
        }
        return builder.toString();
    }

    /**
     * Add result to original result value.
     * After this call returns the original result will be:
     * {@code true} if original result is {@code true} and value is {@code true};
     * {@code false} if original result is {@code false} or value is {@code false}.
     *
     * @param returnees the object that performed some operation
     * @param value     boolean representation of the operation result
     * @return a reference to this object
     */
    public ReturnedBoolean addResult(@Nullable Object returnees, boolean value) {
        if (returnees != null) {
            this.returnees.add(returnees);
        }
        result = result && value;
        return this;
    }

    /**
     * Set result to original result value.
     *
     * @param returnees the object that performed some operation
     * @param value     boolean representation of the operation result
     * @return a reference to this object
     */
    public ReturnedBoolean setResult(@Nullable Object returnees, boolean value) {
        if (returnees != null) {
            this.returnees.add(returnees);
        }
        result = value;
        return this;
    }

    /**
     * Add explanatory message.
     *
     * @param message some explanatory message
     * @return a reference to this object
     */
    public ReturnedBoolean addMessage(String message) {
        if (message != null) {
            msg.add(message);
        }
        return this;
    }

    /**
     * Returns {@code true} if {@code obj} has already been processed.
     *
     * @param obj object whose processing is to be tested
     * @return {@code true} if {@code obj} has already been processed
     */
    public boolean isAlreadyProcessed(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }
        for (Object check : returnees) {
            if (check.equals(obj)) {
                return true;
            }
        }
        return false;
    }
}
