package com.example.jerrysprendimai;

import android.animation.Animator;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.jerrysprendimai.interfaces.PicRecyclerViewClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MyAdapterObjectEditPicture extends RecyclerView.Adapter<MyAdapterObjectEditPicture.MyViewHolder>{

    Context context;
    ArrayList<ObjectObjPic> myPictureList;
    ArrayList<ObjectObjPic> myPictureListFull;
    ViewGroup parentView;
    ObjectUser myUser;
    PicRecyclerViewClickListener picClickListener;
    MyAdapterObjectEdit parentAdapterObjectEdit;
    MyAdapterObjectEdit.MyViewHolder parentHolder;

    public MyAdapterObjectEditPicture(Context context, MyAdapterObjectEdit myAdapterObjectEdit, MyAdapterObjectEdit.MyViewHolder myHolder, ViewGroup parentView, ArrayList<ObjectObjPic> pictureList, ObjectUser user){
        this.context             = context;
        this.myPictureList       = pictureList;
        this.myPictureListFull   = new ArrayList<>(this.myPictureList);
        this.parentAdapterObjectEdit = myAdapterObjectEdit;
        this.parentHolder        = myHolder;
        this.parentView          = parentView;
        this.myUser              = user;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        boolean myHoldIndicator;
        //ImageButton myImage;
        ImageView myExpandedImage;
        ImageView myImage;
        ProgressBar myProgressBar;
        LinearLayout myContainer;
        boolean bacgroundMarked;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myHoldIndicator = false;
            //---------------Element binding------------------------
            myImage         = itemView.findViewById(R.id.objectDetailsPicture_img);
            myExpandedImage = itemView.findViewById(R.id.expanded_image);
            myContainer     = itemView.findViewById(R.id.objectDetailsPicture_top_level);
            myProgressBar   = itemView.findViewById(R.id.objectDetailsPicture_progressBar);
        }
        public boolean isMyHoldIndicator() {return myHoldIndicator;}
        public void setMyHoldIndicator(boolean myHoldIndicator) {this.myHoldIndicator = myHoldIndicator;}
        public boolean isBacgroundMarked() {         return bacgroundMarked;      }
        public void setBacgroundMarked(boolean bacgroundMarked) {      this.bacgroundMarked = bacgroundMarked;      }
    }

    @NonNull
    @Override
    public MyAdapterObjectEditPicture.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_picture, parent, false);
        return new MyAdapterObjectEditPicture.MyViewHolder(view);
    }
    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
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
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String url = "";

        ObjectObjPic objectObjPic = myPictureList.get(holder.getAdapterPosition());
        holder.myContainer.setBackgroundColor(Color.TRANSPARENT);
        holder.setBacgroundMarked(false);

        //----------reading values from Internal DB
        SQLiteDB dbHelper = new SQLiteDB(context);
        Cursor result = dbHelper.getData();
        if(result.getCount() > 0 ){
            result.moveToNext();
            url = result.getString(1);
        }

        if(objectObjPic.getPicUri().length() > 0){
            //holder.myImage.setImageBitmap(decodeSampledBitmapFromResource(100, 100, objectObjPic, 4));
            //holder.myProgressBar.setVisibility(View.GONE);
            Glide.with(context)
                    .asBitmap()
                    .load(Uri.parse(objectObjPic.picUri))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().override(500,500))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.myProgressBar.setVisibility(View.GONE);
                            holder.myImage.setImageBitmap(resource);
                            //objectObjPic.setImageResource(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        }else{
            Glide.with(context)
                    .asBitmap()
                    .load(url + "/" + objectObjPic.getPicUrl())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().override(500,500))
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            holder.myProgressBar.setVisibility(View.GONE);
                            holder.myImage.setImageBitmap(resource);
                            //objectObjPic.setImageResource(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
            }


        //-----image click handling
        holder.myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!parentAdapterObjectEdit.getDeletionMode()){
                    if(!holder.isMyHoldIndicator()){
                        Intent intent = new Intent(context, ActivityPictureFullSizeView.class);
                        intent.putParcelableArrayListExtra("myPictureList", myPictureList);
                        intent.putExtra("myPosition", holder.getAdapterPosition());
                        context.startActivity(intent);
                    }else{
                        holder.setMyHoldIndicator(false);
                    }
                }else{
                    if(!holder.isMyHoldIndicator()) {
                        if (holder.isBacgroundMarked()) {
                            holder.setBacgroundMarked(false);
                            holder.myContainer.setBackgroundColor(Color.TRANSPARENT);
                            parentAdapterObjectEdit.removeToBeDeleted(objectObjPic);
                        } else {
                            holder.setBacgroundMarked(true);
                            holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow_opacity));
                            parentAdapterObjectEdit.addToBeDeleted(objectObjPic);
                        }
                    }else{
                        holder.setMyHoldIndicator(false);
                    }
                }
            }
        });

        holder.myImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.myImage.setSoundEffectsEnabled(false);
                holder.setMyHoldIndicator(true);
                parentAdapterObjectEdit.setDeletionMode(true);
                parentAdapterObjectEdit.addToBeDeleted(objectObjPic);
                parentHolder.setDeletionModeButtons(true);
                holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow));
                holder.setBacgroundMarked(true);
                holder.myImage.setSoundEffectsEnabled(true);
                return false;
            }
        });
    }
    @Override
    public int getItemCount() {
        return this.myPictureList.size();
    }


}
