package ru.prohor.universe.bobafett.feature.currency;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.prohor.universe.bobafett.data.Currency;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

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
        String result = formatter.format(List.of(usdRate), Opt.empty());
        String expected = "Курс валют сейчас:\n\n- 1 Доллар США (USD 🇺🇸) = 100,00 ₽";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Должен подставлять дефолтный эмодзи монеты, если флаг null (BTC)")
    void format_CurrencyWithoutFlag_UsesDefaultCoinEmoji() {
        Rate btcRate = new Rate(Currency.BTC, 0.0000002);
        String result = formatter.format(List.of(btcRate), Opt.empty());
        assertTrue(result.contains("(BTC 🪙)"));
        assertTrue(result.endsWith("₽"));
    }

    @Test
    @DisplayName("Должен корректно форматировать дешевую валюту (IRR) с умножением на 1000")
    void format_CheapCurrency_MultipliesByThousandAndUsesCorrectName() {
        Rate irrRate = new Rate(Currency.IRR, 2.0);
        String result = formatter.format(List.of(irrRate), Opt.empty());
        String expected = "Курс валют сейчас:\n\n- 1000 Иранских риалов (IRR 🇮🇷) = 500,00 ₽";
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Должен объединять несколько валют через перенос строки")
    void format_MultipleCurrencies_JoinsWithNewline() {
        Rate usdRate = new Rate(Currency.USD, 0.01);
        Rate irrRate = new Rate(Currency.IRR, 2.0);

        String result = formatter.format(List.of(usdRate, irrRate), Opt.empty());
        String expected = """
                Курс валют сейчас:
                
                - 1 Доллар США (USD 🇺🇸) = 100,00 ₽
                - 1000 Иранских риалов (IRR 🇮🇷) = 500,00 ₽""";
        assertEquals(expected, result);
    }

    @ParameterizedTest(name = "Для курса {0} ожидается отформатированное значение {1}")
    @CsvSource({
            "0.0142845, 70,01",
            "0.01, 100,00",
            "0.00999000999, 100,10",
            "0.00999950002, 100,01",
            "0.01000049997, 100,00"
    })
    @DisplayName("Должен корректно округлять значения по HALF_UP и выводить с запятой")
    void format_RoundingAndLocale_FormatsWithCommaAndHalfUp(double rateValue, String expectedValue) {
        Rate rate = new Rate(Currency.USD, rateValue);
        String result = formatter.format(List.of(rate), Opt.empty());

        assertTrue(
                result.contains("= " + expectedValue),
                "Ожидалось форматирование значения '" + expectedValue + "', но получено: " + result
        );
    }

    @Test
    @DisplayName("Должен разделять целую часть пробелом по 3 разряда для больших сумм")
    void format_LargeAmount_SeparatesGroupsWithSpaces() {
        Rate btcRate = new Rate(Currency.BTC, 0.0000002); // 5 000 000,00 рублей.
        String result = formatter.format(List.of(btcRate), Opt.empty());
        String numberPart = result.substring(result.indexOf("=") + 2).trim();

        assertTrue(
                numberPart.startsWith("5\u00A0000\u00A0000,00"),
                "Целая часть должна разделяться пробелами по 3 разряда. Было: " + numberPart
        );
    }

    @Test
    @DisplayName("Должен показывать рост курса")
    void format_RateIncreased_ShowsIncrease() {
        Rate lastRate = new Rate(Currency.USD, 0.01); // 100,00 ₽
        Rate currentRate = new Rate(Currency.USD, 0.00987154); // 101,30 ₽

        String result = formatter.format(
                List.of(currentRate),
                Opt.of(List.of(lastRate))
        );

        assertTrue(
                result.contains("= 101,30 ₽ 📈 +1,30 ₽"),
                "Ожидался рост курса, но получено: " + result
        );
    }

    @Test
    @DisplayName("Должен показывать падение курса")
    void format_RateDecreased_ShowsDecrease() {
        Rate lastRate = new Rate(Currency.USD, 0.01); // 100,00 ₽
        Rate currentRate = new Rate(Currency.USD, 0.01010101); // 99,00 ₽

        String result = formatter.format(
                List.of(currentRate),
                Opt.of(List.of(lastRate))
        );

        assertTrue(
                result.contains("= 99,00 ₽ 📉 -1,00 ₽"),
                "Ожидалось падение курса, но получено: " + result
        );
    }

    @Test
    @DisplayName("Не должен показывать изменение, если курс не изменился")
    void format_RateUnchanged_DoesNotShowChange() {
        Rate lastRate = new Rate(Currency.USD, 0.01);
        Rate currentRate = new Rate(Currency.USD, 0.01);

        String result = formatter.format(
                List.of(currentRate),
                Opt.of(List.of(lastRate))
        );

        assertEquals(
                "Курс валют сейчас:\n\n- 1 Доллар США (USD 🇺🇸) = 100,00 ₽",
                result
        );
    }

    @Test
    @DisplayName("Не должен показывать изменение, если предыдущие курсы неизвестны")
    void format_WithoutLastRates_DoesNotShowChange() {
        Rate currentRate = new Rate(Currency.USD, 0.01);
        String result = formatter.format(List.of(currentRate), Opt.empty());
        assertEquals("Курс валют сейчас:\n\n- 1 Доллар США (USD 🇺🇸) = 100,00 ₽", result);
    }

    @Test
    @DisplayName("Должен корректно рассчитывать изменение для дешевой валюты с умножением на 1000")
    void format_CheapCurrency_CalculatesChangeForThousandUnits() {
        Rate lastRate = new Rate(Currency.IRR, 2.0); // 500,00 ₽ за 1000
        Rate currentRate = new Rate(Currency.IRR, 1.96078431); // 510,00 ₽ за 1000

        String result = formatter.format(
                List.of(currentRate),
                Opt.of(List.of(lastRate))
        );

        assertTrue(
                result.contains("= 510,00 ₽ 📈 +10,00 ₽"),
                "Ожидался рост на 10 ₽, но получено: " + result
        );
    }
}
