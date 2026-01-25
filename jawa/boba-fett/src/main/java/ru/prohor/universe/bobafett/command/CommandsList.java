package ru.prohor.universe.bobafett.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommandsList implements CommandHandler {
    private final String commandsListMessage;

    public CommandsList(List<CommandHandler> commandHandlers) {
        this.commandsListMessage = commandHandlers.stream()
                .filter(handler -> handler.description().isPresent())
                .map(handler -> handler.command() + " - " + handler.description().get() + "\n")
                .collect(Collectors.joining())
                .trim();
    }

    @Override
    public String command() {
        return Commands.COMMANDS;
    }

    @Override
    public void handle(Message message, FeedbackExecutor feedbackExecutor) {
        if (!commandsListMessage.isEmpty())
            feedbackExecutor.sendMessage(message.getChatId(), commandsListMessage);
    }
}
