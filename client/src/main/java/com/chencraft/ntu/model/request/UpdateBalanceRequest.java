package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for services that allow a user to deposit or withdraw money.
 * This operation is non-idempotent as it modifies the account balance.
 */
@Data
@Schema(description = "Request to deposit or withdraw money")
public class UpdateBalanceRequest implements MySerializable {
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

    private Boolean depositFlag;

    @Override
    public OpCode getOpCode() {
        if (depositFlag == null) {
            throw new IllegalArgumentException("depositFlag must be set");
        }

        return depositFlag ? OpCode.OpDeposit : OpCode.OpWithdraw;
    }

    @Override
    public List<FieldDefn> getFieldDefs() {
        return List.of(FieldDefn.NAME, FieldDefn.PASSWORD, FieldDefn.ACCOUNT_NO, FieldDefn.CURRENCY, FieldDefn.ACCOUNT_NO);
    }
}
