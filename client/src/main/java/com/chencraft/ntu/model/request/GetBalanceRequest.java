package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.MySerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data transfer object for an idempotent operation to retrieve the current balance of an account.
 */
@Data
@Schema(description = "Request to query account balance")
public class GetBalanceRequest implements MySerializable {
    /**
     * Name of the account holder.
     */
    @Schema(description = "Name of the account holder", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * The account number to query.
     */
    @Schema(description = "Account number", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer accountNumber;

    /**
     * Password to authenticate the balance query.
     */
    @Schema(description = "Password for the account", example = "secure123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
