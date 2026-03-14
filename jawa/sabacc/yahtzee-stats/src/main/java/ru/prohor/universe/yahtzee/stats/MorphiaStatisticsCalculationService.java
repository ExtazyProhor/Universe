package ru.prohor.universe.yahtzee.stats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.benchmark.Benchmark;
import ru.prohor.universe.jocasta.morphia.impl.MongoMorphiaRepository;
import ru.prohor.universe.jocasta.morphia.query.MongoQuery;
import ru.prohor.universe.jocasta.morphia.query.MongoSorts;
import ru.prohor.universe.yahtzee.core.data.pojo.game.Game;
import ru.prohor.universe.yahtzee.stats.model.Stats;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class MorphiaStatisticsCalculationService implements StatisticsCalculationService {
    private static final String GLOBAL_STATISTIC_CALCULATION = "/global-statistics-calculation.json";
    private static final TypeReference<List<Map<String, Object>>> TYPE_REFERENCE = new TypeReference<>() {};

    private final MongoMorphiaRepository<Game> gamesRepository;
    private final MongoMorphiaRepository<Stats> statsRepository;
    private final ObjectMapper objectMapper;

    public MorphiaStatisticsCalculationService(
            MongoMorphiaRepository<Game> gamesRepository,
            MongoMorphiaRepository<Stats> statsRepository
    ) {
        this.gamesRepository = gamesRepository;
        this.statsRepository = statsRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Opt<Stats> calculateAndGet() {
        long ms = Benchmark.measureSneaky(() -> executeAggregationPipeline(loadRawPipeline()));
        // TODO log.info("The aggregation pipeline finished in {} ms", ms);
        System.out.println("aggregation finished in " + ms + " ms");
        return getLatestStats();
    }

    private void executeAggregationPipeline(List<Document> pipeline) {
        MongoCollection<Document> collection = gamesRepository.getCollection();
        collection.aggregate(pipeline).toCollection();
    }

    private Opt<Stats> getLatestStats() {
        MongoQuery<Stats> query = new MongoQuery<Stats>()
                .sort(MongoSorts.descending(Stats::id))
                .limit(1);
        List<Stats> stats = statsRepository.find(query);
        return Opt.when(!stats.isEmpty(), stats::getFirst);
    }

    private List<Document> loadRawPipeline() throws Exception {
        try (InputStream is = getClass().getResourceAsStream(GLOBAL_STATISTIC_CALCULATION)) {
            return objectMapper.readValue(is, TYPE_REFERENCE).stream().map(Document::new).toList();
        }
    }
}
