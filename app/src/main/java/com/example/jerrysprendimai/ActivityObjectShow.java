package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityObjectShow extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    RecyclerView recyclerView;
    ObjectUser myUser;
    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;
    SwipeRefreshLayout swipeRefreshLayout;

    MyAdapterObjectShow myAdapterObjectShow;

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

        new ActivityObjectShow.HttpsRequestGetObjectList(this).execute();
    }

    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.my_recycle_view);
        this.myAdapterObjectShow = new MyAdapterObjectShow(this, findViewById(R.id.userShow_main_containerView), this.myObjectList);
        this.recyclerView.setAdapter(myAdapterObjectShow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        new ActivityObjectShow.HttpsRequestGetObjectList(this).execute();
    }

    @Override
    protected void onResume() {
        onRefresh();
        super.onResume();
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
            connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(myUser.getUname().getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityObjectShow) context).myObjectList.removeAll(((ActivityObjectShow) context).myObjectListOriginal);
            ((ActivityObjectShow) context).myObjectList.addAll(objectArryList);
            ((ActivityObjectShow) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityObjectShow) context).myObjectListOriginal.addAll(((ActivityObjectShow) context).myObjectList);
            ((ActivityObjectShow) context).myAdapterObjectShow.notifyDataSetChanged();
            ((ActivityObjectShow) context).swipeRefreshLayout.setRefreshing(false);
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
}