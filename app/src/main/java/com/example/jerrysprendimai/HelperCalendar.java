package com.example.jerrysprendimai;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HelperCalendar {
    private static final String CALENDAR_NAME = "Jerry";
    private static final String CALENDAR_TYPE = "LOCAL";
    private static String[] FIELDS = new String[] { "_id","calendar_id","dtstart","dtend","title","description","allDay","eventLocation" };

    public Context context;
    public String calenderID;
    public boolean error;
    public String errorMessage;

    public HelperCalendar(Context cntx){
        setError(false);
        setErrorMessage("");
        this.context = cntx;
        this.calenderID = getCalendarID(this.context);
    }

    public boolean isError() {                         return error;    }
    public void setError(boolean error) {              this.error = error;    }
    public String getErrorMessage() {                  return errorMessage;    }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage;    }

    public ArrayList<ObjectEvent> getJerryCalendarEvents(){        //HelperCalendar
        ArrayList<ObjectEvent> eventArray = new ArrayList<>();
        if(isError()){
            Toast.makeText(context, getErrorMessage(), Toast.LENGTH_SHORT).show();
            return eventArray;
        }
            ContentResolver contentResolver = context.getContentResolver();
            String selectionClause = "calendar_id = ?";
            String[] selectionsArgs = new String[]{this.calenderID};
        try {
            Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, FIELDS, selectionClause, selectionsArgs, null);
            int cursorCount = cursor.getCount();
            cursor.moveToFirst();
            for (int i = 0; i < cursorCount; i++) {
                ObjectEvent event = new ObjectEvent(cursor);
                eventArray.add(event);
                cursor.moveToNext();
            }
        }catch (Exception e){
            setError(true);
            setErrorMessage("error @getJerryCalendarEvents");
        }
        return eventArray;
    }                             //HelperCalendar
    /*public void setAllEvents(CompactCalendarView calendarView){
        ArrayList<ObjectEvent> eventArray = getJerryCalendarEvents();
        for(int i = 0; i<eventArray.size(); i++){
            //List<Event> calenderEvents = calendarView.getEventsForMonth(calendar.getTime());
            Event newEvent = new Event(context.getResources().getColor(R.color.jerry_blue), eventArray.get(i).getCalendar().getTimeInMillis(), eventArray.get(i).getTitle());
            //if(!calenderEvents.contains(newEvent)){
            calendarView.addEvent(newEvent);
            //}
        }
    }*/
    public ArrayList<ObjectEvent> getDayEvents(Date dateClicked){
        ArrayList<ObjectEvent> returnArray = new ArrayList<>();
        if (isError()){
            Toast.makeText(context, getErrorMessage(), Toast.LENGTH_SHORT).show();
            return returnArray;
        }
        try {
            ArrayList<ObjectEvent> eventArray = getJerryCalendarEvents();
            for (int i = 0; i < eventArray.size(); i++) {
                Date date = eventArray.get(i).getDate();
                if (eventArray.get(i).getDate().equals(dateClicked)) {
                    returnArray.add(eventArray.get(i));
                }
            }
        }catch (Exception e){
            setError(true);
            setErrorMessage("error @getDayEvents");
        }
        return returnArray;
    }                       //ActivityCalendar + HttpsRequestGetObjectList
    public void setMonthEvents(CompactCalendarView calendarView, Calendar calendar){
        try {
            if (isError()){
                Toast.makeText(context, getErrorMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            ArrayList<ObjectEvent> eventArray = getJerryCalendarEvents();
            List<Event> calenderEvents = calendarView.getEventsForMonth(calendar.getTime());
            List<Event> eventsToKeep = new ArrayList<>();
            calendarView.removeAllEvents();
            calendarView.setEventIndicatorStyle(2);
            for (int i = 0; i < eventArray.size(); i++) {
                if ((eventArray.get(i).year == year) && (eventArray.get(i).month == month)) {
                    Event newEvent = new Event(context.getResources().getColor(R.color.jerry_blue), eventArray.get(i).getCalendar().getTimeInMillis(), eventArray.get(i).getTitle());
                    calendarView.addEvent(newEvent);
                    //eventsToKeep.add(newEvent);
                    //if(!calenderEvents.contains(newEvent)){
                    //    calendarView.addEvent(newEvent);
                    //}
                }
            }
        }catch (Exception e){
            setError(false);
            setErrorMessage("error @setMonthEvents");
        }
    }    //ActivityCalendar + HttpsRequestGetObjectList
    public void syncJerryCalenderEvents(ArrayList<ObjectObject> myObjectList){
        try {
            if (isError()){
                Toast.makeText(context, getErrorMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
            ContentResolver contentResolver = context.getContentResolver();
            //----deleting events
            String selectionClause = "calendar_id = ?";
            String[] selectionsArgs = new String[]{this.calenderID};
            Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, FIELDS, selectionClause, selectionsArgs, null);
            int cursorCount = cursor.getCount();
            cursor.moveToFirst();
            //String str1 = cursor.getString(1);
            //String str2 = cursor.getString(2);

            for (int i = 0; i < cursorCount; i++) {
                String eventID = cursor.getString(0);  //_id
                //String clendarID   = cursor.getString(1);  //calendar_id
                String dstart = cursor.getString(2);  //dstart
                String dtend = cursor.getString(3);  //dtend
                String title = cursor.getString(4);  //title
                String description = cursor.getString(5);  //description

                SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(dstart));
                String eventDate = displayDateFormat.format(calendar.getTime());

                //------find cooresponding object
                ObjectObject myObject = null;
                for (int j = 0; j < myObjectList.size(); j++) {
                    if (myObjectList.get(j).getId().toString().equals(eventID)) {
                        myObject = myObjectList.get(j);
                        break;
                    }
                }
                if (myObject == null) {
                    contentResolver.delete(HelperCalendar.asSyncAdapter(CalendarContract.Events.CONTENT_URI), "_ID = ? AND CALENDAR_ID = ?", new String[]{eventID, this.calenderID});
                    //contentResolver.delete(CalendarContract.Events.CONTENT_URI, "_ID = ? AND CALENDAR_ID = ?", new String[]{eventID, this.calenderID});
                } else {
                    //dstart
                    //dtend
                    String expectedEventName = //myObject.getObjectName()   +"\n"+
                            //myObject.getCustomerName() +"\n"+
                            myObject.getObjectAddress();
                    String name = myObject.getObjectName() + "  #" + myObject.getId();
                    if (!title.equals(myObject.getObjectName() + "  #" + myObject.getId())) {
                        contentResolver.delete(CalendarContract.Reminders.CONTENT_URI, "EVENT_ID = ?", new String[]{eventID});
                        contentResolver.delete(HelperCalendar.asSyncAdapter(CalendarContract.Events.CONTENT_URI), "_ID = ? AND CALENDAR_ID = ?", new String[]{eventID, this.calenderID});
                        cursor.moveToNext();
                        continue;
                    }
                    if (!description.equals(expectedEventName)) {
                        contentResolver.delete(CalendarContract.Reminders.CONTENT_URI, "EVENT_ID = ?", new String[]{eventID});
                        contentResolver.delete(HelperCalendar.asSyncAdapter(CalendarContract.Events.CONTENT_URI), "_ID = ? AND CALENDAR_ID = ?", new String[]{eventID, this.calenderID});
                        cursor.moveToNext();
                        continue;
                    }
                    String date = myObject.getDate();
                    if (!eventDate.equals(myObject.getDate())) {
                        contentResolver.delete(CalendarContract.Reminders.CONTENT_URI, "EVENT_ID = ?", new String[]{eventID});
                        contentResolver.delete(HelperCalendar.asSyncAdapter(CalendarContract.Events.CONTENT_URI), "_ID = ? AND CALENDAR_ID = ?", new String[]{eventID, this.calenderID});
                        cursor.moveToNext();
                        continue;
                    }
                }
                cursor.moveToNext();
            }

            //----adding events
            selectionClause = "CALENDAR_ID = ? AND TITLE = ?";
            for (int i = 0; i < myObjectList.size(); i++) {
                String[] selectionsArgs2 = new String[]{this.calenderID, myObjectList.get(i).getObjectName()};
                Cursor cursor2 = contentResolver.query(CalendarContract.Events.CONTENT_URI, FIELDS, selectionClause, selectionsArgs2, null);
                int cusrsorCount = cursor2.getCount();

                if (cursor2.getCount() == 0) {
                    //----create new event
                    ContentValues eventValues = new ContentValues();
                    Calendar beginTime = Calendar.getInstance();
                    Calendar endTime = Calendar.getInstance();

                    SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    //Calendar calendar = Calendar.getInstance();
                    //calendar.setTimeInMillis(Long.parseLong(dstart));
                    //String eventDate = displayDateFormat.format(calendar.getTime());

                    try {
                        Date dateObj = displayDateFormat.parse(myObjectList.get(i).getDate());
                        beginTime.setTimeInMillis(dateObj.getTime());
                        beginTime.set(Calendar.HOUR_OF_DAY, 0);
                        beginTime.set(Calendar.MINUTE, 0);
                        beginTime.set(Calendar.SECOND, 0);
                        dateObj.setTime(beginTime.getTimeInMillis());
                        endTime = beginTime;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    eventValues.put(CalendarContract.Events._ID, myObjectList.get(i).getId().toString());
                    eventValues.put(CalendarContract.Events.CALENDAR_ID, this.calenderID);
                    eventValues.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                    eventValues.put(CalendarContract.Events.ALL_DAY, true);
                    eventValues.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
                    eventValues.put(CalendarContract.Events.TITLE, myObjectList.get(i).getObjectName() + "  #" + String.valueOf(myObjectList.get(i).getId()));
                    eventValues.put(CalendarContract.Events.DESCRIPTION, //myObjectList.get(i).getObjectName()   +"\n"+
                            //myObjectList.get(i).getCustomerName() +"\n"+
                            myObjectList.get(i).getObjectAddress());
                    eventValues.put(CalendarContract.Events.EVENT_COLOR, context.getResources().getColor(R.color.jerry_blue));
                    eventValues.put(CalendarContract.Events.HAS_ALARM, 1);
                   // eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                    Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, eventValues);
                    //long eventID = Long.parseLong(uri.getLastPathSegment());

                    //----create Remainder
                    String[] remainderFields = new String[]{"event_id", "minutes", "method"};
                    Cursor remainderCursor = contentResolver.query(CalendarContract.Reminders.CONTENT_URI, remainderFields, "event_id = ?", new String[]{myObjectList.get(i).getId().toString()}, null);
                    if (remainderCursor.getCount() == 0) {
                        ContentValues reminderValues = new ContentValues();
                        reminderValues.put("event_id", myObjectList.get(i).getId().toString());
                        reminderValues.put("minutes", 720);
                        reminderValues.put("method", 1);
                        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
                    }
                } else {
                    cursor2.moveToFirst();
                    String str0 = cursor2.getString(0);
                    String str1 = cursor2.getString(1);
                    String str2 = cursor2.getString(2);
                    String str3 = cursor2.getString(3);
                    String str4 = cursor2.getString(4);
                    String str5 = cursor2.getString(5);
                }

            }
        }catch (Exception e){
            setError(false);
            setErrorMessage("error @syncJerryCalenderEvents");
        }

    }          //ActivityCalendar.HttpsRequestGetObjectList + ActivityMenu.HttpsRequestGetObjectList
    static Uri asSyncAdapter(Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter(android.provider.CalendarContract.CALLER_IS_SYNCADAPTER,"true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDAR_TYPE).build();
    }                                                 //HelperCalendar
    public String getCalendarID(Context context){
        String value = null;
        if(isError()){
            Toast.makeText(context, getErrorMessage(), Toast.LENGTH_SHORT).show();
            return value;
        }
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
        String[] selectionsArgs = new String[]{CALENDAR_NAME};
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, FIELDS, "NAME = ?", selectionsArgs, null);
            if (cursor.getCount() != 0) {
                cursor.moveToNext();
                value = cursor.getString(4);
            } else {
                //---Insert new "Jerry" Calendar
                ContentValues calendarValues = new ContentValues();
                calendarValues.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDAR_NAME);
                calendarValues.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDAR_TYPE);
                calendarValues.put(CalendarContract.Calendars.NAME, CALENDAR_NAME);
                calendarValues.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_NAME);
                calendarValues.put(CalendarContract.Calendars.CALENDAR_COLOR, "-11749922");  //"-11749922"  //"Color.parseColor(String.valueOf(context.getResources().getColor(R.color.jerry_grey)))
                calendarValues.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, "700");
                calendarValues.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDAR_NAME);
                calendarValues.put(CalendarContract.Calendars.VISIBLE, "1");
                //calendarValues.put(CalendarContract.Calendars._ID, "3");
                //calendarValues.put(CalendarContract.Calendars.CALENDAR_COLOR_KEY, getResources().getColor(R.color.jerry_blue));
                Uri calendarUri = contentResolver.insert(CalendarContract.Calendars.CONTENT_URI, calendarValues);

                cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, FIELDS, "NAME = ?", selectionsArgs, null);
                cursor.moveToNext();
                value = cursor.getString(4);
            }
        }catch (Exception e){
            setError(true);
            setErrorMessage("error @getCalendarID");
        }
        return value;
    }                                       //HelperCalendar
}
