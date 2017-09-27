package com.lancius.palle2patnam.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.CommonUtilities;
import com.lancius.palle2patnam.utils.Constants;
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

public class ForgotPassword extends AppCompatActivity {

    EditText mobileEt;
    ProgressDialog pDialog;
    String message;
    JsonParser jsonParser = new JsonParser();
    String mobile, user_id, password;

    private Toolbar toolbar;

    int success;

    private static final String TAG_SUCCESS = "success";
    TextView btnOkPopup, statusTxt;

    Dialog pwindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // getSupportActionBar().setTitle("Forgot Password");

        Constants.session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = Constants.session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);

        TextView submitBtn = (TextView) findViewById(R.id.forgot_btn_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mobileEt = (EditText) findViewById(R.id.forgot_password_email);
                mobile = mobileEt.getText().toString().trim();

                if (mobileEt.getText().toString().trim().length() == 10) {

                    if (CommonUtilities.checkConn(getApplicationContext())) {

                        new sendDeatils().execute();

                    } else {

                        Constants.intent = new Intent(getApplicationContext(),
                                NoNetworkActivity.class);
                        startActivity(Constants.intent);
                    }


                } else {

                    Snackbar.make(view, "Please enter valid Mobile Number..", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }
            }
        });
    }

    class sendDeatils extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPassword.this);
            pDialog.setMessage("Sending Information...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating Application
         */
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobile", mobile));
            JSONObject json = null;

            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.FORGOT_PASSWORD_URL, "POST",
                            params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                try {

                    success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {

                        password = json.getString("password");
                        message = json.getString("message");


                    } else {

                        message = json.getString("message");
                        showToast();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (
                    StringIndexOutOfBoundsException e)

            {

                runOnUiThread(new Runnable() {
                    public void run() {
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
                    Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();

            if (success == 0) {
                showAlertDialog(password);
            } else {
                showAlertDialog(message);
            }


        }

    }


    private void showAlertDialog(String message) {

        Context mContext = ForgotPassword.this;

        pwindow = new Dialog(mContext);
        pwindow.setCanceledOnTouchOutside(false);
        pwindow.setCancelable(false);
        pwindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pwindow.setContentView(R.layout.forgot_password_popup);
        pwindow.show();

        this.message = message;

        statusTxt = (TextView) pwindow.findViewById(R.id.status_popup_tv);
        statusTxt.setText(message);

        btnOkPopup = (TextView) pwindow.findViewById(R.id.status_ok_button);
        btnOkPopup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                pwindow.dismiss();

                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
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
