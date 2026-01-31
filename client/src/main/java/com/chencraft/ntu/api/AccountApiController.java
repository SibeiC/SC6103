package com.chencraft.ntu.api;

import com.chencraft.ntu.model.request.CloseAccountRequest;
import com.chencraft.ntu.model.request.MonitorRequest;
import com.chencraft.ntu.model.request.OpenAccountRequest;
import com.chencraft.ntu.model.response.DefaultResponse;
import com.chencraft.ntu.model.response.OpenAccountResponse;
import com.chencraft.ntu.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller implementation for the Account management API.
 * Delegates business logic to the BankingService.
 */
@RestController
public class AccountApiController implements AccountApi {
    private final BankingService bankingService;

    /**
     * Constructor for AccountApiController.
     *
     * @param bankingService the banking service to handle account operations
     */
    @Autowired
    public AccountApiController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<OpenAccountResponse> openAccount(OpenAccountRequest request) {
        Integer newAccountNo = bankingService.openAccount(request);

        OpenAccountResponse response = new OpenAccountResponse(newAccountNo);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<DefaultResponse> closeAccount(CloseAccountRequest request) {
        bankingService.closeAccount(request);

        return ResponseEntity.ok(new DefaultResponse());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<DefaultResponse> monitorAccounts(MonitorRequest request) {
        bankingService.registerMonitor(request);

        return ResponseEntity.ok(new DefaultResponse());
    }
}
