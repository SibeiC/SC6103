package com.chencraft.ntu.cli;

import com.chencraft.ntu.exception.OperationFailedException;
import com.chencraft.ntu.model.Currency;
import com.chencraft.ntu.model.request.*;
import com.chencraft.ntu.service.BankingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Scanner;

/**
 * Command Line Interface for the Distributed Banking System.
 * Provides a user-friendly way to interact with banking services via console.
 */
@Slf4j
@Component
public class BankingCli {
    private final BankingService bankingService;

    @Autowired
    public BankingCli(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    /**
     * Starts the CLI interactive loop.
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        printHeader();

        while (true) {
            System.out.print("BANKING-CLI> ");
            if (!scanner.hasNextLine()) break;
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0].toLowerCase();

            try {
                switch (command) {
                    case "open" -> handleOpen(parts);
                    case "close" -> handleClose(parts);
                    case "deposit" -> handleDeposit(parts);
                    case "withdraw" -> handleWithdraw(parts);
                    case "balance" -> handleBalance(parts);
                    case "transfer" -> handleTransfer(parts);
                    case "monitor" -> handleMonitor(parts);
                    case "help" -> printHelp();
                    case "exit", "quit" -> {
                        System.out.println("Exiting CLI...");
                        return;
                    }
                    default ->
                            System.out.println("Unknown command: " + command + ". Type 'help' for available commands.");
                }
            } catch (OperationFailedException e) {
                System.out.println("[Operation Failed] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
        }
    }

    private void printHeader() {
        System.out.println("=================================================");
        System.out.println("   Distributed Banking System CLI Interface      ");
        System.out.println("=================================================");
        System.out.println("Type 'help' to see available commands.");
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("  open <name> <password> <currency> <initialBalance>");
        System.out.println("  close <name> <accountNumber> <password>");
        System.out.println("  deposit <name> <accountNumber> <password> <currency> <amount>");
        System.out.println("  withdraw <name> <accountNumber> <password> <currency> <amount>");
        System.out.println("  balance <name> <accountNumber> <password>");
        System.out.println("  transfer <name> <fromAccountNumber> <password> <toAccountNumber> <amount>");
        System.out.println("  monitor <interval>");
        System.out.println("  help - Show this help message");
        System.out.println("  exit/quit - Exit the application");
    }

    private void handleOpen(String[] parts) {
        if (parts.length != 5) {
            System.out.println("Usage: open <name> <password> <currency> <initialBalance>");
            return;
        }
        OpenAccountRequest request = new OpenAccountRequest();
        request.setName(parts[1]);
        request.setPassword(parts[2]);
        request.setCurrency(Currency.valueOf(parts[3].toUpperCase()));
        request.setInitialBalance(Double.parseDouble(parts[4]));

        Integer accountNumber = bankingService.openAccount(request);
        System.out.println("[SUCCESS] Account opened successfully. Account Number: " + accountNumber);
    }

    private void handleClose(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Usage: close <name> <accountNumber> <password>");
            return;
        }
        CloseAccountRequest request = new CloseAccountRequest();
        request.setName(parts[1]);
        request.setAccountNumber(Integer.parseInt(parts[2]));
        request.setPassword(parts[3]);

        String msg = bankingService.closeAccount(request);
        System.out.println("[SUCCESS] " + msg);
    }

    private void handleDeposit(String[] parts) {
        if (parts.length != 6) {
            System.out.println("Usage: deposit <name> <accountNumber> <password> <currency> <amount>");
            return;
        }
        UpdateBalanceRequest request = new UpdateBalanceRequest();
        request.setName(parts[1]);
        request.setAccountNumber(Integer.parseInt(parts[2]));
        request.setPassword(parts[3]);
        request.setCurrency(Currency.valueOf(parts[4].toUpperCase()));
        request.setAmount(Double.parseDouble(parts[5]));
        request.setDepositFlag(true);

        Double newBalance = bankingService.deposit(request);
        System.out.println("[SUCCESS] Deposit successful. New Balance: " + newBalance);
    }

    private void handleWithdraw(String[] parts) {
        if (parts.length != 6) {
            System.out.println("Usage: withdraw <name> <accountNumber> <password> <currency> <amount>");
            return;
        }
        UpdateBalanceRequest request = new UpdateBalanceRequest();
        request.setName(parts[1]);
        request.setAccountNumber(Integer.parseInt(parts[2]));
        request.setPassword(parts[3]);
        request.setCurrency(Currency.valueOf(parts[4].toUpperCase()));
        request.setAmount(Double.parseDouble(parts[5]));
        request.setDepositFlag(false);

        Double newBalance = bankingService.withdrawal(request);
        System.out.println("[SUCCESS] Withdrawal successful. New Balance: " + newBalance);
    }

    private void handleBalance(String[] parts) {
        if (parts.length != 4) {
            System.out.println("Usage: balance <name> <accountNumber> <password>");
            return;
        }
        GetBalanceRequest request = new GetBalanceRequest();
        request.setName(parts[1]);
        request.setAccountNumber(Integer.parseInt(parts[2]));
        request.setPassword(parts[3]);

        Double balance = bankingService.getBalance(request);
        System.out.println("[SUCCESS] Current Balance: " + balance);
    }

    private void handleTransfer(String[] parts) {
        if (parts.length != 7) {
            System.out.println("Usage: transfer <name> <fromAccountNumber> <password> <toAccountNumber> <currency> <amount>");
            return;
        }
        TransferRequest request = new TransferRequest();
        request.setName(parts[1]);
        request.setAccountNumber(Integer.parseInt(parts[2]));
        request.setPassword(parts[3]);
        request.setDestAccountNumber(Integer.parseInt(parts[4]));
        request.setCurrency(Currency.valueOf(parts[5].toUpperCase()));
        request.setAmount(Double.parseDouble(parts[6]));

        Double response = bankingService.transfer(request);
        System.out.println("[SUCCESS] Transfer successful.");
        System.out.println("  Source Account (" + request.getAccountNumber() + ") New Balance: " + request.getCurrency() + " " + response);
    }

    private void handleMonitor(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Usage: monitor <interval>");
            return;
        }
        MonitorRequest request = new MonitorRequest();
        request.setMonitorInterval(Integer.parseInt(parts[1]));

        String msg = bankingService.registerMonitor(request);
        System.out.println("Server Response: " + msg);
        System.out.println("[SUCCESS] Monitoring registered for " + parts[1] + " seconds.");
        System.out.println("Waiting for updates... (CLI will be blocked during this period)");

        try {
            // TODO: Implement monitor behavior
            Thread.sleep(Long.parseLong(parts[1]) * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Monitoring interval expired.");
    }
}
