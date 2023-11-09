package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomItemListAdapter extends ArrayAdapter<Item> implements Filterable {
    private ItemList items;
    private Context context;

    public CustomItemListAdapter(Context context, ItemList items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (items == null) {
            return 0;
        }
        return this.items.size();
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
        itemValueTV.setText("$" + item.getValueDollarString());

        // Create a unique Checkbox for each item that is accessible elsewhere
        CheckBox checkBox = view.findViewById(R.id.checkBox);
        item.setCheckBox(checkBox);


        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return customFilter;
    }

    Filter customFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            JsonObject filters = JsonParser.parseString((String) constraint).getAsJsonObject();

            if (constraint != null && constraint.length() > 1 && items != null) {
                char filterType = constraint.charAt(0);
                CharSequence filterContent = constraint.subSequence(1, constraint.length());
                ItemList output = new ItemList();

                for (int i = 0; i < items.size(); i++) {
                    Item item = items.get(i);
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yyyy");
                    switch (filterType) {
                        case 'D': // description
                            if (item.getDescription().toLowerCase()
                                    .contains(filterContent.toString().toLowerCase())) {
                                output.add(item);
                            }
                            break;
                        case '1': // initial date in range
                            if (!item.getDateObj().isBefore(LocalDate.parse(constraint, format))) {
                                output.add(item);
                            }
                            break;
                        case '2':
                            if (!item.getDateObj().isAfter(LocalDate.parse(constraint, format))) {
                                output.add(item);
                            }
                            break;
                        case 'M': // make
                            if (item.getMake().equals(filterContent.toString())) {
                                output.add(item);
                            }
                            break;
                    }

                }

                results.values = output;
                results.count = output.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items = (ItemList) results.values;
            notifyDataSetChanged();
        }
    };
}
