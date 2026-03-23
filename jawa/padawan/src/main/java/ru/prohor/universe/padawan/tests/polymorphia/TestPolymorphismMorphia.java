package ru.prohor.universe.padawan.tests.polymorphia;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.utils.CastUtils;
import ru.prohor.universe.jocasta.morphia.MongoRepository;
import ru.prohor.universe.jocasta.morphia.filter.MongoFilters;
import ru.prohor.universe.padawan.tests.polymorphia.pojo.Container;
import ru.prohor.universe.padawan.tests.polymorphia.pojo.Rectangle;
import ru.prohor.universe.padawan.tests.polymorphia.pojo.RectangleContainer;
import ru.prohor.universe.padawan.tests.polymorphia.pojo.Square;
import ru.prohor.universe.padawan.tests.polymorphia.pojo.SquareContainer;

import java.util.List;

@Service
public class TestPolymorphismMorphia {
    private final MongoRepository<Container> repository;

    public TestPolymorphismMorphia(MongoRepository<Container> repository) {
        this.repository = repository;
    }

    public void test() {
        System.out.println("1. creating objects");
        List<Container> containers = List.of(
                new RectangleContainer(ObjectId.get(), "rec", new Rectangle(2, 3)),
                new RectangleContainer(ObjectId.get(), "rec", new Rectangle(20, 2)),
                new SquareContainer(ObjectId.get(), "sq", new Square(5)),
                new SquareContainer(ObjectId.get(), "sq", new Square(75))
        );
        System.out.println("2. writing objects");
        repository.save(containers);
        System.out.println("3. reading as containers");
        List<Container> r = repository.findAll();
        r.forEach(i -> System.out.println("    - " + i));
        System.out.println("4. reading as squares");
        List<SquareContainer> s = CastUtils.cast(
                repository.find(MongoFilters.eq(FR.wrap(Container::type), FigureType.SQUARE))
        );
        s.forEach(i -> System.out.println("    - " + i));
        System.out.println("5. reading as rectangles");
        List<RectangleContainer> f = CastUtils.cast(
                repository.find(MongoFilters.eq(FR.wrap(Container::type), FigureType.RECTANGLE))
        );
        f.forEach(i -> System.out.println("    - " + i));
        System.out.println("6. delete all");
        System.out.println("deleted: " + repository.deleteAll());
        System.out.println("7. count documents");
        System.out.println("count: " + repository.countDocuments());
        System.out.println("8. end");
    }
}
