package com.example.breadheadsinventorymanager;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * Activity to view or log out a currently-logged-in user.
 * Some code adapted from <a href="https://developers.google.com/identity/sign-in/android/sign-in">...</a>
 *
 * @version 1
 */
public class UserLoggedInActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInAccount account;
    private Authenticator auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_logged_in);
        account = getIntent().getParcelableExtra("account");
        // enable the back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        populateProfileInfo();

        auth = new Authenticator(FirebaseAuth.getInstance(), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.sign_out_button).setOnClickListener(this);
    }

    /**
     * Fills the screen with info retrieved from the user's account
     */
    protected void populateProfileInfo() {
        TextView nameView = findViewById(R.id.profile_name);
        TextView emailView = findViewById(R.id.profile_email);
        ImageView imageView = findViewById(R.id.profile_picture);

        nameView.setText(account.getDisplayName());
        emailView.setText(account.getEmail());
        Picasso.get().load(account.getPhotoUrl()).into(imageView);
    }

    /**
     * Handles the top bar to enable the back button.
     * @param item The menu item that was selected.
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles clicking the sign-out button.
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_out_button) {
            auth.signOut(this);
            this.startActivity(new Intent(this, UserActivity.class));
        }
    }
}
