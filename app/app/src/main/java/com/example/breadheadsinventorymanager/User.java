package com.example.breadheadsinventorymanager;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.CollectionReference;

import java.util.HashMap;

/**
 * Represents a User that can be put onto Firestore.
 * Uses the user's ID retrieved from the GoogleSignInAccount system.
 * Technically insecure; authentication isn't handled here, but rather in the Firestore database
 * permission settings (so the exact specifics of the validation system depend on the needs
 * of the user). For the project submission, no security measures are in place to ensure testing
 * TAs don't encounter any roadblocks when trying to access the database.
 */
public class User implements FirestorePuttable {
    GoogleSignInAccount account;
    String id;

    /**
     * Create a user from an account.
     * @param account A GoogleSignInAccount to retrieve the account and ID from.
     */
    protected User(GoogleSignInAccount account) {
        this.account = account;
        this.id = account.getId();
    }

    @Override
    public HashMap<String, Object> formatForFirestore() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", account.getDisplayName());
        map.put("email", account.getEmail());
        return map;
    }

    @Override
    public void put(CollectionReference collection) {
        // pass
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }
}
