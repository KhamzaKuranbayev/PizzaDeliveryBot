package impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface OrderOperations {

    void viewReceivedOrderList(SendMessage sendMessage, Update update);

    void viewMyOrdersButton(SendMessage sendMessage);
}
