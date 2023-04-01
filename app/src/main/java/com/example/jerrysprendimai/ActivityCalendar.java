package com.example.jerrysprendimai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ActivityCalendar extends AppCompatActivity {
    private static final int CALENDAR_READ_CODE  = 2000;
    private static final int CALENDAR_WRITE_CODE = 2001;
    private static final String CALENDER_NAME = "Jerry";

    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;
    ArrayList<ObjectEvent> myEventList;

    ObjectObject objectToDisplay;

    RecyclerView eventRecyclerView;
    MyAdapterCalendarEvents myAdapterCalendarEvents;

    ObjectUser myUser;
    CompactCalendarView sundeepkCalendarView;
    TextView calendarCaption, buttonLeft, buttonRight, calendarDayCaption;
    CardView calendarCardView;
    ProgressBar progressBar;
    Date displayDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        this.myEventList  = new ArrayList<>();
        this.myObjectList = new ArrayList<>();
        this.myObjectListOriginal = new ArrayList<>();

        //---------------Read myUser object----------------------
        this.myUser = getIntent().getParcelableExtra("myUser");

        //----Element binding
        sundeepkCalendarView = findViewById(R.id.calendar_sundeepk_calendarView);
        calendarCaption      = findViewById(R.id.calendar_caption);
        buttonLeft           = findViewById(R.id.calendar_button_left);
        buttonRight          = findViewById(R.id.calendar_button_right);
        calendarCardView     = findViewById(R.id.calendar_cardView);
        calendarDayCaption   = findViewById(R.id.calendar_day_caption);
        progressBar          = findViewById(R.id.calendar_progressBar);
        //eventIcon            = findViewById(R.id.calendar_event_icon);

        //----fill values
        progressBar.setVisibility(View.GONE);
        //eventIcon

        //----cehck calendar permission
        /*if (!(checkPermission(CALENDAR_READ_CODE, Manifest.permission.READ_CALENDAR) == true)   ||
                !(checkPermission(CALENDAR_WRITE_CODE, Manifest.permission.WRITE_CALENDAR) == true)){
            return;
        }*/

        Calendar calendar = Calendar.getInstance();
        SQLiteCalendarDB sqLiteCalendarHelper = new SQLiteCalendarDB(this);
        sqLiteCalendarHelper.setMonthEvents(sundeepkCalendarView, calendar);
        //HelperCalendar jerryCalenderHelper = new HelperCalendar(this);
        //jerryCalenderHelper.setMonthEvents(sundeepkCalendarView, calendar);

        try {
            String value = DateFormat.getDateInstance(DateFormat.YEAR_FIELD).format(calendar.getTime());
            String[] strArry = value.split(" ");
            calendarCaption.setText(strArry[1] + " " + strArry[2]);
        }catch (Exception e){}
        sundeepkCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                setDisplayDate(dateClicked);
                calendarDayCaption.setText(DateFormat.getDateInstance(DateFormat.FULL).format(dateClicked.getTime()));
                getMyEventList().removeAll(getMyEventList());
                getMyEventList().addAll(sqLiteCalendarHelper.getDayEvents(dateClicked));
                //getMyEventList().addAll(jerryCalenderHelper.getDayEvents(dateClicked));
                myAdapterCalendarEvents.notifyDataSetChanged();

                sundeepkCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.jerry_blue_opacity));
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                try {
                    String value = DateFormat.getDateInstance(DateFormat.YEAR_FIELD).format(firstDayOfNewMonth.getTime());
                    String[] strArry = value.split(" ");
                    calendarCaption.setText(strArry[1] + " " + strArry[2]);
                }catch (Exception e){}

                Calendar calendar =  Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                //calendar.set(Calendar.MONTH, calendar.getTime().getMonth());
                calendar.set(Calendar.HOUR,   0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date currentFirstDayOfMonth = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if(dateFormat.format(currentFirstDayOfMonth).compareTo(dateFormat.format(firstDayOfNewMonth)) == 0) {
                    sundeepkCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.jerry_grey_light));
                }else{
                    sundeepkCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.white));
                }
                calendar.setTimeInMillis(firstDayOfNewMonth.getTime());
                sqLiteCalendarHelper.setMonthEvents(sundeepkCalendarView, calendar);
                //jerryCalenderHelper.setMonthEvents(sundeepkCalendarView, calendar);
                sundeepkCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.jerry_blue_opacity));
                onDayClick(firstDayOfNewMonth);
            }
        });
        sundeepkCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        sundeepkCalendarView.setCurrentDate(Calendar.getInstance().getTime());
        sundeepkCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.jerry_grey_light));

        //----button click
        buttonLeft.setOnClickListener(v->{
            //sundeepkCalendarView.showCalendarWithAnimation();
            sundeepkCalendarView.scrollLeft();
        });
        buttonRight.setOnClickListener(v->{
            //sundeepkCalendarView.showCalendarWithAnimation();
            sundeepkCalendarView.scrollRight();
        });

        //----recyclerView handler
        buildRwcyclerView();
        //----to show todays events when calender is opened
        Date date = new Date();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        date.setTime(today.getTimeInMillis());
        getMyEventList().removeAll(getMyEventList());
        getMyEventList().addAll(sqLiteCalendarHelper.getDayEvents(date));
        //getMyEventList().addAll(jerryCalenderHelper.getDayEvents(date));
        setDisplayDate(date);
        myAdapterCalendarEvents.notifyDataSetChanged();
        calendarDayCaption.setText(DateFormat.getDateInstance(DateFormat.FULL).format(today.getTime()));

        setViewEnabled(true);

        Button del = findViewById(R.id.test_button_delte);
        del.setOnClickListener(v->{
            //----delete "Jerry" Calender
            ContentResolver contentResolver = getContentResolver();
            String[] args = new String[]{CALENDER_NAME};
            contentResolver.delete(CalendarContract.Calendars.CONTENT_URI, "NAME = ?", args);
        });
    }

    public void setViewEnabled(boolean value){

        sundeepkCalendarView.setEnabled(value);
        calendarCaption.setEnabled(value);
        buttonLeft.setEnabled(value);
        buttonRight.setEnabled(value);
        calendarCardView.setEnabled(value);
        calendarDayCaption.setEnabled(value);
        if(value) {
           progressBar.setVisibility(View.GONE);
        }else{
           progressBar.setVisibility(View.VISIBLE);
        }


    }

    public void buildRwcyclerView() {
        this.eventRecyclerView    = findViewById(R.id.calendar_event_recycleView);
        this.myAdapterCalendarEvents = new MyAdapterCalendarEvents(this, this.myEventList, this.myObjectList, this.myUser);
        this.eventRecyclerView.setAdapter(myAdapterCalendarEvents);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean checkPermission(int callbackId, String... permissionsId){
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissions){
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
        }
        return permissions;
    }

    @Override
    protected void onResume() {
        setViewEnabled(false);
        new HttpsRequestCheckSessionAlive(this).execute();

        super.onResume();
    }
    public void lockView(){
        for(int i=0; i<myEventList.size(); i++){
            try {
                myEventList.get(i).getMyViewHolderCalendarEvents().myRow.setEnabled(false);
            }catch (Exception e){

            }
        }
    }

    public void unlockView(){
        for(int i=0; i<myEventList.size(); i++){
            try {
                myEventList.get(i).getMyViewHolderCalendarEvents().myRow.setEnabled(true);
            }catch (Exception e){

            }
        }
    }

    public ObjectObject getObjectToDisplay() {       return objectToDisplay;    }
    public void setObjectToDisplay(ObjectObject objectToDisplay) {        this.objectToDisplay = objectToDisplay;    }
    public Date getDisplayDate() {       return displayDate;    }
    public void setDisplayDate(Date displayDate) {        this.displayDate = displayDate;    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if(){}
        super.onActivityResult(requestCode, resultCode, data);
    }

    public ArrayList<ObjectEvent> getMyEventList() {  return myEventList;  }
    public void setMyEventList(ArrayList<ObjectEvent> myEventList) {  this.myEventList = myEventList; }

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
            ((ActivityCalendar) context).myObjectList.removeAll(((ActivityCalendar) context).myObjectListOriginal);
            ((ActivityCalendar) context).myObjectList.addAll(objectArryList);
            ((ActivityCalendar) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityCalendar) context).myObjectListOriginal.addAll(((ActivityCalendar) context).myObjectList);

            ((ActivityCalendar) context).setViewEnabled(true);

            //----sync calender events
            //----cehck calendar permission
            boolean permissionOk = true;
            //if (!(checkPermission(CALENDAR_READ_CODE, Manifest.permission.READ_CALENDAR) == true)   ||
            //        !(checkPermission(CALENDAR_WRITE_CODE, Manifest.permission.WRITE_CALENDAR) == true)){
            //    permissionOk = false;
            //}
            //if( permissionOk && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED){
            //if(permissionOk){

                SQLiteCalendarDB sqLiteCalendarHelper = new SQLiteCalendarDB(context);
                sqLiteCalendarHelper.syncCalendarEvents(myObjectList);

                //HelperCalendar jerryCalenderHelper = new HelperCalendar(context);
                //jerryCalenderHelper.syncJerryCalenderEvents(myObjectList);

                if((myAdapterCalendarEvents != null)&&(getDisplayDate() != null)&&(sundeepkCalendarView != null)){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(getDisplayDate().getTime());
                    sqLiteCalendarHelper.setMonthEvents(sundeepkCalendarView, calendar);
                    //jerryCalenderHelper.setMonthEvents(sundeepkCalendarView, calendar);
                    getMyEventList().removeAll(getMyEventList());
                    getMyEventList().addAll(sqLiteCalendarHelper.getDayEvents(getDisplayDate()));
                    //getMyEventList().addAll(jerryCalenderHelper.getDayEvents(getDisplayDate()));
                    myAdapterCalendarEvents.notifyDataSetChanged();
            //}
                //Uri deleteUri = null;
                //deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong("53"));
                //int rows = getContentResolver().delete(deleteUri, null, null);

            }

            //findViewById(R.id.progressBar).setVisibility(View.GONE);

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

                    if((myAdapterCalendarEvents != null)&&(getObjectToDisplay() != null)){
                        new HttpsRequestLockObject(context, objectToDisplay, objectToDisplay.getId().toString(),"unlock").execute();
                        setObjectToDisplay(null);
                    }

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
            unlockView();
            super.onPostExecute(inputStream);
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
                    //getObjectDetailsAndDisplay(objectObject);
                }else{
                    //Toast.makeText(context, "Užrakinta", Toast.LENGTH_SHORT).show();
                }
                //((ActivityObjectShow) context).onRefresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}