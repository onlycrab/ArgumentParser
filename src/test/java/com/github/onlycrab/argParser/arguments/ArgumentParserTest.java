package com.github.onlycrab.argParser.arguments;

import com.github.onlycrab.argParser.arguments.exceptions.ArgumentException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test {@link ArgumentParser}.
 *
 * @author Roman Rynkovich
 */
public class ArgumentParserTest {
    /**
     * Test {@link ArgumentParser#parse(ArgumentStorage, String[])}.
     */
    @Test
    public void parse() {
        ArgumentStorage storage = new ArgumentStorage();
        try {
            Argument argA = new Argument("a", "first");
            Argument argB = new Argument("b", "second");
            Argument argC = new Argument("c", "third");
            storage.add(argA);
            storage.add(argB);
            storage.add(argC);

            //Parse names
            ArgumentParser.parse(storage, new String[]{"-a", "--b", "-third"});
            if (!argA.isDeclared()) {
                Assert.fail("Argument <a(first)> is not declared");
            }
            if (!argB.isDeclared()) {
                Assert.fail("Argument <b(second)> is not declared");
            }
            if (!argC.isDeclared()) {
                Assert.fail("Argument <c(third)> is not declared");
            }

            //Parse values
            ArgumentParser.parse(storage, new String[]{"-a", "val", "--third", "-123"});
            Assert.assertEquals("val", argA.getValue());
            Assert.assertEquals("-123", argC.getValue());
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ArgumentParser#parse(ArgumentStorage, Map)}.
     */
    @Test
    public void parseMap() {
        ArgumentStorage storage = new ArgumentStorage();
        try {
            Argument argA = new Argument("a", "first");
            Argument argB = new Argument("b", "second");
            Argument argC = new Argument("c", "third");
            storage.add(argA);
            storage.add(argB);
            storage.add(argC);
            Map<String, String> data = new HashMap<>();
            data.put("a", "val");
            data.put("second", "-5DA2");

            //Parse names and values
            ArgumentParser.parse(storage, data);
            Assert.assertEquals("val", argA.getValue());
            Assert.assertEquals("-5DA2", argB.getValue());
            Assert.assertEquals("", argC.getValue());

            //Throwing exception
            data.clear();
            data.put("", "miss");
            try {
                ArgumentParser.parse(storage, data);
                Assert.fail("ArgumentException expected, but not thrown : data with empty key");
            } catch (ArgumentException ignored) {
            }
        } catch (ArgumentException e) {
            Assert.fail("Unexpected ArgumentException : " + e.getMessage());
        }
    }
}
