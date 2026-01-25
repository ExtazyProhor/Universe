package ru.prohor.universe.jocasta.tgbots.api.status;

/**
 * Enumeration class for determining the further flow of
 * {@link org.telegram.telegrambots.meta.api.objects.Update Update} processing after exiting the
 * {@link StatusHandler StatusHandler} or {@link ValuedStatusHandler ValuedStatusHandler}
 */
public enum StatusFlow {
    /**
     * Continue processing the {@link org.telegram.telegrambots.meta.api.objects.Update Update}
     * (checking for the presence of commands and callbacks)
     */
    CONTINUE,

    /**
     * Stop processing the {@link org.telegram.telegrambots.meta.api.objects.Update Update}
     */
    EXIT
}
