package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
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
import java.util.ArrayList;

public class ActivityUserShow extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    RecyclerView recyclerView;
    ObjectUser myUser;
    ArrayList<ObjectUser> myUserList;
    ArrayList<ObjectUser> myUserListOriginal;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton buttonAddUser;
    FloatingActionButton buttonDeleteUser;

    MyAdapterUserShow myAdapterUserShow;
    HttpsRequestGetUserList requestHandler;
    Boolean deletionMode;
    ArrayList toBeDeletedList;

    JSONObject jsonObjectToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show);

        //----------------SwipeRefreschLayout
        swipeRefreshLayout = findViewById(R.id.my_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        swipeRefreshLayout.setRefreshing(true);

        this.deletionMode = false;
        this.toBeDeletedList = new ArrayList<Integer>();

        //----------------Button Add User
        Context context = this;
        buttonAddUser = findViewById(R.id.button_user_add_new);
        buttonAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);
                Intent intent = new Intent(context, ActivityUserEdit.class);
                intent.putExtra("myUser", myUser);
                context.startActivity(intent);
            }
        });

        //----------------Button Delete User
        buttonDeleteUser = findViewById(R.id.button_user_delete);
        buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray jsonObjectArray = new JSONArray();
                JSONObject jsonObjectWA = new JSONObject();
                JSONObject jsonObjectToSend = new JSONObject();
                for(int i=0; i<toBeDeletedList.size(); i++){
                    jsonObjectWA = new JSONObject();
                    try {
                        jsonObjectWA.put("id", myUserList.get(Integer.parseInt(toBeDeletedList.get(i).toString())).getId().toString());
                        jsonObjectArray.put(jsonObjectWA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jsonObjectToSend.put("toDelete", jsonObjectArray);
                    ((ActivityUserShow) context).setJsonObjectToDelete(jsonObjectToSend);
                    new ActivityUserShow.HttpsRequestDelete(context).execute();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        this.myUser = getIntent().getParcelableExtra("myUser");

        this.myUserList = new ArrayList<ObjectUser>();
        this.myUserListOriginal = new ArrayList<ObjectUser>();
        //---------------------Recycle View-------------------------------
        buildRecyclerView();

        new HttpsRequestGetUserList(this).execute();

    }

    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.my_recycle_view);
        this.myAdapterUserShow = new MyAdapterUserShow(this, findViewById(R.id.userShow_main_containerView), this.myUserList);
        this.recyclerView.setAdapter(myAdapterUserShow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public JSONObject getJsonObjectToDelete() {
        return jsonObjectToDelete;
    }

    public void setJsonObjectToDelete(JSONObject jsonObjectToDelete) {
        this.jsonObjectToDelete = jsonObjectToDelete;
    }

    public Boolean getDeletionMode() {
        return deletionMode;
    }

    public void setDeletionMode(Boolean deletionMode) {
        this.deletionMode = deletionMode;
    }
    public void setButtonDeleteUser(){
        this.buttonDeleteUser.setVisibility(View.VISIBLE);
        this.buttonAddUser.setVisibility(View.GONE);
    }
    public void setButtonAddUser(){
        this.buttonDeleteUser.setVisibility(View.GONE);
        this.buttonAddUser.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRefresh() {
      swipeRefreshLayout.setRefreshing(true);
      new HttpsRequestGetUserList(this).execute();
      this.setDeletionMode(false);
      setButtonAddUser();
    }

    @Override
    protected void onResume() {
        //swipeRefreshLayout.setRefreshing(false);
        onRefresh();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(this.getDeletionMode().equals(true)){
            this.setDeletionMode(false);
            this.setButtonAddUser();
            myAdapterUserShow.notifyDataSetChanged();
        }else{
            super.onBackPressed();
        }

    }
    public void addToBeDeleted(int position){
        boolean found = false;
        for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(position)){
                found = true;
                break;
            }
        }
        if(!found){
            this.toBeDeletedList.add(position);
        }
    }
    public void removeToBeDeleted(int position){
        for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(position)){
                this.toBeDeletedList.remove(i);
                break;
            }
        }
        if(this.toBeDeletedList.size() == 0){
            this.setDeletionMode(false);
            this.setButtonAddUser();
            myAdapterUserShow.notifyDataSetChanged();
        }
    }
    public void clearToBeDeleteList(){
        this.toBeDeletedList = new ArrayList<Integer>();
    }

    @Override
    public void onClick(View v) {

    }

    class HttpsRequestDelete extends AsyncTask<String, Void, InputStream> {
        private static final String delete_user_url   = "delete_user.php";

        private Context context;
        Connector connector;

        public HttpsRequestDelete(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            connector = new Connector(context, delete_user_url);
            connector.addPostParameter("toDelete", MCrypt2.encodeToString(getJsonObjectToDelete().toString()));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            try{
                connector.clearResponse();
                JSONObject object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));
                String login_status = object.getString("status");
                if (login_status.equals("1")) {
                    Toast.makeText(this.context, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this.context, getResources().getString(R.string.issue), Toast.LENGTH_SHORT).show();
                }
                onRefresh();
            }catch (Exception e){

            }
            super.onPostExecute(inputStream);
        }
    }
    class HttpsRequestGetUserList extends AsyncTask<String, Void, InputStream> {
        private static final String get_user_list_url = "get_user_list.php";

        private Context context;
        Connector connector;

        public HttpsRequestGetUserList(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

                connector = new Connector(context, get_user_list_url);
                connector.addPostParameter("user_type", MCrypt2.encodeToString(myUser.getType()));
                connector.send();
                connector.receive();
                connector.disconnect();
                String result = connector.getResult();
                result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
               connector.clearResponse();
               ArrayList<ObjectUser> userArryList = getUserList(connector);
               ((ActivityUserShow) context).myUserList.removeAll(((ActivityUserShow) context).myUserListOriginal);
               ((ActivityUserShow) context).myUserList.addAll(userArryList);
               ((ActivityUserShow) context).myUserListOriginal = new ArrayList<ObjectUser>();
               ((ActivityUserShow) context).myUserListOriginal.addAll(((ActivityUserShow) context).myUserList);
               ((ActivityUserShow) context).myAdapterUserShow.notifyDataSetChanged();
               ((ActivityUserShow) context).swipeRefreshLayout.setRefreshing(false);

            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectUser>getUserList(Connector conn){
            ArrayList<ObjectUser> userArrayList = new ArrayList<>();
            try{
                ObjectUser objectUser;
                JSONArray responseObjects1 = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects1.length(); i++) {
                    objectUser = new ObjectUser((JSONObject) responseObjects1.get(i));

                    switch (objectUser.getType()){
                        case "1":
                            objectUser.setUser_lv(getResources().getString(R.string.admin));
                            break;
                        case "2":
                            objectUser.setUser_lv(getResources().getString(R.string.owner));
                            break;
                        case "3":
                            objectUser.setUser_lv(getResources().getString(R.string.employee));
                            break;
                    }
                    userArrayList.add(objectUser);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return userArrayList;
        }
    }
}