package ru.prohor.universe.venator;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class PidService {
    @PostConstruct
    public void init() {
        start();
    }

    public void start() {
        System.out.println("Автозапуск PidService из @PostConstruct");
    }
}
