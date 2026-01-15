package ru.prohor.universe.bobafett.distribution;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.BobaFettBot;
import ru.prohor.universe.jocasta.core.utils.DateTimeUtil;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class Distributor {
    private final Set<Integer> availableMinutes = new HashSet<>(Set.of(0, 15, 30, 45));
    private final List<DistributionTask> tasks;
    private final FeedbackExecutor feedbackExecutor;

    public Distributor(List<DistributionTask> tasks, BobaFettBot bot) {
        this.tasks = tasks;
        this.feedbackExecutor = bot.getFeedbackExecutor();
    }

    @Scheduled(cron = "0 0/15 * * * ?", zone = "Europe/Moscow")
    public void execute() {
        LocalTime now = LocalTime.now(DateTimeUtil.MOSCOW_ZONE_ID);
        int hour = now.getHour();
        int minute = now.getMinute();
        if (!availableMinutes.contains(minute)) {
            // TODO log err
            throw new RuntimeException("Minute value is not available: " + minute);
        }
        tasks.forEach(task -> task.distribute(feedbackExecutor, hour, minute));
    }
}
