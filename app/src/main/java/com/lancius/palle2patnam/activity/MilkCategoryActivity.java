package com.lancius.palle2patnam.activity;

import android.app.Activity;
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
 * Created by lancius on 9/7/2017.
 */

public class MilkCategoryActivity extends AppCompatActivity {

    JsonParser jsonParser = new JsonParser();
    ProgressDialog pDialog;
    JSONArray result = null;

    static final String TAG_SUCCESS = "success";
    private static final String TAG_CATEGORY_PRODUCT_ID = "id";
    static final String TAG_CATEGORY_PRODUCT_NAME = "product_name";
    static final String TAG_CATEGORY_PRODUCT_PRICE = "price";
    static final String TAG_CATEGORY_PRODUCT_PRICE_ID = "price_id";
    static final String TAG_CATEGORY_PRODUCT_WEIGHT_ID = "weight_type_id";
    static final String TAG_CATEGORY_PRODUCT_WEIGHT = "weight_type";
    static final String TAG_CATEGORY_PRODUCT_IMAGE = "image";
    static final String TAG_RESULTS = "lists";

    String catid, selected_price, selected_weight, productId, productName, productPrice, productPriceId, productWeight, productWeightId, productImage, product_id, amount, categoryId, message;
    ArrayList<HashMap<String, String>> productsList;
    ListView listview;
    CategoryDetailAdapter adapter;
    ArrayList priceList, weightList;
    private TextView catTilteTv, add_price, itemsCountTxt, cart_list_count_txt, category_product_name, category_product_price, category_product_weight;
    private ImageView category_product_image;

