package com.example.jerrysprendimai;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

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
    ObjectUser myUser;
    ViewGroup parentView;

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
        TextView objectName;
        TextView objectCustomer;
        ProgressBar progressBar;
        TextView progressBarLabel;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            objectLockLayout     = itemView.findViewById(R.id.object_lock_layout);
            objectName           = itemView.findViewById(R.id.object_name);
            objectCustomer       = itemView.findViewById(R.id.object_customer);
            progressBar          = itemView.findViewById(R.id.object_progess_bar);
            progressBarLabel     = itemView.findViewById(R.id.object_progess_bar_label);
            bottomSheetContainer = itemView.findViewById(R.id.bottomSheetContainer);

            myRow = itemView.findViewById(R.id.my_container);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObjectObject myObjectObject = this.myObjectList.get(position);

        //-------lock icon
        if(myObjectObject.getLockedByUserId().equals("0")){
            holder.objectLockLayout.setVisibility(View.GONE);
        }else{
            holder.objectLockLayout.setVisibility(View.VISIBLE);
        }

        //------object values
        holder.objectName.setText(myObjectObject.getObjectName());
        holder.objectCustomer.setText(myObjectObject.getCustomerName());
        holder.progressBar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(myObjectObject.getCompleteness())))));
        holder.progressBarLabel.setText(myObjectObject.getCompleteness()+"%");

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
            //--todo lock parent view

            //--lock Object in DB
            new MyAdapterObjectShow.HttpsRequestLockObject(context, myObjectObject.getId().toString(),"lock").execute();
            new MyAdapterObjectShow.HttpsRequestGetObjectDetails(context, myObjectObject).execute();

        });

    }

    @Override
    public int getItemCount() {
        return this.myObjectList.size();
        //return 0;
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
                    //--todo schow "locked" bottomScheet
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
        Connector connector;

        public HttpsRequestGetObjectDetails(Context ctx, ObjectObject obj){
            context     = ctx;
            clickObject = obj;
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
            Integer completeCount = 0;
            try {
                JSONArray responseObjDetails = MCrypt.decryptJSONArray((JSONArray) connector.getResultJsonArray().get(0));
                JSONArray responseObjUser    = MCrypt.decryptJSONArray((JSONArray)(JSONArray) connector.getResultJsonArray().get(1));
                JSONArray responseObjPic     = MCrypt.decryptJSONArray((JSONArray)(JSONArray) connector.getResultJsonArray().get(2));

                for(int i = 0; i < responseObjDetails.length(); i++){
                    ObjectObjDetails objectObjDetails = new ObjectObjDetails((JSONObject) responseObjDetails.get(i));
                    objDetailsArrayList.add(objectObjDetails);
                    if(objectObjDetails.getCompleted().equals("X")){
                       completeCount += 1;
                    }
                }
                for(int i = 0; i < responseObjUser.length(); i++){
                    ObjectObjUser objectObjUser = new ObjectObjUser((JSONObject) responseObjUser.get(i));
                    objUserArrayList.add(objectObjUser);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, parentView, true);

                //---- fill bottomSheet values
                ((TextView) bottomSheetView.findViewById(R.id.bottomsheet_objectName)).setText(clickObject.getObjectName());
                ((TextView) bottomSheetView.findViewById(R.id.bottomsheet_customerName)).setText(clickObject.getCustomerName());
                ((TextView) bottomSheetView.findViewById(R.id.bottomsheet_assigned_user_label)).setText(String.valueOf(objUserArrayList.size()));
                ((TextView) bottomSheetView.findViewById(R.id.bottomsheet_objectDate)).setText(String.valueOf(clickObject.getDate()));
                ((TextView) bottomSheetView.findViewById(R.id.bottomsheet_objectJobs)).setText(String.valueOf(objDetailsArrayList.size()));
                ((TextView) bottomSheetView.findViewById(R.id.bottomsheet_objectJobsDone)).setText(String.valueOf(completeCount));
                ((TextView) bottomSheetView.findViewById(R.id.object_progess_bar_label)).setText(String.valueOf(clickObject.getCompleteness()));
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
                retractableButton.performClick();

                //---- lock indicator
                LinearLayout lockIndicator = bottomSheetView.findViewById(R.id.bottomsheet_lock_layout);
                TextView lockedUname = bottomSheetView.findViewById(R.id.bottomsheet_lock_label);
                if((clickObject.getLockedByUserId().equals("0"))||(clickObject.getLockedByUserId().equals(String.valueOf(myUser.getId())))){
                    lockIndicator.setVisibility(View.GONE);
                    lockedUname.setVisibility(View.GONE);
                    bottomSheetView.findViewById(R.id.bottomsheet_edit_btn).setEnabled(true);
                    bottomSheetView.findViewById(R.id.bottomsheet_pdf_btn).setEnabled(true);
                    bottomSheetView.findViewById(R.id.bottomsheet_delete_btn).setEnabled(true);
                    bottomSheetView.findViewById(R.id.bottomsheet_add_user_button).setEnabled(true);
                    ((CardView) bottomSheetView.findViewById(R.id.bottomsheet_add_user_button_cardView)).setEnabled(true);
                }else{
                    lockIndicator.setVisibility(View.VISIBLE);
                    lockedUname.setText(clickObject.getLockedUname());
                    bottomSheetView.findViewById(R.id.bottomsheet_edit_btn).setEnabled(false);
                    bottomSheetView.findViewById(R.id.bottomsheet_pdf_btn).setEnabled(false);
                    bottomSheetView.findViewById(R.id.bottomsheet_delete_btn).setEnabled(false);
                    bottomSheetView.findViewById(R.id.bottomsheet_add_user_button).setEnabled(false);
                    ((CardView) bottomSheetView.findViewById(R.id.bottomsheet_add_user_button_cardView)).setEnabled(false);

                    Toast.makeText(context, context.getResources().getString(R.string.locked_by) +" '" + clickObject.getLockedUname()+"'", Toast.LENGTH_SHORT).show();
                }

                bottomSheetDialog.setOnDismissListener(dialog -> {
                    //--unlock Object in DB
                    new HttpsRequestLockObject(context, clickObject.getId().toString(),"unlock").execute();
                    //((ActivityObjectShow) context).executeLockUnlock(myObjectObject.getId().toString(),"unlock");
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
        }

    }
}
