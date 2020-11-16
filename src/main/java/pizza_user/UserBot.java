package pizza_user;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UserBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1318778812:AAEXkWfKS3sk6xyMEElYpFvn1VuCpsTUPKk";


    @Override
    public void onUpdateReceived(Update update) {



    }

    @Override
    public String getBotUsername() {
        return "http://t.me/uzbek_pizza_uzbot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
