package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityChatShow extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    ObjectUser myUser;

    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;
    RecyclerView recyclerView;
    MyAdapterChatShow myAdapterChatShow;
    ArrayList<ValueEventListener> myChatEventListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_show);

        //-----bind screen values
        swipeRefreshLayout = findViewById(R.id.my_swipe_refresh);
        recyclerView       = findViewById(R.id.my_recycle_view);

        //-----Read intent value
        this.myUser       = getIntent().getParcelableExtra("myUser");
        this.myObjectList = getIntent().getParcelableArrayListExtra("myObjectList");

        myChatEventListeners = new ArrayList<>();
        myObjectListOriginal = new ArrayList<>();
        myObjectListOriginal.addAll(myObjectList);

        //-----Listeners
        swipeRefreshLayout.setOnRefreshListener(this);

        //----Initialize values
        //myObjectList = new ArrayList<>();
        //myObjectListOriginal = new ArrayList<>();
        myAdapterChatShow = new MyAdapterChatShow(this, myObjectList, myUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapterChatShow);

    }

    public ArrayList<ValueEventListener> getMyChatEventListeners() {        return myChatEventListeners;    }
    public void setMyChatEventListeners(ArrayList<ValueEventListener> myChatEventListeners) {        this.myChatEventListeners = myChatEventListeners;    }

    public void removeMessageListeners(){
        for(int i=0; i<this.myObjectList.size(); i++){
            FirebaseDatabase.getInstance().getReference("objects/" + this.myObjectList.get(i).getId().toString()).removeEventListener(this.myChatEventListeners.get(i));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        new HttpsRequestCheckSessionAlive(this).execute();
    }

    @Override
    public void onBackPressed() {
        removeMessageListeners();
        myChatEventListeners.clear();
        super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new HttpsRequestGetObjectList(this).execute();
    }

    public void lockView(){
        for(int i = 0; i < myObjectList.size(); i++){
          try {
            myObjectList.get(i).getMyViewHolderChatShow().myRow.setEnabled(false);
          }catch (Exception e){

          }
        }
    }
    public void unlockView(){
        for(int i = 0; i < myObjectList.size(); i++){
            try {
                myObjectList.get(i).getMyViewHolderChatShow().myRow.setEnabled(true);
            }catch (Exception e){

            }
        }
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
            if(myObjectList == null){
                myObjectList = new ArrayList<>();
                myObjectListOriginal = new ArrayList<>();
            }
            ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityChatShow) context).myObjectList.removeAll(((ActivityChatShow) context).myObjectListOriginal);
            ((ActivityChatShow) context).myObjectList.addAll(objectArryList);
            ((ActivityChatShow) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityChatShow) context).myObjectListOriginal.addAll(((ActivityChatShow) context).myObjectList);
            ((ActivityChatShow) context).myAdapterChatShow.notifyDataSetChanged();
            ((ActivityChatShow) context).swipeRefreshLayout.setRefreshing(false);
            //setAnimationShowed(true);
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
                    //---here actions than should continue if session still valid
                    //setAnimationShowed(true);
                    onRefresh();
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
}