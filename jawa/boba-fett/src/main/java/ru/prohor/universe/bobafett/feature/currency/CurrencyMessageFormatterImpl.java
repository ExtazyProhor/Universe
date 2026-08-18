package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.dto.Rate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CurrencyMessageFormatterImpl implements CurrencyMessageFormatter {
    private final NumberFormat numberFormat = setupNumberFormat();

    private NumberFormat setupNumberFormat() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU"));
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        return numberFormat;
    }

    @Override
    public String format(List<Rate> rates) {
        return "Курс валют сейчас:\n\n" + rates.stream().map(this::formatRate).collect(Collectors.joining("\n"));
    }

    private String formatRate(Rate rate) {
        BigDecimal rateToRussianRuble = BigDecimal.valueOf(1.0 / rate.getRateToRussianRuble());

        BigDecimal amount;
        long currencyAmount;
        String currencyName;
        if (rateToRussianRuble.compareTo(BigDecimal.ONE) < 0) {
            amount = rateToRussianRuble.multiply(BigDecimal.valueOf(1000));
            currencyAmount = 1000;
            currencyName = rate.getCurrency().russianNameForThousand;
        } else {
            amount = rateToRussianRuble;
            currencyAmount = 1;
            currencyName = rate.getCurrency().russianName;
        }

        String formattedValue = numberFormat.format(amount);
        String currencySign = getRussianRublesWord(amount.longValue());
        return "- " + currencyAmount + " " + currencyName + " (" + rate.getCurrency().code + " " +
                rate.getCurrency().flag + ") = " + formattedValue + " " + currencySign;
    }

    private String getRussianRublesWord(long rubles) {
        long lastTwoDigits = rubles % 100;
        long lastDigit = rubles % 10;

        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            return "рублей";
        }
        if (lastDigit == 1) {
            return "рубль";
        }
        if (lastDigit >= 2 && lastDigit <= 4) {
            return "рубля";
        }
        return "рублей";
    }
}
