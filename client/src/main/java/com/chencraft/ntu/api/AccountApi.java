package com.chencraft.ntu.api;

import com.chencraft.ntu.model.request.CloseAccountRequest;
import com.chencraft.ntu.model.request.MonitorRequest;
import com.chencraft.ntu.model.request.OpenAccountRequest;
import com.chencraft.ntu.model.response.GenericResponse;
import com.chencraft.ntu.model.response.OpenAccountResponse;
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

import static com.chencraft.ntu.constant.Tags.ACCOUNT;

/**
 * Interface for the Account management API.
 * Provides services to open, close, and monitor accounts.
 */
@Validated
@RequestMapping("/account")
public interface AccountApi {
    /**
     * Service to open a new account.
     *
     * @param request the account opening details (name, password, currency, initial balance)
     * @return a response containing the new account number
     */
    @Operation(summary = "Open a new account", description = "Creates a new account and returns the account number.", tags = ACCOUNT)
    @ApiResponse(responseCode = "200", description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = OpenAccountResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @RequestMapping(value = "/open", method = RequestMethod.POST)
    ResponseEntity<OpenAccountResponse> openAccount(@RequestBody OpenAccountRequest request);

    /**
     * Service to close an existing account.
     *
     * @param request the account closing details (name, account number, password)
     * @return a generic acknowledgment message
     */
    @Operation(summary = "Close an existing account", description = "Closes an existing account.", tags = ACCOUNT)
    @ApiResponse(responseCode = "200", description = "Account closed successfully",
            content = @Content(schema = @Schema(implementation = GenericResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @RequestMapping(value = "/close", method = RequestMethod.DELETE)
    ResponseEntity<GenericResponse> closeAccount(@RequestBody CloseAccountRequest request);

    /**
     * Service to register for monitoring account updates.
     * The client will receive callbacks for account updates during the designated monitor interval.
     *
     * @param request the length of the monitor interval in seconds
     * @return a generic acknowledgment message
     */
    @Operation(summary = "Monitor accounts for updates", description = "Monitors accounts for updates and returns the updated balance.", tags = ACCOUNT)
    @ApiResponse(responseCode = "200", description = "Account balance updated successfully",
            content = @Content(schema = @Schema(implementation = GenericResponse.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    @RequestMapping(value = "/monitor", method = RequestMethod.POST)
    ResponseEntity<GenericResponse> monitorAccounts(@RequestBody MonitorRequest request);
}
