package com.tadeeek;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        List<Account> accounts = new ArrayList<>();
        program: while (true) {

            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    createAccount(accounts);
                    break;
                case "2":
                    logIn(accounts);
                    break;
                case "0":
                    break program;

            }
        }
    }

    public static void createAccount(List<Account> accounts) {
        Random random = new Random();
        String BIN = "405671";
        String checksum = "3";
        long leftLimit = 0L;
        long rightLimit = 999900999L;
        long accountIdentifier = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        String cardNumber = BIN + String.format("%09d", accountIdentifier) + checksum;
        System.out.println(cardNumber);
        System.out.println("Your card PIN:");
        int number = random.nextInt(1000);
        String cardPIN = String.format("%04d", number);
        System.out.println(cardPIN);

        Account newAccount = new Account(cardNumber, cardPIN);
        accounts.add(newAccount);
    }

    static class Account {
        private final String cardNumber;
        private final String cardPIN;
        private long balance;

        public Account(String cardNumber, String cardPIN) {
            this.cardNumber = cardNumber;
            this.cardPIN = cardPIN;
            this.balance = 0;
        }

        public long getBalance() {
            return balance;
        }
    }

    public static void logIn(List<Account> accounts) {

        System.out.println("logged in");
    }
}
