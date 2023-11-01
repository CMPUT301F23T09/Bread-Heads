package com.example.breadheadsinventorymanager;

import static java.lang.Long.parseLong;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import java.time.LocalDate;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class AddItemFragment extends DialogFragment {

    // editText ids
    EditText itemNameBox;
    EditText itemModelBox;
    EditText itemMakeBox;
    EditText itemDateBox;
    EditText itemCommentsBox;
    EditText itemValueBox;

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
    }

    /**
     * Handles dialog creation and input validation
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return nothing if input does not pass checks
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
        itemDateBox = view.findViewById(R.id.item_acquisition_date_text);
        itemValueBox = view.findViewById(R.id.item_value_text);
        itemCommentsBox = view.findViewById(R.id.item_comments_text);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setTitle("Add Item")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", (dialog, which) -> {
                    String name = itemNameBox.getText().toString();
                    String make = itemModelBox.getText().toString();
                    String model = itemModelBox.getText().toString();
                    String date = itemDateBox.getText().toString();
                    String value = itemValueBox.getText().toString();
                    String comments = itemCommentsBox.getText().toString();
                    BigInteger bigInt = new BigInteger(value);
                    long newValue;

                    // create date stuff to check entered date
                    //LocalDate currentDate = LocalDate.now();
                    // check for empty fields
                    if(name.equals("") || make.equals("") || model.equals("") || date.equals("") || value.equals("")) {
                        return;
                    } else if (bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                        // checks for input greater than max number possible
                        // code modified from this stackoverflow post
                        //https://stackoverflow.com/questions/36147202/how-to-detect-if-a-number-is-greater-than-long-max-value
                        return;
                    } else {
                        newValue = parseLong(value);
                    }
                    // TODO validate date is not from the future and in correct format

                    listener.onOKPressed(new Item(name, make, model, date, comments, newValue));
                }).create();
    }

    public interface OnFragmentInteractionListener {
        void onOKPressed(Item item);
    }
}
