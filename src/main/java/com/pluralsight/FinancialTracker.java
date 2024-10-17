package com.pluralsight;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class FinancialTracker {

    private static ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private static final String FILE_NAME = "transactions.csv";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String TIME_FORMAT = "HH:mm:ss";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static final LocalDate NOW = LocalDate.now();

    public static void main(String[] args) {
        loadTransactions(FILE_NAME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\nWelcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
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
                    break;
                default:
                    System.out.println("\nInvalid option");
                    break;
            }
        }
        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        // Load transactions from the CSV file
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
            // Optionally create the file if it doesn't exist
        }
    }
    private static void addDeposit(Scanner scanner) {
        try {
            System.out.println("Enter deposit details (yyyy-MM-dd HH:mm:ss | description | vendor | amount):");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\|");

            if (parts.length != 4) {
                System.out.println("\nERROR: Invalid input format. Please enter 4 fields separated by '|'.");
                return;
            }

            LocalDate date = LocalDate.parse(parts[0].trim(), DATE_FORMATTER);
            LocalTime time = LocalTime.parse(parts[1].trim(), TIME_FORMATTER);
            String description = parts[2].trim();
            String vendor = parts[3].trim();
            double amount = Double.parseDouble(parts[4].trim());

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
            System.err.println("\nERROR occurred while entering deposit: " + e.getMessage());
        }
    }
    private static void addPayment(Scanner scanner) {
        try {
            System.out.println("Enter payment details (yyyy-MM-dd HH:mm:ss | description | vendor | amount):");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\|");

            if (parts.length != 4) {
                System.out.println("\nERROR: Invalid input format. Please enter 4 fields separated by '|'.");
                return;
            }

            LocalDate date = LocalDate.parse(parts[0].trim(), DATE_FORMATTER);
            LocalTime time = LocalTime.parse(parts[1].trim(), TIME_FORMATTER);
            String description = parts[2].trim();
            String vendor = parts[3].trim();
            double amount = Double.parseDouble(parts[4].trim());

            if (amount <= 0) {
                System.out.println("\nERROR: Cannot enter value less than or equal to 0.");
                return;
            }
            // Make the amount negative because it's a payment.
            double amountNegative = amount * -1;

            Transaction transaction = new Transaction(date, time, description, vendor, amountNegative);
            transactions.add(transaction);

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
                bufferedWriter.write(transaction.toString());
                bufferedWriter.newLine();
            }

            System.out.println("\nPayment of $" + String.format("%.2f", amount) + " successfully processed.");

        } catch (Exception e) {
            System.err.println("\nERROR occurred while entering payment: " + e.getMessage());
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
        System.out.println("Date\t\tTime\t\tDescription\t\tVendor\t\tAmount");
        for (Transaction transaction : transactions) {
            System.out.printf("%s\t%s\t%s\t%s\t%.2f%n",
                    transaction.getDate(),
                    transaction.getTime(),
                    transaction.getDescription(),
                    transaction.getVendor(),
                    transaction.getAmount());
        }
    }
    private static void displayDeposits() {
        System.out.println("Date\t\tTime\t\tDescription\t\tVendor\t\tAmount");
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() > 0) {
                System.out.printf("%s\t%s\t%s\t%s\t%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }
    private static void displayPayments() {
        System.out.println("Date\t\tTime\t\tDescription\t\tVendor\t\tAmount");
        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.printf("%s\t%s\t%s\t%s\t%.2f%n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
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

            switch (input) {
                case "1":
                    // Generate report for current month
                    break;
                case "2":
                    // Generate report for previous month
                    break;
                case "3":
                    // Generate report for current year
                    break;
                case "4":
                    // Generate report for previous year
                    break;
                case "5":
                    // Prompt for vendor name and generate report
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
        System.out.printf("%-10s %-8s %-20s %-15s %-10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        boolean hasResults = false;
        for (Transaction transaction : transactions) {
            if (!transaction.getDate().isBefore(startDate) && !transaction.getDate().isAfter(endDate)) {
                System.out.printf("%-10s %-8s %-20s %-15s %-10.2f%n",
                        transaction.getDate().format(DATE_FORMATTER),
                        transaction.getTime().format(TIME_FORMATTER),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
                hasResults = true;
            }
        }
        if (!hasResults) {
            System.out.println("No transactions found for the specified date range.");
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        System.out.printf("%-10s %-8s %-20s %-15s %-10s%n", "Date", "Time", "Description", "Vendor", "Amount");
        boolean hasResults = false;
        for (Transaction transaction : transactions) {
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                System.out.printf("%-10s %-8s %-20s %-15s %-10.2f%n",
                        transaction.getDate().format(DATE_FORMATTER),
                        transaction.getTime().format(TIME_FORMATTER),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
                hasResults = true;
            }
        }
        if (!hasResults) {
            System.out.println("No transactions found for vendor: " + vendor);
        }
    }
}