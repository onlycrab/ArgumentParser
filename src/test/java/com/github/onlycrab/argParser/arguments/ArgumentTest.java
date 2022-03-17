package com.github.onlycrab.argParser.arguments;

import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link Argument}.
 *
 * @author Roman Rynkovich
 */
public class ArgumentTest {
    /**
     * Test {@link Argument#isDeclared()}.
     */
    @Test
    public void isDeclared() {
        try {
            ArgumentT arg = new ArgumentT("arg1", "argument1");
            Assert.assertFalse(arg.isDeclared());
            arg.setDeclared(true);
            Assert.assertTrue(arg.isDeclared());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link Argument#isFilled()}.
     */
    @Test
    public void isFilled() {
        try {
            ArgumentT arg = new ArgumentT("arg", "argument");
            Assert.assertFalse(arg.isFilled());
            arg.setValue("some value");
            Assert.assertTrue(arg.isFilled());
            arg.setValue("");
            Assert.assertFalse(arg.isFilled());
            arg.setValue(null);
            Assert.assertFalse(arg.isFilled());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link Argument#isKeyEquals(Object)}.
     */
    @Test
    public void isKeyEquals() {
        Argument arg1, arg2;
        try {
            arg1 = new Argument("a1", "arg1");

            arg2 = new Argument("a2", "arg2");
            Assert.assertFalse(arg1.isKeyEquals(arg2));

            arg2 = new Argument("a2", "arg1");
            Assert.assertTrue(arg1.isKeyEquals(arg2));

            arg2 = new Argument("a1", "arg2");
            Assert.assertTrue(arg1.isKeyEquals(arg2));

            arg2 = new Argument("a1", "arg1");
            Assert.assertTrue(arg1.isKeyEquals(arg2));

            arg2 = new Argument("a1", null);
            Assert.assertTrue(arg1.isKeyEquals(arg2));

            arg1 = new Argument("a1", null);
            arg2 = new Argument("a2", "arg2");
            Assert.assertFalse(arg1.isKeyEquals(arg2));

            arg2 = new Argument("a2", null);
            Assert.assertFalse(arg1.isKeyEquals(arg2));

            arg2 = new Argument("a1", null);
            Assert.assertTrue(arg1.isKeyEquals(arg2));

        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link Argument#absorb(Argument)}.
     */
    @Test
    public void absorb() {
        ArgumentT arg1, arg2;
        try {
            arg1 = new ArgumentT(
                    "a1",
                    "arg1",
                    "valDef1",
                    false,
                    false,
                    "param1",
                    "desc1",
                    "descDetail1"
            );
            arg2 = new ArgumentT("a2", "arg2");
            Assert.assertFalse(arg1.absorb(arg2));

            arg2 = new ArgumentT(
                    "a1",
                    null,
                    "valDef2",
                    true,
                    true,
                    "param2",
                    "desc2",
                    "descDetail2"
            );

            arg1.setValue("val1");
            arg1.setDeclared(true);
            arg2.setValue("val2");
            arg2.setDeclared(false);
            Assert.assertTrue(arg1.absorb(arg2));

            Assert.assertEquals("val1", arg1.getValue());
            Assert.assertTrue(arg1.isDeclared());
            Assert.assertEquals("valDef2", arg1.getValueDefault());
            Assert.assertTrue(arg1.isRequiredBeDeclared());
            Assert.assertTrue(arg1.isRequiredBeFilled());
            Assert.assertEquals("param2", arg1.getParameters());
            Assert.assertEquals("desc2", arg1.getDescription());
            Assert.assertEquals("descDetail2", arg1.getDescriptionDetailed());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link Argument#equals(Object)}.
     */
    @Test
    public void equals() {
        try {
            ArgumentT arg1, arg2;
            arg1 = new ArgumentT("a", "arg");

            arg2 = new ArgumentT("a", "arg2");
            Assert.assertNotEquals(arg1, arg2);

            arg2 = new ArgumentT("a2", "arg");
            Assert.assertNotEquals(arg1, arg2);

            arg2 = new ArgumentT("a", "arg");
            Assert.assertEquals(arg1, arg2);

            arg2.setValueDefault("vd");
            Assert.assertNotEquals(arg1, arg2);
            arg1.setValueDefault("vd");
            Assert.assertEquals(arg1, arg2);

            arg2.setRequiredBeDeclared(true);
            Assert.assertNotEquals(arg1, arg2);
            arg1.setRequiredBeDeclared(true);
            Assert.assertEquals(arg1, arg2);

            arg2.setRequiredBeFilled(true);
            Assert.assertNotEquals(arg1, arg2);
            arg1.setRequiredBeFilled(true);
            Assert.assertEquals(arg1, arg2);

            arg2.setDescription("desc");
            Assert.assertNotEquals(arg1, arg2);
            arg1.setDescription("desc");
            Assert.assertEquals(arg1, arg2);

            arg2.setParameters("params");
            Assert.assertNotEquals(arg1, arg2);
            arg1.setParameters("params");
            Assert.assertEquals(arg1, arg2);

            arg2.setDescriptionDetailed("desc det");
            Assert.assertNotEquals(arg1, arg2);
            arg1.setDescriptionDetailed("desc det");
            Assert.assertEquals(arg1, arg2);

            arg2.setDeclared(true);
            Assert.assertNotEquals(arg1, arg2);
            arg1.setDeclared(true);
            Assert.assertEquals(arg1, arg2);

            arg2.setValue("some val");
            Assert.assertNotEquals(arg1, arg2);
            arg1.setValue("some val");
            Assert.assertEquals(arg1, arg2);
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * This class was created to be able to use methods directly during testing.
     */
    @SuppressWarnings({"EmptyMethod", "SameParameterValue"})
    private class ArgumentT extends Argument {
        ArgumentT(String shortName, String longName) throws ArgumentException {
            super(shortName, longName);
        }

        ArgumentT(String shortName, String longName, String valueDefault, boolean isRequiredBeDeclared, boolean isRequiredBeFilled, String parameters, String description, String descriptionDetailed) throws ArgumentException {
            super(shortName, longName, valueDefault, isRequiredBeDeclared, isRequiredBeFilled, parameters, description, descriptionDetailed);
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
