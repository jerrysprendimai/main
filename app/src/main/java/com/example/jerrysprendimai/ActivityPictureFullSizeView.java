package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityPictureFullSizeView extends AppCompatActivity {

    ViewPager viewPager;
    int position;
    ArrayList<ObjectObjPic> myPictureList;
    ObjectUser myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        Intent intent = getIntent();
        myUser        = intent.getParcelableExtra("myUser");
        myPictureList = intent.getParcelableArrayListExtra("myPictureList");
        position      = intent.getIntExtra("myPosition", 0);

        viewPager = findViewById(R.id.pictureView_viewPager);

        MyAdapterPictureFullSizeView fullsizeAdapter = new MyAdapterPictureFullSizeView(this, myPictureList);
        viewPager.setAdapter(fullsizeAdapter);
        viewPager.setCurrentItem(position,true);
    }

    @Override
    protected void onResume() {
        new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
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
                    //---here actions than should continue if session still valid

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