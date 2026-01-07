package com.chencraft.ntu.util;

/**
 * Utility class providing static methods for converting various data types
 * into byte array representations. This class is designed to support
 * serialization processes by handling the following data types:
 * - String
 * - Integer
 * - Double
 * <p>
 * Each method throws an {@code UnsupportedOperationException} by default,
 * indicating that the conversion logic needs to be implemented.
 */
public class Converter {
    public static byte[] toByteArray(String str) {
        throw new UnsupportedOperationException();
    }

    public static byte[] toByteArray(Integer i) {
        throw new UnsupportedOperationException();
    }

    public static byte[] toByteArray(Double d) {
        throw new UnsupportedOperationException();
    }

    public static Integer toInt(byte[] responseData) {
        throw new UnsupportedOperationException();
    }

    public static Double toDouble(byte[] responseData) {
        throw new UnsupportedOperationException();
    }
}
