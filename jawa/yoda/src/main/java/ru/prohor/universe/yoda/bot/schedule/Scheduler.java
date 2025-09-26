package ru.prohor.universe.yoda.bot.schedule;

import jakarta.annotation.PreDestroy;
import org.joda.time.LocalTime;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class Scheduler {
    private static final int SECS_IN_MINUTE = 60;

    private final ScheduledExecutorService scheduler;

    public Scheduler() {
        LocalTime now = LocalTime.now();
        int initialDelay = SECS_IN_MINUTE - now.getSecondOfMinute();
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {}, initialDelay, SECS_IN_MINUTE, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
