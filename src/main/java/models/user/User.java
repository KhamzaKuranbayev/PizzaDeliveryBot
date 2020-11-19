package models.user;

public class User {

    private long chat_id;
    private String username;
    private String full_name;
    private String phone_number;
    private Address address;
    private String language;
    private double balance;

    public User(long chat_id, String username, String full_name, String phone_number, Address address, String language, double balance) {
        this.chat_id = chat_id;
        this.username = username;
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.address = address;
        this.language = language;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public long getChat_id() {
        return chat_id;
    }

    public void setChat_id(long chat_id) {
        this.chat_id = chat_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

}
