package com.lancius.palle2patnam.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lancius.palle2patnam.R;
import com.lancius.palle2patnam.adapter.SlidingImageAdapter;
import com.lancius.palle2patnam.service.MyVolley;
import com.lancius.palle2patnam.utils.CirclePageIndicator;
import com.lancius.palle2patnam.utils.CommonUtilities;
import com.lancius.palle2patnam.utils.Constants;
import com.lancius.palle2patnam.utils.JsonParser;
import com.lancius.palle2patnam.utils.SessionManager;
import com.lancius.palle2patnam.utils.WebServices;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.lancius.palle2patnam.utils.Constants.productsList;
import static com.lancius.palle2patnam.utils.Constants.session;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final String TAG_BANNER_IMAGE = "image";
    static final String TAG_UPCOMING_BANNER_IMAGE = "image";
    static final String TAG_ID = "id";

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;

    Context activity;
    HLVAdapter mAdapter;

    ArrayList<String> imagesList;
    ArrayList<HashMap<String, String>> bannerList;
    JSONArray products = null;
    JsonParser jsonParser = new JsonParser();
    ProgressDialog pDialog;
    JSONArray result = null;

    static final String TAG_SUCCESS = "success";
    static final String TAG_CATEGORY_ID = "id";
    static final String TAG_CATEGORY_NAME = "category_name";
    static final String TAG_CATEGORY_IMAGE = "category_image";
    static final String TAG_RESULTS = "lists";

    String banner_image, message, categoryName, categoryImage, categoryId;
    RecyclerView mRecyclerView;
    ListView listView;
    IconAdapter dataAdapter;
    ImageView imageview;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String username = user.get(SessionManager.KEY_NAME);
        String email = user.get(SessionManager.KEY_EMAIL);
        imagesList = new ArrayList<String>();
        productsList = new ArrayList<>();
        bannerList = new ArrayList<HashMap<String, String>>();
        final String token = com.lancius.palle2patnam.activity.SharedPrefManager.getInstance(this).getDeviceToken();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

//        TextView navUsernameTv = (TextView) findViewById(R.id.nav_username);
//        navUsernameTv.setText(username);
//        TextView navUseremailTv = (TextView) findViewById(R.id.nav_user_email);
//        navUseremailTv.setText(email);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (CommonUtilities.checkConn(getApplicationContext())) {

            new gettingBanners().execute();
            new categoryDetail().execute();
            new gettingUpcomingBanner().execute();
            sendTokenToServer();
            Log.d("token_generated", token);

        } else {

            Constants.intent = new Intent(getApplicationContext(),
                    NoNetworkActivity.class);
            startActivity(Constants.intent);

        }
    }
    private void sendTokenToServer() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering Device...");
        progressDialog.show();

        final String token = com.lancius.palle2patnam.activity.SharedPrefManager.getInstance(this).getDeviceToken();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, WebServices.NOTIFICATION_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
