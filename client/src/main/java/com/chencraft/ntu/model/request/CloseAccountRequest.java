package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for the service that allows a user to close an existing account.
 */
@Data
@Schema(description = "Request to close an existing account")
public class CloseAccountRequest implements MySerializable {
    /**
     * Name of the account holder.
     */
    @Schema(description = "Name of the account holder", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * The account number to be closed.
     */
    @Schema(description = "Account number to close", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer accountNumber;

    /**
     * Password to authenticate the account closure.
     */
    @Schema(description = "Password for the account", example = "secure123", requiredMode = Schema.RequiredMode.REQUIRED)
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
