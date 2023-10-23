package com.example.breadheadsinventorymanager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

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
    private long value; // in cents
    private String comment = ""; // comment is optional
    // private ArrayList<Photo> photos; // second half
    // private TagList tags; // second half

    /**
     * Empty constructor
     */
    public Item() {};

    /**
     * Constructor given most fields, incl. serial number
     */
    public Item(String date, String description, String make,
                String model, String serialNum, long value) {
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNum = serialNum;
        this.value = value;
    }

    /**
     * Constructor given a document snapshot containing the parameters of the field
     * @param document From the Firestore database
     */
    public Item(QueryDocumentSnapshot document) {
        databaseID = document.getId();
        date = document.getString("date");
        description = document.getString("description");
        make = document.getString("make");
        model = document.getString("model");
        serialNum = document.getString("serialNum");
        value = (long) document.get("value");
        comment = document.getString("comment");
        // TODO PART 2: photos and tags
    }

    /**
     * Constructor given most fields, not incl. serial number.
     * [01.01.01] only requires a serial number "when applicable"
     */
    public Item(String date, String description, String make, String model, long value) {
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
    public static String toDollarString(long cents) {
        long justCents = cents % 100; // taking mod 100 extracts the last 2 digits
        long dollars = cents / 100; // integer division by 100 removes the last 2 digits

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

    public long getValue() {
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

    public void setValue(long value) {
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
