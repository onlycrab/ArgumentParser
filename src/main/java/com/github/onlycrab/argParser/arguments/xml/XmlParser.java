package com.github.onlycrab.argParser.arguments.xml;

import org.jetbrains.annotations.Nullable;

import javax.xml.stream.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML parser.
 * To receive data you need:<br>
 * - Parse the data using the {@link XmlParser#parse(InputStream, String)} (the
 * parsed data will be saved inside this object);<br>
 * - Get data from the parser using one of the get methods.
 *
 * <p>Before parsing data, it is recommended to first validate it using the
 * {@link Validator#validate(InputStream, InputStream)}.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class XmlParser {
    /**
     * A factory for getting XMLStreamReader
     */
    private XMLInputFactory inputFactory;

    /**
     * Parsed project info data
     */
    private Map<String, String> info;

    /**
     * Parsed arguments data list
     */
    private List<Map<String, String>> arguments;

    /**
     * Parsed dependencies data list
     */
    private List<String[]> dependencies;

    /**
     * Parsed conflicts data list
     */
    private List<String[]> conflicts;

    /**
     * Create new XmlParser.
     *
     * @throws XMLStreamException if a XMLStreamException occurs
     */
    public XmlParser() throws XMLStreamException {
        try {
            inputFactory = XMLInputFactory.newInstance();
        } catch (FactoryConfigurationError err) {
            Exception e = err.getException();
            XMLStreamException toThrow = new XMLStreamException(e.getMessage(), e.getCause());
            toThrow.setStackTrace(err.getStackTrace());
            throw toThrow;
        }
        arguments = new ArrayList<>();
        dependencies = new ArrayList<>();
        conflicts = new ArrayList<>();
        info = new HashMap<>();
    }

    /**
     * Returns parsed project info data.
     *
     * @return parsed project info data
     */
    public Map<String, String> getInfo() {
        return info;
    }

    /**
     * Returns parsed arguments data.
     *
     * @return parsed arguments data
     */
    public List<Map<String, String>> getArguments() {
        return arguments;
    }

    /**
     * Returns parsed dependencies data.
     *
     * @return parsed dependencies data
     */
    public List<String[]> getDependencies() {
        return dependencies;
    }

    /**
     * Returns parsed conflicts data.
     *
     * @return parsed conflicts data
     */
    public List<String[]> getConflicts() {
        return conflicts;
    }

    /**
     * Parse XML data from a java.io.InputStream.
     * <b>Be careful, after executing this method the {@code xmlSteam} will be closed.</b>
     *
     * @param xmlSteam the InputStream to read from
     * @param encoding the character encoding of the stream
     * @throws XMLStreamException if a XMLStreamException occurs
     */
    public void parse(InputStream xmlSteam, @Nullable String encoding) throws XMLStreamException {
        XMLStreamReader xmlReader = null;
        try {
            if (encoding != null) {
                xmlReader = inputFactory.createXMLStreamReader(xmlSteam, encoding);
            } else {
                xmlReader = inputFactory.createXMLStreamReader(xmlSteam);
            }
            parse(xmlReader);
        } catch (XMLStreamException e) {
            throw new XMLStreamException("XML parsing error : " + e.getMessage());
        } finally {
            if (xmlReader != null) {
                try {
                    xmlReader.close();
                } catch (XMLStreamException ignore) {
                    //do nothing
                }
            }
        }
    }

    /**
     * Parse XML data from javax.xml.stream.XMLStreamReader.
     *
     * @param xmlReader the used reader
     * @throws XMLStreamException if a XMLStreamException occurs
     */
    private void parse(XMLStreamReader xmlReader) throws XMLStreamException {
        while (xmlReader.hasNext()) {
            if (xmlReader.next() == XMLStreamConstants.START_ELEMENT) {
                String name = xmlReader.getLocalName();
                switch (name) {
                    case "info":
                        readInfo(xmlReader);
                        break;
                    case "argument":
                        readArgument(xmlReader);
                        break;
                    case "dependence":
                        readDependence(xmlReader);
                        break;
                    case "conflict":
                        readConflict(xmlReader);
                        break;
                }
            }
        }
    }

    private void readInfo(XMLStreamReader xmlReader) {
        if (xmlReader == null) return;
        try {
            for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
                info.put(xmlReader.getAttributeName(i).toString(), xmlReader.getAttributeValue(i));
            }
        } catch (IllegalStateException ignore) {
            //If the data is not well-formed - ignore
        }
    }

    private void readArgument(XMLStreamReader xmlReader) {
        if (xmlReader == null) return;
        try {
            Map<String, String> argument = new HashMap<>();
            for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
                argument.put(xmlReader.getAttributeName(i).toString(), xmlReader.getAttributeValue(i));
            }
            arguments.add(argument);
        } catch (IllegalStateException e) {
            //If the data is not well-formed - ignore
        }
    }

    private void readDependence(XMLStreamReader xmlReader) {
        if (xmlReader == null) return;
        try {
            String[] parsed = new String[]{xmlReader.getAttributeValue(null, "nameDependent"),
                    xmlReader.getAttributeValue(null, "nameOn")};
            if (parsed[0] != null && parsed[1] != null) {
                dependencies.add(parsed);
            }
        } catch (IllegalStateException e) {
            //If the data is not well-formed - ignore
        }
    }

    private void readConflict(XMLStreamReader xmlReader) {
        if (xmlReader == null) return;
        try {
            String[] parsed = new String[]{xmlReader.getAttributeValue(null, "nameFirst"),
                    xmlReader.getAttributeValue(null, "nameSecond")};
            if (parsed[0] != null && parsed[1] != null) {
                conflicts.add(parsed);
            }
        } catch (IllegalStateException e) {
            //If the data is not well-formed - ignore
        }
    }
}
