package ru.prohor.universe.padawan.scripts.jocasta;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TupleGeneration {
    private static final Path TUPLE_PACKAGE = Path.of(
            "jocasta/jocasta-core/src/main/java/ru/prohor/universe/jocasta/core/collections/tuple"
    );
    private static final int MAX = 5;

    public static void main(String[] args) {
        generateAndSave();
    }

    private static void generateAndSave() {
        IntStream.range(2, MAX + 1).forEach(i -> Sneaky.execute(() -> {
            Files.writeString(TUPLE_PACKAGE.resolve("Tuple" + i + ".java"), generateFor(i));
        }));
    }

    private static final String HEAD = """
            /**
             * This file is a modified version of the original file developed by Stepan Koltsov at Yandex.
             * Original code is available under <a href="http://www.apache.org/licenses/LICENSE-2.0">the Apache License 2.0</a>
             * at <a href="https://github.com/v1ctor/bolts">github.com/v1ctor/bolts</a>
             */
            public class Tuple""";

    private static String generateFor(int size) {
        if (size < 2)
            throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder();
        String generic = IntStream.range(1, size + 1)
                .mapToObj(i -> "T" + i)
                .collect(Collectors.joining(", ", "<", ">"));

        builder.append("package ru.prohor.universe.jocasta.core.collections.tuple;\n\n");
        builder.append(
                Stream.of("MonoFunction", FunctionalGeneration.prefix(size) + "Function")
                        .sorted()
                        .map(name -> "import ru.prohor.universe.jocasta.core.functional." + name + ";")
                        .collect(Collectors.joining("\n"))
        );
        builder.append("\n\n");
        builder.append(HEAD);
        builder.append(size);
        builder.append(generic);
        builder.append(" extends AbstractTuple {\n");
        builder.append(
                IntStream.range(1, size + 1)
                        .mapToObj(i -> "    private final T" + i + " t" + i + ";\n")
                        .collect(Collectors.joining())
        );
        builder.append("\n    public Tuple");
        builder.append(size);
        builder.append(
                IntStream.range(1, size + 1)
                        .mapToObj(i -> "T" + i + " t" + i)
                        .collect(Collectors.joining(", ", "(", ") {\n        super("))
        );
        builder.append(size);
        builder.append(");\n");
        builder.append(
                IntStream.range(1, size + 1)
                        .mapToObj(i -> "        this.t" + i + " = t" + i + ";\n")
                        .collect(Collectors.joining("", "", "    }\n\n"))
        );
        builder.append("    @Override\n    public Object get(int n) {\n        return switch (n) {\n");
        builder.append(
                IntStream.range(1, size + 1)
                        .mapToObj(i -> "            case " + i + " -> t" + i + ";\n")
                        .collect(Collectors.joining(""))
        );
        builder.append("            default -> throw new IllegalArgumentException();\n        };\n    }\n\n");
        for (int i = 1; i <= size; ++i) {
            builder.append("    public T");
            builder.append(i);
            builder.append(" get");
            builder.append(i);
            builder.append("() {\n        return t");
            builder.append(i);
            builder.append(";\n    }\n\n");
        }
        builder.append("    public <R> R reduce(");
        builder.append(FunctionalGeneration.prefix(size));
        builder.append(
                IntStream.range(1, size + 1)
                        .mapToObj(i -> "T" + i + ", ")
                        .collect(Collectors.joining("", "Function<", "R> reducer) {\n        return reducer.apply("))
        );
        builder.append(
                IntStream.range(1, size + 1)
                        .mapToObj(i -> "t" + i)
                        .collect(Collectors.joining(", "))
        );
        builder.append(");\n    }\n\n");

        for (int i = 1; i <= size; ++i) {
            int[] ii = {i};
            builder.append("    public <R> Tuple");
            builder.append(size);
            builder.append(
                    IntStream.range(1, size + 1)
                            .mapToObj(t -> t == ii[0] ? "R" : "T" + t)
                            .collect(Collectors.joining(", ", "<", "> map"))
            );
            builder.append(i);
            builder.append("(MonoFunction<T");
            builder.append(i);
            builder.append(", R> f) {\n        return new Tuple");
            builder.append(size);
            builder.append("<>(");
            builder.append(
                    IntStream.range(1, size + 1)
                            .mapToObj(t -> t == ii[0] ? ("f.apply(t" + t + ")") : ("t" + t))
                            .collect(Collectors.joining(", "))
            );
            builder.append(");\n    }\n\n");
        }
        if (size == 2) {
            String swap = """
                        public Tuple2<T2, T1> swap() {
                            return new Tuple2<>(t2, t1);
                        }
                    
                    """;
            builder.append(swap);
        }
        builder.append("    @SuppressWarnings(\"unchecked\")\n    public ");
        String incrementGeneric = IntStream.range(size + 1, size + size + 1)
                .mapToObj(t -> "T" + t)
                .collect(Collectors.joining(", ", "<", ">"));
        builder.append(incrementGeneric);
        builder.append(" Tuple");
        builder.append(size);
        builder.append(incrementGeneric);
        builder.append(" uncheckedCast() {\n        return (Tuple");
        builder.append(size);
        builder.append(incrementGeneric);
        builder.append(") this;\n    }\n}\n");
        return builder.toString();
    }
}
