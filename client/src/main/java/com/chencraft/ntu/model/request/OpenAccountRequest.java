package com.chencraft.ntu.model.request;

import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.model.MySerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Data transfer object for the service that allows a user to open a new account.
 */
@Data
@Schema(description = "Request to open a new account")
public class OpenAccountRequest implements MySerializable {
    /**
     * Name of the person opening the account.
     */
    @Schema(description = "Name of the account holder", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * Password to secure the new account.
     */
    @Schema(description = "Password for the new account", example = "secure123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /**
     * The type of currency for the new account.
     */
    @Schema(description = "Currency type", requiredMode = Schema.RequiredMode.REQUIRED)
    private Currency currency;

    /**
     * The initial deposit amount.
     */
    @Schema(description = "Initial account balance", example = "100.0", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double initialBalance;
}
