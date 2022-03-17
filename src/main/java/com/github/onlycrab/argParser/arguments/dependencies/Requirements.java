package com.github.onlycrab.argParser.arguments.dependencies;

import com.github.onlycrab.argParser.arguments.Argument;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Argument requirements(dependencies) management class.
 * See the description of the {@link Argument} class for information about argument restriction conditions.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class Requirements {
    private final Map<Argument, List<Argument>> reqLinks;

    /**
     * Create new requirements management object.
     */
    public Requirements() {
        reqLinks = new HashMap<>();
    }

    /**
     * Removes all requirements.
     */
    public void clear() {
        reqLinks.clear();
    }

    /**
     * Add requirement of one argument on another.
     *
     * @param req dependent argument
     * @param on  argument on which depends
     */
    public void addRequirement(Argument req, Argument on) {
        if (req == null || on == null) {
            return;
        } else if (req == on) {
            return;
        }
        if (reqLinks.containsKey(req)) {
            reqLinks.get(req).add(on);
        } else {
            List<Argument> links = new ArrayList<>();
            links.add(on);
            reqLinks.put(req, links);
        }
    }

    /**
     * Check for cyclic dependency of arguments.
     *
     * @param arg      argument to check
     * @param returned instance of {@code ReturnedBoolean} object
     * @return instance of {@code ReturnedBoolean} with result {@code false} if no one cyclic dependency
     * found (start from {@code arg}), otherwise with result {@code true}.
     * @see ReturnedBoolean
     */
    public ReturnedBoolean isCyclicDependencyExists(Argument arg, ReturnedBoolean returned) {
        return isCyclicDependencyExists(arg, returned, null);
    }

    /**
     * Check for cyclic dependency of arguments.
     *
     * @param arg      argument to check
     * @param returned instance of {@code ReturnedBoolean} object
     * @param chain    string representation of dependency chain
     * @return instance of {@code ReturnedBoolean} with result {@code false} if no one cyclic dependency
     * found (start from {@code arg}), otherwise with result {@code true}.
     * @see ReturnedBoolean
     */
    private ReturnedBoolean isCyclicDependencyExists(Argument arg, ReturnedBoolean returned, String chain) {
        if (returned == null) {
            returned = new ReturnedBoolean(false);
        } else if (arg == null) {
            return returned;
        } else if (returned.getResult()) {
            return returned;
        }

        if (chain == null) {
            chain = arg.getName();
        } else {
            chain = String.format("%s -> %s", chain, arg.getName());
        }

        if (returned.isAlreadyProcessed(arg)) {
            returned.setResult(null, true);
            returned.addMessage("Cyclic dependency of arguments was been detected : " + chain + ".");
            return returned;
        }

        returned.setResult(arg, false);

        if (reqLinks.containsKey(arg)) {
            for (Argument also : reqLinks.get(arg)) {
                returned = isCyclicDependencyExists(also, returned, chain);
            }
        }

        return returned;
    }

    /**
     * Check is the argument declared (if {@code isRequiredDeclared} is {@code true}) and
     * value filled (if {@code isRequiredFilled} is {@code true}), and check all arguments
     * from dependency list.
     *
     * @param arg argument to be checked
     * @return if check passed - instance of {@code ReturnedBoolean} with result {@code true},
     * else - with result {@code false}
     * @see ReturnedBoolean
     */
    public ReturnedBoolean isRequireFilled(Argument arg) {
        return isRequireFilled(arg, null);
    }

    /**
     * Check is the argument declared (if {@code isRequiredDeclared} is {@code true}) and
     * value filled (if {@code isRequiredFilled} is {@code true}), and check all arguments
     * from dependency list.
     *
     * @param arg      argument to be checked
     * @param returned instance of {@code ReturnedBoolean} object
     * @return if check passed - instance of {@code ReturnedBoolean} with result {@code true},
     * else - with result {@code false}
     * @see ReturnedBoolean
     */
    public ReturnedBoolean isRequireFilled(Argument arg, @Nullable ReturnedBoolean returned) {
        if (returned == null) {
            returned = new ReturnedBoolean(true);
        } else if (arg == null) {
            return returned;
        } else if (returned.isAlreadyProcessed(arg)) {
            return returned;
        }

        List<Argument> links;
        if (reqLinks.containsKey(arg)) {
            links = reqLinks.get(arg);
        } else {
            links = new ArrayList<>();
        }

        if (arg.isRequiredBeFilled() || arg.isRequiredBeDeclared()) {
            //If there is any condition on the argument

            if (arg.isRequiredBeFilled() && arg.isRequiredBeDeclared()) {
                //If the argument must be declared and filled
                if (arg.isFilled() && arg.isDeclared()) {
                    returned.addResult(null, true);
                } else {
                    returned.addResult(null, false);
                    if (!arg.isDeclared()) {
                        returned.addMessage(String.format("Parameter %s is missing.",
                                arg.getName()));
                    } else {
                        returned.addMessage(String.format("Parameter %s value is missing.",
                                arg.getName()));
                    }
                }
            } else if (arg.isRequiredBeFilled() && !arg.isRequiredBeDeclared()) {
                /*If the argument must be filled IF IT IS DECLARED.
                If the argument not declared it is can be not filled. */
                if (arg.isDeclared()) {
                    if (arg.isFilled()) {
                        returned.addResult(null, true);
                    } else {
                        returned.addResult(null, false);
                        returned.addMessage(String.format("Parameter %s value is missing.",
                                arg.getName()));
                    }
                }
            } else {
                //If the argument must be declared
                if (arg.isDeclared()) {
                    returned.addResult(null, true);
                } else {
                    returned.addResult(null, false);
                    returned.addMessage(String.format("Parameter %s is missing.",
                            arg.getName()));
                }
            }

            if (arg.isDeclared() || arg.isFilled()) {
                for (Argument also : links) {
                    if (!returned.isAlreadyProcessed(also)) {
                        returned = isDependencyRequireFilled(also, arg, returned);
                    }
                }
            }
        } else {
            if (arg.isDeclared() || arg.isFilled()) {
                //If argument is not required, but is declared - check is filled all argument from alsoRequired list
                for (Argument also : links) {
                    if (!returned.isAlreadyProcessed(also)) {
                        returned = isDependencyRequireFilled(also, arg, returned);
                    }
                }
            } else {
                //If argument is not required and is not declared - check passed
                returned.addResult(this, true);
            }
        }

        return returned;
    }

    /**
     * Check argument {@code check} for filling like an element of dependency chain for {@code root}.
     * In this case argument {@code check} should be declared or filled regardless of it is {@code isRequireBeFilled}
     * or {@code isRequireBeDeclared}.
     *
     * @param check    argument to be checked
     * @param root     argument dependent on {@code check}
     * @param returned instance of {@code ReturnedBoolean} object
     * @return if check passed - instance of {@code ReturnedBoolean} with result {@code true},
     * else - with result {@code false}
     * @see ReturnedBoolean
     */
    private ReturnedBoolean isDependencyRequireFilled(Argument check, Argument root, ReturnedBoolean returned) {
        /*If root is not null, then argument should be declared or filled.
         Such a condition is imposed by checking another argument that depends on the current one. */
        if (returned == null) {
            returned = new ReturnedBoolean(true);
        } else if (check == null || root == null) {
            return returned;
        }
        if (!check.isFilled() && !check.isDeclared()) {
            returned.addResult(null, false);
            returned.addMessage(String.format("Parameter %s must be declared for the dependent argument %s.",
                    check.getName(), root.getName()));
        } else if (check.isRequiredBeFilled() && !check.isFilled()) {
            returned.addResult(null, false);
            returned.addMessage(String.format("Parameter %s must be filled for the dependent argument %s.",
                    check.getName(), root.getName()));
        } else if (check.isRequiredBeDeclared() && !check.isDeclared()) {
            returned.addResult(null, false);
            returned.addMessage(String.format("Parameter %s must be declared for the dependent argument %s.",
                    check.getName(), root.getName()));
        } else {
            returned.addResult(null, true);
        }
        return returned;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof Requirements)) {
            return false;
        }
        Requirements another = (Requirements) obj;
        if (reqLinks.size() != another.reqLinks.size()) {
            return false;
        }
        return contains(another) && another.contains(this);
    }

    /**
     * Is this object contains all requirement links from {@code test}.
     *
     * @param test requirements object
     * @return {@code true} if each requirement of {@code test} has equivalent requirement in this object,
     * otherwise returns {@code false}.
     */
    private boolean contains(Requirements test) {
        if (test == null) {
            return false;
        }
        boolean isEquals;
        for (Argument testKey : test.reqLinks.keySet()) {
            if (!reqLinks.containsKey(testKey)) {
                return false;
            }
            for (Argument testArg : test.reqLinks.get(testKey)) {
                isEquals = false;
                for (Argument thisArg : reqLinks.get(testKey)) {
                    if (testArg.equals(thisArg)) {
                        isEquals = true;
                        break;
                    }
                }
                if (!isEquals) {
                    return false;
                }
            }
        }
        return true;
    }
}
