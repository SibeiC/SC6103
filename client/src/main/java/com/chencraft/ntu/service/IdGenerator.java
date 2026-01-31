package com.chencraft.ntu.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * IdGenerator is a thread-safe, self-incrementing ID generator.
 * It starts from 0 and increments every time getNextId() is called.
 */
@Component
public class IdGenerator {
    private static final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Returns the next ID and increments the counter.
     *
     * @return next ID
     */
    public static int getNextId() {
        return counter.getAndIncrement();
    }

    /**
     * Resets the counter to 0. (Mainly for testing purposes)
     */
    public static void reset() {
        counter.set(0);
    }
}
