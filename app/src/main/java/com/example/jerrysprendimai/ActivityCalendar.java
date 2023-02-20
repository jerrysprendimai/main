package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

public class ActivityCalendar extends AppCompatActivity {
    private static final int CALENDAR_READ_CODE  = 2000;
    private static final int CALENDAR_WRITE_CODE = 2001;
    private static final String CALENDER_NAME = "Jerry";

    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;

    ObjectUser myUser;
    CompactCalendarView sundeepkCalendarView;
    TextView calendarCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        this.myObjectList = new ArrayList<>();
        this.myObjectListOriginal = new ArrayList<>();

        //---------------Read myUser object----------------------
        this.myUser = getIntent().getParcelableExtra("myUser");

        //----cehck calendar permission
        if (!(checkPermission(CALENDAR_READ_CODE, Manifest.permission.READ_CALENDAR) == true)   ||
                !(checkPermission(CALENDAR_WRITE_CODE, Manifest.permission.WRITE_CALENDAR) == true)){
            return;
        }

        //----Element binding
        sundeepkCalendarView = findViewById(R.id.calendar_sundeepk_calendarView);
        calendarCaption      = findViewById(R.id.calendar_caption);


        //----fill values
        Calendar calendar = Calendar.getInstance();
        HelperCalendar jerryCalenderHelper = new HelperCalendar(this);
        jerryCalenderHelper.setMonthEvents(sundeepkCalendarView, calendar);
        try {
            String value = DateFormat.getDateInstance(DateFormat.YEAR_FIELD).format(calendar.getTime());
            String[] strArry = value.split(" ");
            calendarCaption.setText(strArry[1] + " " + strArry[2]);
        }catch (Exception e){}
        sundeepkCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                //sundeepkCalendarView.setCurrentDate(Calendar.getInstance().getTime());
                sundeepkCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.jerry_blue_opacity));
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                try {
                    String value = DateFormat.getDateInstance(DateFormat.YEAR_FIELD).format(firstDayOfNewMonth.getTime());
                    String[] strArry = value.split(" ");
                    calendarCaption.setText(strArry[1] + " " + strArry[2]);
                }catch (Exception e){}
                sundeepkCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.white));
                Calendar calendar =  Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 0);
                calendar.set(Calendar.HOUR,   12);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Date currentFirstDayOfMonth = calendar.getTime();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if(dateFormat.format(currentFirstDayOfMonth).compareTo(dateFormat.format(firstDayOfNewMonth)) == 0) {
                    sundeepkCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.jerry_grey_light));
                }else{
                    sundeepkCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.white));
                }
                jerryCalenderHelper.setMonthEvents(sundeepkCalendarView, calendar);
            }
        });
        sundeepkCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        sundeepkCalendarView.setCurrentDate(Calendar.getInstance().getTime());
        sundeepkCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.jerry_grey_light));
       /*
        Event ev1 = new Event(getResources().getColor(R.color.jerry_blue), Calendar.getInstance().getTimeInMillis(), "Some extra data that I want to store.");
        sundeepkCalendarView.addEvent(ev1);

        Event ev2 = new Event(getResources().getColor(R.color.jerry_blue), 1676790000000L);
        sundeepkCalendarView.addEvent(ev2);
        */

        Button del = findViewById(R.id.test_button_delte);
        del.setOnClickListener(v->{
            //----delete "Jerry" Calender
            ContentResolver contentResolver = getContentResolver();
            String[] args = new String[]{CALENDER_NAME};
            contentResolver.delete(CalendarContract.Calendars.CONTENT_URI, "NAME = ?", args);
        });
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
        new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if(){}
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
            ((ActivityCalendar) context).myObjectList.removeAll(((ActivityCalendar) context).myObjectListOriginal);
            ((ActivityCalendar) context).myObjectList.addAll(objectArryList);
            ((ActivityCalendar) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityCalendar) context).myObjectListOriginal.addAll(((ActivityCalendar) context).myObjectList);

            //----sync calender events
            //----cehck calendar permission
            boolean permissionOk = true;
            if (!(checkPermission(CALENDAR_READ_CODE, Manifest.permission.READ_CALENDAR) == true)   ||
                    !(checkPermission(CALENDAR_WRITE_CODE, Manifest.permission.WRITE_CALENDAR) == true)){
                permissionOk = false;
            }
            if(permissionOk){
                HelperCalendar jerryCalenderHelper = new HelperCalendar(context);
                jerryCalenderHelper.syncJerryCalenderEvents(myObjectList);

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