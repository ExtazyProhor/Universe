package ru.prohor.universe.yahtzee.stats;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.stats.model.Stats;

public interface StatisticsCalculationService {
    /**
     * @return Opt.empty if {@code games} collection is empty, else - statistics
     */
    Opt<Stats> calculateAndGet();
}
