package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for an idempotent operation to retrieve the current balance of an account.
 */
@Data
public class GetBalanceRequest implements MySerializable {
    /**
     * Name of the account holder.
     */
    private String name;

    /**
     * The account number to query.
     */
    private Integer accountNumber;

    /**
     * Password to authenticate the balance query.
     */
    private String password;

    @Override
    public OpCode getOpCode() {
        return OpCode.OpBalance;
    }

    @Override
    public List<FieldDefn> getFieldDefs() {
        return List.of(FieldDefn.NAME, FieldDefn.PASSWORD, FieldDefn.ACCOUNT_NO);
    }
}
