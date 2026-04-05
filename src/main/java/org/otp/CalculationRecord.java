package org.otp;

public class CalculationRecord {
    private final double distance;
    private final double consumption;
    private final double price;
    private final double totalFuel;
    private final double totalCost;
    private final String language;

    public CalculationRecord(double distance, double consumption, double price, double totalFuel, double totalCost, String language) {
        this.distance = distance;
        this.consumption = consumption;
        this.price = price;
        this.totalFuel = totalFuel;
        this.totalCost = totalCost;
        this.language = language;
    }

    public double getDistance() {
        return distance;
    }

    public double getConsumption() {
        return consumption;
    }

    public double getPrice() {
        return price;
    }

    public double getTotalFuel() {
        return totalFuel;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getLanguage() {
        return language;
    }
}
