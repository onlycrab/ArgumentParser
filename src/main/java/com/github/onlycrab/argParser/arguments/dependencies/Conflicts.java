package com.github.onlycrab.argParser.arguments.dependencies;

import com.github.onlycrab.argParser.arguments.Argument;

import java.util.ArrayList;
import java.util.List;

/**
 * Arguments conflict management class.
 * See the description of the {@link Argument} class for information about argument restriction conditions.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class Conflicts {
    private List<Argument[]> confLinks;

    /**
     * Create new conflicts management object.
     */
    public Conflicts() {
        confLinks = new ArrayList<>();
    }

    /**
     * Removes all conflicts.
     */
    public void clear() {
        confLinks = new ArrayList<>();
    }

    /**
     * Add a conflict between two arguments.
     *
     * @param first  argument
     * @param second argument
     */
    public void addConflict(Argument first, Argument second) {
        if (first == null || second == null) {
            return;
        } else if (first == second) {
            return;
        }
        boolean isDuplicate = false;
        for (Argument[] pair : confLinks) {
            if ((first == pair[0] && second == pair[1]) || (first == pair[1] && second == pair[0])) {
                isDuplicate = true;
                break;
            }
        }
        if (!isDuplicate) {
            confLinks.add(new Argument[]{first, second});
        }
    }

    /**
     * Check is this argument in conflict with another arguments.
     *
     * @param arg argument to check
     * @return if conflicts founded - instance of {@code ReturnedBoolean} object with result
     * {@code true}, else - with result {@code false}
     * @see ReturnedBoolean
     */
    public ReturnedBoolean isInConflict(Argument arg) {
        return isInConflict(arg, null);
    }

    /**
     * Check is this argument in conflict with another arguments.
     *
     * @param arg      argument to check
     * @param returned instance of {@code ReturnedBoolean} object
     * @return if conflicts founded - instance of {@code ReturnedBoolean} object with result
     * {@code true}, else - with result {@code false}
     * @see ReturnedBoolean
     */
    public ReturnedBoolean isInConflict(Argument arg, ReturnedBoolean returned) {
        if (returned == null) {
            returned = new ReturnedBoolean(false);
        } else if (arg == null) {
            return returned;
        }

        if (arg.isDeclared() || arg.isFilled()) {
            for (Argument[] pair : confLinks) {
                //If pair is already processed - skip
                if (returned.isAlreadyProcessed(pair)) {
                    continue;
                }
                if (arg == pair[0]) {
                    if (pair[1].isDeclared() || pair[1].isFilled()) {
                        returned.setResult(pair, true);
                        returned.addMessage(String.format("Parameter %s is in conflict with %s.",
                                arg.getName(), pair[1].getName()));
                    }
                } else if (arg == pair[1]) {
                    if (pair[0].isDeclared() || pair[0].isFilled()) {
                        returned.setResult(pair, true);
                        returned.addMessage(String.format("Parameter %s is in conflict with %s.",
                                arg.getName(), pair[1].getName()));
                    }
                }
            }
        }

        return returned;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof Conflicts)) {
            return false;
        }
        Conflicts another = (Conflicts) obj;
        if (confLinks.size() != another.confLinks.size()) {
            return false;
        }
        return contains(another) && another.contains(this);
    }

    /**
     * Is this object contains all conflict links from {@code test}.
     *
     * @param test conflicts object
     * @return {@code true} if each conflict of {@code test} has equivalent conflict in this object,
     * otherwise returns {@code false}.
     */
    private boolean contains(Conflicts test) {
        if (test == null) {
            return false;
        }
        boolean isEquals;
        for (Argument[] testLink : test.confLinks) {
            isEquals = false;
            for (Argument[] thisLink : confLinks) {
                if ((testLink[0].equals(thisLink[0]) && testLink[1].equals(thisLink[1])) ||
                        (testLink[0].equals(thisLink[1]) && testLink[1].equals(thisLink[0]))) {
                    isEquals = true;
                    break;
                }
            }
            if (!isEquals) {
                return false;
            }
        }
        return true;
    }
}
