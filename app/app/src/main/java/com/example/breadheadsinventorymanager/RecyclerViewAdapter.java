package com.example.breadheadsinventorymanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Recycler adapter borrowed from this video https://www.youtube.com/watch?v=7GPUpvcU1FE
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private final AddItemFragment.OnFragmentInteractionListener recyclerViewInterface;

    public interface OnItemClickListener {
        void onItemClick(RecyclerItem item);
    }


    private Context context;
    private final List<RecyclerItem> items;

    RecyclerViewAdapter(Context context, List<RecyclerItem> items, AddItemFragment.OnFragmentInteractionListener recyclerViewInterface) {
        this.context = context;
        this.items = items;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_recycler_view_layout, parent, false);
        return new ViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind the filter string to the textView
        holder.filterName.setText(items.get(position).getFilter());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView filterName;
        private Button cancelFilter;

        public ViewHolder(View itemView, AddItemFragment.OnFragmentInteractionListener recyclerViewInterface) {
            super(itemView);
            filterName = itemView.findViewById(R.id.filter_name);
            cancelFilter = itemView.findViewById(R.id.cancel_filter);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
