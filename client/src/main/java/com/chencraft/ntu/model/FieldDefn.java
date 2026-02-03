package com.chencraft.ntu.model;

import lombok.Getter;

@Getter
public enum FieldDefn {
    NAME("name", String.class),
    PASSWORD("password", String.class),
    ACCOUNT_NO("accountNumber", Integer.class),
    CURRENCY("currency", Currency.class),
    INITIAL_BALANCE("initialBalance", Double.class),
    MONITOR_INTERVAL("monitorInterval", Integer.class),
    DEST_ACCOUNT_NO("destAccountNumber", Integer.class),
    AMOUNT("amount", Double.class);

    private final String fieldName;
    private final Class<?> fieldType;

    FieldDefn(String fieldName, Class<?> fieldType) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }
}
