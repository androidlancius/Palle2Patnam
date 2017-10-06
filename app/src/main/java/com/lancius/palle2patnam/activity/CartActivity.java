package com.lancius.palle2patnam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lancius.palle2patnam.Database.Category;
import com.lancius.palle2patnam.Database.DatabaseHandler;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    String id = "", price, weight = "", quantity, image = "",
            message, user_id, product_id, product_name, items, selected_price;
    ListView listview;
    TextView total_price_tv;
    ImageView proceed_to_checkout_image;
    ArrayList<HashMap<String, String>> productsList;
    CustomerListAdapter listadapter;
    JSONArray products = null;
    String total_price_cart;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_RESULTS = "lists";
    static final String TAG_ID = "id";
    static final String TAG_PRODUCT_ID = "id";
    static final String TAG_PRODUCT_NAME = "product_name";
    static final String TAG_QUANTITY = "quantity";
    static final String TAG_PRICE = "price";
    static final String TAG_IMAGE = "image";
    static final String TAG_WEIGHT = "weight";
    static final String TAG_ITEMS = "items";

    String prod_id, weight_count, qty, price_count, prod_names, from;
    static ArrayList<String> categoryIdList, categoryCountList, categoryPriceList, categoryWeightList, categoryNamesList, weightList;
    Double final_price = 0.00;
    private Toolbar toolbar;
    DatabaseHandler db;
    SessionManager session;
    int success;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_list_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new SessionManager(getApplicationContext());

        db = new DatabaseHandler(getApplicationContext());

        productsList = new ArrayList<HashMap<String, String>>();

        categoryCountList = new ArrayList<>();
        categoryIdList = new ArrayList<>();
        categoryPriceList = new ArrayList<>();
        categoryWeightList = new ArrayList<>();
        categoryNamesList = new ArrayList<>();

        Constants.session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = Constants.session.getUserDetails();
        user_id = user.get(SessionManager.KEY_ID);
        total_price_cart = user.get(SessionManager.KEY_TOTAL_AMOUNT);
        from = user.get(SessionManager.KEY_TO_CART);

        Log.d("user_id", user_id);

        final List<Category> contacts = db.getAllCategories();

        TextView total_items_tv = (TextView) findViewById(R.id.checkout_total_items);
        total_items_tv.setText("" + contacts.size() + " Item(s)");

        for (Category cn : contacts) {

            String catid = cn.getCatId();
            categoryIdList.add(catid);
            String qty = cn.getQty();
            categoryCountList.add(qty);
            String price = cn.getPrice();
            categoryPriceList.add(price);
            String weight = cn.getWeight();
            categoryWeightList.add(weight);
        }


        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < categoryCountList.size(); i++) {
            if (categoryCountList.size() - 1 == i) {
                sb.append(categoryCountList.get(i));
            } else {
                sb.append(categoryCountList.get(i));
                sb.append(",");
            }
        }

        qty = sb.toString();
        Log.d("QTY", sb.toString());

        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < categoryPriceList.size(); i++) {
            if (categoryPriceList.size() - 1 == i) {
                sb2.append(categoryPriceList.get(i));
            } else {
                sb2.append(categoryPriceList.get(i));
                sb2.append(",");
            }
        }

        price_count = sb2.toString();
        Log.d("PRICES", sb2.toString());

        StringBuilder sb3 = new StringBuilder();
        for (int i = 0; i < categoryWeightList.size(); i++) {
            if (categoryWeightList.size() - 1 == i) {
                sb3.append(categoryWeightList.get(i));
            } else {
                sb3.append(categoryWeightList.get(i));
                sb3.append(",");
            }
        }

        weight_count = sb3.toString();
        Log.d("WEGHTS", sb3.toString());

        StringBuilder sb1 = new StringBuilder();
        for (int i = 0; i < categoryIdList.size(); i++) {

            if (categoryIdList.size() - 1 == i) {
                sb1.append(categoryIdList.get(i));
            } else {
                sb1.append(categoryIdList.get(i));
                sb1.append(",");
            }
        }

        Log.d("CATGID", sb1.toString());
        prod_id = sb1.toString();


        if (CommonUtilities.checkConn(getApplicationContext())) {

            new gettingCartDetails().execute();

        } else {

            Constants.intent = new Intent(getApplicationContext(),
                    NoNetworkActivity.class);
            startActivity(Constants.intent);
        }

        proceed_to_checkout_image = (ImageView) findViewById(R.id.delivery_address);
        proceed_to_checkout_image.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                StringBuilder sb4 = new StringBuilder();
                for (int i = 0; i < categoryNamesList.size(); i++) {
                    if (categoryNamesList.size() - 1 == i) {
                        sb4.append(categoryNamesList.get(i));
                    } else {
                        sb4.append(categoryNamesList.get(i));
                        sb4.append(",");
                    }
                }
                prod_names = sb4.toString();
                Log.d("PRODNAMES", sb4.toString());

                Intent i = new Intent(getApplicationContext(),
                        OrderPayment.class);
                session.storeCartDetails(price_count, prod_id, qty, weight_count, prod_names, "" + final_price);
                startActivity(i);

            }
        });

    }

    class gettingCartDetails extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBar = (ProgressBar) findViewById(R.id.cart_progress_bar);
            progressBar.setIndeterminate(false);

        }

        /**
         * Creating Application
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("user_id", user_id));
            params.add(new BasicNameValuePair("product_id", prod_id));
            params.add(new BasicNameValuePair("product_quantity", qty));
            params.add(new BasicNameValuePair("product_price", price_count));
            params.add(new BasicNameValuePair("product_weight", weight_count));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = null;
            try {
                try {

                    json = Constants.jsonParser.makeHttpRequest(WebServices.CART_VIEW, "POST",
                            params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }

                // check log cat fro response
                // Log.d("Cart Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {

                        products = json.getJSONArray(TAG_RESULTS);

                        productsList.clear();

                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);

                            id = c.getString(TAG_ID);
                            product_id = c.getString(TAG_PRODUCT_ID);
                            product_name = c.getString(TAG_PRODUCT_NAME);
                            quantity = c.getString(TAG_QUANTITY);
                            price = c.getString(TAG_PRICE);
                            weight = c.getString(TAG_WEIGHT);
                            items = c.getString(TAG_ITEMS);
                            image = c.getString(TAG_IMAGE);

                            categoryNamesList.add(product_name);
                            categoryIdList.add(product_id);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_ID, id);
                            map.put(TAG_PRODUCT_ID, product_id);
                            map.put(TAG_PRODUCT_NAME, product_name);
                            map.put(TAG_QUANTITY, quantity);
                            map.put(TAG_PRICE, price);
                            map.put(TAG_WEIGHT, weight);
                            map.put(TAG_ITEMS, items);
                            map.put(TAG_IMAGE, image);

                            productsList.add(map);
                        }
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
                        showToast2();
                    }
                });
            }

            return null;
        }

        private void showToast() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CartActivity.this, message,
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        private void showToast2() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(CartActivity.this,
                            "Server Error..Please Try Again Later..",
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

            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    if (!productsList.isEmpty()) {
                        listview = (ListView) findViewById(R.id.cart_listview);
                        listadapter = new CustomerListAdapter(CartActivity.this,
                                productsList);
                        listview.setAdapter(listadapter);
                    } else {

                    }
                }

            });
        }
    }

    public class CustomerListAdapter extends BaseAdapter {

        Context activity;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> data;
        HashMap<String, String> resultp = new HashMap<String, String>();
        int count = 0;

        TextView txt_prod_name, txt_prod_qty, txt_prod_price,
                txt_prod_header, txt_prod_weight;
        ImageView img_delete, img_plus, img_minus;

        public CustomerListAdapter(CartActivity activity2,
                                   ArrayList<HashMap<String, String>> productsList) {
            // TODO Auto-generated constructor stub
            activity = activity2;
            data = productsList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub

            ImageView imageview;

            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.cart_list_item, null);

            resultp = data.get(position);

            txt_prod_header = (TextView) v
                    .findViewById(R.id.cart_list_item_title_header);
            txt_prod_name = (TextView) v
                    .findViewById(R.id.cart_list_item_title);
            txt_prod_weight = (TextView) v
                    .findViewById(R.id.cart_list_item_weight);
            txt_prod_price = (TextView) v
                    .findViewById(R.id.cart_list_item_price);
            txt_prod_qty = (TextView) v
                    .findViewById(R.id.cart_list_items_count);

            imageview = (ImageView) v
                    .findViewById(R.id.cart_list_item_imageview);

            img_delete = (ImageView) v.findViewById(R.id.cart_list_item_delete);

            img_plus = (ImageView) v.findViewById(R.id.cart_plus_img);
            img_minus = (ImageView) v.findViewById(R.id.cart_minus_img);


            txt_prod_header.setText("fresh");
            txt_prod_name.setText(resultp.get(CartActivity.TAG_PRODUCT_NAME));
            txt_prod_weight.setText("Weight : "
                    + resultp.get(CartActivity.TAG_WEIGHT));
            txt_prod_price.setText("Rs."
                    + resultp.get(CartActivity.TAG_PRICE));

            txt_prod_qty.setText(resultp.get(CartActivity.TAG_QUANTITY));

            Double total_price = db.getTotalPrice();
            totalPrice(total_price);

            String prod_id = resultp.get(CartActivity.TAG_ID);
            String image = db.getProductImage(prod_id);
            Log.d("CartImage", image);
            Glide.with(activity).load(image)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageview);

            img_plus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    resultp = data.get(position);

                    product_id = resultp.get(CartActivity.TAG_ID);
                    String qty = resultp.get(CartActivity.TAG_QUANTITY);
                    count = Integer.parseInt(qty);
                    count = count + 1;

                    selected_price = resultp.get(CartActivity.TAG_PRICE);

                    db.updateQuantity(product_id, selected_price, String.valueOf(count));

                    Constants.intent = new Intent(activity, CartActivity.class);
                    startActivity(Constants.intent);
                    finish();
                }

            });

            img_minus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    resultp = data.get(position);

                    product_id = resultp.get(CartActivity.TAG_ID);
                    String qty = resultp.get(CartActivity.TAG_QUANTITY);
                    count = Integer.parseInt(qty);

                    selected_price = resultp.get(CartActivity.TAG_PRICE);

                    if (count > 1) {
                        count = count - 1;
                        db.updateQuantity(product_id, selected_price, String.valueOf(count));

                        if (count == 0) {
                            db.deleteCategory(new Category(product_id));
                        }
                    } else {
                        db.deleteCategory(new Category(product_id));
                    }

                    Constants.intent = new Intent(activity, CartActivity.class);
                    startActivity(Constants.intent);
                    finish();
                }

            });

            img_delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    resultp = data.get(position);

                    db.deleteCategory(new Category(resultp.get(CartActivity.TAG_PRODUCT_ID), resultp.get(CartActivity.TAG_PRICE)));

                    Constants.intent = new Intent(activity, CartActivity.class);
                    startActivity(Constants.intent);
                    finish();
                }

            });

            return v;

        }
    }

    public void totalPrice(Double final_price2) {
        // TODO Auto-generated method stub

        final_price = final_price2;
        total_price_tv = (TextView) findViewById(R.id.checkout_items_total_cost);
        total_price_tv.setText("- Rs." + final_price2);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                if (from.equalsIgnoreCase("MilkCatList")) {
                    Constants.intent = new Intent(getApplicationContext(), MilkCategoryActivity.class);
                    startActivity(Constants.intent);
                    this.finish();
                } else if (from.equalsIgnoreCase("CatList")) {
                    Constants.intent = new Intent(getApplicationContext(), CategoryListActivity.class);
                    startActivity(Constants.intent);
                    this.finish();
                } else if (from.equalsIgnoreCase("Main")) {
                    Constants.intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(Constants.intent);
                    this.finish();
                } else {
                    this.finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (from.equalsIgnoreCase("MilkCatList")) {
            Constants.intent = new Intent(getApplicationContext(), MilkCategoryActivity.class);
            startActivity(Constants.intent);
            this.finish();
        } else if (from.equalsIgnoreCase("CatList")) {
            Constants.intent = new Intent(getApplicationContext(), CategoryListActivity.class);
            startActivity(Constants.intent);
            this.finish();
        } else if (from.equalsIgnoreCase("Main")) {
            Constants.intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(Constants.intent);
            this.finish();
        } else {
            this.finish();
        }

    }

}
