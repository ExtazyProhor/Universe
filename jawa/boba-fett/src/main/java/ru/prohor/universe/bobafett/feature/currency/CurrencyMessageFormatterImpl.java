package ru.prohor.universe.bobafett.feature.currency;

import org.springframework.stereotype.Service;
import ru.prohor.universe.bobafett.data.dto.Rate;
import ru.prohor.universe.jocasta.core.collections.common.Opt;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public String format(List<Rate> currentRates, Opt<List<Rate>> lastRates) {
        return "Курс валют сейчас:\n\n" +
                IntStream.range(0, currentRates.size())
                        .mapToObj(i -> formatRate(
                                currentRates.get(i),
                                lastRates.map(rates -> rates.get(i))
                        ))
                        .collect(Collectors.joining("\n"));
    }

    private String formatRate(Rate rate, Opt<Rate> lastRate) {
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
        String change = lastRate.map(last -> formatChange(rate, last, currencyAmount)).orElse("");
        return "- " + currencyAmount + " " + currencyName + " (" +
                rate.getCurrency().code + " " + rate.getCurrency().flag +
                ") = " + formattedValue + " ₽" + change;
    }

    private String formatChange(Rate currentRate, Rate lastRate, long currencyAmount) {
        BigDecimal current = BigDecimal.valueOf(1.0 / currentRate.getRateToRussianRuble())
                .multiply(BigDecimal.valueOf(currencyAmount));
        BigDecimal previous = BigDecimal.valueOf(1.0 / lastRate.getRateToRussianRuble())
                .multiply(BigDecimal.valueOf(currencyAmount));

        BigDecimal change = current.subtract(previous);
        if (change.signum() == 0) {
            return "";
        }

        String arrow = change.signum() > 0 ? " \uD83D\uDCC8 +" : " \uD83D\uDCC9 -";
        return arrow + numberFormat.format(change.abs()) + " ₽";
    }
}
