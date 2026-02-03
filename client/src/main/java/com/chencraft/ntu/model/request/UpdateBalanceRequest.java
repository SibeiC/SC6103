package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for services that allow a user to deposit or withdraw money.
 * This operation is non-idempotent as it modifies the account balance.
 */
@Data
public class UpdateBalanceRequest implements MySerializable {
    /**
     * Name of the account holder.
     */
    private String name;

    /**
     * The account number where the transaction will occur.
     */
    private Integer accountNumber;

    /**
     * Password to authenticate the transaction.
     */
    private String password;

    /**
     * The type of currency for the transaction.
     */
    private Currency currency;

    /**
     * The amount to be processed.
     * Interpretation depends on the specific service (deposit/withdraw).
     */
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
