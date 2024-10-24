package com.example.model;

public class OrderOutput {
    private String orderNumber;
    private String name;
    private int quantity;

    public OrderOutput(String orderNumber, String name, int quantity) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.quantity = quantity;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
