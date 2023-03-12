package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.dcastalia.localappupdate.DownloadApk;

public class ActivityMain extends AppCompatActivity {
    static ActivityMain activityMain;
    private final String appUrl = "https://jerry-sprendimai.eu/misc/jerry.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.jerry_version)).setHint("- " + ((TextView) findViewById(R.id.jerry_version)).getHint() + " " + BuildConfig.VERSION_NAME + " -");
        findViewById(R.id.jerry_version).setVisibility(View.VISIBLE);

        ActivityMain.setActivityMain(this);

        //----------reading values from Internal DB
        SQLiteDB dbHelper = new SQLiteDB(this);
        Cursor result = dbHelper.getData();
        //----check if values already exist
        result.moveToFirst();
        String url       = result.getString(1);
        String db_server = result.getString(2);
        String db_name   = result.getString(3);
        String db_user   = result.getString(4);
        String db_passwd = result.getString(5);
        if ((url.equals(""))&&(db_server.equals(""))&&(db_name.equals(""))&&(db_user.equals(""))&&(db_passwd.equals(""))){
            dbHelper.addDataInitial();
        }

        dbHelper.close();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {


            @Override
            public void run() {
                startActivity(new Intent(ActivityMain.this, ActivityLogin.class));
            }
        }, 1500);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public static ActivityMain getActivityMain() {
        return ActivityMain.activityMain;
    }

    public static void setActivityMain(ActivityMain activityMain) {
        ActivityMain.activityMain = activityMain;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}