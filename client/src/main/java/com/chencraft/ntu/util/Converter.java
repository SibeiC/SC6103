package com.chencraft.ntu.util;

import java.nio.ByteOrder;

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
    private static final ByteOrder endian = ByteOrder.BIG_ENDIAN;

    /**
     * Converts string to a byte array; prepends length
     */
    public static byte[] toByteArray(String str) {
        byte[] bytes = new byte[str.length() + 4];

        byte[] lengBytes = toByteArray(str.length());
        System.arraycopy(lengBytes, 0, bytes, 0, lengBytes.length);

        int i = 4;

        for (char c : str.toCharArray()) {
            if (c > 127) {
                throw new IllegalArgumentException("String contains non-ASCII characters");
            }
            bytes[i++] = (byte) c;
        }

        return bytes;
    }

    /**
     * Converts integer to a byte array.
     */
    public static byte[] toByteArray(Integer i) {
        byte[] bytes = new byte[4];
        int offset = 0;
        if (ByteOrder.BIG_ENDIAN.equals(endian)) {
            bytes[offset] = (byte) (i >>> 24);
            bytes[offset + 1] = (byte) (i >>> 16);
            bytes[offset + 2] = (byte) (i >>> 8);
            bytes[offset + 3] = (byte) i.intValue();
        } else {
            bytes[offset] = (byte) i.intValue();
            bytes[offset + 1] = (byte) (i >>> 8);
            bytes[offset + 2] = (byte) (i >>> 16);
            bytes[offset + 3] = (byte) (i >>> 24);
        }
        return bytes;
    }

    /**
     * Converts double to a byte array.
     */
    public static byte[] toByteArray(Double d) {
        long bits = Double.doubleToRawLongBits(d);
        byte[] bytes = new byte[8];

        if (ByteOrder.BIG_ENDIAN.equals(endian)) {
            bytes[0] = (byte) (bits >>> 56);
            bytes[1] = (byte) (bits >>> 48);
            bytes[2] = (byte) (bits >>> 40);
            bytes[3] = (byte) (bits >>> 32);
            bytes[4] = (byte) (bits >>> 24);
            bytes[5] = (byte) (bits >>> 16);
            bytes[6] = (byte) (bits >>> 8);
            bytes[7] = (byte) bits;
        } else {
            bytes[0] = (byte) bits;
            bytes[1] = (byte) (bits >>> 8);
            bytes[2] = (byte) (bits >>> 16);
            bytes[3] = (byte) (bits >>> 24);
            bytes[4] = (byte) (bits >>> 32);
            bytes[5] = (byte) (bits >>> 40);
            bytes[6] = (byte) (bits >>> 48);
            bytes[7] = (byte) (bits >>> 56);
        }

        return bytes;
    }

    public static <E extends Enum<E>> byte[] toByteArray(E e) {
        return new byte[]{(byte) e.ordinal()};
    }

    public static Integer toInt(byte[] responseData) {
        throw new UnsupportedOperationException();
    }

    public static Double toDouble(byte[] responseData) {
        throw new UnsupportedOperationException();
    }
}
