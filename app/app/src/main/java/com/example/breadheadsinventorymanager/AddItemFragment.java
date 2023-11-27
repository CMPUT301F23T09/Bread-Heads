package com.example.breadheadsinventorymanager;



import android.util.Log;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

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
    EditText itemBarcodeBox;

    TextView errorBox;
    // Buttons
    com.google.android.material.floatingactionbutton.FloatingActionButton addImageBtn;
    Button scanBarcodeBtn;
    Button addTagBtn;
    Button removeTagBtn;

    private String barcode;

    private ActivityResultLauncher<String> mGetContent;
    private Map<String, Uri> imageMap = new HashMap<String, Uri>();
    private OnFragmentInteractionListener listener;

    private List<String> selectedTags = new ArrayList<>();


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
        itemBarcodeBox = view.findViewById(R.id.item_barcode_text);

        errorBox = view.findViewById(R.id.error_text_message);
        scanBarcodeBtn = view.findViewById(R.id.scan_barcode_button);
        addImageBtn = view.findViewById(R.id.add_image_button);
        addTagBtn = view.findViewById(R.id.add_tag);
        removeTagBtn = view.findViewById(R.id.remove_tag);


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

        // get barcode if a valid barcode is scanned
        ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() != null) {
                barcode = result.getContents();
                queryDbForBarcode(barcode);
                itemBarcodeBox.setText(barcode);
                // TODO pop up the edit item activity with the contents of barcode contents
            }
        });

        // Scan a barcode and fill editText field with said barcode
        scanBarcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // scan barcode
                ScanOptions options = new ScanOptions();
                options.setPrompt("Volume up to flash on");
                options.setBeepEnabled(true);
                options.setOrientationLocked(false);
                options.setCaptureActivity(CaptureAct.class);
                barLauncher.launch(options);
            }
        });


        // Open gallery to append photos to an item
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        // add tag/s need a check box here
        addTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the tag selection dialog
                showTagSelectionDialog();
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
     * Queries the Firestore database for existing barcodes, if one exists, get the Item data associated with it
     * @param barcode the barcode to check
     * @return true if the barcode exists, false otherwise
     */
    private void queryDbForBarcode(String barcode) {
        CollectionReference collection = FirestoreInteract.getItemDB();

        // query barcode against matching barcode within database
        collection.whereEqualTo("barcode", barcode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document: task.getResult()) {
                                Map<String, Object> item = document.getData();
                                populateFields(item);
                            }
                        } else {
                            Log.d("FireStoreInteract.java", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    /**
     * Populates edittext fields with item data associated with matching barcode
     */
    private void populateFields(Map<String, Object> dbItem) {
        itemNameBox.setText(dbItem.get("description").toString());
        itemMakeBox.setText(dbItem.get("make").toString());
        itemModelBox.setText(dbItem.get("model").toString());
        itemSerialNumBox.setText(dbItem.get("serialNum").toString());
        itemDateBox.setText(dbItem.get("date").toString());
        itemValueBox.setText(dbItem.get("value").toString());
        itemCommentsBox.setText(dbItem.get("comment").toString());

        String dbUri = dbItem.get("imageUris").toString();
        // prepare uri for string copying as the database item is driving me nuts and is not iterable
        if (dbUri != null) {
            dbUri = dbUri.substring(1, dbUri.length()-1);
            Log.d("h2", dbUri);
            int size = 1;
            // get size as dbItem.get is not iterable
            for (int i = 0; i < dbUri.length(); i++) {
                if (dbUri.charAt(i)== ',') {
                    size++;
                }
            }
            imageMap.clear();
            String[] uriArray = dbUri.split(", ", size);
            for ( int i = 0; i < uriArray.length; i++) {
                Log.d("h2", uriArray[i]);
            }
            for(int j = 0; j < uriArray.length; j++) {
                // don't need database version of imagePath, just need to generate our own
                String imagePath = "images/" + UUID.randomUUID().toString();
                Uri uri = Uri.parse(uriArray[j]);
                imageMap.put(imagePath, uri);

            }

        }

        //imageMap.clear();
        //imageMap.put(imagePath, imageUri);

        //selectedTags.clear();
       // String classString = dbItem.get("tags").getClass().toString();
       // List<HashMap<String, Object>> tagsList = (List<HashMap<String, Object>>) dbItem.get("tags");

        //TODO find a way to convert the tags in the hashmap into a string
        /*for(int i = 0; i < tagsList.size(); i++) {
            //Log.d("h2", tags.get(i).getClass().toString());
            BiConsumer<? super String, ? super Object> k = null;
            Object v = null;
            //HashMap<String, Object> tag = tagsList.get(i);
            //tag.forEach(key, value) ->
        }*/


        //Log.d("h2", tagStrings.toString());
        //Log.d("h2", classString);

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
        ArrayList<Uri> imageUrisForUpload = new ArrayList<Uri>(imageMap.values());
        listener.onOKPressed(new Item(date, name, make, model, comments, newValue, serialNumber, imagePathsForUpload, imageUrisForUpload, selectedTags, barcode), imageMap);
        return true;
    }

    /**
     * interface for button pressed in dialog
     */
    public interface OnFragmentInteractionListener {
        void onRecyclerItemPressed(int position);
        void onOKPressed(Item item, Map<String, Uri> imageMap);
    }
    private void showTagSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());

        // Inflate the custom layout
        View dialogView = inflater.inflate(R.layout.taglist_dialog, null);
        builder.setView(dialogView);

        // Get the container for checkboxes
        LinearLayout tagListContainer = dialogView.findViewById(R.id.tagListContainer);

        // Get the global tag list
        TagList globalTagList = ((MainActivity) getActivity()).getGlobalTagList();

        // Create checkboxes for each tag
        for (Tag tag : globalTagList) {
            CheckBox checkBox = new CheckBox(getContext());
            checkBox.setText(tag.getTag());
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

        builder.setPositiveButton("Confirm", (dialog, which) -> {
            // Handle Confirm button click if needed
            Log.d("TagSelection", "Selected Tags: " + selectedTags);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Clear the selectedTags list when "Cancel" is pressed
            selectedTags.clear();
        });

        // Show the dialog
        builder.show();
    }
}
