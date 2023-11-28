package com.example.breadheadsinventorymanager;


import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static java.lang.Long.parseLong;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import android.net.Uri;
import android.os.Bundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    ImageButton addImageBtn;
    ImageButton takePhotoBtn;

    Button addTagBtn;
    String imagePathGlobal;
    Button removeTagBtn;

    // used for camera usage
    private ActivityResultLauncher<Intent> activityResultLauncher;

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

        // check if camera is available, if so, get image taken and add it to image hashmap
        // converting the bitmap into a Uri is from user Uzzam Altaf's stackOverflow post which is modified to suit our needs
        https://stackoverflow.com/questions/8295773/how-can-i-transform-a-bitmap-into-a-uri
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");

                    // create a temporary file, populate it with bitmap data, flush it, close it and call it a day
                    File tempFile = null;
                    try {
                        tempFile = File.createTempFile("temprentpk", ".png");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    byte[] bitmapData = bytes.toByteArray();
                    FileOutputStream fileOutPut = null;
                    // obligatory try catch statements for file handling
                    try {
                        fileOutPut = new FileOutputStream(tempFile);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        fileOutPut.write(bitmapData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        fileOutPut.flush();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        fileOutPut.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Uri uri =  Uri.fromFile(tempFile);

                    // create key for map
                    String imagePath = "images/" + UUID.randomUUID().toString();
                    imageMap.put(imagePath, uri);

                    // call function to check if it has a serialnumber
                    setSerialNumberFromImage(bitmap);
                }
            }
        });
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

        takePhotoBtn = view.findViewById(R.id.take_photo_button);
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

        // Take photo from system camera
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // launch camera intent
                activityResultLauncher.launch(intent);
//                setSerialNumberFromImage(imagePathGlobal);
                }
            });

        // Open gallery to append photos to an item
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mGetContent.launch("image/*");
//                setSerialNumberFromImage(imagePathGlobal);
            }
        });

        // add tag/s need a check box here
        addTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the tag selection dialog
                TagList globalTagList = ((MainActivity) getActivity()).getGlobalTagList();

                TagSelectionDialog.show_selected(getContext(), selectedTags, globalTagList, (dialog, which) -> {
                    // Handle Confirm button click if needed
                    Log.d("TagSelection", "Selected Tags: " + selectedTags);
                });

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
        listener.onOKPressed(new Item(date, name, make, model, comments, newValue, serialNumber, imagePathsForUpload,selectedTags), imageMap);
        return true;
    }

    /**
     * interface for button pressed in dialog
     */
    public interface OnFragmentInteractionListener {
        void onRecyclerItemPressed(int position);
        void onOKPressed(Item item, Map<String, Uri> imageMap);
    }

    /**
     *
     * Function to read the serial number from an image and set the serial number based off the image
     * @param bitmap
     */
    private void setSerialNumberFromImage(Bitmap bitmap){
        InputImage image;

        // turn the bitmap into an image
        image = InputImage.fromBitmap(bitmap, 0);

        // initialize text recognizer
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        Task <Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text texts) {
                                // Task completed successfully
                                String textFound = "";
//                                processTextRecognitionResult(texts)
                                List<Text.TextBlock> blocks = texts.getTextBlocks();
                                if (blocks.size() == 0) {
                                    // no text found
                                    return;
                                }
//                                itemSerialNumBox.setText("Success");
                                // all text found is in one block
                                for (int i = 0; i < blocks.size(); i++) {
                                    // within the block are each of the lines found
                                    List<Text.Line> lines = blocks.get(i).getLines();
                                    for (int j = 0; j < lines.size(); j++) {
//                                        itemSerialNumBox.setText(lines.get(j).getText());
                                        textFound = textFound + lines.get(j).getText();
                                    }
                                }
                                itemSerialNumBox.setText(textFound);
                            }
                        });

//        String resultText = result.getText();
//        for (Text.TextBlock block : result.getTextBlocks()) {
//            String blockText = block.getText();
//            Point[] blockCornerPoints = block.getCornerPoints();
//            Rect blockFrame = block.getBoundingBox();
//            for (Text.Line line : block.getLines()) {
//                String lineText = line.getText();
//                itemSerialNumBox.setText(lineText);
//            }
//        }
    }
    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            // no text found
            return;
        }
        // all text found is in one block
        for (int i = 0; i < blocks.size(); i++) {
            // within the block are each of the lines found
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();

            }
        }
    }

}
