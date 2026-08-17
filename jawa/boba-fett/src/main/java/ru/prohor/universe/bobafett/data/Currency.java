package ru.prohor.universe.bobafett.data;

import java.util.Arrays;
import java.util.List;

public enum Currency {
    AED("UAE Dirham", "Дирхам ОАЭ", "AED"),
    AMD("Armenian Dram", "Армянский драм", "AMD"),
    AUD("Australian Dollar", "Австралийский доллар", "AUD"),
    AZN("Azerbaijani Manat", "Азербайджанский манат", "AZN"),
    BYN("Belarusian Ruble", "Белорусский рубль", "BYN"),
    CAD("Canadian Dollar", "Канадский доллар", "CAD"),
    CHF("Swiss Franc", "Швейцарский франк", "CHF"),
    CNY("Chinese Yuan", "Китайский юань", "CNY"),
    CZK("Czech Koruna", "Чешская крона", "CZK"),
    EUR("Euro", "Евро", "EUR"),
    GBP("British Pound Sterling", "Британский фунт стерлингов", "GBP"),
    GEL("Georgian Lari", "Грузинский лари", "GEL"),
    HKD("Hong Kong Dollar", "Гонконгский доллар", "HKD"),
    ILS("New Israeli Shekel", "Новый израильский шекель", "ILS"),
    INR("Indian Rupee", "Индийская рупия", "INR"),
    JPY("Japanese Yen", "Японская иена", "JPY"),
    KGS("Kyrgyzstani Som", "Киргизский сом", "KGS"),
    KRW("South Korean Won", "Южнокорейская вона", "KRW"),
    KZT("Kazakhstani Tenge", "Казахстанский тенге", "KZT"),
    MXN("Mexican Peso", "Мексиканское песо", "MXN"),
    NZD("New Zealand Dollar", "Новозеландский доллар", "NZD"),
    PLN("Polish Zloty", "Польский злотый", "PLN"),
    RUB("Russian Ruble", "Российский рубль", "RUB"),
    SAR("Saudi Riyal", "Саудовский риял", "SAR"),
    SGD("Singapore Dollar", "Сингапурский доллар", "SGD"),
    THB("Thai Baht", "Тайский бат", "THB"),
    TRY("Turkish Lira", "Турецкая лира", "TRY"),
    USD("United States Dollar", "Доллар США", "USD"),
    UZS("Uzbekistan Som", "Узбекский сум", "UZS");

    public final String name;
    public final String russianName;
    public final String code;

    Currency(String name, String russianName, String code) {
        this.name = name;
        this.russianName = russianName;
        this.code = code;
    }

    public static final List<Currency> CURRENCIES_FOR_RATES = Arrays.stream(values())
            .filter(currency -> currency != RUB)
            .toList();
}
