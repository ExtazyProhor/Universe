package ru.prohor.universe.jocasta.core.feature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.prohor.universe.jocasta.core.collections.common.Opt;
import ru.prohor.universe.jocasta.core.features.fieldref.FR;
import ru.prohor.universe.jocasta.core.features.fieldref.FieldProperties;
import ru.prohor.universe.jocasta.core.features.fieldref.Name;

public class FieldReferenceTest {
    // without nesting

    @Test
    public void testPrimitiveFieldWithoutNestingNames() {
        FieldProperties<Outer, String> str = FR.wrap(Outer::str);
        FieldProperties<Outer, String> optStrPresent = FR.wrapO(Outer::optStrPresent);
        FieldProperties<Outer, String> optStrEmpty = FR.wrapO(Outer::optStrEmpty);

        Assertions.assertEquals("str", str.name());
        Assertions.assertEquals("optStrPresent", optStrPresent.name());
        Assertions.assertEquals("optStrEmpty", optStrEmpty.name());
    }

    @Test
    public void testPrimitiveFieldWithoutNestingGetters() {
        FieldProperties<Outer, String> str = FR.wrap(Outer::str);
        FieldProperties<Outer, String> optStrPresent = FR.wrapO(Outer::optStrPresent);
        FieldProperties<Outer, String> optStrEmpty = FR.wrapO(Outer::optStrEmpty);

        test(str, Opt.of("a"));
        test(optStrPresent, Opt.of("b"));
        test(optStrEmpty, Opt.empty());
    }

    @Test
    public void testPrimitiveFieldWithoutNestingByChainNames() {
        FieldProperties<Outer, String> str = FR.chain(Outer::str);
        FieldProperties<Outer, String> optStrPresent = FR.chainO(Outer::optStrPresent);
        FieldProperties<Outer, String> optStrEmpty = FR.chainO(Outer::optStrEmpty);

        Assertions.assertEquals("str", str.name());
        Assertions.assertEquals("optStrPresent", optStrPresent.name());
        Assertions.assertEquals("optStrEmpty", optStrEmpty.name());
    }

    @Test
    public void testPrimitiveFieldWithoutNestingByChainGetters() {
        FieldProperties<Outer, String> str = FR.chain(Outer::str);
        FieldProperties<Outer, String> optStrPresent = FR.chainO(Outer::optStrPresent);
        FieldProperties<Outer, String> optStrEmpty = FR.chainO(Outer::optStrEmpty);

        test(str, Opt.of("a"));
        test(optStrPresent, Opt.of("b"));
        test(optStrEmpty, Opt.empty());
    }

    @Test
    public void testCompositeFieldWithoutNestingNames() {
        FieldProperties<Outer, Middle> middle = FR.wrap(Outer::middle);
        FieldProperties<Outer, Middle> optMiddlePresent = FR.wrapO(Outer::optMiddlePresent);
        FieldProperties<Outer, Middle> optMiddleEmpty = FR.wrapO(Outer::optMiddleEmpty);

        Assertions.assertEquals("middle", middle.name());
        Assertions.assertEquals("optMiddlePresent", optMiddlePresent.name());
        Assertions.assertEquals("optMiddleEmpty", optMiddleEmpty.name());
    }

    @Test
    public void testCompositeFieldWithoutNestingGetters() {
        FieldProperties<Outer, Middle> middle = FR.wrap(Outer::middle);
        FieldProperties<Outer, Middle> optMiddlePresent = FR.wrapO(Outer::optMiddlePresent);
        FieldProperties<Outer, Middle> optMiddleEmpty = FR.wrapO(Outer::optMiddleEmpty);

        test(middle, Opt.of(OUTER.middle));
        test(optMiddlePresent, OUTER.optMiddlePresent);
        test(optMiddleEmpty, Opt.empty());
    }

    @Test
    public void testCompositeFieldWithoutNestingByChainNames() {
        FieldProperties<Outer, Middle> middle = FR.chain(Outer::middle);
        FieldProperties<Outer, Middle> optMiddlePresent = FR.chainO(Outer::optMiddlePresent);
        FieldProperties<Outer, Middle> optMiddleEmpty = FR.chainO(Outer::optMiddleEmpty);

        Assertions.assertEquals("middle", middle.name());
        Assertions.assertEquals("optMiddlePresent", optMiddlePresent.name());
        Assertions.assertEquals("optMiddleEmpty", optMiddleEmpty.name());
    }

