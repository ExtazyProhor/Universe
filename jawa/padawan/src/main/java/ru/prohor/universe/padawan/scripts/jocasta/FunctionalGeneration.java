package ru.prohor.universe.padawan.scripts.jocasta;

import ru.prohor.universe.jocasta.core.features.sneaky.Sneaky;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FunctionalGeneration {
    private static final Path FUNCTIONAL_PACKAGE = Path.of(
            "jocasta/jocasta-core/src/main/java/ru/prohor/universe/jocasta/core/functional"
    );
    private static final int MIN = 0;
    private static final int MAX = 7;

    public static void main(String[] args) {
        generateAndSave();
    }

    public static String prefix(int i) {
        return switch (i) {
            case 0 -> "Nil";
            case 1 -> "Mono";
            case 2 -> "Di";
            case 3 -> "Tri";
            case 4 -> "Tetra";
            case 5 -> "Penta";
            case 6 -> "Hexa";
            case 7 -> "Hepta";
            default -> throw new RuntimeException("illegal index: " + i);
        };
    }

    private static void generateAndSave() {
        IntStream.range(MIN, MAX + 1).forEach(i -> Sneaky.execute(() -> {
            Files.writeString(FUNCTIONAL_PACKAGE.resolve(prefix(i) + "Function.java"), generateFunction(i));
            Files.writeString(FUNCTIONAL_PACKAGE.resolve(prefix(i) + "Consumer.java"), generateConsumer(i));
            Files.writeString(FUNCTIONAL_PACKAGE.resolve(prefix(i) + "Predicate.java"), generatePredicate(i));
        }));
    }

    private static String generateFunction(int size) {
        String className = prefix(size) + "Function";
        StringBuilder builder = new StringBuilder();
        builder.append("package ru.prohor.universe.jocasta.core.functional;\n\n");
        if (size == 1)
            builder.append("import java.util.function.Function;\n\n");
        else if (size == 2)
            builder.append("import java.util.function.BiFunction;\n\n");
        builder.append("@FunctionalInterface\n");
        builder.append("public interface ");
        builder.append(className);
        String generic = IntStream.range(1, size + 1)
                .mapToObj(i -> "T" + i + ", ")
                .collect(Collectors.joining("", "<", "R>"));
        builder.append(generic);
        if (size == 1)
            builder.append(" extends Function").append(generic);
        else if (size == 2)
            builder.append(" extends BiFunction").append(generic);
        builder.append(" {\n");
        if (size == 1 || size == 2)
            builder.append("    @Override\n");
        builder.append(IntStream.range(1, size + 1).mapToObj(i -> "T" + i + " t" + i).collect(Collectors.joining(
                ", ",
                "    R apply(",
                ");\n"
        )));
        if (size == 1)
            builder.append("\n    static <T> MonoFunction<T, T> identity() {\n        return t -> t;\n    }\n");
        builder.append("}\n");
        return builder.toString();
    }

    private static String generateConsumer(int size) {
        String className = prefix(size) + "Consumer";
        StringBuilder builder = new StringBuilder();
        builder.append("package ru.prohor.universe.jocasta.core.functional;\n\n");
        if (size == 1)
            builder.append("import java.util.function.Consumer;\n\n");
        else if (size == 2)
            builder.append("import java.util.function.BiConsumer;\n\n");
        builder.append("@FunctionalInterface\n");
        builder.append("public interface ");
        builder.append(className);
        String generic = IntStream.range(1, size + 1)
                .mapToObj(i -> "T" + i)
                .collect(Collectors.joining(", ", "<", ">"));
        if (generic.length() == 2)
            generic = "";
        builder.append(generic);
        if (size == 1)
            builder.append(" extends Consumer").append(generic);
        else if (size == 2)
            builder.append(" extends BiConsumer").append(generic);
        builder.append(" {\n");
        if (size == 1 || size == 2)
            builder.append("    @Override\n");
        builder.append(IntStream.range(1, size + 1).mapToObj(i -> "T" + i + " t" + i).collect(Collectors.joining(
                ", ",
                "    void accept(",
                ");\n"
        )));
        builder.append("}\n");
        return builder.toString();
    }

    private static String generatePredicate(int size) {
        String className = prefix(size) + "Predicate";
        StringBuilder builder = new StringBuilder();
        builder.append("package ru.prohor.universe.jocasta.core.functional;\n\n");

        if (size > 1)
            builder.append("import java.util.Objects;\n\n");
        builder.append("@FunctionalInterface\n");
        builder.append("public interface ");
        builder.append(className);
        String generic = IntStream.range(1, size + 1)
                .mapToObj(i -> "T" + i)
                .collect(Collectors.joining(", ", "<", ">"));
        if (generic.length() == 2)
            generic = "";
        builder.append(generic);
        builder.append(" {\n");
        builder.append(IntStream.range(1, size + 1).mapToObj(i -> "T" + i + " t" + i).collect(Collectors.joining(
                ", ",
                "    boolean test(",
                ");\n\n"
        )));

        if (size > 1) {
            builder.append("    static ");
            builder.append(generic);
            builder.append(" ");
            builder.append(className);
            builder.append(generic);
            builder.append(" allEquals() {\n        return ");
            if (size == 2)
                builder.append("Objects::equals");
            else {
                builder.append("(");
                builder.append(IntStream.range(1, size + 1).mapToObj(i -> "t" + i).collect(Collectors.joining(", ")));
                builder.append(") -> ");
                String delimiter = size >= 5 ? " &&\n                " : " && ";
                builder.append(
                        IntStream.range(1, size)
                                .mapToObj(i -> "Objects.equals(t" + i + ", t" + (i + 1) + ")")
                                .collect(Collectors.joining(delimiter))
                );
            }
            builder.append(";\n    }\n\n");
        }

        String defaultLogicalPrefix = "    default " + prefix(size) + "Predicate" + generic + " ";
        String other = prefix(size) + "Predicate" + IntStream.range(1, size + 1)
                .mapToObj(i -> "? super T" + i)
                .collect(Collectors.joining(", ", "<", "> other"));
        other = other.replaceAll("<>", "");
        String paramsWithBrackets = IntStream.range(1, size + 1)
                .mapToObj(i -> "t" + i)
                .collect(Collectors.joining(", ", "(", ")"));
        if (size >= 5)
            other = "\n            " + other + "\n    ";

        // and
        builder.append(defaultLogicalPrefix);
        builder.append("and(");
        builder.append(other);
        builder.append(") {\n        return ");
        builder.append(paramsWithBrackets);
        builder.append(" -> test");
        builder.append(paramsWithBrackets);
        builder.append(" &&");
        if (size >= 5)
            builder.append("\n                ");
        else
            builder.append(" ");
        builder.append("other.test");
        builder.append(paramsWithBrackets);
        builder.append(";\n    }\n\n");

        // negate
        builder.append(defaultLogicalPrefix);
        builder.append("negate() {\n        return ");
        builder.append(paramsWithBrackets);
        builder.append(" -> !test");
        builder.append(paramsWithBrackets);
        builder.append(";\n    }\n\n");

        // or
        builder.append(defaultLogicalPrefix);
        builder.append("or(");
        builder.append(other);
        builder.append(") {\n        return ");
        builder.append(paramsWithBrackets);
        builder.append(" -> test");
        builder.append(paramsWithBrackets);

        builder.append(" ||");
        if (size >= 5)
            builder.append("\n                ");
        else
            builder.append(" ");
        builder.append("other.test");
        builder.append(paramsWithBrackets);
        builder.append(";\n    }\n");

        builder.append("}\n");
        return builder.toString();
    }
}
