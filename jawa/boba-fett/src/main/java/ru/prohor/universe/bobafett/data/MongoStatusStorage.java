package ru.prohor.universe.bobafett.data;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.service.BobaFettUserService;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.tgbots.api.status.ValuedStatusStorage;

@Service
public class MongoStatusStorage implements ValuedStatusStorage<String, String> {
    private final BobaFettUserService bobaFettUserService;

    public MongoStatusStorage(BobaFettUserService bobaFettUserService) {
        this.bobaFettUserService = bobaFettUserService;
    }

    @Override
    public Opt<ValuedStatus<String, String>> getStatus(long chatId) {
        return bobaFettUserService.getStatusAndRemoveIt(chatId).map(it -> new ValuedStatus<>(
                it.key(),
                it.value()
        ));
    }
}
