package ru.prohor.universe.bobafett.distribution;

import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

public interface DistributionTask {
    void distribute(FeedbackExecutor feedbackExecutor, int hour, int minute);
}
