package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for the service that allows a user to open a new account.
 */
@Data
public class OpenAccountRequest implements MySerializable {
    /**
     * Name of the person opening the account.
     */
    private String name;

    /**
     * Password to secure the new account.
     */
    private String password;

    /**
     * The type of currency for the new account.
     */
    private Currency currency;

    /**
     * The initial deposit amount.
     */
    private Double initialBalance;

    @Override
    public OpCode getOpCode() {
        return OpCode.OpOpen;
    }

    @Override
    public List<FieldDefn> getFieldDefs() {
        return List.of(FieldDefn.NAME, FieldDefn.PASSWORD, FieldDefn.CURRENCY, FieldDefn.INITIAL_BALANCE);
    }
}
