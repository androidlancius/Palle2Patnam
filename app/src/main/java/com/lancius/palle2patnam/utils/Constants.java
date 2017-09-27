package com.lancius.palle2patnam.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Constants {

    public static Context context;

    public static Intent intent;
    public static ListView listview;
    public static JsonParser jsonParser = new JsonParser();
    public static ProgressDialog pDialog;
    public static ArrayList<HashMap<String, String>> productsList, postsList, postCommentsList;

    public static List<String> galleryList = new ArrayList<>();
    public static List<String> momentsLists = new ArrayList<>();
    public static List<String> networkList = new ArrayList<>();
    public static List<String> activityList = new ArrayList<>();
    public static List<String> hubsList = new ArrayList<>();

    public static AsyncTask<Void, Void, Void> mRegisterTask;
    public static SessionManager session;
    public static JSONArray products = null, postProducts = null, postComments = null, galleryProducts = null,
            momentsProducts = null, networkProducts = null, activityProducts = null, hubsProducts = null;

    public static final String TAG_SUCCESS = "success";
    public static final String TAG_USER_EMAIL = "email";
    public static final String TAG_RESULTS = "users";

    public static final String TAG_LIST_RESULTS = "lists";

    public static String CATEGORY_ID1 = "1"; // HUBS
    public static String CATEGORY_ID2 = "2"; // FLASH HUBS
    public static String CATEGORY_ID3 = "3"; // STORIES

    public static String TYPE1 = "1"; // HUBS
    public static String TYPE2 = "2"; // POSTS
    public static String TYPE3 = "3"; // COMMENTS

    public static DrawerLayout mDrawerLayout;
    public static View containerView;

    public static void showToast(Context context, String message) {
        // TODO Auto-generated method stub

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

}
