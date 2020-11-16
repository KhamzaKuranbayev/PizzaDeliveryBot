package pizza_manager;

import models.Manager;
import models.Order;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ManagerBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1498903995:AAFYP67JhTVGfL0Sizc6FQf9cpjOkZv_4g8";

    public static List<Manager> managers = new ArrayList<>();

    public static List<Order> orderList = new ArrayList<>();

    public static ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<>();


    @Override
    public void onUpdateReceived(Update update) {


    }

    public void setManagers() {
        managers.add(new Manager("Jamshid", "Isroilov", "1234"));
        managers.add(new Manager("Elyorxo'ja", "Asadullayev", "4534"));
        managers.add(new Manager("Xondamir", "Alijonov", "6789"));
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
