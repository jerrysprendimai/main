package com.example.jerrysprendimai;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAdapterChatShow extends RecyclerView.Adapter<MyAdapterChatShow.MyViewHolder> {

    ArrayList<ObjectObject> myObjectList;
    Context context;
    ObjectUser myUser;

    public MyAdapterChatShow(Context context, ArrayList<ObjectObject> myObjectList, ObjectUser myUser) {
        this.context = context;
        this.myObjectList = myObjectList;
        this.myUser = myUser;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_chat, parent, false);
        return new MyAdapterChatShow.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObjectObject myObject = myObjectList.get(holder.getAdapterPosition());

        holder.chatObjectIcon.setImageResource(context.getResources().getIdentifier(myObject.getIcon(), "drawable", context.getApplicationInfo().packageName));
        holder.chatObjectName.setText(myObject.getObjectName());
        holder.chatObjectCustomer.setText(myObject.getCustomerName());

        //---row on click listener
        holder.myRow.setOnClickListener(v->{
            ((ActivityChatShow)context).swipeRefreshLayout.setRefreshing(true);
            new HttpsRequestGetObjectDetails(context, myObject).execute();
        });
    }

    @Override
    public int getItemCount() {
        return this.myObjectList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout myRow;
        ImageView chatObjectIcon;
        TextView chatObjectName, chatObjectCustomer;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chatObjectIcon     = itemView.findViewById(R.id.chat_object_icon);
            chatObjectName     = itemView.findViewById(R.id.chat_object_name);
            chatObjectCustomer = itemView.findViewById(R.id.chat_object_customer);
            myRow              = itemView.findViewById(R.id.chat_my_container);
        }
    }

    class HttpsRequestGetObjectDetails extends AsyncTask<String, Void, InputStream> {
        private static final String get_object_details_url = "get_object_details.php";

        private Context context;
        private ObjectObject clickObject;
        private ArrayList<ObjectObjUser> objectUserArrayList;
        private ArrayList<ObjectObjDetails> objectDetailsArrayList;
        private ArrayList<ObjectObjPic> objectPicturesArrayList;

        Connector connector;

        public HttpsRequestGetObjectDetails(Context ctx, ObjectObject obj){
            context     = ctx;
            clickObject = obj;
        }

        public ArrayList<ObjectObjUser> getObjectUserArrayList() {
            return objectUserArrayList;
        }
        public void setObjectUserArrayList(ArrayList<ObjectObjUser> objectUserArrayList) {
            this.objectUserArrayList = objectUserArrayList;
        }
        public ArrayList<ObjectObjDetails> getObjectDetailsArrayList() {
            return objectDetailsArrayList;
        }
        public void setObjectDetailsArrayList(ArrayList<ObjectObjDetails> objectDetailsArrayList) {
            this.objectDetailsArrayList = objectDetailsArrayList;
        }
        public ArrayList<ObjectObjPic> getObjectPicturesArrayList() {
            return objectPicturesArrayList;
        }
        public void setObjectPicturesArrayList(ArrayList<ObjectObjPic> objectPicturesArrayList) {
            this.objectPicturesArrayList = objectPicturesArrayList;
        }
        public void setClickObject(ObjectObject clickObject) {
            this.clickObject = clickObject;
        }
        public ObjectObject getClickObject() {
            return clickObject;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, get_object_details_url);
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(String.valueOf(clickObject.getId()).getBytes()), Base64.DEFAULT));
            //connector.addPostParameter("user_id",   Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            ArrayList<ObjectObjUser> objUserArrayList = new ArrayList<>();
            ArrayList<ObjectObjDetails> objDetailsArrayList = new ArrayList<>();
            ArrayList<ObjectObjPic> objPicsArrayList = new ArrayList<>();
            ArrayList<ObjectUser> employeeArrayList = new ArrayList<>();
            ArrayList<ObjectUser> ownerArrayList = new ArrayList<>();
            ArrayList<ObjectUser> headerArrayList = new ArrayList<>();
            ArrayList<ObjectObject> objectArrayList = new ArrayList<>();

            JSONArray responseObjDetails  = new JSONArray();
            JSONArray responseObjUser     = new JSONArray();
            JSONArray responseObjPic      = new JSONArray();
            JSONArray responseEmployee    = new JSONArray();
            JSONArray responseObject      = new JSONArray();
            JSONArray responseOwners      = new JSONArray();
            JSONArray responseHeader     = new JSONArray();
            Integer completeCount = 0;
            try {
                responseObjDetails = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(0));
                responseObjUser    = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(1));
                responseObjPic     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(2));
                responseEmployee   = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(3));
                responseObject     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(4));
                responseOwners     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(5));
                responseHeader     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(6));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try{
                for(int i = 0; i < responseObjDetails.length(); i++){
                    ObjectObjDetails objectObjDetails = new ObjectObjDetails((JSONObject) responseObjDetails.get(i));
                    objDetailsArrayList.add(objectObjDetails);
                    if(objectObjDetails.getCompleted().equals("X")){
                        completeCount += 1;
                    }
                }
                setObjectDetailsArrayList(objDetailsArrayList);

                for(int i = 0; i < responseObjUser.length(); i++){
                    ObjectObjUser objectObjUser = new ObjectObjUser((JSONObject) responseObjUser.get(i));
                    objUserArrayList.add(objectObjUser);
                }
                setObjectUserArrayList(objUserArrayList);

                for(int i = 0; i < responseObjPic.length(); i++){
                    ObjectObjPic objectObjPic = new ObjectObjPic((JSONObject) responseObjPic.get(i));
                    objPicsArrayList.add(objectObjPic);
                }
                setObjectPicturesArrayList(objPicsArrayList);

                for(int i =0; i < responseEmployee.length(); i++){
                    ObjectUser objectUser = new ObjectUser((JSONObject) responseEmployee.get(i));
                    employeeArrayList.add(objectUser);
                }

                for(int i = 0; i < responseObject.length(); i++ ){
                    ObjectObject updatedObject = new ObjectObject((JSONObject) responseObject.get(i), "wa");
                    if(updatedObject != null){
                        setClickObject(updatedObject);
                        break;
                    }
                }
                for(int i = 0; i<responseOwners.length(); i++){
                    ObjectUser objectUser = new ObjectUser((JSONObject) responseOwners.get(i));
                    ownerArrayList.add(objectUser);
                }
                ObjectObject objectObject = null;
                for (int i=0; i<responseHeader.length(); i++){
                    objectObject = new ObjectObject((JSONObject) responseHeader.get(i), "wa");
                    break;
                }

                Intent intent = new Intent(context, ActivityChat.class);
                intent.putExtra("myUser", myUser);
                intent.putExtra("objectObject", getClickObject());
                intent.putParcelableArrayListExtra("listUser", getObjectUserArrayList());
                intent.putParcelableArrayListExtra("employeeList", employeeArrayList);
                intent.putParcelableArrayListExtra("ownerList", ownerArrayList);
                context.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
