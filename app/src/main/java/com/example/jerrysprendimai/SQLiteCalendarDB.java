package com.example.jerrysprendimai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jerrysprendimai.ObjectObject;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SQLiteCalendarDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "jerryCalendar.db";
    private static final String TABLE_NAME = "calendarEvents";
    private static final String COLUMN0 = "ID";
    private static final String COLUMN1 = "DATE";
    private static final String COLUMN2 = "TITLE";
    private static final String COLUMN3 = "OBJECT_ID";
    private static final String COLUMN4 = "ADDRESS";
    private static final String COLUMN5 = "DSTART";

    Context context;

    public SQLiteCalendarDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;

        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DATE       TEXT, " +
                "TITLE      TEXT, " +
                "OBJECT_ID  TEXT, " +
                "ADDRESS    TEXT, " +
                "DSTART     TEXT) ";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(ObjectObject obj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        db.delete(TABLE_NAME, null, null);
        //deleteData(db, TABLE_NAME);

        contentValues.put(COLUMN1, obj.getDate());            //"DATE"
        contentValues.put(COLUMN2, obj.getObjectName());      //"TITLE"
        contentValues.put(COLUMN3, obj.getId().toString());   //"OBJECT_ID"
        contentValues.put(COLUMN4, obj.getObjectAddress());   //"ADDRESS"
        contentValues.put(COLUMN5, "");                       //"DSTART"

        long result = db.insert(TABLE_NAME, null, contentValues);

        db.close();

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        //db.close();
        return data;
    }

    public void syncCalendarEvents(ArrayList<ObjectObject> myObjectList) {

        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < myObjectList.size(); i++) {
            String[] whereArgs = new String[]{myObjectList.get(i).getId().toString(),
                    myObjectList.get(i).getDate(),
                    myObjectList.get(i).getObjectName(),
                    myObjectList.get(i).getObjectAddress()};
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                    " WHERE OBJECT_ID = ? " +
                    "   AND DATE = ? " +
                    "   AND TITLE = ?" +
                    "   AND ADDRESS = ?", whereArgs);

            if (cursor.getCount() == 0) {
                //----delete existing
                String[] whereArgs2 = new String[]{myObjectList.get(i).getId().toString()};
                db.delete(TABLE_NAME, "OBJECT_ID = ?", whereArgs2);

                Date dateObj = null;
                try {
                    SimpleDateFormat displayDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar beginTime = Calendar.getInstance();
                    dateObj = displayDateFormat.parse(myObjectList.get(i).getDate());
                    beginTime.setTimeInMillis(dateObj.getTime());
                    beginTime.set(Calendar.HOUR_OF_DAY, 0);
                    beginTime.set(Calendar.MINUTE, 0);
                    beginTime.set(Calendar.SECOND, 0);
                    dateObj.setTime(beginTime.getTimeInMillis());
                    //getCalendarEvents(dateObj);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //----create new record
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN1, myObjectList.get(i).getDate());                                                 //"DATE"
                contentValues.put(COLUMN2, myObjectList.get(i).getObjectName() + "      #" + myObjectList.get(i).getId()); //"TITLE"
                contentValues.put(COLUMN3, myObjectList.get(i).getId().toString());                                        //"OBJECT_ID"
                contentValues.put(COLUMN4, myObjectList.get(i).getObjectAddress());                                        //"ADDRESS"
                contentValues.put(COLUMN5, String.valueOf(dateObj.getTime()));                                             //"DSTART"

                long result = db.insert(TABLE_NAME, null, contentValues);

            } else {
                //----record exists
            }

        }
        db.close();
    }

    public ArrayList<ObjectEvent> getCalendarEvents() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<ObjectEvent> returnArray = new ArrayList<>();
        //Calendar calendar = Calendar.getInstance();

        //calendar.setTimeInMillis(dateClicked.getTime());
        //String date = calendar.get(Calendar.YEAR) + "-" + String.format("%02d", (calendar.get(Calendar.MONTH) + 1)) + "-" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        //String[] whereArgs = new String[]{date};
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                //" WHERE DATE = ? " +
                " ORDER BY OBJECT_ID DESC", null);//whereArgs);
        int cursorCount = cursor.getCount();
        cursor.moveToFirst();
        for (int i = 0; i < cursorCount; i++) {
            ObjectEvent objEvent = new ObjectEvent(cursor, 2);
            returnArray.add(objEvent);
            cursor.moveToNext();
        }

        db.close();
        return returnArray;
    }

    public void setMonthEvents(CompactCalendarView calendarView, Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<ObjectEvent> eventArray = getCalendarEvents();
        List<Event> calenderEvents = calendarView.getEventsForMonth(calendar.getTime());
        List<Event> eventsToKeep = new ArrayList<>();
        calendarView.removeAllEvents();
        calendarView.setEventIndicatorStyle(2);
        for (int i = 0; i < eventArray.size(); i++) {
            if ((eventArray.get(i).year == year) && (eventArray.get(i).month == month)) {
                Event newEvent = new Event(context.getResources().getColor(R.color.jerry_blue), eventArray.get(i).getCalendar().getTimeInMillis(), eventArray.get(i).getTitle());
                calendarView.addEvent(newEvent);
            }
        }
        db.close();
    }
    public ArrayList<ObjectEvent> getDayEvents(Date dateClicked){
        ArrayList<ObjectEvent> returnArray = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateClicked.getTime());
        String date = calendar.get(Calendar.YEAR) + "-" + String.format("%02d", (calendar.get(Calendar.MONTH) + 1)) + "-" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        String[] whereArgs = new String[]{date};
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE DATE = ? " +
                " ORDER BY OBJECT_ID DESC", whereArgs);
        int cursorCount = cursor.getCount();
        cursor.moveToFirst();
        for (int i = 0; i < cursorCount; i++) {
            ObjectEvent objEvent = new ObjectEvent(cursor, 2);
            returnArray.add(objEvent);
            cursor.moveToNext();
        }
        db.close();
        return returnArray;
    }
}
