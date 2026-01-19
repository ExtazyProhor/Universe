package ru.prohor.universe.padawan;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public class CompareJsonsTest {
    @Test
    public void compare() throws JSONException {
        String expected = PadawanTest.read("json_expected.json");
        String actual = PadawanTest.read("json_actual.json");
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT);
    }
}
