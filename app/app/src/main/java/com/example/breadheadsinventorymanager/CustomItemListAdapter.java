package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomItemListAdapter extends ArrayAdapter<Item> {
    private ItemList items;
    private Context context;

    public CustomItemListAdapter(Context context, ItemList items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.main_menu_list_content, parent, false);
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

        return view;

    }
}
