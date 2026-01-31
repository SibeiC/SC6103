package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.service.IdGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OpenAccountRequestTest {
    @BeforeEach
    public void setUp() {
        IdGenerator.reset();
    }

    @Test
    public void validRequest() {
        OpenAccountRequest request = new OpenAccountRequest();
        request.setName("John Doe");
        request.setPassword("qwer1234");
        request.setCurrency(Currency.USD);
        request.setInitialBalance(1000.0);

        byte[] expected = new byte[]{
                0x00,                       // Message Type
                0x00, 0x00, 0x00, 0x00,     // Message ID
                0x01,                       // Operation ID
                // ==================== Body ====================
                // Name
                0x00, 0x00, 0x00, 0x08,     // Length of name string
                0x4A, 0x6F, 0x68, 0x6E, 0x20, 0x44, 0x6F, 0x65,
                // Password
                0x00, 0x00, 0x00, 0x08,     // Length of password string
                0x71, 0x77, 0x65, 0x72, 0x31, 0x32, 0x33, 0x34,
                // Currency Flag
                0x00,                       // Currency Flag = USD
                // Initial Balance
                0x40, (byte) 0x8f, 0x40, 0x00,
                0x00, 0x00, 0x00, 0x00      // Initial Balance = 1000.0
        };

        assertMarshallingSuccess(request, expected);
    }

    private void assertMarshallingSuccess(OpenAccountRequest request, byte[] expectedBytes) {
        Assertions.assertNotNull(request);
        Assertions.assertNotNull(expectedBytes);

        byte[] actualBytes = request.marshall();
        Assertions.assertArrayEquals(expectedBytes, actualBytes);
    }
}
