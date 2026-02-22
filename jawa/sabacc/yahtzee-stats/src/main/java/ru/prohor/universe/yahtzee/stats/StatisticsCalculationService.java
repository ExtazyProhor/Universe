package ru.prohor.universe.yahtzee.stats;

import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.yahtzee.stats.model.OfflineStats;

public interface StatisticsCalculationService {
    /**
     * @return Opt.empty if offline_games collection is empty, else - offline statistics
     */
    Opt<OfflineStats> calculateAndGet();
}
