package ru.prohor.universe.padawan.scripts.jocasta;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OneOfGeneration {
    private static final Path ONE_OF_PACKAGE = Path.of(
            "jocasta/jocasta-core/src/main/java/ru/prohor/universe/jocasta/core/collections/oneof"
    );
    private static final int MAX = 5;

    public static void main(String[] args) {
        generateAndSave();
    }

    private static void generateAndSave() {
        IntStream.range(2, MAX + 1).forEach(i -> Sneaky.execute(() -> {
            Files.writeString(ONE_OF_PACKAGE.resolve("OneOf" + i + ".java"), generateFor(i));
        }));
        Sneaky.execute(() -> Files.writeString(ONE_OF_PACKAGE.resolve("OneOf.java"), generateInterface()));
    }

    private static final List<String> STATIC_FACTORIES = new ArrayList<>();

    private static String generateInterface() {
        String basic = """
                package ru.prohor.universe.jocasta.core.collections.oneof;
                
                public interface OneOf {
                    Object getAsObject();
                
                    <T> T getUnchecked();
                
                    Class<?> getType();
                
                """;
        return basic + String.join("\n\n", STATIC_FACTORIES) + "\n}\n";
    }

    private static String generateFor(int size) {
        if (size < 2)
            throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder();
        String generic = IntStream.range(1, size + 1)
                .mapToObj(i -> "T" + i)
                .collect(Collectors.joining(", ", "<", ">"));

        for (int i = 0; i < size; ++i) {
            builder.append("    static ");
            builder.append(generic);
            builder.append(" OneOf");
            builder.append(size);
            builder.append(generic);
            builder.append(" oneOf");
            builder.append(size);
            builder.append("of");
            builder.append(i + 1);
            builder.append("(T");
            builder.append(i + 1);
            builder.append(" value) {\n        return new OneOf");
            builder.append(size);
            builder.append("<>(");
            int[] ii = {i};
            builder.append(IntStream.range(0, size).mapToObj(idx -> idx == ii[0] ? "value" : "null").collect(
                    Collectors.joining(", ")
            ));
            builder.append(", ");
            builder.append(i + 1);
            builder.append(");\n    }");
            STATIC_FACTORIES.add(builder.toString());
            builder.setLength(0);
        }

        builder.append("package ru.prohor.universe.jocasta.core.collections.oneof;\n\n");
        builder.append("public ");
        if (size == MAX)
            builder.append("non-");
        builder.append("sealed class OneOf");
        builder.append(size);
        builder.append(generic);
        builder.append(" extends OneOf");
        builder.append(size == 2 ? "Base" : (size - 1));
        builder.append(IntStream.range(1, size).mapToObj(i -> "T" + i).collect(Collectors.joining(", ", "<", ">")));
        if (size != MAX) {
            builder.append(" permits OneOf");
            builder.append(size + 1);
        }
        builder.append(" {\n");
        builder.append("    private static final int INDEX_");
        builder.append(size);
        builder.append(" = ");
        builder.append(size);
        builder.append(";\n\n    private final T");
        builder.append(size);
        builder.append(" t");
        builder.append(size);
        builder.append(";\n\n    protected OneOf");
        builder.append(size);
        builder.append(IntStream.range(1, size + 1).mapToObj(i -> "T" + i + " t" + i).collect(
                Collectors.joining(", ", "(", ", int index) {\n        super(")
        ));
        builder.append(IntStream.range(1, size).mapToObj(i -> "t" + i).collect(Collectors.joining(", ")));
        builder.append(", index);\n        this.t");
        builder.append(size);
        builder.append(" = t");
        builder.append(size);
        builder.append(";\n    }\n\n    public boolean is");
        builder.append(size);
        builder.append("() {\n        return index == INDEX_");
        builder.append(size);
        builder.append(";\n    }\n\n    public T");
        builder.append(size);
        builder.append(" get");
        builder.append(size);
        builder.append("() {\n        if (index != INDEX_");
        builder.append(size);
        builder.append(")\n            return t");
        builder.append(size);
        builder.append(";\n        throw illegalValueRequest(INDEX_");
        builder.append(size);
        builder.append(");\n    }\n\n");
        builder.append("    @Override\n    public Object getAsObject() {\n        return switch (index) {\n");
        builder.append(
                IntStream.range(1, size + 1)
                        .mapToObj(i -> "            case " + i + " -> get" + i + "();\n")
                        .collect(Collectors.joining(""))
        );
        builder.append("            default -> throw illegalIndex(INDEX_");
        builder.append(size);
        builder.append(");\n        };\n    }\n}\n");
        return builder.toString();
    }
}
