package com.example.jerrysprendimai;

import android.animation.ObjectAnimator;
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
import java.util.List;

public class MyAdapterObjectShow extends RecyclerView.Adapter<MyAdapterObjectShow.MyViewHolder>{
    Context context;

    List<ObjectObject> myObjectList;
    List<ObjectObject> myObjectListFull;

    ObjectObject clickObject;
    ObjectUser myUser;
    ViewGroup parentView;
    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;

    public MyAdapterObjectShow(Context context, ViewGroup parentView, List<ObjectObject> objectList, ObjectUser user) {
        this.context = context;
        this.myObjectList = objectList;
        this.myObjectListFull = new ArrayList<>(this.myObjectList);
        this.parentView = parentView;
        this.myUser = user;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_object, parent, false);
        return new MyViewHolder(view);
        //return null;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout objectLockLayout;
        LinearLayout myRow;
        LinearLayout bottomSheetContainer;
        TextView objectName, objectCustomer, objectDate, objectNewIndicator;
        ProgressBar progressBar;
        TextView progressBarLabel;
        CardView objectCardView;
        ImageView objectIcon;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            objectCardView       = itemView.findViewById(R.id.object_cardView);
            objectNewIndicator   = itemView.findViewById(R.id.object_new_indicator);
            objectLockLayout     = itemView.findViewById(R.id.object_lock_layout);
            objectName           = itemView.findViewById(R.id.object_name);
            objectCustomer       = itemView.findViewById(R.id.object_customer);
            objectDate           = itemView.findViewById(R.id.object_date);
            progressBar          = itemView.findViewById(R.id.object_progess_bar);
            progressBarLabel     = itemView.findViewById(R.id.object_progess_bar_label);
            bottomSheetContainer = itemView.findViewById(R.id.bottomSheetContainer);
            objectIcon           = itemView.findViewById(R.id.object_image_view);

            myRow = itemView.findViewById(R.id.my_container);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObjectObject myObjectObject = this.myObjectList.get(position);

        holder.objectCardView.setBackgroundResource(R.drawable.card_view_background);

        //-------lock icon
        if(myObjectObject.getLockedByUserId().equals("0")){
            holder.objectLockLayout.setVisibility(View.GONE);
        }else{
            holder.objectLockLayout.setVisibility(View.VISIBLE);
        }

        //-------new indicator
        if(myObjectObject.getNotViewed().equals("X")){
            holder.objectNewIndicator.setVisibility(View.VISIBLE);
        }else{
            holder.objectNewIndicator.setVisibility(View.GONE);
        }


