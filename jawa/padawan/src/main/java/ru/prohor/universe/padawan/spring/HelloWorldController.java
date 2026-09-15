package ru.prohor.universe.padawan.spring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloWorldController {
    @GetMapping("/hello-world")
    public ResponseEntity<?> helloWorld(
            @RequestParam("name") String name,
            @RequestParam("age") int age
    ) {
        return ResponseEntity.ok("Hello, " + name + " " + age + " y.o.");
    }
}
