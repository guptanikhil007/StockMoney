package com.nikhilpc.stockmoney;

/**
 * Created by NIKHIL on 7/18/2016.
 */
public class Stock {
    public String Stock_Name;
    public String Stock_Code;
    public double CurrentPrice;
    public double PriceRise;
    public Stock(){}
    public Stock(String stock_Name, String stock_Code, double currentPrice, double priceRise) {
        Stock_Name = stock_Name;
        Stock_Code = stock_Code;
        CurrentPrice = currentPrice;
        PriceRise = priceRise;
    }

    public String getStock_Name() {
        return Stock_Name;
    }

    public double getCurrentPrice() {
        return CurrentPrice;
    }

    public String getStock_Code() {
        return Stock_Code;
    }

    public double getPriceRise() {
        return PriceRise;
    }

    public void setStock_Name(String stock_Name) {
        Stock_Name = stock_Name;
    }

    public void setPriceRise(double priceRise) {
        PriceRise = priceRise;
    }

    public void setCurrentPrice(double currentPrice) {
        CurrentPrice = currentPrice;
    }

    public void setStock_Code(String stock_Code) {
        Stock_Code = stock_Code;
    }
}
