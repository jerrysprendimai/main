package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.kofigyan.stateprogressbar.StateProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ActivityOrder1 extends AppCompatActivity {
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 101;
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int REQUEST_IMAGE_CAPTURE = 10;
    private static final int PERMISSION_CODE = 1001;

    private static ObjectOrder staticMyorder;
    Button proceedButton;
    LinearLayout addDealerButton, addObjectButton;

    ArrayList<ObjectDealer> myDealerList;
    ArrayList<ObjectDealer> myDealerListOriginal;
    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;

    View dialogView;
    AlertDialog.Builder builder;
    AlertDialog dialog ;
    ObjectUser myUser;
    //String myHtml;
    CardView cardViewDealer, cardViewObject;
    RecyclerView recyclerViewDealer, recyclerViewObject;
    View.OnClickListener dealerOnClickListener, objectOnClickListener;

    MyViewPagerAdapterOrder myViewPagerAdapterOrder;
    MyAdapterDealerShow myAdapterDealer;
    MyAdapterObjectShowP1 myAdapterObject;

    ArrayList<ObjectObject> myObject;
    ArrayList<ObjectDealer> myDealer;

    FragmentOrderPart1 fragmentOrderPart1;
    FragmentOrderPart2 fragmentOrderPart2;
    FragmentOrderPart3 fragmentOrderPart3;
    ObjectOrder myOrder, myPreviousEmail;
    ProgressBar progressBar;
    ViewPager viewPager;
    StateProgressBar stateProgressBar;
    ArrayList<ObjectObjPic> toBeDeletedList;
    boolean deletionMode;
    private int backgroundJobs = 10, retutnThreadCount = 0, newPicCount = 0;
    String mCurrentPhotoPath, myHtml, emailLabel;
    Uri mCurrentPhotoUri;
    File mPhotoFile;
    MyAdapterOrderPicture myAdapterOrderPicture;
    Spannable spanable, spannableOriginalEmail;
    Spanned spaned, spannedOriginalEmail;
    String actionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order1);

        //----binding
        //proceedButton      = findViewById(R.id.button_order_p1_continue);
        //addDealerButton    = findViewById(R.id.linearLayout_oder_p1_dealer_button);
        //addObjectButton    = findViewById(R.id.linearLayout_oder_p1_object_button);
        //recyclerViewDealer = findViewById(R.id.my_recycle_view_dealer);
        //recyclerViewObject = findViewById(R.id.my_recycle_view_object);
        //cardViewDealer     = findViewById(R.id.cardView_oder_p1_dealer);
        //cardViewObject     = findViewById(R.id.cardView_oder_p1_object);
        viewPager          = findViewById(R.id.order_p1_viewPager);
        stateProgressBar   = findViewById(R.id.order_state_progress_bar);
        progressBar        = findViewById(R.id.order_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        viewPager.setOnTouchListener((v, event) -> true);
        emailLabel = "";
        //---------------Read references from Intent----------------------
        this.myPreviousEmail = getIntent().getParcelableExtra("myOrder");
        this.myUser = getIntent().getParcelableExtra("myUser");
        this.actionType = getIntent().getExtras().getString("actionType");
        //this.myOrder = getIntent().getParcelableExtra("myOrder");
        if(myOrder == null){
          this.myOrder = new ObjectOrder();
          this.myOrder.setUserId(myUser.getId());
          this.myOrder.setFirstName(myUser.getFirst_name());
          //ActivityOrder1.setStaticOrder(myOrder);
        }

        //proceedButton.setText(proceedButton.getText() + "   1 / 3");
        //proceedButton.setEnabled(false);
        //proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button_grey));

        dealerOnClickListener = v-> {
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_with_recycler_view, findViewById(R.id.order_pt1_main_containerView), false);
            MyAdapterDealerShow myAdapterDealerShow = new MyAdapterDealerShow(this, myDealerList, myDealerListOriginal, "dealerShowDialogP1", null, false, myUser);
            RecyclerView dialogRecyclerViewDealer = dialogView.findViewById(R.id.my_recycle_view_dialog);
            dialogRecyclerViewDealer.setAdapter(myAdapterDealerShow);
            dialogRecyclerViewDealer.setLayoutManager(new LinearLayoutManager(this));
            builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setView(dialogView);

            builder.setOnDismissListener(dialog -> { });
            dialog = builder.create();
            dialog.show();
        };
        objectOnClickListener = v->{
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_with_recycler_view, findViewById(R.id.order_pt1_main_containerView), false);
            MyAdapterObjectShowP1 myAdapterObjectShowP1 = new MyAdapterObjectShowP1(this, null, myObjectList, myUser, null, "objectShowP1", false);
            RecyclerView dialogRecyclerViewObject = dialogView.findViewById(R.id.my_recycle_view_dialog);
            dialogRecyclerViewObject.setAdapter(myAdapterObjectShowP1);
            dialogRecyclerViewObject.setLayoutManager(new LinearLayoutManager(this));
            builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setView(dialogView);

            builder.setOnDismissListener(dialog -> { });
            dialog = builder.create();
            dialog.show();
        };

        myAdapterOrderPicture = new MyAdapterOrderPicture(this, myOrder.getMyPictureList(), myUser, "pictureShowP1");

        String[] stateLabels = {getResources().getString(R.string.to), getResources().getString(R.string.message), getResources().getString(R.string.place_order)};
        stateProgressBar.setStateDescriptionData(stateLabels);

        /*myViewPagerAdapterOrder = new MyViewPagerAdapterOrder(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentOrderPart1 = FragmentOrderPart1.newInstance(this);
        fragmentOrderPart2 = FragmentOrderPart2.newInstance(this, myAdapterOrderPicture);
        fragmentOrderPart3 = FragmentOrderPart3.newInstance(this);
        myViewPagerAdapterOrder.addFragment(fragmentOrderPart1, "");
        myViewPagerAdapterOrder.addFragment(fragmentOrderPart2, "");
        myViewPagerAdapterOrder.addFragment(fragmentOrderPart3, "");
        viewPager.setAdapter(myViewPagerAdapterOrder);
        viewPager.setCurrentItem(0);

        //-----Handling email Forward request
        if( getIntent().getParcelableExtra("myObject") != null ){
            myObject = new ArrayList<>();
            myObject.add(getIntent().getParcelableExtra("myObject"));
            myPreviousEmail = getIntent().getParcelableExtra("myOrder");
            //myAdapterObject = new MyAdapterObjectShowP1(this, null, myObject, myUser, objectOnClickListener, "objectShowP1", false);
            this.myOrder.setMyObject(myObject.get(0));
            //new HttpsEmailDetails(this).execute();

            //ObjectOrder ord = getIntent().getParcelableExtra("myOrder");
            //this.myOrder.setMyHtml(ord.getMyHtml());
            //myHtml = getIntent().getParcelableExtra("myHtml");
        }*/

        new HttpsRequestCheckSessionAlive(this).execute();

    }
    public void callback(){
        progressBar.setVisibility(View.GONE);
        myViewPagerAdapterOrder = new MyViewPagerAdapterOrder(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        fragmentOrderPart1 = FragmentOrderPart1.newInstance(this);
        fragmentOrderPart2 = FragmentOrderPart2.newInstance(this, myAdapterOrderPicture);
        fragmentOrderPart3 = FragmentOrderPart3.newInstance(this);
        myViewPagerAdapterOrder.addFragment(fragmentOrderPart1, "");
        myViewPagerAdapterOrder.addFragment(fragmentOrderPart2, "");
        myViewPagerAdapterOrder.addFragment(fragmentOrderPart3, "");
        viewPager.setAdapter(myViewPagerAdapterOrder);
        viewPager.setCurrentItem(0);

        //-----Handling email Forward request
        if( getActionType() != null ){
            if(getActionType().equals("forward")) {
                myObject = new ArrayList<>();
                myObject.add(getIntent().getParcelableExtra("myObject"));
                myPreviousEmail = getIntent().getParcelableExtra("myOrder");
                //myAdapterObject = new MyAdapterObjectShowP1(this, null, myObject, myUser, objectOnClickListener, "objectShowP1", false);
                this.myOrder.setMyObject(myObject.get(0));
                setEmailLabel(getResources().getString(R.string.forwarding));
                fragmentOrderPart2.setWebView(true, getResources().getString(R.string.forwarding));
                //new HttpsEmailDetails(this).execute();
                //ObjectOrder ord = getIntent().getParcelableExtra("myOrder");
                //this.myOrder.setMyHtml(ord.getMyHtml());
                //myHtml = getIntent().getParcelableExtra("myHtml");
            }else if(getActionType().equals("reply")){
                myObject = new ArrayList<>();
                myObject.add(getIntent().getParcelableExtra("myObject"));
                this.myOrder.setMyObject(myObject.get(0));

                myDealer = new ArrayList<>();
                myDealer.add(getIntent().getParcelableExtra("myDealer"));
                myPreviousEmail = getIntent().getParcelableExtra("myOrder");
                this.myOrder.setMyDealer(myDealer.get(0));
                setEmailLabel(getResources().getString(R.string.replying));
                fragmentOrderPart2.setWebView(true, getResources().getString(R.string.replying));
            }
        }
    }
    public void buttonClickCallback(StateProgressBar.StateNumber state, int sateNr){
        stateProgressBar.setCurrentStateNumber(state);
        viewPager.setCurrentItem(sateNr);
    }
    public void sendButtonCallabck(){

        myOrder.setType("out");
        retutnThreadCount = 0;
        if(myOrder.getMyPictureList().size() > 0){
            newPicCount = myOrder.getMyPictureList().size();
            for(ObjectObjPic objectObjPic: myOrder.getMyPictureList()){
                //newPicCount += 1;

                Thread thread = new Thread(new RunnableTask(this, objectObjPic));
                thread.start();
                //}
            }
        }else{
            new HttpsRequestSendOrderMessage(this).execute();
        }
    }

    public void dealerSelectedCallback(int adapterPosition){
        //cardViewDealer.setVisibility(View.GONE);
        //recyclerViewDealer.setVisibility(View.VISIBLE);
        myDealer = new ArrayList<>();
        myDealer.add(myDealerList.get(adapterPosition));
        myAdapterDealer = new MyAdapterDealerShow(this, myDealer, myDealerListOriginal, "dealerShowP1", dealerOnClickListener, false, myUser);
        //recyclerViewDealer.setAdapter(myAdapterDealer);
        //recyclerViewDealer.setLayoutManager(new LinearLayoutManager(this));
        dialog.dismiss();

        this.myOrder.setMyDealer(myDealerList.get(adapterPosition));
        //checkIsOkToProceed();

        fragmentOrderPart1.dealerSelectedCallback(adapterPosition, myAdapterDealer);
    }


    public void objectSelectedCallback(int adapterPosition) {
        //cardViewObject.setVisibility(View.GONE);
        //recyclerViewObject.setVisibility(View.VISIBLE);
        myObject = new ArrayList<>();
        myObject.add(myObjectList.get(adapterPosition));
        myAdapterObject = new MyAdapterObjectShowP1(this, null, myObject, myUser, objectOnClickListener, "objectShowP1", false);
        //recyclerViewObject.setAdapter(myAdapterObject);
        //recyclerViewObject.setLayoutManager(new LinearLayoutManager(this));
        dialog.dismiss();

        this.myOrder.setMyObject(myObjectList.get(adapterPosition));
        //checkIsOkToProceed();

        fragmentOrderPart1.objectSelectedCallback(adapterPosition, myAdapterObject);
    }

    public void takePhotoClickCallback(){
        //check permission camera
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                this.requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }else{
                //check permission external storage
                if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
                }else{
                    takeNewPhoto();
                }
            }
        }else{
            takeNewPhoto();
        }
    }

    public void addPhotoClickCallback(){
        //check permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show popup for runtime permission
                this.requestPermissions(permissions, PERMISSION_CODE);
            }else{
                //permission already granted
                pickImageFromGallery();
            }
        }else{
            //system os is less that marshmallow
            pickImageFromGallery();
        }
    }

    public void deletePhotoClickCallback(){
        for(ObjectObjPic pic: toBeDeletedList){
            myOrder.getMyPictureList().remove(pic);
        }
        setDeletionMode(false);
        fragmentOrderPart2.deletionModeButtons.setVisibility(View.GONE);
        fragmentOrderPart2.addModeButtons.setVisibility(View.VISIBLE);
        myAdapterOrderPicture.notifyDataSetChanged();
        fragmentOrderPart2.checkAbleToProceed();
    }

    public void cancelPhotoClickCallback(){
        toBeDeletedList.removeAll(toBeDeletedList);
        fragmentOrderPart2.deletionModeButtons.setVisibility(View.GONE);
        fragmentOrderPart2.addModeButtons.setVisibility(View.VISIBLE);
        myAdapterOrderPicture.notifyDataSetChanged();
        fragmentOrderPart2.checkAbleToProceed();
    }

    private void pickImageFromGallery(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Intent intent = new Intent();
                //intent.setType("image/*");
                intent.setType("*/*");
                String[] mimetypes = {"image/*",
                                      "application/pdf",
                                      "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                      "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                                      "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                      "text/plain"
                                     };
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }catch(Exception e){
                Intent photoPickerIntent = new Intent();
                this.startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
            }
        }else {
            Intent photoPickerIntent = new Intent();
            this.startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
        }
    }

    private void takeNewPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = myUser.getId().toString() + "_" + myOrder.getMyObject().getId().toString() + "_" + timeStamp+"_";
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = getmPhotoFile();//new File(getmCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        MediaScannerConnection.scanFile(
                this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String[] mimetypes = {"image/*",
                "application/pdf",
                "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                "text/plain"
        };

        //-----take picture
        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE ){

            String fileName = "order_" + getmPhotoFile().getName();

            galleryAddPic();
            ObjectObjPic newPic = new ObjectObjPic();
            newPic.setPicName(fileName);
            newPic.setPosNr(myOrder.getMyPictureList().size());
            newPic.setUserId(myUser.getId());
            newPic.setFirstName(myUser.getFirst_name());
            newPic.setPicUri(getmCurrentPhotoUri().toString());//filePath.toString());
            newPic.setObjectId(this.myOrder.getMyObject().getId());

            myOrder.getMyPictureList().add(newPic);
            myAdapterOrderPicture.notifyDataSetChanged();

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
                ContentResolver cr = this.getContentResolver();
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();

                    String mime = cr.getType(uri);
                    if(!Arrays.asList(mimetypes).contains(mime)){
                        if(!mime.contains("image/")) {
                            Toast.makeText(this, getResources().getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                    cursor.moveToFirst();
                    String fileName = "order_" + myUser.getId().toString() + "_" + myOrder.getMyObject().getId().toString() + "_" + cursor.getString(nameIndex);

                    String mimeType = getContentResolver().getType(uri);
                    String filePath = "";
                    if(!mimeType.contains("image/")){
                        String filename = "";
                        if (mimeType == null) {
                            String path = getPath(this, uri);
                            if (path == null) {
                                //filename = FilenameUtils.getName(uri.toString());
                            } else {
                                File file = new File(path);
                                filename = file.getName();
                            }
                        } else {
                            Cursor returnCursor = getContentResolver().query(uri, null, null, null, null);
                            int nameIndexx = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndexx = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();
                            filename = returnCursor.getString(nameIndexx);
                            String size = Long.toString(returnCursor.getLong(sizeIndexx));
                        }
                        File fileSave = getExternalFilesDir(null);
                        String sourcePath = getExternalFilesDir(null).toString();

                        //File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/download/"+ cursor.getString(nameIndex));
                        try {
                            File file = new File(sourcePath +"/"+ filename);
                            filePath = sourcePath +"/"+ filename;
                            copyFileStream(file, uri,this);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    ObjectObjPic newPic = new ObjectObjPic();
                    newPic.setMimeType(mimeType);
                    newPic.setPosNr(myOrder.getMyPictureList().size());
                    newPic.setUserId(myUser.getId());
                    newPic.setFirstName(myUser.getFirst_name());
                    newPic.setPicUri(uri.toString());
                    newPic.setObjectId(myOrder.getMyObject().getId());
                    newPic.setPicName(fileName);
                    newPic.setFilePath(filePath);

                    myOrder.getMyPictureList().add(newPic);

                }
            }catch (Exception e){
                //set image to image view
                Uri uri = data.getData();
                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);

                ContentResolver cr = this.getContentResolver();
                String mime = cr.getType(uri);

                if(!Arrays.asList(mimetypes).contains(mime)){
                    if(!mime.contains("image/")) {
                        Toast.makeText(this, getResources().getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                cursor.moveToFirst();
                String fileName = "order_" + myUser.getId().toString() + "_" + myOrder.getMyObject().getId().toString() + "_" + cursor.getString(nameIndex);

                String mimeType = getContentResolver().getType(uri);
                String filePath = "";
                if(!mimeType.contains("image/")){
                    String filename = "";
                    if (mimeType == null) {
                        String path = getPath(this, uri);
                        if (path == null) {
                            //filename = FilenameUtils.getName(uri.toString());
                        } else {
                            File file = new File(path);
                            filename = file.getName();
                        }
                    } else {
                        Uri returnUri = data.getData();
                        Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                        int nameIndexx = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndexx = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        filename = returnCursor.getString(nameIndexx);
                        String size = Long.toString(returnCursor.getLong(sizeIndexx));
                    }
                    File fileSave = getExternalFilesDir(null);
                    String sourcePath = getExternalFilesDir(null).toString();

                    //File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/download/"+ cursor.getString(nameIndex));
                    try {
                        File file = new File(sourcePath +"/"+ filename);
                        filePath = sourcePath +"/"+ filename;
                        copyFileStream(file, uri,this);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                ObjectObjPic newPic = new ObjectObjPic();
                newPic.setMimeType(mimeType);
                newPic.setPosNr(myOrder.getMyPictureList().size());
                newPic.setUserId(myUser.getId());
                newPic.setFirstName(myUser.getFirst_name());
                newPic.setPicUri(uri.toString());
                newPic.setObjectId(myOrder.getMyObject().getId());
                newPic.setPicName(fileName);
                newPic.setFilePath(filePath);

                myOrder.getMyPictureList().add(newPic);
            }
            myAdapterOrderPicture.notifyDataSetChanged();
        }

        fragmentOrderPart2.checkAbleToProceed();
    }

    private void copyFileStream(File dest, Uri uri, Context context)throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public static String getPath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    public void onBackPressed() {
        //ActivityOrder1.setStaticOrder(null);

        if(viewPager.getCurrentItem() == 0){
            super.onBackPressed();
        }else{
            int currentItem = viewPager.getCurrentItem();
            switch (currentItem){
                case 1:
                    buttonClickCallback(StateProgressBar.StateNumber.ONE, 0);
                    //stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
                    //myAdapterDealer.notifyDataSetChanged();
                    //myAdapterObject.notifyDataSetChanged();
                    break;
                case 2:
                    buttonClickCallback(StateProgressBar.StateNumber.TWO, 1);
                    //stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
                    break;
            }
            //viewPager.setCurrentItem(currentItem - 1);
        }

    }

    @Override
    protected void onResume() {
        //fragmentOrderPart1.setViewEnabled(false);
        //new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
    }
    public void checkProceedOrderSend(){
        if(retutnThreadCount == newPicCount){
            new HttpsRequestSendOrderMessage(this).execute();
        }
    }
    public String generateHtml(){
        String content = Html.toHtml(this.getSpanable());
        return content;
    }
    public String generateOriginalEmailHtml(){
        String adjusted = "";
        String content = Html.toHtml(this.getSpannableOriginalEmail());
        String[] tmp = content.split(",");
        int count = 0;
        for(String str: tmp){
            if(count == 0){
                count++;
                adjusted = adjusted + str;
                continue;
            }
            if(str.contains("\"></p>")){
                String[] tmp2 = str.split("\"></p>");
                String[] tmp3 = tmp2[0].split(" ");
                String noSpace = "";
                for(String strNoSpace: tmp3){
                    noSpace = noSpace + strNoSpace;
                }
                adjusted = adjusted + "," + noSpace + "\"></p>" + tmp2[1];
            }
            count++;
        }
        return adjusted;
    }

    public ObjectOrder getMyOrder() {        return myOrder;    }
    public void setMyOrder(ObjectOrder myOrder) {        this.myOrder = myOrder;    }
    public View.OnClickListener getDealerOnClickListener() {        return dealerOnClickListener;    }
    public void setDealerOnClickListener(View.OnClickListener dealerOnClickListener) {        this.dealerOnClickListener = dealerOnClickListener;    }
    public View.OnClickListener getObjectOnClickListener() {        return objectOnClickListener;    }
    public void setObjectOnClickListener(View.OnClickListener objectOnClickListener) {        this.objectOnClickListener = objectOnClickListener;    }
    public boolean isDeletionMode() {        return deletionMode;    }
    public void setDeletionMode(boolean deletionMode) {        this.deletionMode = deletionMode;    }
    public ArrayList<ObjectObjPic> getToBeDeletedList() {        return toBeDeletedList;    }
    public void setToBeDeletedList(ArrayList<ObjectObjPic> toBeDeletedList) {        this.toBeDeletedList = toBeDeletedList;    }
    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {    this.mCurrentPhotoPath = mCurrentPhotoPath;    }
    public String getmCurrentPhotoPath() {    return mCurrentPhotoPath;    }
    public void setmCurrentPhotoUri(Uri mCurrentPhotoUri) {        this.mCurrentPhotoUri = mCurrentPhotoUri;    }
    public Uri getmCurrentPhotoUri() {        return mCurrentPhotoUri;    }
    public void setmPhotoFile(File mPhotoFile) {        this.mPhotoFile = mPhotoFile;    }
    public File getmPhotoFile() {        return mPhotoFile;    }
    public MyAdapterDealerShow getMyAdapterDealer() {        return myAdapterDealer;    }
    public void setMyAdapterDealer(MyAdapterDealerShow myAdapterDealer) {        this.myAdapterDealer = myAdapterDealer;    }
    public MyAdapterObjectShowP1 getMyAdapterObject() {        return myAdapterObject;    }
    public void setMyAdapterObject(MyAdapterObjectShowP1 myAdapterObject) {        this.myAdapterObject = myAdapterObject;    }
    public ArrayList<ObjectObject> getMyObject() {        return myObject;    }
    public void setMyObject(ArrayList<ObjectObject> myObject) {        this.myObject = myObject;    }
    public ArrayList<ObjectDealer> getMyDealer() {        return myDealer;    }
    public void setMyDealer(ArrayList<ObjectDealer> myDealer) {        this.myDealer = myDealer;    }
    public ObjectUser getMyUser() {        return myUser;    }
    public void setMyUser(ObjectUser myUser) {        this.myUser = myUser;    }
    public String getMyHtml() {        return myHtml;    }
    public void setMyHtml(String myHtml) {        this.myHtml = myHtml;    }
    public String getEmailLabel() {        return emailLabel;    }
    public void setEmailLabel(String emailLabel) {        this.emailLabel = emailLabel;    }
    public Spannable getSpanable() {        return spanable;    }
    public void setSpanable(Spannable spanable) {        this.spanable = spanable;    }
    public ObjectOrder getMyPreviousEmail() {        return myPreviousEmail;    }
    public void setMyPreviousEmail(ObjectOrder myPreviousEmail) {        this.myPreviousEmail = myPreviousEmail;    }
    public String getActionType() {        return actionType;    }
    public void setActionType(String actionType) {        this.actionType = actionType;    }
    public Spannable getSpannableOriginalEmail() {        return spannableOriginalEmail;    }
    public void setSpannableOriginalEmail(Spannable spannableOriginalEmail) {        this.spannableOriginalEmail = spannableOriginalEmail;    }
    public Spanned getSpaned() {        return spaned;    }
    public void setSpaned(Spanned spaned) {        this.spaned = spaned;    }
    public Spanned getSpannedOriginalEmail() {        return spannedOriginalEmail;    }
    public void setSpannedOriginalEmail(Spanned spannedOriginalEmail) {        this.spannedOriginalEmail = spannedOriginalEmail;    }


    class RunnableTask implements Runnable{
        Context context;
        ObjectObjPic objectObjPic;
        public RunnableTask(Context context, ObjectObjPic objectObjPic) {
            this.context = context;
            this.objectObjPic = objectObjPic;
        }
        @Override
        public void run() {
            boolean go = true;
            while (go){
                if(backgroundJobs >0){
                    backgroundJobs -= 1;
                    new HttpsRequestUploadPictures(context, objectObjPic).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
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

    class HttpsRequestUploadPictures extends AsyncTask<String, String, InputStream> {
        private static final String upload_picture_url = "upload_picture.php";
        private Context context;
        Connector connector;
        ObjectObjPic objectObjPic;

        public HttpsRequestUploadPictures(Context ctx, ObjectObjPic objectPic){
            context = ctx;
            objectObjPic = objectPic;
        }

        @Override
        protected void onProgressUpdate(String... value) {
            super.onProgressUpdate(value);
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            //publishProgress("start");
            connector = new Connector(context, upload_picture_url);
            connector.addPostParameter("objectId",      MCrypt2.encodeToString(String.valueOf(myOrder.getMyObject().getId())));
            connector.addPostParameter("pictureName",   MCrypt2.encodeToString(objectObjPic.getPicName()));
            connector.addPostParameter("pictureSource", objectObjPic.orderPicImgSource(context));
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
                //String cntrl_msg  = MCrypt.decryptSingle(responseObject.getString("cntrl_msg"));
                String msg        = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (saveStatus.equals("1")) {
                    objectObjPic.setPicUrl(msg);
                    //objectObjPic.setPicUri("");
                }else{
                    //error handling?
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            backgroundJobs += 1;
            retutnThreadCount += 1;
            checkProceedOrderSend();
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

    class HttpsRequestSendOrderMessage extends AsyncTask<String, Void, InputStream> {
        private static final String send_order_message_url = "send_order_message.php";

        private Context context;
        Connector connector;

        public HttpsRequestSendOrderMessage(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, send_order_message_url);
            //connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(Base64.encodeToString(myUser.getUname().getBytes(), Base64.DEFAULT).getBytes()), Base64.DEFAULT));
            //connector.addPostParameter("user_id", Base64.encodeToString(MCrypt.encrypt(Base64.encodeToString(myUser.getId().toString().getBytes(), Base64.DEFAULT).getBytes()), Base64.DEFAULT));
            //connector.addPostParameter("user_type",  Base64.encodeToString(MCrypt.encrypt(myUser.getType().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("order",  MCrypt2.encodeToString(myOrder.toJson()));
            connector.addPostParameter("dealer", MCrypt2.encodeToString(myOrder.getMyDealer().toJson()));
            connector.addPostParameter("object", MCrypt2.encodeToString(myOrder.getMyObject().toJson()));
            connector.addPostParameter("pictureList", getPicArrayListJson(myOrder.getMyPictureList()));
            if(((ActivityOrder1)context).getActionType() != null){
                connector.addPostParameter("actionType", MCrypt2.encodeToString(((ActivityOrder1)context).getActionType()));
                connector.addPostParameter("originalEmailHtml", MCrypt2.encodeToString(((ActivityOrder1)context).getMyHtml()));
                connector.addPostParameter("originalMessageId", MCrypt2.encodeToString(((ActivityOrder1)context).getMyPreviousEmail().getMessageId()));
                //connector.addPostParameter("originalEmailHtml", MCrypt2.encodeToString(((ActivityOrder1)context).generateOriginalEmailHtml()));
            }
            connector.addPostParameter("messageHtml", MCrypt2.encodeToString(((ActivityOrder1)context).generateHtml()));
            //--to do pictures
            //connector.addPostParameter("control",  "not coded");
            //connector.addPostParameter("control2",  MCrypt2.encodeToString("coded"));

            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        private String getPicArrayListJson(ArrayList<ObjectObjPic> pictureList){
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i < pictureList.size(); i++){
                jsonArray.put(pictureList.get(i).toJson());
            }
            return jsonArray.toString();
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            try {
                connector.decodeResponse();
                JSONObject object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));
                String save_status = object.getString("status");
                String msg = object.getString("msg");
                String sent = object.getString("sent");
                if ((save_status.equals("1"))&&(myOrder.getId().equals(-1))) {
                    myOrder.setId(Integer.parseInt(object.getString("order_id")));
                    myOrder.setEmailSent(true);
                    for(ObjectObjPic pictureObject: myOrder.getMyPictureList()){
                        pictureObject.setObjectId(myOrder.getId());
                    }
                    Toast.makeText(context, context.getResources().getString(R.string.email_sent), Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(context, context.getResources().getString(R.string.email_not_sent), Toast.LENGTH_SHORT).show();
                    fragmentOrderPart3.setSendingStatus(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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
                    //---------------Read myUser object----------------------
                    if(myUser == null){
                        myUser = getIntent().getParcelableExtra("myUser");
                    }
                    /*
                    if(ActivityOrder1.getStaticOrder() != null){
                        myOrder = ActivityOrder1.getStaticOrder();
                    }
                    */
                    new HttpsRequestGetDealerList(context).execute();
                }else{
                    //session and last activity deleted in DB, app will log-out
                    Toast.makeText(context, context.getResources().getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(inputStream);
        }
    }

    class HttpsRequestGetDealerList extends AsyncTask<String, Void, InputStream> {
        private static final String get_dealer_list_url = "get_dealer_list.php";

        private Context context;
        Connector connector;

        public HttpsRequestGetDealerList(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_dealer_list_url);
            //connector.addPostParameter("user_type", MCrypt2.encodeToString(myUser.getType()));
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

            if(((ActivityOrder1) context).myDealerList == null){
               ((ActivityOrder1) context).myDealerList = new ArrayList<>();
               ((ActivityOrder1) context).myDealerListOriginal = new ArrayList<>();
            }

            ArrayList<ObjectDealer> dealerArrayList = getDealerList(connector);
            ((ActivityOrder1) context).myDealerList.removeAll(((ActivityOrder1) context).myDealerListOriginal);
            ((ActivityOrder1) context).myDealerList.addAll(dealerArrayList);
            ((ActivityOrder1) context).myDealerListOriginal = new ArrayList<ObjectDealer>();
            ((ActivityOrder1) context).myDealerListOriginal.addAll(((ActivityOrder1) context).myDealerList);
            //((ActivityOrder1) context).myAdapterDealerShow.notifyDataSetChanged();
            //((ActivityOrder1) context).swipeRefreshLayout.setRefreshing(false);

            if( getIntent().getParcelableExtra("myDealer") != null ){
                myDealer = new ArrayList<>();
                myDealer.add(getIntent().getParcelableExtra("myDealer"));
                myPreviousEmail = getIntent().getParcelableExtra("myOrder");

                //myAdapterObject = new MyAdapterObjectShowP1(this, null, myObject, myUser, objectOnClickListener, "objectShowP1", false);
                myOrder.setMyDealer(myDealer.get(0));
            }
            if((myDealer == null)&&(myPreviousEmail != null)){
                try {
                    String[] from1 = myPreviousEmail.getFrom().split("<");
                    String[] from2 = from1[1].split(">");
                    String email = from2[0];
                    for(ObjectDealer objectDealer: ((ActivityOrder1) context).myDealerList){
                        if(objectDealer.getEmail().equals(email)){
                            myDealer = new ArrayList<>();
                            myDealer.add(objectDealer);
                            myOrder.setMyDealer(myDealer.get(0));
                            break;
                        }
                    }
                }catch (Exception e){
                }

            }

            new HttpsRequestGetObjectList(context).execute();
            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectDealer>getDealerList(Connector conn){
            ArrayList<ObjectDealer> dealerArrayList = new ArrayList<>();
            try{
                ObjectDealer objectDealer;
                JSONArray responseObjects1 = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects1.length(); i++) {
                    objectDealer = new ObjectDealer((JSONObject) responseObjects1.get(i));

                    dealerArrayList.add(objectDealer);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return dealerArrayList;
        }
    }

    class HttpsRequestGetObjectList extends AsyncTask<String, Void, InputStream> {
        private static final String get_object_list_url = "get_object_list.php";

        private Context context;
        Connector connector;

        public HttpsRequestGetObjectList(Context ctx){
            context = ctx;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_object_list_url);
            connector.addPostParameter("user_type",  Base64.encodeToString(MCrypt.encrypt(myUser.getType().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(Base64.encodeToString(myUser.getUname().getBytes(), Base64.DEFAULT).getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if(((ActivityOrder1) context).myObjectList == null){
                ((ActivityOrder1) context).myObjectList = new ArrayList<>();
                ((ActivityOrder1) context).myObjectListOriginal = new ArrayList<>();
            }
            connector.decodeResponse();
            ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityOrder1) context).myObjectList.removeAll(((ActivityOrder1) context).myObjectListOriginal);
            ((ActivityOrder1) context).myObjectList.addAll(objectArryList);
            ((ActivityOrder1) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityOrder1) context).myObjectListOriginal.addAll(((ActivityOrder1) context).myObjectList);

            if( getIntent().getParcelableExtra("myObject") != null ){
                myObject = new ArrayList<>();
                myObject.add(getIntent().getParcelableExtra("myObject"));
                //myPreviousEmail = getIntent().getParcelableExtra("myOrder");

                //myAdapterObject = new MyAdapterObjectShowP1(this, null, myObject, myUser, objectOnClickListener, "objectShowP1", false);
                myOrder.setMyObject(myObject.get(0));

                //ObjectOrder ord = getIntent().getParcelableExtra("myOrder");
                //this.myOrder.setMyHtml(ord.getMyHtml());
                //myHtml = getIntent().getParcelableExtra("myHtml");
            }
            if(getActionType() != null){
                new HttpsEmailDetails(context).execute();
            }else{
                callback();
            }
            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectObject>getObjectList(Connector conn){
            ArrayList<ObjectObject> objectArrayList = new ArrayList<>();
            try {
                ObjectObject objectObject;
                JSONArray responseObjects = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects.length(); i++) {
                    objectObject = new ObjectObject((JSONObject) responseObjects.get(i));
                    objectArrayList.add(objectObject);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return objectArrayList;
        }
    }

    class HttpsEmailDetails extends AsyncTask<String, Void, InputStream> {
        private static final String get_email_details_url = "get_email_details.php";

        private Context context;
        Connector connector;

        public HttpsEmailDetails(Context ctx){
            context = ctx;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_email_details_url);
            connector.addPostParameter("user_type",  Base64.encodeToString(MCrypt.encrypt(myUser.getType().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(Base64.encodeToString(myUser.getUname().getBytes(), Base64.DEFAULT).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_id",    Base64.encodeToString(MCrypt.encrypt(myUser.getId().toString().getBytes()), Base64.DEFAULT));
            //connector.addPostParameter("set_email_viewed", Base64.encodeToString(MCrypt.encrypt(String.valueOf("X").getBytes()), Base64.DEFAULT));
            connector.addPostParameter("message_id",    Base64.encodeToString(MCrypt.encrypt(myPreviousEmail.getMessageId().getBytes()), Base64.DEFAULT));
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
            try {
                JSONObject responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                myHtml = MCrypt.decryptSingle(responseObject.getString("html"));

                //fragmentOrderPart2.setMyHtml(myHtml);
                if (myObject != null) {
                    myAdapterObject = new MyAdapterObjectShowP1(context, null, myObject, myUser, objectOnClickListener, "objectShowP1", false);
                }
                if(myDealer != null) {
                    myAdapterDealer = new MyAdapterDealerShow(context, myDealer, myDealerListOriginal, "dealerShowP1", dealerOnClickListener, false, myUser);
                }

                //myOrder.setMyObject(myObject.get(0));
                //myOrder.setMyHtml(myHtml);

            }catch (Exception e){
                e.printStackTrace();
            }

            callback();
            //fragmentOrderPart1.setViewEnabled(true);
            super.onPostExecute(inputStream);
        }
    }
}