    @Test
    public void testCompositeFieldWithoutNestingByChainGetters() {
        FieldProperties<Outer, Middle> middle = FR.chain(Outer::middle);
        FieldProperties<Outer, Middle> optMiddlePresent = FR.chainO(Outer::optMiddlePresent);
        FieldProperties<Outer, Middle> optMiddleEmpty = FR.chainO(Outer::optMiddleEmpty);

        test(middle, Opt.of(OUTER.middle));
        test(optMiddlePresent, OUTER.optMiddlePresent);
        test(optMiddleEmpty, Opt.empty());
    }

    // once nested

    @Test
    public void testMiddleNestedFieldsByMiddleNames() {
        FieldProperties<Outer, Inner> inner = FR.chain(Outer::middle).then(Middle::inner);
        FieldProperties<Outer, Inner> optInnerPresent = FR.chain(Outer::middle).thenO(Middle::optInnerPresent);
        FieldProperties<Outer, Inner> optInnerEmpty = FR.chain(Outer::middle).thenO(Middle::optInnerEmpty);

        Assertions.assertEquals("middle.inner", inner.name());
        Assertions.assertEquals("middle.optInnerPresent", optInnerPresent.name());
        Assertions.assertEquals("middle.optInnerEmpty", optInnerEmpty.name());
    }

    @Test
    public void testMiddleNestedFieldsByMiddleGetters() {
        FieldProperties<Outer, Inner> inner = FR.chain(Outer::middle).then(Middle::inner);
        FieldProperties<Outer, Inner> optInnerPresent = FR.chain(Outer::middle).thenO(Middle::optInnerPresent);
        FieldProperties<Outer, Inner> optInnerEmpty = FR.chain(Outer::middle).thenO(Middle::optInnerEmpty);

        test(inner, Opt.of(OUTER.middle.inner));
        test(optInnerPresent, OUTER.middle.optInnerPresent);
        test(optInnerEmpty, Opt.empty());
    }

