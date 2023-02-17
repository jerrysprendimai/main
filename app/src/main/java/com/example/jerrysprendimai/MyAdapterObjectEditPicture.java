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
import android.graphics.PorterDuff;
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
import android.widget.TextView;
import android.widget.Toast;

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
        ImageView myImage, myImageUpl, myImageUplFailed, myImageUplLock;
        ProgressBar myProgressBar, myProgressBarUpl;
        TextView myPictureUname;
        LinearLayout myContainer;
        boolean bacgroundMarked;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myHoldIndicator = false;
            //---------------Element binding------------------------
            myImage          = itemView.findViewById(R.id.objectDetailsPicture_img);
            myExpandedImage  = itemView.findViewById(R.id.expanded_image);
            myImageUpl       = itemView.findViewById(R.id.objectDetailsPicture_upl);
            myImageUplFailed = itemView.findViewById(R.id.objectDetailsPicture_upl_failed);
            myContainer      = itemView.findViewById(R.id.objectDetailsPicture_top_level);
            myProgressBar    = itemView.findViewById(R.id.objectDetailsPicture_progressBar);
            myProgressBarUpl = itemView.findViewById(R.id.objectDetailsPicture_progressBar_upl);
            myImageUplLock   = itemView.findViewById(R.id.objectDetailsPicture_upl_lock);
            myPictureUname   = itemView.findViewById(R.id.objectDetailsPicture_uname);
        }
        public boolean isMyHoldIndicator() {return myHoldIndicator;}
        public void setMyHoldIndicator(boolean myHoldIndicator) {this.myHoldIndicator = myHoldIndicator;}
        public boolean isBacgroundMarked() {         return bacgroundMarked;      }
        public void setBacgroundMarked(boolean bacgroundMarked) {      this.bacgroundMarked = bacgroundMarked;      }

        public void callbackSetUpload() {
            this.myImageUpl.setVisibility(View.GONE);
        }
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
    public void callbackSetUpload(){

    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String url = "";

        ObjectObjPic objectObjPic = myPictureList.get(holder.getAdapterPosition());
        objectObjPic.setHolder(holder);

        holder.myImageUplLock.setVisibility(View.GONE);
        holder.myContainer.setBackgroundColor(Color.TRANSPARENT);
        holder.setBacgroundMarked(false);

        //----------reading values from Internal DB
        SQLiteDB dbHelper = new SQLiteDB(context);
        Cursor result = dbHelper.getData();
        if(result.getCount() > 0 ){
            result.moveToNext();
            url = result.getString(1);
        }
        dbHelper.close();

        holder.myImageUpl.setVisibility(View.GONE);
        holder.myImageUplFailed.setVisibility(View.GONE);
        holder.myProgressBarUpl.setVisibility(View.GONE);

        holder.myImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_picture_placeholder_white));
        holder.myProgressBar.setVisibility(View.VISIBLE);
        if(objectObjPic.getPicUri().length() > 0){

            holder.myImageUpl.setVisibility(View.VISIBLE);
            holder.myImageUpl.setColorFilter(context.getResources().getColor(R.color.jerry_blue), PorterDuff.Mode.SRC_ATOP);

            Glide.with(context)
                    .asBitmap()
                    .load(Uri.parse(objectObjPic.picUri))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .apply(new RequestOptions().override(500,500).centerInside())
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
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)

                    //.apply(new RequestOptions().override(500,500).centerInside())
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
        holder.myImage.setOnClickListener(v -> {
            ((ActivityObjectEdit)context).setBackButtonCount(0);
            if(!parentAdapterObjectEdit.getDeletionMode()){
                if( ((ActivityObjectEdit)context).getDeletePictureViewHolder() != null){
                    if(!((ActivityObjectEdit)context).getDeletePictureViewHolder().equals(parentHolder)){
                        for(int i = 0; i < parentAdapterObjectEdit.toBeDeletedList.size(); i++){
                            parentAdapterObjectEdit.toBeDeletedList.get(i).getHolder().myContainer.setBackgroundColor(Color.TRANSPARENT);
                        }
                        parentAdapterObjectEdit.toBeDeletedList.removeAll(parentAdapterObjectEdit.toBeDeletedList);
                        ((ActivityObjectEdit)context).getDeletePictureViewHolder().setDeletionModeButtons(false);
                        ((ActivityObjectEdit)context).setDeletePictureViewHolder(parentHolder);
                        //parentAdapterObjectEdit.setDeletionModeViewHolder(parentAdapterObjectEdit);
                    }
                }
                if(!holder.isMyHoldIndicator()){
                    Intent intent = new Intent(context, ActivityPictureFullSizeView.class);
                    intent.putParcelableArrayListExtra("myPictureList", myPictureList);
                    intent.putExtra("myUser", myUser);
                    intent.putExtra("myPosition", holder.getAdapterPosition());
                    context.startActivity(intent);
                }else{
                    holder.setMyHoldIndicator(false);
                }
            }else{
                if( ((ActivityObjectEdit)context).getDeletePictureViewHolder() != null){
                    if(!((ActivityObjectEdit)context).getDeletePictureViewHolder().equals(parentHolder)){
                        for(int i = 0; i < parentAdapterObjectEdit.toBeDeletedList.size(); i++){
                            parentAdapterObjectEdit.toBeDeletedList.get(i).getHolder().myContainer.setBackgroundColor(Color.TRANSPARENT);
                        }
                        parentAdapterObjectEdit.toBeDeletedList.removeAll(parentAdapterObjectEdit.toBeDeletedList);
                        ((ActivityObjectEdit)context).getDeletePictureViewHolder().setDeletionModeButtons(false);
                        ((ActivityObjectEdit)context).setDeletePictureViewHolder(parentHolder);
                        parentAdapterObjectEdit.setDeletionMode(false);
                        return;
                        //parentAdapterObjectEdit.setDeletionModeViewHolder(parentAdapterObjectEdit);
                    }
                }
                if(!holder.isMyHoldIndicator()) {
                    if (holder.isBacgroundMarked()) {
                        holder.setBacgroundMarked(false);
                        holder.myContainer.setBackgroundColor(Color.TRANSPARENT);
                        parentAdapterObjectEdit.removeToBeDeleted(parentHolder, objectObjPic);
                    } else {
                        if(!((ActivityObjectEdit)context).isUserMode()){
                            holder.setBacgroundMarked(true);
                            holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow_opacity));
                            parentAdapterObjectEdit.addToBeDeleted(objectObjPic);
                        }else{
                            if(objectObjPic.getUserId().equals(myUser.getId())){
                                holder.setBacgroundMarked(true);
                                holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow_opacity));
                                parentAdapterObjectEdit.addToBeDeleted(objectObjPic);
                            }
                        }
                    }
                }else{
                    holder.setMyHoldIndicator(false);
                }
            }
        });

        holder.myPictureUname.setHint(objectObjPic.getFirstName());
        holder.myPictureUname.setHintTextColor(context.getResources().getColor(R.color.teal_700));

        //---- user mode handling
        if (!((ActivityObjectEdit)context).isUserMode()){
            //----admin has an option to delete all pictures
            //parentAdapterObjectEdit.setDeletionModeViewHolder(holder);
            holder.myImage.setOnLongClickListener(v -> {
                if(!((ActivityObjectEdit)context).getObjectObject().getLockedByUserId().equals("0")){
                    Toast.makeText(context, context.getResources().getString(R.string.locked_by) + " "
                            + ((ActivityObjectEdit)context).getObjectObject().getLockedUname(), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if( ((ActivityObjectEdit)context).getDeletePictureViewHolder() != null){
                    if(!((ActivityObjectEdit)context).getDeletePictureViewHolder().equals(parentHolder)){
                        for(int i = 0; i < parentAdapterObjectEdit.toBeDeletedList.size(); i++){
                            parentAdapterObjectEdit.toBeDeletedList.get(i).getHolder().myContainer.setBackgroundColor(Color.TRANSPARENT);
                        }
                        parentAdapterObjectEdit.toBeDeletedList.removeAll(parentAdapterObjectEdit.toBeDeletedList);
                        ((ActivityObjectEdit)context).getDeletePictureViewHolder().setDeletionModeButtons(false);
                        ((ActivityObjectEdit)context).setDeletePictureViewHolder(parentHolder);
                        parentAdapterObjectEdit.setDeletionMode(false);
                        //parentAdapterObjectEdit.setDeletionModeViewHolder(parentAdapterObjectEdit);
                    }
                }

                holder.myImage.setSoundEffectsEnabled(false);
                holder.setMyHoldIndicator(true);
                parentAdapterObjectEdit.setDeletionMode(true);
                parentAdapterObjectEdit.addToBeDeleted(objectObjPic);
                parentHolder.setDeletionModeButtons(true);
                ((ActivityObjectEdit)context).setDeletePictureViewHolder(parentHolder);
                holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow));
                holder.setBacgroundMarked(true);
                holder.myImage.setSoundEffectsEnabled(true);
                return false;
            });
        }else{
            if(!objectObjPic.getUserId().equals(myUser.getId())){
              //holder.myPictureUname.setHintTextColor(context.getResources().getColor(R.color.jerry_grey));
              holder.myPictureUname.setTextColor(context.getResources().getColor(R.color.jerry_grey));
            }else{
              holder.myPictureUname.setTextColor(context.getResources().getColor(R.color.teal_700));
            }
            //----user can delete only own pictures
            holder.myImage.setOnLongClickListener(v -> {
                //---- locked object handling
                if(!(((ActivityObjectEdit)context).getObjectObject().getLockedByUserId().equals("0"))){
                    Toast.makeText(context, context.getResources().getString(R.string.locked_by) + " "
                            + ((ActivityObjectEdit)context).getObjectObject().getLockedUname(), Toast.LENGTH_SHORT).show();
                    return false;
                }
                if(objectObjPic.getUserId().equals(myUser.getId())){
                    if( ((ActivityObjectEdit)context).getDeletePictureViewHolder() != null){
                        if(!((ActivityObjectEdit)context).getDeletePictureViewHolder().equals(parentHolder)){
                            for(int i = 0; i < parentAdapterObjectEdit.toBeDeletedList.size(); i++){
                                parentAdapterObjectEdit.toBeDeletedList.get(i).getHolder().myContainer.setBackgroundColor(Color.TRANSPARENT);
                            }
                            parentAdapterObjectEdit.toBeDeletedList.removeAll(parentAdapterObjectEdit.toBeDeletedList);
                            ((ActivityObjectEdit)context).getDeletePictureViewHolder().setDeletionModeButtons(false);
                            ((ActivityObjectEdit)context).setDeletePictureViewHolder(parentHolder);
                            parentAdapterObjectEdit.setDeletionMode(false);
                            //parentAdapterObjectEdit.setDeletionModeViewHolder(parentAdapterObjectEdit);
                        }
                    }
                    holder.myImage.setSoundEffectsEnabled(false);
                    holder.setMyHoldIndicator(true);
                    parentAdapterObjectEdit.setDeletionMode(true);
                    parentAdapterObjectEdit.addToBeDeleted(objectObjPic);
                    parentHolder.setDeletionModeButtons(true);
                    ((ActivityObjectEdit)context).setDeletePictureViewHolder(parentHolder);
                    holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow));
                    holder.setBacgroundMarked(true);
                    holder.myImage.setSoundEffectsEnabled(true);
                }
                return false;
            });

        }
    }
    @Override
    public int getItemCount() {
        return this.myPictureList.size();
    }


}
