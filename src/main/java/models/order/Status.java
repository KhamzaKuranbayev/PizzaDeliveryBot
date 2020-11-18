package models.order;

public enum Status {

    NEW("YANGI", ""),
    RECEIVED("QABUL QILINDI", ""),
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

    public String getUz() {
        return uz;
    }

    public String getRu() {
        return ru;
    }
}
