package com.example.jerrysprendimai;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoViewAttacher;

import java.io.FileNotFoundException;
import java.io.IOException;
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
    public Bitmap decodeSampledBitmapFromResource(int reqWidth, int reqHeight, ObjectObjPic objectObjPic, int scaleSize) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(objectObjPic.getPicUri())), null, o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int scale = scaleSize * 2;
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(objectObjPic.getPicUri())), null, o2);
        } catch (FileNotFoundException e) {
            return null;
        }
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

        if(objectObjPic.getPicUri().length() > 0){
           //imageView.setImageBitmap(decodeSampledBitmapFromResource(100, 100, objectObjPic, 3));
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.parse(objectObjPic.picUri))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().override(500,500).centerInside())
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            //holder.myProgressBar.setVisibility(View.GONE);
                            //holder.myImage.setImageBitmap(resource);
                            imageView.setImageBitmap(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }else{
            Glide.with(context)
                    .load(url + "/" +objectObjPic.getPicUrl())
                    .apply(new RequestOptions().centerInside())
                    .into(imageView);
        }
        ViewPager vp = (ViewPager) container;
        vp.addView(v,0);

        //PhotoViewAttacher photoView = new PhotoViewAttacher(imageView);
        //photoView.update();
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
