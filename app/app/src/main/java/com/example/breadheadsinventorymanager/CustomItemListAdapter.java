package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomItemListAdapter extends ArrayAdapter<Item> {
    private ItemList items;
    private Context context;
    private ItemList newItemList;

    public CustomItemListAdapter(Context context, ItemList items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    public void update(ArrayList<Item> results) {
        // updates adapter with data, called in filter showMakeSubMenu()
        newItemList = new ItemList();
        newItemList.addAll(results);
        notifyDataSetChanged();
        }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.main_menu_list_content_constraint, parent, false);
        }

        // Find TextView widgets for each main menu item
        Item item = items.get(position);
        TextView itemDescriptionTV = view.findViewById(R.id.item_description_text);
        TextView itemMakeTV = view.findViewById(R.id.item_make_text);
        TextView itemModelTV = view.findViewById(R.id.item_model_text);
        TextView itemAcquisitionDateTV = view.findViewById(R.id.item_acquisition_date_text);
        TextView itemValueTV = view.findViewById(R.id.item_value_text);

        // Display items
        itemDescriptionTV.setText(item.getDescription());
        itemMakeTV.setText(item.getMake());
        itemModelTV.setText(item.getModel());
        itemAcquisitionDateTV.setText(item.getDate());
        itemValueTV.setText("" + item.getValue());

        // Create a unique Checkbox for each item that is accessible elsewhere
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        item.setCheckBox(checkBox);

        return view;

    }
}
