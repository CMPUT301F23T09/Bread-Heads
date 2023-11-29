package com.example.breadheadsinventorymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.StorageReference;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


/**
 * Fragment for editing existing items in the inventory.
 */
public class EditItemFragment extends DialogFragment implements ImageAdapter.ItemClickListener {

    // Define UI elements for editing
    private EditText itemNameBox;
    private EditText itemModelBox;
    private EditText itemMakeBox;
    private EditText itemDateBox;
    private EditText itemCommentsBox;
    private EditText itemValueBox;

    private Button removeImagesBtn;
    // private Button addImagesBtn; //implement later
    private RecyclerView imageRecyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<String> imagesToDelete;
    private TextView errorBox;
    OnFragmentInteractionListener listener;
    private Item selectedItem; // The item to be edited
    private boolean deleteImagesMode = false;
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
        imagesToDelete = new ArrayList<>();
    }

    @Override
    public void onItemClick(String imagePath, int position) {
        if (deleteImagesMode) {
            if (imagesToDelete.contains(imagePath)) { // Removing from the selection
                imagesToDelete.remove(imagePath);
                String msg = "image " + imagePath.toString() + "removed from candidates";
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
            else { // Adding to the selection
                imagesToDelete.add(imagePath);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Inflate the layout for the EditItemFragment
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.edit_item_layout, null);

        // Initialize UI elements for editing and Pre-fill UI elements with data from selectedItem
        itemNameBox = view.findViewById(R.id.edit_item_name_text);
        itemMakeBox = view.findViewById(R.id.edit_item_make_text);
        itemModelBox = view.findViewById(R.id.edit_item_model_text);
        itemDateBox = view.findViewById(R.id.edit_item_acquisition_date_text);
        itemValueBox = view.findViewById(R.id.edit_item_value_text);
        itemCommentsBox = view.findViewById(R.id.edit_item_comments_text);
        removeImagesBtn = view.findViewById(R.id.select_images_to_delete_button);
        errorBox = view.findViewById(R.id.edit_error_text_message);

        if (selectedItem != null) {
            itemNameBox.setText(selectedItem.getDescription());
            itemMakeBox.setText(selectedItem.getMake());
            itemModelBox.setText(selectedItem.getModel());
            itemDateBox.setText(selectedItem.getDate());
            itemValueBox.setText(String.valueOf(selectedItem.getValue()));
            itemCommentsBox.setText(selectedItem.getComment());

            // For displaying image previews
            imageRecyclerView = view.findViewById(R.id.image_recyclerView);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            imageRecyclerView.setLayoutManager(layoutManager);

            // Obtain image references
            imageAdapter = new ImageAdapter(getActivity(), selectedItem.getImagePaths(), this);
            imageRecyclerView.setAdapter(imageAdapter);
        }

        // Removing images
        removeImagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteImagesMode) { // Confirm deletion
//                    imageAdapter.toggleCheckboxVisibility(); //fixme: this doesn't work??
                    for (String imagePath : imagesToDelete) {
                        selectedItem.removeImagePath(imagePath); // fixme: also communicate to the database, and check handle cases where items don't have images, also when the entire item is deleted
                        database.deleteImage(imagePath);
                    }
                    imagesToDelete.clear();
                    deleteImagesMode = false;
                    imageAdapter.changeCheckboxVisibility(false);
                    imageAdapter.notifyDataSetChanged();
                }
                else { // Start selection
                    deleteImagesMode = true;
                    imageAdapter.changeCheckboxVisibility(true);
                }
            }
        });

        // Set up the dialog with Save and Cancel buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(view);
        builder.setTitle("Edit Item");

        // Set positive button
        builder.setPositiveButton("Save", null);

        // Set negative button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        // Set an onShowListener to override the onClick method of the positive button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Handle editing and saving of the item here
                        selectedItem.setDescription(itemNameBox.getText().toString());
                        selectedItem.setMake(itemMakeBox.getText().toString());
                        selectedItem.setModel(itemModelBox.getText().toString());
                        selectedItem.setDate(itemDateBox.getText().toString());

                        // Validate and parse the value When this was in the check function
                        // it was crashing the app but it works when it's here
                        String valueText = itemValueBox.getText().toString();
                        try {
                            double parsedValue = Double.parseDouble(valueText);
                            selectedItem.setValue((long) parsedValue);
                        } catch (NumberFormatException e) {
                            Log.e("EditItemFragment", "Error parsing value", e);
                            errorBox.setText("Invalid Value");
                            errorBox.setVisibility(View.VISIBLE);
                            return;
                        }

                        selectedItem.setComment(itemCommentsBox.getText().toString());

                        // Validate the date
                        if (!checkDateEntry()) {
                            // Display an error message or handle the invalid date case
                            errorBox.setVisibility(View.VISIBLE);
                        } else {
                            // Use the putItem method to update the item in Firestore
                            database.putItem(selectedItem).addOnSuccessListener(aVoid -> {
                                Log.d("EditItemFragment", "Firestore update successful");
                                // Notify the listener that the item has been updated
                                notifyItemUpdated(selectedItem);

                                // Dismiss the dialog only if the data entry is valid and Firestore update is successful
                                dialog.dismiss();
                            }).addOnFailureListener(e -> {
                                // Handle failure if needed
                                Log.e("EditItemFragment", "Error updating item in Firestore", e);
                            });
                        }
                    }
                });
            }
        });

        // Set up the onCancelListener to handle the Cancel button
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                // Handle cancellation if needed
                Log.d("EditItemFragment", "Dialog canceled");
            }
        });

        return dialog;
    }

    private ArrayList<StorageReference> fetchImageReferencesFromStorage() {
        ArrayList<StorageReference> imageRefs = new ArrayList<>();
        for (String imagePath : selectedItem.getImagePaths()) {
            imageRefs.add(database.getStorageReference().child(imagePath));
        }
        return imageRefs;
    }

    // Define the checkDataEntry() method to validate user input
    private boolean checkDateEntry() {
        // If the data is valid, update the selected item and return true
        // If the data is invalid, display an error message and return false
        String name = itemNameBox.getText().toString();
        String make = itemMakeBox.getText().toString();
        String model = itemModelBox.getText().toString();
        String date = itemDateBox.getText().toString();
        String value = itemValueBox.getText().toString();
        String comments = itemCommentsBox.getText().toString();

        // Check for empty fields
        if (name.equals("") || make.equals("") || model.equals("") || date.equals("") || value.equals("")) {
            errorBox.setText("Empty Fields");
            return false;
        }

//        // Check if value is parsable
//        long newValue;
//        try {
//            newValue = Item.toValue(value);
//        } catch (NumberFormatException e) {
//            errorBox.setText("Invalid Value");
//            return false;
//        }

        // Check the date format and validity
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate newDate = LocalDate.parse(date, formatter);
            LocalDate currentDate = LocalDate.now();
            if (newDate.isAfter(currentDate)) {
                Log.e("EditItemFragment", "Error entered date is after current date");
                errorBox.setText("Invalid Date");
                return false;
            }
        } catch (DateTimeParseException e) {
            Log.e("EditItemFragment", "Error parsing date", e);
            errorBox.setText("Invalid Date");
            return false;
        }

        // Data is valid
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