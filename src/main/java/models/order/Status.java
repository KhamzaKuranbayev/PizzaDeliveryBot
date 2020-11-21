package models.order;

public enum Status {

    NEW("Yangi Buyurtma", ""),
    RECEIVED("BUYURTMANGIZ QABUL QILINDI \uD83D\uDCE5", ""),
    PROCESS("BUYURTMANGIZ TAYYORLANMOQDA \uD83D\uDC69\u200D\uD83C\uDF73\uD83D\uDC68\u200D\uD83C\uDF73", ""),
    READY("BUYURTMANGIZ TAYYOR \uD83D\uDCE6", ""),
    ON_THE_WAY("BUYURTMANGIZ YO'LDA \uD83D\uDE97", ""),
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