    DatabaseHandler db;
    Toolbar toolbar;
    int count = 0, sel_position = 0;
    List<Category> contacts;
    List<String> categoryIdList;
    SessionManager session;
    RelativeLayout bottomLayout;
    Dialog pwindow;
    ListView product_price_listview;
    WeightListAdapter listAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milk_category);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        categoryId = user.get(session.KEY_CAT_ID);
        String page_title = user.get(session.KEY_CAT_NAME);

        catTilteTv = (TextView) findViewById(R.id.toolbar_title_milk);
        catTilteTv.setText(page_title);

        productsList = new ArrayList<>();
        priceList = new ArrayList();
        weightList = new ArrayList();

        categoryIdList = new ArrayList<>();

        db = new DatabaseHandler(getApplicationContext());
        db.deleteAll();

        contacts = db.getAllCategories();
        bottomLayout = (RelativeLayout) findViewById(R.id.milk_list_bottom_layout);
        bottomLayout.setVisibility(View.GONE);
        cart_list_count_txt = (TextView) findViewById(R.id.milk_category_list_cart_count);
        cart_list_count_txt.setText("" + contacts.size());

        itemsCountTxt = (TextView) findViewById(R.id.milk_category_list_items_count);
        itemsCountTxt.setText("" + contacts.size() + " item(s)");

        add_price = (TextView) findViewById(R.id.milk_category_list_total_price);
        final Double total_price = db.getTotalPrice();
        totalPrice(total_price);

        if (contacts.size() > 0) {
            bottomLayout.setVisibility(View.VISIBLE);
        }


        if (CommonUtilities.checkConn(getApplicationContext())) {

            new categoryDetails().execute();

        } else {

            Constants.intent = new Intent(getApplicationContext(),
                    NoNetworkActivity.class);
            startActivity(Constants.intent);
        }

        ImageView next = (ImageView) findViewById(R.id.milk_categories_next_view);
        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                final List<Category> contacts = db.getAllCategories();

                if (contacts.size() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "Please Add something to Cart", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            CartActivity.class);
                    session.storeToCart("MilkCatList");
                    startActivity(i);
                    finish();
                }

            }
        });

        ImageView cart = (ImageView) findViewById(R.id.milk_list_item_cart);
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
                    session.storeToCart("MilkCatList");
                    startActivity(i);
                    finish();
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
            pDialog = new ProgressDialog(MilkCategoryActivity.this);
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
            params.add(new BasicNameValuePair("category_id", categoryId));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = null;
            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.MAIN_CATEGORY_LIST, "POST", params);

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

                        productsList.clear();

                        result = json.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < result.length(); i++) {
                            JSONObject c = result.getJSONObject(i);
                            // successfully created product
                            productId = c.getString(TAG_CATEGORY_PRODUCT_ID);
                            productName = c.getString(TAG_CATEGORY_PRODUCT_NAME);
                            productPrice = c.getString(TAG_CATEGORY_PRODUCT_PRICE);
                            productPriceId = c.getString(TAG_CATEGORY_PRODUCT_PRICE_ID);
                            productWeight = c.getString(TAG_CATEGORY_PRODUCT_WEIGHT);
                            productWeightId = c.getString(TAG_CATEGORY_PRODUCT_WEIGHT_ID);
                            productImage = c.getString(TAG_CATEGORY_PRODUCT_IMAGE);

                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_CATEGORY_PRODUCT_ID, productId);
                            map.put(TAG_CATEGORY_PRODUCT_NAME, productName);
                            map.put(TAG_CATEGORY_PRODUCT_PRICE, productPrice);
                            map.put(TAG_CATEGORY_PRODUCT_PRICE_ID, productPriceId);
                            map.put(TAG_CATEGORY_PRODUCT_WEIGHT, productWeight);
                            map.put(TAG_CATEGORY_PRODUCT_WEIGHT_ID, productWeightId);
                            map.put(TAG_CATEGORY_PRODUCT_IMAGE, productImage);

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
                    Toast.makeText(MilkCategoryActivity.this, message,
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

            if (!productsList.isEmpty()) {
                listview = (ListView) findViewById(R.id.milk_category_listview);
                adapter = new CategoryDetailAdapter(
                        MilkCategoryActivity.this, productsList);
                adapter.notifyDataSetChanged();
                listview.setAdapter(adapter);
            }

        }

    }

    public class CategoryDetailAdapter extends BaseAdapter {

        Context activity;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> data;
        HashMap<String, String> resultp = new HashMap<String, String>();

        public CategoryDetailAdapter(Activity activity2,
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


            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final RelativeLayout plus_button, minus_button;
            final TextView total_selected_count;


            final View itemView = inflater.inflate(R.layout.activity_milk_category_detail_list_item,
                    parent, false);

            category_product_name = (TextView) itemView
                    .findViewById(R.id.category_list_item_tittle);
            category_product_price = (TextView) itemView
                    .findViewById(R.id.category_list_item_price);
            category_product_weight = (TextView) itemView
                    .findViewById(R.id.category_list_item_sub_tittle);
            category_product_image = (ImageView) itemView
                    .findViewById(R.id.category_image_view);
            total_selected_count = (TextView) itemView
                    .findViewById(R.id.category_list_total_count);

            plus_button = (RelativeLayout) itemView.findViewById(R.id.plus_layout);
            minus_button = (RelativeLayout) itemView.findViewById(R.id.minus_layout);

            resultp = data.get(position);

            priceList.clear();
            weightList.clear();

            String price_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_PRICE);
            ArrayList aListType = new ArrayList(Arrays.asList(price_type.split(",")));
            for (int i = 0; i < aListType.size(); i++) {
                priceList.add(aListType.get(i).toString());
            }

            String weight_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_WEIGHT);
            ArrayList aList = new ArrayList(Arrays.asList(weight_type.split(",")));
            for (int i = 0; i < aList.size(); i++) {
                weightList.add(aList.get(i).toString());
            }

            category_product_price.setText("Rs. " + priceList.get(0));
            category_product_weight.setText("" + weightList.get(0));
            category_product_name.setText(resultp
                    .get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_NAME));

            Glide.with(activity).load(resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_IMAGE))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(category_product_image);

            contacts = db.getAllCategories();

            for (Category cn : contacts) {
                catid = cn.getCatId();
                categoryIdList.add(catid);
            }

            if (categoryIdList.contains(resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID))) {

                for (Category cn : contacts) {
                    catid = cn.getCatId();
                    String qty = cn.getQty();
                    String price = cn.getPrice();
                    String weight = cn.getWeight();

                    if (catid.equalsIgnoreCase(resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID))) {

                        total_selected_count.setText(qty + "+");

                        category_product_price.setText("Rs. " + price);
                        category_product_weight.setText(weight);
                    }

                }

            } else {
                total_selected_count.setText("Add");
                minus_button.setVisibility(View.GONE);

                category_product_price.setText("Rs. " + priceList.get(0));
                category_product_weight.setText("" + weightList.get(0));
            }


            category_product_weight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    priceList.clear();
                    weightList.clear();

                    resultp = data.get(position);

                    product_id = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID);


                    String price_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_PRICE);
                    ArrayList aListType = new ArrayList(Arrays.asList(price_type.split(",")));
                    for (int i = 0; i < aListType.size(); i++) {
                        priceList.add(aListType.get(i).toString());
                    }

                    String weight_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_WEIGHT);
                    ArrayList aList = new ArrayList(Arrays.asList(weight_type.split(",")));
                    for (int i = 0; i < aList.size(); i++) {
                        weightList.add(aList.get(i).toString());
                    }

                    pwindow = new Dialog(activity);
                    pwindow.setCanceledOnTouchOutside(true);
                    pwindow.setCancelable(true);
                    pwindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    pwindow.setContentView(R.layout.activity_category_product_price);
                    pwindow.show();

                    String prod_name = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_NAME);
                    TextView packageProdText = (TextView) pwindow.findViewById(R.id.text_price_pack);
                    packageProdText.setText(prod_name + " - fresh");

                    product_price_listview = (ListView) pwindow.findViewById(R.id.category_product_price_listview);

                    listAdapter = new WeightListAdapter(activity, priceList, weightList);
                    listAdapter.notifyDataSetChanged();

                    product_price_listview.setAdapter(listAdapter);

                    product_price_listview.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int pos, long id) {
                                    // When clicked, show a toast with the TextView text

                                    resultp = data.get(position);

                                    product_id = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID);
                                    selected_price = "" + priceList.get(pos);
                                    selected_weight = "" + weightList.get(pos);
                                    sel_position = pos;

                                    ((TextView) itemView.findViewById(R.id.category_list_item_sub_tittle)).setText(selected_weight);
                                    ((TextView) itemView.findViewById(R.id.category_list_item_price)).setText(selected_price);

                                    String price_test = db.checkProductExistsWithWeight(product_id, selected_price);

                                    if (price_test.equalsIgnoreCase("Yes")) {

                                        String qty = db.getQty(product_id, selected_price);
                                        total_selected_count.setText(qty + "+");
                                        minus_button.setVisibility(View.VISIBLE);
                                        category_product_price.setText("Rs. " + selected_price);
                                        category_product_weight.setText(selected_weight);

                                    } else if (price_test.equalsIgnoreCase("No")) {
                                        total_selected_count.setText("Add");
                                        minus_button.setVisibility(View.GONE);
                                    }

                                    pwindow.dismiss();
                                }
                            });

                }
            });

            plus_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    minus_button.setVisibility(View.VISIBLE);

                    resultp = data.get(position);

                    priceList.clear();
                    weightList.clear();

                    String price_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_PRICE);
                    ArrayList aListType = new ArrayList(Arrays.asList(price_type.split(",")));
                    for (int i = 0; i < aListType.size(); i++) {
                        priceList.add(aListType.get(i).toString());
                    }

                    String weight_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_WEIGHT);
                    ArrayList aList = new ArrayList(Arrays.asList(weight_type.split(",")));
                    for (int i = 0; i < aList.size(); i++) {
                        weightList.add(aList.get(i).toString());
                    }

                    try {
                        selected_price = "" + priceList.get(sel_position);
                        selected_weight = "" + weightList.get(sel_position);
                    } catch (IndexOutOfBoundsException e) {
                        sel_position = 0;
                        selected_price = "" + priceList.get(sel_position);
                        selected_weight = "" + weightList.get(sel_position);
                    }

                    product_id = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID);
                    String imageView1 = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_IMAGE);

                    Log.d("SLEPRICE", "PLUS " + selected_price);

                    String price_test = db.checkProductExistsWithWeight(product_id, selected_price);
                    String qty = db.getQty(product_id, selected_price);
                    count = Integer.parseInt(qty);

                    if (price_test.equalsIgnoreCase("Yes")) {

                        count = count + 1;

                        Log.d("SelectedPrice", "YSE " + selected_price + ", " + count);
                        db.updateQuantity(product_id, selected_price, String.valueOf(count));
                        total_selected_count.setText("" + count + "+");

                        contacts = db.getAllCategories();
                        cart_list_count_txt.setText("" + contacts.size());
                        itemsCountTxt.setText("" + contacts.size() + " item(s)");

                        Double total_price = db.getTotalPrice();
                        totalPrice(total_price);

                        bottomLayout.setVisibility(View.VISIBLE);

                    } else if (price_test.equalsIgnoreCase("No")) {

                        count = 0;
                        count = count + 1;

                        Log.d("SLEPRICE", selected_price);
                        Log.d("SelectedPrice", "NO " + selected_price + ", " + count);

                        db.addCategory(new Category(product_id, String.valueOf(count), selected_price, imageView1, selected_weight));
                        total_selected_count.setText("" + count + "+");

                        contacts = db.getAllCategories();

                        cart_list_count_txt.setText("" + contacts.size());
                        itemsCountTxt.setText("" + contacts.size() + " item(s)");

                        Double total_price = db.getTotalPrice();
                        totalPrice(total_price);

                        bottomLayout.setVisibility(View.VISIBLE);

                    }
                }
            });
            minus_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resultp = data.get(position);

                    priceList.clear();
                    weightList.clear();

                    String price_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_PRICE);
                    ArrayList aListType = new ArrayList(Arrays.asList(price_type.split(",")));
                    for (int i = 0; i < aListType.size(); i++) {
                        priceList.add(aListType.get(i).toString());
                    }

                    String weight_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_WEIGHT);
                    ArrayList aList = new ArrayList(Arrays.asList(weight_type.split(",")));
                    for (int i = 0; i < aList.size(); i++) {
                        weightList.add(aList.get(i).toString());
                    }

                    selected_price = "" + priceList.get(sel_position);
                    selected_weight = "" + weightList.get(sel_position);

                    product_id = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID);

                    String qty = db.getQty(product_id, selected_price);
                    count = Integer.parseInt(qty);

                    String price_test = db.checkProductExistsWithWeight(product_id, selected_price);

                    if (price_test.equalsIgnoreCase("Yes")) {

                        if (count > 0) {

                            count = count - 1;
                            db.updateQuantity(product_id, selected_price, String.valueOf(count));
                            total_selected_count.setText("" + count + "+");

                            Double total_price = db.getTotalPrice();
                            totalPrice(total_price);

                            if (count == 0) {

                                db.deleteCategory(new Category(product_id, selected_price));

                                contacts = db.getAllCategories();
                                cart_list_count_txt.setText("" + contacts.size());
                                itemsCountTxt.setText("" + contacts.size() + " item(s)");

                                Double total_price_ = db.getTotalPrice();
                                totalPrice(total_price_);

                                total_selected_count.setText("Add");

                                minus_button.setVisibility(View.GONE);

                                if (contacts.size() == 0) {
                                    minus_button.setVisibility(View.GONE);
                                } else
                                    bottomLayout.setVisibility(View.VISIBLE);
                            }

                        } else {
                            count = 0;

                            total_selected_count.setText("Add");
                            minus_button.setVisibility(View.GONE);

                            db.deleteCategory(new Category(product_id, selected_price));

                            contacts = db.getAllCategories();
                            cart_list_count_txt.setText("" + contacts.size());
                            itemsCountTxt.setText("" + contacts.size() + " item(s)");

                            if (contacts.size() == 0) {
                                minus_button.setVisibility(View.GONE);
                            } else
                                bottomLayout.setVisibility(View.VISIBLE);

                        }

                    } else if (price_test.equalsIgnoreCase("No")) {

                        count = 0;

                        total_selected_count.setText("Add");
                        minus_button.setVisibility(View.GONE);
                        bottomLayout.setVisibility(View.GONE);

                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resultp = data.get(position);

                    product_id = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID);


