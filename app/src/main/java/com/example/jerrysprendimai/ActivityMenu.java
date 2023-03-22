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
import android.widget.ImageView;
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
    final String owner = "owner";
    final String admin = "admin";

    public static ObjectUser objectUser;

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

        //--------------check single-sign-on
        SQLiteSSO.compare(this, myUser);

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
            if ((myUser.getUser_lv().equals(admin)) || (myUser.getUser_lv().equals(owner))){
                this.backButtonCount = 0;
                disableWholeView(gridLayout);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, ActivityUserShow.class);
                intent.putExtra("myUser", myUser);
                context.startActivity(intent);
            }else if (myUser.getUser_lv().equals(user)){
                this.backButtonCount = 0;
                disableWholeView(gridLayout);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, ActivityUserEdit.class);

                ObjectUser objectUser = myUser;
                try {
                    String value = new String(Base64.decode(objectUser.getPasswd(), 0));
                    objectUser.setPasswd(value);
                }catch (Exception e){

                }
                intent.putExtra("myUserEdit", objectUser);
                intent.putExtra("myUser", myUser);
                context.startActivity(intent);
            }
        });
        //------------User icon
        View.OnClickListener userProfileListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backButtonCount = 0;
                disableWholeView(gridLayout);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, ActivityUserEdit.class);

                ObjectUser objectUser = myUser;
                try {
                    String value = new String(Base64.decode(objectUser.getPasswd(), 0));
                    objectUser.setPasswd(value);
                }catch (Exception e){

                }
                intent.putExtra("myUserEdit", objectUser);
                intent.putExtra("myUser", myUser);
                context.startActivity(intent);
            }
        };
        ((CardView) findViewById(R.id.main_menu_user_indicator)).setOnClickListener(userProfileListener);
        ((LinearLayout) findViewById(R.id.main_menu_user_indicator_caption)).setOnClickListener(userProfileListener);

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

        //-----------Chat
        LinearLayout chatLayout = (LinearLayout) findViewById(R.id.main_menu_chat);
        chatLayout.setOnClickListener(v->{
            this.backButtonCount = 0;
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivityChatShow.class);
            intent.putExtra("myUser", myUser);
            intent.putParcelableArrayListExtra("myObjectList", myObjectList);
            context.startActivity(intent);
        });
        /*CardView chatCard = (CardView) findViewById(R.id.CardView_chat);
        chatCard.setVisibility(View.GONE);

        chatLayout.setVisibility(View.GONE);*/

        //-----------Supplier
        CardView supplierCard = (CardView) findViewById(R.id.CardView_dealers);
        supplierCard.setVisibility(View.GONE);
        LinearLayout supplierLayout = (LinearLayout) findViewById(R.id.main_menu_dealers);
        supplierLayout.setVisibility(View.GONE);

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

    public static void setMyUser(ObjectUser user){
        ActivityMenu.objectUser = user;
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
        if(ActivityMenu.objectUser != null){
            this.myUser.setFirst_name(ActivityMenu.objectUser.getFirst_name());
            this.myUser.setLast_name(ActivityMenu.objectUser.getLast_name());
            this.myUser.setUname(ActivityMenu.objectUser.getUname());
            this.myUser.setPasswd(ActivityMenu.objectUser.getPasswd());
            ActivityMenu.setMyUser(null);
        }
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
            ImageView image = findViewById(R.id.maim_menu_user_img);
            image.setImageResource(this.getResources().getIdentifier( "ic_person_white", "drawable", this.getApplicationInfo().packageName));
            TextView caption = findViewById(R.id.main_menu_user_caption);
            caption.setText(this.getResources().getString(R.string.my_profile));
            //findViewById(R.id.CardView_main_menu_user).setVisibility(View.GONE);
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

            //-------Notification click handling
            String objectId = ((ActivityMenu)context).getIntent().getExtras().getString("chatObjID");
            //String objectId = ((ActivityMenu)context).getIntent().getParcelableExtra("chatObjID");
            ObjectObject obj = null;
            if(objectId != null){
                ((ActivityMenu)context).getIntent().removeExtra("chatObjID");
                //((ActivityMenu)context).getIntent().getExtras().remove("chatObjID");
                for(int i=0; i<myObjectList.size();i++){
                    if(objectId.equals(myObjectList.get(i).getId().toString())){
                        obj = myObjectList.get(i);
                        break;
                    }
                }
               if(obj != null){
                   new HttpsRequestGetObjectDetails(context, obj).execute();
               }
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

    class HttpsRequestGetObjectDetails extends AsyncTask<String, Void, InputStream> {
        private static final String get_object_details_url = "get_object_details.php";

        private Context context;
        private ObjectObject clickObject;
        Connector connector;

        public HttpsRequestGetObjectDetails(Context ctx, ObjectObject obj){
            context     = ctx;
            clickObject = obj;
        }

        public ObjectObject getClickObject() { return clickObject; }
        public void setClickObject(ObjectObject clickObject) { this.clickObject = clickObject; }

        @Override
        protected InputStream doInBackground(String... strings) {
            connector = new Connector(context, get_object_details_url);
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(String.valueOf(clickObject.getId()).getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {

            //connector.decodeResponse();
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
                //getObjectDetailsArrayList().removeAll(getObjectDetailsArrayList());
                //getObjectDetailsArrayList().addAll(objDetailsArrayList);
                //setObjectDetailsArrayList(objDetailsArrayList);

                for(int i = 0; i < responseObjUser.length(); i++){
                    ObjectObjUser objectObjUser = new ObjectObjUser((JSONObject) responseObjUser.get(i));
                    objUserArrayList.add(objectObjUser);
                }
                //setObjectUserArrayList(objUserArrayList);

                for(int i = 0; i < responseObjPic.length(); i++){
                    ObjectObjPic objectObjPic = new ObjectObjPic((JSONObject) responseObjPic.get(i));
                    objPicsArrayList.add(objectObjPic);
                }

                //getObjectPicturesArrayList().removeAll(getObjectPicturesArrayList());
                //getObjectPicturesArrayList().addAll(objPicsArrayList);
                //setObjectPicturesArrayList(objPicsArrayList);

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

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(context, ActivityChat.class);
            intent.putExtra("myUser", myUser);
            intent.putExtra("objectObject", clickObject);
            intent.putParcelableArrayListExtra("listUser", objUserArrayList);
            intent.putParcelableArrayListExtra("employeeList", employeeArrayList);
            intent.putParcelableArrayListExtra("ownerList", ownerArrayList);
            context.startActivity(intent);

            super.onPostExecute(inputStream);
        }
    }
}