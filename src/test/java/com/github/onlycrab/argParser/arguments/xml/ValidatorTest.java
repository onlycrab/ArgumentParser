package com.github.onlycrab.argParser.arguments.xml;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;

/**
 * Test {@link Validator}.
 *
 * @author Roman Rynkovich
 */
public class ValidatorTest {
    private static final String validSchema = "ArgumentsSchema.xsd";
    private static final String validData = "test/ArgumentTestValidator1.xml";
    private static final String invalidData = "test/ArgumentTestValidator2.xml";
    private static final String dependenciesData = "test/ArgumentTestValidator3.xml";
    private static final String emptyData = "test/ArgumentTestValidator4.xml";
    private static final String nonExistentData = "test/ArgumentTestValidator5.xml";

    /**
     * Test {@link Validator#validate(Source, Source)}.
     */
    @Test
    public void validate() {
        ClassLoader loader = this.getClass().getClassLoader();
        Validator validator = new Validator();

        /*Data is valid */
        if (!validator.validate(
                new StreamSource(loader.getResourceAsStream(validSchema)),
                new StreamSource(loader.getResourceAsStream(validData))
        )) {
            Assert.fail(validator.getMessage());
        }

        /*Only dependencies*/
        if (!validator.validate(
                new StreamSource(loader.getResourceAsStream(validSchema)),
                new StreamSource(loader.getResourceAsStream(dependenciesData))
        )) {
            Assert.fail(validator.getMessage());
        }

        /*Empty data*/
        if (!validator.validate(
                new StreamSource(loader.getResourceAsStream(validSchema)),
                new StreamSource(loader.getResourceAsStream(emptyData))
        )) {
            Assert.fail(validator.getMessage());
        }

        /*Data is not valid */
        if (validator.validate(
                new StreamSource(loader.getResourceAsStream(validSchema)),
                new StreamSource(loader.getResourceAsStream(invalidData))
        )) {
            Assert.fail("Validator returns <true>, but data is not valid");
        }

        /*Schema is not valid*/
        if (validator.validate(
                new StreamSource(loader.getResourceAsStream(validData)),
                new StreamSource(loader.getResourceAsStream(validData))
        )) {
            Assert.fail("Validator returns <true>, but schema is not valid");
        } else {
            if (!validator.getMessage().contains("SAXParseException")) {
                Assert.fail("Validator message not contains info about SAXParseException. \n Message: ["
                        + validator.getMessage() + "]");
            }
        }
    }

    /**
     * Test {@link Validator#validate(InputStream, InputStream)}.
     * Only throwing errors is tested here, since the validation procedure has
     * already been tested in {@link ValidatorTest#validate()}.
     */
    @Test
    public void validateStream() {
        ClassLoader loader = this.getClass().getClassLoader();
        Validator validator = new Validator();

        /*Stream is null */
        if (validator.validate(
                loader.getResourceAsStream(validSchema),
                loader.getResourceAsStream(nonExistentData)
        )) {
            Assert.fail("IOException expected, but not thrown : target stream is <null>");
        }

        if (validator.validate(
                null,
                loader.getResourceAsStream(validData)
        )) {
            Assert.fail("IOException expected, but not thrown : schema stream is <null>");
        }
        if (validator.validate(
                null,
                loader.getResourceAsStream(nonExistentData)
        )) {
            Assert.fail("IOException expected, but not thrown : schema stream and target stream is <null>");
        }
    }
}
