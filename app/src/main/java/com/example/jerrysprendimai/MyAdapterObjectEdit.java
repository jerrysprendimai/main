package com.example.jerrysprendimai;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MyAdapterObjectEdit extends RecyclerView.Adapter<MyAdapterObjectEdit.MyViewHolder> {
        Context context;

        ArrayList<ObjectObjDetails> myObjectList;
        ArrayList<ObjectObjDetails> myObjectListFull;
        ObjectObjDetails myObjectObjDetails;
        ObjectUser myUser;
        ViewGroup parentView;
        MyViewHolder myHolder;

    public MyAdapterObjectEdit(Context context, ViewGroup parentView, ArrayList<ObjectObjDetails> objectList, ObjectUser user) {
        this.context = context;
        this.myObjectList = objectList;
        this.myObjectListFull = new ArrayList<>(this.myObjectList);
        this.parentView = parentView;
        this.myUser = user;
    }
    @NonNull
    @Override
    public MyAdapterObjectEdit.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_object_detail, parent, false);
        return new MyAdapterObjectEdit.MyViewHolder(view);
        //return null;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout objectDetailLockLayout;
        LinearLayout myRow;

        LinearLayout lockLayout, lockLayoutExtended, layoutSummary, layoutExtended, retractableButtonLayout, getRetractableButtonLayoutExtended;
        ImageView oDJobDoneImg, oDJobDoneImgExtended;
        TextView oDJobName, oDCompletedJobLabel;
        TextInputEditText oDJobNameExtended, oDJobDescriptionExtended;
        SwitchCompat oDCompleteJob;
        Button oDRetractableButton, oDRetractableButtonExtended;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            layoutSummary     = itemView.findViewById(R.id.objectDetails_summary_line);
            layoutExtended    = itemView.findViewById(R.id.objectDetails_summary_expanded);

            retractableButtonLayout = itemView.findViewById(R.id.objectDetails_retractable_button_layout);
            getRetractableButtonLayoutExtended = itemView.findViewById(R.id.objectDetails_retractable_button_layout_extended);

            lockLayout         = itemView.findViewById(R.id.objectDetails_lock_layout_visible);
            lockLayoutExtended = itemView.findViewById(R.id.objectDetails_lock_layout_extended);

            oDJobDoneImg         = itemView.findViewById(R.id.objectDetails_image_view);
            oDJobDoneImgExtended = itemView.findViewById(R.id.objectDetails_image_view_extended);

            oDJobName         = itemView.findViewById(R.id.objectDetails_jobName);
            oDJobNameExtended =  itemView.findViewById(R.id.objectDetails_jobName_extended);
            oDJobDescriptionExtended = itemView.findViewById(R.id.objectDetail_jobDescription_extended);

            oDRetractableButton         = itemView.findViewById(R.id.objectDetails_retractable_button);
            oDRetractableButtonExtended = itemView.findViewById(R.id.objectDetails_retractable_button_extended);

            oDCompleteJob = itemView.findViewById(R.id.objectDetails_switchButton);
            oDCompletedJobLabel = itemView.findViewById(R.id.objectDetails_switchButton_label);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        setMyHolder(holder);
        setMyObjectObjDetails(this.myObjectList.get(position));
        ObjectObjDetails myObjectObjDetails = this.myObjectList.get(position);

        holder.lockLayout.setVisibility(View.GONE);
        holder.lockLayoutExtended.setVisibility(View.GONE);
        if(myObjectObjDetails.getCompleted().equals("X")){
            holder.oDJobDoneImg.setVisibility(View.VISIBLE);
            holder.oDJobDoneImgExtended.setVisibility(View.VISIBLE);
            holder.oDCompletedJobLabel.setVisibility(View.VISIBLE);
            holder.oDCompleteJob.setChecked(true);
        }else{
            holder.oDJobDoneImg.setVisibility(View.GONE);
            holder.oDJobDoneImgExtended.setVisibility(View.GONE);
            holder.oDCompletedJobLabel.setVisibility(View.GONE);
            holder.oDCompleteJob.setChecked(false);
        }

        holder.oDJobName.setText(myObjectObjDetails.getName());
        holder.oDJobNameExtended.setText(myObjectObjDetails.getName());
        holder.oDJobDescriptionExtended.setText(myObjectObjDetails.getDescription());

        if(holder.layoutSummary.getVisibility() == View.VISIBLE){
            holder.layoutExtended.setVisibility(View.GONE);
            holder.getRetractableButtonLayoutExtended.setVisibility(View.GONE);
        }

        holder.oDRetractableButton.setOnClickListener(v -> {
            if((holder.layoutSummary.getVisibility() == View.VISIBLE)&&(holder.layoutExtended.getVisibility() == View.GONE)){
                TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
                //TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());
                holder.layoutExtended.setVisibility(View.VISIBLE);
                holder.getRetractableButtonLayoutExtended.setVisibility(View.VISIBLE);
                holder.layoutSummary.setVisibility(View.GONE);
                holder.retractableButtonLayout.setVisibility(View.GONE);
            }
        });
        holder.oDRetractableButtonExtended.setOnClickListener(v -> {
            if((holder.layoutSummary.getVisibility() == View.GONE)&&(holder.layoutExtended.getVisibility() == View.VISIBLE)){
                //TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
                TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());
                holder.layoutExtended.setVisibility(View.GONE);
                holder.getRetractableButtonLayoutExtended.setVisibility(View.GONE);
                holder.layoutSummary.setVisibility(View.VISIBLE);
                holder.retractableButtonLayout.setVisibility(View.VISIBLE);
            }
        });
        holder.layoutSummary.setSoundEffectsEnabled(false);
        holder.layoutSummary.setOnClickListener(v -> {
            holder.oDRetractableButton.performClick();
        });
        holder.oDCompleteJob.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked == true){
                holder.oDJobDoneImg.setVisibility(View.VISIBLE);
                holder.oDJobDoneImgExtended.setVisibility(View.VISIBLE);
                holder.oDCompletedJobLabel.setVisibility(View.VISIBLE);
            }else{
                holder.oDJobDoneImg.setVisibility(View.GONE);
                holder.oDJobDoneImgExtended.setVisibility(View.GONE);
                holder.oDCompletedJobLabel.setVisibility(View.GONE);
            }
        });

        holder.oDJobNameExtended.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!myObjectObjDetails.getName().equals(holder.oDJobNameExtended.getText().toString())){
                    holder.oDJobName.setText(holder.oDJobNameExtended.getText().toString());
                    myObjectObjDetails.setName(holder.oDJobNameExtended.getText().toString());
                    ((ActivityObjectEdit)context).setNeedSave(true);
                }
                if(((ActivityObjectEdit)context).isNeedSave() == true ){
                    ((ActivityObjectEdit)context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                }
            }
        });
        holder.oDJobDescriptionExtended.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!myObjectObjDetails.getDescription().equals(holder.oDJobDescriptionExtended.getText().toString())){
                    myObjectObjDetails.setName(holder.oDJobDescriptionExtended.getText().toString());
                    ((ActivityObjectEdit)context).setNeedSave(true);
                }
                if(((ActivityObjectEdit)context).isNeedSave() == true ){
                    ((ActivityObjectEdit)context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.myObjectList.size();
        //return 0;
    }

    public ObjectObjDetails getMyObjectObjDetails() {
        return myObjectObjDetails;
    }

    public void setMyObjectObjDetails(ObjectObjDetails myObjectObjDetails) {
        this.myObjectObjDetails = myObjectObjDetails;
    }

    public MyViewHolder getMyHolder() {
        return myHolder;
    }

    public void setMyHolder(MyViewHolder myHolder) {
        this.myHolder = myHolder;
    }
}
