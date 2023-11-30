package com.example.breadheadsinventorymanager;

import static java.lang.Float.parseFloat;
import static java.lang.Math.round;

import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.widget.CheckBox;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
    private String comment = ""; // comment is optional
    private String barcode = ""; // barcode is optional
    private long value; // in cents

    private ArrayList<String> imagePaths = new ArrayList<>();
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private transient CheckBox checkBox; // must be transient so the class can be serialized
    private TagList tags = new TagList();

    // valid format for date setting
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");

    /**
     * Empty constructor.
     */
    public Item() {}

    /**
     * Constructor with serial number but not imagePaths
     */
    public Item(String date, String description, String make, String model, String comments, long value, String serialNum) {
        this(date, description, make, model, comments, value);
        this.serialNum = serialNum;
    }

    /**
     * Constructor with imagePaths but not serial number
     */
    public Item(String date, String description, String make, String model, String comments, long value, ArrayList<String> imagePaths, ArrayList<Uri> imageUris) {
        this(date, description, make, model, comments, value);
        this.imagePaths = imagePaths;
        this.imageUris = imageUris;

    }

    /**
     * Constructor with everything (no tags)
     */
    public Item(String date, String description, String make, String model, String comments, long value, String serialNum, ArrayList<String> imagePaths, ArrayList<Uri> imageUris, List<String> tags, String barcode) {
        this(date, description, make, model, comments, value);
        this.barcode = barcode;
        this.serialNum = serialNum;
        this.imagePaths = imagePaths;
        this.imageUris = imageUris;
        this.tags = new TagList(tags);
        Log.d(null,tags.toString());
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
        value = document.getLong("value");
        comment = document.getString("comment");
        imagePaths = (ArrayList<String>) document.get("imagePaths");
        imageUris = (ArrayList<Uri>) document.get("imageUris");
        barcode = document.getString("barcode");
        List<HashMap<String, Object>> tagsFromFirebase = (List<HashMap<String, Object>>) document.get("tags");
        List<String> tagList = new ArrayList<>();

        if (tagsFromFirebase != null) {
            for (HashMap<String, Object> tagMap : tagsFromFirebase) {
                String tag = (String) tagMap.get("tag");
                if (tag != null) {
                    tagList.add(tag);
                }
            }
        }

        tags = new TagList(tagList);
    }

    /**
     * Constructor given an Item document snapshot containing the parameters of the field.
     * @param document From the Firestore database; must be formatted correctly as an Item.
     */
    // TODO: HANDLE TAGS THEY ARE MAPPED AS HASHMAP
    public Item(QueryDocumentSnapshot document) {
        id = document.getId();
        date = document.getString("date");
        description = document.getString("description");
        make = document.getString("make");
        model = document.getString("model");
        serialNum = document.getString("serialNum");
        value = document.getLong("value");
        comment = document.getString("comment");
        imagePaths = (ArrayList<String>) document.get("imagePaths");
        imageUris = (ArrayList<Uri>) document.get("imageUris");
        barcode = document.getString("barcode");
        // Assuming 'tags' is a list of HashMaps
        List<HashMap<String, Object>> tagsFromFirebase = (List<HashMap<String, Object>>) document.get("tags");
        List<String> tagList = new ArrayList<>();

        if (tagsFromFirebase != null) {
            for (HashMap<String, Object> tagMap : tagsFromFirebase) {
                String tag = (String) tagMap.get("tag");
                if (tag != null) {
                    tagList.add(tag);
                }
            }
        }

        tags = new TagList(tagList);

    }

    /**
     * Constructor given most fields, not incl. serial number.
     * [01.01.01] only requires a serial number "when applicable"
     */
    public Item(String date, String description, String make, String model, String comments, long value) {
        this.date = LocalDate.parse(date, formatter)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); // coerce date into nice format
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
        return LocalDate.parse(getDate(), formatter);

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
        map.put("imagePaths", imagePaths);
        map.put("imageUris", imageUris);
        map.put("tags", tags);
        map.put("barcode", barcode);

        return map;
    }

    public void put(CollectionReference collection) {
        // pass
    }

    /*
    ============================================
    Sorting utility
    ============================================
     */

    /**
     * Returns a comparator for sorting by the specified parameter
     * @param sortMode The field to sort by; accepts "comment", "make", "date", "value", or
     *                 "description". Defaults to description if not specified
     * @param ascending True if sorting in ascending order, else false
     * @return The comparator for the specified field in the given order
     */
    public static Comparator<Item> getComparator(String sortMode, boolean ascending) {
        Comparator<Item> comparator;
        switch (sortMode) {
            case("comment"):
                comparator = (Item lhs, Item rhs) ->
                    String.CASE_INSENSITIVE_ORDER.compare(lhs.getComment(), rhs.getComment());
                break;
            case("make"):
                comparator = (Item lhs, Item rhs) ->
                        String.CASE_INSENSITIVE_ORDER.compare(lhs.getMake(), rhs.getMake());
                break;
            case("date"):
                comparator = Comparator.comparing(Item::getDateObj);
                break;
            case("value"):
                comparator = Comparator.comparing(Item::getValue);
                break;
            default:
                // default to description
                comparator = (Item lhs, Item rhs) ->
                        String.CASE_INSENSITIVE_ORDER.compare(lhs.getDescription(), rhs.getDescription());
                break;
        }
        return ascending ? comparator : comparator.reversed();
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

    public void setDate(String date) {
        this.date = LocalDate.parse(date, formatter)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));  // coerce date into nice format
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

    public void addImagePath(String imagePath) {
        imagePaths.add(imagePath);
    }

    public void removeImagePath(String imagePath) {
        imagePaths.remove(imagePath);
    }

    public void removeImagePaths(ArrayList<String> imagePaths) {
        for (String path : imagePaths) {
            removeImagePath(path);
        }
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public void addImageUri(Uri uri) { imageUris.add(uri);}

    public ArrayList<Uri> getImageUris() { return imageUris;}

    public TagList getTags() {
        return tags;
    }

    public void setTags(TagList tags) {
        this.tags = tags;
    }

    public String getBarcode() { return barcode; }

    public void setBarcode(String barcode) { this.barcode = barcode; }

    /**
     * Sets CheckBox object associated with this Item.
     * @param checkBox Checkbox object
     */
    public void setCheckBox(CheckBox checkBox) {this.checkBox = checkBox;}

    /**
     * Gets CheckBox object associated with this Item.
     * @return Object's associated CheckBox
     */
    public CheckBox getCheckBox() {return this.checkBox;}
}

