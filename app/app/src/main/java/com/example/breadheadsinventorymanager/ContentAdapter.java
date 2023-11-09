package com.example.breadheadsinventorymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RecyclerItem item);
    }


    private final List<RecyclerItem> items;
    private final OnItemClickListener listener;

    public ContentAdapter(List<RecyclerItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_recycler_view_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView filterName;
        private Button cancelFilter;

        public ViewHolder(View itemView) {
            super(itemView);
            filterName = itemView.findViewById(R.id.filter_name);
            cancelFilter = itemView.findViewById(R.id.cancel_filter);
        }

        public void bind(final RecyclerItem item, final OnItemClickListener listener) {
            filterName.setText(item.getFilter());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
