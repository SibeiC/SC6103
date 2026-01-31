package com.chencraft.ntu.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdGeneratorTest {

    @BeforeEach
    void setUp() {
        IdGenerator.reset();
    }

    @Test
    void testGetNextId() {
        assertEquals(0, IdGenerator.getNextId());
        assertEquals(1, IdGenerator.getNextId());
        assertEquals(2, IdGenerator.getNextId());
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int threadCount = 100;
        int iterationsPerThread = 1000;
        int totalExpectedIds = threadCount * iterationsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Set<Integer> ids = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterationsPerThread; j++) {
                    ids.add(IdGenerator.getNextId());
                }
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        assertEquals(totalExpectedIds, ids.size());
        for (int i = 0; i < totalExpectedIds; i++) {
            assertTrue(ids.contains(i), "Missing ID: " + i);
        }
    }
}
