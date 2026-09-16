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
    private static final int DISPLAY_SCALE = 2;
    private static final int CALCULATION_SCALE = 10;

    private final NumberFormat numberFormat = setupNumberFormat();

    private NumberFormat setupNumberFormat() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru-RU"));
        numberFormat.setMinimumFractionDigits(DISPLAY_SCALE);
        numberFormat.setMaximumFractionDigits(DISPLAY_SCALE);
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
        BigDecimal rateToRussianRuble = calculateRateToRussianRuble(rate);

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
        BigDecimal current = roundForDisplay(
                calculateRateToRussianRuble(currentRate).multiply(BigDecimal.valueOf(currencyAmount))
        );
        BigDecimal previous = roundForDisplay(
                calculateRateToRussianRuble(lastRate).multiply(BigDecimal.valueOf(currencyAmount))
        );

        BigDecimal change = current.subtract(previous);
        if (change.signum() == 0) {
            return "";
        }

        String arrow = change.signum() > 0 ? " \uD83D\uDCC8 +" : " \uD83D\uDCC9 -";
        return arrow + numberFormat.format(change.abs()) + " ₽";
    }

    private BigDecimal calculateRateToRussianRuble(Rate rate) {
        return BigDecimal.ONE.divide(
                BigDecimal.valueOf(rate.getRateToRussianRuble()),
                CALCULATION_SCALE,
                RoundingMode.HALF_UP
        );
    }

    private BigDecimal roundForDisplay(BigDecimal value) {
        return value.setScale(DISPLAY_SCALE, RoundingMode.HALF_UP);
    }
}
