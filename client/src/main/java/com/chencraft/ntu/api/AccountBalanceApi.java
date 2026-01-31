package com.chencraft.ntu.api;

import com.chencraft.ntu.constant.Tags;
import com.chencraft.ntu.model.request.GetBalanceRequest;
import com.chencraft.ntu.model.request.TransferRequest;
import com.chencraft.ntu.model.request.UpdateBalanceRequest;
import com.chencraft.ntu.model.response.TransferResponse;
import com.chencraft.ntu.model.response.UpdateBalanceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Interface for the Account Balance API.
 * Provides services for balance updates, fund transfers, and balance inquiries.
 */
@Validated
@RequestMapping("/balance")
public interface AccountBalanceApi {
    /**
     * Service to deposit the balance of an account.
     * This is a non-idempotent operation.
     *
     * @param request the balance update details (name, account number, password, currency, amount)
     * @return a response containing the updated balance
     */
    @Operation(summary = "Deposit money into an account", description = "Deposits the specified amount into the account.", tags = {Tags.BALANCE})
    @ApiResponse(responseCode = "200", description = "Deposited successfully",
            content = @Content(schema = @Schema(implementation = UpdateBalanceResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    ResponseEntity<UpdateBalanceResponse> deposit(@RequestBody UpdateBalanceRequest request);

    /**
     * Service to withdraw the balance of an account.
     * This is a non-idempotent operation.
     *
     * @param request the balance update details (name, account number, password, currency, amount)
     * @return a response containing the updated balance
     */
    @Operation(summary = "Withdraw money from an account", description = "Withdraws the specified amount from the account.", tags = {Tags.BALANCE})
    @ApiResponse(responseCode = "200", description = "Withdrawal successful",
            content = @Content(schema = @Schema(implementation = UpdateBalanceResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @RequestMapping(value = "/withdrawal", method = RequestMethod.POST)
    ResponseEntity<UpdateBalanceResponse> withdrawal(@RequestBody UpdateBalanceRequest request);

    /**
     * Service to transfer funds between two accounts.
     * This is a non-idempotent operation.
     *
     * @param request the transfer details (sender, receiver, amount, password)
     * @return a response containing the updated balances of both accounts
     */
    @Operation(summary = "Transfer funds between accounts", description = "Transfers the specified amount from one account to another. This action is non-idempotent.", tags = {Tags.BALANCE})
    @ApiResponse(responseCode = "200", description = "Transfer successful",
            content = @Content(schema = @Schema(implementation = TransferResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    ResponseEntity<TransferResponse> transferFunds(@RequestBody TransferRequest request);

    /**
     * Service to query the current balance of an account.
     * This is an idempotent operation.
     *
     * @param request the balance inquiry details (name, account number, password)
     * @return a response containing the current balance
     */
    @Operation(summary = "Inquiry balance of an account", description = "Queries the current balance of the specified account. This action is idempotent.", tags = {Tags.BALANCE})
    @ApiResponse(responseCode = "200", description = "Balance retrieved successfully",
            content = @Content(schema = @Schema(implementation = UpdateBalanceResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @RequestMapping(value = "/inquiry", method = RequestMethod.POST)
    ResponseEntity<UpdateBalanceResponse> inquiryBalance(@RequestBody GetBalanceRequest request);
}
