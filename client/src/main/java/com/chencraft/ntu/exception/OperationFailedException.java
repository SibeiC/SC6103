package com.chencraft.ntu.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * Custom runtime exception thrown when a banking operation fails.
 * Includes a descriptive error message and an error code.
 */
@Getter
@ToString(callSuper = true)
public class OperationFailedException extends RuntimeException {
    /**
     * Descriptive message explaining why the operation failed.
     */
    private final String errorMessage;

    /**
     * Constructor for OperationFailedException.
     *
     * @param errorMessage descriptive error message
     */
    public OperationFailedException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
