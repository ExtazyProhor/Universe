package ru.prohor.universe.venator.old;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseServiceNotifier {

    private final List<SseEmitter> clients = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(0L);
        clients.add(emitter);
        emitter.onCompletion(() -> clients.remove(emitter));
        emitter.onTimeout(() -> clients.remove(emitter));
        return emitter;
    }

    record N(
            String name,
            Path jarPath,
            long pid,
            String startedAt,
            boolean alive
    ){}

    public void notifyAllClients(Collection<ServiceInfo> data) {
        for (SseEmitter emitter : clients) {
            try {
                emitter.send(data.stream().map(info -> new N(
                        info.name(),
                        info.jarPath(),
                        info.pid(),
                        info.startedAt().toString(),
                        info.alive()
                )).toList());
            } catch (IOException e) {
                emitter.complete();
                clients.remove(emitter);
            }
        }
    }
}
