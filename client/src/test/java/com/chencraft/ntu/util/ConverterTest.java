package com.chencraft.ntu.util;

import com.chencraft.ntu.model.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConverterTest {
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
}
