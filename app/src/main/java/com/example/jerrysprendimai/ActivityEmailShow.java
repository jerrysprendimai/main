package com.example.jerrysprendimai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

    ArrayList<ObjectDealer> myDealerList;
    ArrayList<ObjectDealer> myDealerListOriginal;
    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;
    ArrayList<ObjectObject> myObject;
    ArrayList<ObjectDealer> myDealer;

    MyAdapterEmailShow myAdapterEmailShow;
    ArrayList<ObjectOrder> myEmailList;
    ArrayList<ObjectOrder> myEmailListOriginal;
    ArrayList<ObjectOrder> inbox;
    ArrayList<ObjectOrder> sent;

    MyAdapterDealerShow myAdapterDealer;
    MyAdapterObjectShowP1 myAdapterObject;

    CardView dealerCardView, objectCardView;
    Boolean deletionMode;
    ArrayList toBeDeletedList;
    DrawerLayout mainContainer;
    RecyclerView dealerFilterRecyclerView;
    RecyclerView objectFitlerRecycclerView;
    Boolean dealerFilter;
    Boolean objectFilter;

    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton buttonAddEmail;
    FloatingActionButton buttonDeleteEmail;
    LinearLayout retractableView, retractableRow, dealerButton, objectButton;
    View.OnClickListener dealerOnClickListener, objectOnClickListener, callbackObjectClickListener;
    View dialogView;
    AlertDialog.Builder builder;
    AlertDialog dialog ;

    Button retractableButton;
    SwitchCompat mailboxSwitch;
    TextView mailboxLabel, switchButtonLabel;
    CheckBox showAllCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_show);

        mainContainer = findViewById(R.id.email_show_main_container);
        //----------------filter handling
        dealerCardView = findViewById(R.id.email_dealer_cardView);
        objectCardView = findViewById(R.id.email_object_cardView);
        dealerButton   = findViewById(R.id.email_dealer_linearLayout_button);
        objectButton   = findViewById(R.id.email_object_linearLayout_button);
        dealerFilterRecyclerView = findViewById(R.id.my_recycle_view_dealer_email_read);
        objectFitlerRecycclerView =findViewById(R.id.my_recycle_view_object_email_read);

        dealerOnClickListener = v-> {
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_with_recycler_view, findViewById(R.id.email_show_main_container), false);
            MyAdapterDealerShow myAdapterDealerShow = new MyAdapterDealerShow(this, myDealerList, myDealerListOriginal, "dealerShowEmailFilter", null, false);
            RecyclerView dialogRecyclerViewDealer = dialogView.findViewById(R.id.my_recycle_view_dialog);
            dialogRecyclerViewDealer.setAdapter(myAdapterDealerShow);
            dialogRecyclerViewDealer.setLayoutManager(new LinearLayoutManager(this));
            builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setView(dialogView);

            builder.setOnDismissListener(dialog -> { });
            dialog = builder.create();
            dialog.show();
        };
        objectOnClickListener = v->{
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_with_recycler_view, findViewById(R.id.email_show_main_container), false);
            MyAdapterObjectShowP1 myAdapterObjectShowP1 = new MyAdapterObjectShowP1(this, null, myObjectList, myUser, null, "objectShowEmailFilter", false);
            RecyclerView dialogRecyclerViewObject = dialogView.findViewById(R.id.my_recycle_view_dialog);
            dialogRecyclerViewObject.setAdapter(myAdapterObjectShowP1);
            dialogRecyclerViewObject.setLayoutManager(new LinearLayoutManager(this));
            builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setView(dialogView);

            builder.setOnDismissListener(dialog -> { });
            dialog = builder.create();
            dialog.show();
        };
        dealerFilter = false;
        objectFilter = false;
        dealerCardView.setOnClickListener(dealerOnClickListener);
        objectCardView.setOnClickListener(objectOnClickListener);
        dealerButton.setOnClickListener(v->{
            //dealerOnClickListener
            if(dealerFilter){
                dealerButton.setBackgroundColor(getResources().getColor(R.color.jerry_blue));
                ((ImageView) findViewById(R.id.email_dealer_button_img)).setImageDrawable(getDrawable(R.drawable.ic_add_white));
                myDealer.removeAll(myDealer);
                dealerFilter = false;
                myEmailList.removeAll(myEmailList);
                myEmailList.addAll(applyFilter(myEmailListOriginal));
                myAdapterEmailShow.notifyDataSetChanged();
                myAdapterDealer.notifyDataSetChanged();
                dealerCardView.setVisibility(View.VISIBLE);
                dealerFilterRecyclerView.setVisibility(View.GONE);
            }else{
                dealerCardView.setSoundEffectsEnabled(false);
                dealerCardView.performClick();
                dealerCardView.setSoundEffectsEnabled(true);
            }
           }
        );
        objectButton.setOnClickListener(v->{
            //objectOnClickListener
            if(objectFilter){
                objectButton.setBackgroundColor(getResources().getColor(R.color.jerry_blue));
                ((ImageView) findViewById(R.id.email_object_button_img)).setImageDrawable(getDrawable(R.drawable.ic_add_white));
                myObject.removeAll(myObject);
                objectFilter = false;
                myEmailList.removeAll(myEmailList);
                myEmailList.addAll(applyFilter(myEmailListOriginal));
                myAdapterEmailShow.notifyDataSetChanged();
                myAdapterObject.notifyDataSetChanged();
                objectCardView.setVisibility(View.VISIBLE);
                objectFitlerRecycclerView.setVisibility(View.GONE);
            }else{
                objectCardView.setSoundEffectsEnabled(false);
                objectCardView.performClick();
                objectCardView.setSoundEffectsEnabled(true);
            }
        });

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
                switchButtonLabel.setText(getResources().getString(R.string.all_email));
                mailboxLabel.setText(getResources().getString(R.string.all_email));
                mailboxSwitch.setEnabled(false);
                myEmailList.removeAll(myEmailList);
                myEmailList.addAll(applyFilter(myEmailListOriginal));
                myAdapterEmailShow.notifyDataSetChanged();
            }else{
                if(mailboxSwitch.isChecked()){
                    switchButtonLabel.setText(getResources().getString(R.string.sent));
                    mailboxLabel.setText(getResources().getString(R.string.sent));
                }else{
                    switchButtonLabel.setText(getResources().getString(R.string.inbox));
                    mailboxLabel.setText(getResources().getString(R.string.inbox));
                }
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
            Intent intent = new Intent(context, ActivityOrder1.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
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
        this.myAdapterEmailShow = new MyAdapterEmailShow(this, this.myEmailList, myUser);
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
        ArrayList<ObjectOrder> emailArrayList2 = new ArrayList<>();
        if(myDealer != null) {
            if (myDealer.size() > 0) {
                for (ObjectOrder objectOrder : emailArrayList) {
                    if (objectOrder.getDealerId().equals(myDealer.get(0).getId())) {
                        emailArrayList2.add(objectOrder);
                    }
                }
                emailArrayList.removeAll(emailArrayList);
                emailArrayList.addAll(emailArrayList2);
            }
        }
        ArrayList<ObjectOrder> emailArrayList3 = new ArrayList<>();
        if(myObject != null) {
            if (myObject.size() > 0) {
                for (ObjectOrder objectOrder : emailArrayList) {
                    if (objectOrder.getObjectID().equals(myObject.get(0).getId())) {
                        emailArrayList3.add(objectOrder);
                    }
                }
                emailArrayList.removeAll(emailArrayList);
                emailArrayList.addAll(emailArrayList3);
            }
        }

        return emailArrayList;
    }
    public void orderSelectedCallback(int adapterPosition) {
        myObject = new ArrayList<>();
        myObject.add(myObjectList.get(adapterPosition));
        myAdapterObject = new MyAdapterObjectShowP1(this, null, myObject, myUser, objectOnClickListener, "objectShowP1", true);

        objectCardView.setVisibility(View.GONE);
        objectFitlerRecycclerView.setVisibility(View.VISIBLE);
        objectFitlerRecycclerView.setAdapter(myAdapterObject);
        objectFitlerRecycclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog.dismiss();

        myEmailList.removeAll(myEmailList);
        myEmailList.addAll(applyFilter(myEmailListOriginal));
        myAdapterEmailShow.notifyDataSetChanged();

        objectButton.setBackgroundColor(getResources().getColor(R.color.jerry_red));
        ((ImageView) findViewById(R.id.email_object_button_img)).setImageDrawable(getDrawable(R.drawable.ic_remove_white));
        objectFilter = true;
    }
    public void dealerSelectedCallback(int adapterPosition) {
        myDealer = new ArrayList<>();
        myDealer.add(myDealerList.get(adapterPosition));
        myAdapterDealer = new MyAdapterDealerShow(this, myDealer, myDealerListOriginal, "dealerShowP1", dealerOnClickListener, true);

        dealerCardView.setVisibility(View.GONE);
        dealerFilterRecyclerView.setVisibility(View.VISIBLE);
        dealerFilterRecyclerView.setAdapter(myAdapterDealer);
        dealerFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog.dismiss();
        myEmailList.removeAll(myEmailList);
        myEmailList.addAll(applyFilter(myEmailListOriginal));
        myAdapterEmailShow.notifyDataSetChanged();

        dealerButton.setBackgroundColor(getResources().getColor(R.color.jerry_red));
        ((ImageView) findViewById(R.id.email_dealer_button_img)).setImageDrawable(getDrawable(R.drawable.ic_remove_white));
        dealerFilter = true;
    }

    public ArrayList<ObjectDealer> getMyDealerList() {        return myDealerList;    }
    public void setMyDealerList(ArrayList<ObjectDealer> myDealerList) {        this.myDealerList = myDealerList;    }
    public ArrayList<ObjectObject> getMyObjectList() {        return myObjectList;    }
    public void setMyObjectList(ArrayList<ObjectObject> myObjectList) {        this.myObjectList = myObjectList;    }


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


            new HttpsRequestGetDealerList(context).execute();

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

            if(((ActivityEmailShow) context).myDealerList == null){
                ((ActivityEmailShow) context).myDealerList = new ArrayList<>();
                ((ActivityEmailShow) context).myDealerListOriginal = new ArrayList<>();
            }

            //ArrayList<ObjectDealer> dealerArrayList = getDealerList(connector);
            ((ActivityEmailShow) context).myDealerList.removeAll(((ActivityEmailShow) context).myDealerList);
            ((ActivityEmailShow) context).myDealerList.addAll(getDealerList(connector));
            ((ActivityEmailShow) context).myDealerListOriginal.removeAll(((ActivityEmailShow) context).myDealerListOriginal);
            ((ActivityEmailShow) context).myDealerListOriginal.addAll(((ActivityEmailShow) context).myDealerList);


            new HttpsRequestGetObjectList(context).execute();
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

    class HttpsRequestGetObjectList extends AsyncTask<String, Void, InputStream> {
        private static final String get_object_list_url = "get_object_list.php";

        private Context context;
        Connector connector;
        ObjectOrder myOrder;
        ArrayList<ObjectDealer> dealerArrayList;

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
            if(((ActivityEmailShow) context).myObjectList == null){
                ((ActivityEmailShow) context).myObjectList = new ArrayList<>();
                ((ActivityEmailShow) context).myObjectListOriginal = new ArrayList<>();
            }
            connector.decodeResponse();
            //ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityEmailShow) context).myObjectList.removeAll(((ActivityEmailShow) context).myObjectList);
            ((ActivityEmailShow) context).myObjectList.addAll(getObjectList(connector));
            ((ActivityEmailShow) context).myObjectListOriginal.removeAll(((ActivityEmailShow) context).myObjectListOriginal);
            ((ActivityEmailShow) context).myObjectListOriginal.addAll(((ActivityEmailShow) context).myObjectList);

            /*Intent intent = new Intent(context, ActivityEmailRead.class);
            intent.putExtra("myUser", myUser);
            intent.putExtra("myEmail", myOrder);
            intent.putExtra("myObjectList", objectArryList);
            intent.putExtra("myDealerList", dealerArrayList);
            context.startActivity(intent);*/

            ((ActivityEmailShow)context).swipeRefreshLayout.setRefreshing(false);

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