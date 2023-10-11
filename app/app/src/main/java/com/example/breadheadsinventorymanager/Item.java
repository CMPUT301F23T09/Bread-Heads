package com.example.breadheadsinventorymanager;

public class Item {

    // attributes
    String date;
    String description;
    String make;
    String model;
    String serialNum;
    double value;
    String comment;

    // constructor for an Item with all the attributes
    public Item(String date, String description, String make, String model, String serialNum, double value) {
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNum = serialNum;
        this.value = value;
    }

    // constructor for if there is no serial number, user story 01.01.01 requires a "serial number (if applicable)"
    public Item(String date, String description, String make, String model, double value, String comment) {
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.value = value;
    }

    // Obligatory getters/setters
    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public double getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
