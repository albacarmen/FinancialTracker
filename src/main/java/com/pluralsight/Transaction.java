package com.pluralsight;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private LocalDate date;
    private LocalTime time;
    private String description;
    private String vendor;
    private double amount;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    // Constructor
    public Transaction(LocalDate date, LocalTime time, String description, String vendor, double amount) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.vendor = vendor;
        this.amount = amount;
    }

    // Getters
    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getVendor() {
        return vendor;
    }

    public double getAmount() {
        return amount;
    }

    // Setters
    public void setAmount(double amount) {
        this.amount = amount;
    }

    // Convert Transaction to CSV format
    public String toCsv() {
        return String.format("%s|%s|%s|%s|%.2f",
                date.format(DATE_FORMATTER),
                time.format(TIME_FORMATTER),
                description,
                vendor,
                amount);
    }

    // Override toString method for display purposes
    @Override
    public String toString() {
        return String.format("%s %s | %s | %s | %.2f",
                date.format(DATE_FORMATTER),
                time.format(TIME_FORMATTER),
                description,
                vendor,
                amount);
    }
}
