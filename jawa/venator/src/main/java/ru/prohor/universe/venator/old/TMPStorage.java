package ru.prohor.universe.venator.old;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TMPStorage implements ServiceStorage {
    private final Map<String, ServiceInfo> map = new HashMap<>();

    @Override
    public void save(ServiceInfo info) {
        map.put(info.name(), info);
    }

    @Override
    public Optional<ServiceInfo> findByName(String name) {
        return Optional.ofNullable(map.get(name));
    }

    @Override
    public List<ServiceInfo> findAll() {
        return map.values().stream().toList();
    }

    @Override
    public void deleteByName(String name) {
        map.remove(name);
    }
}
