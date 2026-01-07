package com.chencraft.ntu.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enumerated type representing supported currencies in the banking system.
 */
@Schema(description = "Type of currency")
public enum Currency implements MySerializable {
    /**
     * US Dollar
     */
    USD,
    /**
     * Euro
     */
    EUR,
    /**
     * British Pound
     */
    GBP,
    /**
     * Singapore Dollar
     */
    SGD,
    /**
     * Chinese Yuan
     */
    CNY;

    @Override
    public byte[] marshall() {
        throw new UnsupportedOperationException("Not supported yet.");
//        return Converter.toByteArray(this.name());
    }
}
