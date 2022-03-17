package com.github.onlycrab.argParser.arguments.dependencies;

import com.github.onlycrab.argParser.arguments.Argument;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link Requirements}.
 *
 * @author Roman Rynkovich
 */
public class RequirementsTest {
    private static void assertFalse(ReturnedBoolean returned) {
        Assert.assertFalse(returned.getResult());
    }

    private static void assertTrue(ReturnedBoolean returned) {
        Assert.assertTrue(returned.getResult());
    }

    @Test
    public void isCyclicDependencyExists() {
        ArgumentT arg1, arg2, arg3, arg4;
        Requirements req = new Requirements();
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");
            arg3 = new ArgumentT("arg3", "argument3");
            arg4 = new ArgumentT("arg4", "argument4");

            req.addRequirement(arg1, arg2);
            req.addRequirement(arg2, arg3);
            assertFalse(req.isCyclicDependencyExists(arg1, null));
            assertFalse(req.isCyclicDependencyExists(arg2, null));
            req.addRequirement(arg3, arg1);
            assertTrue(req.isCyclicDependencyExists(arg2, null));

            req.addRequirement(arg4, arg2);
            assertTrue(req.isCyclicDependencyExists(arg2, null));
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link Requirements#isRequireFilled(Argument)}.
     */
    @Test
    public void isRequireFilled() {
        ArgumentT arg1, arg2, arg3;
        Requirements req = new Requirements();
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");
            arg3 = new ArgumentT("arg3", "argument3");
            assertTrue(req.isRequireFilled(arg1));

            /*Check declaration condition */
            arg1.setRequiredBeDeclared(true);
            assertFalse(req.isRequireFilled(arg1));
            arg1.setDeclared(true);
            assertTrue(req.isRequireFilled(arg1));

            /*Check filling condition */
            arg1.setRequiredBeFilled(true);
            assertFalse(req.isRequireFilled(arg1));
            arg1.setValue("some value");
            assertTrue(req.isRequireFilled(arg1));

            /*If the argument not declared it is can be not filled. */
            arg1.setRequiredBeDeclared(false);
            arg1.setDeclared(false);
            arg1.setRequiredBeFilled(true);
            arg1.setValue(null);
            assertTrue(req.isRequireFilled(arg1));

            /*Check required link*/
            req.addRequirement(arg2, arg3);
            assertTrue(req.isRequireFilled(arg2));

            arg3.setRequiredBeDeclared(true);
            assertTrue(req.isRequireFilled(arg2));

            arg2.setDeclared(true);
            assertFalse(req.isRequireFilled(arg2));

            arg3.setRequiredBeDeclared(true);
            assertFalse(req.isRequireFilled(arg2));

            arg3.setDeclared(true);
            assertTrue(req.isRequireFilled(arg2));

            arg3.setValue("some value");
            assertTrue(req.isRequireFilled(arg2));
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link Requirements#equals(Object)}.
     */
    @Test
    public void equals() {
        Requirements req1 = new Requirements(), req2 = new Requirements();
        ArgumentT arg1, arg2, arg3;
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");
            arg3 = new ArgumentT("arg3", "argument3");

            req1.addRequirement(arg1, arg2);
            req2.addRequirement(arg1, arg2);
            Assert.assertEquals(req1, req2);

            req1.addRequirement(arg3, arg1);
            req2.addRequirement(arg3, arg1);
            Assert.assertEquals(req1, req2);

            req1 = new Requirements();
            req2 = new Requirements();
            req1.addRequirement(arg1, arg2);
            req2.addRequirement(arg2, arg1);
            Assert.assertNotEquals(req1, req2);
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
