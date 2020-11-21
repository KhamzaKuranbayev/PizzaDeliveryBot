package pizza_manager;

import impl.Auth;
import impl.OrderOperations;
import message.DeliverymanText;
import message.ManagerText;
import message.UserText;
import models.manager.Manager;
import models.order.Order;
import models.order.Product;
import models.order.Status;
import models.user.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pizza_deliveryman.DeliverymanBot;
import pizza_user.UserBot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.StrictMath.toIntExact;

public class ManagerBot extends TelegramLongPollingBot implements Auth, OrderOperations {

    private static final String TOKEN = "1498903995:AAFyvsh5fHbcCTLI_5tYt8WT6ofDp3GcaEo";

    public static List<Manager> managers = new ArrayList<>();

    public static long OrderID = 1;
    public static long orderListIndex = 0;

    public static ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<>();

    private static ConcurrentHashMap<Long, Boolean> onTimeUsername = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Long, Boolean> onTimePassword = new ConcurrentHashMap<>();

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
                case "\uD83D\uDCE9 Mening buyurtmalarim":
                    viewReceivedOrderList(sendMessage, update);
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

                            String username = "";
                            long chatID = Long.parseLong(chat_id);
                            for (User user : UserBot.users) {
                                if (user != null) {
                                    if (user.getChat_id() == chatID) {
                                        username = user.getUsername();
                                        break;
                                    }
                                }
                            }
                            for (Long orderHashMapIndex : orderHashMapIndexes) {
                                if (orderHashMapIndex != null) {
                                    try {
                                        execute(setInlineButtonNewOrder(chat_id, "@" + username + " dan yangi buyurtma keldi", orderHashMapIndex));
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            viewMyOrdersButton(sendMessage);

                        } else {
                            printPassword(sendMessage);
                        }
                    }
            }
        } else if (update.hasCallbackQuery()) {

            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();

            Manager manager = getManager(chat_id);
            String username = null;
            if (manager != null) {
                username = manager.getTelegram_username();
            }

            if (call_data.contains("receiveOrderBtn")) {
                long index = Long.parseLong(call_data.substring(call_data.indexOf("n") + 1));

                if (index != 0) {
                    index -= 1;
                }
                Order order = orders.get(index);
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

            } else if (call_data.contains("sendToCooking")) {

                long orderId = Long.parseLong(call_data.substring(call_data.indexOf("g") + 1));

                orders.forEach((aLong, order) -> {
                    if (order != null) {
                        if (order.getOrderId() == orderId) {
                            order.setStatus(Status.PROCESS);
                            UserBot userBot = new UserBot();
                            SendMessage sendMessage = new SendMessage().setChatId(order.getUser_chat_id()).setText(Status.PROCESS.getUz());
                            try {
                                userBot.execute(sendMessage);
                                execute(sendPizzaForDelivery(sendMessage, order.getOrderId(), "Buyurtma oshpazga jo'natildi"));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else if (call_data.contains("sendToDelivery")) {
                long orderId = Long.parseLong(call_data.substring(call_data.indexOf("y") + 1));
                String finalUsername = username;
                orders.forEach((aLong, order) -> {
                    if (order != null) {
                        if (order.getOrderId() == orderId) {
                            order.setStatus(Status.READY);

                            UserBot userBot = new UserBot();
                            SendMessage sendMessage = new SendMessage().setChatId(order.getUser_chat_id()).setText(Status.READY.getUz());

                            try {
                                DeliverymanBot deliverymanBot = new DeliverymanBot();
                                deliverymanBot.execute(setInlineButtonNewOrderForDeliveryman(216179264, "Manager: " + finalUsername + " dan yangi buyurtma keldi " + orderId, orderId));
                                deliverymanBot.execute(setInlineButtonNewOrderForDeliveryman(162035045, "Manager: " + finalUsername + " dan yangi buyurtma keldi " + orderId, orderId));
                                userBot.execute(sendMessage);

                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

            }

        }

    }

    private Manager getManager(long chatId) {

        for (Manager manager : managers) {
            if (manager != null) {
                if (manager.getManagerChatId().equals(String.valueOf(chatId)))
                    return manager;
            }
        }
        return null;
    }

    private SendMessage setInlineButtonNewOrderForDeliveryman(long deliverymanChatID, String text, long orderId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Buyurtmani Qabul Qilish");
        inlineKeyboardButton1.setCallbackData("receiveOrderBtnDelivery" + orderId);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return new SendMessage().setChatId(deliverymanChatID).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }

    private SendMessage setInlineButtonNewOrder(String managerChatID, String text, Long orderListIndex) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Buyurtmani Qabul Qilish");
        inlineKeyboardButton1.setCallbackData("receiveOrderBtn" + orderListIndex);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return new SendMessage().setChatId(managerChatID).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }


    @Override
    public void printLogin(SendMessage sendMessage) {
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

    @Override
    public void printPassword(SendMessage sendMessage) {
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

    @Override
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

    @Override
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

    @Override
    public void viewMyOrdersButton(SendMessage sendMessage) {
        sendMessage.setText("Qabul qilingan buyurtmalarni ko'rish uchun bosing");

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("\uD83D\uDCE9 Mening buyurtmalarim"));
        keyboardRowList.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void viewReceivedOrderList(SendMessage sendMessage, Update update) {

        orders.forEach((index, order) -> {
            if (order != null) {

                if (order.getManager_chat_id().equals(sendMessage.getChatId())) {
                    String answer = "Buyurtma №: " + order.getOrderId() + " | Holati: " + order.getStatus();
                    answer += "\n=================================\n";
                    for (Product product : order.getProducts()) {
                        if (product != null) {
                            answer += "ID: " + product.getProductId() + ". " + product.getPizza().toString().toUpperCase() + " dan " + product.getUser_amount() + " ta\n";
                        }
                    }
                    answer += "\n=================================\n";
                    answer += "\n\n";

                    if (order.getStatus().equals(Status.RECEIVED)) {
                        /*SendMessage sendMessage1 = new SendMessage();
                        sendMessage1.setText("Buyurtma oshpazga jo'natildi ✅");*/
                        try {
                            execute(sendPizzaForCooking(sendMessage, order.getOrderId(), answer));

                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (order.getStatus().equals(Status.PROCESS)) {

                        try {
                            execute(sendPizzaForDelivery(sendMessage, order.getOrderId(), answer));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }


                }

                /*UserBot userBot1 = new UserBot();
                SendMessage sendMessage1 = new SendMessage().setChatId(order.orderId).setText(Status.PROCESS.getUz());

                try {
                    userBot1.execute(sendMessage1);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    private SendMessage sendPizzaForCooking(SendMessage sendMessage, Long orderID, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("\uD83D\uDC69\u200D\uD83C\uDF73\uD83D\uDC68\u200D\uD83C\uDF73 Pishirishga jo'natish");
        inlineKeyboardButton1.setCallbackData("sendToCooking" + orderID);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return new SendMessage().setChatId(sendMessage.getChatId()).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }

    private SendMessage sendPizzaForDelivery(SendMessage sendMessage, Long orderID, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("\uD83D\uDE97 Yetkazib beruvchiga jo'natish");
        inlineKeyboardButton1.setCallbackData("sendToDelivery" + orderID);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return new SendMessage().setChatId(sendMessage.getChatId()).setText(text).setReplyMarkup(inlineKeyboardMarkup);
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
