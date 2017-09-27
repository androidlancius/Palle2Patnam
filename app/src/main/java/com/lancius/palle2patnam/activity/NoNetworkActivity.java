package com.lancius.palle2patnam.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.lancius.palle2patnam.R;


public class NoNetworkActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog pDialog;
    ImageView retryIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nonetwork);

        retryIcon = (ImageView) findViewById(R.id.no_internet_retry_img);
        retryIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == retryIcon) {
            //Calling signin
            tryAgain(v);
        }

    }

    public void closeView(View v) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    public void settings(View v) {

        startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
    }

    public void tryAgain(View v) {

        pDialog = new ProgressDialog(NoNetworkActivity.this);
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                checkNetwork();
                if (wifiDataAvailable == true || mobileDataAvailable == true) {

                    pDialog.dismiss();
                    finish();
                }
                pDialog.dismiss();

            }
        }, 5000);


    }

    boolean wifiDataAvailable = false;
    boolean mobileDataAvailable = false;

    private boolean checkNetwork() {

        wifiDataAvailable = false;
        mobileDataAvailable = false;

        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = conManager.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    wifiDataAvailable = true;
            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    mobileDataAvailable = true;
        }

        return wifiDataAvailable || mobileDataAvailable;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();

    }

}
