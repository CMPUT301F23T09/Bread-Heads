package com.example.breadheadsinventorymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * Activity to sign in a user.
 * Some code adapted from <a href="https://developers.google.com/identity/sign-in/android/sign-in">...</a>
 *
 * @version 1
 */
public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    private Authenticator auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        // disable the back button - users must sign in first to use app
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        auth = new Authenticator(FirebaseAuth.getInstance(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.google_auth_button).setOnClickListener(this);
    }

    /**
     * Handles clicking the sign-in button.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.google_auth_button) {
            auth.signIn(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    /**
     * Handles the result of a task that attempted to sign in account
     * @param completedTask The task to be handled
     */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            returnToMain(completedTask.getResult(ApiException.class));
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("USER_ACTIVITY", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    /**
     * Returns to the main activity with a signed-in account
     * @param account The signed-in user account
     */
    private void returnToMain(GoogleSignInAccount account) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("account", account);
        this.startActivity(intent);
    }
}
