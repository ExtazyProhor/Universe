package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.dto.Rate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CurrencyMessageFormatterImpl implements CurrencyMessageFormatter {
    @Override
    public String format(List<Rate> rates) {
        return "Курс валют сейчас:\n\n" + rates.stream().map(this::formatRate).collect(Collectors.joining("\n"));
    }

    private String formatRate(Rate rate) {
        BigDecimal bd = new BigDecimal(Double.toString(1.0 / rate.getRateToRussianRuble()));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        String formattedValue = String.format(Locale.ROOT, "%.2f", bd);

        long totalRubles = bd.longValue();
        long lastTwoDigits = totalRubles % 100;
        long lastDigit = totalRubles % 10;

        String currencySign;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 19) {
            currencySign = "рублей";
        } else if (lastDigit == 1) {
            currencySign = "рубль";
        } else if (lastDigit >= 2 && lastDigit <= 4) {
            currencySign = "рубля";
        } else {
            currencySign = "рублей";
        }
        return "- 1 " + rate.getCurrency().russianName + " = " + formattedValue + " " + currencySign;
    }
}
