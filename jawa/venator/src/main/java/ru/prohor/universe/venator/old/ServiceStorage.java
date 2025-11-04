package ru.prohor.universe.venator.old;

import java.util.List;
import java.util.Optional;

public interface ServiceStorage {
    void save(ServiceInfo info);

    Optional<ServiceInfo> findByName(String name);

    List<ServiceInfo> findAll();

    void deleteByName(String name);
}
