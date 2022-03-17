package com.github.onlycrab.argParser.arguments;

import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentNotFoundException;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * An object that parse array of command line arguments and save it to {@code arguments} object.
 *
 * @author Roman Rynkovich
 * @version 1.0
 * @see Argument
 * @see ArgumentStorage
 */

@SuppressWarnings("WeakerAccess")
public class ArgumentParser {
    /**
     * A pattern to check if a string is a valid argument name
     */
    private static final Pattern pattern = Pattern.compile("(-|--)(([A-Za-z])([A-Za-z]|\\d)*[-_]?)+");

    /**
     * Returns argument name from {@code target}. If {@code target} is not argument name - returns {@code null}.
     *
     * @param target argument name string
     * @return argument name if {@code target} is valid argument name, otherwise returns {@code null}
     */
    private static String parseArgumentName(String target) {
        if (target == null) {
            return null;
        } else if (!pattern.matcher(target).matches()) {
            return null;
        } else {
            String name = target;
            while (name.charAt(0) == '-') {
                name = name.substring(1);
                if (name.length() == 1) {
                    break;
                }
            }
            return name;
        }
    }

    /**
     * Parse argument array.
     *
     * <p>A parsing process is as follows:<br>
     * - If {@code arg} matches with {@code (-|--){1}(([A-Za-z]|\d)+(-|_)?)+}, it will be parsed like a argument name;
     * otherwise it will be parsed like argument value.<br>
     * - If two argument names will be transferred one by one, then first argument will be declared, but the value will
     * be empty.<br>
     * - Argument value must be transferred immediately after argument name, f.e {-argName1, value1, -argName2, value2}.<br>
     * - If two argument values will be transferred one by one, only the last value will be assigned to the argument. IF
     * you want to get an array of values for one argument, pass it in one line with the delimiter ','. Then, after
     * parsing, you can get array by using value converter.</p>
     *
     * @param storage object where the argument data will be written
     * @param args    argument array
     */
    public static void parse(ArgumentStorage storage, String[] args) {
        if (storage == null || args == null) {
            return;
        }
        String argName = null;
        String tmp;
        Argument element;
        for (String arg : args) {
            tmp = parseArgumentName(arg);
            /* If {@code tmp} is not null - it is argument name, sets argument {@code argName} is declared.
            If {@code tmp} is null - it is argument value, sets last argument {@code argName} value. */
            try {
                if (tmp != null) {
                    argName = tmp;
                    try {
                        element = storage.getArgument(argName);
                    } catch (ArgumentNotFoundException e) {
                        element = new Argument(argName, null);
                        storage.add(element);
                    }
                    element.setDeclared(true);
                } else {
                    if (argName != null) {
                        try {
                            element = storage.getArgument(argName);
                        } catch (ArgumentNotFoundException e) {
                            element = new Argument(argName, null);
                            storage.add(element);
                        }
                        element.setValue(arg);
                    }
                }
            } catch (ArgumentException ignored) {
                //ArgumentException will never thrown, because argName is already valid argument name
            }
        }
    }

    /**
     * Parse key-value pairs.
     *
     * @param storage object where the argument data will be written
     * @param pairs   key-value pairs
     * @throws ArgumentException if an error occurred while adding new argument
     */
    public static void parse(ArgumentStorage storage, Map<?, ?> pairs) throws ArgumentException {
        if (storage == null || pairs == null) {
            return;
        }
        Argument arg;
        for (Map.Entry<?, ?> pair : pairs.entrySet()) {
            try {
                arg = storage.getArgument(pair.getKey().toString());
            } catch (ArgumentNotFoundException e) {
                arg = new Argument(pair.getKey().toString(), null);
                storage.add(arg);
            }
            arg.setValue(pair.getValue().toString());
        }
    }
}
