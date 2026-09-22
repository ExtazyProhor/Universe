package ru.prohor.universe.scarif.jwt;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Перехватывает логи logback для конкретного класса и включает уровень TRACE,
 * чтобы можно было проверять и trace-сообщения.
 */
final class LogCaptor implements AutoCloseable {
    private final Logger logger;
    private final Level previousLevel;
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    private LogCaptor(Class<?> type) {
        this.logger = (Logger) LoggerFactory.getLogger(type);
        this.previousLevel = logger.getLevel();
        logger.setLevel(Level.TRACE);
        appender.start();
        logger.addAppender(appender);
    }

    static LogCaptor of(Class<?> type) {
        return new LogCaptor(type);
    }

    List<ILoggingEvent> all() {
        return List.copyOf(appender.list);
    }

    List<ILoggingEvent> at(Level level) {
        return appender.list.stream().filter(e -> e.getLevel() == level).toList();
    }

    boolean has(Level level, String formattedMessage) {
        return appender.list.stream().anyMatch(
                e -> e.getLevel() == level && e.getFormattedMessage().equals(formattedMessage)
        );
    }

    @Override
    public void close() {
        logger.detachAppender(appender);
        appender.stop();
        logger.setLevel(previousLevel);
    }
}
