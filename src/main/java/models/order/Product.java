package models.order;

public class Product {

    private String productId;
    private double amount; // ombordagi soni
    private Pizza pizza;
    private double user_amount; // user kiritgan soni

    public Product(String productId, double amount, Pizza pizza) {
        this.productId = productId;
        this.amount = amount;
        this.pizza = pizza;
    }

    public double getUser_amount() {
        return user_amount;
    }

    public void setUser_amount(double user_amount) {
        this.user_amount = user_amount;
    }

    public boolean checkAmount() {
        return getAmount() >= getUser_amount();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public double getCost() {
        return pizza.getPrice() * amount;
    }

}
