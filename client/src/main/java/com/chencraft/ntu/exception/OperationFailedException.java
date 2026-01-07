package com.chencraft.ntu.exception;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

/**
 * Custom runtime exception thrown when a banking operation fails.
 * Includes a descriptive error message and an error code.
 */
@Getter
@ToString(callSuper = true)
public class OperationFailedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Descriptive message explaining why the operation failed.
     */
    private final String errorMessage;

    /**
     * Integer error code associated with the failure.
     */
    private final int errorCode;

    /**
     * Constructor for OperationFailedException.
     *
     * @param errorMessage descriptive error message
     * @param errorCode    error code
     */
    public OperationFailedException(String errorMessage, int errorCode) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
