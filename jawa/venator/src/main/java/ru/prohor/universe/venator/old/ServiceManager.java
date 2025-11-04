package ru.prohor.universe.venator.old;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class ServiceManager {
    private final Map<String, ServiceInfo> running = new HashMap<>();
    private final ProcessLauncherImpl launcher = new ProcessLauncherImpl();
    private final ServiceStorage storage;
    private final SseServiceNotifier notifier;

    public ServiceManager(ServiceStorage storage, SseServiceNotifier notifier) {
        this.storage = storage;
        this.notifier = notifier;
    }

    @PostConstruct
    public void init() {
        storage.findAll().forEach(info -> running.put(info.name(), new ServiceInfo(
                info.name(),
                info.jarPath(),
                info.pid(),
                info.startedAt(),
                launcher.isAlive(info.pid())
        )));
    }

    public ServiceInfo startService(String name, Path jarPath) throws IOException {
        ServiceInfo info = launcher.launch(name, jarPath);
        running.put(name, info);
        storage.save(info);
        notifier.notifyAllClients(running.values());
        return info;
    }

    public void stopService(String name) {
        ServiceInfo info = running.get(name);
        if (info != null) {
            launcher.kill(info.pid());
            info = new ServiceInfo(info.name(), info.jarPath(), info.pid(), info.startedAt(), false);
            storage.save(info);
            running.put(info.name(), info);
            notifier.notifyAllClients(running.values());
        }
    }

    public List<ServiceInfo> listServices() {
        return new ArrayList<>(running.values());
    }

    public Optional<ServiceInfo> getService(String name) {
        return Optional.ofNullable(running.get(name));
    }

    @Scheduled(fixedRate = 5000)
    public void checkServiceStatuses() {
        boolean changed = false;
        for (ServiceInfo info : running.values()) {
            boolean actual = launcher.isAlive(info.pid());
            if (info.alive() != actual) {
                info = new ServiceInfo(info.name(), info.jarPath(), info.pid(), info.startedAt(), actual);
                running.put(info.name(), info);
                storage.save(info);
                changed = true;
            }
        }
        if (changed)
            notifier.notifyAllClients(running.values());
    }
}
