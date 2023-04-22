package com.example.jerrysprendimai;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
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
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyAdapterObjectShowP1 extends RecyclerView.Adapter<MyAdapterObjectShowP1.MyViewHolder>{
    Context context;

    List<ObjectObject> myObjectList;
    List<ObjectObject> myObjectListFull;

    final String USER = "user";
    final String OWNER = "owner";
    final String ADMIN = "admin";

    ObjectObject clickObject;
    ObjectUser myUser;
    ViewGroup parentView;
    BottomSheetDialog bottomSheetDialog;
    View bottomSheetView;
    View.OnClickListener onClickListener;

    public MyAdapterObjectShowP1(Context context, ViewGroup parentView, List<ObjectObject> objectList, ObjectUser user, View.OnClickListener onClickListener) {
        this.context = context;
        this.myObjectList = objectList;
        this.myObjectListFull = new ArrayList<>(this.myObjectList);
        this.parentView = parentView;
        this.myUser = user;
        this.onClickListener = onClickListener;
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
        LinearLayout objectLockLayout, myRow, bottomSheetContainer, objectCompletnessProgress;
        TextView objectName, objectCustomer, objectDate, objectNewIndicator;
        ProgressBar progressBar;
        TextView progressBarLabel, objectNameLabel, customerNameLabel, objectDateLabel;
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
            objectCompletnessProgress = itemView.findViewById(R.id.object_completness_progressBar_linearLayout);
            objectNameLabel      = itemView.findViewById(R.id.object_name_label);
            customerNameLabel    = itemView.findViewById(R.id.object_customer_label);
            objectDateLabel      = itemView.findViewById(R.id.object_date_label);

            myRow = itemView.findViewById(R.id.my_container);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObjectObject myObjectObject = this.myObjectList.get(position);

        //yObjectObject.setMyViewHolder(holder);
        holder.objectCardView.setBackgroundResource(R.drawable.card_view_background);

        //-------lock icon
        holder.objectLockLayout.setVisibility(View.GONE);


        //-------new indicator
        if(myObjectObject.getNotViewed().equals("X")){
            holder.objectNewIndicator.setVisibility(View.VISIBLE);
        }else{
            holder.objectNewIndicator.setVisibility(View.GONE);
        }

        //------object values
        holder.objectName.setText(myObjectObject.getObjectName());
        holder.objectName.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.objectNameLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.objectCustomer.setText(myObjectObject.getCustomerName());
        holder.objectCustomer.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.customerNameLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.objectDate.setText(myObjectObject.getDate());
        holder.objectDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);
        holder.objectDateLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP,11);

        //holder.progressBar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(myObjectObject.getCompleteness())))));
        //holder.progressBarLabel.setText(myObjectObject.getCompleteness()+"%");
        holder.objectIcon.setImageResource(context.getResources().getIdentifier(myObjectObject.getIcon(), "drawable", context.getApplicationInfo().packageName));

        holder.objectCompletnessProgress.setVisibility(View.GONE);

        //holder.objectIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_svg_shower));
        //holder.objectIcon.setImageDrawable(R.mipmap.ic_air_cinditioner);

        /*if(!((ActivityObjectShow)context).isAnimationShowed()){
            ObjectAnimator objectAnimator = ObjectAnimator.ofInt(holder.progressBar, "progress", 0,Integer.parseInt(String.valueOf(Math.round(Double.valueOf(myObjectObject.getCompleteness())))));
            objectAnimator.setDuration(400);
            objectAnimator.start();

            if(position == this.myObjectList.size() - 1){
              ((ActivityObjectShow)context).setAnimationShowed(true);
            }
        }

        if(myObjectObject.getCompleteness().equals("100.0")){
            holder.progressBarLabel.setTextColor(((ActivityOrder1)context).getResources().getColor(R.color.jerry_green));
        }else{
            holder.progressBarLabel.setTextColor(((ActivityOrder1)context).getResources().getColor(R.color.jerry_blue));
        }*/

        //---clear hints
        holder.objectName.setHint("");
        holder.objectCustomer.setHint("");

        View.OnClickListener clickListener = null;
        if(this.onClickListener == null){
            clickListener = v->{
              ((ActivityOrder1)context).objectSelectedCallback(holder.getAdapterPosition());
            };
        }else{
            clickListener = this.onClickListener;
        }
        //-----card click listener
        holder.myRow.setOnClickListener(clickListener);
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

}