package models.deliveryman;

public class Deliveryman {

    private String chatID;
    private String firstname;
    private String lastname;
    private String telegram_username;
    private String password;

    public Deliveryman(String chatID, String firstname, String lastname, String telegram_username, String password) {
        this.chatID = chatID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.telegram_username = telegram_username;
        this.password = password;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTelegram_username() {
        return telegram_username;
    }

    public void setTelegram_username(String telegram_username) {
        this.telegram_username = telegram_username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
