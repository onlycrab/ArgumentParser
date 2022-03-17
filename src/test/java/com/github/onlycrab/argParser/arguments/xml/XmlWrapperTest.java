package com.github.onlycrab.argParser.arguments.xml;

import com.github.onlycrab.argParser.common.ExternalReader;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Test {@link XmlWrapper}.
 *
 * @author Roman Rynkovich
 */
public class XmlWrapperTest {
    private static final String notNullData = "ArgumentsSchema.xsd";

    /**
     * Test like in {@link XmlParserTest#parseStream(String)}.
     *
     * @param validData some valid data
     */
    @SuppressWarnings("SameParameterValue")
    private void readStream(String validData) {
        try {
            XmlWrapper wrapper = new XmlWrapper();
            wrapper.read(this.getClass().getClassLoader().getResourceAsStream(validData), null);

            /*Info */
            Map<String, String> expectedMap = XmlParserTest.TestData.buildInfoTest();
            Map<String, String> actualMap = wrapper.getInfo();
            if (!expectedMap.equals(actualMap)) {
                Assert.fail(String.format("\nExpected data:%s\nActual data  :%s", expectedMap, actualMap));
            }

            /*Dependencies */
            List<String[]> expectedArrayList = XmlParserTest.TestData.buildDependenciesTest();
            List<String[]> actualArrayList = wrapper.getDependencies();
            String err = XmlParserTest.ArrayListCompare.compareList(expectedArrayList, actualArrayList);
            if (err != null) {
                Assert.fail(err);
            }

            /*Conflicts*/
            expectedArrayList = XmlParserTest.TestData.buildConflictsTest();
            actualArrayList = wrapper.getConflicts();
            err = XmlParserTest.ArrayListCompare.compareList(expectedArrayList, actualArrayList);
            if (err != null) {
                Assert.fail(err);
            }

            /*Arguments*/
            List<Map<String, String>> expectedMapList = XmlParserTest.TestData.buildArgumentsTest();
            List<Map<String, String>> actualMapList = wrapper.getArguments();
            if (expectedMapList.size() != actualMapList.size()) {
                Assert.fail(String.format("Wrong list size : expected %d, actual %d.\n\nExpected data:%s\nActual data  :%s",
                        expectedMapList.size(), actualMapList.size(), expectedMapList, actualMapList));
            }
            err = XmlParserTest.ArrayListCompare.compareMap(expectedMapList, actualMapList);
            if (err != null) {
                Assert.fail(err);
            }
        } catch (XMLStreamException | IOException e) {
            Assert.fail("Wrapper cant read data : " + e.getMessage());
        }
    }

    /**
     * Test {@link XmlWrapper#readByteData(InputStream)}.
     * Only throwing errors is tested here, the reading process will be
     * tested in {@link XmlWrapperTest#streamByteTransform()}.
     */
    @Test
    public void readByteData() {
        try {
            //noinspection ConstantConditions
            XmlWrapper.readByteData(null);
            Assert.fail("IOException expected, but not thrown : stream is <null>");
        } catch (IOException ignored) {
        }

        try {
            XmlWrapper.readByteData(new ByteArrayInputStream(new byte[0]));
            Assert.fail("IOException expected, but not thrown : stream is empty");
        } catch (IOException ignored) {
        }
    }

    /**
     * Test {@link XmlWrapper#getInputStream(byte[])}.
     * Only throwing errors is tested here, the data transformation process
     * will be tested in {@link XmlWrapperTest#streamByteTransform()}.
     */
    @Test
    public void getInputStream() {
        try {
            //noinspection ConstantConditions
            XmlWrapper.getInputStream(null);
            Assert.fail("IOException expected, but not thrown : byte array is <null>");
        } catch (IOException ignored) {
        }

        try {
            XmlWrapper.getInputStream(new byte[]{});
            XmlWrapper.getInputStream(new byte[]{1, 2, 3});
        } catch (IOException e) {
            Assert.fail("Cant write array to stream : " + e.getMessage());
        }
    }

    /**
     * Test transform from stream to bytes array {@link XmlWrapper#readByteData(InputStream)}
     * and vice versa {@link XmlWrapper#getInputStream(byte[])} on not empty data.
     */
    @Test
    public void streamByteTransform() {
        String expected, actual;
        try {
            expected = ExternalReader.readStream(this.getClass().getClassLoader().getResourceAsStream(notNullData));
        } catch (IOException e) {
            Assert.fail("Cant read expected data : " + e.getMessage());
            return;
        }

        /*Read bytes from not empty stream */
        byte[] bytes;
        try {
            bytes = XmlWrapper.readByteData(this.getClass().getClassLoader().getResourceAsStream(notNullData));
        } catch (IOException e) {
            Assert.fail("Cant read stream to byte array : " + e.getMessage());
            return;
        }
        /*Write bytes to stream */
        InputStream is;
        try {
            is = XmlWrapper.getInputStream(bytes);
        } catch (IOException e) {
            Assert.fail("Cant read byte array to stream : " + e.getMessage());
            return;
        }
        /*Read actual data to string */
        try {
            actual = ExternalReader.readStream(is);
        } catch (IOException e) {
            Assert.fail("Cant read actual data : " + e.getMessage());
            return;
        }

        Assert.assertEquals(expected, actual);
    }

    /**
     * Test {@link XmlWrapper#read(File, String)}.
     * Only throwing errors is tested here, because the reading process will
     * be tested in {@link XmlWrapperTest#readStream()}.
     */
    @Test
    public void readFile() {
        XmlWrapper wrapper;
        try {
            wrapper = new XmlWrapper();

            try {
                wrapper.read((File) null, null);
                Assert.fail("IOException expected, but not thrown : file is null");
            } catch (IOException ignored) {
            }

            try {
                wrapper.read(new File(""), null);
                Assert.fail("IOException expected, but not thrown : file is not exists");
            } catch (IOException ignored) {
            }

        } catch (IOException | XMLStreamException e) {
            Assert.fail("Unexpected wrapper error occurred : " + e.getMessage());
        }
    }

    /**
     * Test {@link XmlWrapper#read(InputStream, String)}.
     */
    @Test
    public void readStream() {
        XmlWrapper wrapper;

        /*Data is valid */
        readStream(XmlParserTest.validData);

        /*Data have redundant section */
        try {
            wrapper = new XmlWrapper();
            wrapper.read(this.getClass().getClassLoader().getResourceAsStream(XmlParserTest.redundantField), null);
            Assert.fail("XMLStreamException expected, but not thrown : redundant filed");
        } catch (IOException e) {
            Assert.fail("Wrapper cant read data : " + e.getMessage());
        } catch (XMLStreamException ignored) {
        }

        /*Wrong XML format */
        try {
            wrapper = new XmlWrapper();
            wrapper.read(this.getClass().getClassLoader().getResourceAsStream(XmlParserTest.invalidXmlFormat), null);
            Assert.fail("XMLStreamException expected, but not thrown : wrong XML format");
        } catch (IOException e) {
            Assert.fail("Wrapper cant read data : " + e.getMessage());
        } catch (XMLStreamException ignored) {
        }
    }
}
