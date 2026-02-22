package ru.prohor.universe.yahtzee.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.benchmark.Benchmark;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.impl.MongoMorphiaRepository;
import ru.prohor.universe.jocasta.morphia.query.MongoQuery;
import ru.prohor.universe.jocasta.morphia.query.MongoSorts;
import ru.prohor.universe.yahtzee.offline.data.entities.pojo.OfflineGame;
import ru.prohor.universe.yahtzee.stats.model.OfflineStats;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class MorphiaStatisticsCalculationService implements StatisticsCalculationService {
    private static final String GLOBAL_STATISTIC_CALCULATION = "/global-statistics-calculation.json";
    private static final TypeReference<List<Map<String, Object>>> TYPE_REFERENCE = new TypeReference<>() {};

    private final MongoMorphiaRepository<OfflineGame> offlineGamesRepository;
    private final MongoRepository<OfflineStats> offlineStatsRepository;
    private final ObjectMapper objectMapper;

    public MorphiaStatisticsCalculationService(
            MongoMorphiaRepository<OfflineGame> offlineGamesRepository,
            MongoRepository<OfflineStats> offlineStatsRepository
    ) {
        this.offlineGamesRepository = offlineGamesRepository;
        this.offlineStatsRepository = offlineStatsRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Opt<OfflineStats> calculateAndGet() {
        long ms = Benchmark.measureSneaky(() -> executeAggregationPipeline(loadRawPipeline()));
        // TODO log.info("The aggregation pipeline finished in {} ms", ms);
        System.out.println("aggregation finished in " + ms + " ms");
        return getLatestStats();
    }

    private void executeAggregationPipeline(List<Document> pipeline) {
        MongoCollection<Document> collection = offlineGamesRepository.getCollection();
        collection.aggregate(pipeline).toCollection();
    }

    private Opt<OfflineStats> getLatestStats() {
        MongoQuery<OfflineStats> query = new MongoQuery<OfflineStats>()
                .sort(MongoSorts.descending(OfflineStats::id))
                .limit(1);
        List<OfflineStats> stats = offlineStatsRepository.find(query);
        return Opt.when(!stats.isEmpty(), stats::getFirst);
    }

    private List<Document> loadRawPipeline() throws Exception {
        try (InputStream is = getClass().getResourceAsStream(GLOBAL_STATISTIC_CALCULATION)) {
            return objectMapper.readValue(is, TYPE_REFERENCE).stream().map(Document::new).toList();
        }
    }
}
