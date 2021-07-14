package com.tadeeek;

import java.util.*;
import org.hibernate.*;

import com.tadeeek.model.Account;
import com.tadeeek.util.HibernateUtil;

public class App {
    public static void main(String[] args) throws Exception {

        // Connection test

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Account account = new Account("1231231231231231", "3213");
        session.save(account);
        session.getTransaction().commit();

        // Connection test end

        List<Account> accounts = new ArrayList<>();
        Account currentAccount = null;

        program: while (true) {
            boolean isLoggedIn = false;
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            System.out.println("");

            switch (input) {
                case "1":
                    createAccount(accounts);
                    break;
                case "2":
                    System.out.println("Enter your card number:");
                    String cardNumber = scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    String cardPIN = scanner.nextLine();
                    System.out.println("");

                    currentAccount = logIn(accounts, cardNumber, cardPIN);

                    if (currentAccount instanceof Account) {
                        isLoggedIn = true;
                    }

                    while (isLoggedIn) {
                        System.out.println("1. Balance");
                        System.out.println("2. Log out");
                        System.out.println("0. Exit");

                        input = scanner.nextLine();
                        System.out.println("");

                        switch (input) {
                            case "1":
                                System.out.println("Balance: " + currentAccount.getBalance());
                                System.out.println("");
                                break;
                            case "2":
                                isLoggedIn = false;
                                System.out.println("You have successfully logged out!\n");
                                continue program;
                            case "3":
                                System.out.println("Bye!");
                                break program;
                        }
                    }
                    continue program;

                case "0":
                    System.out.println("Bye!");
                    break program;

                default:
                    System.out.println("Unknown command");

            }
        }
    }

    public static void createAccount(List<Account> accounts) {
        Random random = new Random();
        String BIN = "400000";
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
        System.out.println("");

        Account newAccount = new Account(cardNumber, cardPIN);
        accounts.add(newAccount);
    }

    public static Account logIn(List<Account> accounts, String cardNumber, String cardPIN) {
        Iterator<Account> iterator = accounts.iterator();

        loop: while (iterator.hasNext()) {
            Account account = iterator.next();
            if (account.getCardNumber().equals(cardNumber)) {
                if (account.getCardPIN().equals(cardPIN)) {
                    System.out.println("You have successfully logged in!\n");

                    return account;
                }
            }

        }
        System.out.println("Wrong card number or PIN!\n");
        return null;
    }
}