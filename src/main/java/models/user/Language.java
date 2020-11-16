package models.user;

public enum Language {

    UZ("\uD83C\uDDFA\uD83C\uDDFFUzbek"),
    RU("\uD83C\uDDF7\uD83C\uDDFAРусский"),
    ENG("");

    private String description;

    Language(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
