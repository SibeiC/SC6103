package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for a non-idempotent operation to transfer funds between accounts.
 */
@Data
public class TransferRequest implements MySerializable {
    /**
     * Name of the sender (account holder of the source account).
     */
    private String name;

    /**
     * Source account number from which funds will be deducted.
     */
    private Integer accountNumber;

    /**
     * Password of the source account for authentication.
     */
    private String password;

    /**
     * Target account number which will receive the funds.
     */
    private Integer destAccountNumber;

    /**
     * Currency type of the transfer.
     */
    private Currency currency;

    /**
     * The amount of money to transfer.
     */
    private Double amount;

    @Override
    public OpCode getOpCode() {
        return OpCode.OpTransfer;
    }

    @Override
    public List<FieldDefn> getFieldDefs() {
        return List.of(FieldDefn.NAME, FieldDefn.PASSWORD, FieldDefn.ACCOUNT_NO, FieldDefn.DEST_ACCOUNT_NO, FieldDefn.CURRENCY, FieldDefn.AMOUNT);
    }
}
