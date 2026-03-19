package ru.prohor.universe.yahtzee.app.services.images;

import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.yahtzee.core.data.pojo.image.Image;
import ru.prohor.universe.yahtzee.core.services.YahtzeeUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@Service
public class ImagesService {
    private static final String PNG = "png";

    private final MongoRepository<Image> imagesRepository;
    private final AvatarGenerator avatarGenerator;

    public ImagesService(
            MongoRepository<Image> imagesRepository,
            AvatarGenerator avatarGenerator
    ) {
        this.imagesRepository = imagesRepository;
        this.avatarGenerator = avatarGenerator;
    }

    public Image generateAndSave() {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(
                    avatarGenerator.generate(),
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
        return imagesRepository.findById(YahtzeeUtils.parseObjectId(id)).map(image -> image.content().getData());
    }
}
