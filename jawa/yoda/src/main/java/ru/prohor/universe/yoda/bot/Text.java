package ru.prohor.universe.yoda.bot;

import org.slf4j.helpers.MessageFormatter;

/**
 * Класс-шаблонизатор текста, с помощью которого бот взаимодействует с пользователем
 */
public class Text {
    private static String format(String pattern, Object... args) {
        return MessageFormatter.arrayFormat(pattern, args).getMessage();
    }

    /**
     * Описания для команд (используется в команде /help
     * {@link ru.prohor.universe.yoda.bot.handlers.command.HelpCommandHandler HelpCommandHandler})
     */
    public static class CommandDescriptions {
        public static String start() {
            return "команда для запуска или перезапуска бота";
        }

        public static String help() {
            return "список команд и справка";
        }
    }

    /**
     * Ответы на вызовы команд ({@link ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler CommandHandler})
     */
    public static class CommandReplies {
        public static String nonCommand(String text) {
            return format("Я не знаю что делать с сообщением \"{}\"", text);
        }

        public static String start() {
            return "Приветствую! Помогать с занятиями вам буду я";
        }

        public static String unknown() {
            return "Неизвестная команда. Список доступных команд - /help";
        }
    }
}
