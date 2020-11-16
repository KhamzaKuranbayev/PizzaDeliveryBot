package pizza_user;

import models.Manager;
import models.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
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
        public String getBotUsername () {
            return "http://t.me/uzbek_pizza_uzbot";
        }

        @Override
        public String getBotToken () {
            return TOKEN;
        }
    }
