package com.example.breadheadsinventorymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.breadheadsinventorymanager.Tag;

import java.util.Map;
import java.util.UUID;

public class AddTagFragment extends DialogFragment {

    // Remove the listener declaration
    private AddTagFragment.OnFragmentInteractionListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddItemFragment.OnFragmentInteractionListener) {
            listener = (AddTagFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context + "OnFragmentInteractionListener is not implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.add_tag_layout, null);

        final EditText tagNameEditText = view.findViewById(R.id.tag_name_text);

        AlertDialog addTagDialog = new AlertDialog.Builder(getContext())
                .setTitle("Add Tag")
                .setView(view)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tagName = tagNameEditText.getText().toString();
                        if (!tagName.isEmpty()) {
                            // Create a Tag object
                            Tag tag = new Tag(tagName);
                            listener.onOKPressed(tag);

                        }
                    }
                })
                .create();

        return addTagDialog;
    }

    /**
     * interface for button pressed in dialog
     */
    public interface OnFragmentInteractionListener {
        void onOKPressed(Tag tag);
    }


}
