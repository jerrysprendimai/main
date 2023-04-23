package com.example.jerrysprendimai;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MyAdapterOrderPicture extends RecyclerView.Adapter<MyAdapterOrderPicture.MyViewHolder>{

    Context context;
    ArrayList<ObjectObjPic> myPictureList;
    ArrayList<ObjectObjPic> myPictureListFull;
    ObjectUser myUser;
    //ViewGroup parentView;
    //MyAdapterObjectEdit parentAdapterObjectEdit;
    //MyAdapterObjectEdit.MyViewHolder parentHolder;

    public MyAdapterOrderPicture(Context context, ArrayList<ObjectObjPic> pictureList, ObjectUser user){
        this.context             = context;
        this.myPictureList       = pictureList;
        this.myPictureListFull   = new ArrayList<>(this.myPictureList);
        //this.parentAdapterObjectEdit = myAdapterObjectEdit;
        //this.parentHolder        = myHolder;
        //this.parentView          = parentView;
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
    public MyAdapterOrderPicture.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_picture, parent, false);

        return new MyAdapterOrderPicture.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String url = "";

        ObjectObjPic objectObjPic = myPictureList.get(holder.getAdapterPosition());
        //objectObjPic.setHolder(holder);

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

        holder.myPictureUname.setVisibility(View.GONE);
        holder.myImageUpl.setVisibility(View.GONE);

        //-----image click handling
        holder.myImage.setOnClickListener(v -> {
            //((ActivityObjectEdit)context).setBackButtonCount(0);
            if(!((ActivityOrder2)context).isDeletionMode()){
                /*if( ((ActivityObjectEdit)context).getDeletePictureViewHolder() != null){
                    if(!((ActivityObjectEdit)context).getDeletePictureViewHolder().equals(parentHolder)){
                        for(int i = 0; i < parentAdapterObjectEdit.toBeDeletedList.size(); i++){
                            parentAdapterObjectEdit.toBeDeletedList.get(i).getHolder().myContainer.setBackgroundColor(Color.TRANSPARENT);
                        }
                        parentAdapterObjectEdit.toBeDeletedList.removeAll(parentAdapterObjectEdit.toBeDeletedList);
                        ((ActivityObjectEdit)context).getDeletePictureViewHolder().setDeletionModeButtons(false);
                        ((ActivityObjectEdit)context).setDeletePictureViewHolder(parentHolder);
                        //parentAdapterObjectEdit.setDeletionModeViewHolder(parentAdapterObjectEdit);
                    }
                }*/
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
                /*if( ((ActivityObjectEdit)context).getDeletePictureViewHolder() != null){
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
                }*/
                if(!holder.isMyHoldIndicator()) {
                    if (holder.isBacgroundMarked()) {
                        holder.setBacgroundMarked(false);
                        holder.myContainer.setBackgroundColor(Color.TRANSPARENT);
                        ((ActivityOrder2)context).getToBeDeletedList().remove(objectObjPic);
                        //parentAdapterObjectEdit.removeToBeDeleted(parentHolder, objectObjPic);
                        if(((ActivityOrder2)context).getToBeDeletedList().size() == 0){
                            ((ActivityOrder2)context).setDeletionMode(false);
                            ((ActivityOrder2)context).deletionModeButtons.setVisibility(View.GONE);
                            ((ActivityOrder2)context).addModeButtons.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.setBacgroundMarked(true);
                        holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow_opacity));
                        ((ActivityOrder2)context).getToBeDeletedList().add(objectObjPic);
                    }
                }else{
                    holder.setMyHoldIndicator(false);
                }
            }
        });

        //holder.myPictureUname.setHint(objectObjPic.getFirstName());
        //holder.myPictureUname.setHintTextColor(context.getResources().getColor(R.color.teal_700));

        //---- user mode handling

            //----admin has an option to delete all pictures
            //parentAdapterObjectEdit.setDeletionModeViewHolder(holder);
            holder.myImage.setOnLongClickListener(v -> {
                holder.myImage.setSoundEffectsEnabled(false);
                holder.setMyHoldIndicator(true);
                ((ActivityOrder2)context).setDeletionMode(true);
                ((ActivityOrder2)context).deletionModeButtons.setVisibility(View.VISIBLE);
                ((ActivityOrder2)context).addModeButtons.setVisibility(View.GONE);

                ((ActivityOrder2)context).setDeletionMode(true);
                ((ActivityOrder2)context).deletionModeButtons.setVisibility(View.VISIBLE);
                ((ActivityOrder2)context).addModeButtons.setVisibility(View.GONE);
                ((ActivityOrder2)context).toBeDeletedList.add(objectObjPic);
                holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow));
                holder.setBacgroundMarked(true);
                holder.myImage.setSoundEffectsEnabled(true);
                return false;
            });

        /*else{
            if(!objectObjPic.getUserId().equals(myUser.getId())){
              holder.myPictureUname.setHintTextColor(context.getResources().getColor(R.color.jerry_grey));
              holder.myPictureUname.setTextColor(context.getResources().getColor(R.color.jerry_grey));
            }else{
              holder.myPictureUname.setTextColor(context.getResources().getColor(R.color.teal_700));
            }
            //----user can delete only own pictures
            holder.myImage.setOnLongClickListener(v -> {
                //---- locked object handling
                if(!((ActivityObjectEdit)context).getObjectObject().getLockedByUserId().equals("0")){
                    if(!((ActivityObjectEdit)context).getObjectObject().getLockedByUserId().equals(myUser.getId().toString())){
                    Toast.makeText(context, context.getResources().getString(R.string.locked_by) + " "
                            + ((ActivityObjectEdit)context).getObjectObject().getLockedUname(), Toast.LENGTH_SHORT).show();
                    return false;
                    }
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

        }*/
    }
    @Override
    public int getItemCount() {
        return this.myPictureList.size();
    }


}
