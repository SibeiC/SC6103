package com.chencraft.ntu.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * IdGenerator is a thread-safe, self-incrementing ID generator.
 * It starts from 0 and increments every time getNextId() is called.
 */
@Service
public class IdGenerator {
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Returns the next ID and increments the counter.
     *
     * @return next ID
     */
    public int getNextId() {
        return counter.getAndIncrement();
    }

    /**
     * Resets the counter to 0. (Mainly for testing purposes)
     */
    public void reset() {
        counter.set(0);
    }
}
