package com.chencraft.ntu;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.Serial;

@SpringBootApplication
public class Application implements CommandLineRunner {

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
    }

    /**
     * Launches the Spring Boot application.
     *
     * @param args raw command-line arguments.
     */
    public static void main(String[] args) {
        new SpringApplication(Application.class).run(args);
    }

    /**
     * Runtime exception used to terminate the app with a deterministic exit code (10) for scripts.
     */
    static class ExitException extends RuntimeException implements ExitCodeGenerator {
        @Serial
        private static final long serialVersionUID = 1L;

        @Override
        public int getExitCode() {
            return 10;
        }
    }
}
