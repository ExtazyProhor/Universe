package ru.prohor.universe.yahtzee.app.web.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.prohor.universe.yahtzee.app.services.images.ImagesService;

@RestController
@RequestMapping("/image")
public class ImagesController {
    ImagesService imagesService;

    public ImagesController(ImagesService imagesService) {
        this.imagesService = imagesService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> image(@PathVariable("id") String imageId) {
        return imagesService.getAvatarById(imageId).map(
                data -> ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(data)
        ).orElseGet(
                () -> ResponseEntity.notFound().build()
        );
    }
}
