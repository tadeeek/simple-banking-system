package com.tadeeek;

import java.util.*;
import org.hibernate.*;

import com.tadeeek.model.Account;
import com.tadeeek.util.HibernateUtil;

public class App {
    public static void main(String[] args) throws Exception {

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
                    createAccount();
                    break;
                case "2":
                    System.out.println("Enter your card number:");
                    String cardNumber = scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    String cardPIN = scanner.nextLine();
                    System.out.println("");

                    currentAccount = logIn(cardNumber, cardPIN);

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

    public static void createAccount() {
        Random random = new Random();
        String BIN = "400000";
        long leftLimit = 0L;
        long rightLimit = 999900999L;
        long accountIdentifier = leftLimit + (long) (Math.random() * (rightLimit - leftLimit));

        // generate part of cardNumber
        String cardNumberFront = BIN + String.format("%09d", accountIdentifier);

        // generate checksum
        int checksum = generateChecksum(cardNumberFront);
        // generate cardNumber

        String cardNumber = BIN + String.format("%09d", accountIdentifier) + checksum;

        // check if its in database!!!

        System.out.println("Your card has been created");
        System.out.println("Your card number:");

        System.out.println(cardNumber);
        System.out.println("Your card PIN:");
        int number = random.nextInt(1000);
        String cardPIN = String.format("%04d", number);
        System.out.println(cardPIN);
        System.out.println("");

        Account newAccount = new Account(cardNumber, cardPIN);
        saveAccount(newAccount);
    }

    public static int generateChecksum(String cardNumberFront) {

        String cardNumberFrontArr[] = cardNumberFront.split("");

        // generate control number
        // multiply odd digits by 2 and convert array to array of ints.substract 9 to
        // numbers over 9
        // add them together
        int sum = 0;
        for (int i = 0; i < cardNumberFrontArr.length; i++) {
            int currentDigit = Integer.parseInt(cardNumberFrontArr[i]);
            // because index starts at 0 so multiply even digits, not odd
            if (i % 2 == 0 && currentDigit != 0) {
                currentDigit = currentDigit * 2;
                if (currentDigit >= 10) {
                    sum = sum + (currentDigit - 9);
                } else {
                    sum = sum + currentDigit;
                }
            } else {
                sum = sum + currentDigit;
            }

        }
        int checksum = 0;

        while ((sum + checksum) % 10 != 0) {
            checksum++;
        }

        return checksum;
    }

    public static Account logIn(String cardNumber, String cardPIN) {
        List<Account> accounts = getAccounts();
        Iterator<Account> iterator = accounts.iterator();

        loop: while (iterator.hasNext()) {
            Account account = iterator.next();
            if (account.getCardNumber().equals(cardNumber)) {
                if (account.getCardPIN().equals(cardPIN)) {
                    System.out.println("You have successfully loggedin!\n");

                    return account;
                }
            }

        }
        System.out.println("Wrong card number or PIN!\n");
        return null;
    }

    public static List<Account> getAccounts() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        return session.createQuery("FROM Account").getResultList();
    }

    public static void saveAccount(Account account) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(account);
        session.getTransaction().commit();
    }
}