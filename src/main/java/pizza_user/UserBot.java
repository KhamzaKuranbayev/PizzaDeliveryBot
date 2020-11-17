package pizza_user;

import message.UserText;
import models.order.Product;
import models.user.Address;
import models.user.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1318778812:AAHTjaTbisv6aWWnLPGTEe5Lts9DCkP-Px8";

    public static final String START = "/start";

    public static String LANGUAGE;



    public static List<User> users = new ArrayList<>();

    public static User onlineUser = null;

    public static boolean onTimeUsername = false;
    public static boolean onTimeAddress = false;
    public static boolean onTimePhoneNumber = false;
    public static boolean onTimeBalance = false;

    public static List<Product> products = new ArrayList<>();

    public static Map<String, String> temp = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        String chat_id = String.valueOf(update.getMessage().getChatId());
        SendMessage sendMessage = new SendMessage().setChatId(chat_id);

        if (update.hasMessage()) {
            switch (update.getMessage().getText()) {
                case START:
                    if (checkUser(update.getMessage().getChatId())) {
                        afterRegister(sendMessage);
                    } else {
                        startText(sendMessage);
                    }
                    break;
                case UserText.UZ:
                case UserText.RU:
                    LANGUAGE = UserText.UZ;
                    sendMessage.setText(UserText.userNameText());
                    try {
                        ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
                        sendMessage.setReplyMarkup(keyboardMarkup);
                        onTimeUsername = true;
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83D\uDECD Buyurtma berish":
                        showProductList(sendMessage);
                break;



                default:
                    if (onTimeUsername) {
                        temp.put("username", update.getMessage().getText());
                        selectUserAddress(sendMessage);
                        onTimeUsername = false;
                        onTimeAddress = true;
                    } else if (onTimeAddress) {
                        setAddress(update);
                        sendMessage.setText(UserText.userBalanceText());
                        try {
                            ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
                            sendMessage.setReplyMarkup(keyboardMarkup);
                            execute(sendMessage);
                            onTimeAddress = false;
                            onTimeBalance = true;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (onTimeBalance) {
                        temp.put("balance", update.getMessage().getText());
                        sendMessage.setText(UserText.userPhoneNumberText());
                        try {
                            ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
                            sendMessage.setReplyMarkup(keyboardMarkup);
                            execute(sendMessage);
                            onTimeBalance = false;
                            onTimePhoneNumber = true;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (onTimePhoneNumber) {
                        temp.put("phone_number", update.getMessage().getText());

                        Address address = Address.valueOf(temp.get("address"));
                        users.add(new User(update.getMessage().getChatId(), temp.get("username"), temp.get("phone_number"), address, LANGUAGE, Double.parseDouble(temp.get("balance"))));
                        temp.clear();
                        afterRegister(sendMessage);
                        onTimePhoneNumber = false;
                    }

            }
        }

    }

    private void showProductList(SendMessage sendMessage) {

    }

    private void setAddress(Update update) {
        Address address = Address.valueOf(update.getMessage().getText());
        switch (address) {
            case CHILONZOR:
                temp.put("address", Address.CHILONZOR.toString());
                break;
            case YUNUSOBOD:
                temp.put("address", Address.YUNUSOBOD.toString());
                break;
            case SERGELI:
                temp.put("address", Address.SERGELI.toString());
                break;
            case MIROBOD:
                temp.put("address", Address.MIROBOD.toString());
                break;
        }
    }

    private void selectUserAddress(SendMessage sendMessage) {
        sendMessage.setText(UserText.userAddressText());
        try {
            setAddressButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void setAddressButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton keyboardButton1 = new KeyboardButton(Address.CHILONZOR.toString());
        KeyboardButton keyboardButton2 = new KeyboardButton(Address.MIROBOD.toString());
        keyboardRow1.add(keyboardButton1);
        keyboardRow1.add(keyboardButton2);

        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardButton keyboardButton3 = new KeyboardButton(Address.SERGELI.toString());
        KeyboardButton keyboardButton4 = new KeyboardButton(Address.YUNUSOBOD.toString());
        keyboardRow2.add(keyboardButton3);
        keyboardRow2.add(keyboardButton4);

        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public void afterRegister(SendMessage sendMessage) {
        sendMessage.setText(UserText.userStartAfterRegText());

        try {
            setMainMenuButtons(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void startText(SendMessage sendMessage) {
        sendMessage.setText(UserText.userStartBeforeRegText()); // ==> !
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
        KeyboardButton buttonUz = new KeyboardButton(UserText.UZ);
        KeyboardButton buttonRu = new KeyboardButton(UserText.RU);
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(buttonUz);
        keyboardRow.add(buttonRu);
        keyboardRows.add(keyboardRow);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public void setMainMenuButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRowList = new ArrayList<KeyboardRow>();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(UserText.userMainMenuOrderText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(UserText.userMainMenuInfoText()));

        keyboardRowList.add(keyboardRow1);
        keyboardRowList.add(keyboardRow2);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

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

    @Override
    public String getBotUsername() {
        return "http://t.me/uzbek_pizza_uzbot";
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
