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

import com.lancius.palle2patnam.Database.Category;
import com.lancius.palle2patnam.Database.DatabaseHandler;
import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.CommonUtilities;
import com.lancius.palle2patnam.utils.Constants;
import com.lancius.palle2patnam.utils.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.lancius.palle2patnam.utils.Constants.session;
import static com.lancius.palle2patnam.utils.WebServices.PLACE_ORDER_URL;


/**
 * Created by madhu on 4/11/2017.
 */

public class OrderPayment extends AppCompatActivity {

    String type_of_payment = "0", total_weights, slot_date, slot_time, total_cart_price, total_items;
    TextView payment_no_of_orders_tv, payment_selected_date_tv, payment_selected_time_tv,
            payment_cart_amount_tv, payment_delivery_cahsrges_tv, payment_tax_tv, delivery_total_amount_cash_price;
    Button delivery_address_payment_btn, pay_onlinebtn, cash_on_deliverybtn;
    Toolbar toolbar;

    Bundle b;
    Double total_amount = 0.0, cart_amount = 0.0, delivery_caharge_amount = 0.0, tax_amount = 0.0;

    ArrayList<HashMap<String, String>> productsList;

    String cart_price, message, address, mobile, name, order_id, prod_names, user_id, product_id, product_quantity, product_price, items;

    private static final String TAG_SUCCESS = "success";
    static final String TAG_ORDER_ID = "order_id";

    ProgressBar progressBar;
    DatabaseHandler db;
    List<Category> contacts;
    Calendar c;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_payment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        db = new DatabaseHandler(getApplicationContext());

        contacts = db.getAllCategories();

        session = new SessionManager(getApplicationContext());
        productsList = new ArrayList<HashMap<String, String>>();

        progressBar = (ProgressBar) findViewById(R.id.order_progress_bar);
        progressBar.setVisibility(View.GONE);

        HashMap<String, String> user = session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);
        name = user.get(SessionManager.KEY_NAME);
        address = user.get(SessionManager.KEY_ADDRESS);
        mobile = user.get(SessionManager.KEY_PHONE);

        total_cart_price = user.get(SessionManager.KEY_CART_TOTAL_AMOUNT);
        cart_price = user.get(SessionManager.KEY_CART_PRODUCT_PRICE);
        product_id = user.get(SessionManager.KEY_CART_PRODUCT_ID);
        total_items = user.get(SessionManager.KEY_CART_PRODUCT_QTY);
        total_weights = user.get(SessionManager.KEY_CART_PRODUCT_WEIGHT);
        prod_names = user.get(SessionManager.KEY_CART_PRODUCT_NAMES);

        Log.d("CARTDATA`", "PRICE : " + cart_price + "ID : " + product_id + "QTY : " + total_items + "WEIGHTS : " + total_weights + "NAMES : " + prod_names + "TOTAL PRICE : " + total_cart_price);

        payment_no_of_orders_tv = (TextView) findViewById(R.id.payment_no_of_orders_tv);
        payment_selected_date_tv = (TextView) findViewById(R.id.payment_selected_date_tv);
        payment_cart_amount_tv = (TextView) findViewById(R.id.payment_cart_amount_tv);
        delivery_total_amount_cash_price = (TextView) findViewById(R.id.delivery_total_amount_cash_price);

        delivery_address_payment_btn = (Button) findViewById(R.id.delivery_address_payment_btn);
        pay_onlinebtn = (Button) findViewById(R.id.pay_onlinebtn);
        cash_on_deliverybtn = (Button) findViewById(R.id.cash_on_deliverybtn);

        pay_onlinebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay_onlinebtn.setBackgroundResource(R.color.colorAccent);
                cash_on_deliverybtn.setBackgroundResource(R.color.ash);
                type_of_payment = "1";
            }
        });

        cash_on_deliverybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay_onlinebtn.setBackgroundResource(R.color.ash);
                cash_on_deliverybtn.setBackgroundResource(R.color.colorAccent);
                type_of_payment = "2";
            }
        });

        cart_amount = Double.parseDouble(total_cart_price);

        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a");
        String dateString = sdf.format(date);

        payment_no_of_orders_tv.setText("" + contacts.size());
        payment_selected_date_tv.setText(dateString);
        payment_cart_amount_tv.setText("₹ " + total_cart_price);


        delivery_total_amount_cash_price.setText("₹ " + cart_amount);

        delivery_address_payment_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (CommonUtilities.checkConn(getApplicationContext())) {

                    new PlaceOrderCodDetails().execute();

                } else {

                    Constants.intent = new Intent(getApplicationContext(),
                            NoNetworkActivity.class);
                    startActivity(Constants.intent);
                }


            }
        });

    }

    class PlaceOrderCodDetails extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog'5 9+\789+\789+
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar = (ProgressBar) findViewById(R.id.order_progress_bar);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(false);

        }

        /**
         * Creating Application
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("product_id", product_id));
            params.add(new BasicNameValuePair("product_quantity", total_items));
            params.add(new BasicNameValuePair("order_total", total_cart_price));
            params.add(new BasicNameValuePair("product_price", cart_price));
            params.add(new BasicNameValuePair("product_weight", total_weights));
            params.add(new BasicNameValuePair("product_name", prod_names));
            params.add(new BasicNameValuePair("address", address));
            params.add(new BasicNameValuePair("name", name));
            params.add(new BasicNameValuePair("mobile", mobile));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = null;
            try {
                try {

                    json = Constants.jsonParser.makeHttpRequest(PLACE_ORDER_URL, "POST",
                            params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }

                // check log cat fro response
                // Log.d("Payment Option Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {

                        order_id = json.getString(TAG_ORDER_ID);

//                        db.deleteAll();

                        Constants.intent = new Intent(getApplicationContext(), OrderSuccessActivity.class);
                        Constants.intent.putExtra("OrderId", order_id);
                        Constants.intent.putExtra("Items", "" + contacts.size());
                        Constants.intent.putExtra("Amount", total_cart_price);
                        startActivity(Constants.intent);

                    } else {
                        // failed to create product
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
                    Toast.makeText(OrderPayment.this, message,
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done

            progressBar.setVisibility(View.GONE);

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

