package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.inappmessaging.internal.ApiClient;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityChat extends AppCompatActivity {

    public  ObjectUser myUser;
    public  ObjectObject myObject;
    private ArrayList<ObjectObjUser> objectUserArrayList;
    private ArrayList<ObjectUser> employeeList;
    private ArrayList<ObjectUser> ownerList;
    private ArrayList<ObjectObject> myObjectList;
    private ArrayList<ObjectObject> myObjectListOriginal;
    private ArrayList<String> myDisplayDates;
    private String currentDate;
    private String dateToDisplay;
    private LinearLayout participantsButton, objectNameButton, objectIconButton;
    private RecyclerView recyclerView;
    private EditText editMessageInput;
    private TextView txtChattingAbout, txtChattingAbout2, txtChatParticipantCount;
    private ProgressBar progressBar;
    private ImageView imgToolBar, sendButton;
    public boolean doNavigationToActivityObjectEdit;

    private String chatRoomId;

    private MyAdapterMessage myAdapterMessage;
    private ArrayList<ObjectMessage> messages;

    final String user = "user";
    final String admin = "admin";
    final String owner = "owner";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        //----bind screen elements
        recyclerView            = findViewById(R.id.chat_recyclerView);
        editMessageInput        = findViewById(R.id.chat_editText);
        txtChattingAbout        = findViewById(R.id.chat_roomName);
        txtChattingAbout2       = findViewById(R.id.chat_roomName2);
        txtChatParticipantCount = findViewById(R.id.chat_participantsCount);
        progressBar             = findViewById(R.id.chat_progressBar);
        imgToolBar              = findViewById(R.id.chat_room_image);
        sendButton              = findViewById(R.id.chat_sendButton);
        participantsButton      = findViewById(R.id.chat_participantsButton);
        objectNameButton        = findViewById(R.id.chat_bojectNameButton);
        objectIconButton        = findViewById(R.id.chat_iconButton);
        messages = new ArrayList<>();

        //---------------Read Intent values----------------------
        this.myUser               = getIntent().getParcelableExtra("myUser");
        this.myObject             = getIntent().getParcelableExtra("objectObject");
        this.objectUserArrayList  = getIntent().getParcelableArrayListExtra("listUser");
        this.employeeList         = getIntent().getParcelableArrayListExtra("employeeList");
        this.ownerList            = getIntent().getParcelableArrayListExtra("ownerList");

        //----Initialize screen values
        myDisplayDates = new ArrayList<>();
        chatRoomId = myObject.getId().toString();
        imgToolBar.setImageResource(getResources().getIdentifier(myObject.getIcon(), "drawable", getApplicationInfo().packageName));
        txtChattingAbout.setText(myObject.getObjectName());
        txtChattingAbout2.setText(myObject.getObjectAddress());
        txtChatParticipantCount.setText("+" + String.valueOf(this.objectUserArrayList.size()));

        ArrayList<ObjectMessage.User> userSeenArrayList = new ArrayList<>();
        String[] temp = {"1","2","3"};
        //ArrayList<String> data = new ArrayList<>();
        for (int i=0; i< getObjectUserArrayList().size(); i++){
            //data.add(getObjectUserArrayList().get(i).getUserId().toString(), false);
            userSeenArrayList.add(new ObjectMessage.User(getObjectUserArrayList().get(i).getUserId().toString(), false));
        }
        for(int i=0; i<getOwnerList().size(); i++){
            userSeenArrayList.add(new ObjectMessage.User(getOwnerList().get(i).getId().toString(), false));
        }
        //----Listeners
        sendButton.setOnClickListener(v->{
            FirebaseDatabase.getInstance().getReference("objects/" + chatRoomId).push().setValue(new ObjectMessage(myUser.getFirst_name(),
                                                                                                                        myUser.getUname(),
                                                                                                                        myUser.getId().toString(),
                                                                                                                        editMessageInput.getText().toString(),
                                                                                                                        HelperDate.get_current_date_disply(),
                                                                                                                        Calendar.getInstance().getTime().toString(),
                                                                                                                        String.valueOf(Calendar.getInstance().getTimeInMillis()),
                                                                                                                        myUser.getUser_lv()
                                                                                                                        ));
            //----send notification
            String title   = myObject.getObjectName()+"   #"+myObject.getId().toString();
            String message = myUser.getFirst_name() +": "+editMessageInput.getText().toString();//messages.get(messages.size()-1).getFirstName() +": "+ messages.get(messages.size()).getContent();
            for (int i=0; i< getObjectUserArrayList().size(); i++){
                ObjectObjUser objectObjUser = getObjectUserArrayList().get(i);
                Integer userListId = getObjectUserArrayList().get(i).getUserId();
                Integer myUserId = myUser.getId();
                if((!getObjectUserArrayList().get(i).getToken().isEmpty()) &&
                   (!getObjectUserArrayList().get(i).getUserId().equals(myUser.getId()))){

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                            getObjectUserArrayList().get(i).getToken(),
                            title,
                            message,
                            myObject.getId().toString(),
                            myObject.getIcon(),
                            getApplicationContext(),
                            ActivityMain.getActivityMain());
                    notificationsSender.SendNotifications();
                }
            }
            for(int i=0; i<getOwnerList().size(); i++){
                ObjectUser objectOwner = getOwnerList().get(i);
                Integer ownerListId = getOwnerList().get(i).getId();
                Integer myUserId = myUser.getId();
                if((!getOwnerList().get(i).getToken().isEmpty()) &&
                        (!getOwnerList().get(i).getId().equals(myUser.getId()))){

                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(
                            getOwnerList().get(i).getToken(),
                            title,
                            message,
                            myObject.getId().toString(),
                            myObject.getIcon(),
                            getApplicationContext(),
                            ActivityMain.getActivityMain());
                    notificationsSender.SendNotifications();
                }
            }
            editMessageInput.setText("");
        });

        Context context = this;
        View.OnClickListener toProjectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDoNavigationToActivityObjectEdit(true);
                if((myUser.getUser_lv().equals(owner))||(myUser.getUser_lv().equals(admin))){
                    //lock object
                    new HttpsRequestLockObject(context, myObject, myObject.getId().toString(),"lock").execute();
                }else{
                    //display object
                    myObject.setNotViewed("");
                    new HttpsRequestViewObject(context, myObject, myObject.getId().toString(), myObject.getNotViewed()).execute();
                }
            }
        };
        objectNameButton.setOnClickListener(toProjectListener);
        objectIconButton.setOnClickListener(toProjectListener);
        if((myUser.getUser_lv().equals(admin)) || (myUser.getUser_lv().equals(owner))){
            participantsButton.setOnClickListener(v ->{
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_object_user_assignment, findViewById(R.id.chat_main_containerView), false);
                MyAdapterUserAssignmentShow myAdapterUserAssignmentShow = new MyAdapterUserAssignmentShow(this, employeeList, myObject, objectUserArrayList);
                RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.my_recycle_view_user);
                dialogRecyclerView.setAdapter(myAdapterUserAssignmentShow);
                dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
                builder.setView(dialogView);
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    ArrayList assignedUserIdList = new ArrayList();
                    //----changes to DB
                    for(int i = 0; i < employeeList.size(); i++ ){
                        ObjectUser objUsr = employeeList.get(i);
                        if(objUsr.getChecked().equals(true)){
                            assignedUserIdList.add(objUsr.getId());
                        }
                    }
                    new HttpsRequestSetObjectUser(this, myObject, assignedUserIdList).execute();
                });
                builder.setNegativeButton("Cancel", null);
                builder.create();
                builder.show();
            });
        }

        myAdapterMessage = new MyAdapterMessage(messages, this, myUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapterMessage);
        setUpChatRoom();


       /* FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Fetching FCM registration token failed");
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        //System.out.println(token);
                        //Toast.makeText(context, "token " + token, Toast.LENGTH_SHORT).show();
                    }
                });


        FirebaseDatabase.getInstance().getReference("objects/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */
    }

    public boolean isDateToBeDiplayed(String newDate){
        boolean value = false;

        if(currentDate.equals("")){
            setCurrentDate(newDate);
            setDateToDisplay(newDate);
            return value;
        }

        if(!currentDate.equals(newDate)){
            currentDate = newDate;
            value = true;
        }

        return value;
    }

    public String getCurrentDate() {        return currentDate;    }
    public void setCurrentDate(String currentDate) {        this.currentDate = currentDate;    }
    public String getDateToDisplay() {        return dateToDisplay;    }
    public void setDateToDisplay(String dateToDisplay) {        this.dateToDisplay = dateToDisplay;    }
    public ArrayList<ObjectObjUser> getObjectUserArrayList() {        return objectUserArrayList;    }
    public void setObjectUserArrayList(ArrayList<ObjectObjUser> objectUserArrayList) {        this.objectUserArrayList = objectUserArrayList;    }
    public ObjectObject getMyObject() {        return myObject;    }
    public void setMyObject(ObjectObject myObject) {        this.myObject = myObject;    }
    public ArrayList<ObjectUser> getOwnerList() {        return ownerList;    }
    public void setOwnerList(ArrayList<ObjectUser> ownerList) {        this.ownerList = ownerList;    }
    public boolean isNavigationToActivityObjectEdit() {  return doNavigationToActivityObjectEdit;    }
    public void setDoNavigationToActivityObjectEdit(boolean doNavigationToActivityObjectEdit) {        this.doNavigationToActivityObjectEdit = doNavigationToActivityObjectEdit;    }


    private void setUpChatRoom(){
        attachMessageListener(chatRoomId);
    }

    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("objects/" + chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(ObjectMessage.class));
                }
                setCurrentDate("");
                setDateToDisplay("");
                myAdapterMessage.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void sendNotification(String messageBody){

    }
    public void refresh(){
        //--to do header labes darbas + addres
       txtChatParticipantCount.setText("+" + String.valueOf(objectUserArrayList.size()));
       txtChattingAbout.setText(myObject.getObjectName());
       txtChattingAbout2.setText(myObject.getObjectAddress());
    }

    @Override
    protected void onResume() {
        super.onResume();
        new HttpsRequestCheckSessionAlive(this).execute();
        //refresh();
    }

    public void getObjectDetailsAndDisplay(ObjectObject objectToDisplay) {

        new HttpsRequestGetObjectDetails(this, myObject).execute();
    }

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

    class HttpsRequestSetObjectUser extends AsyncTask<String, Void, InputStream> {
        private static final String set_object_user_url = "set_object_user.php";

        private Context context;
        private ObjectObject clickObject;
        private ArrayList assignedUserList;
        Connector connector;

        public HttpsRequestSetObjectUser(Context ctx, ObjectObject obj, ArrayList userIdList){
            context     = ctx;
            clickObject = obj;
            assignedUserList = userIdList;
        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, set_object_user_url);
            connector.addPostParameter("user_id",   Base64.encodeToString(MCrypt.encrypt(String.valueOf(myUser.getId()).getBytes()), Base64.DEFAULT));
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(clickObject.getId().toString().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("date",      Base64.encodeToString(MCrypt.encrypt(HelperDate.get_current_date_mysql().getBytes()), Base64.DEFAULT));
            connector.addPostParameter("user",      Base64.encodeToString(MCrypt.encrypt(assignedUserList.toString().getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            try {
                connector.decodeResponse();
                JSONObject responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String saveStatus = MCrypt.decryptSingle(responseObject.getString("status"));
                String msg        = MCrypt.decryptSingle(responseObject.getString("msg"));
                if (saveStatus.equals("1")) {

                }
                if(msg.equals("")){

                }
            }catch (Exception e){

            }
            new HttpsRequestGetObjectDetails(context, clickObject).execute();
        }
    }

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
                    getObjectDetailsAndDisplay(myObject);
                    //----get object list for calender events sync.
                    //new HttpsRequestGetObjectDetails(context).execute();
                    //findViewById(R.id.progressBar).setVisibility(View.GONE);
                    //enableWholeView(gridLayout);
                }else{
                    //session and last activity deleted in DB, app will log-out
                    Toast.makeText(context, context.getResources().getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(inputStream);
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
                ((ActivityChat)context).setObjectUserArrayList(objUserArrayList);

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
                if (objectObject !=null){
                    ((ActivityChat)context).setMyObject(objectObject);
                }

                if (isNavigationToActivityObjectEdit()) {
                    setDoNavigationToActivityObjectEdit(false);
                    //((ActivityChat)context).setObjectToDisplay(getClickObject());
                    Intent intent = new Intent(context, ActivityObjectEdit.class);
                    intent.putExtra("myUser", myUser);
                    intent.putExtra("objectObject", getClickObject());
                    intent.putParcelableArrayListExtra("listDetails", getObjectDetailsArrayList());
                    intent.putParcelableArrayListExtra("listtUser", getObjectUserArrayList());
                    intent.putParcelableArrayListExtra("listPictures", getObjectPicturesArrayList());
                    context.startActivity(intent);
                }else{
                    refresh();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

}