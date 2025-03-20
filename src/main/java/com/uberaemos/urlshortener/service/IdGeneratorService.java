package com.uberaemos.urlshortener.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class IdGeneratorService {

    // Epoch start timestamp
    public static final long EPOCH = 1742428800000L; // March 20, 2025

    // Bits
    private static final long TIMESTAMP_BITS = 41;
    private static final long DATACENTER_BITS = 5;
    private static final long MACHINE_BITS = 5;
    private static final long SEQUENCE_BITS = 12;

    // Bit max values
    private static final long MAX_DATACENTER_ID = (1L << DATACENTER_BITS) - 1;
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    // Bit shifting
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_SHIFT = SEQUENCE_BITS + MACHINE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS + DATACENTER_BITS;

    // Node identifiers
    private final long datacenterId;
    private final long machineId;

    // Sequence and tracker
    private final AtomicLong lastTimestamp = new AtomicLong(-1L);
    private final AtomicLong sequence = new AtomicLong(0);

    public IdGeneratorService(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID must be between 0 and " + MAX_DATACENTER_ID);
        }

        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException("Machine ID must be between 0 and " + MAX_MACHINE_ID);
        }

        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public long generateId() {
        long timestamp = System.currentTimeMillis();

        while (true) {
            long lastTs = lastTimestamp.get();
            long currentSequence = sequence.get();

            if (timestamp == lastTs) {
                long nextSeq = (currentSequence + 1) & MAX_SEQUENCE;
                if (nextSeq == 0) {
                    // Wait until next millisecond
                    timestamp = waitForNextMillis(lastTs);
                    continue;
                }
                if (sequence.compareAndSet(currentSequence, nextSeq)) {
                    return createId(timestamp, nextSeq);
                }
            } else {
                if (lastTimestamp.compareAndSet(lastTs, timestamp)) {
                    sequence.set(0);
                    return createId(timestamp, 0);
                }
            }
        }
    }

    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    private long createId(long timestamp, long sequence) {
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_SHIFT)
                | (machineId << MACHINE_SHIFT)
                | sequence;
    }
}
