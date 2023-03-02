package com.example.jerrysprendimai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityMenu extends AppCompatActivity {

    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    static final int MY_WRITE_EXTERNAL_STORAGE = 101;
    private static final int PERMISSION_CODE = 1001;
    private static final int CALENDAR_READ_CODE  = 2000;
    private static final int CALENDAR_WRITE_CODE = 2001;

    final String user = "user";
    final String admin = "admin";

    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;

    ObjectUser myUser;
    LinearLayout mainContainer;
    GridLayout gridLayout;
    Integer backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        this.backButtonCount = 0;
        this.myObjectList = new ArrayList<>();
        this.myObjectListOriginal = new ArrayList<>();

        ((TextView) findViewById(R.id.jerry_version)).setHint("- " + ((TextView) findViewById(R.id.jerry_version)).getHint() + " " + BuildConfig.VERSION_NAME + " -");
        findViewById(R.id.jerry_version).setVisibility(View.VISIBLE);
        findViewById(R.id.jerry_copyright).setVisibility(View.GONE);

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        Context context = this;

        this.mainContainer = findViewById(R.id.menu_main_containerView);
        this.gridLayout    = findViewById(R.id.menu_gridLayout);

        //---------------Read myUser object----------------------
        this.myUser = getIntent().getParcelableExtra("myUser");
        TextView userTextView = findViewById(R.id.main_menu_user_value);
        userTextView.setHint("");
        userTextView.setText(this.myUser.getUname());

        TextView userTypeTextView = findViewById(R.id.main_menu_user_type_value);
        userTypeTextView.setText(this.myUser.getUser_lv());
        userTypeTextView.setHint("");

        //--------------set user view visibility
        setUserLevelView();

        //------------Settings
        LinearLayout settingsLayout = (LinearLayout) findViewById(R.id.main_menu_settings);
        settingsLayout.setOnClickListener(v -> {
            this.backButtonCount = 0;
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivitySettings.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });

        //------------User_Show
        LinearLayout userLayout = (LinearLayout) findViewById(R.id.main_menu_user);
        userLayout.setOnClickListener(v -> {
            this.backButtonCount = 0;
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivityUserShow.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });

        //------------Object_Show
        ((TextView) findViewById(R.id.menu_caption_work)).setVisibility(View.GONE);
        LinearLayout objectLayout = (LinearLayout) findViewById(R.id.main_menu_work);
        objectLayout.setOnClickListener(v -> {
            this.backButtonCount = 0;
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivityObjectShow.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });

        //-----------Calendar_Show
        ((TextView) findViewById(R.id.menu_caption_calendar)).setVisibility(View.GONE);
        LinearLayout calendarLayout = (LinearLayout) findViewById(R.id.main_menu_calendar);
        calendarLayout.setOnClickListener(v -> {
            this.backButtonCount = 0;
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivityCalendar.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });


        //----cehck calendar permission
        boolean permissionOk = true;
        /*if (!(checkPermission(this, CALENDAR_READ_CODE, Manifest.permission.READ_CALENDAR) == true)   ||
                !(checkPermission(this, CALENDAR_WRITE_CODE, Manifest.permission.WRITE_CALENDAR) == true)){
            permissionOk = false;
        }*/
        /*
        //----check pictures permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //permission not granted, request it
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                //show popup for runtime permission
                requestPermissions(permissions, PERMISSION_CODE);
            }else{
                //permission already granted
            }
        }else{
               //system os is less that marshmallow
        }
        //----check camera permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                //check permission external storage
                if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
                } else {
                    //permission already granted
                }
            }
        }else{
            //system os is less that marshmallow
        }
        */
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void disableWholeView(ViewGroup gridView){
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View child = gridView.getChildAt(i);
            if((child instanceof CardView) && (getResources().getResourceEntryName(child.getId()).contains("CardView_main_menu"))){
                //child.setBackground(getDrawable(R.drawable.button_disabled));
                View linerLayout = ((CardView) child).getChildAt(0);
                linerLayout.setEnabled(false);
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public  void enableWholeView(ViewGroup gridView){
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View child = gridView.getChildAt(i);
            if((child instanceof CardView) && (getResources().getResourceEntryName(child.getId()).contains("CardView_main_menu"))){
                //child.setBackground(getDrawable(R.drawable.round_button));
                View linerLayout = ((CardView) child).getChildAt(0);
                linerLayout.setEnabled(true);
            }
        }
    }

    @Override
    protected void onResume() {
        this.backButtonCount = 0;
        new HttpsRequestCheckSessionAlive(this).execute();

        //findViewById(R.id.progressBar).setVisibility(View.GONE);
        //enableWholeView(gridLayout);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //-----------back button press handling: in case object not saved - show warning
        if (backButtonCount.equals(0)) {
            Toast.makeText(this, getResources().getString(R.string.exit), Toast.LENGTH_SHORT).show();
            backButtonCount++;
        } else {
            this.backButtonCount = 0;
            myUser.setSessionId("");
            new HttpsRequestCheckSessionAlive(this).execute();
            //super.onBackPressed();
        }
    }

    private void setUserLevelView() {
        if (this.myUser.getUser_lv().equals(user)){
            ((CardView) findViewById(R.id.main_menu_user_indicator)).setCardBackgroundColor(getResources().getColor(R.color.teal_700));
            findViewById(R.id.CardView_main_menu_dealers).setVisibility(View.GONE);
            findViewById(R.id.CardView_main_menu_user).setVisibility(View.GONE);
        }else if(this.myUser.getUser_lv().equals(admin)){
            ((CardView) findViewById(R.id.main_menu_user_indicator)).setCardBackgroundColor(getResources().getColor(R.color.jerry_grey));
            findViewById(R.id.CardView_main_menu_dealers).setVisibility(View.VISIBLE);
            findViewById(R.id.CardView_main_menu_user).setVisibility(View.VISIBLE);
        }else{
            ((CardView) findViewById(R.id.main_menu_user_indicator)).setCardBackgroundColor(getResources().getColor(R.color.jerry_blue));
            findViewById(R.id.CardView_main_menu_dealers).setVisibility(View.VISIBLE);
            findViewById(R.id.CardView_main_menu_user).setVisibility(View.VISIBLE);
        }

    }

    public boolean checkPermission(Context context, int callbackId, String... permissionsId){
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissions){
            ActivityCompat.requestPermissions((Activity) context, permissionsId, callbackId);
        }
        return permissions;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
            connector.decodeResponse();
            ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityMenu) context).myObjectList.removeAll(((ActivityMenu) context).myObjectListOriginal);
            ((ActivityMenu) context).myObjectList.addAll(objectArryList);
            ((ActivityMenu) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityMenu) context).myObjectListOriginal.addAll(((ActivityMenu) context).myObjectList);

            //----Work Caption handling
            int newCount = 0;
            for(int i = 0; i < objectArryList.size(); i++){
                if(objectArryList.get(i).getNotViewed().equals("X")){
                  newCount ++;
                }
            }
            if( newCount > 0){
                ((TextView) findViewById(R.id.menu_caption_work)).setVisibility(View.VISIBLE);
            }else{
                ((TextView) findViewById(R.id.menu_caption_work)).setVisibility(View.GONE);
            }
            ((TextView) findViewById(R.id.menu_caption_work)).setText(String.valueOf(newCount));

            //----sync calender events
            //----cehck calendar permission
            boolean permissionOk = true;

            //if( permissionOk && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){

                SQLiteCalendarDB sqLiteCalendarHelper = new SQLiteCalendarDB(context);
                sqLiteCalendarHelper.syncCalendarEvents(myObjectList);

                //sqLiteCalendarHelper.getWritableDatabase().execSQL("DROP TABLE calendarEvents");

                //HelperCalendar jerryCalenderHelper = new HelperCalendar(context);
                //jerryCalenderHelper.syncJerryCalenderEvents(myObjectList);

              //----calendar caption handling
                if( newCount > 0){
                    ((TextView) findViewById(R.id.menu_caption_calendar)).setVisibility(View.VISIBLE);
                }else{
                    ((TextView) findViewById(R.id.menu_caption_calendar)).setVisibility(View.GONE);
                }
                ((TextView) findViewById(R.id.menu_caption_calendar)).setText(String.valueOf(newCount));
            //}

            findViewById(R.id.progressBar).setVisibility(View.GONE);
            enableWholeView(gridLayout);

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

                    //----get object list for calender events sync.
                    new HttpsRequestGetObjectList(context).execute();
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
            super.onPostExecute(inputStream);
        }
            }
}