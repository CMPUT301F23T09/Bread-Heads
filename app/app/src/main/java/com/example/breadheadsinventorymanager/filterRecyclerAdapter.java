package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

// adapter for the recycler view for displaying active filters
// https://www.youtube.com/watch?v=7GPUpvcU1FE very helpful guide to setting up click listeners for recyclerViews
// https://www.youtube.com/watch?v=Zj9ZE6_HtEo another great video on how to set up recyclerViews
// both videos were used to create this class
public class filterRecyclerAdapter extends RecyclerView.Adapter<filterRecyclerAdapter.MyHolder> {

    private final AddItemFragment.OnFragmentInteractionListener recyclerViewInterface;

    ArrayList<String> data;
    Context context;
    public filterRecyclerAdapter(Context context, ArrayList<String> data, AddItemFragment.OnFragmentInteractionListener recyclerViewInterface) {
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
