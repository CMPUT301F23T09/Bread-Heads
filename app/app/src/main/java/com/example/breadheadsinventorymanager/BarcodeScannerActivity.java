package com.example.breadheadsinventorymanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;


/**
 * Activity to handle scanning a barcode and using associated data to create an item
 */
public class BarcodeScannerActivity extends AppCompatActivity {

    /**
     * Creates the Activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //Toast.makeText(this.getApplicationContext(), "We are in the new activity!", Toast.LENGTH_SHORT).show();
        Log.d("h1", "We are in a new activity!");

        scanCode();

        // check in case something is returned

    }

    /**
     * Initialize Barcode scanner
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    // do stuff when we scan a barcode
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            // setup toast to test
            Toast.makeText(getApplicationContext(), result.getContents(), Toast.LENGTH_LONG).show();
            // TODO pop up the edit item activity with the contents of barcode contents
        }
    });
}
