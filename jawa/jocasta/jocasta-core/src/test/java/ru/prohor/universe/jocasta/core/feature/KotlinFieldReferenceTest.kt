package ru.prohor.universe.jocasta.core.feature

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.prohor.universe.jocasta.core.features.fieldref.KFR

class KotlinFieldReferenceTest {
    @Test
    fun readsPropertiesFromARegularKotlinClass() {
        val name = KFR.wrap(KotlinClass::name)
        val data = KFR.wrap(KotlinClass::data)

        assertEquals("name", name.name())
        assertEquals("data", data.name())

        val inner = DataKotlinClass("Alice", 0)
        val instance = KotlinClass("Alice", inner)
        assertEquals("Alice", name.getO(instance).get())
        assertEquals(inner, data.getO(instance).get())
    }

    @Test
    fun readsPropertiesFromADataClass() {
        val age = KFR.wrap(DataKotlinClass::age)

        assertEquals("age", age.name())
        assertEquals(42, age.getO(DataKotlinClass("Alice", 42)).get())
    }

    @Test
    fun readsNestedPropertiesFromKotlinClasses() {
        val age = KFR.chain(KotlinClass::data).then(KFR.wrap(DataKotlinClass::age))

        val source = KotlinClass("Alice", DataKotlinClass("Alice", 42))

        assertEquals("data.age", age.name())
        assertEquals("data_age", age.name("_"))
        assertEquals(42, age.getO(source).get())
    }

    @Test
    fun readsPropertiesThroughPresentAndEmptyKotlinOptions() {
        val age = KFR.chainO(KotlinClass::optionalData).then(KFR.wrap(DataKotlinClass::age))
        val label = KFR.chain(KotlinClass::data).thenO(KFR.wrapO(DataKotlinClass::optionalLabel))

        val source = KotlinClass(
            "Alice",
            DataKotlinClass("Alice", 42, "present"),
            DataKotlinClass("Bob", 7)
        )
        val sourceWithEmptyOptionalData = KotlinClass(
            "Alice",
            DataKotlinClass("Alice", 42),
            null
        )

        assertEquals("optionalData.age", age.name())
        assertEquals("data.optionalLabel", label.name())
        assertEquals(7, age.getO(source).get())
        assertEquals("present", label.getO(source).get())
        assertTrue(age.getO(sourceWithEmptyOptionalData).isEmpty)
        assertTrue(label.getO(sourceWithEmptyOptionalData).isEmpty)
    }

    private class KotlinClass(
        val name: String,
        val data: DataKotlinClass,
        val optionalData: DataKotlinClass? = null
    )

    private data class DataKotlinClass(
        val name: String,
        val age: Int,
        val optionalLabel: String? = null
    )
}