//                            Toast.makeText(FreetrialNew.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", "harishreddy.tekula@gmail.com");
                params.put("token", token);
                return params;
            }
        };
        MyVolley.getInstance(this).addToRequestQueue(stringRequest);
    }

    class gettingBanners extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating Application
         */
        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = null;
            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.HOME_BANNERS_API, "POST",
                            params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                Log.d("Banners Response", json.toString());

                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {

                        imagesList.clear();
                        products = json.getJSONArray(TAG_RESULTS);


                        for (int i = 0; i < products.length(); i++) {
                            JSONObject c = products.getJSONObject(i);
                            //images = c.getString( TAG_IMAGES );
                            // successfully created product
                            banner_image = c.getString(TAG_BANNER_IMAGE);
                            imagesList.add(banner_image);

                        }
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
                        message = "Server error please try again later";

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
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void runOnUiThread(Runnable runnable) {
            // TODO Auto-generated method stub

        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done

            Log.d("Count", "" + imagesList.size());

            if (!imagesList.isEmpty()) {
                mPager = (ViewPager) findViewById(R.id.pager);

                mPager.setAdapter(new SlidingImageAdapter(MainActivity.this,
                        imagesList));

                CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);

                indicator.setViewPager(mPager);

                final float density = getResources().getDisplayMetrics().density;

                // Set circle indicator radius
                indicator.setRadius(5 * density);

                NUM_PAGES = imagesList.size();

                // Auto start of viewpager
                final Handler handler = new Handler();
                final Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == NUM_PAGES) {
                            currentPage = 0;
                        }
                        mPager.setCurrentItem(currentPage++, true);
                    }
                };
                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(Update);
                    }
                }, 2000, 2000);

                // Pager listener over indicator
                indicator
                        .setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                            @Override
                            public void onPageSelected(int position) {
                                currentPage = position;

                            }

                            @Override
                            public void onPageScrolled(int pos, float arg1,
                                                       int arg2) {

                            }

                            @Override
                            public void onPageScrollStateChanged(int pos) {

                            }
                        });
            }


        }
    }

    class categoryDetail extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog'5 9+\789+\789+
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
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

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = null;
            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.MAIN_CATEGORIES, "POST", params);

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
                            categoryName = c.getString(TAG_CATEGORY_NAME);
                            categoryImage = c.getString(TAG_CATEGORY_IMAGE);
                            categoryId = c.getString(TAG_CATEGORY_ID);


                            HashMap<String, String> map = new HashMap<>();
                            map.put(TAG_CATEGORY_NAME, categoryName);
                            map.put(TAG_CATEGORY_IMAGE, categoryImage);
                            map.put(TAG_CATEGORY_ID, categoryId);


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
                    Toast.makeText(MainActivity.this, message,
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

            mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            mRecyclerView.setHasFixedSize(true);

            // The number of Columns
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new HLVAdapter(MainActivity.this, productsList);
            mRecyclerView.setAdapter(mAdapter);


        }

    }

    public class HLVAdapter extends RecyclerView.Adapter<HLVAdapter.ViewHolder> {


        Context context;
        HashMap<String, String> resultp = new HashMap<String, String>();
        ArrayList<HashMap<String, String>> data;


        public HLVAdapter(Activity activity2,
                          ArrayList<HashMap<String, String>> productsList) {
            // TODO Auto-generated constructor stub

            activity = activity2;
            data = productsList;
        }

        @Override
        public HLVAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(activity)
                    .inflate(R.layout.category_grid_item, viewGroup, false);
            HLVAdapter.ViewHolder viewHolder = new HLVAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            resultp = data.get(position);

            holder.tvSpecies.setText(resultp.get(MainActivity.TAG_CATEGORY_NAME));
            Glide.with(activity).load(resultp.get(MainActivity.TAG_CATEGORY_IMAGE))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.imgThumbnail);

            holder.itemView.setOnClickListener(
                    new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    resultp = data.get(position);
                    categoryId = resultp.get(MainActivity.TAG_CATEGORY_ID);
                    categoryName = resultp.get(MainActivity.TAG_CATEGORY_NAME);
                    session.storeCatId(categoryId, categoryName);

                    if (categoryId.equals("1")) {
                        Log.d("position", "" + position);
                        Intent i = new Intent(getApplicationContext(), MilkCategoryActivity.class);
                        startActivity(i);
                    } else {
                        Log.d("position", "" + position);
                        Intent i = new Intent(getApplicationContext(), CategoryListActivity.class);
                        startActivity(i);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imgThumbnail;
            public TextView tvSpecies;

            public ViewHolder(final View itemView) {
                super(itemView);

                tvSpecies = (TextView) itemView.findViewById(R.id.category_name);
                imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);


            }

        }

    }

    class gettingUpcomingBanner extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub

            // area=locationlabel;

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            JSONObject json = null;
            try {
                try {

                    json = jsonParser.makeHttpRequest(WebServices.HOME_BANNERS_API, "POST",
                            params);

                } catch (JSONException e1) {
                    // TODO Auto-generated catch block
                    // e1.printStackTrace();
                }

                // check log cat fro response
                Log.d("Special Create Response", json.toString());

                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 0) {

                        products = json.getJSONArray(TAG_RESULTS);
                        for (int i = 0; i < products.length(); i++) {

                            JSONObject c = products.getJSONObject(i);

                            String id = c.getString(TAG_ID);
                            String image = c.getString(TAG_UPCOMING_BANNER_IMAGE);

                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(TAG_ID, id);
                            map.put(TAG_UPCOMING_BANNER_IMAGE, image);

                            bannerList.add(map);

                        }

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
                        message = "Server error please try again later";
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
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once done

            if (!bannerList.isEmpty()) {

                Log.d("LOOP CHECKING", "Inside");
                listView = (ListView) findViewById(R.id.upcoming_banner_listview);

                dataAdapter = new IconAdapter(MainActivity.this,
                        bannerList);
                dataAdapter.notifyDataSetChanged();
                listView.setAdapter(dataAdapter);
                setListViewHeightBasedOnChildren(listView);

            } else {

            }

        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, Toolbar.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getHeight() * (listAdapter.getCount() - 1));

        //params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public class IconAdapter extends BaseAdapter {

        Context activity;
        LayoutInflater inflater;
        ArrayList<HashMap<String, String>> data;

        HashMap<String, String> resultp = new HashMap<String, String>();

        public IconAdapter(MainActivity activity,
                           ArrayList<HashMap<String, String>> bannerList) {
            // TODO Auto-generated constructor stub

            this.activity = activity;
            data = bannerList;
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
            final View itemView = inflater.inflate(R.layout.banner_list_item,
                    parent, false);

            resultp = data.get(position);

            Log.d("Special Image", resultp.get(MainActivity.TAG_UPCOMING_BANNER_IMAGE));

            imageview = (ImageView) itemView.findViewById(R.id.image_banner);

            Glide.with(activity).load(resultp.get(MainActivity.TAG_UPCOMING_BANNER_IMAGE))
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageview);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    resultp = data.get(position);
                }
            });
            return itemView;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            session.logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            // Handle the camera action
            Constants.intent = new Intent(getApplicationContext(), MyAccount.class);
            startActivity(Constants.intent);
        } else if (id == R.id.nav_orders) {
            Constants.intent = new Intent(getApplicationContext(), MyOrders.class);
            startActivity(Constants.intent);
        } else if (id == R.id.nav_cart) {
            Constants.intent = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(Constants.intent);
        } else if (id == R.id.nav_faqs) {

        } else if (id == R.id.nav_share) {

            Intent i9 = new Intent(Intent.ACTION_SEND);
            i9.setType("text/plain");
            i9.putExtra(Intent.EXTRA_SUBJECT, "Nice App for organic food items online");
            i9.putExtra(Intent.EXTRA_TEXT, "https://goo.gl/FEsF16");
            startActivity(Intent.createChooser(i9, "Share this App"));


        } else if (id == R.id.nav_contact) {
            Constants.intent = new Intent(getApplicationContext(), ContactUs.class);
            startActivity(Constants.intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