        //------object values
        holder.objectName.setText(myObjectObject.getObjectName());
        holder.objectCustomer.setText(myObjectObject.getCustomerName());
        holder.objectDate.setText(myObjectObject.getDate());
        holder.progressBar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(myObjectObject.getCompleteness())))));
        holder.progressBarLabel.setText(myObjectObject.getCompleteness()+"%");
        holder.objectIcon.setImageResource(context.getResources().getIdentifier(myObjectObject.getIcon(), "drawable", context.getApplicationInfo().packageName));

         //holder.objectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_svg_shower));
        //holder.objectIcon.setImageDrawable(R.mipmap.ic_air_cinditioner);

        if(!((ActivityObjectShow)context).isAnimationShowed()){
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(holder.progressBar, "progress", 0,Integer.parseInt(String.valueOf(Math.round(Double.valueOf(myObjectObject.getCompleteness())))));
            objectAnimator.setDuration(400);
            objectAnimator.start();

            if(position == this.myObjectList.size() - 1){
              ((ActivityObjectShow)context).setAnimationShowed(true);
            }
        }


        if(myObjectObject.getCompleteness().equals("100.0")){
            holder.progressBarLabel.setTextColor(((ActivityObjectShow)context).getResources().getColor(R.color.jerry_green));
        }else{
            holder.progressBarLabel.setTextColor(((ActivityObjectShow)context).getResources().getColor(R.color.jerry_blue));
        }

        //---clear hints
        holder.objectName.setHint("");
        holder.objectCustomer.setHint("");

        //-----card click listener
        holder.myRow.setOnClickListener(v -> {
            ((ActivityObjectShow) context).setNotUserRefresh(true);
            ((ActivityObjectShow) context).setAnimationShowed(true);
            //--todo lock parent view
            clickObject = myObjectObject;
            if(!((ActivityObjectShow) context).isUserMode()){
              //--lock Object in DB
              new HttpsRequestLockObject(context, myObjectObject.getId().toString(),"lock").execute();
            }else{
              if(myObjectObject.getNotViewed().equals("X")){
                myObjectObject.setNotViewed("");
                new HttpsRequestViewObject(context, myObjectObject.getId().toString(), myObjectObject.getNotViewed()).execute();
              }
            }
            new HttpsRequestGetObjectDetails(context, myObjectObject).execute();
        });

    }
    public void requestObjectDetails(Context cntx){
        if (this.clickObject != null) {
            new MyAdapterObjectShow.HttpsRequestGetObjectDetails(cntx, this.clickObject).execute();
        }
    }
    @Override
    public int getItemCount() {
        return this.myObjectList.size();
        //return 0;
    }

    public View getBottomSheetView() {
        return bottomSheetView;
    }
    public void setBottomSheetView(View bottomSheetView) {
        this.bottomSheetView = bottomSheetView;
    }
    public BottomSheetDialog getBottomSheetDialog() {
        return bottomSheetDialog;
    }
    public void setBottomSheetDialog(BottomSheetDialog bottomSheetDialog) {
        this.bottomSheetDialog = bottomSheetDialog;
    }

    class HttpsRequestViewObject extends AsyncTask<String, Void, InputStream> {
        private static final String view_object_url = "view_object.php";

        private Context context;
        private String objectId, notVievedValue;
        Connector connector;

        public HttpsRequestViewObject(Context ctx, String objId, String value){
            context  = ctx;
            objectId = objId;
            notVievedValue = value;
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
                    //implement if needed
                }else{

                }
                ((ActivityObjectShow) context).onRefresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class HttpsRequestLockObject extends AsyncTask<String, Void, InputStream> {
        private static final String lock_object_url = "lock_object.php";

        private Context context;
        private String request;
        private String objectId;
        Connector connector;

        public HttpsRequestLockObject(Context ctx, String objId, String req){
            context  = ctx;
            objectId = objId;
            request  = req;
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
                    //implement if needed
                }else{

                }
                ((ActivityObjectShow) context).onRefresh();
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
            ArrayList<ObjectObject> objectArrayList = new ArrayList<>();

            JSONArray responseObjDetails = new JSONArray();
            JSONArray responseObjUser    = new JSONArray();
            JSONArray responseObjPic     = new JSONArray();
            JSONArray responseEmployee   = new JSONArray();
            JSONArray responseObject     = new JSONArray();
            Integer completeCount = 0;
            try {
                responseObjDetails = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(0));
                responseObjUser    = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(1));
                responseObjPic     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(2));
                responseEmployee   = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(3));
                responseObject     = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(4));
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

            } catch (JSONException e) {
                e.printStackTrace();
            }
                Boolean doCloseRetractable = false;
                BottomSheetDialog bottomSheetDialog = getBottomSheetDialog();
                View bottomSheetView = getBottomSheetView();
                if((bottomSheetDialog == null)||(bottomSheetView == null)) {
                    bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                    bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, parentView, true);
                    bottomSheetDialog.setContentView(bottomSheetView);

                    setBottomSheetDialog(bottomSheetDialog);
                    setBottomSheetView(bottomSheetView);
                    doCloseRetractable = true;
                }
                //----check user mode
                if((!bottomSheetDialog.isShowing())&&(!((ActivityObjectShow)context).isUserMode())){
                   bottomSheetDialog.show();
                }

                //---- fill bottomSheet values
                //bottomSheetView.findViewById(R.id.bottomsheet_top_cardView)).setBackgroundResource(R.drawable.card_view_background);
                (bottomSheetView.findViewById(R.id.bottomsheet_assigne_user_cardView)).setBackgroundResource(R.drawable.card_view_background2);
                ((ImageView) bottomSheetView.findViewById(R.id.bottomsheet_objectIcon)).setImageResource(context.getResources().
                                                                                        getIdentifier(clickObject.getIcon(),
                                                                                                          "drawable",
                                                                                                      context.getApplicationInfo().packageName));
                ((TextView)    bottomSheetView.findViewById(R.id.bottomsheet_objectName)).setText(clickObject.getObjectName());
                ((TextView)    bottomSheetView.findViewById(R.id.bottomsheet_customerName)).setText(clickObject.getCustomerName());
                ((TextView)    bottomSheetView.findViewById(R.id.bottomsheet_assigned_user_label)).setText(String.valueOf(objUserArrayList.size()));
                ((TextView)    bottomSheetView.findViewById(R.id.bottomsheet_objectDate)).setText(String.valueOf(clickObject.getDate()));
                ((TextView)    bottomSheetView.findViewById(R.id.bottomsheet_objectJobs)).setText(String.valueOf(objDetailsArrayList.size()));
                ((TextView)    bottomSheetView.findViewById(R.id.bottomsheet_objectJobsDone)).setText(String.valueOf(completeCount));
                ((TextView)    bottomSheetView.findViewById(R.id.object_progess_bar_label)).setText(String.valueOf(clickObject.getCompleteness()) + "%");
                ((ProgressBar) bottomSheetView.findViewById(R.id.object_progess_bar)).setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(clickObject.getCompleteness())))));

                //---- retractable button/view handling
                LinearLayout retractableLayout     = bottomSheetView.findViewById(R.id.bottomsheet_retractable_layout);
                LinearLayout retractableLayoutLine = bottomSheetView.findViewById(R.id.bottomsheet_retractableLine);
                Button retractableButton           = bottomSheetView.findViewById(R.id.bottomsheet_retractable_button);

                retractableButton.setOnClickListener(v -> {
                    if(retractableLayout.getVisibility()==View.GONE){
                        TransitionManager.beginDelayedTransition(retractableLayout, new AutoTransition());
                        retractableLayout.setVisibility(View.VISIBLE);
                        retractableButton.setBackgroundResource(R.drawable.ic_arrow_up_white);
                    }else{
                        TransitionManager.beginDelayedTransition(retractableLayout, new AutoTransition());
                        retractableLayout.setVisibility(View.GONE);
                        retractableButton.setBackgroundResource(R.drawable.ic_arrow_down_white);
                    }
                });
                retractableLayoutLine.setOnClickListener(v -> retractableButton.performClick());
                if(doCloseRetractable){
                  retractableButton.performClick();
                }

                //---- User assigment recycleView handling
                MyAdapterObjectAsignedUser myAdapterObjectAsignedUser = new MyAdapterObjectAsignedUser(context, objUserArrayList);
                RecyclerView recyclerView = bottomSheetView.findViewById(R.id.my_recycle_view_user_assigned);
                recyclerView.setAdapter(myAdapterObjectAsignedUser);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));

                //---- User assigment button "add" handling
                LinearLayout addUser = bottomSheetView.findViewById(R.id.bottomsheet_add_user_button);
                addUser.setOnClickListener(v -> {
                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_object_user_assignment, parentView, false);
                    MyAdapterUserAssignmentShow myAdapterUserAssignmentShow = new MyAdapterUserAssignmentShow(context, employeeArrayList, clickObject, objUserArrayList);
                    RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.my_recycle_view_user);
                    dialogRecyclerView.setAdapter(myAdapterUserAssignmentShow);
                    dialogRecyclerView.setLayoutManager(new LinearLayoutManager(context));

                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) context, R.style.AlertDialogTheme);
                    builder.setView(dialogView);
                    builder.setPositiveButton("Ok", (dialog, which) -> {
                        ArrayList assignedUserIdList = new ArrayList();
                        //----changes to DB
                        for(int i = 0; i < employeeArrayList.size(); i++ ){
                            ObjectUser objUsr = employeeArrayList.get(i);
                            if(objUsr.getChecked().equals(true)){
                                assignedUserIdList.add(objUsr.getId());
                            }
                        }
                        new HttpsRequestSetObjectUser(context, clickObject, assignedUserIdList).execute();
                    });
                    builder.setNegativeButton("Cancel", null);
                    builder.create();
                    builder.show();
                });

                //---- lock indicator
                LinearLayout lockIndicator = bottomSheetView.findViewById(R.id.bottomsheet_lock_layout);
                TextView lockedUname = bottomSheetView.findViewById(R.id.bottomsheet_lock_label);
                if((clickObject.getLockedByUserId().equals("0"))||(clickObject.getLockedByUserId().equals(String.valueOf(myUser.getId())))){
                    lockIndicator.setVisibility(View.GONE);
                    lockedUname.setVisibility(View.GONE);
                    bottomSheetView.findViewById(R.id.bottomsheet_edit_btn).setEnabled(true);
                    bottomSheetView.findViewById(R.id.bottomsheet_chat_btn).setEnabled(true);
                    bottomSheetView.findViewById(R.id.bottomsheet_delete_btn).setEnabled(true);
                    bottomSheetView.findViewById(R.id.bottomsheet_add_user_button).setEnabled(true);
                    ((CardView) bottomSheetView.findViewById(R.id.bottomsheet_add_user_button_cardView)).setEnabled(true);
                }else{
                    lockIndicator.setVisibility(View.VISIBLE);
                    lockedUname.setText(clickObject.getLockedUname());
                    bottomSheetView.findViewById(R.id.bottomsheet_edit_btn).setEnabled(false);
                    bottomSheetView.findViewById(R.id.bottomsheet_chat_btn).setEnabled(false);
                    bottomSheetView.findViewById(R.id.bottomsheet_delete_btn).setEnabled(false);
                    bottomSheetView.findViewById(R.id.bottomsheet_add_user_button).setEnabled(false);
                    ((CardView) bottomSheetView.findViewById(R.id.bottomsheet_add_user_button_cardView)).setEnabled(false);

                    Toast.makeText(context, context.getResources().getString(R.string.locked_by) +" '" + clickObject.getLockedUname()+"'", Toast.LENGTH_SHORT).show();
                }

                //---- Edit Button click
                FloatingActionButton editButton = bottomSheetView.findViewById(R.id.bottomsheet_edit_btn);
                editButton.setOnClickListener( v ->  {
                    ((ActivityObjectShow)context).setAnimationShowed(false);
                    Intent intent = new Intent(context, ActivityObjectEdit.class);
                    intent.putExtra("myUser", myUser);
                    intent.putExtra("objectObject", getClickObject());
                    intent.putParcelableArrayListExtra("listDetails", getObjectDetailsArrayList());
                    intent.putParcelableArrayListExtra("listUser", getObjectUserArrayList());
                    intent.putParcelableArrayListExtra("listPictures", getObjectPicturesArrayList());
                    context.startActivity(intent);
                });
                //---- Chat Button click
                FloatingActionButton chatButton = bottomSheetView.findViewById(R.id.bottomsheet_chat_btn);
                chatButton.setOnClickListener(v->{
                    ((ActivityObjectShow)context).setAnimationShowed(false);
                    Intent intent = new Intent(context, ActivityChat.class);
                    intent.putExtra("myUser", myUser);
                    intent.putExtra("objectObject", getClickObject());
                    intent.putParcelableArrayListExtra("listUser", getObjectUserArrayList());
                    intent.putParcelableArrayListExtra("employeeList", employeeArrayList);
                    context.startActivity(intent);
                });
                //---- Delete Button click
                FloatingActionButton deleteButton = bottomSheetView.findViewById(R.id.bottomsheet_delete_btn);
                deleteButton.setOnClickListener( v -> {
                    ((ActivityObjectShow)context).setAnimationShowed(false);
                    new HttpsRequestDeleteObject(context, getClickObject()).execute();
                });

                bottomSheetDialog.setOnDismissListener(dialog -> {
                    //clear bottomSheetView
                    //--unlock Object in DB
                    ((ActivityObjectShow) context).setNotUserRefresh(true);
                    ((ActivityObjectShow) context).setAnimationShowed(true);
                    new HttpsRequestLockObject(context, clickObject.getId().toString(),"unlock").execute();
                    //((ActivityObjectShow) context).executeLockUnlock(myObjectObject.getId().toString(),"unlock");
                });
               if(((ActivityObjectShow)context).isUserMode()){
                   editButton.setSoundEffectsEnabled(false);
                   editButton.performClick();
                   editButton.setSoundEffectsEnabled(true);
               }
        }

    }

    class HttpsRequestDeleteObject extends AsyncTask<String, Void, InputStream> {
        private static final String delete_object_url = "delete_object.php";

        private Context context;
        private ObjectObject clickObject;
        private ArrayList assignedUserList;
        Connector connector;


        public HttpsRequestDeleteObject(Context ctx, ObjectObject obj){
            context     = ctx;
            clickObject = obj;

        }
        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, delete_object_url);
            connector.addPostParameter("objectId", MCrypt2.encodeToString(String.valueOf(clickObject.getId())));
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
                String objId      = MCrypt.decryptSingle(responseObject.getString("objectId"));
                if (saveStatus.equals("1")) {
                    if(bottomSheetDialog != null){
                        bottomSheetDialog.dismiss();
                    }
                    Toast.makeText(context, "Ištrinta", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Klaida", Toast.LENGTH_SHORT).show();
                    //error handling?
                }

            }catch (Exception e){
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
}
