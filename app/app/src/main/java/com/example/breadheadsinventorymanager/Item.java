package com.example.breadheadsinventorymanager;

import static android.text.TextUtils.substring;

import static java.lang.Float.parseFloat;
import static java.lang.Math.round;

import android.content.res.Resources;
import android.widget.CheckBox;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Represents an item in the inventory and all the data it contains.
 * @version 1.2
 */
public class Item implements FirestorePuttable, Serializable {
    // attributes
    private String id = null; // ID of the item in the firestore database
    private String date;
    private String description;
    private String make;
    private String model;
    private String serialNum;
    private long value; // in cents
    private transient CheckBox checkBox; // must be transient so the class can be serialized
    private String comment = ""; // comment is optional
    private TagList tags;
    // private ArrayList<Photo> photos; // second half

    /**
     * Empty constructor.
     */
    public Item() {}

    /**
     * Constructor given all fields, incl. serial number.
     */
    public Item(String date, String description, String make, String model, String serialNum, String comments, long value) {
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNum = serialNum;
        this.value = value;
        this.comment = comments;
        this.tags = new TagList();
    }

    /**
     * Constructor given an Item document snapshot containing the parameters of the field.
     * @param document From the Firestore database; must be formatted correctly as an Item.
     */
    public Item(DocumentSnapshot document) {
        id = document.getId();
        date = document.getString("date");
        description = document.getString("description");
        make = document.getString("make");
        model = document.getString("model");
        serialNum = document.getString("serialNum");
        value = (long) document.get("value");
        comment = document.getString("comment");
        tags = new TagList((List<String>) document.get("tags"));
        // TODO PART 2: photos
    }

    /**
     * Constructor given an Item document snapshot containing the parameters of the field.
     * @param document From the Firestore database; must be formatted correctly as an Item.
     */
    public Item(QueryDocumentSnapshot document) {
        id = document.getId();
        date = document.getString("date");
        description = document.getString("description");
        make = document.getString("make");
        model = document.getString("model");
        serialNum = document.getString("serialNum");
        value = document.getLong("value");
        comment = document.getString("comment");
        tags = new TagList((List<String>) document.get("tags"));
    }

    /**
     * Constructor given most fields, not incl. serial number.
     * [01.01.01] only requires a serial number "when applicable"
     */
    public Item(String date, String description, String make, String model, String comments, long value) {
        this.date = date;
        this.description = description;
        this.make = make;
        this.model = model;
        this.value = value;
        this.comment = comments;
        this.tags = new TagList();
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
        return dollars + "." + String.format(Resources.getSystem()
                .getConfiguration().getLocales().get(0), "%02d", justCents);
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

    /**
     * Static utility to convert a dollar string to a value
     * @param string A String formatted as a decimal value with or without a leading $
     * @return The returned value, i.e. a long amount of cents
     */
    public static long toValue(String string) {
        if (string.charAt(0) == '$') {
            string = string.substring(1);
        }

        double parsedFloat = parseFloat(string);
        return (long)round(parsedFloat * 100.0);
    }

    /**
     * Gets the date of the Item in as a LocalDate object
     * @return the Local date object of the item
     */
    public LocalDate getDateObj() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(this.date, formatter);

    }


    /*
    ============================================
    Implement the FirestorePuttable interface
    ============================================
     */
    public HashMap<String, Object> formatForFirestore() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("date", date);
        map.put("description", description);
        map.put("make", make);
        map.put("model", model);
        map.put("serialNum", serialNum);
        map.put("value", value);
        map.put("comment", comment);
        map.put("tags", tags);
        // TODO SECOND HALF: photos

        return map;
    }

    public void put(CollectionReference collection) {
        // pass
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public TagList getTags() {
        return tags;
    }

    public void setTags(TagList tags) {
        this.tags = tags;
    }

    public void setCheckBox(CheckBox checkBox) {this.checkBox = checkBox;}

    public CheckBox getCheckBox() {return this.checkBox;}

}

