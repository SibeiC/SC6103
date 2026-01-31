package com.chencraft.ntu;

import com.chencraft.ntu.cli.BankingCli;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Distributed Banking System.
 * Bootstraps the Spring Boot application and provides a command-line runner hook
 * to launch the interactive CLI.
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

    private final BankingCli bankingCli;

    /**
     * Constructor for Application.
     *
     * @param bankingCli the CLI interface to be launched
     */
    @Autowired
    public Application(BankingCli bankingCli) {
        this.bankingCli = bankingCli;
    }

    /**
     * Hook invoked after the application context has started.
     *
     * @param arg0 command-line arguments passed to the application; if the first argument equals "exitcode",
     *             an ExitException will be thrown to terminate with a deterministic exit code.
     */
    @Override
    public void run(String... arg0) {
        if (arg0.length > 0 && arg0[0].equals("exitcode")) {
            throw new ExitException();
        }
        bankingCli.start();
    }

    /**
     * Launches the Spring Boot application.
     *
     * @param args raw command-line arguments.
     */
    static void main(String[] args) {
        new SpringApplication(Application.class).run(args);
    }

    /**
     * Runtime exception used to terminate the app with a deterministic exit code (10) for scripts.
     */
    static class ExitException extends RuntimeException implements ExitCodeGenerator {

        @Override
        public int getExitCode() {
            return 10;
        }
    }
}
