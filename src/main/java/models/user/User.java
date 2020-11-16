package models.user;

public class User {

    private long chat_id;

    private String full_name;
    private String phone_number;
    private Address address;
    private String language;

    public User(long chat_id, String full_name, Address address, String phone_number, String language) {
        this.chat_id = chat_id;
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.address = address;
        this.language = language;
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
