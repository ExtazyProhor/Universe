package ru.prohor.universe.yoda.bot.schedule.api;

import jakarta.annotation.PreDestroy;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class Scheduler {
    private final List<ScheduleService> scheduleServices;

    private static final int SECS_IN_MINUTE = 60;

    private final ScheduledExecutorService scheduler;

    public Scheduler(
            @Value("${universe.yoda.scheduler-threads}") int schedulerThreads,
            List<ScheduleService> scheduleServices
    ) {
        this.scheduleServices = scheduleServices;
        this.scheduler = Executors.newScheduledThreadPool(schedulerThreads);
        int initialDelay = SECS_IN_MINUTE - LocalTime.now().getSecondOfMinute();
        scheduler.scheduleAtFixedRate(this::executeScheduleServices, initialDelay, SECS_IN_MINUTE, TimeUnit.SECONDS);
    }

    public void executeScheduleServices() {
        DateTime now = DateTime.now();
        if (now.getSecondOfMinute() >= 30)
            now = now.plusMinutes(1);
        int minute = now.getMinuteOfHour();
        int hour = now.getHourOfDay();
        int dayOfWeek = now.getDayOfWeek();
        scheduleServices.forEach(scheduleService -> scheduleService.executeAt(minute, hour, dayOfWeek));
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }
}
