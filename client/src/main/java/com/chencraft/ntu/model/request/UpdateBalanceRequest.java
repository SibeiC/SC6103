package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data transfer object for services that allow a user to deposit or withdraw money.
 * This operation is non-idempotent as it modifies the account balance.
 */
@Data
@Schema(description = "Request to deposit or withdraw money")
public class UpdateBalanceRequest {
    /**
     * Name of the account holder.
     */
    @Schema(description = "Name of the account holder", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * The account number where the transaction will occur.
     */
    @Schema(description = "Account number", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer accountNumber;

    /**
     * Password to authenticate the transaction.
     */
    @Schema(description = "Password for the account", example = "secure123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * The type of currency for the transaction.
     */
    @Schema(description = "Currency type", requiredMode = Schema.RequiredMode.REQUIRED)
    private Currency currency;

    /**
     * The amount to be processed.
     * Interpretation depends on the specific service (deposit/withdraw).
     */
    @Schema(description = "Amount to deposit or withdraw", example = "50.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double amount;
}
