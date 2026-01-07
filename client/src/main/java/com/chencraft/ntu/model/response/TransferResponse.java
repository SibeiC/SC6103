package com.chencraft.ntu.model.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Response object returned after a fund transfer operation.
 * Provides the updated balances for both source and destination accounts.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "Response to transfer funds between accounts")
public class TransferResponse extends GenericResponse {
    /**
     * The account number from which funds were transferred.
     */
    @Schema(description = "Source account number")
    private Integer originAccountNumber;

    /**
     * The updated balance of the source account.
     */
    @Schema(description = "Updated balance of source account")
    private Double originBalance;

    /**
     * The account number that received the funds.
     */
    @Schema(description = "Destination account number")
    private Integer destinationAccountNumber;

    /**
     * The updated balance of the destination account.
     */
    @Schema(description = "Updated balance of destination account")
    private Double destinationBalance;
}
