package com.tadeeek;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        program: while (true) {

            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    break;
                case "2":
                    break;
                case "0":
                    break program;

            }
        }
    }
}
