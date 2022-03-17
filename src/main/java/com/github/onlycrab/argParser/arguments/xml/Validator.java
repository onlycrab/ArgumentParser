package com.github.onlycrab.argParser.arguments.xml;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * XML file validator.
 *
 * <p>Validation can only be performed with an internal schema {@code ArgumentSchema.xsd}.
 * The textual representation of validation and errors are stored inside the class.</p>
 *
 * <p>To find out if the data is valid, call {@link Validator#validate(InputStream, InputStream)}.
 * To get a textual representation of the result (and errors if any), call
 * {@link Validator#getMessage()}.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class Validator {
    /**
     * Messages generated by {@link ValidatorErrorHandler}
     */
    private List<String> messages = new ArrayList<>();

    /**
     * Returns validation result message.
     * If the validation is unsuccessful, information about all errors will also be returned.
     *
     * @return validation result message.
     */
    public String getMessage() {
        if (messages.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String msg : messages) {
            if (!isFirst) {
                sb.append('\n');
            } else {
                isFirst = false;
            }
            sb.append(msg);
        }
        return sb.toString();
    }

    /**
     * Returns validation result.
     * For get validation process message use method {@link Validator#getMessage()}.
     *
     * <p>Be careful, after executing this method the {@code schemaStream} and {@code targetStream} will be closed.</p>
     *
     * @param schemaStream InputStream to read schema from
     * @param targetStream InputStream to read xml-data from
     * @return {@code true} if target data is valid
     */
    public boolean validate(InputStream schemaStream, InputStream targetStream) {
        messages.clear();
        if (schemaStream == null) {
            messages.add("Validation failure : SchemaStream is <null>");
            return false;
        } else if (targetStream == null) {
            messages.add("Validation failure : TargetStream is <null>");
            return false;
        }
        return validate(new StreamSource(schemaStream), new StreamSource(targetStream));
    }

    /**
     * Returns validation result.
     * For get validation process message use method {@link Validator#getMessage()}.
     *
     * @param schemaSource source to read schema from
     * @param targetSource source to read xml-data from
     * @return {@code true} if target data is valid
     */
    public boolean validate(Source schemaSource, Source targetSource) {
        messages.clear();
        if (schemaSource == null) {
            messages.add("Validation failure : SchemaSource is <null>");
            return false;
        } else if (targetSource == null) {
            messages.add("Validation failure : TargetSource is <null>");
            return false;
        }
        String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        SchemaFactory schemaFactory = SchemaFactory.newInstance(language);
        try {
            Schema schema = schemaFactory.newSchema(schemaSource);
            javax.xml.validation.Validator validator = schema.newValidator();
            ValidatorErrorHandler configErrorHandler = new ValidatorErrorHandler();
            validator.setErrorHandler(configErrorHandler);
            validator.validate(targetSource);
            if (configErrorHandler.getCounter() == 0) {
                messages.add(String.format(
                        "%s is valid.",
                        (targetSource.getSystemId() != null ? "File <" + targetSource.getSystemId() + ">" : "Data")
                ));
                return true;
            } else {
                messages = configErrorHandler.getMessage();
                messages.add(String.format(
                        "%s is not valid.",
                        (targetSource.getSystemId() != null ? "File <" + targetSource.getSystemId() + ">" : "Data")
                ));
                return false;
            }
        } catch (SAXException | IOException e) {
            messages.add(String.format("Validation failure : type <%s>; msg <%s>.", e.getClass().getName(), e.getMessage()));
            messages.add(String.format(
                    "%s is not valid.",
                    (targetSource.getSystemId() != null ? "File <" + targetSource.getSystemId() + ">" : "Data")
            ));
            return false;
        }
    }
}
