package ru.prohor.universe.yoda.bot.handlers.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.prohor.universe.jocasta.tgbots.api.FeedbackExecutor;
import ru.prohor.universe.jocasta.tgbots.api.comand.CommandHandler;
import ru.prohor.universe.yoda.bot.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HelpCommandHandler implements CommandHandler {
    private final List<CommandHandler> commandHandlers;

    public HelpCommandHandler(List<CommandHandler> commandHandlers) {
        List<CommandHandler> mutable = new ArrayList<>(commandHandlers);
        mutable.add(this);
        this.commandHandlers = mutable;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public String description() {
        return Text.CommandDescriptions.help();
    }

    @Override
    public boolean handle(Message message, FeedbackExecutor feedbackExecutor) {
        feedbackExecutor.sendMessage(
                message.getChatId(),
                commandHandlers.stream()
                        .map(cmd -> cmd.command() + " - " + cmd.description())
                        .collect(Collectors.joining("\n")) // TODO
        );
        return false;
    }
}
