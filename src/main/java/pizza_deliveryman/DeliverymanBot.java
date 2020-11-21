package pizza_deliveryman;

import impl.Auth;
import impl.OrderOperations;
import models.deliveryman.Deliveryman;
import message.DeliverymanText;
import models.manager.Manager;
import models.order.Product;
import models.order.Status;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pizza_manager.ManagerBot;
import pizza_user.UserBot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.StrictMath.toIntExact;


public class DeliverymanBot extends TelegramLongPollingBot implements Auth, OrderOperations {

    private static final String TOKEN = "1424821558:AAEIn4VewsMQNKSC8GPHYzTzx3XEYZRbDIE";

    public static List<Deliveryman> deliverymanList = new ArrayList<>();

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
                            viewMyOrdersButton(sendMessage);
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
        } else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (call_data.contains("receiveOrderBtnDelivery")) {
                long orderId = Long.parseLong(call_data.substring(call_data.indexOf("y") + 1));

                ManagerBot.orders.forEach((aLong, order) -> {
                    if (order != null) {
                        if (order.getOrderId() == orderId) {
                            order.setStatus(Status.ON_THE_WAY);
                            UserBot userBot = new UserBot();
                            SendMessage sendMessageToUser = new SendMessage().setChatId(order.getUser_chat_id()).setText(Status.ON_THE_WAY.getUz());

                            EditMessageText sendMessageToDeliveryman = new EditMessageText()
                                    .setChatId(chatId)
                                    .setMessageId(toIntExact(message_id))
                                    .setText(String.valueOf(order.getOrderId()));


                            try {
                                userBot.execute(sendMessageToUser);
                                execute(sendMessageToDeliveryman);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void viewReceivedOrderList(SendMessage sendMessage, Update update) {

        ManagerBot.orders.forEach((index, order) -> {
            if (order != null) {

                if (order.getManager_chat_id().equals(sendMessage.getChatId())) {
                    String answer = "Buyurtma №: " + order.getOrderId() + " | Holati: " + order.getStatus();
                    answer += "\n======================================\n";
                    for (Product product : order.getProducts()) {
                        if (product != null) {
                            answer += "ID: " + product.getProductId() + ". " + product.getPizza().toString().toUpperCase() + " dan " + product.getUser_amount() + " ta\n";
                        }
                    }
                    answer += "\n======================================\n";
                    answer += "\n\n";

                    if (order.getStatus().equals(Status.ON_THE_WAY)) {
                        try {
                            execute(deliveredPizza(sendMessage, order.getOrderId(), answer));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }


                }

            }
        });
    }

    private SendMessage deliveredPizza(SendMessage sendMessage, Long orderID, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("\uD83D\uDCE3 Pizza topshirildi");
        inlineKeyboardButton1.setCallbackData("deliveredPizza" + orderID);

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        keyboardButtonsRow.add(inlineKeyboardButton1);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return new SendMessage().setChatId(sendMessage.getChatId()).setText(text).setReplyMarkup(inlineKeyboardMarkup);
    }

    @Override
    public void viewMyOrdersButton(SendMessage sendMessage) {
        sendMessage.setText("Qabul qilingan buyurtmalarni ko'rish \uD83D\uDC47");

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

    public static void setDeliveryman() {
        deliverymanList.add(new Deliveryman("162035045", "Shokir", "Jumaniyazov", "@Shokirbek01", "123456"));
        deliverymanList.add(new Deliveryman("216179264", "Khamza", "Kuranbayev", "@khamzakuranbayev", "123456"));
        deliverymanList.add(new Deliveryman("1326662257", "Kamol", "Narkulov", "@k_narkulov", "123456"));
    }

    @Override
    public String getBotUsername() {
        return "http://t.me/uzbek_pizza_deliveryman_bot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
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
        for (Deliveryman deliveryman : deliverymanList) {
            if (deliveryman != null) {
                if (deliveryman.getTelegram_username().equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
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
}
