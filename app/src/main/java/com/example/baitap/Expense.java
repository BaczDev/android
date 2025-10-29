package com.example.baitap;

public class Expense {
    private int id;
    private String description;
    private double amount;
    private String date; // Định dạng YYYY-MM-DD

    public Expense() {
        // Constructor rỗng
    }

    public Expense(int id, String description, double amount, String date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    // Các hàm Getters
    public int getId() { return id; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }

    // Các hàm Setters
    public void setId(int id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
}