package com.github.onlycrab.argParser.arguments.dependencies;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link Conflicts}.
 *
 * @author Roman Rynkovich
 */
public class ConflictsTest {
    /**
     * Test {@link Conflicts#isInConflict(Argument)}.
     */
    @Test
    public void isInConflict() {
        ArgumentT arg1, arg2;
        Conflicts conf = new Conflicts();
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");

            conf.addConflict(arg1, arg2);
            Assert.assertFalse(conf.isInConflict(arg1).getResult());

            arg2.setDeclared(true);
            Assert.assertFalse(conf.isInConflict(arg1).getResult());

            arg1.setDeclared(true);
            Assert.assertTrue(conf.isInConflict(arg1).getResult());

        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link Conflicts#equals(Object)}.
     */
    @Test
    public void equals() {
        Conflicts conf1 = new Conflicts(), conf2 = new Conflicts();
        Argument arg1, arg2, arg3;
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");
            arg3 = new ArgumentT("arg3", "argument3");

            conf1.addConflict(arg1, arg2);
            conf2.addConflict(arg2, arg3);
            Assert.assertNotEquals(conf1, conf2);

            conf2 = new Conflicts();
            conf2.addConflict(arg1, arg2);
            Assert.assertEquals(conf1, conf2);

            conf2 = new Conflicts();
            conf2.addConflict(arg2, arg1);
            Assert.assertEquals(conf1, conf2);
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * This class was created to be able to use methods directly during testing.
     */
    @SuppressWarnings("EmptyMethod")
    private class ArgumentT extends Argument {
        ArgumentT(String shortName, String longName) throws ArgumentException {
            super(shortName, longName);
        }

        @Override
        protected Argument setDeclared(boolean value) {
            return super.setDeclared(value);
        }

        @Override
        protected void setValue(String value) {
            super.setValue(value);
        }
    }
}
