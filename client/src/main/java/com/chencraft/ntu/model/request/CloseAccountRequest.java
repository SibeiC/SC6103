package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for the service that allows a user to close an existing account.
 */
@Data
public class CloseAccountRequest implements MySerializable {
    /**
     * Name of the account holder.
     */
    private String name;

    /**
     * The account number to be closed.
     */
    private Integer accountNumber;

    /**
     * Password to authenticate the account closure.
     */
    private String password;

    @Override
    public OpCode getOpCode() {
        return OpCode.OpClose;
    }

    @Override
    public List<FieldDefn> getFieldDefs() {
        return List.of(FieldDefn.NAME, FieldDefn.PASSWORD, FieldDefn.ACCOUNT_NO);
    }
}
