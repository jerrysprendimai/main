package com.example.jerrysprendimai;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
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
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jerrysprendimai.interfaces.OnIntentReceived;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MyAdapterObjectEdit extends RecyclerView.Adapter<MyAdapterObjectEdit.MyViewHolder> implements OnIntentReceived {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int PICK_IMAGE_MULTIPLE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    static final int REQUEST_IMAGE_CAPTURE = 10;
    static final int MY_WRITE_EXTERNAL_STORAGE = 101;

    Context context;
    ArrayList<ObjectObjDetails> myObjectList;
    ArrayList<ObjectObjDetails> myObjectListFull;
    ArrayList<ObjectObjPic> myObjectListPic;

    ArrayList<MyAdapterObjectEdit.MyViewHolder> myViewHolderList;
    ObjectUser myUser;
    ViewGroup parentView;
    MyViewHolder myHolder;
    boolean deletionMode;
    MyAdapterObjectEditPicture.MyViewHolder deletionModeViewHolder;
    ArrayList<ObjectObjPic> toBeDeletedList;
    String mCurrentPhotoPath;
    Uri mCurrentPhotoUri;
    File mPhotoFile;

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
        CardView summaryCardView, summaryExpandedCardView;
        LinearLayout lockLayout, lockLayoutExtended, layoutSummary, layoutExtended, retractableButtonLayout,
                retractableButtonLayoutExtended, oDAddFotoButton, oDTakeFotoButton, oDDeleteFotoButton,
                     addButtonsLayout, deleteButtonsLayout, oDDeleteCancel;
        ImageView oDJobDoneImg, oDJobDoneImgExtended, oDJobDeleteImg, oDJobDeleteImgExtended;
        TextView oDJobName, oDCompletedJobLabel, oDJobNameCount, oDJobNameCountExtended;
        TextInputEditText oDJobNameExtended, oDJobDescriptionExtended;
        RecyclerView oDFotoRecycleView;
        SwitchCompat oDCompleteJob;
        Button oDRetractableButton, oDRetractableButtonExtended, oDRetractableButtonToTopExtended;//, oDUserModeSaveDetail;
        EditText oDfocusHolder;
        ObjectObjDetails myObjectObjDetails;
        boolean myHoldIndicator, isChanged;
        MyAdapterObjectEditPicture myAdapterObjectEditPicture;
        ArrayList<ObjectObjPic> filteredPics;
        TextWatcher myTextWatcher;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            summaryCardView                    = itemView.findViewById(R.id.objectDetails_summary_cardView);
            summaryExpandedCardView            = itemView.findViewById(R.id.objectDetails_summary_expanded_cardView);
            layoutSummary                      = itemView.findViewById(R.id.objectDetails_summary_line);
            layoutExtended                     = itemView.findViewById(R.id.objectDetails_summary_expanded);
            retractableButtonLayout            = itemView.findViewById(R.id.objectDetails_retractable_button_layout);
            retractableButtonLayoutExtended    = itemView.findViewById(R.id.objectDetails_retractable_button_layout_extended);
            lockLayout                         = itemView.findViewById(R.id.objectDetails_lock_layout_visible);
            lockLayoutExtended                 = itemView.findViewById(R.id.objectDetails_lock_layout_extended);
            oDJobDoneImg                       = itemView.findViewById(R.id.objectDetails_image_view);
            oDJobDoneImgExtended               = itemView.findViewById(R.id.objectDetails_image_view_extended);
            oDJobDeleteImg                     = itemView.findViewById(R.id.objectDetails_delete_image_view);
            oDJobDeleteImgExtended             = itemView.findViewById(R.id.objectDetails_delete_image_view_extended);
            oDJobName                          = itemView.findViewById(R.id.objectDetails_jobName);
            oDJobNameCount                     = itemView.findViewById(R.id.objectDetails_jobName_count);
            oDJobNameExtended                  = itemView.findViewById(R.id.objectDetails_jobName_extended);
            oDJobNameCountExtended             = itemView.findViewById(R.id.objectDetails_jobName_count_extended);
            oDJobDescriptionExtended           = itemView.findViewById(R.id.objectDetail_jobDescription_extended);
            oDRetractableButton                = itemView.findViewById(R.id.objectDetails_retractable_button);
            oDRetractableButtonExtended        = itemView.findViewById(R.id.objectDetails_retractable_button_extended);
            oDRetractableButtonToTopExtended   = itemView.findViewById(R.id.objectDetails_retractable_button_toTop_extended);
            //oDUserModeSaveDetail               = itemView.findViewById(R.id.objectDetails_retractable_button_save_userMode_extended);
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
            myTextWatcher = null;
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
        public boolean isChanged() { return isChanged;   }
        public void setChanged(boolean changed) {        isChanged = changed;        }

        public ObjectObjDetails getMyObjectObjDetails() {
            return myObjectObjDetails;
        }

        public void setMyObjectObjDetails(ObjectObjDetails myObjectObjDetails) {
            this.myObjectObjDetails = myObjectObjDetails;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        setMyHolder(holder);
        holder.setMyObjectObjDetails(this.myObjectList.get(holder.getAdapterPosition()));
        holder.setPos(holder.getAdapterPosition());
        ObjectObjDetails myObjectObjDetails = this.myObjectList.get(holder.getAdapterPosition());

        holder.isChanged = false;

        holder.summaryCardView.setBackgroundResource(R.drawable.card_view_background2);
        holder.summaryExpandedCardView.setBackgroundResource(R.drawable.card_view_background2);

        myObjectObjDetails.setHolder(holder);

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
        holder.oDJobNameCount.setText((holder.getAdapterPosition()+1) +". ");
        holder.oDJobNameCountExtended.setText((holder.getAdapterPosition()+1) +". ");
        holder.oDJobNameExtended.setText(myObjectObjDetails.getName());
        holder.oDJobDescriptionExtended.setText(myObjectObjDetails.getDescription());

        //TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
        //TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());

        holder.myTextWatcher  = new TextWatcher() {
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
            ((ActivityObjectEdit)context).setBackButtonCount(0);
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
            ((ActivityObjectEdit)context).calculateCompletness();

            holder.setChanged(true);
            if (!((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().contains(holder.getMyObjectObjDetails())){
                ((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().add(holder.getMyObjectObjDetails());
            }

            //---- save action in user mode
            if(((ActivityObjectEdit)context).isUserMode()){
                //holder.oDUserModeSaveDetail.setEnabled(true);
                //holder.oDUserModeSaveDetail.setBackground(context.getResources().getDrawable(R.drawable.round_button));


               // BottomNavigationView navigationView = ((ActivityObjectEdit)context).findViewById(R.id.save_cancel_buttons);
               // Menu menu = navigationView.getMenu();
               // MenuItem menuItemSave = menu.findItem(R.id.item_save);
               // ((ActivityObjectEdit)context).setObjectObjDetailsToUpdate(myObjectObjDetails);
               // ((ActivityObjectEdit)context).onNavigationItemSelected(menuItemSave);
            }
        };
        holder.oDCompleteJob.setOnCheckedChangeListener(myOnCheckedChangeListener);
        holder.oDRetractableButton.setOnClickListener(v -> {
            ((ActivityObjectEdit)context).setBackButtonCount(0);
            if(((ActivityObjectEdit)context).getDeletionMode().equals(false)){
                if((holder.layoutSummary.getVisibility() == View.VISIBLE)&&(holder.layoutExtended.getVisibility() == View.GONE)){
                    TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
                    //TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());
                    holder.layoutExtended.setVisibility(View.VISIBLE);
                    holder.retractableButtonLayoutExtended.setVisibility(View.VISIBLE);
                    holder.layoutSummary.setVisibility(View.GONE);
                    holder.retractableButtonLayout.setVisibility(View.GONE);

                    holder.oDJobNameExtended.addTextChangedListener(holder.myTextWatcher);
                    holder.oDJobDescriptionExtended.addTextChangedListener(holder.myTextWatcher);

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
            ((ActivityObjectEdit)context).setBackButtonCount(0);
            if((holder.layoutSummary.getVisibility() == View.GONE)&&(holder.layoutExtended.getVisibility() == View.VISIBLE)){
                TransitionManager.beginDelayedTransition(holder.layoutExtended, new AutoTransition());
                //TransitionManager.beginDelayedTransition(holder.layoutSummary, new AutoTransition());
                holder.layoutExtended.setVisibility(View.GONE);
                holder.retractableButtonLayoutExtended.setVisibility(View.GONE);
                holder.layoutSummary.setVisibility(View.VISIBLE);
                holder.retractableButtonLayout.setVisibility(View.VISIBLE);

                holder.oDJobNameExtended.removeTextChangedListener(holder.myTextWatcher);
                holder.oDJobDescriptionExtended.removeTextChangedListener(holder.myTextWatcher);
                //holder.oDCompleteJob.setOnCheckedChangeListener(emptyStateListener);

                holder.oDfocusHolder.setInputType(InputType.TYPE_NULL);
                holder.oDfocusHolder.requestFocus();

                ((ActivityObjectEdit)context).hideSoftKeyboard();
            }
        });
        holder.oDRetractableButtonToTopExtended.setSoundEffectsEnabled(false);
        holder.oDRetractableButtonToTopExtended.setOnClickListener( v-> {
            holder.oDRetractableButtonExtended.performClick();
        });
        holder.layoutSummary.setSoundEffectsEnabled(false);
        holder.layoutSummary.setOnClickListener(v -> holder.oDRetractableButton.performClick());
        holder.layoutSummary.setOnLongClickListener(v -> {
            holder.oDRetractableButton.performLongClick();
            return false;
        });

        if(holder.layoutSummary.getVisibility() == View.VISIBLE){
            holder.layoutExtended.setVisibility(View.GONE);
            holder.retractableButtonLayoutExtended.setVisibility(View.GONE);
        }else{
            holder.oDRetractableButtonExtended.setSoundEffectsEnabled(false);
            holder.oDRetractableButtonExtended.performClick();
            holder.oDRetractableButtonExtended.setSoundEffectsEnabled(true);
        }


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
        //setDeletionModeViewHolder(null);
        holder.myAdapterObjectEditPicture = new MyAdapterObjectEditPicture(context, this, holder, parentView, holder.filteredPics, myUser);
        holder.oDFotoRecycleView.setAdapter(holder.myAdapterObjectEditPicture);
        holder.oDFotoRecycleView.setLayoutManager(new GridLayoutManager(context, 3));
        //holder.oDFotoRecycleView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        //---------delete cancel handler
        holder.oDDeleteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeletionMode(false);
                //setDeletionModeViewHolder(null);
                holder.setDeletionModeButtons(false);
                holder.myAdapterObjectEditPicture.notifyDataSetChanged();
                toBeDeletedList.removeAll(toBeDeletedList);
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
                    ArrayList<ObjectObjPic> tmp = ((ActivityObjectEdit) context).getObjectPicturesArrayList();
                    tmp.size();
                    myObjectListPic.remove(toBeDeletedList.get(i));
                    ((ActivityObjectEdit)context).getObjectPicturesArrayList().remove(toBeDeletedList.get(i));
                }
                //toBeDeletedList = new ArrayList<>();
                toBeDeletedList.removeAll(toBeDeletedList);
                setDeletionMode(false);
                //setDeletionModeViewHolder(null);
                holder.setDeletionModeButtons(false);
                holder.myAdapterObjectEditPicture.notifyDataSetChanged();

                holder.setChanged(true);
                ArrayList<ObjectObjDetails> tmp = ((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList();
                if (!((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().contains(holder.getMyObjectObjDetails())){
                    ((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().add(holder.getMyObjectObjDetails());
                }

                ((ActivityObjectEdit)context).setNeedSave(true);
                ((ActivityObjectEdit)context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));


                //---- Save Button in UserMode handling
                /*if ((((ActivityObjectEdit) context).isUserMode())) {
                    if((((ActivityObjectEdit) context).isNeedSave())){
                        holder.oDUserModeSaveDetail.setEnabled(true);
                        holder.oDUserModeSaveDetail.setBackground(context.getResources().getDrawable(R.drawable.round_button));
                    }else {
                        holder.oDUserModeSaveDetail.setEnabled(false);
                        holder.oDUserModeSaveDetail.setBackground(context.getResources().getDrawable(R.drawable.round_button_grey));
                    }
                }*/

            }
        });
        //----------add picture handler
        MyAdapterObjectEdit thisInstance = this;
        holder.oDAddFotoButton.setOnClickListener(v -> {
            ((ActivityObjectEdit)context).setBackButtonCount(0);
            ((ActivityObjectEdit)context).setCallbackAdapterReference(thisInstance, holder, "addFoto");
            //check permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
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

        });
        //----------take photo handler
        holder.oDTakeFotoButton.setOnClickListener(v -> {
            ((ActivityObjectEdit)context).setBackButtonCount(0);
            ((ActivityObjectEdit)context).setCallbackAdapterReference(thisInstance, holder, "takeFoto");
            //check permission camera
           if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ((ActivityObjectEdit)context).requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                } else {
                    //check permission external storage
                    if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        ((ActivityObjectEdit)context).requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_WRITE_EXTERNAL_STORAGE);
                    } else {
                        takeNewPhoto();
                    }
                }
            }else{
               takeNewPhoto();
           }
        });

        if (((ActivityObjectEdit) context).isUserMode()){
            holder.oDJobName.setEnabled(false);
            holder.oDJobNameExtended.setEnabled(false);
            //holder.oDJobDescriptionExtended.clearFocus();
            //holder.oDJobDescriptionExtended.setEnabled(false);
            holder.oDJobDescriptionExtended.setFocusableInTouchMode(false);
            holder.oDJobDescriptionExtended.setVerticalScrollBarEnabled(true);

            //if (((ActivityObjectEdit) context).isNeedSave()){
            //    holder.oDUserModeSaveDetail.setEnabled(true);
            //    holder.oDUserModeSaveDetail.setBackground(context.getResources().getDrawable(R.drawable.round_button));
            //}else{

                //holder.oDUserModeSaveDetail.setEnabled(false);
                //holder.oDUserModeSaveDetail.setBackground(context.getResources().getDrawable(R.drawable.round_button_grey));

            //}
            holder.oDRetractableButton.setOnLongClickListener(v ->{
                holder.oDRetractableButton.setSoundEffectsEnabled(false);
                holder.oDRetractableButton.performClick();
                holder.oDRetractableButton.setSoundEffectsEnabled(true);
                return false;
            });
            holder.layoutSummary.setOnLongClickListener(v -> {
                holder.oDRetractableButton.setSoundEffectsEnabled(false);
                holder.oDRetractableButton.performLongClick();
                holder.oDRetractableButton.setSoundEffectsEnabled(true);
                return false;
            });

            /*
            holder.oDUserModeSaveDetail.setVisibility(View.VISIBLE);
            holder.oDUserModeSaveDetail.setOnClickListener(v ->{
                holder.oDUserModeSaveDetail.setEnabled(false);
                holder.oDUserModeSaveDetail.setBackground(context.getResources().getDrawable(R.drawable.round_button_grey));

                BottomNavigationView navigationView = ((ActivityObjectEdit)context).findViewById(R.id.save_cancel_buttons);
                Menu menu = navigationView.getMenu();
                MenuItem menuItemSave = menu.findItem(R.id.item_save);
                ((ActivityObjectEdit)context).setObjectObjDetailsToUpdate(myObjectObjDetails);
                ((ActivityObjectEdit)context).onNavigationItemSelected(menuItemSave);
            });
            */
        }else{
            holder.oDJobDescriptionExtended.setVerticalScrollBarEnabled(true);
            //holder.oDUserModeSaveDetail.setVisibility(View.GONE);
        }
        holder.oDJobDescriptionExtended.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        //---- locked object handling
        //---check if object is loked
        if(!((ActivityObjectEdit)context).getObjectObject().getLockedByUserId().equals("0")){
            //---check if object is loked but not by me
            if(!((ActivityObjectEdit)context).getObjectObject().getLockedByUserId().equals(myUser.getId().toString())){

                holder.oDAddFotoButton.setEnabled(false);
                holder.oDDeleteFotoButton.setEnabled(false);
                holder.oDTakeFotoButton.setEnabled(false);
                holder.oDDeleteCancel.setEnabled(false);

                holder.oDAddFotoButton.setBackgroundColor(context.getResources().getColor(R.color.jerry_grey));
                holder.oDDeleteFotoButton.setBackgroundColor(context.getResources().getColor(R.color.jerry_grey));
                holder.oDTakeFotoButton.setBackgroundColor(context.getResources().getColor(R.color.jerry_grey));
                holder.oDDeleteCancel.setBackgroundColor(context.getResources().getColor(R.color.jerry_grey));

                holder.oDCompleteJob.setEnabled(false);

                BottomNavigationView navigationView = ((ActivityObjectEdit)context).findViewById(R.id.save_cancel_buttons);
                Menu menu = navigationView.getMenu();
                menu.findItem(R.id.item_save).setEnabled(false);
            }
        }else{
            holder.oDAddFotoButton.setEnabled(true);
            holder.oDDeleteFotoButton.setEnabled(true);
            holder.oDTakeFotoButton.setEnabled(true);
            holder.oDDeleteCancel.setEnabled(true);

            holder.oDAddFotoButton.setBackgroundColor(context.getResources().getColor(R.color.jerry_blue));
            holder.oDDeleteFotoButton.setBackgroundColor(context.getResources().getColor(R.color.jerry_blue));
            holder.oDTakeFotoButton.setBackgroundColor(context.getResources().getColor(R.color.jerry_blue));
            holder.oDDeleteCancel.setBackgroundColor(context.getResources().getColor(R.color.jerry_blue));

            holder.oDCompleteJob.setEnabled(true);

            BottomNavigationView navigationView = ((ActivityObjectEdit)context).findViewById(R.id.save_cancel_buttons);
            Menu menu = navigationView.getMenu();
            menu.findItem(R.id.item_save).setEnabled(true);
        }

    }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data, MyViewHolder holder, int resultOk, String actionTp){
        String[] mimetypes = {"image/*",
                "application/pdf",
                "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                "text/plain"
        };

        //-----add picture/add pictures
        if(resultCode == resultOk && (requestCode == IMAGE_PICK_CODE || requestCode == PICK_IMAGE_MULTIPLE)){
            try {
                ContentResolver cr = context.getContentResolver();
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();

                    String mime = cr.getType(uri);
                    if(!Arrays.asList(mimetypes).contains(mime)){
                        if(!mime.contains("image/")) {
                            Toast.makeText(context, context.getResources().getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                    cursor.moveToFirst();
                    String fileName = myUser.getId().toString() + "_" + myObjectList.get(holder.getAdapterPosition()).getObjectId().toString() + "_" + cursor.getString(nameIndex);

                    String mimeType = context.getContentResolver().getType(uri);
                    String filePath = "";
                    if(!mimeType.contains("image/")){
                        String filename = "";
                        if (mimeType == null) {
                            String path = ((ActivityObjectEdit)context).getPath(context, uri);
                            if (path == null) {
                                //filename = FilenameUtils.getName(uri.toString());
                            } else {
                                File file = new File(path);
                                filename = file.getName();
                            }
                        } else {
                            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
                            int nameIndexx = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                            int sizeIndexx = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                            returnCursor.moveToFirst();
                            filename = returnCursor.getString(nameIndexx);
                            String size = Long.toString(returnCursor.getLong(sizeIndexx));
                        }
                        File fileSave = ((ActivityObjectEdit)context).getExternalFilesDir(null);
                        String sourcePath = ((ActivityObjectEdit)context).getExternalFilesDir(null).toString();

                        //File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/download/"+ cursor.getString(nameIndex));
                        try {
                            File file = new File(sourcePath +"/"+ filename);
                            filePath = sourcePath +"/"+ filename;
                            ((ActivityObjectEdit)context).copyFileStream(file, uri,context);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }


                    ObjectObjPic newPic = new ObjectObjPic();
                    newPic.setMimeType(mimeType);
                    newPic.setPosNr(holder.getAdapterPosition());
                    newPic.setUserId(myUser.getId());
                    newPic.setFirstName(myUser.getFirst_name());
                    newPic.setPicUri(uri.toString());
                    newPic.setObjectId(((ActivityObjectEdit) context).objectObject.getId());
                    newPic.setPicName(fileName);
                    newPic.setFilePath(filePath);

                    myObjectListPic.add(newPic);
                    holder.filteredPics.add(newPic);

                    holder.setChanged(true);
                    if (!((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().contains(myObjectList.get(holder.getAdapterPosition()))){
                        ((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().add(myObjectList.get(holder.getAdapterPosition()));
                    }


                    ((ActivityObjectEdit) context).setNeedSave(true);
                    ((ActivityObjectEdit) context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                }
            }catch (Exception e){
                //set image to image view
                Uri uri = data.getData();
                Cursor cursor = context.getContentResolver().query(uri, null,null,null,null);

                ContentResolver cr = context.getContentResolver();
                String mime = cr.getType(uri);

                if(!Arrays.asList(mimetypes).contains(mime)){
                    if(!mime.contains("image/")) {
                        Toast.makeText(context, context.getResources().getString(R.string.not_supported), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                cursor.moveToFirst();
                String fileName = myUser.getId().toString() + "_" + myObjectList.get(holder.getAdapterPosition()).getObjectId().toString() + "_" + cursor.getString(nameIndex);

                String mimeType = context.getContentResolver().getType(uri);
                String filePath = "";
                if(!mimeType.contains("image/")){
                    String filename = "";
                    if (mimeType == null) {
                        String path = ((ActivityObjectEdit)context).getPath(context, uri);
                        if (path == null) {
                            //filename = FilenameUtils.getName(uri.toString());
                        } else {
                            File file = new File(path);
                            filename = file.getName();
                        }
                    } else {
                        Uri returnUri = data.getData();
                        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
                        int nameIndexx = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndexx = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        filename = returnCursor.getString(nameIndexx);
                        String size = Long.toString(returnCursor.getLong(sizeIndexx));
                    }
                    File fileSave = ((ActivityObjectEdit)context).getExternalFilesDir(null);
                    String sourcePath = ((ActivityObjectEdit)context).getExternalFilesDir(null).toString();

                    //File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/download/"+ cursor.getString(nameIndex));
                    try {
                        File file = new File(sourcePath +"/"+ filename);
                        filePath = sourcePath +"/"+ filename;
                        ((ActivityObjectEdit)context).copyFileStream(file, uri,context);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                ObjectObjPic newPic = new ObjectObjPic();
                newPic.setMimeType(mimeType);
                newPic.setPosNr(holder.getAdapterPosition());
                newPic.setUserId(myUser.getId());
                newPic.setFirstName(myUser.getFirst_name());
                newPic.setPicUri(uri.toString());
                newPic.setObjectId(((ActivityObjectEdit)context).objectObject.getId());
                newPic.setPicName(fileName);
                newPic.setFilePath(filePath);

                myObjectListPic.add(newPic);
                holder.filteredPics.add(newPic);

                holder.setChanged(true);
                if (!((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().contains(myObjectList.get(holder.getAdapterPosition()))){
                    ((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().add(myObjectList.get(holder.getAdapterPosition()));
                }

                ((ActivityObjectEdit)context).setNeedSave(true);
                ((ActivityObjectEdit)context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
            }

            holder.myAdapterObjectEditPicture.notifyDataSetChanged();
            ((ActivityObjectEdit)context).clearCallbackAdapterReference();
        }
        //-----take picture
        if(resultCode == resultOk && requestCode == REQUEST_IMAGE_CAPTURE ){
            //Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);
            //Uri filePath = data.getData();
            galleryAddPic();
            ObjectObjPic newPic = new ObjectObjPic();
            newPic.setUserId(myUser.getId());
            newPic.setFirstName(myUser.getFirst_name());
            newPic.setPicUri(getmCurrentPhotoUri().toString());//filePath.toString());
            newPic.setObjectId(((ActivityObjectEdit)context).objectObject.getId());
            newPic.setPosNr(holder.getAdapterPosition());
            newPic.setPicName(getmPhotoFile().getName());
            myObjectListPic.add(newPic);
            holder.filteredPics.add(newPic);

            holder.setChanged(true);
            if (!((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().contains(myObjectList.get(holder.getAdapterPosition()))){
                ((ActivityObjectEdit) context).getObjectObjDetailsToUpdateArrayList().add(myObjectList.get(holder.getAdapterPosition()));
            }

            holder.myAdapterObjectEditPicture.notifyDataSetChanged();
            ((ActivityObjectEdit)context).clearCallbackAdapterReference();
            ((ActivityObjectEdit)context).setNeedSave(true);
            ((ActivityObjectEdit)context).oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
        }else if(resultCode != resultOk && requestCode == REQUEST_IMAGE_CAPTURE){
            File photoImage = getmPhotoFile();
            if(photoImage.exists()){
                photoImage.delete();
            }
            setmPhotoFile(null);
            setmCurrentPhotoUri(null);
            setmCurrentPhotoPath(null);
        }
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = getmPhotoFile();//new File(getmCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);

        MediaScannerConnection.scanFile(
                context,
                new String[]{getmCurrentPhotoPath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.v("foto", "file" + path + "was scanned: " + uri);
                    }
                }
        );
    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = myUser.getId().toString() + "_" + ((ActivityObjectEdit)context).getObjectObject().getId().toString() + "_" + timeStamp+"_";
        File storageDir = //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/Jerry");//Environment.getExternalStorageDirectory();
        File image = File.createTempFile(imageName, ".jpg", storageDir );

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "JerrySprendimai_"+timeStamp);
        values.put(MediaStore.Images.Media.DESCRIPTION, "JerrySprendimai_"+timeStamp);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.ImageColumns.BUCKET_ID, image.toString().toLowerCase(Locale.ROOT).hashCode());
        values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, image.getName().toLowerCase(Locale.ROOT));
        values.put("_data", image.getAbsolutePath());
        ContentResolver cr = context.getContentResolver();
        cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        setmCurrentPhotoPath(image.getAbsolutePath());
        return image;
    }

    public void takeNewPhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider",photoFile);
                //Uri imageUri = FileProvider.getUriForFile(context, "com.example.android.fileprovider",photoFile);
                setmCurrentPhotoUri(imageUri);
                setmPhotoFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider" , photoFile));
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                ((ActivityObjectEdit)context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    public void pickImageFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Intent intent = new Intent();
                //intent.setType("image/*");
                intent.setType("*/*");
                String[] mimetypes = {"image/*",
                        "application/pdf",
                        "application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain"
                };
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                ((ActivityObjectEdit)context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }catch(Exception e){
                Intent photoPickerIntent = new Intent();
                ((ActivityObjectEdit)context).startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
            }
        } else {
            Intent photoPickerIntent = new Intent();
            ((ActivityObjectEdit)context).startActivityForResult(photoPickerIntent, IMAGE_PICK_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, String actionTp) {
        switch (requestCode){
            case PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted

                       pickImageFromGallery();
                }else{
                    //permission denied
                    Toast.makeText(context, "Atšaukta", Toast.LENGTH_SHORT).show();
                }
            case MY_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted
                     takeNewPhoto();
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


    public void removeToBeDeleted(MyViewHolder holder, ObjectObjPic objectObjPic){
        if(toBeDeletedList.contains(objectObjPic)){
            this.toBeDeletedList.remove(objectObjPic);

            if(this.toBeDeletedList.size() == 0){
                setDeletionMode(false);
                //setDeletionModeViewHolder(null);
                holder.setDeletionModeButtons(false);
                holder.myAdapterObjectEditPicture.notifyDataSetChanged();
            }
        }
        /*for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(objectObjPic)){
                this.toBeDeletedList.remove(i);
                break;
            }
        }
        if(this.toBeDeletedList.size() == 0){
            setDeletionMode(false);
            holder.setDeletionModeButtons(false);
            holder.myAdapterObjectEditPicture.notifyDataSetChanged();
        }*/
    }
    public void addToBeDeleted(ObjectObjPic objectObjPic){
        if(!toBeDeletedList.contains(objectObjPic)){
            this.toBeDeletedList.add(objectObjPic);
        }
        /*boolean found = false;
        for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(objectObjPic)){
                found = true;
                break;
            }
        }
        if(!found){
            this.toBeDeletedList.add(objectObjPic);
        }*/
    }

    public void setMyHolder(MyViewHolder myHolder) {        this.myHolder = myHolder;    }
    public MyViewHolder getMyHolder() {        return myHolder;    }
    public ArrayList<MyViewHolder> getMyViewHolderList() {        return myViewHolderList;    }
    public Boolean getDeletionMode() {        return deletionMode;    }
    public void setDeletionMode(Boolean deletionMode) {       this.deletionMode = deletionMode;    }

    public String getmCurrentPhotoPath() { return mCurrentPhotoPath;    }
    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {        this.mCurrentPhotoPath = mCurrentPhotoPath;    }
    public void setmCurrentPhotoUri(Uri mCurrentPhotoUri){       this.mCurrentPhotoUri = mCurrentPhotoUri;    }
    public Uri getmCurrentPhotoUri() {        return mCurrentPhotoUri;    }
    public File getmPhotoFile() {        return mPhotoFile;    }
    public void setmPhotoFile(File mPhotoFile) {        this.mPhotoFile = mPhotoFile;    }
    public ArrayList<ObjectObjDetails> getMyObjectList() { return myObjectList;  }
    public void setMyObjectList(ArrayList<ObjectObjDetails> myObjectList) {  this.myObjectList = myObjectList;   }
    public ArrayList<ObjectObjPic> getMyObjectListPic() {   return myObjectListPic;   }
    public void setMyObjectListPic(ArrayList<ObjectObjPic> myObjectListPic) {   this.myObjectListPic = myObjectListPic;  }
}
