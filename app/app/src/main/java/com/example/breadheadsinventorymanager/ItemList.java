package com.example.breadheadsinventorymanager;

import java.util.HashMap;

public class ItemList{

    private final HashMap<String, Item> itemGrid;

    public ItemList() {
        this.itemGrid = new HashMap<>();
    }

    public void addItem(Item item){
        itemGrid.put(item.getSerialNum(), item);
    }

    public Item getItem(String serialNumber){
        return itemGrid.get(serialNumber);
    }
}
