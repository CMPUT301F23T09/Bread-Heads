package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.util.Log;
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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Custom filter to allow filtering by multiple fields at once.
     * Filter is done by passing a JSON array and parsing it as a string.
     * The first character of each entry indicates what type of filter it is.
     * The rest of the entry is the info about the filter.
     * This approach is kinda clunky but is necessary because the arguments of Filter() methods must
     * be CharSequences.
     */
    Filter customFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            JsonArray filtersJson = JsonParser.parseString((String) constraint).getAsJsonArray();
            // conversion utility adapted from https://stackoverflow.com/a/8371455
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> filters = new Gson().fromJson(filtersJson, listType);

            // parse filters
            String description = null;
            LocalDate initDate = null;
            LocalDate finalDate = null;
            ArrayList<String> makes = new ArrayList<>();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("d/M/yyyy");
            for (String filter : filters) {
                switch (filter.charAt(0)) {
                    case 'D': // description
                        description = filter.substring(1).toLowerCase();
                        break;
                    case '1': // lower date bound
                        initDate = LocalDate.parse(filter.substring(1), format);
                        break;
                    case '2': // upper date bound
                        finalDate = LocalDate.parse(filter.substring(1), format);
                        break;
                    case 'M': // make
                        makes.add(filter.substring(1));
                        break;
                }
            }

            if (items != null) {
                ItemList output = new ItemList();
                for (int i = 0; i < items.size(); i++) {
                    boolean keepFlag = true; // do we keep the item or not? default "yes"
                    Item item = items.get(i);

                    if (description != null) {
                        // unnecessary '&& keepFlag' kept for clarity
                        keepFlag = item.getDescription().toLowerCase().contains(description)
                                && keepFlag;
                    }
                    if (initDate != null) {
                        keepFlag = !(item.getDateObj().isBefore(initDate)) && keepFlag;
                    }
                    if (finalDate != null) {
                        keepFlag = !(item.getDateObj().isAfter(finalDate)) && keepFlag;
                    }
                    if (!makes.isEmpty()) {
                        keepFlag = makes.contains(item.getMake()) && keepFlag;
                    }

                    if (keepFlag) { // passed all applicable tests
                        output.add(item);
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
