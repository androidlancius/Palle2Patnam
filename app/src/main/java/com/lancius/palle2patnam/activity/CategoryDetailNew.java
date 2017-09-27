package com.lancius.palle2patnam.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lancius.palle2patnam.Database.Category;
import com.lancius.palle2patnam.Database.DatabaseHandler;
import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.utils.CommonUtilities;
import com.lancius.palle2patnam.utils.Constants;
import com.lancius.palle2patnam.utils.JsonParser;
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

/**
 * Created by lancius on 8/18/2017.
 */

public class CategoryDetailNew extends AppCompatActivity {

    RadioGroup radioGroup;
    RelativeLayout minusLayout, plusLayout;
    TextView quantity_tv;
    int count = 0;
    Toolbar toolbar;
    TextView toolbarNameTv, cart_list_count_txt, productNameTv, productPriceTv, productWeightTv, productInfoTv;
    DatabaseHandler db;
    List<Category> contacts;
    JsonParser jsonParser = new JsonParser();
    ProgressDialog pDialog;
    JSONArray result = null;

    static final String TAG_SUCCESS = "success";
    static final String TAG_CATEGORY_PRODUCT_ID = "id";
    static final String TAG_CATEGORY_PRODUCT_NAME = "product_name";
    static final String TAG_CATEGORY_PRODUCT_PRICE = "price";
    static final String TAG_CATEGORY_PRODUCT_PRICE_ID = "price_id";
    static final String TAG_CATEGORY_PRODUCT_WEIGHT_ID = "weight_type_id";
    static final String TAG_CATEGORY_PRODUCT_WEIGHT = "weight_type";
    static final String TAG_CATEGORY_DETAIL_IMAGE = "image";
    static final String TAG_CATEGORY_PRODUCT_INFO = "product_info";
    static final String TAG_RESULTS = "lists";

    String from, selected_weight, productInfo, productName, productPrice = "0.0", productPriceId, productWeight, productWeightId, productImage, product_id, message;

    ArrayList priceList, weightList;

