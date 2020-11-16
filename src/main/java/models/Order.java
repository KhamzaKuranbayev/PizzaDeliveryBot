package models;

import java.time.LocalDateTime;

public class Order {

    private String orderId;
    private String name;
    private PizzaType type;
    private LocalDateTime date;

    private long user_chat_id;
    private long manager_chat_id;

    private Status status;

    public Order(String orderId, String name, PizzaType type, LocalDateTime date,
                 long user_chat_id, long manager_chat_id, Status status) {
        this.orderId = orderId;
        this.name = name;
        this.type = type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PizzaType getType() {
        return type;
    }

    public void setType(PizzaType type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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
