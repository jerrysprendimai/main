package com.example.jerrysprendimai;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class MyAdapterCalendarEvents extends RecyclerView.Adapter<MyAdapterCalendarEvents.MyViewHolder>{
    final String USER = "user";

    Context context;
    ObjectUser myUser;
    ObjectEvent myClickObjectEvent;
    ArrayList<ObjectEvent> myEventList;
    ArrayList<ObjectObject> myObjectList;


    public MyAdapterCalendarEvents(Context cntx, ArrayList events, ArrayList<ObjectObject> objectList, ObjectUser user) {
        this.context = cntx;
        this.myEventList = events;
        this.myObjectList = objectList;
        this.myUser = user;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_calendar_event, parent, false);
        return new MyViewHolder(view);
        //return null;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout myRow;
        TextView title, description, viewedIndicator;
        ImageView eventIcon;
        ObjectObject objectToDisplay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            viewedIndicator = itemView.findViewById(R.id.calendar_event_indicator);
            eventIcon   = itemView.findViewById(R.id.calendar_event_icon);
            title       = itemView.findViewById(R.id.calendar_event_title);
            description = itemView.findViewById(R.id.calendar_event_description);
            myRow       = itemView.findViewById(R.id.my_container);

        }
    }



    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObjectEvent myObjectEvent = myEventList.get(holder.getAdapterPosition());
        myObjectEvent.setMyViewHolderCalendarEvents(holder);

        holder.objectToDisplay = null;
        try {
            String[] strArry = myObjectEvent.getTitle().split("#");
            for (int i = 0; i < myObjectList.size(); i++) {
                if (myObjectList.get(i).getId().toString().equals(strArry[1])) {
                    holder.objectToDisplay = myObjectList.get(i);
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if(holder.objectToDisplay != null){
            holder.eventIcon.setImageResource(context.getResources().getIdentifier(holder.objectToDisplay.getIcon(),"drawable", context.getApplicationInfo().packageName));

            if(holder.objectToDisplay.getNotViewed().equals("X")){
                holder.viewedIndicator.setVisibility(View.VISIBLE);
            }else{
                holder.viewedIndicator.setVisibility(View.GONE);
            }
        }else{
            holder.viewedIndicator.setVisibility(View.GONE);
        }


        holder.title.setText(myObjectEvent.getTitle());
        holder.description.setText(myObjectEvent.getDescription());
        holder.myRow.setOnClickListener(v->{
            ((ActivityCalendar) context).setViewEnabled(false);
            ((ActivityCalendar) context).lockView();

            setMyClickObjectEvent(myObjectEvent);
            /*
            ObjectObject objectToDisplay = null;
            try {
                String[] strArry = myObjectEvent.getTitle().split("#");


                for (int i = 0; i < myObjectList.size(); i++) {
                    if (myObjectList.get(i).getId().toString().equals(strArry[1])) {
                        objectToDisplay = myObjectList.get(i);
                        break;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            */
            if(holder.objectToDisplay != null){
                if(!myUser.getUser_lv().equals(USER)){
                    new HttpsRequestLockObject(context, holder.objectToDisplay, holder.objectToDisplay.getId().toString(),"lock").execute();
                }else{
                    holder.objectToDisplay.setNotViewed("");
                    new HttpsRequestViewObject(context, holder.objectToDisplay, holder.objectToDisplay.getId().toString(), holder.objectToDisplay.getNotViewed()).execute();
                    //getObjectDetailsAndDisplay(holder.objectToDisplay);
                }
            }
        });
    }

    public void getObjectDetailsAndDisplay(ObjectObject objectToDisplay) {

        new HttpsRequestGetObjectDetails(context, objectToDisplay).execute();
    }


    @Override
    public int getItemCount() {
        return this.myEventList.size();
        //return 0;
    }

    public ObjectEvent getMyClickObjectEvent() {       return myClickObjectEvent;    }
    public void setMyClickObjectEvent(ObjectEvent myClickObjectEvent) {        this.myClickObjectEvent = myClickObjectEvent;    }

    class HttpsRequestViewObject extends AsyncTask<String, Void, InputStream> {
        private static final String view_object_url = "view_object.php";

        private Context context;
        private String objectId, notVievedValue;
        ObjectObject objectToDisplay;
        Connector connector;

        public HttpsRequestViewObject(Context ctx, ObjectObject obj, String objId, String value){
            context  = ctx;
            objectId = objId;
            notVievedValue = value;
            objectToDisplay = obj;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, view_object_url);
            connector.addPostParameter("user_id",    Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("object_id",  Base64.encodeToString(MCrypt.encrypt(objectId.getBytes()), Base64.DEFAULT));
            connector.addPostParameter("not_viewed", Base64.encodeToString(MCrypt.encrypt(notVievedValue.getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            JSONObject responseObject;
            try {
                responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String lockStatus = MCrypt.decryptSingle(responseObject.getString("status"));
                String lockMsg    = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (lockStatus.equals("1")) {
                    //getObjectDetailsAndDisplay(this.objectToDisplay);
                }else{

                }
                //((ActivityObjectShow) context).onRefresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getObjectDetailsAndDisplay(this.objectToDisplay);
        }
    }

    class HttpsRequestLockObject extends AsyncTask<String, Void, InputStream> {
        private static final String lock_object_url = "lock_object.php";

        private Context context;
        private String request;
        private String objectId;
        private ObjectObject objectObject;
        Connector connector;

        public HttpsRequestLockObject(Context ctx, ObjectObject objectObject, String objId, String req){
            this.context  = ctx;
            this.objectId = objId;
            this.request  = req;
            this.objectObject = objectObject;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, lock_object_url);
            connector.addPostParameter("user_id",   Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(objectId.getBytes()), Base64.DEFAULT));
            connector.addPostParameter("request",   Base64.encodeToString(MCrypt.encrypt(request.getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            JSONObject responseObject;
            try {
                responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String lockStatus = MCrypt.decryptSingle(responseObject.getString("status"));
                String lockMsg    = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (lockStatus.equals("1")) {
                    getObjectDetailsAndDisplay(objectObject);
                }else{
                    Toast.makeText(context, "Užrakinta", Toast.LENGTH_SHORT).show();
                }
                //((ActivityObjectShow) context).onRefresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

            JSONArray responseObjDetails = new JSONArray();
            JSONArray responseObjUser    = new JSONArray();
            JSONArray responseObjPic     = new JSONArray();
            JSONArray responseEmployee   = new JSONArray();
            JSONArray responseObject     = new JSONArray();
            JSONArray responseOwners     = new JSONArray();
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

                ((ActivityCalendar)context).setObjectToDisplay(getClickObject());
                Intent intent = new Intent(context, ActivityObjectEdit.class);
                intent.putExtra("myUser", myUser);
                intent.putExtra("objectObject", getClickObject());
                intent.putParcelableArrayListExtra("listDetails",  getObjectDetailsArrayList());
                intent.putParcelableArrayListExtra("listUser",     getObjectUserArrayList());
                intent.putParcelableArrayListExtra("listPictures", getObjectPicturesArrayList());
                intent.putParcelableArrayListExtra("employeeList", employeeArrayList);
                context.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
