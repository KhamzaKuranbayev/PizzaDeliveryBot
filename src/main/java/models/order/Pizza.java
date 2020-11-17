package models.order;

public enum Pizza {

    Margherita_Pizza(45000, "tomato,vegetables"),
    Pepperoni_Pizza(50000, "meat,cheese"),
    Capricciosa_Pizza(4200, "tomato,mushroom"),
    Mushroom_Pizza(48000, "tomato,mushroom,vegetables"),
    Marinara_Pizza(54000, "fish,vegetables"),
    Seafood_Pizza(78000, "shrimp,vegetables"),
    Hawalian_Pizza(49000, "meat"),
    Vegeteriana_Pizza(55000, "vegetables"),
    Mexican_Pizza(66000, "meat,pepper");

    private double price;
    private String ing;

    Pizza(double price, String ing) {
        this.price = price;
        this.ing = ing;
    }

    public String getIng() {
        return ing;
    }

    public void setIng(String ing) {
        this.ing = ing;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
