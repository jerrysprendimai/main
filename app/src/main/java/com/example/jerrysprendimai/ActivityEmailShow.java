package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class ActivityEmailShow extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    RecyclerView recyclerView;
    ObjectUser myUser;

    MyAdapterEmailShow myAdapterEmailShow;
    ArrayList<ObjectOrder> myEmailList;
    ArrayList<ObjectOrder> myEmailListOriginal;
    ArrayList<ObjectOrder> inbox;
    ArrayList<ObjectOrder> sent;

    Boolean deletionMode;
    ArrayList toBeDeletedList;

    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton buttonAddEmail;
    FloatingActionButton buttonDeleteEmail;
    LinearLayout retractableView, retractableRow;
    Button retractableButton;
    SwitchCompat mailboxSwitch;
    TextView mailboxLabel, switchButtonLabel;
    CheckBox showAllCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_show);

        //----------------SwipeRefreschLayout
        swipeRefreshLayout = findViewById(R.id.my_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this::onRefresh);
        swipeRefreshLayout.setRefreshing(true);

        this.deletionMode = false;
        this.toBeDeletedList = new ArrayList<Integer>();

        //--retractable button
        retractableView = findViewById(R.id.email_filter_linearLayout);
        retractableView.setVisibility(View.GONE);
        retractableButton = findViewById(R.id.email_filter_retractable_button);
        retractableButton.setOnClickListener(v->{
            if(retractableView.getVisibility() == View.GONE){
                TransitionManager.beginDelayedTransition(retractableView, new AutoTransition());
                retractableView.setVisibility(View.VISIBLE);
                retractableButton.setBackground(getResources().getDrawable(R.drawable.ic_arrow_up_white));
            }else{
                retractableView.setVisibility(View.GONE);
                retractableButton.setBackground(getResources().getDrawable(R.drawable.ic_arrow_down_white));
            }
        });
        retractableRow = findViewById(R.id.email_filter_row_linearLayout);
        retractableRow.setOnClickListener(v->{
            retractableButton.setSoundEffectsEnabled(false);
            retractableButton.performClick();
            retractableButton.setSoundEffectsEnabled(true);
        });

        //---Mailbox Switch + Label
        mailboxSwitch = findViewById(R.id.email_mailbox_switchButton);
        switchButtonLabel = findViewById(R.id.email_switchButton_label);
        mailboxLabel = findViewById(R.id.email_mailbox_label);
        mailboxSwitch.setChecked(false);
        switchButtonLabel.setText(getResources().getString(R.string.inbox));
        mailboxLabel.setText(getResources().getString(R.string.inbox));
        mailboxSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                switchButtonLabel.setText(getResources().getString(R.string.sent));
                mailboxLabel.setText(getResources().getString(R.string.sent));
                myEmailList.removeAll(myEmailList);
                myEmailList.addAll(applyFilter(myEmailListOriginal));
                myAdapterEmailShow.notifyDataSetChanged();
            }else{
                switchButtonLabel.setText(getResources().getString(R.string.inbox));
                mailboxLabel.setText(getResources().getString(R.string.inbox));
                myEmailList.removeAll(myEmailList);
                myEmailList.addAll(applyFilter(myEmailListOriginal));
                myAdapterEmailShow.notifyDataSetChanged();
            }
        });

        //---CheckBox showAll
        showAllCheckbox = findViewById(R.id.email_show_all_checkbox);
        showAllCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                mailboxSwitch.setEnabled(false);
                myEmailList.removeAll(myEmailList);
                myEmailList.addAll(applyFilter(myEmailListOriginal));
                myAdapterEmailShow.notifyDataSetChanged();
            }else{
                mailboxSwitch.setEnabled(true);
                myEmailList.removeAll(myEmailList);
                myEmailList.addAll(applyFilter(myEmailListOriginal));
                myAdapterEmailShow.notifyDataSetChanged();
            }
        });


        //----------------Button Add Order
        Context context = this;
        buttonAddEmail = findViewById(R.id.button_email_add_new);
        buttonAddEmail.setOnClickListener(v->{
            swipeRefreshLayout.setRefreshing(true);
            //Intent intent = new Intent(context, ActivityDealerEdit.class);
            //intent.putExtra("myUser", myUser);
            //context.startActivity(intent);
        });
        //----------------Button Delete Order
        buttonDeleteEmail = findViewById(R.id.button_email_delete);
        buttonDeleteEmail.setOnClickListener(v->{
            //--Todo
        });

        this.myUser = getIntent().getParcelableExtra("myUser");
        this.myEmailList = new ArrayList<ObjectOrder>();
        this.myEmailListOriginal = new ArrayList<ObjectOrder>();
        this.inbox = new ArrayList<ObjectOrder>();
        this.sent = new ArrayList<ObjectOrder>();

        //---------------------Recycle View-------------------------------
        buildRecyclerView();

    }
    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.my_recycle_view);
        this.myAdapterEmailShow = new MyAdapterEmailShow(this, this.myEmailList);
        this.recyclerView.setAdapter(myAdapterEmailShow);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClick(View v) {

    }

    public void refresh(){
        new HttpsRequestGetEmailList(this).execute();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    public ArrayList<ObjectOrder> applyFilter(ArrayList<ObjectOrder> myEmailListOriginal) {
        ArrayList<ObjectOrder> emailArrayList = new ArrayList<>();

        if(showAllCheckbox.isChecked()){
            emailArrayList.addAll(myEmailListOriginal);
        }else{
            String criteria = "in";
            if(mailboxSwitch.isChecked()){  //switched = sent
                criteria = "out";
            }else{
                criteria = "in";
            }
            for(ObjectOrder objectOrder: myEmailListOriginal){
                if(objectOrder.getType().equals(criteria)){
                    emailArrayList.add(objectOrder);
                }
            }
        }

        return emailArrayList;
    }

    class HttpsRequestGetEmailList extends AsyncTask<String, Void, InputStream> {
        private static final String get_email_list_url = "get_email_list.php";

        private Context context;
        Connector connector;

        public HttpsRequestGetEmailList(Context ctx){
            context = ctx;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_email_list_url);
            connector.addPostParameter("user_type",  Base64.encodeToString(MCrypt.encrypt(myUser.getType().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(myUser.getUname().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_id",    Base64.encodeToString(MCrypt.encrypt(myUser.getId().toString().getBytes()), Base64.DEFAULT));
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

            ArrayList<ObjectOrder> objectEmailList = getEmailList(connector);

            ((ActivityEmailShow)context).myEmailListOriginal.removeAll(((ActivityEmailShow)context).myEmailListOriginal);
            ((ActivityEmailShow)context).myEmailListOriginal.addAll(objectEmailList);

            ((ActivityEmailShow)context).myEmailList.removeAll(((ActivityEmailShow)context).myEmailList);
            ((ActivityEmailShow)context).myEmailList.addAll(applyFilter(((ActivityEmailShow)context).myEmailListOriginal));
            //((ActivityEmailShow)context).myEmailList.addAll(objectEmailList);

            ((ActivityEmailShow)context).myAdapterEmailShow.notifyDataSetChanged();
            ((ActivityEmailShow)context).swipeRefreshLayout.setRefreshing(false);

            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectOrder>getEmailList(Connector conn){
            ArrayList<ObjectOrder> objectArrayList = new ArrayList<>();
            try {
                ObjectOrder objectOrder;
                JSONArray responseObjects = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects.length(); i++) {
                    objectOrder = new ObjectOrder((JSONObject) responseObjects.get(i));
                    objectArrayList.add(objectOrder);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return objectArrayList;
        }
    }

}