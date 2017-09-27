package com.lancius.palle2patnam.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.CommonUtilities;
import com.lancius.palle2patnam.utils.Constants;
import com.lancius.palle2patnam.utils.SessionManager;
import com.lancius.palle2patnam.utils.WebServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lancius on 9/15/2017.
 */

public class MyAccount extends AppCompatActivity {

    String message, userid, name, address, email, mobile;
    SessionManager session;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESULTS = "lists";
    static final String TAG_F_NAME = "first_name";
    static final String TAG_L_NAME = "last_name";
    static final String TAG_EMAIL = "email";
    static final String TAG_MOBILE = "mobile";

    public static final String TAG_ADDRESS = "address";
    static final String TAG_ADDRESS_NAME = "name";

    JSONArray products;
    int success;
    Intent i;

    Toolbar toolbar;
    ProgressBar pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        userid = user.get(SessionManager.KEY_ID);
        name = user.get(SessionManager.KEY_NAME);
        mobile = user.get(SessionManager.KEY_PHONE);
        email = user.get(SessionManager.KEY_EMAIL);
        address = user.get(SessionManager.KEY_ADDRESS);

        TextView nameTv = (TextView) findViewById(R.id.profile_name);
        TextView emailTv = (TextView) findViewById(R.id.profile_email);
        TextView mobileTv = (TextView) findViewById(R.id.profile_mobile);
        TextView addressTv = (TextView) findViewById(R.id.profile_address);

        nameTv.setText(name);
        emailTv.setText(email);
        mobileTv.setText(mobile);
        addressTv.setText(address);
        
        Button changePwdBtn = (Button) findViewById(R.id.change_pwd_btn);
        changePwdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                i = new Intent(getApplicationContext(),
                        ChangePassword.class);

                startActivity(i);

            }
        });

        Button logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                session.logoutUser();
                finish();

            }
        });


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
