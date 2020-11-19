package impl;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface Auth {

    void printLogin(SendMessage sendMessage);

    void printPassword(SendMessage sendMessage);

    boolean checkUsername(String username);

    boolean checkPassword(String password);
}
