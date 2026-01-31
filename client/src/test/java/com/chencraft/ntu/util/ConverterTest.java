package com.chencraft.ntu.util;

import com.chencraft.ntu.exception.OperationFailedException;
import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.service.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConverterTest {
    @BeforeEach
    public void setUp() {
        IdGenerator.reset();
    }

    @Test
    public void testString() {
        String value = "Hi 5!";
        byte[] expected = new byte[]{
                0b00000000, 0b00000000, 0b00000000, 0b00000101, // Denote string length of 5
                0x48, 0x69, 0x20, 0x35, 0x21};
        byte[] actual = Converter.toByteArray(value);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testInt() {
        int value = 0x0a0b0c0d;

        // For big-endian, this is 0A 0B 0C 0D
        byte[] expected = new byte[]{0b00001010, 0b00001011, 0b00001100, 0b00001101};
        byte[] actual = Converter.toByteArray(value);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testDouble() {
        double value = Double.parseDouble("42");

        byte[] expected = new byte[]{0b01000000, 0b01000101, 0b00000000, 0b00000000,
                0b00000000, 0b00000000, 0b00000000, 0b00000000};
        byte[] actual = Converter.toByteArray(value);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testCurrencyUSD() {
        Currency value = Currency.USD;
        byte[] expected = new byte[]{0b00000000};
        byte[] actual = Converter.toByteArray(value);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testCurrencyCNY() {
        Currency value = Currency.CNY;
        byte[] expected = new byte[]{0b00000100};
        byte[] actual = Converter.toByteArray(value);

        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void testUnmarshallingInt() {
        byte[] data = new byte[]{
                0x01,                   // Message Type: MsgResponse
                0x00, 0x00, 0x00, 0x01, // Request ID: 1
                0x01,                   // Operation Code: Open Account
                0x0A, 0x0B, 0x0C, 0x0D  // Account ID: 168496141
        };
        int actual = Converter.toInt(data);
        int expected = 168496141;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testUnmarshallingDouble() {
        byte[] data = new byte[]{
                0x01,                   // Message Type: MsgResponse
                0x00, 0x00, 0x00, 0x02, // Request ID: 2
                0x03,                   // Operation Code: Deposit
                0b01000000, 0b01000101, 0b00000000, 0b00000000,
                0b00000000, 0b00000000, 0b00000000, 0b00000000  // Balance: 42
        };
        double actual = Converter.toDouble(data);
        double expected = 42;

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testUnmarshallingString() {
        byte[] data = new byte[]{
                0x03,                   // Message Type: MsgCallback
                0x00, 0x00, 0x00, 0x03, // Request ID: 3
                0x05,                   // Operation Code: Monitor
                0b00000000, 0b00000000, 0b00000000, 0b00000101, // Denote string length of 5
                0x48, 0x69, 0x20, 0x35, 0x21    // String: Hi 5!
        };
        String actual = Converter.toString(data);
        String expected = "Hi 5!";

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testUnmarshallingErrorMsg() {
        String expected = "Incorrect Password";

        try {
            byte[] data = new byte[]{
                    0x02,                   // Message Type: MsgResponse
                    0x00, 0x00, 0x00, 0x04, // Request ID: 4
                    0x04,                   // Operation Code: Withdraw
                    0b00000000, 0b00000000, 0b00000000, 0b00010010, // Denote string length of 18
                    0x49, 0x6E, 0x63, 0x6F,
                    0x72, 0x72, 0x65, 0x63,
                    0x74, 0x20, 0x50, 0x61,
                    0x73, 0x73, 0x77, 0x6F,
                    0x72, 0x64              // String: Incorrect Password
            };
            Converter.toString(data);
        } catch (OperationFailedException e) {
            String msg = e.getMessage();
            Assertions.assertEquals(expected, msg);
        }
    }
}
