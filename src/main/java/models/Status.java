package models;

public enum Status {

    NEW("YANGI", ""),
    PROCESS("TAYYORLANMOQDA", ""),
    READY("TAYYOR", ""),
    ON_THE_WAY("YO'LDA", ""),
    DELIVERED("YETKAZILDI", "");

    private String uz;
    private String ru;

    Status(String uz, String ru) {
        this.uz = uz;
        this.ru = ru;
    }
}
