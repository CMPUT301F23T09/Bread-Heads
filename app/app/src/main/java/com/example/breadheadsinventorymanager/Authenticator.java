package com.example.breadheadsinventorymanager;

import static androidx.core.app.ActivityCompat.startActivityForResult;
import static androidx.core.app.ActivityCompat.startIntentSenderForResult;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * Handles Google authentication via Firebase.
 * Some code adapted from <a href="https://developers.google.com/identity/sign-in/android/sign-in">...</a>
 *
 * @version 1
 */
public class Authenticator {
    private FirebaseAuth firebaseAuth;
    GoogleSignInClient signInClient;

    /**
     * Constructs an Authenticator from a giving FirebaseAuth instance.
     * @param firebaseAuth The FirebaseAuth instance
     * @param activity The current activity
     */
    protected Authenticator(FirebaseAuth firebaseAuth, Activity activity) {
        this.firebaseAuth = firebaseAuth;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .build();
        signInClient = GoogleSignIn.getClient(activity, gso);
    }

    /**
     * Attempt to sign in via Google authentication
     * @param activity The current activity
     */
    public void signIn(Activity activity) {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(activity, signInIntent, 1, null);
    }

    /**
     * Attempt to sign out of Google authentication
     * @param activity The current activity
     */
    public void signOut(Activity activity) {
        // need to use the application context, not activity context, to sign out
        GoogleSignInClient signOutClient = GoogleSignIn
                .getClient(activity.getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        signOutClient.signOut();
        signOutClient.revokeAccess();
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public GoogleSignInClient getSignInClient() {
        return signInClient;
    }
}
