package com.example.jerrysprendimai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivityOrder2 extends AppCompatActivity {
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_WRITE_EXTERNAL_STORAGE = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 10;

    Button proceedButton, backButton;
    TextInputEditText textInput;
    EditText invisibleFocus;
    RecyclerView photoRecyclerView;
    boolean deletionMode;
    ObjectUser myUser;
    ObjectOrder myOrder;
    String mCurrentPhotoPath;
    Uri mCurrentPhotoUri;
    File mPhotoFile;
    ArrayList<ObjectObjPic> toBeDeletedList;
    LinearLayout takePhoto, addPhoto, deletePhoto, cancelPhotoEdit,addModeButtons, deletionModeButtons;
    MyAdapterOrderPicture myAdapterOrderPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder2);

        //----binding
        proceedButton  = findViewById(R.id.button_order_p2_continue);
        backButton     = findViewById(R.id.button_order_p2_back);
        textInput      = findViewById(R.id.oder_p2_textInput);
        invisibleFocus = findViewById(R.id.order_invisibleFocusHolder);
        photoRecyclerView = findViewById(R.id.order_photo_recycleView);
        takePhoto     = findViewById(R.id.oder_p2_take_photo_linearLayout);
        addPhoto      = findViewById(R.id.oder_p2_add_photo_linearLayout);
        deletePhoto   = findViewById(R.id.order_p2_delete_photo_linearLayout);
        cancelPhotoEdit = findViewById(R.id.order_p2_cancel_linearLayout);
        addModeButtons = findViewById(R.id.order_p2_add_photo_buttons_linear_layout);
        deletionModeButtons = findViewById(R.id.order_p2_delete_photo_buttons_linear_layout);

        //---------------Read references from Intent----------------------
        this.myUser = getIntent().getParcelableExtra("myUser");
        this.myOrder = getIntent().getParcelableExtra("myOrder");

        proceedButton.setText(proceedButton.getText() + "   2 / 3");
        textInput.setText(myOrder.getMyText());
        backButton.setOnClickListener(v->{
            onBackPressed();
        });

        boolean deletionMode = false;
        deletionModeButtons.setVisibility(View.GONE);
        toBeDeletedList = new ArrayList<>();

        //-----invisible focus holder dandling
        this.invisibleFocus.setInputType(InputType.TYPE_NULL);
        this.invisibleFocus.requestFocus();

        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!myOrder.getMyText().equals(textInput.getText().toString())){
                    myOrder.setMyText(textInput.getText().toString());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {        }
        });

        takePhoto.setOnClickListener(v->{
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
        });

        deletePhoto.setOnClickListener(v->{
            for(ObjectObjPic pic: toBeDeletedList){
                myOrder.getMyPictureList().remove(pic);
            }
            setDeletionMode(false);
            deletionModeButtons.setVisibility(View.GONE);
            addModeButtons.setVisibility(View.VISIBLE);
            myAdapterOrderPicture.notifyDataSetChanged();
        });

        myAdapterOrderPicture = new MyAdapterOrderPicture(this, myOrder.getMyPictureList(), myUser);
        photoRecyclerView.setAdapter(myAdapterOrderPicture);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //-----take picture
        if(resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE ){
            galleryAddPic();
            ObjectObjPic newPic = new ObjectObjPic();
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
    }

    @Override
    public void onBackPressed() {
        ActivityOrder1.setStaticOrder(myOrder);
        //parentActivity.getIntent().putExtra("myOrder", myOrder);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
    }

    public String getmCurrentPhotoPath() {    return mCurrentPhotoPath;    }
    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {    this.mCurrentPhotoPath = mCurrentPhotoPath;    }
    public Uri getmCurrentPhotoUri() {        return mCurrentPhotoUri;    }
    public void setmCurrentPhotoUri(Uri mCurrentPhotoUri) {        this.mCurrentPhotoUri = mCurrentPhotoUri;    }
    public File getmPhotoFile() {        return mPhotoFile;    }
    public boolean isDeletionMode() {        return deletionMode;    }
    public void setDeletionMode(boolean deletionMode) {        this.deletionMode = deletionMode;    }
    public void setmPhotoFile(File mPhotoFile) {        this.mPhotoFile = mPhotoFile;    }
    public ArrayList<ObjectObjPic> getToBeDeletedList() {        return toBeDeletedList;    }
    public void setToBeDeletedList(ArrayList<ObjectObjPic> toBeDeletedList) {        this.toBeDeletedList = toBeDeletedList;    }


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
                    //getObjectDetailsAndDisplay(myObject);
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
            //unlockView();
            super.onPostExecute(inputStream);
        }
    }
}