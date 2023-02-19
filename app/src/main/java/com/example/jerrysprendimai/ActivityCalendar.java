package com.example.jerrysprendimai;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class ActivityCalendar extends AppCompatActivity {
    private static final int CALENDAR_READ_CODE  = 2000;
    private static final int CALENDAR_WRITE_CODE = 2001;
    private static final String CALENDER_NAME = "Jerry";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        //--cehck calendar permission
        if (!(checkPermission(CALENDAR_READ_CODE, Manifest.permission.READ_CALENDAR) == true)   ||
                !(checkPermission(CALENDAR_WRITE_CODE, Manifest.permission.WRITE_CALENDAR) == true)){
            return;
        }

        Button del = findViewById(R.id.test_button_delte);
        del.setOnClickListener(v->{
            //----delete "Jerry" Calender
            ContentResolver contentResolver = getContentResolver();
            String[] args = new String[]{CALENDER_NAME};
            contentResolver.delete(CalendarContract.Calendars.CONTENT_URI, "NAME = ?", args);
        });

        Button btn = findViewById(R.id.test_button);
        btn.setOnClickListener(v->{

            //--cehck calendar permission
            if (!(checkPermission(CALENDAR_READ_CODE, Manifest.permission.READ_CALENDAR) == true)   ||
                    !(checkPermission(CALENDAR_WRITE_CODE, Manifest.permission.WRITE_CALENDAR) == true)){
                return;
            }

            String calenderID = getCalenderID();

            //----get all calendars

            String[] FIELDS = {
                    CalendarContract.Calendars.NAME,                       //0
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,      //1
                    CalendarContract.Calendars.CALENDAR_COLOR,             //2
                    CalendarContract.Calendars.VISIBLE,                    //3
                    CalendarContract.Calendars._ID,                        //4
                    CalendarContract.Calendars.ACCOUNT_NAME,               //5
                    CalendarContract.Calendars.ACCOUNT_TYPE,               //6
                    CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,      //7
                    CalendarContract.Calendars.OWNER_ACCOUNT,              //8
            };
            Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
            Set<String> calendars = new HashSet<String>();
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS, null, null, null);
            boolean calendarExists = false;
            String calendarID = "";
            ArrayList colelciton = new ArrayList();

            /*
            try {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        for(int i = 0; i < FIELDS.length; i++ ){
                            ArrayList line = new ArrayList();
                            line.add(FIELDS[i]);
                            line.add(cursor.getString(i));
                            colelciton.add(line);
                        }
                        String name = cursor.getString(0);
                        String displayName = cursor.getString(1);
                        // This is actually a better pattern:
                        //String color = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR));
                        Boolean selected = !cursor.getString(3).equals("0");
                        calendars.add(displayName);

                        if (displayName.equals(CALENDER_NAME)){
                            //calendarExists = true;
                            calendarID = cursor.getString(4);
                            //String[] args = new String[]{"4"};
                            //contentResolver.delete(CalendarContract.Calendars.CONTENT_URI, "_ID = ?", args);
                        }
                    }
                }
            }
            catch (AssertionError ex) { //TODO: log exception and bail }
            */
            if(calendarExists != true) {
                ContentValues calendarValues = new ContentValues();

                calendarValues.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDER_NAME);
                calendarValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, "LOCAL");
                calendarValues.put(CalendarContract.Calendars.NAME, CALENDER_NAME);
                calendarValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDER_NAME);
                calendarValues.put(CalendarContract.Calendars.CALENDAR_COLOR, "-11749922");
                calendarValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, "700");
                calendarValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDER_NAME);
                calendarValues.put(CalendarContract.Calendars.VISIBLE, "1");
                //calendarValues.put(CalendarContract.Calendars._ID, "3");
                //calendarValues.put(CalendarContract.Calendars.CALENDAR_COLOR_KEY, getResources().getColor(R.color.jerry_blue));
                Uri calendarUri = contentResolver.insert(CalendarContract.Calendars.CONTENT_URI, calendarValues);
                String[] args = new String[]{"3"};
                contentResolver.update(CalendarContract.Calendars.CONTENT_URI, calendarValues, "_ID = ?", args);
                //Uri calendarUri = contentResolver.update( CalendarContract.Calendars.CONTENT_URI, calendarValues);
                //long calenderID = Long.parseLong(calendarUri.getLastPathSegment());
            }

            //----add new event
            ContentResolver cr = this.getContentResolver();
            ContentValues eventValues = new ContentValues();

            Calendar beginTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();

            eventValues.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
            eventValues.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            eventValues.put(CalendarContract.Events.TITLE, "test name");
            eventValues.put(CalendarContract.Events.DESCRIPTION, "test description");
            eventValues.put(CalendarContract.Events.CALENDAR_ID, "1");
            eventValues.put(CalendarContract.Events.EVENT_COLOR, getResources().getColor(R.color.jerry_blue));
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, eventValues);
            long eventID = Long.parseLong(uri.getLastPathSegment());

            String reminderUriString = "content://com.android.calendar/reminders";
            ContentValues reminderValues = new ContentValues();
            reminderValues.put("event_id", eventID);
            reminderValues.put("minutes", 5);
            reminderValues.put("method", 1);
            cr.insert(Uri.parse(reminderUriString), reminderValues);

            Toast.makeText(this, "Event_ID " + eventID, Toast.LENGTH_SHORT).show();

            //----get calendar events
            String[] vec = new String[] { "calendar_id", "title", "description", "dtstart", "dtend", "allDay", "eventLocation" };
            String selectionClause = "calendar_id = ?";   //String selectionClause = "(dtstart >= ? AND dtend <= ?) OR (dtstart >= ? AND allDay = ?)";
            String[] selectionsArgs = new String[]{"1"};  //String[] selectionsArgs = new String[]{"" + dtstart, "" + dtend, "" + dtstart, "1"};
            Cursor crsr = contentResolver.query(Uri.parse("content://com.android.calendar/events"), vec, selectionClause, selectionsArgs, null);
            crsr.moveToFirst();
            String CNames[] = new String[crsr.getCount()];
            for (int i = 0; i < CNames.length; i++) {
                String str1 = crsr.getString(1);
                String str2 = crsr.getString(2);
                String str3 = crsr.getString(3);
                String str4 = crsr.getString(4);
                crsr.moveToNext();
            }
            //CalendarProvider calendarProvider = new CalendarProvider(context);
            //List<Calendar> calendars = calendarProvider.getCalendars().getList();
            /*Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setData( CalendarContract.Events.CONTENT_URI);
            //intent.setData( CalendarContract.Events.DTSTART);
            intent.putExtra(CalendarContract.Events.TITLE, "test title");
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "test location");
            intent.putExtra(CalendarContract.Events.DESCRIPTION, "test descrition");
            intent.putExtra(CalendarContract.Events.ALL_DAY, "true");
            //intent.putExtra(CalendarContract.Events., "true");
            intent.putExtra(Intent.EXTRA_EMAIL, "test1@yahoo.com, test2@yahoo.com, test3@yahoo.com");
            
            if(intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }else{
                Toast.makeText(this, "There is no app that can support this action", Toast.LENGTH_SHORT).show();
            }*/
        });
    }

    private String getCalenderID() {
        //----get all calendars
        String value = null;
        String[] FIELDS = {
                CalendarContract.Calendars.NAME,                       //0
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,      //1
                CalendarContract.Calendars.CALENDAR_COLOR,             //2
                CalendarContract.Calendars.VISIBLE,                    //3
                CalendarContract.Calendars._ID,                        //4
                CalendarContract.Calendars.ACCOUNT_NAME,               //5
                CalendarContract.Calendars.ACCOUNT_TYPE,               //6
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,      //7
                CalendarContract.Calendars.OWNER_ACCOUNT,              //8
        };
        String[] selectionsArgs = new String[]{CALENDER_NAME};
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, FIELDS, "nullNAME = ?", selectionsArgs, null);
        if (cursor.getCount() != 0){
            cursor.moveToNext();
            value = cursor.getString(4);
        }else{
            //---Insert new "Jerry" Calendar
            ContentValues calendarValues = new ContentValues();
            calendarValues.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDER_NAME);
            calendarValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, "LOCAL");
            calendarValues.put(CalendarContract.Calendars.NAME, CALENDER_NAME);
            calendarValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDER_NAME);
            calendarValues.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.parseColor(String.valueOf(getResources().getColor(R.color.jerry_grey))));  //"-11749922"
            calendarValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, "700");
            calendarValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDER_NAME);
            calendarValues.put(CalendarContract.Calendars.VISIBLE, "1");
            //calendarValues.put(CalendarContract.Calendars._ID, "3");
            //calendarValues.put(CalendarContract.Calendars.CALENDAR_COLOR_KEY, getResources().getColor(R.color.jerry_blue));
            Uri calendarUri = contentResolver.insert(CalendarContract.Calendars.CONTENT_URI, calendarValues);

            cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, FIELDS, "nullNAME = ?", selectionsArgs, null);
            cursor.moveToNext();
            value = cursor.getString(4);
        }
        return value;
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //if(){}
        super.onActivityResult(requestCode, resultCode, data);
    }
}