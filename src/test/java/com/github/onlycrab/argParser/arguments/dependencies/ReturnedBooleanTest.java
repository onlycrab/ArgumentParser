package com.github.onlycrab.argParser.arguments.dependencies;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link ReturnedBoolean}.
 *
 * @author Roman Rynkovich
 */
public class ReturnedBooleanTest {
    /**
     * Test {@link ReturnedBoolean#addResult(Object, boolean)}.
     */
    @Test
    public void addResult() {
        ReturnedBoolean rb = new ReturnedBoolean(true);
        rb.addResult("", true);
        Assert.assertTrue(rb.getResult());

        rb.addResult("", false);
        Assert.assertFalse(rb.getResult());

        rb = new ReturnedBoolean(false);
        rb.addResult("", true);
        Assert.assertFalse(rb.getResult());

        rb.addResult("", false);
        Assert.assertFalse(rb.getResult());
    }

    /**
     * Test {@link ReturnedBoolean#setResult(Object, boolean)}.
     */
    @Test
    public void setResult() {
        ReturnedBoolean rb = new ReturnedBoolean(true);
        rb.setResult("", false);
        Assert.assertFalse(rb.getResult());
        rb.setResult("", true);
        Assert.assertTrue(rb.getResult());
    }

    /**
     * Test {@link ReturnedBoolean#addMessage(java.lang.String)}.
     */
    @Test
    public void addMessage() {
        ReturnedBoolean rb = new ReturnedBoolean(true);
        Assert.assertEquals("", rb.getMessage());

        String expected = "msg";
        rb.addMessage("msg");
        Assert.assertEquals(expected, rb.getMessage());

        expected = "msg msg2 ";
        rb.addMessage("msg2");
        Assert.assertEquals(expected, rb.getMessage());
    }

    /**
     * Test {@link ReturnedBoolean#isAlreadyProcessed(Object)}.
     */
    @Test
    @SuppressWarnings("RedundantStringConstructorCall")
    public void isAlreadyProcessed() {
        ReturnedBoolean rb = new ReturnedBoolean(true);
        String s1 = new String(), s2 = new String(), s3 = new String(), s4 = new String();

        rb.addResult(s1, true);
        Assert.assertTrue(rb.isAlreadyProcessed(s1));

        rb.addResult(s2, false);
        Assert.assertTrue(rb.isAlreadyProcessed(s2));

        rb.setResult(s3, true);
        Assert.assertTrue(rb.isAlreadyProcessed(s3));

        rb.setResult(s4, false);
        Assert.assertTrue(rb.isAlreadyProcessed(s4));

        Assert.assertTrue(rb.isAlreadyProcessed(s1));
        Assert.assertTrue(rb.isAlreadyProcessed(s2));
        Assert.assertTrue(rb.isAlreadyProcessed(s3));
    }
}
