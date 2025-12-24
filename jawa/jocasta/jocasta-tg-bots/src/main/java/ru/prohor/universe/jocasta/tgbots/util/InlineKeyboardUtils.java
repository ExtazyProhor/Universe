package ru.prohor.universe.jocasta.tgbots.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardUtils {
    private InlineKeyboardUtils() {}

    // TODO make it by Tuple2 or Maps
    public static InlineKeyboardMarkup getInlineKeyboard(
            List<List<String>> buttonText,
            List<List<String>> buttonCallback
    ) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < buttonText.size(); ++i) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int j = 0; j < buttonText.get(i).size(); ++j) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(buttonText.get(i).get(j));
                button.setCallbackData(buttonCallback.get(i).get(j));
                row.add(button);
            }
            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);
        return markup;
    }

    // TODO make it by Tuple2 or Maps
    public static InlineKeyboardMarkup getColumnInlineKeyboard(
            List<String> buttonText,
            List<String> buttonCallback
    ) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < buttonText.size(); ++i) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonText.get(i));
            button.setCallbackData(buttonCallback.get(i));
            row.add(button);
            keyboard.add(row);
        }

        markup.setKeyboard(keyboard);
        return markup;
    }
}
