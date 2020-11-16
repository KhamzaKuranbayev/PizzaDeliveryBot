package models.order;

import java.time.LocalDateTime;
import java.util.List;

public class Order {

    public String orderId;
    public List<Product> products;
    private LocalDateTime date; // buyurtma jo'natilgan sana va vaqti

    private long user_chat_id;
    private long manager_chat_id;

    private Status status;

    public Order(String orderId, List<Product> products,
                 LocalDateTime date, double cost, long user_chat_id, long manager_chat_id, Status status) {
        this.orderId = orderId;
        this.products = products;
        this.date = date;
        this.user_chat_id = user_chat_id;
        this.manager_chat_id = manager_chat_id;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getCost() {
        double cost = 0;
        for (Product product : products) {
            if (product != null) {
                cost += product.getPizza().getPrice() * product.getAmount();
            }
        }

        return cost;
    }

    public long getUser_chat_id() {
        return user_chat_id;
    }

    public void setUser_chat_id(long user_chat_id) {
        this.user_chat_id = user_chat_id;
    }

    public long getManager_chat_id() {
        return manager_chat_id;
    }

    public void setManager_chat_id(long manager_chat_id) {
        this.manager_chat_id = manager_chat_id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