//                    sel_position = db.getSelPosition(product_id, selected_price);

                    priceList.clear();
                    weightList.clear();

                    String price_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_PRICE);
                    ArrayList aListType = new ArrayList(Arrays.asList(price_type.split(",")));
                    for (int i = 0; i < aListType.size(); i++) {
                        priceList.add(aListType.get(i).toString());
                    }

                    String weight_type = resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_WEIGHT);
                    ArrayList aList = new ArrayList(Arrays.asList(weight_type.split(",")));
                    for (int i = 0; i < aList.size(); i++) {
                        weightList.add(aList.get(i).toString());
                    }

                    selected_price = "" + priceList.get(sel_position);
                    selected_weight = "" + weightList.get(sel_position);

                    session.storeToCart("MilkCatList");
                    Intent intent = new Intent(activity, CategoryDetailNew.class);
                    intent.putExtra("product_id", resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_ID));
                    intent.putExtra("product_name", resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_NAME));
                    intent.putExtra("product_image", resultp.get(MilkCategoryActivity.TAG_CATEGORY_PRODUCT_IMAGE));
                    intent.putExtra("product_price_position", "" + sel_position);
                    intent.putExtra("product_price", selected_price);
                    intent.putExtra("product_weight", selected_weight);
                    startActivity(intent);
                    finish();

                }
            });
            return itemView;
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


    public void totalPrice(Double result) {

        add_price.setText("" + result);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
