package com.example.farm;

import java.io.Serializable;

public class Nutrition implements Serializable {

    private String nutrition;
    private String unit;
    private double amount;
    private String effect;
    private String type;
    public Nutrition(String nutrition, String unit, double amount, String type, String effect){
        this.nutrition = nutrition;
        this.unit = unit;
        this.amount = amount;
        this.type = type;
        this.effect = effect;
    }

    public String getNutrition() {
        return nutrition;
    }

    public String getUnit() {
        return unit;
    }

    public double getAmount() {
        return amount;
    }

    public String getEffect() {
        return effect;
    }

    public String getType() {
        return type;
    }
}