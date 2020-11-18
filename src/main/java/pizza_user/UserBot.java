package pizza_user;

import message.UserText;
import models.order.Order;
import models.order.Pizza;
import models.order.Product;
import models.order.Status;
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
import pizza_manager.ManagerBot;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1318778812:AAHz-TE8v-KWsq74WaS8NdN04_qXI0uATlk";

    public static final String START = "/start";

    public static String LANGUAGE;

    public static String PRODUCT_NAME;

    public static List<User> users = new ArrayList<>();

    public static ConcurrentHashMap<Long, Boolean> onTimeUsername = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Boolean> onTimeAddress = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Boolean> onTimePhoneNumber = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Boolean> onTimeBalance = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, Boolean> onTimeCountOrderProduct = new ConcurrentHashMap<>();

    public static List<Product> products = new ArrayList<>();

    public static ConcurrentHashMap<String, String> tempUserRegData = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, String> tempProductData = new ConcurrentHashMap<>();

    public static ManagerBot managerBot = new ManagerBot();

    @Override
    public void onUpdateReceived(Update update) {
        String chat_id = String.valueOf(update.getMessage().getChatId());
        SendMessage sendMessage = new SendMessage().setChatId(chat_id);

        if (update.hasMessage()) {

            PRODUCT_NAME = getProduct(update);

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
                        onTimeUsername.put(update.getMessage().getChatId(), true);
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83C\uDF55 Pizza tanlash":
                    showProductList(sendMessage);
                    break;
                case "\uD83D\uDECD Buyurtma savatchasi":

                    showOrderCart(sendMessage);

                    try {
                        orderCartButtons(sendMessage);
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83D\uDCDD Buyurtma berish":

                    sendOrder(sendMessage);

                    SendMessage sendMessage1 = new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText("New Order from " + update.getMessage().getChat().getUserName());
                    try {
                        managerBot.execute(sendMessage1);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    break;
                case "\uD83E\uDDFA Xarid savatchasiga qo'shish":

                    writeProductToCartFile(update);
                    sendMessage.setText("✅ Pizza buyurtma savatchasiga joylandi!\n\n");
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    showProductList(sendMessage);
                    break;
                case "◀️ Orqaga qaytish":
                    sendMessage.setText("Bosh Menyu qaytdi♻️");
                    try {
                        setMainMenuButtons(sendMessage);
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                default:

                    if (onTimeUsername.get(update.getMessage().getChatId())) {
                        tempUserRegData.put("username", update.getMessage().getText());
                        selectUserAddress(sendMessage);
                        onTimeUsername.replace(update.getMessage().getChatId(), false);
                        onTimeAddress.put(update.getMessage().getChatId(), true);
                    } else if (onTimeAddress.get(update.getMessage().getChatId())) {
                        setAddress(update);
                        sendMessage.setText(UserText.userBalanceText());
                        try {
                            ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
                            sendMessage.setReplyMarkup(keyboardMarkup);
                            execute(sendMessage);
                            onTimeAddress.replace(update.getMessage().getChatId(), false);
                            onTimeBalance.put(update.getMessage().getChatId(), true);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (onTimeBalance.get(update.getMessage().getChatId())) {
                        tempUserRegData.put("balance", update.getMessage().getText());
                        sendMessage.setText(UserText.userPhoneNumberText());
                        try {
                            ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
                            sendMessage.setReplyMarkup(keyboardMarkup);
                            execute(sendMessage);
                            onTimeBalance.replace(update.getMessage().getChatId(), false);
                            onTimePhoneNumber.put(update.getMessage().getChatId(), true);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else if (onTimePhoneNumber.get(update.getMessage().getChatId())) {
                        tempUserRegData.put("phone_number", update.getMessage().getText());

                        Address address = Address.valueOf(tempUserRegData.get("address"));
                        users.add(new User(update.getMessage().getChatId(), tempUserRegData.get("username"), tempUserRegData.get("phone_number"), address, LANGUAGE, Double.parseDouble(tempUserRegData.get("balance"))));
                        tempUserRegData.clear();
                        afterRegister(sendMessage);
                        onTimePhoneNumber.replace(update.getMessage().getChatId(), false);
                    } else if (!"".equals(PRODUCT_NAME)) {
                        showProduct(sendMessage);
                    } else if (onTimeCountOrderProduct.get(update.getMessage().getChatId())) {

                        tempProductData.put("product_count", update.getMessage().getText());

                        sendMessage.setText("Buyurtmani savatchaga qo'shing");
                        try {
                            setToCardButtons(sendMessage);
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                        onTimeCountOrderProduct.replace(update.getMessage().getChatId(), false);
                    }

            }
        }

    }

    private void sendOrder(SendMessage sendMessage) {

        List<Product> productList = getProductsFromFile(sendMessage.getChatId());

        ManagerBot.orders.put(ManagerBot.orderListIndex++, new Order(ManagerBot.OrderID++, productList, LocalDateTime.now(), sendMessage.getChatId(), "", Status.NEW));
    }


    private List<Product> getProductsFromFile(String chatId) {

        List<Product> productList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/" + chatId + ".txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(" ");
                String productId = data[1];
                String productName = data[3];
                double productCount = Double.parseDouble(data[0]);
                productList.add(new Product(productId, productCount, Pizza.valueOf(productName)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return productList;
    }

    private void writeProductToCartFile(Update update) {
        File file = new File("src/main/resources/" + update.getMessage().getChatId().toString() + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));

            for (Map.Entry<String, String> entry : tempProductData.entrySet()) {
                bufferedWriter.write(entry.getValue() + " ");
            }
            bufferedWriter.newLine();
            bufferedWriter.flush();
            tempProductData.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showOrderCart(SendMessage sendMessage) {

        File file = new File("src/main/resources/" + sendMessage.getChatId() + ".txt");

        if (file.exists()) {
            try {
                StringBuilder answer = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;
                double sum = 0;
                int index = 1;
                while ((line = bufferedReader.readLine()) != null) {
                    answer.append("----------------------  ").append(index).append("  ----------------------\n\n");

                    String[] data = line.split(" ");
                    String productName = "NOMI: " + data[3];
                    String productCount = "BUYURTMA SONI: " + data[0] + " ta";
                    sum += Double.parseDouble(data[0]) * Double.parseDouble(data[2]);
                    String productPrice = "PULI: " + sum + " uzs";

                    answer.append(productName);
                    answer.append("\n");
                    answer.append(productPrice);
                    answer.append("\n");
                    answer.append(productCount);
                    answer.append("\n\n");

                    index++;
                }
                answer.append("==============================\n");
                answer.append("UMUMIY SUMMA: ").append(sum);
                sendMessage.setText(String.valueOf(answer));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private void showProduct(SendMessage sendMessage) {
        String answer = "";
        for (Product product : products) {
            if (product != null) {
                if (product.getPizza().toString().equals(PRODUCT_NAME)) {
                    answer = "Nomi: " + PRODUCT_NAME + "\n";
                    answer += "Tarkibi: " + product.getPizza().getIng() + "\n";
                    answer += "Narxi: " + product.getPizza().getPrice() + " UZS\n\n";
                    answer += "Nechta buyurtma qilasiz? (MAXIMUM: " + product.getAmount() + " ta)";
                    tempProductData.put("product_id", product.getProductId());
                    tempProductData.put("product_name", product.getPizza().toString());
                    tempProductData.put("product_price", String.valueOf(product.getPizza().getPrice()));
                    break;
                }
            }
        }
        sendMessage.setText(answer);
        try {
            execute(sendMessage);
            onTimeCountOrderProduct.put(Long.valueOf(sendMessage.getChatId()), true);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void addProductsToList() {
        products.add(new Product("1", 50, Pizza.Capricciosa_Pizza));
        products.add(new Product("2", 50, Pizza.Hawalian_Pizza));
        products.add(new Product("3", 50, Pizza.Margherita_Pizza));
        products.add(new Product("4", 50, Pizza.Marinara_Pizza));
        products.add(new Product("5", 50, Pizza.Mexican_Pizza));
    }

    private String getProduct(Update update) {
        for (Product product : products) {
            if (product != null) {
                if (update.getMessage().getText().contains(product.getPizza().toString())) {
                    return product.getPizza().toString();
                }
            }
        }
        return "";
    }

    private void showProductList(SendMessage sendMessage) {
        StringBuilder result = new StringBuilder();
        for (Product product : products) {
            result.append("/").append(product.getPizza()).append(":\n").append(product.getPizza().getPrice()).append(" UZS\n\n");
        }
        sendMessage.setText(result.toString());
        try {
            ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
            sendMessage.setReplyMarkup(keyboardMarkup);
            setBackToMenuButton(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void setBackToMenuButton(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton(UserText.backText());

        keyboardRow.add(keyboardButton);
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    private void setAddress(Update update) {
        Address address = Address.valueOf(update.getMessage().getText());
        switch (address) {
            case CHILONZOR:
                tempUserRegData.put("address", Address.CHILONZOR.toString());
                break;
            case YUNUSOBOD:
                tempUserRegData.put("address", Address.YUNUSOBOD.toString());
                break;
            case SERGELI:
                tempUserRegData.put("address", Address.SERGELI.toString());
                break;
            case MIROBOD:
                tempUserRegData.put("address", Address.MIROBOD.toString());
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
        keyboardRow1.add(new KeyboardButton(UserText.userMainMenuChoosePizzaText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(UserText.userMainMenuCardText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(UserText.userMainMenuInfoText()));

        keyboardRowList.add(keyboardRow1);
        keyboardRowList.add(keyboardRow2);
        keyboardRowList.add(keyboardRow3);

        replyKeyboardMarkup.setKeyboard(keyboardRowList);

        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public void orderCartButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton(UserText.sendOrderText()));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton(UserText.cancelOrderText()));
        KeyboardRow keyboardRow3 = new KeyboardRow();
        keyboardRow3.add(new KeyboardButton(UserText.backText()));
        keyboardRowList.add(keyboardRow1);
        keyboardRowList.add(keyboardRow2);
        keyboardRowList.add(keyboardRow3);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    private void setToCardButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton keyboardButton1 = new KeyboardButton(UserText.addToCartText());
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardButton keyboardButton2 = new KeyboardButton(UserText.backText());

        keyboardRow1.add(keyboardButton1);
        keyboardRows.add(keyboardRow1);
        keyboardRow2.add(keyboardButton2);
        keyboardRows.add(keyboardRow2);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
    }

    public boolean checkUser(long chat_id) {

        for (User user : users) {
            if (user != null) {
                if (user.getChat_id() == chat_id) {
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

