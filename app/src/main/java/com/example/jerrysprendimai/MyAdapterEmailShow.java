package com.example.jerrysprendimai;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAdapterEmailShow extends RecyclerView.Adapter<MyAdapterEmailShow.MyViewHolder> {

    Context context;

    List<ObjectOrder> myEmailList;
    List<ObjectOrder> myEmailListFull;
    ViewGroup parentView;
    ObjectUser myUser;

    final String USER = "user";
    final String OWNER = "owner";
    final String ADMIN = "admin";

    public MyAdapterEmailShow(Context context, List<ObjectOrder> myEmailList, ObjectUser myUser) {
        this.context = context;
        //this.parentView = parentView;
        this.myEmailList = myEmailList;
        this.myEmailListFull = new ArrayList<>(this.myEmailList);
        this.myUser = myUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_email, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.myEmailList.size();
        //return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ObjectOrder myOrder = myEmailList.get(position);

        if(myOrder.getObjectID().equals(0)){
            holder.objectAssignedIndicator.setVisibility(View.VISIBLE);
        }else{
            holder.objectAssignedIndicator.setVisibility(View.GONE);
        }
        String[] fromArray = myOrder.getFrom().split("<");
        holder.emailSender.setHint(fromArray[0]);
        //holder.emailSender.setHint(myOrder.getFrom());
        holder.emailDate.setText(myOrder.getSentDate());
        holder.emailSubject.setHint(myOrder.getMyText());

        if(myOrder.isNotViewed()){
            holder.newIndicator.setVisibility(View.VISIBLE);
            holder.emailSender.setTypeface(null, Typeface.BOLD);
            holder.emailSubject.setTypeface(null, Typeface.BOLD);
            holder.emailSender.setText(fromArray[0]);
            holder.emailSubject.setText(myOrder.getMyText());
        }else{
            holder.newIndicator.setVisibility(View.GONE);
            holder.emailSender.setTypeface(null, Typeface.NORMAL);
            holder.emailSubject.setTypeface(null, Typeface.NORMAL);
            holder.emailSender.setText("");
            holder.emailSubject.setText("");
        }
        if(myOrder.isHasAttachments()){
            holder.attachmentIndicator.setVisibility(View.VISIBLE);
        }else{
            holder.attachmentIndicator.setVisibility(View.GONE);
        }
        if(myOrder.getType().equals("in")){
            holder.emailTypeIn.setVisibility(View.VISIBLE);
            holder.emailTypeOut.setVisibility(View.GONE);
        }else{
            holder.emailTypeIn.setVisibility(View.GONE);
            holder.emailTypeOut.setVisibility(View.VISIBLE);
        }

        holder.myRow.setOnClickListener(v->{
            //new HttpsEmailDetails(context,myOrder).execute();
            Intent intent = new Intent(context, ActivityEmailRead.class);
            intent.putExtra("myUser", myUser);
            intent.putExtra("myEmail", myOrder);
            context.startActivity(intent);
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView newIndicator, objectAssignedIndicator, emailSender, emailDate, emailSubject, emailText;
        ImageView attachmentIndicator, emailTypeIn, emailTypeOut;

        LinearLayout myRow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            attachmentIndicator = itemView.findViewById(R.id.email_attachment_indicator);
            newIndicator = itemView.findViewById(R.id.email_new_indicator);
            objectAssignedIndicator = itemView.findViewById(R.id.email_not_assigned_indicator);
            emailSender  = itemView.findViewById(R.id.email_sender);
            emailDate    = itemView.findViewById(R.id.email_date);
            emailSubject = itemView.findViewById(R.id.email_subject);
            emailTypeIn  = itemView.findViewById(R.id.email_type_in);
            emailTypeOut  = itemView.findViewById(R.id.email_type_out);

            myRow = itemView.findViewById(R.id.my_container);

        }
    }
    /*
    class HttpsEmailDetails extends AsyncTask<String, Void, InputStream> {
        private static final String get_email_details_url = "get_email_details.php";

        private Context context;
        Connector connector;
        ObjectOrder myOrder;

        public HttpsEmailDetails(Context ctx, ObjectOrder myOrder){
            this.context = ctx;
            this.myOrder = myOrder;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_email_details_url);
            connector.addPostParameter("user_type",  Base64.encodeToString(MCrypt.encrypt(myUser.getType().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_uname", Base64.encodeToString(MCrypt.encrypt(Base64.encodeToString(myUser.getUname().getBytes(), Base64.DEFAULT).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user_id",    Base64.encodeToString(MCrypt.encrypt(myUser.getId().toString().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("set_email_viewed", Base64.encodeToString(MCrypt.encrypt(String.valueOf("X").getBytes()), Base64.DEFAULT));
            connector.addPostParameter("message_id",    Base64.encodeToString(MCrypt.encrypt(myOrder.getMessageId().getBytes()), Base64.DEFAULT));
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
            try {
                JSONObject responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String html = MCrypt.decryptSingle(responseObject.getString("html"));
                myOrder.setMyHtml(html);


                //String msg        = MCrypt.decryptSingle(responseObject.getString("msg"));
            }catch (Exception e){
                e.printStackTrace();
            }


            new HttpsRequestGetDealerList(context, myOrder).execute();
            super.onPostExecute(inputStream);
        }
    }
    class HttpsRequestGetDealerList extends AsyncTask<String, Void, InputStream> {
        private static final String get_dealer_list_url = "get_dealer_list.php";

        private Context context;
        Connector connector;
        ObjectOrder myOrder;

        public HttpsRequestGetDealerList(Context ctx, ObjectOrder myOrder){
            context = ctx;
            this.myOrder = myOrder;
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

            if(((ActivityEmailRead) context).myDealerList == null){
                ((ActivityEmailRead) context).myDealerList = new ArrayList<>();
                ((ActivityEmailRead) context).myDealerListOriginal = new ArrayList<>();
            }

            ArrayList<ObjectDealer> dealerArrayList = getDealerList(connector);
            //((ActivityEmailRead) context).myDealerList.removeAll(((ActivityEmailRead) context).myDealerListOriginal);
            //((ActivityEmailRead) context).myDealerList.addAll(dealerArrayList);
            //((ActivityEmailRead) context).myDealerListOriginal = new ArrayList<ObjectDealer>();
            //((ActivityEmailRead) context).myDealerListOriginal.addAll(((ActivityEmailRead) context).myDealerList);


            new HttpsRequestGetObjectList(context, myOrder, dealerArrayList).execute();
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

        public HttpsRequestGetObjectList(Context ctx, ObjectOrder myOrder, ArrayList<ObjectDealer> dealerArrayList){
            context = ctx;
            this.myOrder = myOrder;
            this.dealerArrayList = dealerArrayList;

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
            if(((ActivityEmailRead) context).myObjectList == null){
                ((ActivityEmailRead) context).myObjectList = new ArrayList<>();
                ((ActivityEmailRead) context).myObjectListOriginal = new ArrayList<>();
            }
            connector.decodeResponse();
            ArrayList<ObjectObject> objectArryList = getObjectList(connector);
            //((ActivityEmailRead) context).myObjectList.removeAll(((ActivityEmailRead) context).myObjectListOriginal);
            //((ActivityEmailRead) context).myObjectList.addAll(objectArryList);
            //((ActivityEmailRead) context).myObjectListOriginal = new ArrayList<ObjectObject>();
            //((ActivityEmailRead) context).myObjectListOriginal.addAll(((ActivityEmailRead) context).myObjectList);

            Intent intent = new Intent(context, ActivityEmailRead.class);
            intent.putExtra("myUser", myUser);
            intent.putExtra("myEmail", myOrder);
            intent.putExtra("myObjectList", objectArryList);
            intent.putExtra("myDealerList", dealerArrayList);
            context.startActivity(intent);

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
    */
}
