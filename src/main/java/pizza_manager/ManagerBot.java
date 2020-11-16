package pizza_manager;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ManagerBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1498903995:AAFYP67JhTVGfL0Sizc6FQf9cpjOkZv_4g8";




    @Override
    public void onUpdateReceived(Update update) {




    }

    @Override
    public String getBotUsername() {
        return "http://t.me/uzbek_pizza_manager_bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
