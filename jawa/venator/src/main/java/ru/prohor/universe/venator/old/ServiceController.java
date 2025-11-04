package ru.prohor.universe.venator.old;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {
    private final ServiceManager manager;

    public ServiceController(ServiceManager manager) {
        this.manager = manager;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestParam("name") String name, @RequestParam("path") String path) {
        try {
            ServiceInfo info = manager.startService(name, Path.of(path));
            return ResponseEntity.ok(info);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка запуска: " + e.getMessage());
        }
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stop(@RequestParam("name") String name) {
        manager.stopService(name);
        return ResponseEntity.ok("Остановлен: " + name);
    }

    @GetMapping
    public List<ServiceInfo> list() {
        return manager.listServices();
    }

    @GetMapping("/{name}")
    public ResponseEntity<?> get(@PathVariable String name) {
        return manager.getService(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}