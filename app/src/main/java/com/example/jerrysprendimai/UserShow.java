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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class UserShow extends AppCompatActivity {

    RecyclerView recyclerView;
    ObjectUser myUser;
    ArrayList<ObjectUser> myUserList;
    ArrayList<ObjectUser> myUserListOriginal;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton buttonAddUser;

    MyAdapterUserShow myAdapterUserShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_show);

        this.myUser = getIntent().getParcelableExtra("myUser");

        this.myUserList = new ArrayList<ObjectUser>();
        this.myUserListOriginal = new ArrayList<ObjectUser>();
        //---------------------Recycle View-------------------------------
        buildRecyclerView();

        new UserShow.HttpsRequest(this).execute();
    }

    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.my_recycle_view);
        this.myAdapterUserShow = new MyAdapterUserShow(this, findViewById(R.id.userShow_main_containerView), this.myUserList);
        this.recyclerView.setAdapter(myAdapterUserShow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    class HttpsRequest extends AsyncTask<String, Void, InputStream> {
        private static final String get_user_list_url = "get_user_list.php";
        private Context context;
        Connector connector;

        public HttpsRequest(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_user_list_url);
            connector.addPostParameter("user_type", myUser.getType());
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            //super.onPostExecute(inputStream);
            //-->todo process user list into UserObject array + notify (changes) Adapter view
        }
    }
}