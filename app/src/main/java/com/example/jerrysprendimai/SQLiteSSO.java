package com.example.jerrysprendimai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import androidx.annotation.Nullable;

public class SQLiteSSO extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "jerry.db";
    private static final String TABLE_NAME = "singleSignOn";

    private static final String COLUMN0 = "ID";
    private static final String COLUMN1 = "USER_ID";
    private static final String COLUMN2 = "UNAME";
    private static final String COLUMN3 = "PASSWD";

    public SQLiteSSO(Context context) {
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
                    "USER_ID TEXT, " +
                    "UNAME   TEXT, " +
                    "PASSWD  TEXT)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String userId, String uname, String passwd){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        db.delete(TABLE_NAME, null, null);

        contentValues.put(COLUMN1, userId);
        contentValues.put(COLUMN2, uname);
        contentValues.put(COLUMN3, passwd);

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
                             data.getString(2),
                             data.getString(3)};

        return toReturn;
    }

    public static void compare(Context context, ObjectUser myUser){
        SQLiteSSO dbSSO = new SQLiteSSO(context);

        String[] result = dbSSO.getData();

        String passwdValue = "";
        try {
            passwdValue = new String(Base64.decode(myUser.getPasswd(), 0));
        }catch (Exception e){
            passwdValue = myUser.getPasswd();
        }

        if ((result[0].equals("")) && (result[1].equals("")) && (result[2].equals(""))){
           dbSSO.addData(myUser.getId().toString(), myUser.getUname(), passwdValue);
        }else{
            if(result[0].equals(myUser.getId().toString())){
                if((!result[1].equals(myUser.getUname()))||(!result[2].equals(passwdValue))){
                    dbSSO.addData(myUser.getId().toString(), myUser.getUname(), passwdValue);
                }
            }
        }

        dbSSO.close();
    }
}
