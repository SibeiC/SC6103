package com.chencraft.ntu.util;

import com.chencraft.ntu.exception.OperationFailedException;
import com.chencraft.ntu.model.MessageType;
import com.chencraft.ntu.model.OpCode;
import com.chencraft.ntu.model.response.DoubleResponse;
import com.chencraft.ntu.model.response.GenericResponse;
import com.chencraft.ntu.model.response.IntResponse;
import com.chencraft.ntu.model.response.StringResponse;

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
     * Converts long to a 4-byte array (truncates to lower 32 bits for unsigned int compatibility).
     */
    public static byte[] toByteArray(Long l) {
        byte[] bytes = new byte[4];
        int value = l.intValue();  // Truncate to 32 bits
        int offset = 0;
        if (ByteOrder.BIG_ENDIAN.equals(endian)) {
            bytes[offset] = (byte) (value >>> 24);
            bytes[offset + 1] = (byte) (value >>> 16);
            bytes[offset + 2] = (byte) (value >>> 8);
            bytes[offset + 3] = (byte) value;
        } else {
            bytes[offset] = (byte) value;
            bytes[offset + 1] = (byte) (value >>> 8);
            bytes[offset + 2] = (byte) (value >>> 16);
            bytes[offset + 3] = (byte) (value >>> 24);
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

    public static int byteArrayToInt(byte[] bytes, int offset) {
        if (endian == ByteOrder.BIG_ENDIAN) {
            return ((bytes[offset] & 0xFF) << 24) | ((bytes[offset + 1] & 0xFF) << 16) | ((bytes[offset + 2] & 0xFF) << 8) | (bytes[offset + 3] & 0xFF);
        } else {
            return ((bytes[offset + 3] & 0xFF) << 24) | ((bytes[offset + 2] & 0xFF) << 16) | ((bytes[offset + 1] & 0xFF) << 8) | (bytes[offset] & 0xFF);
        }
    }

    /**
     * Converts byte array to unsigned 32-bit integer (returned as long).
     */
    public static long byteArrayToUnsignedInt(byte[] bytes, int offset) {
        return Integer.toUnsignedLong(byteArrayToInt(bytes, offset));
    }

    private static double byteArrayToDouble(byte[] bytes, int offset) {
        if (endian == ByteOrder.BIG_ENDIAN) {
            return Double.longBitsToDouble(
                ((long) (bytes[offset] & 0xFF) << 56) | 
                ((long) (bytes[offset + 1] & 0xFF) << 48) | 
                ((long) (bytes[offset + 2] & 0xFF) << 40) | 
                ((long) (bytes[offset + 3] & 0xFF) << 32) | 
                ((long) (bytes[offset + 4] & 0xFF) << 24) | 
                ((long) (bytes[offset + 5] & 0xFF) << 16) | 
                ((long) (bytes[offset + 6] & 0xFF) << 8) | 
                ((long) (bytes[offset + 7] & 0xFF)));
        } else {
            return Double.longBitsToDouble(
                ((long) (bytes[offset + 7] & 0xFF) << 56) | 
                ((long) (bytes[offset + 6] & 0xFF) << 48) | 
                ((long) (bytes[offset + 5] & 0xFF) << 40) | 
                ((long) (bytes[offset + 4] & 0xFF) << 32) | 
                ((long) (bytes[offset + 3] & 0xFF) << 24) | 
                ((long) (bytes[offset + 2] & 0xFF) << 16) | 
                ((long) (bytes[offset + 1] & 0xFF) << 8) | 
                ((long) (bytes[offset] & 0xFF)));
        }
    }

    private static String byteArrayToString(byte[] bytes, int offset) {
        int length = byteArrayToInt(bytes, offset);
        StringBuilder sb = new StringBuilder();
        for (int i = offset + 4; i < offset + 4 + length; i++) {
            sb.append((char) bytes[i]);
        }
        return sb.toString();
    }

    private static GenericResponse unmarshalResponse(byte[] responseData, Class<?> responseType) {
        if (responseData == null || responseData.length < 6) {
            throw new IllegalArgumentException("Invalid response data");
        }

        // Byte 0: Message Type
        MessageType msgType = MessageType.values()[responseData[0]];

        // Byte 1-4: Request ID
        int reqId = byteArrayToInt(responseData, 1);

        // Byte 5: Operation Code
        OpCode opCode = OpCode.fromByte(responseData[5]);

        // ================================ Body ================================
        if (msgType == MessageType.MsgRequest) {
            throw new IllegalArgumentException("Invalid response data: Message Type is MsgRequest");
        }

        if (msgType == MessageType.MsgError || responseType == String.class) {
            String msg = byteArrayToString(responseData, 6);
            if (msgType == MessageType.MsgError) {
                throw new OperationFailedException(msg);
            }
            return StringResponse.builder()
                                 .messageType(msgType)
                                 .requestId(reqId)
                                 .operationCode(opCode)
                                 .value(msg)
                                 .build();
        } else if (responseType == Long.class) {
            long value = byteArrayToUnsignedInt(responseData, 6);
            return IntResponse.builder()
                              .messageType(msgType)
                              .requestId(reqId)
                              .operationCode(opCode)
                              .value(value)
                              .build();
        } else if (responseType == Double.class) {
            double value = byteArrayToDouble(responseData, 6);
            return DoubleResponse.builder()
                                 .messageType(msgType)
                                 .requestId(reqId)
                                 .operationCode(opCode)
                                 .value(value)
                                 .build();
        } else {
            throw new IllegalArgumentException("Unsupported response type: " + responseType);
        }
    }

    public static Long toLong(byte[] responseData) {
        return ((IntResponse) unmarshalResponse(responseData, Long.class)).getValue();
    }

    public static Double toDouble(byte[] responseData) {
        return ((DoubleResponse) unmarshalResponse(responseData, Double.class)).getValue();
    }

    public static String toString(byte[] responseData) {
        return ((StringResponse) unmarshalResponse(responseData, String.class)).getValue();
    }
}
