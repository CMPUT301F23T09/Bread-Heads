package com.example.breadheadsinventorymanager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item in the inventory and all the data it contains
 *
 * Currently only suppports sending data TO Firestore, not retrieving FROM Firestore
 * That is typically better done by adapters anyway!
 *
 * @version
 * 1.1
 */
public class Item {
    // attributes
    private String databaseID; // ID of the item in the firestore database
    private String date;
    private String description;
    private String make;
    private String model;
    private String serialNum;
    private int value; // in cents
    private String comment = ""; // comment is optional
    // private ArrayList<Photo> photos; // second half
    // private TagList tags; // second half

    /**
     * Empty constructor
     */
    public Item() {};

    /**
     * Constructor given a serial number
     */
    public Item(String date, String description, String make,
                String model, String serialNum, int value) {
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNum = serialNum;
        this.value = value;
    }

    /**
     * Constructor when not given a serial number
     * [01.01.01] only requires a serial number "when applicable"
     */
    public Item(String date, String description, String make, String model, int value) {
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.value = value;
    }

    /**
     * Gets the data associated with the object in a format suitable for Firestore
     * @return
     * A HashMap containing the object's data that can be added to Firestore
     */
    public HashMap<String, Object> getData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("date", date);
        map.put("description", description);
        map.put("make", make);
        map.put("model", model);
        map.put("serialNum", serialNum);
        map.put("value", value);
        map.put("comment", comment);
        // TODO SECOND HALF: photos and tags

        return map;
    }

    /**
     * Attempts to put the object into a Firestore collection
     * @param collection
     * Reference to the collection to insert the object into
     */
    public void put(CollectionReference collection) {
        if (databaseID != null) {
            collection.document(databaseID).set(getData());
        }
        else {
            DocumentReference doc = collection.document(); // firestore will generate ID for us
            doc.set(getData());
            this.databaseID = doc.getId(); // make sure this item's ID matches firestore's
        }
    }

    /**
     * Helper function to convert an integer amount of cents to a dollar representation
     * Adapted from Timothy's submitted code for CMPUT 301 Assignment 1
     * @param cents
     * Amount of cents
     * @return
     * String in format 0.00; for example, a cents amount of 1305 would give 13.05
     */
    public static String toDollarString(int cents) {
        int justCents = cents % 100; // taking mod 100 extracts the last 2 digits
        int dollars = cents / 100; // integer division by 100 removes the last 2 digits

        // format as dollars.cents where cents is always 2 digits (e.g. 1305 is $13.05, not $13.5)
        // line adapted from https://stackoverflow.com/a/15358131
        return dollars + "." + String.format("%02d", justCents);
    }

    /**
     * Gets the item's value as a nicely-formatted string
     * Dollar sign prefix is not included, so append that if necessary
     * @return
     * String in format 0.00; for example, a value of 1305 would give 13.05
     */
    public String getValueDollarString() {
        return toDollarString(value);
    }

    /*
    ============================================
    Obligatory getters/setters
    ============================================
     */
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

    public void setValue(int value) {
        this.value = value;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDatabaseID() {
        return databaseID;
    }

    public void setDatabaseID(String databaseID) {
        this.databaseID = databaseID;
    }
}
