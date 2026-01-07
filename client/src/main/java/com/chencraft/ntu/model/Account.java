package com.chencraft.ntu.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Represents a bank account in the distributed banking system.
 * Each account is characterized by an account number, holder name, password, currency, and balance.
 */
@Data
@Schema(description = "Account information")
public class Account {
    /**
     * Unique account number assigned by the server.
     */
    @Schema(description = "Account number", example = "1001")
    private Integer accountNumber;

    /**
     * Name of the account holder.
     */
    @Schema(description = "Name of the account holder", example = "John Doe")
    private String holderName;

    /**
     * Fixed-length password for the account.
     */
    @Schema(description = "Password for the account", example = "secure123")
    private String password;

    /**
     * The type of currency used in this account.
     */
    @Schema(description = "Currency type")
    private Currency currency;

    /**
     * The current balance of the account.
     */
    @Schema(description = "Current account balance", example = "1000.50")
    private Double balance;
}
