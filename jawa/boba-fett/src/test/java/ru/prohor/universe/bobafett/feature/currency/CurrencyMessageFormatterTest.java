package ru.prohor.universe.bobafett.feature.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyMessageFormatterTest {
    private CurrencyMessageFormatterImpl formatter;

    @BeforeEach
    void setUp() {
        formatter = new CurrencyMessageFormatterImpl();
    }

    @Test
    @DisplayName("Должен корректно форматировать дорогую валюту (USD) с флагом")
    void format_ExpensiveCurrencyWithFlag_FormatsCorrectly() {
        Rate usdRate = new Rate(Currency.USD, 0.01);
        String result = formatter.format(List.of(usdRate));
        String expected = "Курс валют сейчас:\n\n- 1 Доллар США (USD 🇺🇸) = 100,00 рублей";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Должен подставлять дефолтный эмодзи монеты, если флаг null (BTC)")
    void format_CurrencyWithoutFlag_UsesDefaultCoinEmoji() {
        Rate btcRate = new Rate(Currency.BTC, 0.0000002);
        String result = formatter.format(List.of(btcRate));
        assertTrue(result.contains("(BTC 🪙)"));
        assertTrue(result.endsWith("рублей"));
    }

    @Test
    @DisplayName("Должен корректно форматировать дешевую валюту (IRR) с умножением на 1000")
    void format_CheapCurrency_MultipliesByThousandAndUsesCorrectName() {
        Rate irrRate = new Rate(Currency.IRR, 2.0);
        String result = formatter.format(List.of(irrRate));
        String expected = "Курс валют сейчас:\n\n- 1000 Иранских риалов (IRR 🇮🇷) = 500,00 рублей";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Должен объединять несколько валют через перенос строки")
    void format_MultipleCurrencies_JoinsWithNewline() {
        Rate usdRate = new Rate(Currency.USD, 0.01);
        Rate irrRate = new Rate(Currency.IRR, 2.0);

        String result = formatter.format(List.of(usdRate, irrRate));
        String expected = """
                Курс валют сейчас:
                
                - 1 Доллар США (USD 🇺🇸) = 100,00 рублей
                - 1000 Иранских риалов (IRR 🇮🇷) = 500,00 рублей""";
        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "Для курса {0} руб. окончание должно соответствовать {1}")
    @CsvSource({
            "1.0, рубль", // 1.0 / 1.0 = 1 рубль
            "0.047619, рубль", // 1.0 / 0.047619 ≈ 21 рубль
            "2.0, рублей", // 1.0 / 2.0 * 1000 = 500 рублей
            "0.5, рубля", // 1.0 / 0.5 = 2 рубля
    })
    @DisplayName("Проверка склонения слова рубль через подбор курса")
    void format_DeclensionRules_CheckRublesWord(double rateValue, String expectedWord) {
        Rate rate = new Rate(Currency.EUR, rateValue);
        String result = formatter.format(List.of(rate));

        assertTrue(
                result.endsWith(expectedWord),
                "Ожидалось окончание на '" + expectedWord + "' для рейта " + rateValue + ". Результат: " + result
        );
    }

    @ParameterizedTest(name = "Для курса {0} ожидается отформатированная строка \"{1}\"")
    @CsvSource({
            "0.0142845, 70,01",
            "0.01, 100,00",
            "0.00999000999, 100,10",
            "0.00999950002, 100,01",
            "0.01000049997, 100,00"
    })
    @DisplayName("Должен корректно округлять копейки по HALF_UP и выводить с запятой (Локаль RU)")
    void format_RoundingAndLocale_FormatsWithCommaAndHalfUp(double rateValue, String expectedValue) {
        Rate rate = new Rate(Currency.USD, rateValue);
        String result = formatter.format(List.of(rate));

        assertTrue(
                result.contains("= " + expectedValue),
                "Ожидалось форматирование значения '" + expectedValue + "', но получено: " + result
        );
    }

    @Test
    @DisplayName("Должен разделять целую часть пробелом по 3 разряда для больших сумм")
    void format_LargeAmount_SeparatesGroupsWithSpaces() {
        Rate btcRate = new Rate(Currency.BTC, 0.0000002); // 5 000 000,00 рублей.
        String result = formatter.format(List.of(btcRate));
        String numberPart = result.substring(result.indexOf("=") + 2).trim();

        assertTrue(
                numberPart.startsWith("5\u00A0000\u00A0000,00"),
                "Целая часть должна разделяться пробелами по 3 разряда. Было: " + numberPart
        );
    }

    @ParameterizedTest(name = "Для {0} руб. должно быть {1}")
    @CsvSource({
            "1, рубль",
            "21, рубль",
            "101, рубль",
            "2, рубля",
            "4, рубля",
            "24, рубля",
            "5, рублей",
            "10, рублей",
            "11, рублей",
            "14, рублей",
            "19, рублей",
            "20, рублей",
            "112, рублей"
    })
    @DisplayName("Прямой тест склонения числительных")
    void getRussianRublesWord_DirectTest(long rubles, String expectedWord) {
        String actualWord = formatter.getRussianRublesWord(rubles);
        assertEquals(expectedWord, actualWord);
    }
}
