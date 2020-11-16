package pizza_user;

import models.Manager;
import models.User;
import org.checkerframework.checker.units.qual.K;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pizza_manager.ManagerBot;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1318778812:AAEXkWfKS3sk6xyMEElYpFvn1VuCpsTUPKk";

    public static List<User> users = new ArrayList<>();

    public static User onlineUser = null;


    @Override
    public void onUpdateReceived(Update update) {
    }

    public void startText(SendMessage sendMessage) {
        sendMessage.setText(""); // ==> !
        try {
            setLangButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setLangButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardButton buttonUz = new KeyboardButton("\uD83C\uDDFA\uD83C\uDDFFUzbek");
        KeyboardButton buttonRu = new KeyboardButton("\uD83C\uDDF7\uD83C\uDDFAРусский");
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(buttonUz);
        keyboardRow.add(buttonRu);
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }


    // Kamol
    // 1ta text va 2ta button qo'shish kerak uzb, rus

    public boolean checkUser(long chat_id) {

        for (User user : users) {
            if (user != null) {
                if (user.getChat_id() == chat_id) {
                    onlineUser = user;
                    return true;
                }
            }
        }
        return false;

    }
    // Dior
    // yangi userni users dan check qiladigan method yozish, boolean

    @Override
    public String getBotUsername() {
        return "http://t.me/uzbek_pizza_uzbot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
