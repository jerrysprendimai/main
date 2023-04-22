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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
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
            //--Todo
        });

        this.myUser = getIntent().getParcelableExtra("myUser");
        this.myDealerList = new ArrayList<ObjectDealer>();
        this.myDealerListOriginal = new ArrayList<ObjectDealer>();

        //---------------------Recycle View-------------------------------
        buildRecyclerView();

        new HttpsRequestGetDealerList(this).execute();

    }
    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.my_recycle_view);
        this.myAdapterDealerShow = new MyAdapterDealerShow(this, this.myDealerList, this.myDealerListOriginal, "dealerShow", null);
        this.recyclerView.setAdapter(myAdapterDealerShow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onResume() {
        this.swipeRefreshLayout.setRefreshing(false);
        super.onResume();
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
}