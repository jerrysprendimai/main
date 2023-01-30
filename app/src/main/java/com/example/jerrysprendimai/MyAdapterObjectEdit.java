package com.example.jerrysprendimai;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jerrysprendimai.interfaces.OnIntentReceived;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.ArrayList;

public class MyAdapterObjectEdit extends RecyclerView.Adapter<MyAdapterObjectEdit.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback, OnIntentReceived {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int GALLERY_REQ_CODE = 1000;

        Context context;
        ArrayList<ObjectObjDetails> myObjectList;
        ArrayList<ObjectObjDetails> myObjectListFull;
        ArrayList<ObjectObjPic> myObjectListPic;

        ArrayList<MyAdapterObjectEdit.MyViewHolder> myViewHolderList;
        ObjectObjDetails myObjectObjDetails;
        ObjectUser myUser;
        ViewGroup parentView;
        MyViewHolder myHolder;
        Boolean deletionMode;
        ArrayList<ObjectObjPic> toBeDeletedList;

    public MyAdapterObjectEdit(Context context, ViewGroup parentView, ArrayList<ObjectObjDetails> objectList, ObjectUser user, ArrayList<ObjectObjPic> pictureList) {
        this.context = context;
        this.myObjectList = objectList;
        this.myObjectListPic = pictureList;
        this.myObjectListFull = new ArrayList<>(this.myObjectList);
        this.parentView = parentView;
        this.myUser = user;
        this.myViewHolderList = new ArrayList<>();
        this.toBeDeletedList = new ArrayList<>();
    }
    @NonNull
    @Override
    public MyAdapterObjectEdit.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_object_detail, parent, false);
        //this.myViewHolderList.add(currentViewHolder);
        return new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        Integer pos;
        LinearLayout lockLayout, lockLayoutExtended, layoutSummary, layoutExtended, retractableButtonLayout,
                     getRetractableButtonLayoutExtended, oDAddFotoButton, oDTakeFotoButton, oDDeleteFotoButton,
                     addButtonsLayout, deleteButtonsLayout, oDDeleteCancel;
        ImageView oDJobDoneImg, oDJobDoneImgExtended, oDJobDeleteImg, oDJobDeleteImgExtended;
        TextView oDJobName, oDCompletedJobLabel;
        TextInputEditText oDJobNameExtended, oDJobDescriptionExtended;
        RecyclerView oDFotoRecycleView;
        SwitchCompat oDCompleteJob;
        Button oDRetractableButton, oDRetractableButtonExtended;
        EditText oDfocusHolder;
        boolean myHoldIndicator;
        MyAdapterObjectEditPicture myAdapterObjectEditPicture;
        ArrayList<ObjectObjPic> filteredPics;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            layoutSummary                      = itemView.findViewById(R.id.objectDetails_summary_line);
            layoutExtended                     = itemView.findViewById(R.id.objectDetails_summary_expanded);
            retractableButtonLayout            = itemView.findViewById(R.id.objectDetails_retractable_button_layout);
            getRetractableButtonLayoutExtended = itemView.findViewById(R.id.objectDetails_retractable_button_layout_extended);
            lockLayout                         = itemView.findViewById(R.id.objectDetails_lock_layout_visible);
            lockLayoutExtended                 = itemView.findViewById(R.id.objectDetails_lock_layout_extended);
            oDJobDoneImg                       = itemView.findViewById(R.id.objectDetails_image_view);
            oDJobDoneImgExtended               = itemView.findViewById(R.id.objectDetails_image_view_extended);
            oDJobDeleteImg                     = itemView.findViewById(R.id.objectDetails_delete_image_view);
            oDJobDeleteImgExtended             = itemView.findViewById(R.id.objectDetails_delete_image_view_extended);
            oDJobName                          = itemView.findViewById(R.id.objectDetails_jobName);
            oDJobNameExtended                  = itemView.findViewById(R.id.objectDetails_jobName_extended);
            oDJobDescriptionExtended           = itemView.findViewById(R.id.objectDetail_jobDescription_extended);
            oDRetractableButton                = itemView.findViewById(R.id.objectDetails_retractable_button);
            oDRetractableButtonExtended        = itemView.findViewById(R.id.objectDetails_retractable_button_extended);
            oDCompleteJob                      = itemView.findViewById(R.id.objectDetails_switchButton);
            oDCompletedJobLabel                = itemView.findViewById(R.id.objectDetails_switchButton_label);
            oDfocusHolder                      = itemView.findViewById(R.id.objectDetails_invisibleFocusHolder);
            oDFotoRecycleView                  = itemView.findViewById(R.id.objectDetails_foto_recycleView_extended);
            oDAddFotoButton                   = itemView.findViewById(R.id.objectDetails_add_picture_button_extended);
            oDTakeFotoButton                   = itemView.findViewById(R.id.objectDetails_take_picture_button_extended);
            oDDeleteFotoButton                 = itemView.findViewById(R.id.objectDetails_delete_picture_button_extended);
            oDDeleteCancel                     = itemView.findViewById(R.id.objectDetails_delete_cancel_picture_button_extended);
            addButtonsLayout                   = itemView.findViewById(R.id.objectDetails_add_picture_button_layoutButtons);
            deleteButtonsLayout                = itemView.findViewById(R.id.objectDetails_delete_picture_button_layoutButtons);
            filteredPics = new ArrayList<>();
        }
        public void setPos(int position){
            pos = position;
        }
        public boolean isMyHoldIndicator() {
            return myHoldIndicator;
        }
        public void setMyHoldIndicator(boolean myHoldIndicator) {
            this.myHoldIndicator = myHoldIndicator;
        }
        public void setDeletionModeButtons(boolean value){
            if(value){
                addButtonsLayout.setVisibility(View.GONE);
                deleteButtonsLayout.setVisibility(View.VISIBLE);
            }else{
                addButtonsLayout.setVisibility(View.VISIBLE);
                deleteButtonsLayout.setVisibility(View.GONE);
            }
        }

    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        setMyHolder(holder);
        setMyObjectObjDetails(this.myObjectList.get(holder.getAdapterPosition()));
        holder.setPos(holder.getAdapterPosition());
        ObjectObjDetails myObjectObjDetails = this.myObjectList.get(holder.getAdapterPosition());

        holder.lockLayout.setVisibility(View.GONE);
        holder.lockLayoutExtended.setVisibility(View.GONE);
        TextWatcher emptyTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        CompoundButton.OnCheckedChangeListener emptyStateListener = (buttonView, isChecked) -> {};
        holder.oDCompleteJob.setOnCheckedChangeListener(emptyStateListener);
        holder.oDJobNameExtended.addTextChangedListener(emptyTextWatcher);
        holder.oDJobDescriptionExtended.addTextChangedListener(emptyTextWatcher);

        holder.oDJobDeleteImg.setVisibility(View.GONE);
        holder.oDJobDeleteImgExtended.setVisibility(View.GONE);

        if(myObjectObjDetails.getCompleted().equals("X")){
            holder.oDJobDoneImg.setVisibility(View.VISIBLE);
            holder.oDJobDoneImgExtended.setVisibility(View.VISIBLE);
            holder.oDCompletedJobLabel.setVisibility(View.VISIBLE);
            if(!holder.oDCompleteJob.isChecked()) {
                holder.oDCompleteJob.setChecked(true);
            }
        }else{
            holder.oDJobDoneImg.setVisibility(View.GONE);
            holder.oDJobDoneImgExtended.setVisibility(View.GONE);
            holder.oDCompletedJobLabel.setVisibility(View.GONE);
            if(holder.oDCompleteJob.isChecked()) {
               holder.oDCompleteJob.setChecked(false);
            }
        }

        holder.oDJobName.setText(myObjectObjDetails.getName());
        holder.oDJobNameExtended.setText(myObjectObjDetails.getName());
        holder.oDJobDescriptionExtended.setText(myObjectObjDetails.getDescription());

        //TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
        //TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());
        if(holder.layoutSummary.getVisibility() == View.VISIBLE){
            holder.layoutExtended.setVisibility(View.GONE);
            holder.getRetractableButtonLayoutExtended.setVisibility(View.GONE);
        }

        TextWatcher myTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!myObjectList.get(holder.getAdapterPosition()).getName().equals(holder.oDJobNameExtended.getText().toString())){
                    holder.oDJobName.setText(holder.oDJobNameExtended.getText().toString());
                    myObjectList.get(holder.getAdapterPosition()).setName(holder.oDJobNameExtended.getText().toString());
                    ((ActivityObjectEdit)context).setNeedSave(true);
                }
                if(!myObjectList.get(holder.getAdapterPosition()).getDescription().equals(holder.oDJobDescriptionExtended.getText().toString())){
                    myObjectList.get(holder.getAdapterPosition()).setDescription(holder.oDJobDescriptionExtended.getText().toString());
                    ((ActivityObjectEdit)context).setNeedSave(true);
                }
                if(((ActivityObjectEdit) context).isNeedSave()){
                    ((ActivityObjectEdit)context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                }
            }
        };
        CompoundButton.OnCheckedChangeListener myOnCheckedChangeListener = (buttonView, isChecked) -> {
            if((isChecked)&&(!myObjectList.get(holder.getAdapterPosition()).getCompleted().equals("X"))){
                ((ActivityObjectEdit)context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                myObjectList.get(holder.getAdapterPosition()).setCompleted("X");
                holder.oDJobDoneImg.setVisibility(View.VISIBLE);
                holder.oDJobDoneImgExtended.setVisibility(View.VISIBLE);
                holder.oDCompletedJobLabel.setVisibility(View.VISIBLE);
            }
            if((!isChecked)&&(!myObjectList.get(holder.getAdapterPosition()).getCompleted().equals(""))) {
                ((ActivityObjectEdit) context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                myObjectList.get(holder.getAdapterPosition()).setCompleted("");
                holder.oDJobDoneImg.setVisibility(View.GONE);
                holder.oDJobDoneImgExtended.setVisibility(View.GONE);
                holder.oDCompletedJobLabel.setVisibility(View.GONE);
            }
        };
        holder.oDCompleteJob.setOnCheckedChangeListener(myOnCheckedChangeListener);
        holder.oDRetractableButton.setOnClickListener(v -> {
            if(((ActivityObjectEdit)context).getDeletionMode().equals(false)){
                if((holder.layoutSummary.getVisibility() == View.VISIBLE)&&(holder.layoutExtended.getVisibility() == View.GONE)){
                    TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
                    //TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());
                    holder.layoutExtended.setVisibility(View.VISIBLE);
                    holder.getRetractableButtonLayoutExtended.setVisibility(View.VISIBLE);
                    holder.layoutSummary.setVisibility(View.GONE);
                    holder.retractableButtonLayout.setVisibility(View.GONE);

                    holder.oDJobNameExtended.addTextChangedListener(myTextWatcher);
                    holder.oDJobDescriptionExtended.addTextChangedListener(myTextWatcher);

                    ((ActivityObjectEdit)context).scrollToPosition();
                }
            }else{
                if(!holder.isMyHoldIndicator()) {
                    if (holder.oDJobDeleteImg.getVisibility() == View.GONE) {
                        holder.oDJobDeleteImg.setVisibility(View.VISIBLE);
                        ((ActivityObjectEdit) context).addToBeDeleted(position);
                    } else {
                        holder.oDJobDeleteImg.setVisibility(View.GONE);
                        ((ActivityObjectEdit) context).removeToBeDeleted(position);
                    }
                }else{
                    holder.setMyHoldIndicator(false);
                }
            }
        });
        holder.oDRetractableButton.setOnLongClickListener(v ->{
            ((ActivityObjectEdit)context).setDeletionMode(true);
            ((ActivityObjectEdit)context).setDeleteButtonVisibility(true);
            ((ActivityObjectEdit)context).setSaveCancelVisibility(false);
            ((ActivityObjectEdit)context).addToBeDeleted(position);
            holder.setMyHoldIndicator(true);
            holder.oDJobDeleteImg.setVisibility(View.VISIBLE);
            return false;
        });
        holder.oDRetractableButtonExtended.setOnClickListener(v -> {
            if((holder.layoutSummary.getVisibility() == View.GONE)&&(holder.layoutExtended.getVisibility() == View.VISIBLE)){
                TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
                //TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());
                holder.layoutExtended.setVisibility(View.GONE);
                holder.getRetractableButtonLayoutExtended.setVisibility(View.GONE);
                holder.layoutSummary.setVisibility(View.VISIBLE);
                holder.retractableButtonLayout.setVisibility(View.VISIBLE);

                holder.oDJobNameExtended.removeTextChangedListener(myTextWatcher);
                holder.oDJobDescriptionExtended.removeTextChangedListener(myTextWatcher);
                //holder.oDCompleteJob.setOnCheckedChangeListener(emptyStateListener);

                holder.oDfocusHolder.setInputType(InputType.TYPE_NULL);
                holder.oDfocusHolder.requestFocus();

                ((ActivityObjectEdit)context).hideSoftKeyboard();
            }
        });
        holder.layoutSummary.setSoundEffectsEnabled(false);
        holder.layoutSummary.setOnClickListener(v -> holder.oDRetractableButton.performClick());
        holder.layoutSummary.setOnLongClickListener(v -> {
            holder.oDRetractableButton.performLongClick();
            return false;
        });

        //----picture handling
        holder.filteredPics = new ArrayList<>();
        holder.filteredPics.addAll(myObjectListPic);
        for(int i = holder.filteredPics.size()-1; i>=0 ; i--){
            ObjectObjPic objectObjPic = holder.filteredPics.get(i);
            if(!objectObjPic.getPosNr().equals(myObjectObjDetails.getPosNr())){
                holder.filteredPics.remove(objectObjPic);
            }
        }
        holder.setDeletionModeButtons(false);
        setDeletionMode(false);
        holder.myAdapterObjectEditPicture = new MyAdapterObjectEditPicture(context, this, holder, parentView, holder.filteredPics, myUser);
        holder.oDFotoRecycleView.setAdapter(holder.myAdapterObjectEditPicture);
        holder.oDFotoRecycleView.setLayoutManager(new GridLayoutManager(context, 3));
        //holder.oDFotoRecycleView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        //---------delete cancel handler
        holder.oDDeleteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeletionMode(false);
                holder.setDeletionModeButtons(false);
                holder.myAdapterObjectEditPicture.notifyDataSetChanged();
            }
        });
        //---------delete handler
        holder.oDDeleteFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //----Delete it from whole Picture ArrayList + delete from filtered ArrayList
                for(int i=0; i<toBeDeletedList.size();i++){
                    holder.filteredPics.remove(toBeDeletedList.get(i));
                }
                for(int i=0; i<toBeDeletedList.size();i++){
                    myObjectListPic.remove(toBeDeletedList.get(i));
                }
                toBeDeletedList = new ArrayList<>();
                setDeletionMode(false);
                holder.setDeletionModeButtons(false);
                holder.myAdapterObjectEditPicture.notifyDataSetChanged();
            }
        });
        //----------add picture handler
        MyAdapterObjectEdit thisInstance = this;
        holder.oDAddFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActivityObjectEdit)context).setCallbackAdapterReference(thisInstance, holder);
                //check permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        ((ActivityObjectEdit)context).requestPermissions(permissions, PERMISSION_CODE);
                    }else{
                        //permission already granted
                        pickImageFromGallery();
                    }
                }else{
                    //system os is less that marshmallow
                    pickImageFromGallery();
                }

            }

        });

    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, MyViewHolder holder, int resultOk){
        if(resultCode == resultOk && requestCode == IMAGE_PICK_CODE){
            //set image to image view
            Uri filePath = data.getData();
            ObjectObjPic newPic = new ObjectObjPic();
            newPic.setPicUri(filePath.toString());
            newPic.setObjectId(((ActivityObjectEdit)context).objectObject.getId());
            newPic.setPosNr(holder.getAdapterPosition());
            try {
                newPic.setImageResource(MediaStore.Images.Media.getBitmap(context.getContentResolver(), filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            myObjectListPic.add(newPic);
            holder.filteredPics.add(newPic);
            holder.myAdapterObjectEditPicture.notifyDataSetChanged();
        }
    }
    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        ((ActivityObjectEdit)context).startActivityForResult(intent, IMAGE_PICK_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted
                    pickImageFromGallery();
                }else{
                    //permission denied
                    Toast.makeText(context, "Atšaukta", Toast.LENGTH_SHORT).show();
                }
        }
    }
    @Override
    public void onIntent(Intent i, int resultCode) {
       // TODO: Handle here
    }

    @Override
    public int getItemCount() {
        return this.myObjectList.size();
        //return 0;
    }


    public void removeToBeDeleted(ObjectObjPic objectObjPic){
        for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(objectObjPic)){
                this.toBeDeletedList.remove(i);
                break;
            }
        }
        if(this.toBeDeletedList.size() == 0){
            setDeletionMode(false);
            getMyHolder().setDeletionModeButtons(false);
            getMyHolder().myAdapterObjectEditPicture.notifyDataSetChanged();
        }
    }
    public void addToBeDeleted(ObjectObjPic objectObjPic){
        boolean found = false;
        for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(objectObjPic)){
                found = true;
                break;
            }
        }
        if(!found){
            this.toBeDeletedList.add(objectObjPic);
        }
    }

    public void setMyHolder(MyViewHolder myHolder) {
        this.myHolder = myHolder;
    }

    public MyViewHolder getMyHolder() {
        return myHolder;
    }

    public void setMyObjectObjDetails(ObjectObjDetails myObjectObjDetails) {
        this.myObjectObjDetails = myObjectObjDetails;
    }

    public ArrayList<MyViewHolder> getMyViewHolderList() {
        return myViewHolderList;
    }
    public Boolean getDeletionMode() {
        return deletionMode;
    }
    public void setDeletionMode(Boolean deletionMode) {
        this.deletionMode = deletionMode;
    }
}
