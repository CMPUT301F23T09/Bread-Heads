package com.example.breadheadsinventorymanager;

import static java.lang.Long.parseLong;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Fragment for adding new items to the inventory.
 */
public class AddItemFragment extends DialogFragment {

    // editText ids
    EditText itemNameBox;
    EditText itemModelBox;
    EditText itemMakeBox;
    EditText itemSerialNumBox;
    EditText itemDateBox;
    EditText itemCommentsBox;
    EditText itemValueBox;
    TextView errorBox;
    com.google.android.material.floatingactionbutton.FloatingActionButton addImageBtn;

    private ActivityResultLauncher<String> mGetContent;
    private Map<String, Uri> imageMap = new HashMap<String, Uri>();
    private OnFragmentInteractionListener listener;

    /**
     * Called when the fragment is first attached to MainActivity
     * Checks if the fragment has implemented the required listener
     * @param context context of the dialog that pops up
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + "OnFragmentInteractionListener is not implemented");
        }

        // For accessing the Gallery for images - must be called upon instantiation (in onAttach)
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        // Handle the new image
                        String imagePath = "images/" + UUID.randomUUID().toString();
                        if (uri != null) {
                            imageMap.put(imagePath, uri); //fixme: use try catch in case key already exists, implement after edit is complete.
                        }
                    }
                }
        );
    }

    /**
     * Handles dialog creation and input validation
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return the dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_item_layout, null);

        // TODO add functionality for adding tags and images, the buttons are there!

        // ids for edittext fields
        itemNameBox = view.findViewById(R.id.item_name_text);
        itemMakeBox = view.findViewById(R.id.item_make_text);
        itemModelBox = view.findViewById(R.id.item_model_text);
        itemSerialNumBox = view.findViewById(R.id.serial_number_text);
        itemDateBox = view.findViewById(R.id.item_acquisition_date_text);
        itemValueBox = view.findViewById(R.id.item_value_text);
        itemCommentsBox = view.findViewById(R.id.item_comments_text);
        errorBox = view.findViewById(R.id.error_text_message);
        addImageBtn = view.findViewById(R.id.add_image_button);

        // addItemDialog builder code modified from this stackOverflow post
        //https://stackoverflow.com/questions/6275677/alert-dialog-in-android-should-not-dismiss
        final AlertDialog addItemDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .setTitle("Add Item")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK",
                    new Dialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int which) {
                            //Do nothing here. Override onClick() so we can do things when OK is tapped
                        }
                    }).create();

        // Open gallery to append photos to an item
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        // set a listener for the OK button
        addItemDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b1 = addItemDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkDataEntry()) {
                                addItemDialog.dismiss();
                                errorBox.setVisibility(View.GONE);
                            } else {
                                errorBox.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });

        return addItemDialog;
    }

    /**
     * Checks data entered in dialog
     * @return true if the data is valid, false otherwise
     */
    public boolean checkDataEntry() {
        String name = itemNameBox.getText().toString();
        String make = itemMakeBox.getText().toString();
        String serialNumber = itemSerialNumBox.getText().toString(); //fixme: No checking for this number yet
        String model = itemModelBox.getText().toString();
        String date = itemDateBox.getText().toString();
        String value = itemValueBox.getText().toString();
        String comments = itemCommentsBox.getText().toString();

        // check for empty fields
        if(name.equals("") || make.equals("") || model.equals("") || date.equals("") || value.equals("")) {
            errorBox.setText("Empty Fields");
            return false;
        }

        // check if value is parsable
        long newValue;
        try {
            newValue = Item.toValue(value);
        } catch (NumberFormatException e) {
            errorBox.setText("Invalid Value");
            return false;
        }

        // if this passes, then we know that our date string is in the right format to perform logic on later
        // does not except dates after current date or of invalid format (given in textView hint)
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
            LocalDate newDate = LocalDate.parse(date, formatter);
            LocalDate currentDate = LocalDate.now();
            if(newDate.isAfter(currentDate)) {
                errorBox.setText("Invalid Date");
                return false;
            }
        } catch (DateTimeParseException e) {
            errorBox.setText("Invalid Date");
            return false;
        }
        // create the item object
        ArrayList<String> imagePathsForUpload = new ArrayList<String>(imageMap.keySet());
        listener.onOKPressed(new Item(date, name, make, model, comments, newValue, serialNumber, imagePathsForUpload), imageMap);
        return true;
    }

    /**
     * interface for button pressed in dialog
     */
    public interface OnFragmentInteractionListener {
        void onRecyclerItemPressed(int position);
        void onOKPressed(Item item, Map<String, Uri> imageMap);
    }
}