    @Test
    public void testMiddleNestedFieldsByOptMiddlePresentNames() {
        FieldProperties<Outer, Inner> inner = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner);
        FieldProperties<Outer, Inner> optInnerPresent = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent);
        FieldProperties<Outer, Inner> optInnerEmpty = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty);

        Assertions.assertEquals("optMiddlePresent.inner", inner.name());
        Assertions.assertEquals("optMiddlePresent.optInnerPresent", optInnerPresent.name());
        Assertions.assertEquals("optMiddlePresent.optInnerEmpty", optInnerEmpty.name());
    }

    @Test
    public void testMiddleNestedFieldsByOptMiddlePresentGetters() {
        FieldProperties<Outer, Inner> inner = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner);
        FieldProperties<Outer, Inner> optInnerPresent = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent);
        FieldProperties<Outer, Inner> optInnerEmpty = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty);

        test(inner, Opt.of(OUTER.optMiddlePresent.get().inner));
        test(optInnerPresent, OUTER.optMiddlePresent.get().optInnerPresent);
        test(optInnerEmpty, Opt.empty());
    }

    @Test
    public void testMiddleNestedFieldsByOptMiddleEmptyNames() {
        FieldProperties<Outer, Inner> inner = FR.chainO(Outer::optMiddleEmpty).then(Middle::inner);
        FieldProperties<Outer, Inner> optInnerPresent = FR.chainO(Outer::optMiddleEmpty).thenO(Middle::optInnerPresent);
        FieldProperties<Outer, Inner> optInnerEmpty = FR.chainO(Outer::optMiddleEmpty).thenO(Middle::optInnerEmpty);

        Assertions.assertEquals("optMiddleEmpty.inner", inner.name());
        Assertions.assertEquals("optMiddleEmpty.optInnerPresent", optInnerPresent.name());
        Assertions.assertEquals("optMiddleEmpty.optInnerEmpty", optInnerEmpty.name());
    }

    @Test
    public void testMiddleNestedFieldsByOptMiddleEmptyGetters() {
        FieldProperties<Outer, Inner> inner = FR.chainO(Outer::optMiddleEmpty).then(Middle::inner);
        FieldProperties<Outer, Inner> optInnerPresent = FR.chainO(Outer::optMiddleEmpty).thenO(Middle::optInnerPresent);
        FieldProperties<Outer, Inner> optInnerEmpty = FR.chainO(Outer::optMiddleEmpty).thenO(Middle::optInnerEmpty);

        test(inner, Opt.empty());
        test(optInnerPresent, Opt.empty());
        test(optInnerEmpty, Opt.empty());
    }

    // twice nested

    @Test
    public void testTwiceNestedFieldsByMiddleAndInnerNames() {
        FieldProperties<Outer, Integer> num = FR.chain(Outer::middle)
                .then(Middle::inner)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chain(Outer::middle)
                .then(Middle::inner)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chain(Outer::middle)
                .then(Middle::inner)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("middle.inner.num", num.name());
        Assertions.assertEquals("middle.inner.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("middle.inner.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByMiddleAndInnerGetters() {
        FieldProperties<Outer, Integer> num = FR.chain(Outer::middle)
                .then(Middle::inner)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chain(Outer::middle)
                .then(Middle::inner)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chain(Outer::middle)
                .then(Middle::inner)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.of(1));
        test(optNumPresent, Opt.of(2));
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByMiddleAndOptInnerPresentNames() {
        FieldProperties<Outer, Integer> num = FR.chain(Outer::middle)
                .thenO(Middle::optInnerPresent)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chain(Outer::middle)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chain(Outer::middle)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("middle.optInnerPresent.num", num.name());
        Assertions.assertEquals("middle.optInnerPresent.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("middle.optInnerPresent.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByMiddleAndOptInnerPresentGetters() {
        FieldProperties<Outer, Integer> num = FR.chain(Outer::middle)
                .thenO(Middle::optInnerPresent)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chain(Outer::middle)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chain(Outer::middle)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.of(3));
        test(optNumPresent, Opt.of(4));
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByMiddleAndOptInnerEmptyNames() {
        FieldProperties<Outer, Integer> num = FR.chain(Outer::middle)
                .thenO(Middle::optInnerEmpty)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chain(Outer::middle)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chain(Outer::middle)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("middle.optInnerEmpty.num", num.name());
        Assertions.assertEquals("middle.optInnerEmpty.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("middle.optInnerEmpty.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByMiddleAndOptInnerEmptyGetters() {
        FieldProperties<Outer, Integer> num = FR.chain(Outer::middle)
                .thenO(Middle::optInnerEmpty)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chain(Outer::middle)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chain(Outer::middle)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.empty());
        test(optNumPresent, Opt.empty());
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddlePresentAndInnerNames() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("optMiddlePresent.inner.num", num.name());
        Assertions.assertEquals("optMiddlePresent.inner.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("optMiddlePresent.inner.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddlePresentAndInnerGetters() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddlePresent)
                .then(Middle::inner)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.of(5));
        test(optNumPresent, Opt.of(6));
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddlePresentAndOptInnerPresentNames() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("optMiddlePresent.optInnerPresent.num", num.name());
        Assertions.assertEquals("optMiddlePresent.optInnerPresent.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("optMiddlePresent.optInnerPresent.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddlePresentAndOptInnerPresentGetters() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.of(7));
        test(optNumPresent, Opt.of(8));
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddlePresentAndOptInnerEmptyNames() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("optMiddlePresent.optInnerEmpty.num", num.name());
        Assertions.assertEquals("optMiddlePresent.optInnerEmpty.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("optMiddlePresent.optInnerEmpty.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddlePresentAndOptInnerEmptyGetters() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.empty());
        test(optNumPresent, Opt.empty());
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddleEmptyAndInnerNames() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddleEmpty)
                .then(Middle::inner)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddleEmpty)
                .then(Middle::inner)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddleEmpty)
                .then(Middle::inner)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("optMiddleEmpty.inner.num", num.name());
        Assertions.assertEquals("optMiddleEmpty.inner.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("optMiddleEmpty.inner.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddleEmptyAndInnerGetters() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddleEmpty)
                .then(Middle::inner)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddleEmpty)
                .then(Middle::inner)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddleEmpty)
                .then(Middle::inner)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.empty());
        test(optNumPresent, Opt.empty());
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddleEmptyAndOptInnerPresentNames() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerPresent)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("optMiddleEmpty.optInnerPresent.num", num.name());
        Assertions.assertEquals("optMiddleEmpty.optInnerPresent.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("optMiddleEmpty.optInnerPresent.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddleEmptyAndOptInnerPresentGetters() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerPresent)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.empty());
        test(optNumPresent, Opt.empty());
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddleEmptyAndOptInnerEmptyNames() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerEmpty)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumEmpty);

        Assertions.assertEquals("optMiddleEmpty.optInnerEmpty.num", num.name());
        Assertions.assertEquals("optMiddleEmpty.optInnerEmpty.optNumPresent", optNumPresent.name());
        Assertions.assertEquals("optMiddleEmpty.optInnerEmpty.optNumEmpty", optNumEmpty.name());
    }

    @Test
    public void testTwiceNestedFieldsByOptMiddleEmptyAndOptInnerEmptyGetters() {
        FieldProperties<Outer, Integer> num = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerEmpty)
                .then(Inner::num);
        FieldProperties<Outer, Integer> optNumPresent = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> optNumEmpty = FR.chainO(Outer::optMiddleEmpty)
                .thenO(Middle::optInnerEmpty)
                .thenO(Inner::optNumEmpty);

        test(num, Opt.empty());
        test(optNumPresent, Opt.empty());
        test(optNumEmpty, Opt.empty());
    }

    @Test
    public void testCustomDelimiters() {
        FieldProperties<Outer, Integer> chain1 = FR.chain(Outer::middle)
                .thenO(Middle::optInnerPresent)
                .thenO(Inner::optNumEmpty);
        FieldProperties<Outer, Integer> chain2 = FR.chainO(Outer::optMiddleEmpty)
                .then(Middle::inner)
                .thenO(Inner::optNumPresent);
        FieldProperties<Outer, Integer> chain3 = FR.chainO(Outer::optMiddlePresent)
                .thenO(Middle::optInnerEmpty)
                .then(Inner::num);

        Assertions.assertEquals("middle/optInnerPresent/optNumEmpty", chain1.name("/"));
        Assertions.assertEquals("optMiddleEmpty--inner--optNumPresent", chain2.name("--"));
        String expected = "optMiddlePresent" + "optInnerEmpty" + "num";
        Assertions.assertEquals(expected, chain3.name(""));
    }

    @Test
    public void testCustomNamesByAnnotation() {
        FieldProperties<ClassWithAnnotation, ?> withoutAnnotation = FR.wrap(
                ClassWithAnnotation::fieldWithoutAnnotation
        );
        FieldProperties<ClassWithAnnotation, ?> withAnnotation = FR.wrap(
                ClassWithAnnotation::fieldWithAnnotation
        );

        Assertions.assertEquals("fieldWithoutAnnotation", withoutAnnotation.name());
        Assertions.assertEquals("field_with_annotation", withAnnotation.name());
    }

    private <T> void test(FieldProperties<Outer, T> fieldProperties, Opt<T> expected) {
        Opt<T> actual = fieldProperties.getO(OUTER);
        if (expected.isEmpty()) {
            Assertions.assertTrue(actual.isEmpty());
            return;
        }
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertEquals(expected.get(), actual.get());
    }

    private static final Outer OUTER = Outer.create();

    record Outer(
            String str,
            Opt<String> optStrPresent,
            Opt<String> optStrEmpty,
            Middle middle,
            Opt<Middle> optMiddlePresent,
            Opt<Middle> optMiddleEmpty
    ) {
        private static Outer create() {
            return new Outer(
                    "a",
                    Opt.of("b"),
                    Opt.empty(),
                    Middle.create(
                            Inner.create(1, 2),
                            Inner.create(3, 4)
                    ),
                    Opt.of(Middle.create(
                            Inner.create(5, 6),
                            Inner.create(7, 8)
                    )),
                    Opt.empty()
            );
        }
    }

    record Middle(
            Inner inner,
            Opt<Inner> optInnerPresent,
            Opt<Inner> optInnerEmpty
    ) {
        private static Middle create(Inner inner, Inner present) {
            return new Middle(
                    inner,
                    Opt.of(present),
                    Opt.empty()
            );
        }
    }

    record Inner(
            Integer num,
            Opt<Integer> optNumPresent,
            Opt<Integer> optNumEmpty
    ) {
        private static Inner create(Integer num, Integer present) {
            return new Inner(
                    num,
                    Opt.of(present),
                    Opt.empty()
            );
        }
    }

    record ClassWithAnnotation(
            String fieldWithoutAnnotation,
            @Name("field_with_annotation")
            String fieldWithAnnotation
    ) {}
}
