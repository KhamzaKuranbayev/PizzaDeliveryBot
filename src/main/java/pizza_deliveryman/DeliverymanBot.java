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
import java.util.concurrent.ConcurrentHashMap;

public class DeliverymanBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1424821558:AAFRkP0zc5oes10EyZ4OcSOxLp2Lb5dawTM";

    public static List<Deliveryman> deliverymanList = new ArrayList<>();

    private static ConcurrentHashMap<Long, Boolean> onTimeUsername = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Boolean> onTimePassword = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, String> temp = new ConcurrentHashMap<>();

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
                    if (onTimeUsername.get(Long.valueOf(sendMessage.getChatId()))) {
                        if (checkUsername(update.getMessage().getText())) {
                            temp.put("username", update.getMessage().getText());
                            if (onTimeUsername.get(Long.valueOf(sendMessage.getChatId())) == null) {
                                onTimeUsername.put(Long.valueOf(sendMessage.getChatId()), false);
                            } else {
                                onTimeUsername.replace(Long.valueOf(sendMessage.getChatId()), false);
                            }
                            printPassword(sendMessage);
                        } else {
                            printLogin(sendMessage);
                        }
                    } else if (onTimePassword.get(Long.valueOf(sendMessage.getChatId()))) {
                        if (checkPassword(update.getMessage().getText())) {
                            temp.put("password", update.getMessage().getText());
                            if (onTimePassword.get(Long.valueOf(sendMessage.getChatId())) == null) {
                                onTimePassword.put(Long.valueOf(sendMessage.getChatId()), false);
                            } else {
                                onTimePassword.replace(Long.valueOf(sendMessage.getChatId()), false);
                            }

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
            if (onTimeUsername.get(Long.valueOf(sendMessage.getChatId())) == null) {
                onTimeUsername.put(Long.valueOf(sendMessage.getChatId()), true);
            } else {
                onTimeUsername.replace(Long.valueOf(sendMessage.getChatId()), true);
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void printPassword(SendMessage sendMessage) {
        sendMessage.setText(DeliverymanText.password);
        try {
            execute(sendMessage);
            if (onTimePassword.get(Long.valueOf(sendMessage.getChatId())) == null) {
                onTimePassword.put(Long.valueOf(sendMessage.getChatId()), true);
            } else {
                onTimePassword.replace(Long.valueOf(sendMessage.getChatId()), true);
            }
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
