package com.chencraft.ntu.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response object returned after successfully opening a new account.
 * Contains the generated unique account number.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Response to open a new account")
public class OpenAccountResponse extends GenericResponse {
    /**
     * The unique account number assigned to the new account.
     */
    @Schema(description = "Account number of the newly opened account", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer accountNumber;
}
