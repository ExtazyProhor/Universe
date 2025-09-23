package ru.prohor.universe.jocasta.core.features;

public class SnowflakeIdGenerator {
    private static final int WORKER_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;

    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    // 2025.01.01 00:00:00 UTC (millis)
    private static final long EPOCH = 1735689600_000L;

    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    String.format("Worker ID must be in range [0, %d]", MAX_WORKER_ID)
            );
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0)
                currentTimestamp = waitNextMillis(lastTimestamp);
        } else
            sequence = 0L;
        lastTimestamp = currentTimestamp;
        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp)
            timestamp = getCurrentTimestamp();
        return timestamp;
    }

    // TODO для тестов
    // TODO public static long extractTimestamp(long id) {
    // TODO     return ((id >> TIMESTAMP_SHIFT) + EPOCH);
    // TODO }
    // TODO public static long extractWorkerId(long id) {
    // TODO     return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    // TODO }
    // TODO public static long extractSequence(long id) {
    // TODO     return id & MAX_SEQUENCE;
    // TODO }
    // TODO // Getters для информации о конфигурации
    // TODO public long getWorkerId() {
    // TODO     return workerId;
    // TODO }
    // TODO public static long getMaxWorkerId() {
    // TODO     return MAX_WORKER_ID;
    // TODO }
    // TODO public static long getEpoch() {
    // TODO     return EPOCH;
    // TODO }
    // TODO // Пример использования
    // TODO public static void main(String[] args) {
    // TODO     SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);
    // TODO     System.out.println("Генерация Snowflake ID:");
    // TODO     for (int i = 0; i < 10; i++) {
    // TODO         long id = generator.nextId();
    // TODO         System.out.printf("ID: %d, Timestamp: %d, WorkerID: %d, Sequence: %d%n",
    // TODO                 id,
    // TODO                 extractTimestamp(id),
    // TODO                 extractWorkerId(id),
    // TODO                 extractSequence(id)
    // TODO         );
    // TODO     }
    // TODO }
}
