package ru.prohor.universe.jocasta.tgbots.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardUtils {
    private InlineKeyboardUtils() {}

    // TODO make it by Tuple2 or Maps
    public static InlineKeyboardMarkup getInlineKeyboard(
            List<List<String>> buttonText,
            List<List<String>> buttonCallback
    ) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        for (int i = 0; i < buttonText.size(); ++i) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < buttonText.get(i).size(); ++j) {
                InlineKeyboardButton button = InlineKeyboardButton.builder()
                        .text(buttonText.get(i).get(j))
                        .callbackData(buttonCallback.get(i).get(j))
                        .build();
                row.add(button);
            }
            keyboard.add(new InlineKeyboardRow(row));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }

    // TODO make it by Tuple2 or Maps
    public static InlineKeyboardMarkup getColumnInlineKeyboard(
            List<String> buttonText,
            List<String> buttonCallback
    ) {
        List<InlineKeyboardRow> keyboard = new ArrayList<>();

        for (int i = 0; i < buttonText.size(); ++i) {
            InlineKeyboardButton button = InlineKeyboardButton.builder()
                    .text(buttonText.get(i))
                    .callbackData(buttonCallback.get(i))
                    .build();
            keyboard.add(new InlineKeyboardRow(List.of(button)));
        }

        return InlineKeyboardMarkup.builder()
                .keyboard(keyboard)
                .build();
    }
}
