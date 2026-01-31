package com.chencraft.ntu.service;

import com.chencraft.ntu.model.request.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that handles the core business logic for the Distributed Banking System.
 * It manages account storage, performs transactions, and handles client monitoring registrations.
 * All operations are designed to be thread-safe.
 */
@Slf4j
@Service
public class BankingService {
    private final SocketService socketService;

    @Autowired
    public BankingService(SocketService socketService) {
        this.socketService = socketService;
    }

    /**
     * Opens a new account with the specified details.
     * Generates a unique account number and stores the account.
     *
     * @param request the account details (name, password, currency, initial balance)
     * @return the generated unique account number
     */
    public Integer openAccount(OpenAccountRequest request) {
        return socketService.sendAndReceiveInt(request);
    }

    /**
     * Closes an existing account.
     * Validates the name and password before removal.
     *
     * @param request the account closing details
     */
    public void closeAccount(CloseAccountRequest request) {
        socketService.sendAndForget(request);
    }

    /**
     * Deposits funds into an existing account.
     *
     * @param request the update details
     * @return the updated balance
     */
    public Double deposit(UpdateBalanceRequest request) {
        return socketService.sendAndReceiveDouble(request);
    }

    /**
     * Withdraws funds from an existing account.
     *
     * @param request the update details
     * @return the updated balance
     */
    public Double withdrawal(UpdateBalanceRequest request) {
        return socketService.sendAndReceiveDouble(request);
    }

    /**
     * Retrieves the current balance of an account.
     * This is an idempotent operation.
     *
     * @param request the inquiry details
     * @return the current balance
     */
    public Double getBalance(GetBalanceRequest request) {
        return socketService.sendAndReceiveDouble(request);
    }

    /**
     * Transfers funds between two accounts.
     * This is a non-idempotent operation.
     *
     * @param request the transfer details
     * @return a response containing updated balances for both accounts
     */
    public Double transfer(TransferRequest request) {
        return socketService.sendAndReceiveDouble(request);
    }

    /**
     * Registers a client for monitoring account updates.
     *
     * @param request the monitor interval details
     */
    public void registerMonitor(MonitorRequest request) {
        socketService.sendAndForget(request);
    }
}
