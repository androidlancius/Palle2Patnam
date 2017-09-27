package com.lancius.palle2patnam.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lancius.palle2patnam.R;

import java.util.ArrayList;

/**
 * Created by dilip on 4/6/2017.
 */
public class SlidingImageAdapter extends PagerAdapter {
    String ratedValue;
    Context activity;
    LayoutInflater inflater;
    ArrayList<String> data;
    ArrayList<String> imagesList;

    public SlidingImageAdapter(Context context,
                               ArrayList<String> imagesList2) {
        // TODO Auto-generated constructor stub
        activity = context;
        data = imagesList2;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        ImageView imageview = null;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(
                R.layout.sliding_images_layout, view, false);
        imageview = (ImageView) itemView.findViewById(R.id.home_banner_image);

        Glide.with(activity).load(data.get(position))
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageview);

        ((ViewPager) view).addView(itemView, 0);

        return itemView;

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}