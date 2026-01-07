package com.chencraft.ntu.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response object returned after a deposit, withdrawal, or balance inquiry.
 * Contains the updated or current balance of the account.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Response to deposit or withdraw money")
public class UpdateBalanceResponse extends GenericResponse {
    /**
     * The current balance of the account after the operation.
     */
    @Schema(description = "Current account balance", example = "150.0")
    private final Double balance;
}
