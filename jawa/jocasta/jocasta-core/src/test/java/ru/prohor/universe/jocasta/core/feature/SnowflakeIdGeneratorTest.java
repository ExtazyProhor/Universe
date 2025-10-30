package ru.prohor.universe.jocasta.core.feature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.jocasta.core.features.SnowflakeIdGenerator;

import java.util.HashSet;
import java.util.Set;

public class SnowflakeIdGeneratorTest {
    @Test
    void shouldCreateGeneratorWithValidWorkerId() {
        new SnowflakeIdGenerator(0);
        new SnowflakeIdGenerator(512);
        new SnowflakeIdGenerator(1023);
    }

    @Test
    void shouldThrowForInvalidWorkerId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(-1));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(1024));
    }

    @Test
    void idsShouldBeUniqueAndIncreasing() {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(1);

        long id1 = gen.nextId();
        long id2 = gen.nextId();

        Assertions.assertNotEquals(id1, id2);
        Assertions.assertTrue(id2 > id1);
    }

    @Test
    void workerIdShouldBeEncoded() {
        long workerId = 42;
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(workerId);

        long id = gen.nextId();

        Assertions.assertEquals(workerId, SnowflakeIdGenerator.extractWorkerId(id));
    }

    @Test
    void sequenceShouldResetOnTimestampChange() throws InterruptedException {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(5);

        long id1 = gen.nextId();
        long seq1 = SnowflakeIdGenerator.extractSequence(id1);

        Thread.sleep(5);

        long id2 = gen.nextId();
        long seq2 = SnowflakeIdGenerator.extractSequence(id2);

        Assertions.assertEquals(0, seq2);
        Assertions.assertEquals(seq1, seq2);
    }

    @Test
    void sequenceOverflowShouldWaitNextMillis() {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(0);

        long firstId = gen.nextId();
        long firstTs = SnowflakeIdGenerator.extractTimestamp(firstId);

        long lastId = 0;
        for (int i = 0; i < (4096 + 2); i++) {
            lastId = gen.nextId();
        }
        long lastTs = SnowflakeIdGenerator.extractTimestamp(lastId);

        Assertions.assertTrue(lastTs >= firstTs);
    }

    @Test
    void multithreadingShouldNotProduceDuplicates() throws InterruptedException {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(7);
        Set<Long> ids = new HashSet<>();

        int threads = 20;
        int perThread = 1000;
        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    synchronized (ids) {
                        ids.add(gen.nextId());
                    }
                }
            });
        }

        for (Thread t : workers)
            t.start();
        for (Thread t : workers)
            t.join();

        Assertions.assertEquals(threads * perThread, ids.size());
    }

    @Test
    void timestampShouldIncrease() throws InterruptedException {
        SnowflakeIdGenerator gen = new SnowflakeIdGenerator(2);

        long id1 = gen.nextId();
        Thread.sleep(5);
        long id2 = gen.nextId();

        Assertions.assertTrue(
                SnowflakeIdGenerator.extractTimestamp(id2) > SnowflakeIdGenerator.extractTimestamp(id1)
        );
    }
}
