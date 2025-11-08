package com.example.demo.entity;

public class Card {
    private String card_id;
    private String user_id;
    private String password;
    private double money;
    private int state;

    public String getCard_id() {
        return card_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPassword() {
        return password;
    }

    public double getMoney() {
        return money;
    }

    public int getSate() {
        return state;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setState(int state) {
        this.state = state;
    }
}