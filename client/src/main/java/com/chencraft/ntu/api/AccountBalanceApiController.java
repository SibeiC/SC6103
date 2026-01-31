package com.chencraft.ntu.api;

import com.chencraft.ntu.model.request.GetBalanceRequest;
import com.chencraft.ntu.model.request.TransferRequest;
import com.chencraft.ntu.model.request.UpdateBalanceRequest;
import com.chencraft.ntu.model.response.TransferResponse;
import com.chencraft.ntu.model.response.UpdateBalanceResponse;
import com.chencraft.ntu.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller implementation for the Account Balance API.
 * Delegates business logic to the BankingService.
 */
@RestController
public class AccountBalanceApiController implements AccountBalanceApi {
    private final BankingService bankingService;

    /**
     * Constructor for AccountBalanceApiController.
     *
     * @param bankingService the banking service to handle balance operations
     */
    @Autowired
    public AccountBalanceApiController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<UpdateBalanceResponse> deposit(UpdateBalanceRequest request) {
        request.setDepositFlag(true);
        Double balance = bankingService.deposit(request);

        UpdateBalanceResponse response = new UpdateBalanceResponse(balance);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<UpdateBalanceResponse> withdrawal(UpdateBalanceRequest request) {
        request.setDepositFlag(false);
        Double balance = bankingService.withdrawal(request);

        UpdateBalanceResponse response = new UpdateBalanceResponse(balance);
        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<TransferResponse> transferFunds(TransferRequest request) {
        TransferResponse response = bankingService.transfer(request);

        return ResponseEntity.ok(response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<UpdateBalanceResponse> inquiryBalance(GetBalanceRequest request) {
        Double balance = bankingService.getBalance(request);

        UpdateBalanceResponse response = new UpdateBalanceResponse(balance);
        return ResponseEntity.ok(response);
    }
}
