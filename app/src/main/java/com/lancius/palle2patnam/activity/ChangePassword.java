package com.lancius.palle2patnam.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.CommonUtilities;
import com.lancius.palle2patnam.utils.JsonParser;
import com.lancius.palle2patnam.utils.SessionManager;
import com.lancius.palle2patnam.utils.WebServices;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by madhu on 5/15/2017.
 */

public class ChangePassword extends AppCompatActivity {

    Toolbar toolbar;
    SessionManager session;
    String userid, message, oldpassword, newpassword, confirmpassword;
    ProgressDialog pDialog;

    JsonParser jsonParser = new JsonParser();

    private static final String TAG_SUCCESS = "success";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();
        userid = user.get(SessionManager.KEY_ID);

        Button submitBtn = (Button) findViewById(R.id.change_pwd_submit_btn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText oldpass = (EditText) findViewById(R.id.input_old_password);
                EditText newpass = (EditText) findViewById(R.id.input_new_password);
                EditText confirmnew = (EditText) findViewById(R.id.input_confirm_password);


                if (oldpass.length() == 0 || newpass.length() == 0
                        || confirmnew.length() == 0) {
                    Snackbar.make(view, "Please fill all the fields", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    if (newpass.getText().toString().trim()
                            .matches(confirmnew.getText().toString().trim())) {

                        newpassword = newpass.getText().toString().trim();
                        confirmpassword = confirmnew.getText().toString().trim();

                        if (CommonUtilities.checkConn(getApplicationContext())) {

                            new sendDetails().execute();

                        } else {

                            Intent i = new Intent(getApplicationContext(),
                                    NoNetworkActivity.class);
                            startActivity(i);

                        }

                    } else {

                        Snackbar.make(view, "New and Confirm Passwords should be same.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                }
            }
        });

    }

    class sendDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ChangePassword.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        /**
         * Creating Application
         */
        @SuppressWarnings("deprecation")
        protected String doInBackground(String... args) {


            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("opassword", oldpassword));
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("npassword", newpassword));

            JSONObject json = null;

            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.CHANGE_PASSWORD_URL,
                            "POST", params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                try {

                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {

                        message = json.getString("message");
                        showToast();

                        session.logoutUser();

                        finish();

                    } else {
                        message = json.getString("message");
                        showToast();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (StringIndexOutOfBoundsException e) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        message = "Server Error..Please Try Again Later..";
                        showToast();
                    }
                });
            }

            return null;
        }

        private void showToast() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ChangePassword.this, message,
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
        }
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
