package com.github.onlycrab.argParser.arguments.xml;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test {@link XmlParser}.
 *
 * @author Roman Rynkovich
 */
public class XmlParserTest {
    static final String validData = "test/ArgumentTestXmlParser1.xml";
    static final String redundantField = "test/ArgumentTestXmlParser2.txt";
    static final String invalidXmlFormat = "test/ArgumentTestXmlParser3.txt";

    private void parseStream(String validData) {
        try {
            XmlParser parser = new XmlParser();
            parser.parse(this.getClass().getClassLoader().getResourceAsStream(validData), null);

            /*Info */
            Map<String, String> expectedMap = TestData.buildInfoTest();
            Map<String, String> actualMap = parser.getInfo();
            if (!expectedMap.equals(actualMap)) {
                Assert.fail(String.format("\nExpected data:%s\nActual data  :%s", expectedMap, actualMap));
            }

            /*Dependencies */
            List<String[]> expectedArrayList = TestData.buildDependenciesTest();
            List<String[]> actualArrayList = parser.getDependencies();
            String err = ArrayListCompare.compareList(expectedArrayList, actualArrayList);
            if (err != null) {
                Assert.fail(err);
            }

            /*Conflicts*/
            expectedArrayList = TestData.buildConflictsTest();
            actualArrayList = parser.getConflicts();
            err = ArrayListCompare.compareList(expectedArrayList, actualArrayList);
            if (err != null) {
                Assert.fail(err);
            }

            /*Arguments*/
            List<Map<String, String>> expectedMapList = TestData.buildArgumentsTest();
            List<Map<String, String>> actualMapList = parser.getArguments();
            if (expectedMapList.size() != actualMapList.size()) {
                Assert.fail(String.format("Wrong list size : expected %d, actual %d.\n\nExpected data:%s\nActual data  :%s",
                        expectedMapList.size(), actualMapList.size(), expectedMapList, actualMapList));
            }
            err = ArrayListCompare.compareMap(expectedMapList, actualMapList);
            if (err != null) {
                Assert.fail(err);
            }
        } catch (XMLStreamException e) {
            Assert.fail("Parser not created : XMLStreamException : " + e.getMessage());
        }
    }

    /**
     * Test {@link XmlParser#parse(InputStream, String)}.
     */
    @Test
    public void parseStream() {

        /*Data is valid */
        parseStream(validData);

        /*Data have redundant section */
        parseStream(redundantField);

        /*Wrong XML format */
        try {
            XmlParser parser = new XmlParser();
            parser.parse(this.getClass().getClassLoader().getResourceAsStream(invalidXmlFormat), null);
            Assert.fail("XMLStreamException expected, but not thrown : wrong XML format");
        } catch (XMLStreamException ignored) {
        }
    }

    /**
     * Class for building test data in accordance with ArgumentTestXmlParser1.xml.
     */
    static final class TestData {
        static Map<String, String> buildInfoTest() {
            Map<String, String> info = new HashMap<>();
            info.put("description", "some description");
            info.put("usage", "some usage");
            info.put("example", "some example");
            return info;
        }

        static List<Map<String, String>> buildArgumentsTest() {
            List<Map<String, String>> args = new ArrayList<>();
            Map<String, String> arg = new HashMap<>();
            arg.put("shortName", "at1");
            arg.put("longName", "argument-test1");
            arg.put("isRequiredDeclared", "false");
            arg.put("isRequiredFilled", "true");
            args.add(arg);

            arg = new HashMap<>();
            arg.put("shortName", "at2");
            arg.put("longName", "argument-test2");
            arg.put("isRequiredDeclared", "false");
            arg.put("isRequiredFilled", "false");
            args.add(arg);

            arg = new HashMap<>();
            arg.put("shortName", "at3");
            arg.put("longName", "argument-test3");
            arg.put("isRequiredDeclared", "true");
            arg.put("isRequiredFilled", "false");
            args.add(arg);

            arg = new HashMap<>();
            arg.put("shortName", "at4");
            arg.put("longName", "argument-test4");
            arg.put("isRequiredDeclared", "false");
            arg.put("isRequiredFilled", "false");
            args.add(arg);

            return args;
        }

        static List<String[]> buildDependenciesTest() {
            List<String[]> dep = new ArrayList<>();
            dep.add(new String[]{"at1", "at2"});
            dep.add(new String[]{"at4", "at2"});
            return dep;
        }

        static List<String[]> buildConflictsTest() {
            List<String[]> con = new ArrayList<>();
            con.add(new String[]{"at3", "at4"});
            return con;
        }
    }

    /**
     * Class for comparing string data in lists.
     * {@code list_1} and {@code list_2} are considered to be the same if:
     * 1.each item from {@code list_1} has an equal item in {@code list_2};
     * 2.each item from {@code list_2} has an equal item in {@code list_1};
     * 3.If the dimensions of {@code list_1} and {@code list_2} are the same.
     */
    static final class ArrayListCompare {
        static String compareMap(List<Map<String, String>> expectedMapList, List<Map<String, String>> actualMapList) {
            boolean isEquals;
            for (Map<String, String> expected : expectedMapList) {
                isEquals = false;
                for (Map<String, String> actual : actualMapList) {
                    if (expected.entrySet().equals(actual.entrySet())) {
                        isEquals = true;
                        break;
                    }
                }
                if (!isEquals) {
                    return String.format("Each of actual map is not equal to expected map %s.\nExpected list:%s\nActual list  :%s",
                            expected, expectedMapList, actualMapList);
                }
            }
            return null;
        }

        static String compareList(List<String[]> expected, List<String[]> actual) {
            if (expected == null) {
                return "Expected list is <null>";
            } else if (actual == null) {
                return "Actual list is <null>";
            }
            if (expected.size() != actual.size()) {
                return String.format("Wrong list size : expected %d, actual %d.\n\nExpected data:%s\nActual data  :%s",
                        expected.size(), actual.size(), toString(expected), toString(actual));
            }

            boolean isEquals;
            for (String[] arr1 : expected) {
                isEquals = false;
                for (String[] arr2 : actual) {
                    if (isEquals(arr1, arr2)) {
                        isEquals = true;
                        break;
                    }
                }
                if (!isEquals) {
                    return String.format("Each of actual arrays is not equal to expected array %s.\nExpected list:%s\nActual list  :%s",
                            toString(arr1), toString(expected), toString(actual));
                }
            }

            return null;
        }

        static String toString(List<String[]> target) {
            if (target == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            boolean isFirst;
            for (String[] arr : target) {
                sb.append("{");
                isFirst = true;
                for (String s : arr) {
                    if (isFirst) {
                        isFirst = false;
                        sb.append(s);
                    } else {
                        sb.append(',').append(s);
                    }
                }
                sb.append("}");
            }
            sb.append("}");
            return sb.toString();
        }

        static boolean isEquals(String[] arr1, String[] arr2) {
            if (arr1 == null || arr2 == null) {
                return false;
            }
            if (arr1.length != arr2.length) {
                return false;
            }
            for (int i = 0; i < arr1.length; i++) {
                if (!arr1[i].equals(arr2[i])) {
                    return false;
                }
            }
            return true;
        }

        static String toString(String[] arr) {
            if (arr == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder("{");
            if (arr.length == 1) {
                sb.append(arr[0]);
            } else if (arr.length > 1) {
                sb.append(arr[0]);
                for (int i = 1; i < arr.length; i++) {
                    sb.append(',').append(arr[i]);
                }
            }
            sb.append("}");
            return sb.toString();
        }
    }
}
