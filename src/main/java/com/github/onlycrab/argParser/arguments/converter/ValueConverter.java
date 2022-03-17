package com.github.onlycrab.argParser.arguments.converter;

import com.github.onlycrab.argParser.arguments.exceptions.ConverterException;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for conversion string value. A value can be converted to simple data types and arrays.
 *
 * <p>Converting to an array occurs by splitting the string into substrings by separator {@code ','}.
 * Arrays of simple types are obtained by conversion from a string array: each element (string) is
 * converted to a simple type according to the algorithm of this class. That is {@link ValueConverter#toArrayChar()}
 * uses {@link ValueConverter#toChar()} for conversion, {@link ValueConverter#toArrayBoolean()} uses
 * {@link ValueConverter#toBoolean()} and so on.</p>
 *
 * @author Roman Rynkovich
 * @version 1.0
 */
public class ValueConverter {
    /**
     * Default radix for numeric conversions
     */
    private static final int RADIX_DEFAULT = 10;
    /**
     * Value for conversion
     */
    private final String value;
    /**
     * Converter object
     */
    private final Converter converter;
    /**
     * String values for which the boolean equivalent is {@code true}
     */
    private String[] booleanTrueCases = {"true"};
    /**
     * String values for which the boolean equivalent is {@code false}
     */
    private String[] booleanFalseCases = {"false"};

    /**
     * Create new converter.
     *
     * @param value value for conversion
     */
    public ValueConverter(String value) {
        this.value = value;
        converter = new Converter();
    }

    /**
     * Create new converter. {@code booleanTrueCases} and {@code booleanFalseCases}
     * will override the defaults cases. By default:
     * true cases  = {"true"}
     * false cases = {"false"}
     *
     * @param value             value for conversion
     * @param booleanTrueCases  true cases
     * @param booleanFalseCases false cases
     * @see ValueConverter#toBoolean()
     */
    public ValueConverter(String value, String[] booleanTrueCases, String[] booleanFalseCases) {
        this.value = value;
        this.booleanTrueCases = booleanTrueCases;
        this.booleanFalseCases = booleanFalseCases;
        converter = new Converter();
    }

    /**
     * Returns {@code boolean} representation of the value.
     * Conversion occurs according to the following rules:<br>
     * 1. if the string matches any element of the {@link ValueConverter#booleanTrueCases}, then it will be
     * converted to {@code true};<br>
     * 2. otherwise, if the string matches any element of the {@link ValueConverter#booleanFalseCases}, then
     * it will be converted to {@code false};<br>
     * 3. otherwise an {@link ConverterException} will be thrown.
     *
     * @return boolean representation of the value
     * @throws ConverterException if value cant be represented as {@code boolean}.
     */
    public boolean toBoolean() throws ConverterException {
        return converter.toBoolean(value);
    }

    /**
     * Returns {@code char} representation of the value.
     * Conversion occurs according to the following rules:
     * 1. if the value consists of one character, then it will be returned as {@code char};
     * 2. if the length of the value is greater than 1, then first an attempt will be made
     * to convert it to {@code int}, then to {@code char} by {@link Character#forDigit(int, int)}.
     * In case of an error, an {@link ConverterException} will be thrown;
     * 3. if value is zero length, then an {@link ConverterException} will be thrown.
     *
     * @return char representation of the value
     * @throws ConverterException if value cant be represented as {@code char}.
     */
    public char toChar() throws ConverterException {
        return converter.toChar(value);
    }

    /**
     * Returns {@code int} representation of the value.
     *
     * @return int representation of the value
     * @throws ConverterException if value cant be represented as {@code int}.
     */
    public int toInt() throws ConverterException {
        return converter.toInt(value, RADIX_DEFAULT);
    }

    /**
     * Returns {@code int} representation of the value.
     *
     * @param radix the radix
     * @return int representation of the value
     * @throws ConverterException if value cant be represented as {@code int}.
     */
    public int toInt(int radix) throws ConverterException {
        return converter.toInt(value, radix);
    }

    /**
     * Returns {@code long} representation of the value.
     *
     * @return int representation of the value
     * @throws ConverterException if value cant be represented as {@code long}.
     */
    public long toLong() throws ConverterException {
        return converter.toLong(value, RADIX_DEFAULT);
    }

    /**
     * Returns {@code long} representation of the value.
     *
     * @param radix the radix
     * @return int representation of the value
     * @throws ConverterException if value cant be represented as {@code long}.
     */
    public long toLong(int radix) throws ConverterException {
        return converter.toLong(value, radix);
    }

    /**
     * Returns {@code float} representation of the value.
     *
     * @return int representation of the value
     * @throws ConverterException if value cant be represented as {@code float}.
     */
    public float toFloat() throws ConverterException {
        return converter.toFloat(value);
    }

    /**
     * Returns {@code double} representation of the value.
     *
     * @return int representation of the value
     * @throws ConverterException if value cant be represented as {@code double}.
     */
    public double toDouble() throws ConverterException {
        return converter.toDouble(value);
    }

    //ARRAYS

    /**
     * Converts value to the array of strings by delimiter ','.
     * To pass the character ',' as part of a string rather than as a delimiter, precede it
     * with an escape character '\'.<br>
     * 'qwe,asd,zxc' ~ {"qwe", "asd", "zxc"}<br>
     * 'qwe,asd\,zxc' ~ {"qwe", "asdzxc"}
     *
     * @return array of strings from value by delimiter ','
     * @throws ConverterException if value length is 0.
     */
    public String[] toArray() throws ConverterException {
        return converter.toArray(value);
    }

    /**
     * Converts value to {@code boolean} array. Each element of the string array will be converted
     * to {@code boolean} like in {@link ValueConverter#toBoolean()}.
     *
     * @return {@code boolean} array representation of value
     * @throws ConverterException if array cant be converted to {@code boolean} array.
     */
    public boolean[] toArrayBoolean() throws ConverterException {
        return converter.toArrayBoolean(value);
    }

    /**
     * Converts value to {@code char} array. Each element of the string array will be converted
     * to {@code char} like in {@link ValueConverter#toChar()}.
     *
     * @return {@code char} array representation of value
     * @throws ConverterException if array cant be converted to {@code char} array.
     */
    public char[] toArrayChar() throws ConverterException {
        return converter.toArrayChar(value);
    }

    /**
     * Converts value to {@code int} array. Each element of the string array will be converted
     * to {@code int} like in {@link ValueConverter#toInt()}.
     *
     * @return {@code int} array representation of value
     * @throws ConverterException if array cant be converted to {@code int} array.
     */
    public int[] toArrayInt() throws ConverterException {
        return converter.toArrayInt(value, RADIX_DEFAULT);
    }

    /**
     * Converts value to {@code int} array. Each element of the string array will be converted
     * to {@code int} like in {@link ValueConverter#toInt(int)}.
     *
     * @param radix the radix
     * @return {@code int} array representation of value
     * @throws ConverterException if array cant be converted to {@code int} array.
     */
    public int[] toArrayInt(int radix) throws ConverterException {
        return converter.toArrayInt(value, radix);
    }

    /**
     * Converts value to {@code long} array. Each element of the string array will be converted
     * to {@code long} like in {@link ValueConverter#toLong()}.
     *
     * @return {@code long} array representation of value
     * @throws ConverterException if array cant be converted to {@code long} array.
     */
    public long[] toArrayLong() throws ConverterException {
        return converter.toArrayLong(value, RADIX_DEFAULT);
    }

    /**
     * Converts value to {@code long} array. Each element of the string array will be converted
     * to {@code long} like in {@link ValueConverter#toLong(int)}.
     *
     * @param radix the radix
     * @return {@code long} array representation of value
     * @throws ConverterException if array cant be converted to {@code long} array.
     */
    public long[] toArrayLong(int radix) throws ConverterException {
        return converter.toArrayLong(value, radix);
    }

    /**
     * Converts value to {@code float} array. Each element of the string array will be converted
     * to {@code float} like in {@link ValueConverter#toFloat()}.
     *
     * @return {@code float} array representation of value
     * @throws ConverterException if array cant be converted to {@code float} array.
     */
    public float[] toArrayFloat() throws ConverterException {
        return converter.toArrayFloat(value);
    }

    /**
     * Converts value to {@code double} array. Each element of the string array will be converted
     * to {@code double} like in {@link ValueConverter#toLong()}.
     *
     * @return {@code double} array representation of value
     * @throws ConverterException if array cant be converted to {@code double} array.
     */
    public double[] toArrayDouble() throws ConverterException {
        return converter.toArrayDouble(value);
    }

    /**
     * An object that is designed to convert string values to simple types.
     */
    private class Converter {
        boolean toBoolean(String value) throws ConverterException {
            if (value == null) {
                throw new ConverterException("boolean", "null");
            }
            for (String s : booleanTrueCases) {
                if (value.equalsIgnoreCase(s)) {
                    return true;
                }
            }
            for (String s : booleanFalseCases) {
                if (value.equalsIgnoreCase(s)) {
                    return false;
                }
            }
            throw new ConverterException("boolean", value);
        }

        char toChar(String value) throws ConverterException {
            if (value == null) {
                throw new ConverterException("char", "null");
            } else if (value.length() == 0) {
                throw new ConverterException("char", "");
            } else if (value.length() == 1) {
                return value.charAt(0);
            } else {
                try {
                    return (char) toInt(value, 10);
                } catch (ConverterException e) {
                    throw new ConverterException("char", value);
                }
            }
        }

        int toInt(String value, int radix) throws ConverterException {
            try {
                return Integer.valueOf(value, radix);
            } catch (NumberFormatException e) {
                throw new ConverterException("int", value, e.getCause());
            }
        }

        long toLong(String value, int radix) throws ConverterException {
            try {
                return Long.valueOf(value, radix);
            } catch (NumberFormatException e) {
                throw new ConverterException("long", value, e.getCause());
            }
        }

        float toFloat(String value) throws ConverterException {
            try {
                return Float.valueOf(value);
            } catch (NumberFormatException e) {
                throw new ConverterException("float", value, e.getCause());
            }
        }

        double toDouble(String value) throws ConverterException {
            try {
                return Double.valueOf(value);
            } catch (NumberFormatException e) {
                throw new ConverterException("double", value, e.getCause());
            }
        }

        /**
         * Converts a string to a list of strings by delimiter ','.
         * To pass the character ',' as part of a string rather than as a delimiter, precede it
         * with an escape character '/'.<br>
         * 'qwe,asd,zxc' ~ {"qwe", "asd", "zxc"}<br>
         * 'qwe,asd/,zxc' ~ {"qwe", "asdzxc"}
         *
         * @param value string for conversion to list
         * @return list of strings from value by delimiter ','
         * @throws ConverterException if value is {@code null};
         *                            if value length is 0.
         */
        List<String> toList(String value) throws ConverterException {
            final char delimiter = ',';
            final char escapeChar = '/';
            if (value == null) {
                throw new ConverterException("list", "null");
            }
            List<String> res;
            if (value.length() == 0) {
                throw new ConverterException("list", "");
            } else if (value.length() == 1 || value.indexOf(delimiter) == -1) {
                res = new ArrayList<>();
                res.add(value);
                return res;
            }

            res = new ArrayList<>();
            int index = 0;
            int startIndex = 0;
            if (value.charAt(index) == ',') {
                res.add("");
                index = 1;
                startIndex = 1;
            }
            for (; index < value.length(); index++) {
                if (value.charAt(index) == delimiter) {
                    if (value.charAt(index - 1) != escapeChar) {
                        res.add(value.substring(startIndex, index).replaceAll(
                                String.valueOf(escapeChar) + String.valueOf(delimiter), String.valueOf(delimiter))
                        );
                        startIndex = index + 1;
                        if (index == value.length() - 1) {
                            res.add("");
                        }
                    }
                }
            }
            if (startIndex < value.length()) {
                res.add(value.substring(startIndex, value.length()).replaceAll(
                        String.valueOf(escapeChar) + String.valueOf(delimiter), String.valueOf(delimiter))
                );
            }
            return res;
        }

        String[] toArray(String value) throws ConverterException {
            List<String> values = toList(value);
            String[] arr = new String[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = values.get(i);
            }
            return arr;
        }

        boolean[] toArrayBoolean(String value) throws ConverterException {
            List<String> values = toList(value);
            boolean[] arr = new boolean[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = toBoolean(values.get(i));
            }
            return arr;
        }

        char[] toArrayChar(String value) throws ConverterException {
            List<String> values = toList(value);
            char[] arr = new char[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = toChar(values.get(i));
            }
            return arr;
        }

        int[] toArrayInt(String value, int radix) throws ConverterException {
            List<String> values = toList(value);
            int[] arr = new int[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = toInt(values.get(i), radix);
            }
            return arr;
        }

        long[] toArrayLong(String value, int radix) throws ConverterException {
            List<String> values = toList(value);
            long[] arr = new long[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = toLong(values.get(i), radix);
            }
            return arr;
        }

        float[] toArrayFloat(String value) throws ConverterException {
            List<String> values = toList(value);
            float[] arr = new float[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = toFloat(values.get(i));
            }
            return arr;
        }

        double[] toArrayDouble(String value) throws ConverterException {
            List<String> values = toList(value);
            double[] arr = new double[values.size()];
            for (int i = 0; i < values.size(); i++) {
                arr[i] = toDouble(values.get(i));
            }
            return arr;
        }
    }
}
