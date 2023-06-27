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
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
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
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MyAdapterOrderPicture extends RecyclerView.Adapter<MyAdapterOrderPicture.MyViewHolder>{
    private final String objectShowP1 = "pictureShowP1";
    private final String objectShowP3 = "pictureShowP3";

    Context context;
    ArrayList<ObjectObjPic> myPictureList;
    ArrayList<ObjectObjPic> myPictureListFull;
    ObjectUser myUser;
    String type;
    //ViewGroup parentView;
    //MyAdapterObjectEdit parentAdapterObjectEdit;
    //MyAdapterObjectEdit.MyViewHolder parentHolder;

    public MyAdapterOrderPicture(Context context, ArrayList<ObjectObjPic> pictureList, ObjectUser user, String type){
        this.context             = context;
        this.myPictureList       = pictureList;
        this.myPictureListFull   = new ArrayList<>(this.myPictureList);
        this.myUser              = user;
        this.type                = type;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        boolean myHoldIndicator;
        //ImageButton myImage;
        ImageView myExpandedImage;
        ImageView myImage, myImageUpl, myImageUplFailed, myImageUplLock;
        ProgressBar myProgressBar, myProgressBarUpl;
        TextView myPictureUname, myAttachmentName;
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
            myAttachmentName = itemView.findViewById(R.id.objectDetailsPicture_name);
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
        objectObjPic.setOrderPicHolder(holder);
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

        if(objectObjPic.getMimeType().contains("image/")){
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
        }else{
            holder.myAttachmentName.setVisibility(View.VISIBLE);
            holder.myAttachmentName.setText(objectObjPic.getPicName());
            holder.myProgressBar.setVisibility(View.GONE);
            if(objectObjPic.getMimeType().contains("pdf")){
                holder.myImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_svg_pdf));
            }else if(objectObjPic.getMimeType().contains("doc")){
                holder.myImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_svg_doc));
            }else if(objectObjPic.getMimeType().contains("ppt")){
                holder.myImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_svg_ppt));
            }else if(objectObjPic.getMimeType().contains("xls")){
                holder.myImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_svg_xls));
            }else if(objectObjPic.getMimeType().contains("txt")){
                holder.myImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_svg_txt));
            }
        }

        holder.myPictureUname.setVisibility(View.GONE);
        holder.myImageUpl.setVisibility(View.GONE);

        //-----image click handling
        holder.myImage.setOnClickListener(v -> {
            //((ActivityObjectEdit)context).setBackButtonCount(0);
            if(!((ActivityOrder1)context).isDeletionMode()){
                if(!holder.isMyHoldIndicator()){
                    if(objectObjPic.getMimeType().contains("image/")){
                        Intent intent = new Intent(context, ActivityPictureFullSizeView.class);
                        intent.putParcelableArrayListExtra("myPictureList", myPictureList);
                        intent.putExtra("myUser", myUser);
                        intent.putExtra("myPosition", holder.getAdapterPosition());
                        context.startActivity(intent);
                    }else{
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(objectObjPic.getFilePath())), objectObjPic.getMimeType());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }else{
                    holder.setMyHoldIndicator(false);
                }
            }else{
                if(!holder.isMyHoldIndicator()) {
                    if (holder.isBacgroundMarked()) {
                        holder.setBacgroundMarked(false);
                        holder.myContainer.setBackgroundColor(Color.TRANSPARENT);
                        ((ActivityOrder1)context).getToBeDeletedList().remove(objectObjPic);
                        //parentAdapterObjectEdit.removeToBeDeleted(parentHolder, objectObjPic);
                        if(((ActivityOrder1)context).getToBeDeletedList().size() == 0){
                            ((ActivityOrder1)context).setDeletionMode(false);
                            ((ActivityOrder1)context).fragmentOrderPart2.deletionModeButtons.setVisibility(View.GONE);
                            ((ActivityOrder1)context).fragmentOrderPart2.addModeButtons.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.setBacgroundMarked(true);
                        holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow_opacity));
                        ((ActivityOrder1)context).getToBeDeletedList().add(objectObjPic);
                    }
                }else{
                    holder.setMyHoldIndicator(false);
                }
            }
        });

            if(!type.equals(objectShowP3)){
                holder.myImage.setOnLongClickListener(v -> {
                    holder.myImage.setSoundEffectsEnabled(false);
                    holder.setMyHoldIndicator(true);
                    ((ActivityOrder1)context).setDeletionMode(true);
                    ((ActivityOrder1)context).fragmentOrderPart2.deletionModeButtons.setVisibility(View.VISIBLE);
                    ((ActivityOrder1)context).fragmentOrderPart2.addModeButtons.setVisibility(View.GONE);

                    ((ActivityOrder1)context).setDeletionMode(true);
                    ((ActivityOrder1)context).fragmentOrderPart2.deletionModeButtons.setVisibility(View.VISIBLE);
                    ((ActivityOrder1)context).fragmentOrderPart2.addModeButtons.setVisibility(View.GONE);
                    ((ActivityOrder1)context).toBeDeletedList.add(objectObjPic);
                    holder.myContainer.setBackgroundColor(context.getResources().getColor(R.color.jerry_yellow));
                    holder.setBacgroundMarked(true);
                    holder.myImage.setSoundEffectsEnabled(true);
                    return false;
                });
            }

    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = ((ActivityOrder1)context).managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
    @Override
    public int getItemCount() {
        return this.myPictureList.size();
    }


}
