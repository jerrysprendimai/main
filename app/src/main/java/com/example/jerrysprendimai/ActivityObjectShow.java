package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ActivityObjectShow extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    final String USER = "user";
    RecyclerView recyclerView;
    ObjectUser myUser;
    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton buttonAddObject;
    MyAdapterObjectShow myAdapterObjectShow;
    boolean userMode, animationShowed, notUserRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_show);


        //----------------SwipeRefreschLayout
        swipeRefreshLayout = findViewById(R.id.my_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        swipeRefreshLayout.setRefreshing(true);

        this.myUser = getIntent().getParcelableExtra("myUser");

        this.myObjectList = new ArrayList<ObjectObject>();
        this.myObjectListOriginal = new ArrayList<ObjectObject>();
        //---------------------Recycle View-------------------------------
        buildRecyclerView();

        //----------------button Add Object
        buttonAddObject = findViewById(R.id.button_object_add_new);
        buttonAddObject.setOnClickListener(v->{
            ObjectObject objectObject = new ObjectObject();

            Field[] drawableFields = R.drawable.class.getFields();
            //ArrayList<Drawable> drawableIcons = new ArrayList<>();
            List<String> svgIcons = new ArrayList<>();
            for(Field field : drawableFields){
                //String tmp = field.getName();
                if(field.getName().contains("svg_")){
                    try {
                        svgIcons.add(field.getName());
                        //drawableIcons.add(getResources().getDrawable(field.getInt(null)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            int random = new Random().nextInt(svgIcons.size());
            objectObject.setIcon(svgIcons.get(random));
            //objectObject.setIcon("ic_object_white");

            ArrayList<ObjectObjDetails> objDetailsArrayList = new ArrayList<>();
            ArrayList<ObjectUser> objectUserArrayList       = new ArrayList<>();
            ArrayList<ObjectObjPic> objectObjPicArrayList   = new ArrayList<>();
            Intent intent = new Intent(this, ActivityObjectEdit.class);
            intent.putExtra("myUser", myUser);
            intent.putExtra("objectObject", objectObject);
            intent.putParcelableArrayListExtra("listDetails",  objDetailsArrayList);
            intent.putParcelableArrayListExtra("listtUser",    objectUserArrayList);
            intent.putParcelableArrayListExtra("listPictures", objectObjPicArrayList);
            startActivity(intent);
        });

        //---- user mode handling
        if(myUser.getUser_lv().equals(USER)){
            setUserMode(true);
            buttonAddObject.setVisibility(View.GONE);
        }else{
            setUserMode(false);
            buttonAddObject.setVisibility(View.VISIBLE);
        }
        //new HttpsRequestGetObjectList(this).execute();
    }

    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.my_recycle_view);
        this.myAdapterObjectShow = new MyAdapterObjectShow(this, findViewById(R.id.userShow_main_containerView), this.myObjectList, this.myUser);
        this.recyclerView.setAdapter(myAdapterObjectShow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void lockView(){
        for(int i=0; i<myObjectList.size(); i++){
            try {
                myObjectList.get(i).getMyViewHolder().myRow.setEnabled(false);
            }catch (Exception e){

            }
        }
    }

    public void unlockView(){
        for(int i=0; i<myObjectList.size(); i++){
            try {
                myObjectList.get(i).getMyViewHolder().myRow.setEnabled(true);
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onRefresh() {
        setAnimationShowed(false);
        if(isNotUserRefresh()) {
            setAnimationShowed(true);
            setNotUserRefresh(false);
        }
        if(myAdapterObjectShow.bottomSheetDialog != null){
            if(myAdapterObjectShow.bottomSheetDialog.isShowing()){
                myAdapterObjectShow.requestObjectDetails(this);
            }
        }
        swipeRefreshLayout.setRefreshing(true);
        new HttpsRequestGetObjectList(this).execute();
    }

    @Override
    protected void onResume() {
        new HttpsRequestCheckSessionAlive(this).execute();
        //onRefresh();
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
            ((ActivityObjectShow) context).myObjectList.removeAll(((ActivityObjectShow) context).myObjectListOriginal);
            ((ActivityObjectShow) context).myObjectList.addAll(objectArryList);
            ((ActivityObjectShow) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityObjectShow) context).myObjectListOriginal.addAll(((ActivityObjectShow) context).myObjectList);
            ((ActivityObjectShow) context).myAdapterObjectShow.notifyDataSetChanged();
            ((ActivityObjectShow) context).swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public void onBackPressed() {
        setAnimationShowed(false);
        super.onBackPressed();
    }

    public boolean isUserMode() {
        return userMode;
    }
    public void setUserMode(boolean userMode) {
        this.userMode = userMode;
    }
    public boolean isAnimationShowed() {        return animationShowed;    }
    public void setAnimationShowed(boolean animationShowed) {        this.animationShowed = animationShowed;    }
    public boolean isNotUserRefresh() {        return notUserRefresh;    }
    public void setNotUserRefresh(boolean notUserRefresh) {        this.notUserRefresh = notUserRefresh;    }

}