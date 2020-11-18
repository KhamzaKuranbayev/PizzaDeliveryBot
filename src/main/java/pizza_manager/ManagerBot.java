package pizza_manager;

import message.DeliverymanText;
import models.manager.Manager;
import models.order.Order;
import models.order.Product;
import models.order.Status;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pizza_user.UserBot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.StrictMath.toIntExact;

public class ManagerBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1498903995:AAFYP67JhTVGfL0Sizc6FQf9cpjOkZv_4g8";

    public static List<Manager> managers = new ArrayList<>();

    public static long OrderID = 1;
    public static long orderListIndex = 0;

    public static ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, Boolean> onTimeUsername = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Boolean> onTimePassword = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Boolean> onTimeMainMenu = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<String, String> temp = new ConcurrentHashMap<>();

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            String chat_id = String.valueOf(update.getMessage().getChatId());
            SendMessage sendMessage = new SendMessage().setChatId(chat_id);

            switch (update.getMessage().getText()) {
                case UserBot.START:
                    printLogin(sendMessage);
                    break;
                case "/orderSlash1":
                    sendMessage.setText("1*-qor");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
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

                            List<Long> orderHashMapIndexes = new ArrayList<>();

                            orders.forEach((index, order) -> {
                                if (order.getStatus() == Status.NEW) {
                                    orderHashMapIndexes.add(index);
                                }
                            });

                            for (Long orderHashMapIndex : orderHashMapIndexes) {
                                if (orderHashMapIndex != null) {
                                    try {
                                        execute(setInlineButtonNewOrder(chat_id, "test", orderHashMapIndex));
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            printPassword(sendMessage);
                        }
                    }
            }
        } else if (update.hasCallbackQuery()) {

            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.contains("receiveOrderBtn")) {
                long index = Long.parseLong(call_data.substring(call_data.indexOf("n") + 1));

                Order order = orders.get(index - 1);
                if (order.getStatus() != Status.RECEIVED) {
                    order.setStatus(Status.RECEIVED);
                    order.setManager_chat_id(String.valueOf(chat_id));
                    String userChatID = order.getUser_chat_id();
                    orders.replace(index, order);

                    UserBot userBot = new UserBot();
                    SendMessage sendMessageToUser = new SendMessage().setChatId(userChatID).setText(Status.RECEIVED.getUz());
                    EditMessageText sendMessageToManager = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText(String.valueOf(order.getOrderId()));
                    try {
                        userBot.execute(sendMessageToUser);
                        execute(sendMessageToManager);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    EditMessageText sendMessageToManager = new EditMessageText()
                            .setChatId(chat_id)
                            .setMessageId(toIntExact(message_id))
                            .setText("Ushbu buyurtma allaqachon qabul qilingan.");

                    try {
                        execute(sendMessageToManager);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

            }

        }

    }

    private SendMessage setInlineButtonNewOrder(String managerChatID, String text, Long orderListIndex) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Buyurtmani Ko'rish");
        inlineKeyboardButton1.setCallbackData("receiveOrderBtn" + orderListIndex);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return new SendMessage().setChatId(managerChatID).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }

    public static void clearTheFile(File file) throws IOException {
        FileWriter fwOb = new FileWriter(file, false);
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
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
        for (Manager manager : managers) {
            if (manager != null) {
                if (manager.getTelegram_username().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkPassword(String password) {
        for (Manager manager : managers) {
            if (manager != null) {
                if (manager.getPassword().equals(password)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setManagers() {
        managers.add(new Manager("216179264", "Khamza", "Kuranbayev", "@khamzakuranbayev", "123456"));
        managers.add(new Manager("1326662257", "Kamol", "Narkulov", "@k_narkulov", "123456"));
        managers.add(new Manager("805244933", "Zuhra", "Nazarova", "@z_nazarova", "123456"));
        managers.add(new Manager("479241658", "Diyor", "Xolmuradov", "@Supremegoth", "123456"));
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
