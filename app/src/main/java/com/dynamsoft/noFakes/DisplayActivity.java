package com.dynamsoft.noFakes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayActivity extends AppCompatActivity {
    TextView productCodeTxt, productDateCreatedTxt,
            productExpiryDateTxt,productDescription;
    FloatingActionButton scanButton;
    final Context context = this;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    /*Method initialize()
     *initializes all the class variables
     *and add action to scan fab button
     * to return to the BarcodeActivity
     * to scan another item
     */
    public void initialize() {
        bundle = getIntent().getExtras();
        String productCode = bundle.getString("code");
        String dateCreated = bundle.getString("date");
        String expiryDate = bundle.getString("expiry");
        String description =bundle.getString("desc");
        productCodeTxt = (TextView) findViewById(R.id.textProductCodeResult);
        productCodeTxt.setText(productCode);
        productDateCreatedTxt = (TextView) findViewById(R.id.textDateCreatedResult);
        productDateCreatedTxt.setText(dateCreated);
        productExpiryDateTxt = (TextView) findViewById(R.id.textExpiryDateResult);
        productExpiryDateTxt.setText(expiryDate);
        productDescription =(TextView)findViewById(R.id.textDescriptionResult);
        productDescription.setText(description);
        scanButton = (FloatingActionButton) findViewById(R.id.scanBtn);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanIntent = new Intent(getApplicationContext(), BarcodeReader.class);
                 startActivity(scanIntent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        Inflate the menu; this adds items to the
        action bar if it is present.
        */
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Handle action bar item clicks here. The action bar will
           automatically handle clicks on the Home/Up button, so long
           as you specify a parent activity in AndroidManifest.xml.
        */
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent aboutIntent = new Intent(this,About.class);
            startActivity(aboutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /*Method exitApp()
     *initiates a dialog box
     *before exiting the app
    */
    public void exitApp() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.appexit_dailog);
        dialog.setTitle("NoFakes");
        dialog.setCanceledOnTouchOutside(false);
        Button dialogButtonOk = (Button) dialog
                .findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("static-access")
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DisplayActivity.this.finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Button dialogCancel = (Button) dialog
                .findViewById(R.id.dialogButtonCancel);
        dialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }
}
