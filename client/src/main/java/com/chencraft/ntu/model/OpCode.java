package com.chencraft.ntu.model;

import lombok.Getter;

@Getter
public enum OpCode {
    OpOpen(0x01),
    OpClose(0x02),
    OpDeposit(0x03),
    OpWithdraw(0x04),
    OpMonitor(0x05),
    OpBalance(0x06),
    OpTransfer(0x07);

    private final byte value;

    OpCode(int value) {
        this.value = (byte) value;
    }
}
