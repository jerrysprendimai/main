package com.example.jerrysprendimai;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityChat extends AppCompatActivity{
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 10;
    static final int MY_WRITE_EXTERNAL_STORAGE = 101;

    public  ObjectUser myUser;
    public  ObjectObject myObject;
    private ArrayList<ObjectObjUser> objectUserArrayList;
    private ArrayList<ObjectUser> employeeList;
    private ArrayList<ObjectUser> ownerList;

    //private ArrayList<ObjectObject> myObjectListOriginal;
    public ArrayList<MyAdapterMessage.MessageHolder> toBeDeleted;

    private ArrayList<String> myDisplayDates;
    public String currentDate, mCurrentPhotoPath, dateToDisplay;
    private LinearLayout participantsButton, objectNameButton, objectIconButton, toolsLayout;
    private RecyclerView recyclerView;
    private EditText editMessageInput;
    private TextView txtChattingAbout, txtChattingAbout2, txtChatParticipantCount;
    private ProgressBar progressBar;
    private ImageView imgToolBar, sendButton, toolsBackButton, toolsEditButton, toolsDeleteButton, attachmentButton, cameraButton;
    public boolean doNavigationToActivityObjectEdit, deletionMode;
    public Uri mCurrentPhotoUri;
    public File mPhotoFile;
    private int backgroundJobs = 1;
    public ImageView hiddenPicture;
    public int threadStartedCount;
    public ArrayList<ObjectObjPic> myPictureList;

    private String chatRoomId;
    int uploadRunning;
    ArrayList<ObjectMessage> beingUpdated;

    private MyAdapterMessage myAdapterMessage;
    private ArrayList<ObjectMessage> messages;
    private ArrayList<ObjectMessage> messagesUnseenToSeen;
    public ValueEventListener valueEventListener;

    final String user = "user";
    final String admin = "admin";
    final String owner = "owner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        //----bind screen elements
        recyclerView            = findViewById(R.id.chat_recyclerView);
        editMessageInput        = findViewById(R.id.chat_editText);
        txtChattingAbout        = findViewById(R.id.chat_roomName);
        txtChattingAbout2       = findViewById(R.id.chat_roomName2);
        txtChatParticipantCount = findViewById(R.id.chat_participantsCount);
        progressBar             = findViewById(R.id.chat_progressBar);
        imgToolBar              = findViewById(R.id.chat_room_image);
        sendButton              = findViewById(R.id.chat_sendButton);
        participantsButton      = findViewById(R.id.chat_participantsButton);
        objectNameButton        = findViewById(R.id.chat_bojectNameButton);
        objectIconButton        = findViewById(R.id.chat_iconButton);
        toolsLayout             = findViewById(R.id.chat_tools);
        toolsBackButton         = findViewById(R.id.chat_tools_back);
        //toolsEditButton         = findViewById(R.id.chat_tools_edit);
        toolsDeleteButton       = findViewById(R.id.chat_tools_delete);
        attachmentButton        = findViewById(R.id.chat_attachment);
        cameraButton            = findViewById(R.id.chat_camera);
        hiddenPicture           = findViewById(R.id.chat_hidden_container);

        messages = new ArrayList<>();
        messagesUnseenToSeen = new ArrayList<>();

        //---------------Read Intent values----------------------
        this.myUser               = getIntent().getParcelableExtra("myUser");
        this.myObject             = getIntent().getParcelableExtra("objectObject");
        this.objectUserArrayList  = getIntent().getParcelableArrayListExtra("listUser");
        this.employeeList         = getIntent().getParcelableArrayListExtra("employeeList");
        this.ownerList            = getIntent().getParcelableArrayListExtra("ownerList");

        //----Initialize screen values
        this.threadStartedCount = 0;
        this.uploadRunning = 0;
        this.myPictureList = new ArrayList<>();
        this.beingUpdated = new ArrayList<>();
        toBeDeleted = new ArrayList<>();
        myDisplayDates = new ArrayList<>();
        chatRoomId = myObject.getId().toString();
        imgToolBar.setImageResource(getResources().getIdentifier(myObject.getIcon(), "drawable", getApplicationInfo().packageName));
        txtChattingAbout.setText(myObject.getObjectName());
        txtChattingAbout2.setText(myObject.getObjectAddress());
        txtChatParticipantCount.setText("+" + String.valueOf(this.objectUserArrayList.size()));

        ArrayList<ObjectMessage.User> userSeenArrayList = new ArrayList<>();
        String[] temp = {"1","2","3"};
        //ArrayList<String> data = new ArrayList<>();
        for (int i=0; i< getObjectUserArrayList().size(); i++){
            //data.add(getObjectUserArrayList().get(i).getUserId().toString(), false);
            userSeenArrayList.add(new ObjectMessage.User(getObjectUserArrayList().get(i).getUserId().toString(), false));
        }
        for(int i=0; i<getOwnerList().size(); i++){
            userSeenArrayList.add(new ObjectMessage.User(getOwnerList().get(i).getId().toString(), false));
        }
        //----Listeners
        Context context = this;
        //-----------add picture handler
        attachmentButton.setOnClickListener(v->{
            lockView();
            //check permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //permission not granted, request it
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    //show popup for runtime permission
                    ((ActivityChat)context).requestPermissions(permissions, PERMISSION_CODE);
                }else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }else{
                //system os is less that marshmallow
                pickImageFromGallery();
            }
        });
        //-----------take new photo handler
        cameraButton.setOnClickListener(v->{
            lockView();
            //check permission camera
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ((ActivityChat) context).requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }else{
                    //check permission external storage
                    if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ((ActivityChat)context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
                    } else {
                        takeNewPhoto();
                    }
                }
            }else{
                takeNewPhoto();
            }
        });
        editMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0){
                    Animation slideIn = AnimationUtils.loadAnimation(context, R.anim.fadein);
                    attachmentButton.setAnimation(slideIn);
                    attachmentButton.setVisibility(View.VISIBLE);
                    cameraButton.setAnimation(slideIn);
                    cameraButton.setVisibility(View.VISIBLE);
                }else{
                    Animation slideOut = AnimationUtils.loadAnimation(context, R.anim.fadeout);
                    attachmentButton.setAnimation(slideOut);
                    attachmentButton.setVisibility(View.GONE);
                    cameraButton.setAnimation(slideOut);
                    cameraButton.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        toolsBackButton.setOnClickListener(v->{
            setDeletionModeButtons(false);
            setDeletionMode(false);
            clearToBeDeleted();
            setToBeDeleted(new ArrayList<>());
        });
        /*toolsEditButton.setOnClickListener(v->{
        });*/
        toolsDeleteButton.setOnClickListener(v->{
            setDeletionModeButtons(false);
            setDeletionMode(false);
            clearToBeDeleted();
            for(int i = 0; i < getToBeDeleted().size(); i++){
                ObjectMessage message = messages.get(getToBeDeleted().get(i).getAdapterPosition());
                message.setDeleted(true);
                FirebaseDatabase.getInstance()
                        .getReference("objects/" + chatRoomId)
                        .child(message.getKey())
                        .setValue(new ObjectMessage( message.getFirstName(),
                                                     message.getUname(),
                                                     message.getUserId(),
                                                     message.getContent(),
                                                     message.getDate(),
                                                     message.getTime(),
                                                     message.getMills(),
                                                     message.getUserLv(),
                                                     message.getPicUrl(),
                                                     message.getPicUri(),
                                                     message.getPicName(),
                                                     message.isDeleted(),
                                                     message.getUsers()));
                        //.removeValue();
            }
            setToBeDeleted(new ArrayList<>());
        });
        sendButton.setOnClickListener(v->{
            if(editMessageInput.length() == 0){
                return;
            }
            HashMap<String, String> usersHashMap = new HashMap<String, String>();
            boolean contains = false;
            for(int i=0; i<getObjectUserArrayList().size(); i++){
                 if(myUser.getId().equals(getObjectUserArrayList().get(i).getUserId())){
                     contains = true;
                     usersHashMap.put(getObjectUserArrayList().get(i).getUserId().toString(), "true");
                 }else{
                     usersHashMap.put(getObjectUserArrayList().get(i).getUserId().toString(), "false");
                 }
            }
            for(int i=0;i<getOwnerList().size(); i++){
                if(myUser.getId().equals(getOwnerList().get(i).getId())){
                    usersHashMap.put(getOwnerList().get(i).getId().toString(), "true");
                }else{
                    usersHashMap.put(getOwnerList().get(i).getId().toString(), "false");
                }
            }

            //HashMap<String, HashMap> users = new HashMap<String, HashMap>();
            //users.put("users", usersHashMap);
            FirebaseDatabase.getInstance()
                    .getReference("objects/" + chatRoomId)
                    .push()
                    .setValue(new ObjectMessage(myUser.getFirst_name(),
                                                myUser.getUname(),
                                                myUser.getId().toString(),
                                                editMessageInput.getText().toString(),
                                                HelperDate.get_current_date_disply(),
                                                Calendar.getInstance().getTime().toString(),
                                                String.valueOf(Calendar.getInstance().getTimeInMillis()),
                                                myUser.getUser_lv(),
                                                "",
                                                "",
                                                "",
                                                false,
                                                usersHashMap
                                                ));
            //----send notification
            sendMessageNotification();
            editMessageInput.setText("");
        });

        //Context context = this;
        View.OnClickListener toProjectListener = v -> {
            lockView();
            setDoNavigationToActivityObjectEdit(true);
            if((myUser.getUser_lv().equals(owner))||(myUser.getUser_lv().equals(admin))){
                //lock object
                new HttpsRequestLockObject(context, myObject, myObject.getId().toString(),"lock").execute();
            }else{
                //display object
                myObject.setNotViewed("");
                new HttpsRequestViewObject(context, myObject, myObject.getId().toString(), myObject.getNotViewed()).execute();
            }
        };
        objectNameButton.setOnClickListener(toProjectListener);
        objectIconButton.setOnClickListener(toProjectListener);

            participantsButton.setOnClickListener(v ->{
            lockView();
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_object_user_assignment, findViewById(R.id.chat_main_containerView), false);
            MyAdapterUserAssignmentShow myAdapterUserAssignmentShow = null;
            if((myUser.getUser_lv().equals(admin)) || (myUser.getUser_lv().equals(owner))){
              myAdapterUserAssignmentShow = new MyAdapterUserAssignmentShow(this, employeeList, myObject, objectUserArrayList, myUser);
            }else if(myUser.getUser_lv().equals(user)){
                ArrayList<ObjectUser> selectedEmployees = new ArrayList<>();
                for(ObjectUser employee: employeeList){
                    for(ObjectObjUser assignedEmpl: objectUserArrayList){
                        if(assignedEmpl.getUserId().equals(employee.getId())){
                            selectedEmployees.add(employee);
                            break;
                        }
                    }
                }
                myAdapterUserAssignmentShow = new MyAdapterUserAssignmentShow(this, selectedEmployees, myObject, objectUserArrayList, myUser);
            }

            RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.my_recycle_view_user);
            dialogRecyclerView.setAdapter(myAdapterUserAssignmentShow);
            dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setView(dialogView);

            if((myUser.getUser_lv().equals(admin)) || (myUser.getUser_lv().equals(owner))){
                builder.setPositiveButton("Ok", (dialog, which) -> {
                ArrayList assignedUserIdList = new ArrayList();
                //----changes to DB
                for(int i = 0; i < employeeList.size(); i++ ){
                   ObjectUser objUsr = employeeList.get(i);
                   if(objUsr.getChecked().equals(true)){
                      assignedUserIdList.add(objUsr.getId());
                   }
                    }
                    new HttpsRequestSetObjectUser(this, myObject, assignedUserIdList).execute();
                    });
                    builder.setNegativeButton("Cancel", (dialog, which)->{
                        unlockView();
                    });

                }

                builder.setOnDismissListener(dialog -> {
                    unlockView();
                });
                builder.create();
                builder.show();
            });


        myAdapterMessage = new MyAdapterMessage(messages, this, myUser, myObject, employeeList, ownerList, objectUserArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapterMessage);
        setUpChatRoom();

        KeyboardVisibilityEvent.setEventListener(this, new KeyboardVisibilityEventListener() {
            @Override
            public void onVisibilityChanged(boolean isOpen) {
                if((isOpen) && (recyclerView != null)){
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            }
        });

    }

    public void lockView(){
        for(ObjectMessage objectMessage : messages){
            try{
                objectMessage.getHolder().image.setEnabled(false);
            }catch(Exception e){}
        }
        participantsButton.setEnabled(false);
        attachmentButton.setEnabled(false);
        cameraButton.setEnabled(false);
        sendButton.setEnabled(false);
        objectNameButton.setEnabled(false);
        objectIconButton.setEnabled(false);
        editMessageInput.setEnabled(false);
    }
    public void unlockView(){
        for(ObjectMessage objectMessage : messages){
            try{
                objectMessage.getHolder().image.setEnabled(true);
            }catch(Exception e){}
        }
        participantsButton.setEnabled(true);
        attachmentButton.setEnabled(true);
        cameraButton.setEnabled(true);
        sendButton.setEnabled(true);
        objectNameButton.setEnabled(true);
        objectIconButton.setEnabled(true);
        editMessageInput.setEnabled(true);
    }

    public void sendMessageNotification(){
        String title   = myObject.getObjectName()+"   #"+myObject.getId().toString();
        String message = myUser.getFirst_name() +": "+editMessageInput.getText().toString();//messages.get(messages.size()-1).getFirstName() +": "+ messages.get(messages.size()).getContent();
        for (int i=0; i< getObjectUserArrayList().size(); i++){
            ObjectObjUser objectObjUser = getObjectUserArrayList().get(i);
            Integer userListId = getObjectUserArrayList().get(i).getUserId();
            Integer myUserId = myUser.getId();
            String type = "message";
            if((!getObjectUserArrayList().get(i).getToken().isEmpty()) &&
                    (!getObjectUserArrayList().get(i).getUserId().equals(myUser.getId()))){
                if(message.isEmpty()){
                    type = "image";
                }
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                        getObjectUserArrayList().get(i).getToken(),
                        title,
                        message,
                        type,
                        myObject.getId().toString(),
                        myObject.getIcon(),
                        getApplicationContext(),
                        ActivityMain.getActivityMain());
                notificationsSender.SendNotifications();
            }
        }
        for(int i=0; i<getOwnerList().size(); i++){
            ObjectUser objectOwner = getOwnerList().get(i);
            Integer ownerListId = getOwnerList().get(i).getId();
            Integer myUserId = myUser.getId();
            String type = "message";
            if((!getOwnerList().get(i).getToken().isEmpty()) &&
                    (!getOwnerList().get(i).getId().equals(myUser.getId()))){
                if(message.isEmpty()){
                    type = "image";
                }
                FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                        getOwnerList().get(i).getToken(),
                        title,
                        message,
                        type,
                        myObject.getId().toString(),
                        myObject.getIcon(),
                        getApplicationContext(),
                        ActivityMain.getActivityMain());
                notificationsSender.SendNotifications();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //-----take picture
        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE ){
            galleryAddPic();
            /*ObjectObjPic newPic = new ObjectObjPic();
            newPic.setUserId(myUser.getId());
            newPic.setFirstName(myUser.getFirst_name());
            newPic.setPicUri(getmCurrentPhotoUri().toString());
            newPic.setObjectId(myObject.getId());
            newPic.setPosNr(0);
            newPic.setPicName(getmPhotoFile().getName());*/

            String url = "";
            String uri = getmCurrentPhotoUri().toString();
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new RunnableTask(this, myUser, myObject, uri, "chat_" + getmPhotoFile().getName(), hiddenPicture)).start();
            //String.valueOf(Calendar.getInstance().getTimeInMillis())
            setThreadStartedCount(getThreadStartedCount()+1);

            //--upload picture to server
            //new HttpsRequestUploadPicture(this, newPic).execute();

        }else if(resultCode != RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE){
            File photoImage = getmPhotoFile();
            if(photoImage.exists()){
                photoImage.delete();
            }
            setmPhotoFile(null);
            setmCurrentPhotoUri(null);
            setmCurrentPhotoPath(null);
        }
        //-----add picture/add pictures
        if(resultCode == RESULT_OK && (requestCode == IMAGE_PICK_CODE || requestCode == PICK_IMAGE_MULTIPLE)){
            try {
                progressBar.setVisibility(View.VISIBLE);
                setThreadStartedCount(data.getClipData().getItemCount());
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri filePath = data.getClipData().getItemAt(i).getUri();
                    Cursor cursor = this.getContentResolver().query(filePath, null, null, null, null);
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                    cursor.moveToFirst();
                    String fileName = myUser.getId().toString() + "_" + myObject.getId().toString() + "_" + cursor.getString(nameIndex);

                    new Thread(new RunnableTask(this, myUser, myObject, filePath.toString(), "chat_" + fileName, hiddenPicture)).start();
                    //String.valueOf(Calendar.getInstance().getTimeInMillis())

                    /*FirebaseDatabase.getInstance().getReference("objects/" + myObject.getId().toString())
                            .push()
                            .setValue(new ObjectMessage(myUser.getFirst_name(),
                                                        myUser.getUname(),
                                                        myUser.getId().toString(),
                                                        editMessageInput.getText().toString(),
                                                        HelperDate.get_current_date_disply(),
                                                        Calendar.getInstance().getTime().toString(),
                                                        String.valueOf(Calendar.getInstance().getTimeInMillis()),
                                                        myUser.getUser_lv(),
                                                        "",
                                                        filePath.toString(),
                                                        "chat_" + fileName,
                                                        false
                            ));*/
                }
                //----send notification
                //sendMessageNotification();

            }catch (Exception e){
                Uri filePath = data.getData();
                Cursor cursor = this.getContentResolver().query(filePath, null,null,null,null);
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                cursor.moveToFirst();
                String fileName = myUser.getId().toString() + "_" + myObject.getId().toString() + "_" + cursor.getString(nameIndex);

                progressBar.setVisibility(View.VISIBLE);
                new Thread(new RunnableTask(this, myUser, myObject, filePath.toString(), "chat_" + fileName, hiddenPicture)).start();
                //String.valueOf(Calendar.getInstance().getTimeInMillis())
                setThreadStartedCount(getThreadStartedCount()+1);

                /*FirebaseDatabase.getInstance().getReference("objects/" + myObject.getId().toString())
                        .push()
                        .setValue(new ObjectMessage(myUser.getFirst_name(),
                                myUser.getUname(),
                                myUser.getId().toString(),
                                editMessageInput.getText().toString(),
                                HelperDate.get_current_date_disply(),
                                Calendar.getInstance().getTime().toString(),
                                String.valueOf(Calendar.getInstance().getTimeInMillis()),
                                myUser.getUser_lv(),
                                "",
                                filePath.toString(),
                                "chat_" + fileName,
                                false
                        ));

                //----send notification
                sendMessageNotification();
                */
            }
        }
    }
    class RunnableTask implements Runnable {
        Context context;
        ObjectUser user;
        ObjectObject object;
        String uri, fileName;
        ImageView hiddenImage;
        boolean go;

        public RunnableTask(Context context, ObjectUser user, ObjectObject object, String uri, String fileName, ImageView imageView) {
            this.context = context;
            this.user = user;
            this.object = object;
            this.uri = uri;
            this.fileName = fileName;
            this.hiddenImage = imageView;
            this.go = false;
        }
        public boolean isGo() {   return go;  }
        public void setGo(boolean go) {   this.go = go;   }

        @Override
        public void run() {
            setGo(true);
            RunnableTask runnableTask = this;
            while (isGo()) {
                if (backgroundJobs > 0) {
                    backgroundJobs -= 1;
                    Glide.with(context)
                            .asBitmap()
                            .load(Uri.parse(uri))
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .apply(new RequestOptions().override(500,500).centerInside())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    hiddenImage.setImageBitmap(resource);
                                    new HttpsRequestUploadPicture(context, object, fileName, resource, runnableTask).execute();
                                    //setGo(false);
                                }
                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = getmPhotoFile();
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Context context = this;
        MediaScannerConnection.scanFile(
                context,
                new String[]{getmCurrentPhotoPath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("foto", "file" + path + "was scanned: " + uri);
                    }
                }
        );
    }
    public void pickImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }catch(Exception e){
                Intent photoPickerIntent = new Intent();
                this.startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
            }
        } else {
            Intent photoPickerIntent = new Intent();
            this.startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
        }
    }
    public void takeNewPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.resolveActivity(this.getPackageManager());
        //if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",photoFile);
                //Uri imageUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider",photoFile);
                setmCurrentPhotoUri(imageUri);
                setmPhotoFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider" , photoFile));
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        //}
    }
    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = myUser.getId().toString() + "_" + getMyObject().getId().toString() + "_" + timeStamp+"_";
        File storageDir = //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                this.getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/Jerry");//Environment.getExternalStorageDirectory();
        File image = File.createTempFile(imageName, ".jpg", storageDir );

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "JerrySprendimai_"+timeStamp);
        values.put(MediaStore.Images.Media.DESCRIPTION, "JerrySprendimai_"+timeStamp);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, image.toString().toLowerCase(Locale.ROOT).hashCode());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, image.getName().toLowerCase(Locale.ROOT));
        values.put("_data", image.getAbsolutePath());
        ContentResolver cr = this.getContentResolver();
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        setmCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }
    public boolean isDateToBeDisplayed(String newDate){
        boolean value = false;

        if(currentDate.equals("")){
            setCurrentDate(newDate);
            setDateToDisplay(newDate);
            return value;
        }

        if(!currentDate.equals(newDate)){
            currentDate = newDate;
            value = true;
        }

        return value;
    }

    public ArrayList<ObjectMessage> getMessagesUnseenToSeen() {        return messagesUnseenToSeen;    }
    public void setMessagesUnseenToSeen(ArrayList<ObjectMessage> messagesUnseenToSeen) {        this.messagesUnseenToSeen = messagesUnseenToSeen;    }
    public String getCurrentDate() {        return currentDate;    }
    public void setCurrentDate(String currentDate) {        this.currentDate = currentDate;    }
    public String getDateToDisplay() {        return dateToDisplay;    }
    public void setDateToDisplay(String dateToDisplay) {        this.dateToDisplay = dateToDisplay;    }
    public ArrayList<ObjectObjUser> getObjectUserArrayList() {        return objectUserArrayList;    }
    public void setObjectUserArrayList(ArrayList<ObjectObjUser> objectUserArrayList) {        this.objectUserArrayList = objectUserArrayList;    }
    public ObjectObject getMyObject() {        return myObject;    }
    public void setMyObject(ObjectObject myObject) {        this.myObject = myObject;    }
    public ArrayList<ObjectUser> getOwnerList() {        return ownerList;    }
    public void setOwnerList(ArrayList<ObjectUser> ownerList) {        this.ownerList = ownerList;    }
    public boolean isNavigationToActivityObjectEdit() {  return doNavigationToActivityObjectEdit;    }
    public void setDoNavigationToActivityObjectEdit(boolean doNavigationToActivityObjectEdit) {        this.doNavigationToActivityObjectEdit = doNavigationToActivityObjectEdit;    }
    public boolean isDeletionMode() {        return deletionMode;    }
    public void setDeletionMode(boolean deletionMode) {        this.deletionMode = deletionMode;    }
    public ArrayList<MyAdapterMessage.MessageHolder> getToBeDeleted() {     return toBeDeleted;    }
    public void setToBeDeleted(ArrayList<MyAdapterMessage.MessageHolder> toBeDeleted) {    this.toBeDeleted = toBeDeleted;    }
    public String getmCurrentPhotoPath() { return mCurrentPhotoPath;    }
    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {        this.mCurrentPhotoPath = mCurrentPhotoPath;    }
    public void setmCurrentPhotoUri(Uri mCurrentPhotoUri){       this.mCurrentPhotoUri = mCurrentPhotoUri;    }
    public Uri getmCurrentPhotoUri() {        return mCurrentPhotoUri;    }
    public File getmPhotoFile() {        return mPhotoFile;    }
    public void setmPhotoFile(File mPhotoFile) {        this.mPhotoFile = mPhotoFile;    }
    public int getUploadRunning() {        return uploadRunning;    }
    public void setUploadRunning(int uploadRunning) {        this.uploadRunning = uploadRunning;    }
    public ArrayList<ObjectMessage> getBeingUpdated() {        return beingUpdated;    }
    public void setBeingUpdated(ArrayList<ObjectMessage> beingUpdated) {        this.beingUpdated = beingUpdated;    }
    public int getThreadStartedCount() {        return threadStartedCount;    }
    public void setThreadStartedCount(int threadStartedCount) {        this.threadStartedCount = threadStartedCount;    }
    public ArrayList<ObjectObjPic> getMyPictureList() {        return myPictureList;    }
    public void setMyPictureList(ArrayList<ObjectObjPic> myPictureList) {        this.myPictureList = myPictureList;    }

    public void setDeletionModeButtons(boolean set){
        if (set){
           participantsButton.setVisibility(View.GONE);
           toolsLayout.setVisibility(View.VISIBLE);
        }else{
            participantsButton.setVisibility(View.VISIBLE);
            toolsLayout.setVisibility(View.GONE);
        }
    }

    public void clearToBeDeleted(){
        for(int i=0; i<getToBeDeleted().size(); i++){
            getToBeDeleted().get(i).constraintLayout.setBackground(null);
        }
        //setToBeDeleted(new ArrayList<>());
    }

    @Override
    public void onBackPressed() {
        if(isDeletionMode()){
          setDeletionModeButtons(false);
          setDeletionMode(false);
          clearToBeDeleted();
          setToBeDeleted(new ArrayList<>());
        }else{
            FirebaseDatabase.getInstance().getReference("objects/" + chatRoomId).removeEventListener(valueEventListener);
          super.onBackPressed();
        }
    }

    private void setUpChatRoom(){
        attachMessageListener(chatRoomId);
    }

    private void attachMessageListener(String myChatRoomId){
        this.valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messages.clear();
                messagesUnseenToSeen.clear();
                //HashMap fldObj;

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Map<String, String> map = (Map)dataSnapshot.getValue();
                    Object fieldsObj = new Object();
                    //ObjectMessage objectMessage = new ObjectMessage(map);
                    try{
                        fieldsObj = (HashMap)dataSnapshot.getValue(fieldsObj.getClass());
                        ObjectMessage message = new ObjectMessage(fieldsObj);
                        message.setKey(dataSnapshot.getKey());

                        if(messages.size() == 0 ){
                            message.setDateSeparator(true);
                        }else{
                            if(!message.getDate().equals(messages.get(messages.size()-1).getDate())){
                                message.setDateSeparator(true);
                            }else{
                                message.setDateSeparator(false);
                            }
                        }

                        messages.add(message);
                        if(message.getUsers().containsKey(myUser.getId().toString())){
                            if(message.getUsers().get(myUser.getId().toString()).equals("false")){
                                if(!messagesUnseenToSeen.contains(message)){
                                    messagesUnseenToSeen.add(message);
                                }
                            }
                        }
                        if((!message.getPicUri().isEmpty())&&(!message.isDeleted())){
                            message.setPicturePosition(getMyPictureList().size());
                            ObjectObjPic objectObjPic = new ObjectObjPic();
                            objectObjPic.setPicUri(message.getPicUri());
                            getMyPictureList().add(objectObjPic);
                        }else if((!message.getPicUrl().isEmpty())&&(!message.isDeleted())){
                            message.setPicturePosition(getMyPictureList().size());
                            ObjectObjPic objectObjPic = new ObjectObjPic();
                            objectObjPic.setPicUrl(message.getPicUrl());
                            getMyPictureList().add(objectObjPic);
                        }


                    }catch (Exception e){
                        continue;
                    }

                }
                setCurrentDate("");
                setDateToDisplay("");


                myAdapterMessage.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                recyclerView.setVisibility(View.VISIBLE);
                if(getThreadStartedCount() == 0){
                    progressBar.setVisibility(View.GONE);
                }

                if(messagesUnseenToSeen.size() > 0){
                    updateSeenMessages(myChatRoomId);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        FirebaseDatabase.getInstance().getReference("objects/" + myChatRoomId).addValueEventListener(valueEventListener);
        /*FirebaseDatabase.getInstance().getReference("objects/" + myChatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messages.clear();
                messagesUnseenToSeen.clear();
                //HashMap fldObj;

                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Map<String, String> map = (Map)dataSnapshot.getValue();
                    Object fieldsObj = new Object();
                    //ObjectMessage objectMessage = new ObjectMessage(map);
                    try{
                        fieldsObj = (HashMap)dataSnapshot.getValue(fieldsObj.getClass());
                        ObjectMessage message = new ObjectMessage(fieldsObj);
                        message.setKey(dataSnapshot.getKey());
                        messages.add(message);

                        if(message.getUsers().get(myUser.getId().toString()).equals("false")){
                            if(!messagesUnseenToSeen.contains(message)){
                                messagesUnseenToSeen.add(message);
                            }
                        }

                    }catch (Exception e){
                        continue;
                    }

                }
                setCurrentDate("");
                setDateToDisplay("");


                    myAdapterMessage.notifyDataSetChanged();
                    recyclerView.scrollToPosition(messages.size() - 1);
                    recyclerView.setVisibility(View.VISIBLE);
                    if(getThreadStartedCount() == 0){
                      progressBar.setVisibility(View.GONE);
                    }

                    if(messagesUnseenToSeen.size() > 0){
                        updateSeenMessages(myChatRoomId);
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
    }

    public void updateSeenMessages(String chatRoom) {
        for(int i = 0; i < getMessagesUnseenToSeen().size(); i++){
            ObjectMessage message = getMessagesUnseenToSeen().get(i);
            message.getUsers().remove(myUser.getId().toString());
            message.getUsers().put(myUser.getId().toString(), "true");

            FirebaseDatabase.getInstance()
                    .getReference("objects/" + chatRoom)
                    .child(message.getKey())
                    .setValue(new ObjectMessage( message.getFirstName(),
                                                 message.getUname(),
                                                 message.getUserId(),
                                                 message.getContent(),
                                                 message.getDate(),
                                                 message.getTime(),
                                                 message.getMills(),
                                                 message.getUserLv(),
                                                 message.getPicUrl(),
                                                 message.getPicUri(),
                                                 message.getPicName(),
                                                 message.isDeleted(),
                                                 message.getUsers()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    pickImageFromGallery();
                }else{
                    //permission denied
                    Toast.makeText(this, "Atšaukta", Toast.LENGTH_SHORT).show();
                }
            case MY_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    takeNewPhoto();
                }else{
                    //permission denied
                    Toast.makeText(this, "Atšaukta", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void refresh(){
        //--to do header labels darbas + address
       txtChatParticipantCount.setText("+" + String.valueOf(objectUserArrayList.size()));
       txtChattingAbout.setText(myObject.getObjectName());
       txtChattingAbout2.setText(myObject.getObjectAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpsRequestCheckSessionAlive(this).execute();
        //refresh();
    }

    public void getObjectDetailsAndDisplay(ObjectObject objectToDisplay) {

        new HttpsRequestGetObjectDetails(this, myObject).execute();
    }

    class HttpsRequestViewObject extends AsyncTask<String, Void, InputStream> {
        private static final String view_object_url = "view_object.php";

        private Context context;
        private String objectId, notViewedValue;
        ObjectObject objectToDisplay;
        Connector connector;

        public HttpsRequestViewObject(Context ctx, ObjectObject obj, String objId, String value){
            context  = ctx;
            objectId = objId;
            notViewedValue = value;
            objectToDisplay = obj;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, view_object_url);
            connector.addPostParameter("user_id",    Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("object_id",  Base64.encodeToString(MCrypt.encrypt(objectId.getBytes()), Base64.DEFAULT));
            connector.addPostParameter("not_viewed", Base64.encodeToString(MCrypt.encrypt(notViewedValue.getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            JSONObject responseObject;
            try {
                responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String lockStatus = MCrypt.decryptSingle(responseObject.getString("status"));
                String lockMsg    = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (lockStatus.equals("1")) {
                    //getObjectDetailsAndDisplay(this.objectToDisplay);
                }else{

                }
                //((ActivityObjectShow) context).onRefresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getObjectDetailsAndDisplay(this.objectToDisplay);
        }
    }

    class HttpsRequestLockObject extends AsyncTask<String, Void, InputStream> {
        private static final String lock_object_url = "lock_object.php";

        private Context context;
        private String request;
        private String objectId;
        private ObjectObject objectObject;
        Connector connector;

        public HttpsRequestLockObject(Context ctx, ObjectObject objectObject, String objId, String req){
            this.context  = ctx;
            this.objectId = objId;
            this.request  = req;
            this.objectObject = objectObject;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, lock_object_url);
            connector.addPostParameter("user_id",   Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(objectId.getBytes()), Base64.DEFAULT));
            connector.addPostParameter("request",   Base64.encodeToString(MCrypt.encrypt(request.getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            JSONObject responseObject;
            try {
                responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String lockStatus = MCrypt.decryptSingle(responseObject.getString("status"));
                String lockMsg    = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (lockStatus.equals("1")) {
                    getObjectDetailsAndDisplay(objectObject);
                }else{
                    Toast.makeText(context, "Užrakinta", Toast.LENGTH_SHORT).show();
                }
                //((ActivityObjectShow) context).onRefresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class HttpsRequestSetObjectUser extends AsyncTask<String, Void, InputStream> {
        private static final String set_object_user_url = "set_object_user.php";

        private Context context;
        private ObjectObject clickObject;
        private ArrayList assignedUserList;
        Connector connector;

        public HttpsRequestSetObjectUser(Context ctx, ObjectObject obj, ArrayList userIdList){
            context     = ctx;
            clickObject = obj;
            assignedUserList = userIdList;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, set_object_user_url);
            connector.addPostParameter("user_id",   Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(clickObject.getId().toString().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("date",      Base64.encodeToString(MCrypt.encrypt(HelperDate.get_current_date_mysql().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user",      Base64.encodeToString(MCrypt.encrypt(assignedUserList.toString().getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            try {
                connector.decodeResponse();
                JSONObject responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String saveStatus = MCrypt.decryptSingle(responseObject.getString("status"));
                String msg        = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (saveStatus.equals("1")) {

                }
                if(msg.equals("")){

                }
            }catch (Exception e){

            }
            new HttpsRequestGetObjectDetails(context, clickObject).execute();
        }
    }

    class HttpsRequestCheckSessionAlive extends AsyncTask<String, Void, InputStream> {
        private static final String check_session_alive_url = "check_session_alive.php";

        private Context context;
        Connector connector;

        public HttpsRequestCheckSessionAlive(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, check_session_alive_url);
            connector.addPostParameter("user_id", MCrypt2.encodeToString(myUser.getId().toString()));
            connector.addPostParameter("session", MCrypt2.encodeToString(myUser.getSessionId()));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            connector.decodeResponse();

            JSONObject object = null;
            try {
                object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));
                String status  = object.getString("status");
                String msg     = object.getString("msg");
                //String control = object.getString("control");
                if (status.equals("1")) {
                    getObjectDetailsAndDisplay(myObject);
                    //----get object list for calender events sync.
                    //new HttpsRequestGetObjectDetails(context).execute();
                    //findViewById(R.id.progressBar).setVisibility(View.GONE);
                    //enableWholeView(gridLayout);
                }else{
                    //session and last activity deleted in DB, app will log-out
                    Toast.makeText(context, context.getResources().getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            unlockView();
            super.onPostExecute(inputStream);
        }
    }

    class HttpsRequestGetObjectDetails extends AsyncTask<String, Void, InputStream> {
        private static final String get_object_details_url = "get_object_details.php";

        private Context context;
        private ObjectObject clickObject;
        private ArrayList<ObjectObjUser> objectUserArrayList;
        private ArrayList<ObjectObjDetails> objectDetailsArrayList;
        private ArrayList<ObjectObjPic> objectPicturesArrayList;
        Connector connector;

        public HttpsRequestGetObjectDetails(Context ctx, ObjectObject obj){
            context     = ctx;
            clickObject = obj;
        }

        public ArrayList<ObjectObjUser> getObjectUserArrayList() {
            return objectUserArrayList;
        }
        public void setObjectUserArrayList(ArrayList<ObjectObjUser> objectUserArrayList) {
            this.objectUserArrayList = objectUserArrayList;
        }
        public ArrayList<ObjectObjDetails> getObjectDetailsArrayList() {
            return objectDetailsArrayList;
        }
        public void setObjectDetailsArrayList(ArrayList<ObjectObjDetails> objectDetailsArrayList) {
            this.objectDetailsArrayList = objectDetailsArrayList;
        }
        public ArrayList<ObjectObjPic> getObjectPicturesArrayList() {
            return objectPicturesArrayList;
        }
        public void setObjectPicturesArrayList(ArrayList<ObjectObjPic> objectPicturesArrayList) {
            this.objectPicturesArrayList = objectPicturesArrayList;
        }
        public void setClickObject(ObjectObject clickObject) {
            this.clickObject = clickObject;
        }
        public ObjectObject getClickObject() {
            return clickObject;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_object_details_url);
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(String.valueOf(clickObject.getId()).getBytes()), Base64.DEFAULT));
            //connector.addPostParameter("user_id",   Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            ArrayList<ObjectObjUser> objUserArrayList = new ArrayList<>();
            ArrayList<ObjectObjDetails> objDetailsArrayList = new ArrayList<>();
            ArrayList<ObjectObjPic> objPicsArrayList = new ArrayList<>();
            ArrayList<ObjectUser> employeeArrayList = new ArrayList<>();
            ArrayList<ObjectUser> ownerArrayList = new ArrayList<>();
            ArrayList<ObjectUser> headerArrayList = new ArrayList<>();
            ArrayList<ObjectObject> objectArrayList = new ArrayList<>();

            JSONArray responseObjDetails = new JSONArray();
            JSONArray responseObjUser    = new JSONArray();
            JSONArray responseObjPic     = new JSONArray();
            JSONArray responseEmployee   = new JSONArray();
            JSONArray responseObject     = new JSONArray();
            JSONArray responseOwners     = new JSONArray();
            JSONArray responseHeader     = new JSONArray();
            Integer completeCount = 0;
            try {
                responseObjDetails = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(0));
                responseObjUser    = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(1));
                responseObjPic     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(2));
                responseEmployee   = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(3));
                responseObject     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(4));
                responseOwners     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(5));
                responseHeader     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(6));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try{
                for(int i = 0; i < responseObjDetails.length(); i++){
                    ObjectObjDetails objectObjDetails = new ObjectObjDetails((JSONObject) responseObjDetails.get(i));
                    objDetailsArrayList.add(objectObjDetails);
                    if(objectObjDetails.getCompleted().equals("X")){
                        completeCount += 1;
                    }
                }
                setObjectDetailsArrayList(objDetailsArrayList);

                for(int i = 0; i < responseObjUser.length(); i++){
                    ObjectObjUser objectObjUser = new ObjectObjUser((JSONObject) responseObjUser.get(i));
                    objUserArrayList.add(objectObjUser);
                }
                setObjectUserArrayList(objUserArrayList);
                ((ActivityChat)context).setObjectUserArrayList(objUserArrayList);

                for(int i = 0; i < responseObjPic.length(); i++){
                    ObjectObjPic objectObjPic = new ObjectObjPic((JSONObject) responseObjPic.get(i));
                    objPicsArrayList.add(objectObjPic);
                }
                setObjectPicturesArrayList(objPicsArrayList);

                for(int i =0; i < responseEmployee.length(); i++){
                    ObjectUser objectUser = new ObjectUser((JSONObject) responseEmployee.get(i));
                    employeeArrayList.add(objectUser);
                }

                for(int i = 0; i < responseObject.length(); i++ ){
                    ObjectObject updatedObject = new ObjectObject((JSONObject) responseObject.get(i), "wa");
                    if(updatedObject != null){
                        setClickObject(updatedObject);
                        break;
                    }
                }

                for(int i = 0; i<responseOwners.length(); i++){
                    ObjectUser objectUser = new ObjectUser((JSONObject) responseOwners.get(i));
                    ownerArrayList.add(objectUser);
                }
                ObjectObject objectObject = null;
                for (int i=0; i<responseHeader.length(); i++){
                    objectObject = new ObjectObject((JSONObject) responseHeader.get(i), "wa");
                    break;
                }
                if (objectObject !=null){
                    ((ActivityChat)context).setMyObject(objectObject);
                }

                if (isNavigationToActivityObjectEdit()) {
                    setDoNavigationToActivityObjectEdit(false);
                    //((ActivityChat)context).setObjectToDisplay(getClickObject());
                    Intent intent = new Intent(context, ActivityObjectEdit.class);
                    intent.putExtra("myUser", myUser);
                    intent.putExtra("objectObject", getClickObject());
                    intent.putParcelableArrayListExtra("listDetails", getObjectDetailsArrayList());
                    intent.putParcelableArrayListExtra("listUser", getObjectUserArrayList());
                    intent.putParcelableArrayListExtra("listPictures", getObjectPicturesArrayList());
                    context.startActivity(intent);
                }else{
                    refresh();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    class HttpsRequestUploadPicture extends AsyncTask<String, String, InputStream> {
        private static final String upload_picture_url = "upload_picture.php";
        private Context context;
        Connector connector;
        ObjectObjPic objectObjPic;
        ObjectMessage message;
        String fileName;
        ObjectObject object;
        Bitmap resource;
        RunnableTask runnableTask;

        public HttpsRequestUploadPicture(Context context, ObjectObject object, String fileName, Bitmap resource, RunnableTask runnableTask) {
            this.context = context;
            this.object = object;
            this.fileName = fileName;
            this.resource = resource;
            this.runnableTask = runnableTask;
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

            //publishProgress("start");
            connector = new Connector(context, upload_picture_url);
            connector.addPostParameter("objectId",      MCrypt2.encodeToString(String.valueOf(object.getId())));
            connector.addPostParameter("pictureName",   MCrypt2.encodeToString(fileName));//objectObjPic.getPicName()
            //publishProgress("one");
            connector.addPostParameter("pictureSource", str_img);
            //publishProgress("two");
            connector.send();
            //publishProgress("three");
            connector.receive();
            //publishProgress("four");
            connector.disconnect();
            String result = connector.getResult();
            //publishProgress("done");
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
                    //objectObjPic.setPicUrl(msg);
                    //objectObjPic.setPicUri("");

                    //message.setPicUrl(msg);
                    //message.setPicUri("");

                    HashMap<String, String> usersHashMap = new HashMap<String, String>();
                    boolean contains = false;
                    for(int i=0; i<getObjectUserArrayList().size(); i++){
                        if(myUser.getId().equals(getObjectUserArrayList().get(i).getUserId())){
                            contains = true;
                            usersHashMap.put(getObjectUserArrayList().get(i).getUserId().toString(), "true");
                        }else{
                            usersHashMap.put(getObjectUserArrayList().get(i).getUserId().toString(), "false");
                        }
                    }
                    for(int i=0;i<getOwnerList().size(); i++){
                        if(myUser.getId().equals(getOwnerList().get(i).getId())){
                            usersHashMap.put(getOwnerList().get(i).getId().toString(), "true");
                        }else{
                            usersHashMap.put(getOwnerList().get(i).getId().toString(), "false");
                        }
                    }
                    //HashMap<String, HashMap> users = new HashMap<String, HashMap>();
                    //users.put("users", usersHashMap);
                    //---Save message to Firebase
                    FirebaseDatabase.getInstance()
                            .getReference("objects/" + object.getId().toString())
                            //.child(message.getKey())
                            .push()
                            .setValue(new ObjectMessage(myUser.getFirst_name(),
                                                        myUser.getUname(),
                                                        myUser.getId().toString(),
                                                        "",
                                                        HelperDate.get_current_date_disply(),
                                                        Calendar.getInstance().getTime().toString(),
                                                        String.valueOf(Calendar.getInstance().getTimeInMillis()),
                                                        myUser.getUser_lv(),
                                                        msg,
                                                        "",
                                                        fileName,
                                                        false,
                                                        usersHashMap));
                    /*
                    FirebaseDatabase.getInstance().getReference("objects/" + myObject.getId().toString())
                        .push()
                        .setValue(new ObjectMessage(myUser.getFirst_name(),
                                myUser.getUname(),
                                myUser.getId().toString(),
                                editMessageInput.getText().toString(),
                                HelperDate.get_current_date_disply(),
                                Calendar.getInstance().getTime().toString(),
                                String.valueOf(Calendar.getInstance().getTimeInMillis()),
                                myUser.getUser_lv(),
                                "",
                                filePath.toString(),
                                "chat_" + fileName,
                                false
                        ));
                    */
                    //((ActivityChat)context).getBeingUpdated().remove(message.getKey());

                }else{
                    //error handling?
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            backgroundJobs += 1;
            runnableTask.setGo(false);
            setThreadStartedCount(getThreadStartedCount()-1);
            /*if (getThreadStartedCount() == 0){
                progressBar.setVisibility(View.GONE);
            }*/
            //----send notification
            sendMessageNotification();

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