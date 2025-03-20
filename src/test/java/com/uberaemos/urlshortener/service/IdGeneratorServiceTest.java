package com.uberaemos.urlshortener.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class IdGeneratorServiceTest {

    private IdGeneratorService idGeneratorService;

    @BeforeEach
    void setUp() {
        idGeneratorService = new IdGeneratorService(1, 1);
    }

    @Test
    void testIdGeneration() {
        long id1 = idGeneratorService.generateId();
        long id2 = idGeneratorService.generateId();
        assertTrue(id2 > id1, "Generated IDs should be increasing");
    }

    @Test
    void testIdFormat() {
        long id = idGeneratorService.generateId();
        long timestamp = (id >> 22) + IdGeneratorService.EPOCH;
        long datacenterId = (id >> 17) & 0x1F;
        long machineId = (id >> 12) & 0x1F;

        assertTrue(timestamp >= System.currentTimeMillis() - 1000);
        assertEquals(1, datacenterId);
        assertEquals(1, machineId);
    }

    @Test
    void testMultiThreadedIdGeneration() throws InterruptedException {
        int threadCount = 100;
        int idCountPerThread = 100;
        Set<Long> uniqueIds = new HashSet<>();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                for (int j = 0; j < idCountPerThread; j++) {
                    synchronized (uniqueIds) {
                        uniqueIds.add(idGeneratorService.generateId());
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * idCountPerThread, uniqueIds.size(), "All generated IDs should be unique");
    }

}