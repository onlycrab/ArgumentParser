package com.github.onlycrab.argParser.arguments.converter;

import com.github.onlycrab.argParser.arguments.exceptions.ConverterException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link ValueConverter}.
 *
 * @author Roman Rynkovich
 */
public class ValueConverterTest {
    /**
     * Test {@link ValueConverter#ValueConverter(String)} and
     * {@link ValueConverter#ValueConverter(String, String[], String[])}.
     */
    @Test
    @SuppressWarnings({"ConstantConditions", "UnusedAssignment"})
    public void valueConverter() {
        ValueConverter converter;

        try {
            converter = new ValueConverter("");
            converter = new ValueConverter("asd");
            converter = new ValueConverter("123456789");

            converter = new ValueConverter("pos", new String[]{"pos", "positive"}, new String[]{"neg"});
            Assert.assertTrue(converter.toBoolean());

            converter = new ValueConverter("positive", new String[]{"pos", "positive"}, new String[]{"neg"});
            Assert.assertTrue(converter.toBoolean());

            converter = new ValueConverter("neg", new String[]{"pos", "positive"}, new String[]{"neg"});
            Assert.assertFalse(converter.toBoolean());
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }

    }

    /**
     * Test {@link ValueConverter#toBoolean()}.
     */
    @Test
    public void toBoolean() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("True");
            Assert.assertTrue(converter.toBoolean());
            converter = new ValueConverter("fALse");
            Assert.assertFalse(converter.toBoolean());
            converter = new ValueConverter("YES", new String[]{"yes"}, new String[]{"no"});
            Assert.assertTrue(converter.toBoolean());
            converter = new ValueConverter("0", new String[]{"1"}, new String[]{"0"});
            Assert.assertFalse(converter.toBoolean());
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toChar()}.
     */
    @Test
    public void toChar() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("T");
            Assert.assertEquals('T', converter.toChar());

            converter = new ValueConverter("\\");
            Assert.assertEquals('\\', converter.toChar());

            converter = new ValueConverter("7");
            Assert.assertEquals('7', converter.toChar());

            converter = new ValueConverter("com/github/onlycrab/argParser/test");
            try {
                converter.toChar();
                Assert.fail("ConverterException expected, but not thrown : value length > 1");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("123");
            int i = 123;
            char c = (char) i;
            Assert.assertEquals(c, converter.toChar());

            converter = new ValueConverter("-321");
            i = -321;
            c = (char) i;
            Assert.assertEquals(c, converter.toChar());
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toInt(int)} and {@link ValueConverter#toInt()}.
     */
    @Test
    public void toInt() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("d");
            try {
                converter.toInt();
                Assert.fail("ConverterException expected, but not thrown : <d> not convertible to int");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("0");
            Assert.assertEquals(0, converter.toInt());

            converter = new ValueConverter("-17");
            Assert.assertEquals(-17, converter.toInt());
            Assert.assertEquals(-15, converter.toInt(8));
            Assert.assertEquals(-23, converter.toInt(16));

            converter = new ValueConverter("7C");
            Assert.assertEquals(124, converter.toInt(16));

            converter = new ValueConverter("101");
            Assert.assertEquals(5, converter.toInt(2));

        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toLong(int)} and {@link ValueConverter#toLong()}.
     */
    @Test
    public void toLong() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("f");
            try {
                converter.toLong();
                Assert.fail("ConverterException expected, but not thrown : <f> not convertible to long");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("1");
            Assert.assertEquals(1L, converter.toLong());

            converter = new ValueConverter("17");
            Assert.assertEquals(17L, converter.toLong());
            Assert.assertEquals(15L, converter.toLong(8));
            Assert.assertEquals(23L, converter.toLong(16));

            converter = new ValueConverter("-7C");
            Assert.assertEquals(-124L, converter.toLong(16));

            converter = new ValueConverter("101");
            Assert.assertEquals(5L, converter.toLong(2));

        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toFloat()}.
     */
    @Test
    public void toFloat() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("cc");
            try {
                converter.toFloat();
                Assert.fail("ConverterException expected, but not thrown : <cc> not convertible to float");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("0");
            Assert.assertEquals(0.0f, converter.toFloat(), 0.0f);

            converter = new ValueConverter("3.45");
            Assert.assertEquals(3.45f, converter.toFloat(), 0.0f);

            converter = new ValueConverter("-7.654f");
            Assert.assertEquals(-7.654f, converter.toFloat(), 0.0f);

        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toDouble()}.
     */
    @Test
    public void toDouble() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("edc");
            try {
                converter.toDouble();
                Assert.fail("ConverterException expected, but not thrown : <edc> not convertible to double");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("1");
            Assert.assertEquals(1.0d, converter.toDouble(), 0.0d);

            converter = new ValueConverter("-6.32");
            Assert.assertEquals(-6.32d, converter.toDouble(), 0.0d);

            converter = new ValueConverter("76.9834d");
            Assert.assertEquals(76.9834d, converter.toDouble(), 0.0d);

        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    //ARRAYS

    /**
     * Test {@link ValueConverter#toArray()}.
     */
    @Test
    public void toArray() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("");
            try {
                converter.toArray();
                Assert.fail("ConverterException expected, but not thrown : value length is 0");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter(",");
            Assert.assertArrayEquals(new String[]{","}, converter.toArray());

            converter = new ValueConverter("a,");
            Assert.assertArrayEquals(new String[]{"a", ""}, converter.toArray());

            converter = new ValueConverter(",a,bc");
            Assert.assertArrayEquals(new String[]{"", "a", "bc"}, converter.toArray());

            converter = new ValueConverter("/,");
            Assert.assertArrayEquals(new String[]{","}, converter.toArray());

            converter = new ValueConverter("/,,/");
            Assert.assertArrayEquals(new String[]{",", "/"}, converter.toArray());

            converter = new ValueConverter("a,/,,b c,b/c d , e/,f, ");
            Assert.assertArrayEquals(new String[]{"a", ",", "b c", "b/c d ", " e,f", " "}, converter.toArray());

        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toArrayBoolean()}.
     */
    @Test
    public void toArrayBoolean() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("true,false,yes,1,disable",
                    new String[]{"true", "yes", "1", "enable"},
                    new String[]{"false", "no", "0", "disable"});
            Assert.assertArrayEquals(new boolean[]{true, false, true, true, false}, converter.toArrayBoolean());

            try {
                converter = new ValueConverter("true, false,yes");
                converter.toArrayBoolean();
                Assert.fail("ConverterException expected, but not thrown : < false> is not convertible to boolean");
            } catch (ConverterException ignored) {
            }
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toArrayChar()}.
     */
    @Test
    public void toArrayChar() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("\\,E,.");
            Assert.assertArrayEquals(new char[]{'\\', 'E', '.'}, converter.toArrayChar());

            try {
                converter = new ValueConverter("ss,r");
                converter.toArrayChar();
                Assert.fail("ConverterException expected, but not thrown : <ss> is not convertible to char");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("+,-,`, ");
            Assert.assertArrayEquals(new char[]{'+', '-', '`', ' '}, converter.toArrayChar());
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toArrayInt(int)} and {@link ValueConverter#toArrayInt()}.
     */
    @Test
    public void toArrayInt() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("1,-985,23");
            Assert.assertArrayEquals(new int[]{1, -985, 23}, converter.toArrayInt());

            try {
                converter = new ValueConverter("1,-985 ,23");
                converter.toArrayInt();
                Assert.fail("ConverterException expected, but not thrown : <-985 > is not convertible to int");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("5FB2,-38D,61");
            try {
                Assert.assertArrayEquals(new int[]{24498, -909, 97}, converter.toArrayInt(10));
                Assert.fail("ConverterException expected, but not thrown : <5FB2> and <-38D> is not convertible to int with radix 10");
            } catch (ConverterException ignored) {
            }

            Assert.assertArrayEquals(new int[]{24498, -909, 97}, converter.toArrayInt(16));
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toArrayLong(int)} and {@link ValueConverter#toArrayLong()}.
     */
    @Test
    public void toArrayLong() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("1,-985,23");
            Assert.assertArrayEquals(new long[]{1, -985, 23}, converter.toArrayLong());

            try {
                converter = new ValueConverter("1,-985 ,23");
                converter.toArrayInt();
                Assert.fail("ConverterException expected, but not thrown : <-985 > is not convertible to long");
            } catch (ConverterException ignored) {
            }

            converter = new ValueConverter("5FB2,-38D,61");
            try {
                Assert.assertArrayEquals(new long[]{24498, -909, 97}, converter.toArrayLong(10));
                Assert.fail("ConverterException expected, but not thrown : <5FB2> and <-38D> is not convertible to long with radix 10");
            } catch (ConverterException ignored) {
            }

            Assert.assertArrayEquals(new long[]{24498, -909, 97}, converter.toArrayLong(16));
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toArrayFloat()}.
     */
    @Test
    public void toArrayFloat() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("54.24455f,-854.322,37");
            Assert.assertArrayEquals(new float[]{54.24455f, -854.322f, 37}, converter.toArrayFloat(), 0);

            try {
                converter = new ValueConverter("54.24455s,-854.322,37");
                converter.toArrayFloat();
                Assert.fail("ConverterException expected, but not thrown : <54.24455s> is not convertible to float");
            } catch (ConverterException ignored) {
            }
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }

    /**
     * Test {@link ValueConverter#toArrayDouble()}.
     */
    @Test
    public void toArrayDouble() {
        ValueConverter converter;
        try {
            converter = new ValueConverter("54.24455d,-854.322f,37");
            Assert.assertArrayEquals(new double[]{54.24455d, -854.322d, 37}, converter.toArrayDouble(), 0);

            try {
                converter = new ValueConverter("54.24455d,-854.322f,3 7");
                converter.toArrayDouble();
                Assert.fail("ConverterException expected, but not thrown : <3 7> is not convertible to double");
            } catch (ConverterException ignored) {
            }
        } catch (ConverterException e) {
            Assert.fail("Unexpected ConverterException : " + e.getMessage());
        }
    }
}
