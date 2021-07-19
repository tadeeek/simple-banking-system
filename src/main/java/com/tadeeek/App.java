package com.tadeeek;

import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import com.tadeeek.model.Account;
import com.tadeeek.util.HibernateUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

public class App {
    public static void main(String[] args) throws Exception {

        Account currentAccount = null;

        program: while (true) {
            boolean isLoggedIn = false;
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            Scanner scanner = new Scanner(System.in);
            Scanner scanner2 = new Scanner(System.in);
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

                    // Login
                    currentAccount = logIn(cardNumber, cardPIN);
                    if (currentAccount instanceof Account) {
                        System.out.println("You have successfully logged in!\n");
                        isLoggedIn = true;
                    } else {
                        System.out.println("Wrong card number or PIN!\n");
                    }

                    loggedUser: while (isLoggedIn) {
                        System.out.println("1. Balance");
                        System.out.println("2. Add income");
                        System.out.println("3. Do transfer");
                        System.out.println("4. Close account");
                        System.out.println("5. Log out");
                        System.out.println("0. Exit");

                        input = scanner.nextLine();
                        System.out.println("");

                        switch (input) {
                            case "1":
                                System.out.println("Balance: " + currentAccount.getBalance());
                                System.out.println("");
                                break;
                            case "2":
                                System.out.println("Enter income:");
                                try {
                                    double income = scanner2.nextDouble();
                                    addIncome(currentAccount, income);
                                    System.out.println("Income was added!\n");
                                    System.out.println("");
                                } catch (NumberFormatException ex) {
                                    System.out.println("Wrong input, balance should be a number");
                                } catch (InputMismatchException ex) {
                                    System.out.println("Wrong input, balance should be a number");
                                }
                                break;
                            case "3":
                                System.out.println("Transfer money. Enter card number:");
                                String recipentCardNumber = scanner.nextLine();

                                if (StringUtils.isNumeric(recipentCardNumber) && recipentCardNumber.length() == 16) {
                                    Account recipentAccout = checkIfCardNumberExists(recipentCardNumber);

                                    if (recipentAccout instanceof Account) {

                                        try {
                                            System.out.println("Enter how much money you want to transfer:");
                                            double moneyTransfer = scanner2.nextDouble();
                                            if (moneyTransfer <= currentAccount.getBalance()) {
                                                transferMoney(recipentAccout, currentAccount, moneyTransfer);
                                                System.out.println("Money transfered\n");
                                                continue loggedUser;
                                            } else {
                                                System.out.println("Not enough money!\n");
                                                continue loggedUser;
                                            }

                                        } catch (NumberFormatException ex) {
                                            System.out.println("Wrong input, balance should be a number\n");
                                        } catch (InputMismatchException ex) {
                                            System.out.println("Wrong input, balance should be a number\n");
                                        }

                                    } else {
                                        System.out.println("Such a card does not exist.\n");
                                    }

                                } else {
                                    System.out.println(
                                            "Probably u made a mistake in the card number: Please try again!\n");
                                }

                                continue loggedUser;
                            case "4":
                                System.out.println("The account has been closed\n");
                                deleteAccount(currentAccount);
                                isLoggedIn = false;
                                currentAccount = null;
                                continue program;
                            case "5":
                                isLoggedIn = false;
                                System.out.println("You have successfully logged out!\n");
                                continue program;
                            case "0":
                                System.out.println("Bye!");
                                break program;
                            default:
                                System.out.println("Unknown command 2");
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
        boolean cardNumberExists = false;
        String cardNumber = "0";

        do {
            cardNumber = generateCardNumber();

            // Check if its in db
            List<Account> accounts = getAccounts();
            Iterator<Account> iterator = accounts.iterator();

            iteratorLoop: while (iterator.hasNext()) {
                Account account = iterator.next();
                if (account.getCardNumber().equals(cardNumber)) {

                    System.out.println("Number exists in db");
                    cardNumberExists = true;
                    break;
                } else {
                    cardNumberExists = false;
                }

            }

        } while (cardNumberExists);

        if (cardNumberExists) {

        } else {
            System.out.println("Your card has been created");
            System.out.println("Your card number:");
            System.out.println(cardNumber);

            System.out.println("Your card PIN:");
            Random random = new Random();
            int number = random.nextInt(1000);
            String cardPIN = String.format("%04d", number);
            System.out.println(cardPIN);
            System.out.println("");

            Account newAccount = new Account(cardNumber, cardPIN);
            saveAccount(newAccount);
        }

    }

    public static String generateCardNumber() {
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

        return cardNumber;
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

    public static Account checkIfCardNumberExists(String cardNumber) {
        List<Account> accounts = getAccounts();
        Iterator<Account> iterator = accounts.iterator();
        Account account;
        while (iterator.hasNext()) {
            account = iterator.next();
            if (account.getCardNumber().equals(cardNumber)) {
                return account;
            }
        }

        return null;
    }

    public static Account logIn(String cardNumber, String cardPIN) {
        Account account = checkIfCardNumberExists(cardNumber);
        if (account instanceof Account) {
            if (account.getCardPIN().equals(cardPIN)) {
                return account;
            }

            return null;
        }
        return null;
    }

    // Database connections

    public static List<Account> getAccounts() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        List<Account> accounts = session.createQuery("FROM Account").getResultList();
        session.close();
        return accounts;
    }

    public static void saveAccount(Account account) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(account);
        session.getTransaction().commit();
        session.close();
    }

    public static void addIncome(Account account, double income) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        account.addIncome(income);

        session.update(account);
        session.getTransaction().commit();
        session.close();
    }

    public static void transferMoney(Account recipentAccount, Account currentAccount, double money) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        recipentAccount.addIncome(money);
        currentAccount.withdraw(money);

        session.update(recipentAccount);
        session.update(currentAccount);

        session.getTransaction().commit();
        session.close();
    }

    public static void deleteAccount(Account currentAccount) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.delete(currentAccount);

        session.getTransaction().commit();
        session.close();

    }

}