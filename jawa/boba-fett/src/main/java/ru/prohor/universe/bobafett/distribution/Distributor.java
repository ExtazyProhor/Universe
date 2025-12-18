package ru.prohor.universe.bobafett.distribution;

import org.joda.time.LocalDateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.BobaFettBot;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

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

    @Scheduled(cron = "0 0/15 * * * ?")
    public void execute() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHourOfDay();
        int minute = now.getMinuteOfHour();
        if (!availableMinutes.contains(minute)) {
            // TODO log warn
            return;
        }
        tasks.forEach(task -> task.distribute(feedbackExecutor, hour, minute));
    }
}
