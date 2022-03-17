package com.github.onlycrab.argParser.arguments.xml;

import org.jetbrains.annotations.Nullable;

import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for validate and parse argument xml-data using one external method.
 *
 * <p>Calling the validation method closes the passed InputStream. In order to perform validation and then
 * parse the data, the data is previously saved as a byte array by {@link XmlWrapper#readByteData(InputStream)}.
 * Before calling validation (and parsing), the byte array is converted to an InputStream by
 * {@link XmlWrapper#getInputStream(byte[])}.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 * @see Validator
 * @see XmlParser
 */
public class XmlWrapper {
    /**
     * Internal schema for validation xml-data
     */
    private static final String SCHEMA_NAME = "ArgumentsSchema.xsd";

    /**
     * XML-data parser
     */
    private XmlParser parser;

    /**
     * Schema data in the byte form
     */
    private byte[] schemaDada;

    /**
     * Create new wrapper.
     *
     * @throws IOException if an I/O error occurs
     */
    public XmlWrapper() throws IOException {
        parser = null;
        InputStream schemaStream = this.getClass().getClassLoader().getResourceAsStream(SCHEMA_NAME);
        if (schemaStream == null) {
            throw new IOException(String.format("The resource <%s> is not found", SCHEMA_NAME));
        }
        schemaDada = readByteData(schemaStream);
    }

    /**
     * Read data from InputStream to byte array.
     *
     * @param stream InputStream to read data from
     * @return data like byte array
     * @throws IOException if an I/O error occurs
     */
    public static byte[] readByteData(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IOException("InputStream is <null>");
        }
        int size = stream.available();
        byte[] data = new byte[size];
        if (stream.read(data, 0, size) == -1) {
            throw new IOException("There is no data in InputStream");
        }
        try {
            stream.close();
        } catch (IOException ignored) {
        }
        return data;
    }

    /**
     * Convert byte array to InputStream.
     *
     * @param data data like byte array
     * @return data like InputStream
     * @throws IOException if an I/O error occurs
     */
    public static InputStream getInputStream(byte[] data) throws IOException {
        if (data == null) {
            throw new IOException("Byte array is <null>");
        }
        return new ByteArrayInputStream(data, 0, data.length);
    }

    /**
     * Returns parsed project info data.
     *
     * @return parsed project info data
     */
    public Map<String, String> getInfo() {
        if (parser != null) {
            return parser.getInfo();
        } else {
            return new HashMap<>();
        }
    }

    /**
     * Returns parsed arguments data.
     *
     * @return parsed arguments data
     */
    public List<Map<String, String>> getArguments() {
        if (parser != null) {
            return parser.getArguments();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Returns parsed dependencies data.
     *
     * @return parsed dependencies data
     */
    public List<String[]> getDependencies() {
        if (parser != null) {
            return parser.getDependencies();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Returns parsed conflicts data.
     *
     * @return parsed conflicts data
     */
    public List<String[]> getConflicts() {
        if (parser != null) {
            return parser.getConflicts();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Read xml-data from file.
     *
     * @param file     File to read data from
     * @param encoding data encoding
     * @throws IOException        if an I/O error occurs
     * @throws XMLStreamException if an XMLStreamException error occurs
     */
    public void read(File file, @Nullable String encoding) throws IOException, XMLStreamException {
        if (file == null) {
            throw new IOException("File is <null>");
        } else if (!file.exists()) {
            throw new IOException(String.format("File <%s> is not exists", file.getAbsolutePath()));
        }
        read(new FileInputStream(file), encoding);
    }

    /**
     * Read xml-data from InputStream.
     *
     * @param targetStream InputStream to read data from
     * @param encoding     data encoding
     * @throws IOException        if an I/O error occurs
     * @throws XMLStreamException if an XMLStreamException error occurs
     */
    public void read(InputStream targetStream, @Nullable String encoding) throws IOException, XMLStreamException {
        if (targetStream == null) {
            throw new IOException("TargetStream is <null>");
        }
        byte[] targetDada = readByteData(targetStream);
        Validator validator = new Validator();
        if (!validator.validate(getInputStream(schemaDada), getInputStream(targetDada))) {
            throw new XMLStreamException(validator.getMessage());
        }
        parser = new XmlParser();
        parser.parse(getInputStream(targetDada), encoding);
    }
}
