package ru.prohor.universe.yoda.bot

import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler

/**
 * Класс-шаблонизатор текста, с помощью которого бот взаимодействует с пользователем
 */
object Text {
    /**
     * Команды, используемые в боте
     */
    object Commands {
        const val START = "/start"
        const val HELP = "/help"
    }

    /**
     * Описания для команд (используется в команде /help
     * [HelpCommandHandler][ru.prohor.universe.yoda.bot.handlers.command.HelpCommandHandler])
     */
    object CommandDescriptions {
        fun start(): String {
            return "команда для запуска или перезапуска бота"
        }

        fun help(): String {
            return "список команд и справка"
        }
    }

    /**
     * Ответы на вызовы команд ([CommandHandler][CommandHandler])
     */
    object CommandReplies {
        fun nonCommand(text: String?): String? {
            return "Я не знаю что делать с сообщением \"$text\""
        }

        fun start(): String {
            return "Приветствую! Помогать с занятиями вам буду я"
        }

        fun unknown(): String {
            return "Неизвестная команда. Список доступных команд - /help"
        }
    }
}
