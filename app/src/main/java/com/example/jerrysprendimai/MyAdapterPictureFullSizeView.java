package com.example.jerrysprendimai;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class MyAdapterPictureFullSizeView extends PagerAdapter {
    Context context;
    ArrayList<ObjectObjPic> myPictureList;
    LayoutInflater inflater;
    String url;

    public MyAdapterPictureFullSizeView(Context cntx, ArrayList<ObjectObjPic> pictures){
        this.context = cntx;
        this.myPictureList = pictures;
    }
    @Override
    public int getCount() {
        return myPictureList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
        //return false;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //----------reading values from Internal DB
        SQLiteDB dbHelper = new SQLiteDB(context);
        Cursor result = dbHelper.getData();
        if(result.getCount() > 0 ){
            result.moveToNext();
            url = result.getString(1);
        }
        //---------get relevant picture object
        ObjectObjPic objectObjPic = myPictureList.get(position);

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.full_item,null);

        ImageView imageView = (ImageView) v.findViewById(R.id.img);
        Glide.with(context)
                .load(url + "/" +objectObjPic.getPicUrl())
                .apply(new RequestOptions().centerInside())
                .into(imageView);
        ViewPager vp = (ViewPager) container;
        vp.addView(v,0);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);

        ViewPager viewPager = (ViewPager) container;
        View v = (View) object;
        viewPager.removeView(v);
    }
}
