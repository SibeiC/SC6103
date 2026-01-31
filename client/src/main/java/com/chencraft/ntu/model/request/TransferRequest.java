package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.model.FieldDefn;
import com.chencraft.ntu.model.MySerializable;
import com.chencraft.ntu.model.OpCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Data transfer object for a non-idempotent operation to transfer funds between accounts.
 */
@Data
@Schema(description = "Request to transfer funds between accounts")
public class TransferRequest implements MySerializable {
    /**
     * Name of the sender (account holder of the source account).
     */
    @Schema(description = "Name of the sender", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * Source account number from which funds will be deducted.
     */
    @Schema(description = "Sender's account number", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer accountNumber;

    /**
     * Password of the source account for authentication.
     */
    @Schema(description = "Password for the sender's account", example = "secure123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * Target account number which will receive the funds.
     */
    @Schema(description = "Receiver's account number", example = "1002", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer destAccountNumber;

    /**
     * Currency type of the transfer.
     */
    @Schema(description = "Currency type", requiredMode = Schema.RequiredMode.REQUIRED)
    private Currency currency;

    /**
     * The amount of money to transfer.
     */
    @Schema(description = "Amount to transfer", example = "20.0", requiredMode = Schema.RequiredMode.REQUIRED)
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
