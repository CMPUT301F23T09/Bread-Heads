package com.example.breadheadsinventorymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditItemFragment extends DialogFragment {

    // Define UI elements for editing
    private EditText itemNameBox;
    private EditText itemModelBox;
    private EditText itemMakeBox;
    private EditText itemDateBox;
    private EditText itemCommentsBox;
    private EditText itemValueBox;
    private TextView errorBox;
    OnFragmentInteractionListener listener;
    private Item selectedItem; // The item to be edited
    private FirestoreInteract database;

    public EditItemFragment() {
        // Required empty public constructor
    }

    public static EditItemFragment newInstance(Item item) {
        EditItemFragment fragment = new EditItemFragment();
        Bundle args = new Bundle();
        args.putSerializable("selectedItem", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedItem = (Item) getArguments().getSerializable("selectedItem");
        }
        database = new FirestoreInteract();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for the EditItemFragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_item_layout, null);

        // Initialize UI elements for editing
        itemNameBox = view.findViewById(R.id.edit_item_name_text);
        itemMakeBox = view.findViewById(R.id.edit_item_make_text);
        itemModelBox = view.findViewById(R.id.edit_item_model_text);
        itemDateBox = view.findViewById(R.id.edit_item_acquisition_date_text);
        itemValueBox = view.findViewById(R.id.edit_item_value_text);
        itemCommentsBox = view.findViewById(R.id.edit_item_comments_text);
        errorBox = view.findViewById(R.id.edit_error_text_message);

        // Pre-fill UI elements with data from selectedItem
        if (selectedItem != null) {
            itemNameBox.setText(selectedItem.getDescription());
            itemMakeBox.setText(selectedItem.getMake());
            itemModelBox.setText(selectedItem.getModel());
            itemDateBox.setText(selectedItem.getDate());
            itemValueBox.setText(String.valueOf(selectedItem.getValue()));
            itemCommentsBox.setText(selectedItem.getComment());
        }

        // Set up the dialog with OK and Cancel buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        builder.setTitle("Edit Item");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle editing and saving of the item here
                selectedItem.setDescription(itemNameBox.getText().toString());
                selectedItem.setMake(itemMakeBox.getText().toString());
                selectedItem.setModel(itemModelBox.getText().toString());
                selectedItem.setDate(itemDateBox.getText().toString());
                selectedItem.setValue((long) Double.parseDouble(itemValueBox.getText().toString()));
                selectedItem.setComment(itemCommentsBox.getText().toString());
                Log.d("EditItemFragment", "Updated item details: " + selectedItem.toString());
                // Use the putItem method to update the item in Firestore
                database.putItem(selectedItem).addOnSuccessListener(aVoid -> {
                    Log.d("EditItemFragment", "Firestore update successful");
                    // Notify the listener that the item has been updated
                    notifyItemUpdated(selectedItem);
                }).addOnFailureListener(e -> {
                    // Handle failure if needed
                    Log.e("EditItemFragment", "Error updating item in Firestore", e);
                });
                if (checkDataEntry()) {
                    // Update the item in Firestore using putItem method
                    new FirestoreInteract().putItem(selectedItem);
                    // Notify the listener that the item has been updated
                    notifyItemUpdated(selectedItem);
                    dialog.dismiss();
                } else {
                    errorBox.setVisibility(View.VISIBLE);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    // Define the checkDataEntry() method to validate user input
    private boolean checkDataEntry() {
        // Implement your validation logic here and update the selected item
        // You can access UI elements like itemNameBox, itemMakeBox, etc.
        // to get the user's input and validate it.

        // If the data is valid, update the selected item and return true
        // If the data is invalid, display an error message and return false
        Log.d("EditItemFragment", "checkDataEntry returning true");
        return true;
    }

    public interface OnFragmentInteractionListener {
        void onItemUpdated(Item item);
    }

    private void notifyItemUpdated(Item item) {
        if (listener != null) {
            listener.onItemUpdated(item);
        }
    }
}