package com.github.onlycrab.argParser.arguments;

import com.github.onlycrab.argParser.arguments.dependencies.Rule;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import com.github.onlycrab.argParser.arguments.exceptions.ArgumentNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Test {@link ArgumentStorage}.
 *
 * @author Roman Rynkovich
 */
public class ArgumentStorageTest {
    /**
     * Test {@link ArgumentStorage#ArgumentStorage()} and {@link ArgumentStorage#ArgumentStorage(boolean)}.
     */
    @Test
    public void arguments() {
        ArgumentStorage storage;

        //Create empty object
        storage = new ArgumentStorage();
        Assert.assertEquals(0, storage.size());

        //Create arguments with default help
        storage = new ArgumentStorage(true);
        try {
            storage.getArgument("h");
        } catch (ArgumentNotFoundException e) {
            Assert.fail("Cant get systemic argument <help> : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#getArgument(String)}.
     */
    @Test
    public void getArgument() {
        ArgumentStorage storage = new ArgumentStorage();
        try {
            storage.add(new Argument("a1", "argument1"));
            storage.add(new Argument("arg-2", "argument-2"));

            try {
                storage.getArgument("a1");
                storage.getArgument("argument1");
                storage.getArgument("arg-2");
                storage.getArgument("argument-2");

                Assert.assertEquals(storage.getArgument("a1"), storage.getArgument("argument1"));
                Assert.assertEquals(storage.getArgument("argument-2"), storage.getArgument("arg-2"));
                Assert.assertNotEquals(storage.getArgument("a1"), storage.getArgument("argument-2"));
            } catch (ArgumentNotFoundException e) {
                Assert.fail("Cant get argument : " + e.getMessage());
            }
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#add(Argument)}.
     */
    @Test
    public void add() {
        ArgumentStorage storage = new ArgumentStorage();
        try {
            Argument arg1 = new Argument("a1", "arg1");
            Argument arg2 = new Argument("a2", "arg2");
            storage.add(arg1);
            Assert.assertEquals(arg1, storage.getArgument(arg1.getShortName()));

            try {
                storage.getArgument(arg2.getLongName());
                Assert.fail("ArgumentNotFoundException expected, but not thrown : argument <arg2> is not added");
            } catch (ArgumentNotFoundException ignored) {
            }
            storage.add(arg2);
            Assert.assertEquals(arg2, storage.getArgument(arg2.getLongName()));

        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#read(InputStream, String)}.
     */
    @Test
    public void read() {
        //Test read args
        String validData = "/test/ArgumentTestXmlParser1.xml";
        ArgumentStorage storageExpected, storageActual;
        try {
            storageExpected = new ArgumentStorage();
            storageExpected.setDescription("some description");
            storageExpected.setUsage("some usage");
            storageExpected.setExample("some example");
            storageExpected.add(new Argument("at1", "argument-test1").setRequiredBeFilled(true));
            storageExpected.add(new Argument("at2", "argument-test2"));
            storageExpected.add(new Argument("at3", "argument-test3").setRequiredBeDeclared(true));
            storageExpected.add(new Argument("at4", "argument-test4"));
            storageExpected.addDependence("at1", "at2");
            storageExpected.addDependence("at4", "at2");
            storageExpected.addConflict("at3", "at4");

            storageActual = new ArgumentStorage();
            storageActual.read(ArgumentStorageTest.class.getResourceAsStream(validData), null);
            Assert.assertEquals(storageExpected, storageActual);

        } catch (IOException | XMLStreamException e) {
            Assert.fail("Cant create arguments from XML <" + validData + "> : " + e.getMessage());
        } catch (ArgumentException e) {
            Assert.fail("Cant get argument : " + e.getMessage());
        }

        //Test read additional data
        String onlyDep = "/test/ArgumentTestValidator3.xml";
        try {
            storageExpected = new ArgumentStorage();
            storageActual = new ArgumentStorage();

            storageExpected.add(new Argument("a1", "arg1"));
            storageExpected.add(new Argument("a2", "arg2"));
            storageExpected.add(new Argument("at5", "arg5"));
            storageExpected.add(new Argument("we2", "we2test"));

            storageActual.add(new Argument("a1", "arg1"));
            storageActual.add(new Argument("a2", "arg2"));
            storageActual.add(new Argument("at5", "arg5"));
            storageActual.add(new Argument("we2", "we2test"));

            storageActual.read(ArgumentStorageTest.class.getResourceAsStream(onlyDep), null);
            Assert.assertNotEquals(storageExpected, storageActual);

            storageExpected.addDependence("a1", "a2");
            storageExpected.addDependence("at5", "we2");
            storageExpected.addConflict("a1", "we2");
            Assert.assertEquals(storageExpected, storageActual);

        } catch (IOException | XMLStreamException e) {
            Assert.fail("Cant read arguments from XML <" + onlyDep + "> : " + e.getMessage());
        } catch (ArgumentException e) {
            Assert.fail("Cant get argument : " + e.getMessage());
        }

        //Test processing new line characters
        String charsTest = "/test/ArgumentTestXmlSpecChars.xml";
        try {
            storageActual = new ArgumentStorage();
            storageActual.read(ArgumentStorageTest.class.getResourceAsStream(charsTest), null);

            Assert.assertEquals("some description", storageActual.getDescription());
            Assert.assertEquals("some \n usage", storageActual.getUsage());

            Argument arg1 = storageActual.getArgument("at1");
            Argument arg2 = storageActual.getArgument("at2");

            Assert.assertEquals("\nsome description", arg1.getDescription());
            Assert.assertEquals("some \ndetailed\n description", arg1.getDescriptionDetailed());
            Assert.assertEquals("some description", arg2.getDescription());
            Assert.assertEquals("some detailed \ndescription\n\n", arg2.getDescriptionDetailed());

            Assert.assertEquals("some description", storageActual.getDescription());
            Assert.assertEquals("some \n usage", storageActual.getUsage());
        } catch (IOException | XMLStreamException e) {
            Assert.fail("Cant read arguments from XML <" + onlyDep + "> : " + e.getMessage());
        } catch (ArgumentException e) {
            Assert.fail("Cant get argument : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#isDeclared(String)}.
     */
    @Test
    public void isDeclared() {
        ArgumentStorage storage = new ArgumentStorage();
        try {
            ArgumentT arg1 = new ArgumentT("a", "arg");
            storage.add(arg1);
            Assert.assertFalse(storage.isDeclared("a"));

            arg1.setDeclared(true);
            Assert.assertTrue(storage.isDeclared("a"));
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#isFilled(String)}.
     */
    @Test
    public void isFilled() {
        ArgumentStorage storage = new ArgumentStorage();
        try {
            ArgumentT arg1 = new ArgumentT("a", "arg");
            storage.add(arg1);
            Assert.assertFalse(storage.isFilled("a"));

            arg1.setValue("val");
            Assert.assertTrue(storage.isFilled("a"));
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#isCyclicDependencyExists()}.
     */
    @Test
    public void isCyclicDependencyExists() {
        ArgumentStorage args = new ArgumentStorage();
        ArgumentT arg1, arg2, arg3, arg4;
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");
            arg3 = new ArgumentT("arg3", "argument3");
            arg4 = new ArgumentT("arg4", "argument4");

            args.add(arg1).add(arg2).add(arg3).add(arg4);

            args.addDependence(arg2.getShortName(), arg4.getShortName());
            args.addDependence(arg2.getShortName(), arg3.getShortName());
            args.addDependence(arg3.getShortName(), arg1.getShortName());

            if (args.isCyclicDependencyExists()) {
                Assert.fail(args.getMessage());
            }

            args.addDependence(arg1.getShortName(), arg3.getShortName());
            Assert.assertTrue(args.isCyclicDependencyExists());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#isRequireFilled()}.
     */
    @Test
    public void isRequireFilled() {
        ArgumentStorage args = new ArgumentStorage();
        ArgumentT arg1, arg2, arg3;
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");
            arg3 = new ArgumentT("arg3", "argument3");
            args.add(arg1).add(arg2).add(arg3);

            Assert.assertTrue(args.isRequireFilled());

            /*Check cyclic dependencies */
            args.addDependence("arg3", "arg2");
            args.addDependence("arg2", "arg1");
            args.addDependence("arg1", "arg3");
            Assert.assertFalse(args.isRequireFilled());
            args.clearDependencies();

            /*Check declaration condition */
            arg1.setRequiredBeDeclared(true);
            Assert.assertFalse(args.isRequireFilled());
            arg1.setDeclared(true);
            Assert.assertTrue(args.isRequireFilled());

            /*Check filling condition */
            arg1.setRequiredBeFilled(true);
            Assert.assertFalse(args.isRequireFilled());
            arg1.setValue("some value");
            Assert.assertTrue(args.isRequireFilled());

            /*Check required link*/
            args.addDependence("arg2", "arg3");
            Assert.assertTrue(args.isRequireFilled());

            arg3.setRequiredBeDeclared(true);
            Assert.assertFalse(args.isRequireFilled());
            arg3.setDeclared(true);
            Assert.assertTrue(args.isRequireFilled());

            arg3.setRequiredBeFilled(true);
            Assert.assertFalse(args.isRequireFilled());

            arg3.setValue("some value");
            Assert.assertTrue(args.isRequireFilled());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#isRequireFilled()}.
     */
    @Test
    public void isConflict() {
        ArgumentStorage args = new ArgumentStorage();
        ArgumentT arg1, arg2, arg3;
        try {
            arg1 = new ArgumentT("arg1", "argument1");
            arg2 = new ArgumentT("arg2", "argument2");
            arg3 = new ArgumentT("arg3", "argument3");
            args.add(arg1).add(arg2).add(arg3);
            Assert.assertFalse(args.isConflict());

            args.addConflict("arg1", "arg3");
            arg1.setDeclared(true);
            Assert.assertFalse(args.isConflict());
            arg3.setDeclared(true);
            Assert.assertTrue(args.isConflict());
            arg3.setDeclared(false);
            arg3.setValue("val");
            Assert.assertTrue(args.isConflict());
            arg3.setValue(null);
            arg3.setDeclared(false);
            Assert.assertFalse(args.isConflict());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#isRulesSatisfied()}.
     */
    @Test
    public void isRulesSatisfied() {
        ArgumentStorage storage = new ArgumentStorage();
        ArgumentT arg1, arg2;
        Rule rule = new Rule() {
            @Override
            public boolean isSatisfied() {
                boolean result = true;
                for (Argument target : targets) {
                    if (target.getValue().length() < 2) {
                        result = false;
                    }
                }
                return result;
            }
        };
        try {
            arg1 = new ArgumentT("a1", "arg1");
            arg2 = new ArgumentT("a2", "arg2");

            rule.addTarget(arg1);
            storage.addRule(rule);
            Assert.assertFalse(storage.isRulesSatisfied());

            arg1.setValue("1");
            Assert.assertFalse(storage.isRulesSatisfied());

            arg1.setValue("1s");
            Assert.assertTrue(storage.isRulesSatisfied());

            rule.addTarget(arg2);
            arg2.setValue("dds");
            Assert.assertTrue(storage.isRulesSatisfied());

            arg2.setValue("v");
            Assert.assertFalse(storage.isRulesSatisfied());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#getHelp(String)}, {@link ArgumentStorage#getHelp()}.
     */
    @Test
    public void getHelp() {
        Argument arg;
        String expected;
        try {
            arg = new Argument(
                    "a",
                    null,
                    "valDef",
                    false,
                    false,
                    null,
                    null,
                    null
            );
            ArgumentStorage storage = new ArgumentStorage(false);
            storage.add(arg);

            //getHelp(String)
            expected = "Short name : a\n" +
                    "Full name : \n" +
                    "Required : No\n" +
                    "Description : ";
            Assert.assertEquals(expected, storage.getHelp(arg.getShortName()));

            arg.setDescriptionDetailed("descDetail");
            expected = "Short name : a\n" +
                    "Full name : \n" +
                    "Required : No\n" +
                    "Description : descDetail";
            Assert.assertEquals(expected, storage.getHelp(arg.getShortName()));

            arg.setDescriptionDetailed(null);
            arg.setDescription("desc");
            expected = "Short name : a\n" +
                    "Full name : \n" +
                    "Required : No\n" +
                    "Description : desc";
            Assert.assertEquals(expected, storage.getHelp(arg.getShortName()));

            arg = new Argument(
                    "a",
                    "arg",
                    "valDef",
                    true,
                    true,
                    "param",
                    "desc",
                    "descDetail"
            );
            storage.clear();
            storage.add(arg);
            expected = "Short name : a\n" +
                    "Full name : arg\n" +
                    "Required : Yes\n" +
                    "Parameter : param\n" +
                    "Description : descDetail";
            Assert.assertEquals(expected, storage.getHelp(arg.getShortName()));

            //getHelp();
            storage.setDescription("des");
            storage.setExample("ex");
            storage.setUsage("us");
            expected = "des\n\n" +
                    "Usage : \n" +
                    "us\n\n" +
                    "Examples : \n" +
                    "ex\n\n" +
                    "Parameters : \n" +
                    "  -a, --arg   param [Required] desc\n";
            Assert.assertEquals(expected, storage.getHelp());

            storage = new ArgumentStorage(true);
            arg.setParameters(null);
            arg.setRequiredBeFilled(false);
            arg.setRequiredBeDeclared(false);
            storage.add(arg);
            storage.setDescription("des");
            storage.setExample("ex");
            storage.setUsage("us");
            expected = "des\n\n" +
                    "Usage : \n" +
                    "us\n\n" +
                    "Examples : \n" +
                    "ex\n\n" +
                    "Parameters : \n" +
                    "  -h, --help  STRING Print help info.\n" +
                    "  -a, --arg          desc\n";
            Assert.assertEquals(expected, storage.getHelp());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#getSystemHelp(String)}.
     */
    @Test
    public void getSystemHelp(){
        Argument arg, argHelp;
        String expected;
        ArgumentStorage storage;
        try {
            //System help arg
            arg = new Argument(
                    "a",
                    null,
                    "valDef",
                    false,
                    false,
                    null,
                    "desc",
                    "desc detailed"
            );
            storage = new ArgumentStorage(false);
            storage.add(arg);
            ArgumentParser.parse(storage, new String[]{"-h", "a"});
            expected = "Short name : a\n" +
                    "Full name : \n" +
                    "Required : No\n" +
                    "Description : desc detailed";
            Assert.assertEquals(expected, storage.getSystemHelp(null));
            Assert.assertNull(storage.getSystemHelp("myAwesomeArg"));

            argHelp = new Argument(
                    "maa",
                    "myAwesomeArg",
                    null,
                    false,
                    false,
                    null,
                    "My awesome help argument",
                    null
            );
            storage = new ArgumentStorage(false);
            storage.add(arg);
            storage.add(argHelp);
            ArgumentParser.parse(storage, new String[]{"-maa", "a"});
            Assert.assertEquals(expected, storage.getSystemHelp("maa"));
            Assert.assertEquals(expected, storage.getSystemHelp("myAwesomeArg"));

        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentStorage#equals(Object)}.
     */
    @Test
    public void equals() {
        ArgumentStorage args1 = new ArgumentStorage(), args2 = new ArgumentStorage();
        try {
            args1.setExample("ex");
            Assert.assertNotEquals(args1, args2);
            args2.setExample("ex");
            Assert.assertEquals(args1, args2);

            args1.setDescription("desc");
            Assert.assertNotEquals(args1, args2);
            args2.setDescription("desc");
            Assert.assertEquals(args1, args2);

            args1.setUsage("usage");
            Assert.assertNotEquals(args1, args2);
            args2.setUsage("usage");
            Assert.assertEquals(args1, args2);

            Argument arg1 = new Argument("arg1", "argument1");
            Argument arg2 = new Argument("arg2", "argument2");

            args1.add(arg1);
            Assert.assertNotEquals(args1, args2);
            args2.add(arg2);
            Assert.assertNotEquals(args1, args2);
            args1.add(arg2);
            args2.add(arg1);
            Assert.assertEquals(args1, args2);
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
