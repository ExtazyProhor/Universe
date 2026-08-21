package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.pojo.BobaFettUser;
import ru.prohor.universe.bobafett.distribution.DistributionTask;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;

import java.util.List;

@Service
public class CurrencyRatesDistributor implements DistributionTask {
    private final CurrencyMessageGenerator currencyMessageGenerator;
    private final CurrencyDistributionUsersProvider currencyDistributionUsersProvider;
    private final MongoRepository<BobaFettUser> bobaFettUsersRepository;

    public CurrencyRatesDistributor(
            CurrencyMessageGenerator currencyMessageGenerator,
            CurrencyDistributionUsersProvider currencyDistributionUsersProvider,
            MongoRepository<BobaFettUser> bobaFettUsersRepository
    ) {
        this.currencyMessageGenerator = currencyMessageGenerator;
        this.currencyDistributionUsersProvider = currencyDistributionUsersProvider;
        this.bobaFettUsersRepository = bobaFettUsersRepository;
    }

    @Override
    public void distribute(FeedbackExecutor feedbackExecutor, int hour, int minute) {
        List<BobaFettUser> users = currencyDistributionUsersProvider.findUsersToDistribution(
                bobaFettUsersRepository,
                hour,
                minute
        );
        if (users.isEmpty())
            return;

        for (BobaFettUser user : users) {
            feedbackExecutor.sendMessage(
                    user.chatId(),
                    currencyMessageGenerator.getCurrencyMessageFor(user)
            );
        }
    }
}
