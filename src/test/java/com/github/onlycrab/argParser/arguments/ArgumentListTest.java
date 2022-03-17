package com.github.onlycrab.argParser.arguments;

import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test {@link ArgumentList}.
 *
 * @author Roman Rynkovich
 */
public class ArgumentListTest {
    /**
     * Test {@link ArgumentList#add(Argument)} and {@link ArgumentList#clear()}.
     */
    @Test
    public void add() {
        try {
            ArgumentList list = new ArgumentList();

            Assert.assertEquals(0, list.size());
            list.add(new Argument("a1", "arg1"));
            Assert.assertEquals(1, list.size());
            list.add(new Argument("a2", "arg2"));
            Assert.assertEquals(2, list.size());

            //Argument with duplicate name will be absorbed
            list.add(new Argument("a2", "arg3"));
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("a1,arg1;a2,arg3;", argNamesInLine(list));
            list.add(new Argument("a3", "arg3"));
            Assert.assertEquals(2, list.size());
            Assert.assertEquals("a1,arg1;a3,arg3;", argNamesInLine(list));

            list.clear();
            Assert.assertEquals(0, list.size());
            Assert.assertEquals("", argNamesInLine(list));
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException occurred : " + e.getMessage());
        }
    }

    private String argNamesInLine(ArgumentList list) {
        StringBuilder sb = new StringBuilder();
        for (Argument tmp : list) {
            sb.append(tmp.getShortName()).append(",").append(tmp.getLongName()).append(";");
        }
        return sb.toString();
    }

    /**
     * Test {@link ArgumentList#get(String)}.
     */
    @Test
    public void get() {
        try {
            ArgumentList list = new ArgumentList();

            Assert.assertNull(list.get("a1"));
            list.add(new Argument("a1", "arg1"));
            Assert.assertNotNull(list.get("a1"));
            Assert.assertNotNull(list.get("arg1"));

            list.add(new Argument("a2", "arg2"));
            Assert.assertNotNull(list.get("arg2"));

            Argument arg1 = list.get("a1");
            Argument arg2 = list.get("a2");

            Assert.assertEquals("a1", arg1.getShortName());
            Assert.assertEquals("arg1", arg1.getLongName());
            Assert.assertEquals("a2", arg2.getShortName());
            Assert.assertEquals("arg2", arg2.getLongName());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException occurred : " + e.getMessage());
        } catch (NullPointerException e) {
            Assert.fail("Cant get argument from list : returns <null>");
        }
    }

    /**
     * Test {@link ArgumentList#containsKey(Object)}.
     */
    @Test
    public void containsKey() {
        try {
            ArgumentList list = new ArgumentList();

            Assert.assertFalse(list.containsKey("a1"));

            list.add(new Argument("a1", "arg1"));
            Assert.assertTrue(list.containsKey("arg1"));
            list.add(new Argument("a2", "arg2"));
            Assert.assertTrue(list.containsKey("a1"));
            Assert.assertTrue(list.containsKey("a2"));
            Assert.assertTrue(list.containsKey("arg2"));
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException occurred : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentList#iterator()}.
     */
    @Test
    public void iterator() {
        try {
            ArgumentList list = new ArgumentList();

            list.add(new Argument("a1", "arg1"));
            list.add(new Argument("a2", "arg2"));
            list.add(new Argument("a3", "arg3"));

            List<String> returnedNames = new ArrayList<>();
            for (Argument tmp : list) {
                if (tmp == null) {
                    Assert.fail("Iterator returns <null>, but last element has been not reached");
                    return;
                }
                if (returnedNames.contains(tmp.getShortName())) {
                    Assert.fail(String.format("There was a duplicate return of an item : the argument with name <%s> " +
                            "has already been returned.\nReturned names:%s", tmp.getShortName(), returnedNames));
                    return;
                } else if (returnedNames.contains(tmp.getLongName())) {
                    Assert.fail(String.format("There was a duplicate return of an item : the argument with name <%s> " +
                            "has already been returned.\nReturned names:%s", tmp.getLongName(), returnedNames));
                    return;
                }
                returnedNames.add(tmp.getShortName());
                returnedNames.add(tmp.getLongName());
            }
            if (returnedNames.size() / 2 != list.size()) {
                Assert.fail(String.format("Iterator error\nExpected returned elements:%d\nActual returned elements  :%d",
                        list.size(), returnedNames.size() / 2));
            }

        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException occurred : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentList#equals(Object)}.
     */
    @Test
    public void equals() {
        try {
            ArgumentList list1, list2;
            list1 = new ArgumentList();
            list2 = new ArgumentList();

            list1.add(new Argument("a1", "arg1"));
            Assert.assertNotEquals(list1, list2);
            list2.add(new Argument("a1", "arg1"));
            Assert.assertEquals(list1, list2);
            list2.add(new Argument("a2", "arg2"));
            Assert.assertNotEquals(list1, list2);
            list1.add(new Argument("a2", "arg2"));
            Assert.assertEquals(list1, list2);
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException occurred : " + e.getMessage());
        }
    }
}