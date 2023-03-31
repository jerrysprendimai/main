package com.example.jerrysprendimai;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MyAdapterMessage extends RecyclerView.Adapter<MyAdapterMessage.MessageHolder> {

    final String user = "user";
    final String owner = "owner";
    final String admin = "admin";

    private String url;
    private ArrayList <ObjectMessage> messages;
    private String senderImg, recyverImg;
    private Context context;
    private ObjectUser myUser;
    private MyAdapterObjectEditPicture myAdapterObjectEditPicture;
    private ObjectObject object;
    private int backgroundJobs = 1;
    private boolean longClick;

    public MyAdapterMessage(ArrayList<ObjectMessage> messages, Context context, ObjectUser myUser, ObjectObject obj) {
        this.messages = messages;
        this.context = context;
        this.myUser = myUser;
        this.object = obj;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_row_message,parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        ObjectMessage objectMessage = messages.get(holder.getAdapterPosition());
        messages.get(holder.getAdapterPosition()).setHolder(holder);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(objectMessage.getMills()));

        holder.txtMessage.setText(objectMessage.getContent());
        holder.txtSenderName.setText(objectMessage.getFirstName());
        holder.txtTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d",calendar.get(Calendar.MINUTE)));
        holder.imageProgressBarUpl.setVisibility(View.GONE);

        //holder.txtDate.setText(objectMessage.getDate());
        if(((ActivityChat) context).isDateToBeDisplayed(objectMessage.getDate())){
            holder.txtDateSeparator.setVisibility(View.VISIBLE);
            holder.txtDate.setVisibility(View.VISIBLE);
            holder.txtDate.setText(((ActivityChat) context).getDateToDisplay());
            ((ActivityChat) context).setDateToDisplay(objectMessage.getDate());
        }else{
            holder.txtDateSeparator.setVisibility(View.GONE);
            holder.txtDate.setVisibility(View.GONE);
        }
        if((position == 0)&&(holder.txtDateSeparator.getVisibility() == View.GONE)){
            holder.txtDateSeparator.setVisibility(View.VISIBLE);
            holder.txtDate.setVisibility(View.VISIBLE);
            holder.txtDate.setText(((ActivityChat) context).getDateToDisplay());
            ((ActivityChat) context).setDateToDisplay(objectMessage.getDate());
        }

        if((!objectMessage.getPicUrl().isEmpty())||(!objectMessage.getPicUri().isEmpty())) {
            SQLiteDB dbHelper = new SQLiteDB(context);
            Cursor result = dbHelper.getData();
            if (result.getCount() > 0) {
                result.moveToNext();
                url = result.getString(1);
            }
            dbHelper.close();
            holder.cardView.setVisibility(View.GONE);
            holder.txtMessage.setVisibility(View.GONE);
            //holder.txtTime.setVisibility(View.GONE);
            holder.photoLinearLayout.setVisibility(View.VISIBLE);
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_picture_placeholder_white));
            if ((!objectMessage.getPicUri().isEmpty())&&(objectMessage.getUserId().equals(myUser.getId().toString()))) {
                Glide.with(context)
                        .asBitmap()
                        .load(Uri.parse(objectMessage.getPicUri()))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .apply(new RequestOptions().override(500,500).centerInside())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.imageProgressBar.setVisibility(View.GONE);
                                holder.image.setImageBitmap(resource);

                                objectMessage.setHolder(holder);

                                //if(!((ActivityChat)context).getBeingUpdated().contains(objectMessage)) {
                                //    ((ActivityChat)context).getBeingUpdated().add(objectMessage);

                                    ObjectObjPic newPic = new ObjectObjPic();
                                    newPic.setUserId(myUser.getId());
                                    newPic.setFirstName(myUser.getFirst_name());
                                    newPic.setPicUri(objectMessage.getPicUri());
                                    newPic.setObjectId(object.getId());
                                    newPic.setPosNr(0);
                                    newPic.setPicName(objectMessage.getPicName());
                                    new Thread(new RunnableTask(context, myUser, objectMessage, newPic, object, resource)).start();

                                    //thread.start();
                                    //((ActivityChat)context).setUploadRunning(((ActivityChat)context).getUploadRunning() + 1);
                                    //new HttpsRequestUploadPicture(context, newPic, objectMessage, myUser, object, resource).execute();
                                //}
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });

            } else {
                holder.imageProgressBar.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .asBitmap()
                        .load(url + "/" + objectMessage.getPicUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        //.skipMemoryCache(true)
                        .apply(new RequestOptions().override(500,500).centerInside())
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                holder.imageProgressBar.setVisibility(View.GONE);
                                holder.image.setImageBitmap(resource);

                                objectMessage.setHolder(holder);
                                //objectObjPic.setImageResource(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
            }
        }else{
            holder.cardView.setVisibility(View.VISIBLE);
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtTime.setVisibility(View.VISIBLE);
            holder.photoLinearLayout.setVisibility(View.GONE);
        }

        holder.image.setOnClickListener(v->{
            ArrayList<ObjectObjPic> myPictureList = new ArrayList<>();
            ObjectObjPic objectObjPic = new ObjectObjPic();
            objectObjPic.setPicUrl(objectMessage.getPicUrl());
            myPictureList.add(objectObjPic);
            Intent intent = new Intent(context, ActivityPictureFullSizeView.class);
            intent.putParcelableArrayListExtra("myPictureList", myPictureList);
            intent.putExtra("myUser", myUser);
            intent.putExtra("myPosition", 0);
            context.startActivity(intent);
        });
        holder.image.setOnLongClickListener(v->{
            return false;
        });

        if(objectMessage.getUserLv().equals(owner)){
            holder.profImage.setColorFilter(context.getResources().getColor(R.color.jerry_blue));
        }else if(objectMessage.getUserLv().equals(admin)){
            holder.profImage.setColorFilter(context.getResources().getColor(R.color.jerry_green));
        }else{
            holder.profImage.setColorFilter(context.getResources().getColor(R.color.teal_700));
        }

        ConstraintLayout constraintLayout = holder.constraintLayout;
        if(objectMessage.getUserId().equals(myUser.getId().toString())){
            holder.txtMessage.setBackground(context.getResources().getDrawable(R.drawable.background_my_message));
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profile_cardView, ConstraintSet.LEFT);
            constraintSet.clear(R.id.txt_message_content, ConstraintSet.LEFT);
            constraintSet.clear(R.id.chat_photo_LinearView, ConstraintSet.LEFT);
            constraintSet.connect(R.id.chat_photo_LinearView, ConstraintSet.RIGHT, R.id.ccLayout,ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.profile_cardView, ConstraintSet.RIGHT, R.id.ccLayout,ConstraintSet.RIGHT, 3);
            constraintSet.connect(R.id.txt_message_content, ConstraintSet.RIGHT,  R.id.profile_cardView,    ConstraintSet.LEFT, 3);

            constraintSet.clear(R.id.txt_time, ConstraintSet.LEFT);
            constraintSet.clear(R.id.txt_time, ConstraintSet.BOTTOM);

            if((!objectMessage.getPicUrl().isEmpty())||(!objectMessage.getPicUri().isEmpty())){
                constraintSet.connect(R.id.txt_time, ConstraintSet.LEFT, R.id.chat_photo_LinearView, ConstraintSet.LEFT, 3);
                constraintSet.connect(R.id.txt_time, ConstraintSet.TOP, R.id.chat_photo_LinearView, ConstraintSet.BOTTOM, 3);
            }else{
                constraintSet.connect(R.id.txt_time, ConstraintSet.LEFT, R.id.txt_message_content, ConstraintSet.LEFT, 3);
                constraintSet.connect(R.id.txt_time, ConstraintSet.TOP, R.id.txt_message_content, ConstraintSet.BOTTOM, 3);
            }


            constraintSet.clear(R.id.ccLayout,   ConstraintSet.TOP);
            constraintSet.connect(R.id.ccLayout, ConstraintSet.TOP, R.id.chat_date_line, ConstraintSet.BOTTOM, 3);

            constraintSet.applyTo(constraintLayout);
            holder.cardView.setVisibility(View.GONE);
            holder.txtSenderName.setVisibility(View.GONE);
        }else{
            holder.txtMessage.setBackground(context.getResources().getDrawable(R.drawable.background_message));
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profile_cardView, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.txt_message_content, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.chat_photo_LinearView, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.profile_cardView, ConstraintSet.LEFT, R.id.ccLayout,ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.chat_photo_LinearView, ConstraintSet.LEFT, R.id.profile_cardView,ConstraintSet.RIGHT, 0);
            constraintSet.connect(R.id.txt_message_content, ConstraintSet.LEFT, R.id.profile_cardView,ConstraintSet.RIGHT, 0);

            constraintSet.clear(R.id.txt_time, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.txt_time, ConstraintSet.BOTTOM);

            if(!objectMessage.getPicUrl().isEmpty()){
                constraintSet.connect(R.id.txt_time, ConstraintSet.RIGHT, R.id.chat_photo_LinearView, ConstraintSet.RIGHT, 3);
                constraintSet.connect(R.id.txt_time, ConstraintSet.TOP, R.id.chat_photo_LinearView, ConstraintSet.BOTTOM, 3);
            }else{
                constraintSet.connect(R.id.txt_time, ConstraintSet.RIGHT, R.id.txt_message_content, ConstraintSet.RIGHT, 3);
                constraintSet.connect(R.id.txt_time, ConstraintSet.TOP, R.id.txt_message_content, ConstraintSet.BOTTOM, 3);
            }

            constraintSet.clear(R.id.chat_date_line, ConstraintSet.LEFT);
            constraintSet.connect(R.id.chat_date_line, ConstraintSet.TOP, R.id.chat_mainContainer, ConstraintSet.TOP, 3);

            constraintSet.clear(R.id.ccLayout,   ConstraintSet.TOP);
            constraintSet.connect(R.id.ccLayout, ConstraintSet.TOP, R.id.chat_date_line, ConstraintSet.BOTTOM, 3);

            constraintSet.applyTo(constraintLayout);
            holder.cardView.setVisibility(View.VISIBLE);
            holder.txtSenderName.setVisibility(View.VISIBLE);
        }

        constraintLayout.setOnClickListener(v->{
            if(objectMessage.getUserId().equals(myUser.getId().toString())) {
                    if (((ActivityChat) context).isDeletionMode()) {
                        if (!holder.isBackgroundSet()) {
                            holder.constraintLayout.setBackground(context.getResources().getDrawable(R.drawable.round_button_blue_op));
                            holder.setBackgroundSet(true);
                            if (!((ActivityChat) context).getToBeDeleted().contains(holder)) {
                                ((ActivityChat) context).getToBeDeleted().add(holder);
                            }
                        } else {
                            holder.constraintLayout.setBackground(null);
                            holder.setBackgroundSet(false);
                            if (((ActivityChat) context).getToBeDeleted().contains(holder)) {
                                ((ActivityChat) context).getToBeDeleted().remove(holder);
                            }
                        }
                        if (((ActivityChat) context).getToBeDeleted().size() == 0) {
                            ((ActivityChat) context).setDeletionMode(false);
                            ((ActivityChat) context).setDeletionModeButtons(false);
                        }
                    }
                }
        });
        constraintLayout.setOnLongClickListener(v->{
            if(objectMessage.getUserId().equals(myUser.getId().toString())){
                //longClick = true;
                ((ActivityChat)context).setDeletionMode(true);
                holder.constraintLayout.setBackground(context.getResources().getDrawable(R.drawable.round_button_blue_op));
                ((ActivityChat)context).getToBeDeleted().add(holder);
                ((ActivityChat)context).setDeletionModeButtons(true);

            //constraintLayout.setSoundEffectsEnabled(false);
            //constraintLayout.setSoundEffectsEnabled(true);
            }
            return false;
        });

        if(objectMessage.isDeleted()){
            holder.constraintLayout.setVisibility(View.GONE);
            holder.txtDeleted.setVisibility(View.VISIBLE);
        }else{
            holder.constraintLayout.setVisibility(View.VISIBLE);
            holder.txtDeleted.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    class RunnableTask implements Runnable{
        Context context;
        ObjectUser user;
        ObjectMessage message;
        ObjectObjPic pic;
        ObjectObject object;
        Bitmap resource;
        public RunnableTask(Context context, ObjectUser user, ObjectMessage objectMessage, ObjectObjPic newPic, ObjectObject object, Bitmap resource) {
            this.context = context;
            this.user = user;
            this.message = objectMessage;
            this.pic = newPic;
            this.object = object;
            this.resource = resource;
        }

        @Override
        public void run() {
            boolean go = true;
            while (go){
                if(backgroundJobs >0){
                    backgroundJobs -= 1;
                    new HttpsRequestUploadPicture(context, pic, message, user, object, resource).execute();
                    go = false;
                }else{
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class MessageHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        TextView txtMessage, txtSenderName, txtTime, txtDateSeparator, txtDate, txtDeleted;
        boolean backgroundSet;
        LinearLayout photoLinearLayout, imageContainer;
        ImageView profImage, image;
        ProgressBar imageProgressBar, imageProgressBarUpl;
        CardView cardView;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout  = itemView.findViewById(R.id.ccLayout);
            txtMessage        = itemView.findViewById(R.id.txt_message_content);
            txtSenderName     = itemView.findViewById(R.id.message_senderName);
            txtDate           =  itemView.findViewById(R.id.chat_date);
            txtTime           = itemView.findViewById(R.id.txt_time);
            txtDateSeparator  = itemView.findViewById(R.id.chat_date_line);
            profImage         = itemView.findViewById(R.id.senderImage);
            cardView          = itemView.findViewById(R.id.profile_cardView);
            txtDeleted        = itemView.findViewById(R.id.chat_deleted);
            photoLinearLayout = itemView.findViewById(R.id.chat_photo_LinearView);
            image             = itemView.findViewById(R.id.chat_img);
            imageContainer    = itemView.findViewById(R.id.chat_img_container);
            imageProgressBar  = itemView.findViewById(R.id.chat_img_progressBar);
            imageProgressBarUpl = itemView.findViewById(R.id.chat_progressBar_upl);

            backgroundSet = false;
        }

        public boolean isBackgroundSet() {   return backgroundSet;  }
        public void setBackgroundSet(boolean backgroundSet) {   this.backgroundSet = backgroundSet;   }

    }


    class HttpsRequestUploadPicture extends AsyncTask<String, String, InputStream> {
        private static final String upload_picture_url = "upload_picture.php";
        private Context context;
        Connector connector;
        ObjectObjPic objectObjPic;
        ObjectMessage message;
        ObjectUser user;
        ObjectObject object;
        Bitmap resource;

        public HttpsRequestUploadPicture(Context ctx, ObjectObjPic objectPic, ObjectMessage objectMessage, ObjectUser user, ObjectObject object, Bitmap resource){
            this.context = ctx;
            this.objectObjPic = objectPic;
            this.message = objectMessage;
            this.user = user;
            this.object = object;
            this.resource = resource;
        }

        @Override
        protected void onProgressUpdate(String... value) {
            try {
                if (value[0].equals("start")) {
                    message.getHolder().imageProgressBarUpl.setVisibility(View.VISIBLE);
                    message.getHolder().imageProgressBarUpl.setProgress(0);
                } else if(value[0].equals("one")) {
                    message.getHolder().imageProgressBarUpl.setProgress(30);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(message.getHolder().imageProgressBarUpl, "progress", 5,30);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("two")) {
                    message.getHolder().imageProgressBarUpl.setProgress(85);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(message.getHolder().imageProgressBarUpl, "progress", 30,80);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("three")) {
                    message.getHolder().imageProgressBarUpl.setProgress(95);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(message.getHolder().imageProgressBarUpl, "progress", 80,95);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("four")) {
                    message.getHolder().imageProgressBarUpl.setProgress(99);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(message.getHolder().imageProgressBarUpl, "progress", 95,100);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("done")) {
                    message.getHolder().imageProgressBarUpl.setProgress(100);
                    message.getHolder().imageProgressBarUpl.setVisibility(View.GONE);
                }
            }catch (Exception e){
            }

            super.onProgressUpdate(value);
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            String str_img = "";
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap bitmap = resource;
                //bitmap =  ((BitmapDrawable)message.getHolder().image.getDrawable()).getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArray= baos.toByteArray();
                str_img = android.util.Base64.encodeToString(byteArray, Base64.DEFAULT);
            }catch (Exception e){
                e.printStackTrace();
            }

            publishProgress("start");
            connector = new Connector(context, upload_picture_url);
            connector.addPostParameter("objectId",      MCrypt2.encodeToString(String.valueOf(object.getId())));
            connector.addPostParameter("pictureName",   MCrypt2.encodeToString(objectObjPic.getPicName()));
            publishProgress("one");
            connector.addPostParameter("pictureSource", str_img);
            publishProgress("two");
            connector.send();
            publishProgress("three");
            connector.receive();
            publishProgress("four");
            connector.disconnect();
            String result = connector.getResult();
            publishProgress("done");
            result = result;
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            //((ActivityChat)context).setUploadRunning(((ActivityChat)context).getUploadRunning() -1);
            try {
                connector.decodeResponse();
                JSONObject responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String saveStatus = MCrypt.decryptSingle(responseObject.getString("status"));
                String msg        = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (saveStatus.equals("1")) {
                    objectObjPic.setPicUrl(msg);
                    objectObjPic.setPicUri("");

                    message.setPicUrl(msg);
                    message.setPicUri("");
                    //---Save message to Firebase
                    FirebaseDatabase.getInstance()
                            .getReference("objects/" + object.getId().toString())
                            .child(message.getKey())
                            .setValue(new ObjectMessage(message.getFirstName(),
                                                        message.getUname(),
                                                        message.getUserId(),
                                                        message.getContent(),
                                                        message.getDate(),
                                                        message.getTime(),
                                                        message.getMills(),
                                                        message.getUserLv(),
                                                        msg,
                                                        "",
                                                        message.getPicName(),
                                                        message.isDeleted(),
                                                        message.getUsers()));
                    //((ActivityChat)context).getBeingUpdated().remove(message.getKey());

                }else{
                    //error handling?
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            backgroundJobs += 1;
            super.onPostExecute(inputStream);
        }
        private String getPicArrayListJson(ArrayList<ObjectObjPic> pictureList){
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i < pictureList.size(); i++){
                jsonArray.put(pictureList.get(i).toJson());
            }
            return jsonArray.toString();
        }
        private String getDetailsArrayListJson(ArrayList<ObjectObjDetails> detailsList){
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i < detailsList.size(); i++){
                jsonArray.put(detailsList.get(i).toJson());
            }
            return jsonArray.toString();
        }
    }
}