    Double total_price = 0.0;
    SessionManager session;
    Context activity;
    String amount, selected_price;
    TextView countTv;
    RelativeLayout proceedLayout;
    int sel_position = 0;
    Dialog pwindow;
    ListView product_price_listview;
    WeightListAdapter listAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail_new);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        countTv = (TextView) findViewById(R.id.category_detail_list_total_count);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        from = user.get(SessionManager.KEY_TO_CART);

        Bundle b = getIntent().getExtras();
        product_id = b.getString("product_id");
        productImage = b.getString("product_image");
        productName = b.getString("product_name");
        productPrice = b.getString("product_price");
        productWeight = b.getString("product_weight");
        String position = b.getString("product_price_position");
        sel_position = Integer.parseInt(position);
        Log.d("product_id", product_id);

        priceList = new ArrayList();
        weightList = new ArrayList();

        toolbarNameTv = (TextView) findViewById(R.id.category_detail_tool_bar_name);
        toolbarNameTv.setText(productName);

        quantity_tv = (TextView) findViewById(R.id.quantity_text_view);
        minusLayout = (RelativeLayout) findViewById(R.id.layout_category_quantity_minus);
        plusLayout = (RelativeLayout) findViewById(R.id.layout_category_quantity_add);

        db = new DatabaseHandler(getApplicationContext());

        contacts = db.getAllCategories();

        cart_list_count_txt = (TextView) findViewById(R.id.category_detail_cart_count);
        cart_list_count_txt.setText("" + contacts.size());

        String qty = db.getQty(product_id, productPrice);
        count = Integer.parseInt(qty);

        countTv.setText(count + "+");

        if (count == 0) {

            countTv.setText("Add");
            minusLayout.setVisibility(View.GONE);

        } else {
            countTv.setText(count + "+");
        }

        count = Integer.parseInt(qty);
        countTv.setText("" + count);
        if (count >= 1)
            minusLayout.setVisibility(View.VISIBLE);
        countTv.setText("" + count + "+");

        if (count == 0) {
            minusLayout.setVisibility(View.GONE);
            countTv.setText("Add");
        }

        if (CommonUtilities.checkConn(getApplicationContext())) {

            new categoryDetails().execute();

        } else {

            Constants.intent = new Intent(getApplicationContext(),
                    NoNetworkActivity.class);
            startActivity(Constants.intent);
        }

        proceedLayout = (RelativeLayout) findViewById(R.id.product_detail_bottom);

        if (contacts.size() == 0) {
            proceedLayout.setVisibility(View.GONE);
        } else {
            proceedLayout.setVisibility(View.VISIBLE);
        }

        proceedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(),
                        CartActivity.class);
                Constants.session.storeToCart("Main");
                startActivity(i);
                //finish();

            }
        });

        ImageView cart = (ImageView) findViewById(R.id.detail_cart);
        cart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (contacts.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please Add something to Cart", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            CartActivity.class);
                    session.storeToCart("Main");
                    startActivity(i);
//                    finish();
                }

            }
        });


    }

    class categoryDetails extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog'5 9+\789+\789+
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CategoryDetailNew.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }

        /**
         * Creating Application
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("product_id", product_id));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = null;
            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.CATEGORY_LIST_DETAIL, "POST", params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }

                // check log cat fro response
                Log.d("Create Order Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {


                        result = json.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < result.length(); i++) {
                            JSONObject c = result.getJSONObject(i);
                            // successfully created product
                            productName = c.getString(TAG_CATEGORY_PRODUCT_NAME);
                            productPrice = c.getString(TAG_CATEGORY_PRODUCT_PRICE);
                            productPriceId = c.getString(TAG_CATEGORY_PRODUCT_PRICE_ID);
                            productWeight = c.getString(TAG_CATEGORY_PRODUCT_WEIGHT);
                            productWeightId = c.getString(TAG_CATEGORY_PRODUCT_WEIGHT_ID);
                            //  productImage = c.getString(TAG_CATEGORY_DETAIL_IMAGE);
                            productInfo = c.getString(TAG_CATEGORY_PRODUCT_INFO);

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
                    Toast.makeText(CategoryDetailNew.this, message,
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();

            productNameTv = (TextView) findViewById(R.id.category_detail_product_title);
            productPriceTv = (TextView) findViewById(R.id.category_detail_product_price);
            productWeightTv = (TextView) findViewById(R.id.category_detail_product_weight);
            productInfoTv = (TextView) findViewById(R.id.category_detail_product_info);
            ImageView productImageview = (ImageView) findViewById(R.id.category_detail_image);
            quantity_tv = (TextView) findViewById(R.id.quantity_text_view);

            priceList.clear();

            ArrayList aListType = new ArrayList(Arrays.asList(productPrice.split(",")));
            for (int i = 0; i < aListType.size(); i++) {
                System.out.println("-->" + aListType.get(i));
                priceList.add(aListType.get(i).toString());
            }

            weightList.clear();

            ArrayList aList = new ArrayList(Arrays.asList(productWeight.split(",")));
            for (int i = 0; i < aList.size(); i++) {
                System.out.println("-->" + aList.get(i));
                weightList.add(aList.get(i).toString());
            }

            amount = "" + priceList.get(sel_position);

            productNameTv.setText(productName);
            productPriceTv.setText("Rs. " + priceList.get(sel_position));
            productWeightTv.setText("" + weightList.get(sel_position));
            productInfoTv.setText(productInfo);

            Log.d("product_image", productImage);
            activity = CategoryDetailNew.this;
            Glide.with(activity).load(productImage)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(productImageview);

            productWeightTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindow = new Dialog(activity);
                    pwindow.setCanceledOnTouchOutside(true);
                    pwindow.setCancelable(true);
                    pwindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    pwindow.setContentView(R.layout.activity_category_product_price);
                    pwindow.show();

                    TextView packageProdText = (TextView) pwindow.findViewById(R.id.text_price_pack);
                    packageProdText.setText(productName + " - fresh");

                    product_price_listview = (ListView) pwindow.findViewById(R.id.category_product_price_listview);

                    listAdapter = new WeightListAdapter(activity, priceList, weightList);
                    listAdapter.notifyDataSetChanged();

                    product_price_listview.setAdapter(listAdapter);

                    product_price_listview.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int pos, long id) {
                                    // When clicked, show a toast with the TextView text

                                    selected_price = "" + priceList.get(pos);
                                    selected_weight = "" + weightList.get(pos);

                                    sel_position = pos;

                                    ((TextView) findViewById(R.id.category_detail_product_weight)).setText(selected_weight);
                                    ((TextView) findViewById(R.id.category_detail_product_price)).setText(selected_price);

                                    String price_test = db.checkProductExistsWithWeight(product_id, selected_price);

                                    if (price_test.equalsIgnoreCase("Yes")) {

                                        String qty = db.getQty(product_id, selected_price);

                                        countTv.setText(qty + "+");

                                        minusLayout.setVisibility(View.VISIBLE);

                                        productPriceTv.setText("Rs. " + selected_price);
                                        productWeightTv.setText(selected_weight);

                                    } else if (price_test.equalsIgnoreCase("No")) {
                                        countTv.setText("Add");
                                        minusLayout.setVisibility(View.GONE);
                                    }

                                    pwindow.dismiss();
                                }
                            });
                }
            });

            plusLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    minusLayout.setVisibility(View.VISIBLE);

                    String qty = db.getQty(product_id, selected_price);
                    count = Integer.parseInt(qty);
                    String test = db.checkProductExistsWithWeight(product_id, selected_price);

                    if (test.equalsIgnoreCase("Yes")) {
                        count = count + 1;
                        Double total = count * Double.parseDouble(amount);
                        String tamount = String.valueOf(total);
                        db.updateQuantity(product_id, selected_price, String.valueOf(count));
                        countTv.setText(count + "+");
                        contacts = db.getAllCategories();
                        cart_list_count_txt.setText("" + contacts.size());
                        amountPlusSend(tamount);

                        proceedLayout.setVisibility(View.VISIBLE);

                    } else if (test.equalsIgnoreCase("No")) {
                        count = 0;
                        count = count + 1;
                        Double total = count * Double.parseDouble(amount);
                        String tamount = String.valueOf(total);
                        db.addCategory(new Category(product_id, String.valueOf(count), amount, productImage, "" + weightList.get(sel_position), String.valueOf(total_price)));
                        countTv.setText(count + "+");
                        contacts = db.getAllCategories();
                        cart_list_count_txt.setText("" + contacts.size());
                        amountPlusSend(tamount);

                        proceedLayout.setVisibility(View.VISIBLE);
                    }

                }

            });

            minusLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    String qty = db.getQty(product_id, selected_price);
                    count = Integer.parseInt(qty);
                    String test = db.checkProductExistsWithWeight(product_id, selected_price);

                    if (test.equalsIgnoreCase("Yes")) {
                        if (count > 0) {
                            count = count - 1;
                            Double total = count * Double.parseDouble(amount);
                            String tamount = String.valueOf(total);
                            db.updateQuantity(product_id, selected_price, String.valueOf(count));
                            countTv.setText(count + "+");

                            if (count == 0) {
                                db.deleteCategory(new Category(product_id, selected_price));
                                contacts = db.getAllCategories();
                                cart_list_count_txt.setText("" + contacts.size());

                                countTv.setText("Add");
                                minusLayout.setVisibility(View.GONE);

                                if (contacts.size() == 0)
                                    proceedLayout.setVisibility(View.VISIBLE);
                                else
                                    proceedLayout.setVisibility(View.GONE);

                            }

                            amountMinusSend(tamount);

                        } else {
                            count = 0;
                            db.deleteCategory(new Category(product_id, selected_price));
                            String tamount = "0";
                            amountMinusSend(tamount);

                            countTv.setText("Add");
                            minusLayout.setVisibility(View.GONE);

                            contacts = db.getAllCategories();
                            cart_list_count_txt.setText("" + contacts.size());

                            if (contacts.size() == 0)
                                proceedLayout.setVisibility(View.VISIBLE);
                            else
                                proceedLayout.setVisibility(View.GONE);

                        }

                    } else if (test.equalsIgnoreCase("No")) {
                        count = 0;
                        countTv.setText("" + count);
                        String tamount = "0";
                        amountMinusSend(tamount);

                        contacts = db.getAllCategories();
                        cart_list_count_txt.setText("" + contacts.size());

                        if (contacts.size() == 0)
                            proceedLayout.setVisibility(View.VISIBLE);
                        else
                            proceedLayout.setVisibility(View.GONE);
                    }

                }

            });

        }

    }

    public class WeightListAdapter extends BaseAdapter {

        Context activity;
        LayoutInflater inflater;
        ArrayList<String> weightList, priceList;

        public WeightListAdapter(Context activity,
                                 ArrayList<String> priceList, ArrayList<String> weightList) {
            // TODO Auto-generated constructor stub
            this.activity = activity;
            this.weightList = weightList;
            this.priceList = priceList;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return priceList.size();
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

            TextView product_weight, product_price;

            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View itemView = inflater.inflate(R.layout.activity_product_pwindow_list_item,
                    parent, false);

            product_weight = (TextView) itemView.findViewById(R.id.list_item_weightTv);
            product_price = (TextView) itemView.findViewById(R.id.list_item_priceTv);

            product_weight.setText(weightList.get(position));
            product_price.setText("-" + priceList.get(position));

            return itemView;

        }
    }


    public void amountMinusSend(String selling_price) {

//        priceTv.setText(selling_price + "/-");

    }

    public void amountPlusSend(String selling_price) {

//        priceTv.setText(selling_price + "/-");

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
        } else {
            this.finish();
        }
    }

}
