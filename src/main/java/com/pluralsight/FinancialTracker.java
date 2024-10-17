package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class FinancialTracker {

    private static ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment");
            System.out.println("L) View Ledger");
            System.out.println("X) Exit");

            switch (scanner.nextLine().trim().toUpperCase()) {
                case "D":
                    addDeposit(scanner);
                    break;
                case "P":
                    addPayment(scanner);
                    break;
                case "L":
                    ledgerMenu(scanner);
                    break;
                case "X":
                    running = false;
                    System.out.println("Exiting. Transactions are saved automatically.");
                    break;
                default:
                    System.out.println("Invalid option");
            }
        }

        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        try (InputStream inputStream = FinancialTracker.class.getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] receipts = line.split("\\|");
                transactions.add(new Transaction(
                        LocalDate.parse(receipts[0], DATE_FORMATTER),
                        LocalTime.parse(receipts[1], TIME_FORMATTER),
                        receipts[2],
                        receipts[3],
                        Double.parseDouble(receipts[4])
                ));
            }
        } catch (IOException e) {
            System.err.println("ERROR while reading the file: " + e.getMessage());
        }
    }

    private static void addDeposit(Scanner scanner) {
        LocalDate date;
        LocalTime time;
        String description;
        String vendor;
        double amount;

        try {
            System.out.print("\nEnter the date of the deposit (yyyy-MM-dd): ");
            date = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);

            System.out.print("Enter the time of the deposit (HH:mm:ss): ");
            time = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMATTER);

            System.out.print("Enter the description: ");
            description = scanner.nextLine().trim();

            System.out.print("Enter the vendor name: ");
            vendor = scanner.nextLine().trim();

            System.out.print("Enter the amount: ");
            amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("\nERROR: Cannot enter value less than or equal to 0.");
                return;
            }

            Transaction transaction = new Transaction(date, time, description, vendor, amount);
            transactions.add(transaction);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                bufferedWriter.write(transaction.toString());
                bufferedWriter.newLine();
            }

            System.out.println("\nDeposit of $" + String.format("%.2f", amount) + " successfully processed.");
        } catch (Exception e) {
            System.err.println("\nERROR occurred while entering deposit.");
        }
    }

    private static void addPayment(Scanner scanner) {
        LocalDate date;
        LocalTime time;
        String description;
        String vendor;
        double amount;

        try {
            System.out.print("\nEnter the date of the payment (yyyy-MM-dd): ");
            date = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMATTER);

            System.out.print("Enter the time of the payment (HH:mm:ss): ");
            time = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMATTER);

            System.out.print("Enter the description: ");
            description = scanner.nextLine().trim();

            System.out.print("Enter the vendor name: ");
            vendor = scanner.nextLine().trim();

            System.out.print("Enter the amount: ");
            amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("\nERROR: Cannot enter value less than or equal to 0.");
                return;
            }

            double amountNegative = amount * -1;
            Transaction transaction = new Transaction(date, time, description, vendor, amountNegative);
            transactions.add(transaction);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                bufferedWriter.write(transaction.toString());
                bufferedWriter.newLine();
            }

            System.out.println("\nPayment of $" + String.format("%.2f", amount) + " successfully processed.");
        } catch (Exception e) {
            System.err.println("\nERROR occurred while entering payment.");
        }
    }

    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A":
                    displayLedger();
                    break;
                case "D":
                    displayDeposits();
                    break;
                case "P":
                    displayPayments();
                    break;
                case "R":
                    reportsMenu(scanner);
                    break;
                case "H":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void displayLedger() {
        System.out.println("All transactions:");
        for (Transaction transaction : transactions) {
            System.out.println("Date: " + transaction.getDate() + "|" +
                    "Time: " + transaction.getTime() + "|" +
                    " Description: " + transaction.getDescription() +
                    " Vendor: " + transaction.getVendor() + "|" +
                    " Amount: " + transaction.getAmount());
        }
    }

    private static void displayDeposits() {
        System.out.println("Deposit transactions:");
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() >= 0) {
                System.out.println("Date: " + transaction.getDate() + "|" +
                        "Time: " + transaction.getTime() + "|" +
                        " Description: " + transaction.getDescription() +
                        " Vendor: " + transaction.getVendor() + "|" +
                        " Amount: " + transaction.getAmount());
            }
        }
    }

    private static void displayPayments() {
        System.out.println("Payment transactions:");
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.println("Date: " + transaction.getDate() + "|" +
                        "Time: " + transaction.getTime() + "|" +
                        " Description: " + transaction.getDescription() +
                        " Vendor: " + transaction.getVendor() + "|" +
                        " Amount: " + transaction.getAmount());
            }
        }
    }

    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();
            LocalDate today = LocalDate.now();

            switch (input) {
                case "1":
                    LocalDate firstDayOfMonth = today.withDayOfMonth(1);
                    filterTransactionsByDate(firstDayOfMonth, today);
                    break;
                case "2":
                    LocalDate firstDayOfPrevMonth = today.minusMonths(1).withDayOfMonth(1);
                    LocalDate lastDayOfPrevMonth = firstDayOfPrevMonth.withDayOfMonth(firstDayOfPrevMonth.lengthOfMonth());
                    filterTransactionsByDate(firstDayOfPrevMonth, lastDayOfPrevMonth);
                    break;
                case "3":
                    LocalDate firstDayOfYear = today.withDayOfYear(1);
                    filterTransactionsByDate(firstDayOfYear, today);
                    break;
                case "4":
                    LocalDate firstDayOfPrevYear = today.minusYears(1).withDayOfYear(1);
                    LocalDate lastDayOfPrevYear = today.minusYears(1).withMonth(12).withDayOfMonth(31);
                    filterTransactionsByDate(firstDayOfPrevYear, lastDayOfPrevYear);
                    break;
                case "5":
                    System.out.println("Please enter a Vendor name: ");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option");
                    break;
            }
        }
    }

    private static void filterTransactionsByDate(LocalDate startDate, LocalDate endDate) {
        boolean isEmpty = true;
        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate)) {
                System.out.println(transaction);
                isEmpty = false;
            }
        }

        if (isEmpty) {
            System.out.println("There are no results.");
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (vendor.equalsIgnoreCase(transaction.getVendor())) {
                System.out.println(transaction);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No transactions found for vendor: " + vendor);
        }
    }
}

