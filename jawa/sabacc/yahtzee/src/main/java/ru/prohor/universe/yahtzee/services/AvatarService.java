package ru.prohor.universe.yahtzee.services;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.joda.time.Instant;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.Opt;
import ru.prohor.universe.yahtzee.data.MongoRepositoryWithWrapper;
import ru.prohor.universe.yahtzee.data.entities.dto.ImageDto;
import ru.prohor.universe.yahtzee.data.entities.pojo.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class AvatarService {
    private static final String PNG = "png";

    private final MongoRepositoryWithWrapper<ImageDto, Image> imagesRepository;

    public AvatarService(MongoRepositoryWithWrapper<ImageDto, Image> imagesRepository) {
        this.imagesRepository = imagesRepository;
    }

    public Image generateAndSave() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(
                    generate(),
                    PNG,
                    byteArrayOutputStream
            );
            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            Image image = new Image(
                    ObjectId.get(),
                    new Binary(imageBytes),
                    Instant.now()
            );
            imagesRepository.save(image);
            return image;
        } catch (IOException e) {
            throw new RuntimeException("Error writing image", e);
        }
    }

    public Opt<byte[]> getAvatarById(String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (Exception e) {
            // TODO log
            return Opt.empty();
        }
        return imagesRepository.findById(objectId).map(image -> image.content().getData());
    }

    private BufferedImage generate() {
        return new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB); // TODO
    }
}
