package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityDealerShow extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    RecyclerView recyclerView;
    ObjectUser myUser;

    MyAdapterDealerShow myAdapterDealerShow;
    ArrayList<ObjectDealer> myDealerList;
    ArrayList<ObjectDealer> myDealerListOriginal;

    Boolean deletionMode;
    ArrayList toBeDeletedList;

    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton buttonAddDealer;
    FloatingActionButton buttonDeleteDealer;

    JSONObject jsonObjectToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_show);

        //----------------SwipeRefreschLayout
        swipeRefreshLayout = findViewById(R.id.my_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        swipeRefreshLayout.setRefreshing(true);

        this.deletionMode = false;
        this.toBeDeletedList = new ArrayList<Integer>();

        //----------------Button Add Dealer
        Context context = this;
        buttonAddDealer = findViewById(R.id.button_dealer_add_new);
        buttonAddDealer.setOnClickListener(v->{
            swipeRefreshLayout.setRefreshing(true);
            Intent intent = new Intent(context, ActivityDealerEdit.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });

        //----------------Button Delete Dealer
        buttonDeleteDealer = findViewById(R.id.button_dealer_delete);
        buttonDeleteDealer.setOnClickListener(v->{
            JSONArray jsonObjectArray = new JSONArray();
            JSONObject jsonObjectWA = new JSONObject();
            JSONObject jsonObjectToSend = new JSONObject();
            for(int i=0; i<toBeDeletedList.size(); i++){
                jsonObjectWA = new JSONObject();
                try {
                    jsonObjectWA.put("id", myDealerList.get(Integer.parseInt(toBeDeletedList.get(i).toString())).getId().toString());
                    jsonObjectArray.put(jsonObjectWA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                jsonObjectToSend.put("toDelete", jsonObjectArray);
                setJsonObjectToDelete(jsonObjectToSend);
                new HttpsRequestDealerDelete(context).execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        this.myUser = getIntent().getParcelableExtra("myUser");
        this.myDealerList = new ArrayList<ObjectDealer>();
        this.myDealerListOriginal = new ArrayList<ObjectDealer>();

        //---------------------Recycle View-------------------------------
        buildRecyclerView();

        //new HttpsRequestGetDealerList(this).execute();

    }
    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.my_recycle_view);
        this.myAdapterDealerShow = new MyAdapterDealerShow(this, this.myDealerList, this.myDealerListOriginal, "dealerShow", null, false, myUser);
        this.recyclerView.setAdapter(myAdapterDealerShow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRefresh() {
        this.swipeRefreshLayout.setRefreshing(false);
        new HttpsRequestGetDealerList(this).execute();
        this.setDeletionMode(false);
        this.setButtonDeleteDealer(false);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
    }
    public void setButtonDeleteDealer(boolean value) {
        if(value){
            buttonAddDealer.setVisibility(View.GONE);
            buttonDeleteDealer.setVisibility(View.VISIBLE);
        }else{
            buttonAddDealer.setVisibility(View.VISIBLE);
            buttonDeleteDealer.setVisibility(View.GONE);
        }
    }
    public void lockView() {
      for(ObjectDealer objectDealer: myDealerList){
          try {
              objectDealer.getMyViewHolderUserShow().myRow.setEnabled(false);
          }catch (Exception e){

          }
      }
    }
    public void unlockView(){
       for(ObjectDealer objectDealer: myDealerList){
           try {
               objectDealer.getMyViewHolderUserShow().myRow.setEnabled(true);
           }catch (Exception e){

           }
       }
    }

    public void addToBeDeleted(int position) {
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
            this.setButtonDeleteDealer(false);
            myAdapterDealerShow.notifyDataSetChanged();
        }
    }


    public Boolean isDeletionMode() {        return deletionMode;    }
    public void setDeletionMode(Boolean deletionMode) {        this.deletionMode = deletionMode;    }
    public JSONObject getJsonObjectToDelete() {        return jsonObjectToDelete;    }
    public void setJsonObjectToDelete(JSONObject jsonObjectToDelete) {   this.jsonObjectToDelete = jsonObjectToDelete;    }

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
    class HttpsRequestGetDealerList extends AsyncTask<String, Void, InputStream> {
        private static final String get_dealer_list_url = "get_dealer_list.php";

        private Context context;
        Connector connector;

        public HttpsRequestGetDealerList(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_dealer_list_url);
            //connector.addPostParameter("user_type", MCrypt2.encodeToString(myUser.getType()));
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

            ArrayList<ObjectDealer> dealerArrayList = getDealerList(connector);
            ((ActivityDealerShow) context).myDealerList.removeAll(((ActivityDealerShow) context).myDealerListOriginal);
            ((ActivityDealerShow) context).myDealerList.addAll(dealerArrayList);
            ((ActivityDealerShow) context).myDealerListOriginal = new ArrayList<ObjectDealer>();
            ((ActivityDealerShow) context).myDealerListOriginal.addAll(((ActivityDealerShow) context).myDealerList);
            ((ActivityDealerShow) context).myAdapterDealerShow.notifyDataSetChanged();
            ((ActivityDealerShow) context).swipeRefreshLayout.setRefreshing(false);

            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectDealer>getDealerList(Connector conn){
            ArrayList<ObjectDealer> dealerArrayList = new ArrayList<>();
            try{
                ObjectDealer objectDealer;
                JSONArray responseObjects1 = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects1.length(); i++) {
                    objectDealer = new ObjectDealer((JSONObject) responseObjects1.get(i));

                    dealerArrayList.add(objectDealer);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return dealerArrayList;
        }
    }
    class HttpsRequestDealerDelete extends AsyncTask<String, Void, InputStream> {
        private static final String delete_dealer_url   = "delete_dealer.php";

        private Context context;
        Connector connector;

        public HttpsRequestDealerDelete(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            connector = new Connector(context, delete_dealer_url);
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
                connector.decodeResponse();
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
}