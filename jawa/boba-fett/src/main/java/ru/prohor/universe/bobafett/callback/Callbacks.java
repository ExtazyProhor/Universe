package ru.prohor.universe.bobafett.callback;

public interface Callbacks {
    String BLANK = "blank";

    /**
     * Holidays (<code>h/**</code>)
     * <p>
     * i - init
     * c - custom holidays
     * g - get holidays
     * s - subscription
     * d - date
     * m - import
     */
    String SUBSCRIBE_HOLIDAYS_INIT = "h/is";
    String SUBSCRIBE_HOLIDAYS = "h/s";

    String IMPORT_HOLIDAYS_INIT = "h/im";

    String CHOOSE_CUSTOM_HOLIDAY_DATE = "h/cd";
    String CUSTOM_HOLIDAY = "h/c";
    String CUSTOM_HOLIDAY_INIT = "h/ic";

    String GET_HOLIDAYS = "h/g";
    String GET_HOLIDAYS_FOR_CUSTOM_DATE = "h/gd";
    String GET_HOLIDAYS_INIT = "h/ig";

    /**
     *  Currency (<code>c/**</code>)
     * <p>
     * i - init
     * g - get currency
     * s - subscription
     * c - change selected currencies
     * h - disable hint about changing selected currency
     */
    String SUBSCRIBE_CURRENCY_INIT = "c/is";
    String SUBSCRIBE_CURRENCY = "c/s";
    String GET_CURRENCY = "c/g";
    String CHANGE_SELECTED_CURRENCIES = "c/c";
    String DISABLE_HINT_ABOUT_CHANGING_SELECTED_CURRENCY = "c/h";
}
