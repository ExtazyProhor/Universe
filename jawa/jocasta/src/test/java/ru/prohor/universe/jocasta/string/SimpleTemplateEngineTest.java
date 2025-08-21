package ru.prohor.universe.jocasta.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleTemplateEngineTest {
    private record SimpleTemplateEngineTestData(
            String template,
            String processed,
            Set<String> retained,
            Map<String, String> values)
    {}

    private record SimpleTemplateEngineExceptionalTestData(
            String template,
            int errorCode)
    {}

    private static final String MAIN_CLASS_TEMPLATE = """
            package $$package$$
            ??spring??
            import org.springframework.boot.SpringApplication;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
            import org.springframework.scheduling.annotation.EnableScheduling;
                        
            @EnableScheduling
            @SpringBootApplication??spring??
            public class $$main$$ {
                public static void main(String[] args) {
                    ??spring??SpringApplication.run($$main$$.class, args);??spring??
                }
            }
            """;

    private static final String EMPTY_MAIN_CLASS = """
            package\s
                        
            public class  {
                public static void main(String[] args) {
            \s\s\s\s\s\s\s\s
                }
            }
            """;

    private static final String MAIN_CLASS_WITH_SPRING = """
            package com.example
                        
            import org.springframework.boot.SpringApplication;
            import org.springframework.boot.autoconfigure.SpringBootApplication;
            import org.springframework.scheduling.annotation.EnableScheduling;
                        
            @EnableScheduling
            @SpringBootApplication
            public class ExampleMain {
                public static void main(String[] args) {
                    SpringApplication.run(ExampleMain.class, args);
                }
            }
            """;

    private static final String MAIN_CLASS_WITHOUT_SPRING = """
            package com.example
                        
            public class ExampleMain {
                public static void main(String[] args) {
            \s\s\s\s\s\s\s\s
                }
            }
            """;

    private static final String ALL_CONDITIONAL_TEMPLATE = """
            ??if??@
            some text
            SOME TEXT
            $$k$$
            0123456789
            @??if??""";

    private static final String ALL_CONDITIONAL = """
            @
            some text
            SOME TEXT
            instead k
            0123456789
            @""";

    private static final String ONLY_VALUE = "{start $$value$$ end}";
    private static final String WITHOUT_VALUE = "{start  end}";
    private static final String WITH_VALUE = "{start some82t682706*Q^%!&%)\\/?| end}";
    private static final String WITH_MULTILINE_VALUE = """
            {start ->
            l1
            l2
            l3
            <- end}""";

    private static final String MULTILINE_VALUE = """
            ->
            l1
            l2
            l3
            <-""";

    private static final List<SimpleTemplateEngineTestData> TEST_PAIRS = List.of(
            new SimpleTemplateEngineTestData(
                    MAIN_CLASS_TEMPLATE,
                    EMPTY_MAIN_CLASS,
                    Collections.emptySet(),
                    Collections.emptyMap()
            ),
            new SimpleTemplateEngineTestData(
                    MAIN_CLASS_TEMPLATE,
                    EMPTY_MAIN_CLASS,
                    Set.of("k1", "k2"),
                    Map.of("k3", "v3", "k4", "v4")
            ),
            new SimpleTemplateEngineTestData(
                    MAIN_CLASS_TEMPLATE,
                    MAIN_CLASS_WITH_SPRING,
                    Set.of("spring"),
                    Map.of("package", "com.example", "main", "ExampleMain")
            ),
            new SimpleTemplateEngineTestData(
                    MAIN_CLASS_TEMPLATE,
                    MAIN_CLASS_WITH_SPRING,
                    Set.of("spring", "k1", "some-key_withChars1238895"),
                    Map.of("package", "com.example", "main", "ExampleMain", "KKK", "foot\n\nball")
            ),
            new SimpleTemplateEngineTestData(
                    MAIN_CLASS_TEMPLATE,
                    MAIN_CLASS_WITHOUT_SPRING,
                    Collections.emptySet(),
                    Map.of("package", "com.example", "main", "ExampleMain")
            ),
            new SimpleTemplateEngineTestData(
                    MAIN_CLASS_TEMPLATE,
                    MAIN_CLASS_WITHOUT_SPRING,
                    Set.of("k1", "some-key_withChars1238895"),
                    Map.of("package", "com.example", "main", "ExampleMain", "KKK", "foot\n\nball")
            ),
            new SimpleTemplateEngineTestData(
                    ALL_CONDITIONAL_TEMPLATE,
                    ALL_CONDITIONAL,
                    Set.of("if"),
                    Map.of("k", "instead k")
            ),
            new SimpleTemplateEngineTestData(
                    ALL_CONDITIONAL_TEMPLATE,
                    "",
                    Collections.emptySet(),
                    Map.of("k", "v", "1", "11")
            ),
            new SimpleTemplateEngineTestData(
                    ONLY_VALUE,
                    WITH_VALUE,
                    Collections.emptySet(),
                    Map.of("value", "some82t682706*Q^%!&%)\\/?|")
            ),
            new SimpleTemplateEngineTestData(
                    ONLY_VALUE,
                    WITHOUT_VALUE,
                    Collections.emptySet(),
                    Collections.emptyMap()
            ),
            new SimpleTemplateEngineTestData(
                    ONLY_VALUE,
                    WITH_MULTILINE_VALUE,
                    Collections.emptySet(),
                    Map.of("value", MULTILINE_VALUE)
            ),
            new SimpleTemplateEngineTestData(
                    "$$v$$",
                    "",
                    Collections.emptySet(),
                    Collections.emptyMap()
            ),
            new SimpleTemplateEngineTestData(
                    "$$v$$",
                    "string",
                    Collections.emptySet(),
                    Map.of("v", "string")
            )
    );

    private static final List<SimpleTemplateEngineExceptionalTestData> EXCEPTIONAL_PAIRS = List.of(
            new SimpleTemplateEngineExceptionalTestData(
                    "text ??key text",
                    SimpleTemplateEngine.TemplateParsingException.NO_CLOSING_SYMBOLS_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$key text",
                    SimpleTemplateEngine.TemplateParsingException.NO_CLOSING_SYMBOLS_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "??key????key??text $$key text",
                    SimpleTemplateEngine.TemplateParsingException.NO_CLOSING_SYMBOLS_ERROR_CODE
            ), new SimpleTemplateEngineExceptionalTestData(
                    "??key?? text",
                    SimpleTemplateEngine.TemplateParsingException.NO_CLOSING_CONDITION_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text ??key?? text ??key?? --- ??k1?? text ??k2?? text",
                    SimpleTemplateEngine.TemplateParsingException.DIFFERENT_CONDITION_KEYS_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$key $$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$ключ$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$/$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$\\key$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$|$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$($$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$]$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$^$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$#$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            ),
            new SimpleTemplateEngineExceptionalTestData(
                    "text $$%$$ text",
                    SimpleTemplateEngine.TemplateParsingException.ILLEGAL_CHARACTERS_IN_KEY_ERROR_CODE
            )
    );

    @Test
    public void testCommonTemplates() {
        for (SimpleTemplateEngineTestData pair : TEST_PAIRS) {
            Assertions.assertEquals(
                    pair.processed,
                    new SimpleTemplateEngine(pair.retained, pair.values).process(pair.template)
            );
        }
    }

    @Test
    public void testIllegalTemplates() {
        SimpleTemplateEngine engine = new SimpleTemplateEngine(Collections.emptySet(), Collections.emptyMap());
        int i = 0;
        for (SimpleTemplateEngineExceptionalTestData data : EXCEPTIONAL_PAIRS) {
            SimpleTemplateEngine.TemplateParsingException exception = Assertions.assertThrows(
                    SimpleTemplateEngine.TemplateParsingException.class,
                    () -> engine.process(data.template),
                    "Expected TemplateParsingException for template " + i
            );
            Assertions.assertEquals(
                    data.errorCode,
                    exception.errorCode(),
                    "Wrong error code for template " + i
            );
            ++i;
        }
    }
}
