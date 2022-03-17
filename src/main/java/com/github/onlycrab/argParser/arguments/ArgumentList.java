package com.github.onlycrab.argParser.arguments;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An object that maps argument keys to argument objects and stores it as a list; cant contain duplicate keys;
 * each key can map only one argument.
 *
 * <p>The following rules apply for any two arguments in list:<br>
 * - key is a pair of <b>short</b> and <b>long</b> names (each key pair is stored inside the argument);<br>
 * - any argument can be uniquely identified by only short <b>or</b> only long name or names pair;<br>
 * - short and long names are unique for all arguments in map;<br>
 * - short name cant be equals to any long name in map, long name cant be equals to any short name in map.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class ArgumentList extends ArrayList<Argument> {
    /**
     * Returns {@code true} if this list contains an element with the specified key.
     *
     * @param obj object with key whose presence in this map is to be tested
     * @return {@code true} if this list contains an element with the specified key
     */
    public boolean containsKey(Object obj) {
        if (obj == null) {
            return false;
        }
        for (Argument arg : this) {
            if (arg.isKeyEquals(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the argument by the specified key, or {@code null} if argument by key is not found.
     *
     * @param key the key whose associated argument is to be returned
     * @return the argument by the specified key, or {@code null} if argument by key is not found
     */
    public Argument get(String key) {
        if (key == null) {
            return null;
        }
        for (Argument arg : this) {
            if (arg.isKeyEquals(key)) {
                return arg;
            }
        }
        return null;
    }

    @Override
    public Argument set(int index, Argument element) {
        if (get(index).isKeyEquals(element)) {
            return super.set(index, element);
        } else if (!containsKey(element)) {
            return super.set(index, element);
        } else {
            return null;
        }
    }

    @Override
    public boolean add(Argument argument) {
        if (argument == null) {
            return false;
        }
        for (Argument arg : this) {
            if (arg.isKeyEquals(argument)) {
                return arg.absorb(argument);
            }
        }
        return super.add(argument);
    }

    @Override
    public void add(int index, Argument element) {
        if (!containsKey(element)) {
            super.add(index, element);
        } else {
            Argument arg = get(index);
            if (arg.isKeyEquals(element)) {
                arg.absorb(element);
            }
        }
    }

    @Override
    public boolean addAll(Collection<? extends Argument> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Argument> c) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (!(obj instanceof ArgumentList)) {
            return false;
        }
        ArgumentList another = (ArgumentList) obj;
        if (this.size() != another.size()) {
            return false;
        }
        return contains(another) && another.contains(this);
    }


    /**
     * Is this list contains all elements of list {@code test}.
     *
     * @param testList list
     * @return {@code true} if each element of {@code test} has equals element in this list,
     * otherwise returns {@code false}.
     */
    private boolean contains(ArgumentList testList) {
        if (testList == null) {
            return false;
        }
        boolean isEquals;
        for (Argument testArg : testList) {
            isEquals = false;
            for (Argument thisArg : this) {
                if (testArg.equals(thisArg)) {
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
