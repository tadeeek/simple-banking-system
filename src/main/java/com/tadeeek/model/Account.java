package com.tadeeek.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardNumber;
    private String cardPIN;
    private double balance;

    public Account() {
    }

    public Account(String cardNumber, String cardPIN) {
        this.cardNumber = cardNumber;
        this.cardPIN = cardPIN;
        this.balance = 0;
    }

    public void addIncome(double money) {
        this.balance = balance + money;
    }

    public void withdraw(double money) {
        this.balance = balance - money;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardPIN() {
        return cardPIN;
    }
}
