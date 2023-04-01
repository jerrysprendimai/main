package com.example.jerrysprendimai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.util.Calendar;

public class SQLiteSound extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jerry.db";
    private static final String TABLE_NAME = "notification";

    private static final String COLUMN0 = "ID";
    private static final String COLUMN1 = "NOTIFICATION_TIMESTAMP";
    private static final String COLUMN2 = "NOTIFICATION_TYPE";

    public SQLiteSound(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
        //onUpgrade(db,0,1);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                  " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "NOTIFICATION_TIMESTAMP TEXT, " +
                    "NOTIFICATION_TYPE  TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String mills, String type){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        db.delete(TABLE_NAME, null, null);

        contentValues.put(COLUMN1, mills);
        contentValues.put(COLUMN2, type);


        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public String[] getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = null;
        try {
            data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        }catch (Exception e){
            return new String[]{"","",""};
        }

        if(data.getCount() == 0){
            return new String[]{"","",""};
        }

        data.moveToFirst();
        String[] toReturn = {data.getString(1),
                             data.getString(2)};

        return toReturn;
    }

    public static boolean compare(Context context, String type){
        boolean value = true;
        SQLiteSound dbSound = new SQLiteSound(context);
        String[] result = dbSound.getData();


        if ((!result[0].equals(""))&&(!result[1].equals(""))){
            //---check if differrence is more than 1min
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(result[0]));
            long diff = Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis();
            if(( diff <= 30000 )){
                value = false;
            }else{
                dbSound.addData(String.valueOf(Calendar.getInstance().getTimeInMillis()), type);
                value = true;
            }
        }else{
            //----save current time stamp in mills
            dbSound.addData(String.valueOf(Calendar.getInstance().getTimeInMillis()), type);
            value = true;
        }

        dbSound.close();

        return value;
    }
}
