package pizza_deliveryman;

import models.deliveryman.Deliveryman;
import message.DeliverymanText;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pizza_user.UserBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliverymanBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1424821558:AAFRkP0zc5oes10EyZ4OcSOxLp2Lb5dawTM";

    public static List<Deliveryman> deliverymanList = new ArrayList<>();

    private static boolean onTimeUsername = false;
    private static boolean onTimePassword = false;

    public static Map<String, String> temp = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        String chat_id = String.valueOf(update.getMessage().getChatId());
        SendMessage sendMessage = new SendMessage().setChatId(chat_id);

        if (update.hasMessage()) {
            switch (update.getMessage().getText()) {
                case UserBot.START:
                    printLogin(sendMessage);
                    break;
                default:
                    if (onTimeUsername) {
                        if (checkUsername(update.getMessage().getText())) {
                            temp.put("username", update.getMessage().getText());
                            onTimeUsername = false;
                            printPassword(sendMessage);
                        } else {
                            printLogin(sendMessage);
                        }
                    } else if (onTimePassword) {
                        if (checkPassword(update.getMessage().getText())) {
                            temp.put("password", update.getMessage().getText());
                            onTimePassword = false;

                        } else {
                            printPassword(sendMessage);
                        }
                    }
            }
        }
    }

    private void printLogin(SendMessage sendMessage) {
        sendMessage.setText(DeliverymanText.login);
        try {
            execute(sendMessage);
            onTimeUsername = true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void printPassword(SendMessage sendMessage) {
        sendMessage.setText(DeliverymanText.password);
        try {
            execute(sendMessage);
            onTimePassword = true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean checkUsername(String username) {
        for (Deliveryman deliveryman : deliverymanList) {
            if (deliveryman != null) {
                if (deliveryman.getTelegram_username().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkPassword(String password) {
        for (Deliveryman deliveryman : deliverymanList) {
            if (deliveryman != null) {
                if (deliveryman.getPassword().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setDeliveryman() {
        deliverymanList.add(new Deliveryman("Shokir", "Jumaniyazov", "@Shokirbek01", "123456"));
        deliverymanList.add(new Deliveryman("Khamza", "Kuranbayev", "@khamzakuranbayev", "123456"));
        deliverymanList.add(new Deliveryman("Kamol", "Narkulov", "@k_narkulov", "123456"));
    }

    @Override
    public String getBotUsername() {
        return "http://t.me/uzbek_pizza_deliveryman_bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
