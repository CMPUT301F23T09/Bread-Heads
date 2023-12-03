package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Custom recyclerView adapter to display active filters and allow for removing them by tap.
 * <a href="https://www.youtube.com/watch?v=7GPUpvcU1FE">very helpful guide to setting up click listeners for recyclerViews</a> ;
 * <a href="https://www.youtube.com/watch?v=Zj9ZE6_HtEo">another great video on how to set up recyclerViews</a>
 * both videos were used to create this class
 */
public class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.MyHolder> {
    private final AddItemFragment.OnFragmentInteractionListener recyclerViewInterface;
    ArrayList<String> data;
    Context context;

    /**
     * Constructor for FilterRecyclerAdapter
     * @param context Context it lives in
     * @param data Data to populate with
     * @param recyclerViewInterface Recycler View Interface to populate
     */
    public FilterRecyclerAdapter(Context context, ArrayList<String> data, AddItemFragment.OnFragmentInteractionListener recyclerViewInterface) {
        this.data = data;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.active_filter_item_layout, parent, false);
        return new MyHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.filterString.setText(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Custom view holder that lets us tap to remove filters
     */
    static class MyHolder extends RecyclerView.ViewHolder {
        TextView filterString;
        ImageButton removeButton;
        public MyHolder(@NonNull View itemView, AddItemFragment.OnFragmentInteractionListener recyclerViewInterface) {
            super(itemView);
            filterString = itemView.findViewById(R.id.filter_name);
            removeButton = itemView.findViewById(R.id.remove_filter_item_button);
            // click listener for the recyclerView item
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        // make interface call
                        if(pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onRecyclerItemPressed(pos);
                        }
                    }
                }
            });
        }
    }
}
