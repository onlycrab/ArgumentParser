package com.github.onlycrab.argParser.arguments.xml;

import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler for creation XML parsing errors report.
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
class ValidatorErrorHandler extends DefaultHandler {
    /**
     * Error messages
     */
    private final List<String> message;

    /**
     * Errors counter
     */
    private int counter;

    ValidatorErrorHandler() {
        counter = 0;
        message = new ArrayList<>();
    }

    /**
     * Returns errors counter value.
     *
     * @return errors counter value
     */
    int getCounter() {
        return counter;
    }

    /**
     * Returns list of messages.
     *
     * @return list of messages
     */
    List<String> getMessage() {
        return message;
    }

    /**
     * Returns string representation of {@link SAXParseException} position.
     *
     * @param e SAX parse error to get position from
     * @return string representation of {@link SAXParseException} position
     */
    private String getLineAddress(SAXParseException e) {
        return String.format("<row[%4d] : col[%4d]>", e.getLineNumber(), e.getColumnNumber());
    }

    @Override
    public void warning(SAXParseException e) {
        counter++;
        message.add(String.format("WARNING  : %s : %s", getLineAddress(e), e.getMessage()));
    }

    @Override
    public void error(SAXParseException e) {
        counter++;
        message.add(String.format("ERROR    : %s : %s", getLineAddress(e), e.getMessage()));
    }

    @Override
    public void fatalError(SAXParseException e) {
        counter++;
        message.add(String.format("FATAL ERR: %s : %s", getLineAddress(e), e.getMessage()));
    }
}
