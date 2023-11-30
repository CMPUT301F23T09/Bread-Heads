package com.example.breadheadsinventorymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagViewHolder> {

    private List<String> tagList;

    public TagsAdapter(List<String> tagList) {
        this.tagList = tagList;
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_item, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        String tag = tagList.get(position);
        holder.tagTextView.setText(tag);
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView;

        public TagViewHolder(View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tagTextView);
        }
    }
}