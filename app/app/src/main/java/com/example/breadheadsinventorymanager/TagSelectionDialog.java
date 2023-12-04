package com.example.breadheadsinventorymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Dialog utility for selecting tags.
 */
public class TagSelectionDialog {
    /**
     * Inflates tag list container to show a list of selected tags.
     * @param context The context the dialog exists in.
     * @param selectedTags The selected tags.
     * @param globalTagList List of all tags; conceptual superset of selectedTags.
     * @param confirmListener Listener for when confirm button is pressed.
     */
    public static void show_selected(Context context, List<String> selectedTags, TagList globalTagList, DialogInterface.OnClickListener confirmListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View dialogView = inflater.inflate(R.layout.taglist_dialog, null);
        builder.setView(dialogView);

        // Get the container for checkboxes
        LinearLayout tagListContainer = dialogView.findViewById(R.id.tagListContainer);

        // Create checkboxes for each tag
        for (Tag tag : globalTagList) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(tag.getTag());

            // Preselect checkboxes based on tags associated with the item
            if (selectedTags.contains(tag.getTag())) {
                checkBox.setChecked(true);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // Add the tag to the selectedTags list
                    selectedTags.add(tag.getTag());
                } else {
                    // Remove the tag from the selectedTags list
                    selectedTags.remove(tag.getTag());
                }
            });

            tagListContainer.addView(checkBox);
        }

        builder.setPositiveButton("Confirm", confirmListener);

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Clear the selectedTags list when "Cancel" is pressed
            selectedTags.clear();
        });

        // Show the dialog
        builder.show();
    }
}
