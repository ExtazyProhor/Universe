package ru.prohor.universe.yoda.bot.schedule.api;

public interface ScheduleService {
    void executeAt(int minute, int hour, int dayOfWeek);
}
