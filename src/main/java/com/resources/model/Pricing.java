package com.resources.model;

public class Pricing {
    private int id;
    private double washPrice;
    private double dryPrice;
    private double foldPrice;
    
    public Pricing() {}
    
    public Pricing(double washPrice, double dryPrice, double foldPrice) {
        this.washPrice = washPrice;
        this.dryPrice = dryPrice;
        this.foldPrice = foldPrice;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getWashPrice() { return washPrice; }
    public void setWashPrice(double washPrice) { this.washPrice = washPrice; }
    public double getDryPrice() { return dryPrice; }
    public void setDryPrice(double dryPrice) { this.dryPrice = dryPrice; }
    public double getFoldPrice() { return foldPrice; }
    public void setFoldPrice(double foldPrice) { this.foldPrice = foldPrice; }
}