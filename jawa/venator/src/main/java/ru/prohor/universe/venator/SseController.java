package ru.prohor.universe.venator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
    private final SseServiceNotifier notifier;

    public SseController(SseServiceNotifier notifier) {
        this.notifier = notifier;
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        return notifier.subscribe();
    }
}
