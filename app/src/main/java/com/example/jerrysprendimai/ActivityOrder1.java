package com.example.jerrysprendimai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityOrder1 extends AppCompatActivity {

    Button proceedButton;
    LinearLayout addDealerButton, addObjectButton;

    ArrayList<ObjectDealer> myDealerList;
    ArrayList<ObjectDealer> myDealerListOriginal;
    ArrayList<ObjectObject> myObjectList;
    ArrayList<ObjectObject> myObjectListOriginal;

    View dialogView;
    AlertDialog.Builder builder;
    AlertDialog dialog ;
    ObjectUser myUser;
    CardView cardViewDealer, cardViewObject;
    RecyclerView recyclerViewDealer, recyclerViewObject;
    View.OnClickListener dealerOnClickListener, objectOnClickListener;
    MyAdapterDealerShow myAdapterDealerShow;
    ObjectOrder myOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order1);

        //----binding
        proceedButton      = findViewById(R.id.button_order_p1_continue);
        addDealerButton    = findViewById(R.id.linearLayout_oder_p1_dealer_button);
        addObjectButton    = findViewById(R.id.linearLayout_oder_p1_object_button);
        recyclerViewDealer = findViewById(R.id.my_recycle_view_dealer);
        recyclerViewObject = findViewById(R.id.my_recycle_view_object);
        cardViewDealer     = findViewById(R.id.cardView_oder_p1_dealer);
        cardViewObject     = findViewById(R.id.cardView_oder_p1_object);

        //---------------Read myUser object----------------------
        this.myUser = getIntent().getParcelableExtra("myUser");

        this.myOrder = new ObjectOrder();

        proceedButton.setText(proceedButton.getText() + "   1 / 3");
        proceedButton.setEnabled(false);
        proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button_grey));

        dealerOnClickListener = v-> {
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_with_recycler_view, findViewById(R.id.order_pt1_main_containerView), false);
            MyAdapterDealerShow myAdapterDealerShow = new MyAdapterDealerShow(this, myDealerList, myDealerListOriginal, "dealerShowDialogP1", null);
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
            dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_with_recycler_view, findViewById(R.id.order_pt1_main_containerView), false);
            MyAdapterObjectShowP1 myAdapterObjectShowP1 = new MyAdapterObjectShowP1(this, null, myObjectList, myUser, null);
            RecyclerView dialogRecyclerViewObject = dialogView.findViewById(R.id.my_recycle_view_dialog);
            dialogRecyclerViewObject.setAdapter(myAdapterObjectShowP1);
            dialogRecyclerViewObject.setLayoutManager(new LinearLayoutManager(this));
            builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
            builder.setView(dialogView);

            builder.setOnDismissListener(dialog -> { });
            dialog = builder.create();
            dialog.show();
        };

        cardViewDealer.setOnClickListener(dealerOnClickListener);
        addDealerButton.setOnClickListener(dealerOnClickListener);

        cardViewObject.setOnClickListener(objectOnClickListener);
        addObjectButton.setOnClickListener(objectOnClickListener);

        proceedButton.setOnClickListener(v->{
            //--Todo implement check if dealer and object are not empty
            Intent intent = new Intent(this, ActivityOrder2.class);
            intent.putExtra("myUser", myUser);
            startActivity(intent);
        });
    }
    public void dealerSelectedCallback(int adapterPosition){
        cardViewDealer.setVisibility(View.GONE);
        recyclerViewDealer.setVisibility(View.VISIBLE);
        ArrayList<ObjectDealer> myDealer = new ArrayList<>();
        myDealer.add(myDealerList.get(adapterPosition));
        MyAdapterDealerShow myAdapterDealer = new MyAdapterDealerShow(this, myDealer, myDealerListOriginal, "dealerShowP1", dealerOnClickListener);
        recyclerViewDealer.setAdapter(myAdapterDealer);
        recyclerViewDealer.setLayoutManager(new LinearLayoutManager(this));
        dialog.dismiss();

        this.myOrder.setMyDealer(myDealerList.get(adapterPosition));
        checkIsOkToProceed();
    }

    public void objectSelectedCallback(int adapterPosition) {
        cardViewObject.setVisibility(View.GONE);
        recyclerViewObject.setVisibility(View.VISIBLE);
        ArrayList<ObjectObject> myObject = new ArrayList<>();
        myObject.add(myObjectList.get(adapterPosition));
        MyAdapterObjectShowP1 myAdapterObject = new MyAdapterObjectShowP1(this, null, myObject, myUser, objectOnClickListener);
        recyclerViewObject.setAdapter(myAdapterObject);
        recyclerViewObject.setLayoutManager(new LinearLayoutManager(this));
        dialog.dismiss();

        this.myOrder.setMyObject(myObjectList.get(adapterPosition));

        checkIsOkToProceed();
    }

    private void checkIsOkToProceed() {
        if((this.myOrder.getMyDealer() != null)&&(this.myOrder.getMyObject() != null)){
            proceedButton.setEnabled(true);
            proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button));
        }else{

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //---------------Read myUser object----------------------
        if(this.myUser == null){
            this.myUser = getIntent().getParcelableExtra("myUser");
        }
        new HttpsRequestGetDealerList(this).execute();
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

            if(((ActivityOrder1) context).myDealerList == null){
               ((ActivityOrder1) context).myDealerList = new ArrayList<>();
               ((ActivityOrder1) context).myDealerListOriginal = new ArrayList<>();
            }

            ArrayList<ObjectDealer> dealerArrayList = getDealerList(connector);
            ((ActivityOrder1) context).myDealerList.removeAll(((ActivityOrder1) context).myDealerListOriginal);
            ((ActivityOrder1) context).myDealerList.addAll(dealerArrayList);
            ((ActivityOrder1) context).myDealerListOriginal = new ArrayList<ObjectDealer>();
            ((ActivityOrder1) context).myDealerListOriginal.addAll(((ActivityOrder1) context).myDealerList);
            //((ActivityOrder1) context).myAdapterDealerShow.notifyDataSetChanged();
            //((ActivityOrder1) context).swipeRefreshLayout.setRefreshing(false);

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
            if(((ActivityOrder1) context).myObjectList == null){
                ((ActivityOrder1) context).myObjectList = new ArrayList<>();
                ((ActivityOrder1) context).myObjectListOriginal = new ArrayList<>();
            }
            connector.decodeResponse();
            ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            ((ActivityOrder1) context).myObjectList.removeAll(((ActivityOrder1) context).myObjectListOriginal);
            ((ActivityOrder1) context).myObjectList.addAll(objectArryList);
            ((ActivityOrder1) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            ((ActivityOrder1) context).myObjectListOriginal.addAll(((ActivityOrder1) context).myObjectList);
            //((ActivityObjectShow) context).myAdapterObjectShow.notifyDataSetChanged();
            //((ActivityObjectShow) context).swipeRefreshLayout.setRefreshing(false);
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
}