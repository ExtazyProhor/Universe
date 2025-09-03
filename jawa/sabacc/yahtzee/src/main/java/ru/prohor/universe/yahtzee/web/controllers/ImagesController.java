package ru.prohor.universe.yahtzee.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.yahtzee.services.images.ImagesService;

@RestController("/image")
public class ImagesController {
    ImagesService imagesService;

    public ImagesController(ImagesService imagesService) {
        this.imagesService = imagesService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> image(@PathVariable String id) {
        return imagesService.getAvatarById(id).map(
                data -> ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(data)
        ).orElseGet(
                () -> ResponseEntity.notFound().build()
        );
    }
}
