package com.github.onlycrab.argParser.arguments.dependencies;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.ArgumentStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom rule for arguments.
 * <p>You can create your own condition that the arguments must fulfill. For do this:<br>
 * - create a class that extends this class and override the {@link Rule#isSatisfied()};<br>
 * - add the arguments that should execute it by the method {@link Rule#addTarget(Argument)};<br>
 * - add rules to storage object by {@link ArgumentStorage#addRule(Rule)};<br>
 * - to check the result call {@link ArgumentStorage#isRulesSatisfied()} after arguments parsing.<br>
 * More info in {@link ArgumentStorage} description (p. 8).</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
@SuppressWarnings("WeakerAccess")
public abstract class Rule {
    /**
     * List of arguments that must satisfy the rule
     */
    protected final List<Argument> targets;

    /**
     * Dissatisfaction info messages. Each rule dissatisfaction report will be logged here
     */
    private final List<String> messages;

    /**
     * Create new rule.
     */
    protected Rule() {
        targets = new ArrayList<>();
        messages = new ArrayList<>();
    }

    /**
     * Add an argument that must satisfy the rule.
     *
     * @param target argument that must satisfy the rule
     * @return a reference to this object
     */
    @SuppressWarnings("unused")
    public final Rule addTarget(Argument target) {
        if (target != null) {
            targets.add(target);
        }
        return this;
    }

    /**
     * Add dissatisfaction message.
     *
     * @param msg dissatisfaction message
     */
    protected final void addMessage(String msg) {
        if (msg != null) {
            messages.add(msg);
        }
    }

    /**
     * Clear dissatisfaction messages
     */
    protected final void clearMessages() {
        messages.clear();
    }

    /**
     * Get dissatisfaction messages as one string.
     *
     * @return dissatisfaction messages as {@link String}
     */
    public final String getMessage() {
        if (messages.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String msg : messages) {
            sb.append(msg).append(" ");
        }
        return sb.toString();
    }

    /**
     * Set dissatisfaction message. Old message will be overwritten.
     *
     * @param msg dissatisfaction message
     */
    @SuppressWarnings("unused")
    protected final void setMessage(String msg) {
        clearMessages();
        addMessage(msg);
    }

    /**
     * Is all arguments linked to this rule satisfy it.
     *
     * @return {@code true} if all arguments linked to this rule satisfy it, otherwise returns {@code false}.
     */
    public abstract boolean isSatisfied();
}
