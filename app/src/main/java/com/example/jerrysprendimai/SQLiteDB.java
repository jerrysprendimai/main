package com.example.jerrysprendimai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "juliFliesen.db";
    private static final String TABLE_NAME = "settings";
    private static final String COLUMN0 = "ID";
    private static final String COLUMN1 = "URL";
    private static final String COLUMN2 = "DB_SERVER";
    private static final String COLUMN3 = "DB_NAME";
    private static final String COLUMN4 = "DB_USER";
    private static final String COLUMN5 = "DB_PASSWORD";

    public SQLiteDB(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "URL         TEXT, " +
                "DB_SERVER   TEXT, " +
                "DB_NAME     TEXT, " +
                "DB_USER     TEXT, " +
                "DB_PASSWORD TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String url, String dbServer, String dbName, String dbUser, String dbPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        db.delete(TABLE_NAME, null, null);
        //deleteData(db, TABLE_NAME);

        contentValues.put(COLUMN1, url);
        contentValues.put(COLUMN2, dbServer);
        contentValues.put(COLUMN3, dbName);
        contentValues.put(COLUMN4, dbUser);
        contentValues.put(COLUMN5, dbPassword);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /*
    private void deleteData(SQLiteDatabase db, String tableName) {
        db.execSQL("DELETE * FROM " + tableName);
    }
     */

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        //db.close();
        return data;
    }
}
