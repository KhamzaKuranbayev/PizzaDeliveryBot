package models.order;

public enum Pizza {

    Neapolitan_Pizza(50),
    New_York_Style_Pizza(60),
    Deep_Dish_Pizza(70);

    private double price;

    Pizza(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
