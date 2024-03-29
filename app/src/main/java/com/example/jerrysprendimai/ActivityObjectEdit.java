package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class ActivityObjectEdit extends AppCompatActivity implements View.OnClickListener, KeyboardVisibilityEventListener, BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher, View.OnKeyListener {
    final String user = "user";
    public static final int REQUEST_CODE = 101;

    private ArrayList<ObjectObjUser> objectUserArrayList;
    private ArrayList<ObjectObjDetails> objectDetailsArrayList;
    private ArrayList<ObjectObjPic> objectPicturesArrayList;

    ArrayList<ObjectUser> employeeArrayList;
    ArrayList<ObjectUser> ownerArrayList;
    BottomNavigationView bottomNavigationView;
    ObjectObject objectObject;
    ObjectUser myUser;
    ArrayList<ObjectObjDetails> objectObjDetailsToUpdateArrayList;
    ObjectObjDetails objectObjDetailsToUpdate;
    ArrayList<Integer> toBeDeletedList;

    RecyclerView recyclerView;
    MyAdapterObjectEdit myAdapterObjectEdit;
    ScrollView scrollView;
    CardView mainCardView;
    EditText editInvisibleFocusHolder;
    ImageView oSavedStatusIndicator, oObjectIcon;
    Button oAddJob, toChat;
    TextView oId, oNameLb, oDate, oDateLabel, oName, oCustomer, oAddress, oJobs, oJobsUserMode, oJobsDone, oJobsDoneUserMode, oProgressBarLabel, oProgressBarLabelUserMode;
    ProgressBar oProgressbar, oProgressbarUserMode;

    boolean needSave, deletionMode, userMode, saveMode, fieldCheckError, objectLocked;
    FloatingActionButton oDeleteJobButton;
    LinearLayout oDeleteJobButtonLayout;
    Integer backButtonCount;
    MyAdapterObjectEdit adatpreWa;
    MyAdapterObjectEdit.MyViewHolder holderWa;
    String actionTypeWa;
    private int backgroundJobs = 10;
    private int newPicCount = 0;
    private int retutnThreadCount = 0;
    private MyAdapterObjectEdit.MyViewHolder deletionModeAdapterObjectEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_edit);

        this.employeeArrayList = new ArrayList<>();
        this.ownerArrayList = new ArrayList<>();

        this.myUser       = getIntent().getParcelableExtra("myUser");
        this.objectObject = getIntent().getParcelableExtra("objectObject");
        this.objectDetailsArrayList  = getIntent().getParcelableArrayListExtra("listDetails");
        this.objectUserArrayList     = getIntent().getParcelableArrayListExtra("listUser");
        this.objectPicturesArrayList = getIntent().getParcelableArrayListExtra("listPictures");

        //if(getIntent().getParcelableArrayListExtra("employeeList") != null){
            this.employeeArrayList = getIntent().getParcelableArrayListExtra("employeeList");
        //}
        //if(getIntent().getParcelableArrayListExtra("ownerList") != null){
            this.ownerArrayList = getIntent().getParcelableArrayListExtra("ownerList");
        //}


        //-----------------BackButton press counter
        //this.backButtonCount = 0;
        setBackButtonCount(0);
        this.needSave = false;

        //-----------------Deletion Mode
        this.deletionMode = false;
        this.toBeDeletedList = new ArrayList<Integer>();

        //-----------------View element binding----------------
        this.editInvisibleFocusHolder = findViewById(R.id.invisibleFocusHolder);
        this.oObjectIcon              = findViewById(R.id.objectEdit_object_icon);
        this.oId                      = findViewById(R.id.objectEdit_id);
        this.oNameLb                  = findViewById(R.id.objectEdit_name);
        this.oSavedStatusIndicator    = findViewById(R.id.objectEdit_savedStatus_img);
        this.oDate                    = findViewById(R.id.objectEdit_date);
        this.oName                    = findViewById(R.id.objectEdit_objectName);
        this.oDateLabel               = findViewById(R.id.objectEdit_date_label);
        this.oCustomer                = findViewById(R.id.objectEdit_customerName);
        this.oAddress                 = findViewById(R.id.objectEdit_objectAddress);
        this.oJobs                    = findViewById(R.id.objectEdit_objectJobs);
        this.oJobsUserMode            = findViewById(R.id.objectEdit_objectJobs_userMode);
        this.oJobsDone                = findViewById(R.id.objectEdit_objectJobsDone);
        this.oJobsDoneUserMode        = findViewById(R.id.objectEdit_objectJobsDone_userMode);
        this.oProgressbar             = findViewById(R.id.objectEdit_progess_bar);
        this.oProgressbarUserMode     = findViewById(R.id.objectEdit_progess_bar_userMode);
        this.oProgressBarLabel        = findViewById(R.id.objectEdit_progess_bar_label);
        this.oProgressBarLabelUserMode = findViewById(R.id.objectEdit_progess_bar_label_userMode);
        this.oAddJob                  = findViewById(R.id.objectEdit_add_job_button);
        this.toChat                   = findViewById(R.id.objectEdit_to_chat_button);
        this.scrollView               = findViewById(R.id.objectEdit_job_scroll_view);
        this.oDeleteJobButton         = findViewById(R.id.objectEdit_delete_job);
        this.oDeleteJobButtonLayout   = findViewById(R.id.objectEdit_delete_job_layout);
        this.mainCardView             = findViewById(R.id.cardView);

        fillFieldValues();
        buildRecyclerView();

        //--------------setting event listeners---------------
        this.oName.addTextChangedListener(this);
        this.oCustomer.addTextChangedListener(this);
        this.oAddress.addTextChangedListener(this);
        this.oName.setOnKeyListener(this);
        this.oCustomer.setOnKeyListener(this);
        this.oAddress.setOnKeyListener(this);

        //---- Object Icon handling
        this.oObjectIcon.setImageResource(getResources().getIdentifier(objectObject.getIcon(), "drawable", getApplicationInfo().packageName));

        //---- Date Picker handling
        Context context = this;
        this.oDate.setOnClickListener(v->{
            setBackButtonCount(0);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_date_picker, null, false);
            TextView dialogDateLabel = dialogView.findViewById(R.id.datePicker_date_label);
            CalendarView datePickerCalender = dialogView.findViewById(R.id.datePicker_calenderView);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(HelperDate.get_YMD_from_date_display("year",        this.objectObject.getDate())));
            calendar.set(Calendar.MONTH, Integer.parseInt(HelperDate.get_YMD_from_date_display("month",      this.objectObject.getDate())) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(HelperDate.get_YMD_from_date_display("day", this.objectObject.getDate())));
            dialogDateLabel.setText(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
            long milliTime = calendar.getTimeInMillis();
            datePickerCalender.setDate(milliTime);
            datePickerCalender.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month );
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dialogDateLabel.setText(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
                long milliTime1 = calendar.getTimeInMillis();
                datePickerCalender.setDate(milliTime1);
            });
            AlertDialog.Builder datePickerDialog = new AlertDialog.Builder((Context) this, R.style.AlertDialogTheme);
            datePickerDialog.setPositiveButton("Ok", (dialog, which) -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dialogDate = dateFormat.format(datePickerCalender.getDate());
                objectObject.setDate(dialogDate);
                oDate.setText(dialogDate);

                setNeedSave(true);
                oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
            });
            datePickerDialog.setNegativeButton("Cancel", null);
            datePickerDialog.setView(dialogView);
            datePickerDialog.create();
            datePickerDialog.show();
        });
        //---- Add Job button handling
        this.oDeleteJobButton.setOnClickListener(v ->{
            setBackButtonCount(0);
            //--------sort list dscending
            Collections.sort(toBeDeletedList, (o1, o2) -> o2.compareTo(o1));
            ArrayList<ObjectObjDetails> toBeRemoveddDetails = new ArrayList<>();
            ArrayList<ObjectObjPic> toBeRemovedPictures = new ArrayList<>();

            for(int i=0; i<toBeDeletedList.size();i++){
                ObjectObjDetails objectObjDetailsToRemove = objectDetailsArrayList.get(toBeDeletedList.get(i));
                toBeRemoveddDetails.add(objectObjDetailsToRemove);
                //objectDetailsArrayList.remove(objectObjDetailsToRemove);
                for(int j=0; j < objectPicturesArrayList.size(); j++){
                     if(objectPicturesArrayList.get(j).getPosNr().equals(objectObjDetailsToRemove.getPosNr())){
                         //objectPicturesArrayList.remove(objectPicturesArrayList.get(j));
                         toBeRemovedPictures.add(objectPicturesArrayList.get(j));
                     }
                }
            }
            for(int i=0;i< toBeRemoveddDetails.size();i++){
                objectDetailsArrayList.remove(toBeRemoveddDetails.get(i));
            }
            for(int i=0; i< toBeRemovedPictures.size(); i++){
                objectPicturesArrayList.remove(toBeRemovedPictures.get(i));
            }
            for(int i = 0; i < objectDetailsArrayList.size(); i++){
                for(int j=0; j < objectPicturesArrayList.size(); j++){
                    if(objectPicturesArrayList.get(j).getPosNr().equals(objectDetailsArrayList.get(i).getPosNr())){
                        objectPicturesArrayList.get(j).setPosNr(i);
                    }
                }
                objectDetailsArrayList.get(i).setPosNr(i);
                //objectDetailsArrayList.get(i).setName(String.valueOf(i) +". "+objectDetailsArrayList.get(i).getName());
            }
            calculateCompletness();
            setDeletionMode(false);
            this.setSaveCancelVisibility(true);
            this.setDeleteButtonVisibility(false);
            this.toBeDeletedList = new ArrayList<Integer>();
            myAdapterObjectEdit.notifyDataSetChanged();

            setNeedSave(true);
            oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
        });

        //---- retractable button/view handling
        LinearLayout userModeprogressBar   = findViewById(R.id.objectEdit_progrss_bar_layout_userMode);
        LinearLayout retractableLayout     = findViewById(R.id.objectEdit_retractable_layout);
        LinearLayout retractableLayoutLine = findViewById(R.id.objectEdit_retractableLine);
        Button retractableButton           = findViewById(R.id.objectEdit_retractable_button);
        LinearLayout progressBarLayout     = findViewById(R.id.objectEdit_progrss_bar_layout);
        LinearLayout leftPart              = findViewById(R.id.objectEdit_retractable_layout_leftPart);
        userModeprogressBar.setVisibility(View.GONE);
        retractableButton.setOnClickListener(v -> {
            setBackButtonCount(0);
            hideSoftKeyboard();
            if(retractableLayout.getVisibility()==View.GONE){
                TransitionManager.beginDelayedTransition(retractableLayout, new AutoTransition());
                retractableLayout.setVisibility(View.VISIBLE);
                retractableButton.setBackgroundResource(R.drawable.ic_arrow_up_white);
                if (isUserMode()){
                    userModeprogressBar.setVisibility(View.GONE);
                    oDateLabel.setVisibility(View.VISIBLE);
                    oDate.setVisibility(View.VISIBLE);
                }
            }else{
                //TransitionManager.beginDelayedTransition(retractableLayout, new AutoTransition());
                retractableButton.setBackgroundResource(R.drawable.ic_arrow_down_white);
                retractableLayout.setVisibility(View.GONE);
                if (isUserMode()){
                    TransitionManager.beginDelayedTransition(userModeprogressBar, new AutoTransition());
                    userModeprogressBar.setVisibility(View.VISIBLE);
                    oDateLabel.setVisibility(View.GONE);
                    oDate.setVisibility(View.GONE);
                }
            }
            toChat.setVisibility(View.VISIBLE);
        });
        retractableLayoutLine.setSoundEffectsEnabled(false);
        retractableLayoutLine.setOnClickListener(v -> retractableButton.performClick());
        retractableLayoutLine.performClick();

        //---- Add Job Button handling
        oAddJob.setOnClickListener(v ->{
            //backButtonCount = 0;
            setBackButtonCount(0);
            hideSoftKeyboard();
            setDeletionMode(false);
            this.setSaveCancelVisibility(true);
            this.setDeleteButtonVisibility(false);
            this.toBeDeletedList = new ArrayList<Integer>();

            for(ObjectObjDetails objectObjDetails: objectDetailsArrayList){
                objectObjDetails.getHolder().oDJobNameExtended.removeTextChangedListener(objectObjDetails.getHolder().myTextWatcher);
                objectObjDetails.getHolder().oDJobDescriptionExtended.removeTextChangedListener(objectObjDetails.getHolder().myTextWatcher);
            }

            ObjectObjDetails newObjectObjDetails = new ObjectObjDetails();
            //newObjectObjDetails.setObjectId(objectObject.getId());
            objectDetailsArrayList.add(0, newObjectObjDetails);
            for(int i = 0; i < objectDetailsArrayList.size(); i++ ){
                objectDetailsArrayList.get(i).setPosNr(i);
            }
            for(int i = 0; i < objectPicturesArrayList.size(); i++){
                objectPicturesArrayList.get(i).setPosNr(objectPicturesArrayList.get(i).getPosNr()+1);
            }

            //---- on add Job recalculate completness
            calculateCompletness();

            myAdapterObjectEdit.notifyDataSetChanged();
            oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_yellow));
            setNeedSave(true);
        });

        //---- to Chat handling
        if(objectObject.getId() == -1){
            toChat.setEnabled(false);
            toChat.setBackground(getResources().getDrawable(R.drawable.round_button_grey));
        }else{
            toChat.setEnabled(true);
            toChat.setBackground(getResources().getDrawable(R.drawable.round_button));
        }
        toChat.setOnClickListener(v->{
            if(getEmployeeArrayList() == null){
                setEmployeeArrayList(new ArrayList<>());
            }
            if(getOwnerArrayList() == null){
                setOwnerArrayList(new ArrayList<>());
            }
            if(objectUserArrayList == null){
                objectUserArrayList = new ArrayList<>();
            }
            Intent intent = new Intent(this, ActivityChat.class);
            intent.putExtra("myUser", myUser);
            intent.putExtra("objectObject", objectObject);
            intent.putParcelableArrayListExtra("listUser", objectUserArrayList);
            intent.putParcelableArrayListExtra("employeeList", getEmployeeArrayList());
            intent.putParcelableArrayListExtra("ownerList", getOwnerArrayList());
            context.startActivity(intent);
        });

        //-----------------Object Locked message
        if((!objectObject.getLockedByUserId().equals("0"))&&(!objectObject.getLockedByUserId().equals(String.valueOf(myUser.getId())))){
            Toast.makeText(context, context.getResources().getString(R.string.locked) + ". " + context.getResources().getString(R.string.locked_by) + " '" + objectObject.getLockedUname() + "'", Toast.LENGTH_SHORT).show();
        }

        //-----------------Save-Cancel Menu Hide/Show depending on the Keyboard----------------
        KeyboardVisibilityEvent.setEventListener(this, this);
        this.bottomNavigationView = findViewById(R.id.save_cancel_buttons);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);

        this.objectObjDetailsToUpdateArrayList = new ArrayList<>();

        //---- user mode handling
        handleUserMode();

    }

    public ArrayList<ObjectUser> getEmployeeArrayList() {        return employeeArrayList;    }
    public void setEmployeeArrayList(ArrayList<ObjectUser> employeeArrayList) {        this.employeeArrayList = employeeArrayList;    }
    public ArrayList<ObjectUser> getOwnerArrayList() {        return ownerArrayList;    }
    public void setOwnerArrayList(ArrayList<ObjectUser> ownerArrayList) {        this.ownerArrayList = ownerArrayList;    }

    public void handleUserMode(){
        Button retractableButton         = findViewById(R.id.objectEdit_retractable_button);
        LinearLayout userModeprogressBar = findViewById(R.id.objectEdit_progrss_bar_layout_userMode);
        if(this.myUser.getUser_lv().equals(user)){
            this.userMode = true;
            //setSaveCancelVisibility(false);
            //oSavedStatusIndicator.setVisibility(View.GONE);
            oAddJob.setVisibility(View.GONE);
            oDate.setEnabled(false);
            oDate.setTextColor(getResources().getColor(R.color.jerry_grey));
            oName.setEnabled(false);
            oCustomer.setEnabled(false);
            oAddress.setEnabled(false);
            if(userModeprogressBar.getVisibility() != View.VISIBLE){
               retractableButton.setSoundEffectsEnabled(false);
               retractableButton.performClick();
               retractableButton.setSoundEffectsEnabled(true);
            }
        }else{
            this.userMode = false;
            setSaveCancelVisibility(true);
            oSavedStatusIndicator.setVisibility(View.VISIBLE);
            oAddJob.setVisibility(View.VISIBLE);
            oDate.setEnabled(true);
            oName.setEnabled(true);
            oCustomer.setEnabled(true);
            oAddress.setEnabled(true);
        }
        toChat.setVisibility(View.VISIBLE);
    }

    public void setSaveCancelVisibility(Boolean value){
        TransitionManager.beginDelayedTransition(bottomNavigationView, new AutoTransition());
        if(value.equals(true)){
            this.bottomNavigationView.setVisibility(View.VISIBLE);
        }else{
            this.bottomNavigationView.setVisibility(View.GONE);
        }
    }
    public void notifyItemChanged(){
        for(int i=0; i<myAdapterObjectEdit.getMyViewHolderList().size(); i++) {
            myAdapterObjectEdit.notifyItemChanged(i);
        }
    }
    private void fillFieldValues() {
            int completeCount = 0;
            this.mainCardView.setBackgroundResource(R.drawable.card_view_background);

            if(getDeletionMode().equals(false)){
                oDeleteJobButtonLayout.setVisibility(View.GONE);
                //oDeleteJobButton.setVisibility(View.GONE);
            }else{
                oDeleteJobButtonLayout.setVisibility(View.VISIBLE);
                //oDeleteJobButton.setVisibility(View.VISIBLE);
            }

            for(int i = 0; i < this.objectDetailsArrayList.size(); i++){
                ObjectObjDetails objectObjDetails =  this.objectDetailsArrayList.get(i);
                if(objectObjDetails.getCompleted().equals("X")){
                    completeCount += 1;
                }
            }
            if(this.objectObject.getId() >= 0){
                oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_green));
            }else{
                oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_yellow));
            }

            this.oId.setText(this.objectObject.getId().toString());
            this.oNameLb.setText(this.objectObject.getObjectName());
            this.oDate.setText(this.objectObject.getDate());
            this.oName.setText(this.objectObject.getObjectName());
            this.oCustomer.setText(this.objectObject.getCustomerName());
            this.oAddress.setText(this.objectObject.getObjectAddress());
            this.oJobs.setText(String.valueOf(this.objectDetailsArrayList.size()));
            this.oJobsUserMode.setText(String.valueOf(this.objectDetailsArrayList.size()));
            this.oJobsDone.setText(String.valueOf(completeCount));
            this.oJobsDoneUserMode.setText(String.valueOf(completeCount));
            this.oProgressBarLabel.setText(String.valueOf(this.objectObject.getCompleteness()) + "%");
            this.oProgressBarLabelUserMode.setText(String.valueOf(this.objectObject.getCompleteness()) + "%");
            this.oProgressbar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(this.objectObject.getCompleteness())))));
            this.oProgressbarUserMode.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(this.objectObject.getCompleteness())))));

            //-----invisible focus holder dandling
            this.editInvisibleFocusHolder.setInputType(InputType.TYPE_NULL);
            this.editInvisibleFocusHolder.requestFocus();

    }
    private void buildRecyclerView() {
        //---------------------Recycle View---------------------
        this.recyclerView = findViewById(R.id.objectEdit_job_recycle_view);
        this.myAdapterObjectEdit = new MyAdapterObjectEdit(this, findViewById(R.id.objectEdit_main_containerView), getObjectDetailsArrayList(), getMyUser(), getObjectPicturesArrayList());
        this.recyclerView.setAdapter(myAdapterObjectEdit);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addToBeDeleted(int position){
        boolean found = false;
        for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(position)){
                found = true;
                break;
            }
        }
        if(!found){
            this.toBeDeletedList.add(position);
        }
    }
    public void removeToBeDeleted(int position){
        for(int i=0; i<this.toBeDeletedList.size(); i++){
            if(this.toBeDeletedList.get(i).equals(position)){
                this.toBeDeletedList.remove(i);
                break;
            }
        }
        if(this.toBeDeletedList.size() == 0){
            this.setDeletionMode(false);
            this.setSaveCancelVisibility(true);
            this.setDeleteButtonVisibility(false);
            myAdapterObjectEdit.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        new HttpsRequestCheckSessionAlive(this).execute();

        super.onResume();

        //editInvisibleFocusHolder.setInputType(InputType.TYPE_NULL);
        //editInvisibleFocusHolder.requestFocus();
    }
    public void scrollToPosition(){
        int nY_Pos = this.scrollView.getTop();
        int nY_Pos2 = this.scrollView.getBottom();
        //this.scrollView.fullScroll(View.FOCUS_UP);
    }
    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if(this.getDeletionMode().equals(true)){
            this.setDeletionMode(false);
            this.setDeleteButtonVisibility(false);
            this.setSaveCancelVisibility(true);
            this.toBeDeletedList = new ArrayList<Integer>();
            myAdapterObjectEdit.notifyDataSetChanged();
        }else{

            //-----------back button press handling: in case object not saved - show warning
            if ((backButtonCount.equals(0)) && (isNeedSave())) {
                Toast.makeText(this, getResources().getString(R.string.not_saved), Toast.LENGTH_SHORT).show();
                backButtonCount++;
            } else {
                setBackButtonCount(0);
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onVisibilityChanged(boolean b) {
        if (b) {
            this.findViewById(R.id.save_cancel_buttons).setVisibility(View.GONE);
        } else {
            this.findViewById(R.id.save_cancel_buttons).setVisibility(View.VISIBLE);
        }
    }

    public boolean isFieldValueError(String value, TextView field){
        boolean returnValue = false;
        if(value.equals("")){
            returnValue = true;
            field.setError(getResources().getString(R.string.error));
        }else{
            field.setError(null);
        }
        return returnValue;
    }

    public void setDeletePictureViewHolder(MyAdapterObjectEdit.MyViewHolder deletionModeAdapterObjectEdit) {
        this.deletionModeAdapterObjectEdit = deletionModeAdapterObjectEdit;
    }
    public MyAdapterObjectEdit.MyViewHolder getDeletePictureViewHolder() {
        return this.deletionModeAdapterObjectEdit;
    }

    class RunnableTask implements Runnable{
        Context context;
        ObjectObjPic objectObjPic;
        public RunnableTask(ActivityObjectEdit activityObjectEdit, ObjectObjPic objectObjPic) {
            this.context = activityObjectEdit;
            this.objectObjPic = objectObjPic;
        }
        @Override
        public void run() {
            boolean go = true;
            while (go){
                if(backgroundJobs >0){
                    backgroundJobs -= 1;
                    new HttpsRequestUploadPictures(context, objectObjPic).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                    go = false;
                }else{
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void startPictureUpload(){
        newPicCount = 0;
        for(int i=0; i < getObjectPicturesArrayList().size(); i++){
            //if(getObjectPicturesArrayList().get(i).getId().equals(-1)){
                newPicCount += 1;
                Thread thread = new Thread(new RunnableTask(this, getObjectPicturesArrayList().get(i)));
                thread.start();
            //}
        }
    }
    public void startUserModePictureUpload(){
        newPicCount = 0;
        for(int i=0; i < getObjectPicturesArrayList().size(); i++){
            if(getObjectPicturesArrayList().get(i).getUserId().equals(myUser.getId())){
              newPicCount += 1;
              Thread thread = new Thread(new RunnableTask(this, getObjectPicturesArrayList().get(i)));
              thread.start();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_save:
                setBackButtonCount(0);

                //---cehck if fields are not empty
                setFieldCheckError(false);
                if(isFieldValueError(this.objectObject.getObjectName(), this.oName)){
                    setFieldCheckError(true);
                }
                if(isFieldValueError(this.objectObject.getCustomerName(),  this.oCustomer)){
                    setFieldCheckError(true);
                }
                if(isFieldValueError(this.objectObject.getObjectAddress(), this.oAddress)){
                    setFieldCheckError(true);
                }

                if(!isFieldCheckError()){
                    findViewById(R.id.item_save).setEnabled(false);
                    findViewById(R.id.item_cancel).setEnabled(false);
                    findViewById(R.id.objectEdit_retractable_button).setEnabled(false);
                    findViewById(R.id.objectEdit_add_job_button).setEnabled(false);
                    findViewById(R.id.objectEdit_add_job_button).setBackground(getDrawable(R.drawable.round_button_grey));
                    findViewById(R.id.objectEdit_date).setEnabled(false);
                    findViewById(R.id.objectEdit_to_chat_button).setEnabled(false);
                    findViewById(R.id.objectEdit_to_chat_button).setBackground(getDrawable(R.drawable.round_button_grey));
                    findViewById(R.id.objectEdit_objectName).setEnabled(false);
                    findViewById(R.id.objectEdit_customerName).setEnabled(false);
                    findViewById(R.id.objectEdit_objectAddress).setEnabled(false);
                    findViewById(R.id.objectEdit_job_recycle_view).setEnabled(false);
                    setSaveMode(true);

                    //---- Disable View Buttons while saving
                    //if (!isUserMode()) {
                        for (int i = 0; i < objectDetailsArrayList.size(); i++) {
                            try {
                                objectDetailsArrayList.get(i).getHolder().oDCompleteJob.setEnabled(false);
                                objectDetailsArrayList.get(i).getHolder().oDAddFotoButton.setEnabled(false);
                                objectDetailsArrayList.get(i).getHolder().oDTakeFotoButton.setEnabled(false);
                                objectDetailsArrayList.get(i).getHolder().oDAddFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_grey_light));
                                objectDetailsArrayList.get(i).getHolder().oDTakeFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_grey_light));
                                objectDetailsArrayList.get(i).getHolder().oDRetractableButton.setEnabled(false);
                                objectDetailsArrayList.get(i).getHolder().oDRetractableButtonExtended.setEnabled(false);
                                objectDetailsArrayList.get(i).getHolder().oDRetractableButtonToTopExtended.setEnabled(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    //}
                    for(int i = 0; i < objectPicturesArrayList.size();i++){
                        try {
                            objectPicturesArrayList.get(i).getHolder().myImageUplLock.setVisibility(View.VISIBLE);
                            objectPicturesArrayList.get(i).getHolder().myImage.setEnabled(false);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    //myAdapterObjectEdit.notifyDataSetChanged();
                    new HttpsRequestSaveObject(this).execute();
                }else{
                    Button retractableButton = findViewById(R.id.objectEdit_retractable_button);
                    retractableButton.setSoundEffectsEnabled(false);
                    retractableButton.performClick();
                    retractableButton.setSoundEffectsEnabled(true);
                }
                break;
            case R.id.item_cancel:
                onBackPressed();
                break;

        }
        return false;
    }
    public void calculateCompletness(){
        int count = 0;
        for(int i = 0; i < objectDetailsArrayList.size(); i++){
            if(objectDetailsArrayList.get(i).getCompleted().equals("X")){
                count += 1;
            }
        }
        int total = objectDetailsArrayList.size();
        int diff = total - count;

        double completness = Math.round(( 100 - ((double)(total-count)/total) * 100.0));
        int oldValue = 0;
        try{
            oldValue = Integer.parseInt(objectObject.getCompleteness());
        }catch (Exception e){

        }
        objectObject.setCompleteness(new DecimalFormat("##.#").format(completness));
        oProgressbar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(objectObject.getCompleteness())))));
        oProgressbarUserMode.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(objectObject.getCompleteness())))));
        oProgressBarLabel.setText(objectObject.getCompleteness()+"%");
        oProgressBarLabelUserMode.setText(objectObject.getCompleteness()+"%");
        oJobs.setText(String.valueOf(total));
        oJobsUserMode.setText(String.valueOf(total));
        oJobsDone.setText(String.valueOf(count));
        oJobsDoneUserMode.setText(String.valueOf(count));

        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(oProgressbar, "progress", oldValue,Integer.parseInt(String.valueOf(Math.round(Double.valueOf(objectObject.getCompleteness())))));
        objectAnimator.setDuration(400);
        objectAnimator.start();
        ObjectAnimator objectAnimatorUserMode = ObjectAnimator.ofInt(oProgressbarUserMode, "progress", oldValue,Integer.parseInt(String.valueOf(Math.round(Double.valueOf(objectObject.getCompleteness())))));
        objectAnimatorUserMode.setDuration(400);
        objectAnimatorUserMode.start();
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }
    @Override
    public void afterTextChanged(Editable s) {
        if(!this.objectObject.getObjectName().equals(this.oName.getText().toString())){
            oNameLb.setText(this.oName.getText().toString());
            this.objectObject.setObjectName(this.oName.getText().toString());
            setNeedSave(true);
        }
        if(!this.objectObject.getCustomerName().equals(this.oCustomer.getText().toString())){
            this.objectObject.setCustomerName(this.oCustomer.getText().toString());
            setNeedSave(true);
        }
        if(!this.objectObject.getObjectAddress().equals(this.oAddress.getText().toString())){
            this.objectObject.setObjectAddress(this.oAddress.getText().toString());
            setNeedSave(true);
        }
        if((this.needSave == true)&&(!oSavedStatusIndicator.getColorFilter().equals(ContextCompat.getColor(this, R.color.jerry_yellow)))){
            oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_yellow));
        }
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        //-----------------Hide Keyboard--------------------------
        View view = this.getCurrentFocus();

        //-----------------Key "ENTER" + Key "BACK"---------------
        if ((view != null) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            onClick(view);

            Boolean textChanged = false;

        }
        return false;
    }
    public void setDeleteButtonVisibility(Boolean value){
        TransitionManager.beginDelayedTransition(oDeleteJobButtonLayout, new AutoTransition());
        if(value.equals(true)){
            oDeleteJobButtonLayout.setVisibility(View.VISIBLE);
            //this.oDeleteJobButton.setVisibility(View.VISIBLE);
        }else{
            oDeleteJobButtonLayout.setVisibility(View.GONE);
            //this.oDeleteJobButton.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View v) {

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getAdatpreWa().onRequestPermissionsResult(requestCode, permissions, grantResults, getActionTypeWa());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getAdatpreWa().onActivityResult(requestCode, resultCode, data, this.getHolderWa(), RESULT_OK, getActionTypeWa());
    }
    public void setCallbackAdapterReference(MyAdapterObjectEdit adapter, MyAdapterObjectEdit.MyViewHolder holder, String actionType) {
        this.adatpreWa    = adapter;
        this.holderWa     = holder;
        this.actionTypeWa = actionType;
    }
    public void clearCallbackAdapterReference() {
        this.adatpreWa    = null;
        this.holderWa     = null;
        this.actionTypeWa = null;
    }
    public boolean isNeedSave() {
        return needSave;
    }
    public void setNeedSave(boolean needSave) {
        this.needSave = needSave;
    }
    public ArrayList<ObjectObjDetails> getObjectDetailsArrayList() {
        return objectDetailsArrayList;
    }
    public ArrayList<ObjectObjPic> getObjectPicturesArrayList() {
        return objectPicturesArrayList;
    }
    public void setObjectPicturesArrayList(ArrayList<ObjectObjPic> objectPicturesArrayList) {
        this.objectPicturesArrayList = objectPicturesArrayList;
    }
    public void setObjectDetailsArrayList(ArrayList<ObjectObjDetails> objectDetailsArrayList) {
        //this.objectDetailsArrayList = new ArrayList<>();
        //this.objectDetailsArrayList.addAll(objectDetailsArrayList);
        this.objectDetailsArrayList = objectDetailsArrayList;
    }
    public void copyFileStream(File dest, Uri uri, Context context)throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
            os.close();
        }
    }

    public static String getPath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // DocumentProvider
            if (DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public ObjectObject getObjectObject() {      return objectObject;    }
    public void setObjectObject(ObjectObject objectObject) {  this.objectObject = objectObject;   }
    public ArrayList<ObjectObjDetails> getObjectObjDetailsToUpdateArrayList() {       return objectObjDetailsToUpdateArrayList;   }
    public void setObjectObjDetailsToUpdateArrayList(ArrayList<ObjectObjDetails> objectObjDetailsToUpdateArrayList) {      this.objectObjDetailsToUpdateArrayList = objectObjDetailsToUpdateArrayList;    }
    public ObjectUser getMyUser() {
        return myUser;
    }
    public void setMyUser(ObjectUser myUser) {
        this.myUser = myUser;
    }
    public Boolean getDeletionMode() {
        return deletionMode;
    }
    public void setDeletionMode(Boolean deletionMode) {
        this.deletionMode = deletionMode;
    };
    public ObjectObjDetails getObjectObjDetailsToUpdate() {        return objectObjDetailsToUpdate;    }
    public void setObjectObjDetailsToUpdate(ObjectObjDetails objectObjDetailsToUpdate) {     this.objectObjDetailsToUpdate = objectObjDetailsToUpdate;    }
    public MyAdapterObjectEdit getAdatpreWa() {        return adatpreWa;    }
    public MyAdapterObjectEdit.MyViewHolder getHolderWa() {        return holderWa;    }
    public String getActionTypeWa() {  return actionTypeWa;    }
    public boolean isFieldCheckError() {        return fieldCheckError;    }
    public void setFieldCheckError(boolean fieldCheckError) {   this.fieldCheckError = fieldCheckError;    }
    public boolean isSaveMode() {  return saveMode;  }
    public void setSaveMode(boolean saveMode) {  this.saveMode = saveMode;  }
    public Integer getBackButtonCount() {       return backButtonCount;    }
    public void setBackButtonCount(Integer backButtonCount) {       this.backButtonCount = backButtonCount;    }
    public boolean isUserMode() {        return userMode;    }
    public void setUserMode(boolean userMode) {        this.userMode = userMode;    }
    public boolean isObjectLocked() { return objectLocked;   }
    public void setObjectLocked(boolean objectLocked) {      this.objectLocked = objectLocked;   }

    public void refreshUserMode(){
        if(newPicCount == retutnThreadCount ){
            new HttpsRequestGetObjectDetails(this, objectObject).execute();
        }
    }
    public void refresh(){
        if(newPicCount == retutnThreadCount ){
            newPicCount = 0;
            retutnThreadCount = 0;
            findViewById(R.id.item_save).setEnabled(true);
            findViewById(R.id.item_cancel).setEnabled(true);
            findViewById(R.id.objectEdit_retractable_button).setEnabled(true);
            findViewById(R.id.objectEdit_add_job_button).setEnabled(true);
            findViewById(R.id.objectEdit_add_job_button).setBackground(getDrawable(R.drawable.round_button));
            findViewById(R.id.objectEdit_date).setEnabled(true);
            findViewById(R.id.objectEdit_to_chat_button).setEnabled(true);
            findViewById(R.id.objectEdit_to_chat_button).setBackground(getDrawable(R.drawable.round_button));
            findViewById(R.id.objectEdit_objectName).setEnabled(true);
            findViewById(R.id.objectEdit_customerName).setEnabled(true);
            findViewById(R.id.objectEdit_objectAddress).setEnabled(true);
            findViewById(R.id.objectEdit_job_recycle_view).setEnabled(true);
            setSaveMode(false);

            //---- Enable View Buttons while saving
            for(int i = 0; i < getObjectDetailsArrayList().size();i++){
                try {
                    getObjectDetailsArrayList().get(i).getHolder().oDCompleteJob.setEnabled(true);
                    getObjectDetailsArrayList().get(i).getHolder().oDAddFotoButton.setEnabled(true);
                    getObjectDetailsArrayList().get(i).getHolder().oDTakeFotoButton.setEnabled(true);
                    getObjectDetailsArrayList().get(i).getHolder().oDAddFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_blue));
                    getObjectDetailsArrayList().get(i).getHolder().oDTakeFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_blue));
                    getObjectDetailsArrayList().get(i).getHolder().oDRetractableButton.setEnabled(true);
                    getObjectDetailsArrayList().get(i).getHolder().oDRetractableButtonExtended.setEnabled(true);
                    getObjectDetailsArrayList().get(i).getHolder().oDRetractableButtonToTopExtended.setEnabled(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            for(int i = 0; i < getObjectPicturesArrayList().size();i++){
                try {
                    getObjectPicturesArrayList().get(i).getHolder().myImageUplLock.setVisibility(View.GONE);
                    getObjectPicturesArrayList().get(i).getHolder().myImage.setEnabled(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            handleUserMode();

            //myAdapterObjectEdit.notifyDataSetChanged();
            retutnThreadCount = 0;
            setNeedSave(false);
            oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_green));
            if(!isObjectLocked()){
                Toast.makeText( this, "Išsaugota", Toast.LENGTH_SHORT).show();
            }else{
                setObjectLocked(false);
            }

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
                    //---here actions than should continue if session still valid
                    editInvisibleFocusHolder.setInputType(InputType.TYPE_NULL);
                    editInvisibleFocusHolder.requestFocus();
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

    class HttpsRequestUploadPictures extends AsyncTask<String, String, InputStream> {
        private static final String upload_picture_url = "upload_picture.php";
        private Context context;
        Connector connector;
        ObjectObjPic objectObjPic;

        public HttpsRequestUploadPictures(Context ctx, ObjectObjPic objectPic){
            context = ctx;
            objectObjPic = objectPic;
        }

        @Override
        protected void onProgressUpdate(String... value) {
            try {
                if (value[0].equals("start")) {
                    objectObjPic.getHolder().myImageUpl.setVisibility(View.GONE);
                    objectObjPic.getHolder().myProgressBarUpl.setVisibility(View.VISIBLE);
                    objectObjPic.getHolder().myProgressBarUpl.setProgress(0);
                } else if(value[0].equals("one")) {
                    objectObjPic.getHolder().myProgressBarUpl.setProgress(30);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(objectObjPic.getHolder().myProgressBarUpl, "progress", 5,30);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("two")) {
                    objectObjPic.getHolder().myProgressBarUpl.setProgress(85);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(objectObjPic.getHolder().myProgressBarUpl, "progress", 30,80);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("three")) {
                    objectObjPic.getHolder().myProgressBarUpl.setProgress(95);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(objectObjPic.getHolder().myProgressBarUpl, "progress", 80,95);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("four")) {
                    objectObjPic.getHolder().myProgressBarUpl.setProgress(99);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofInt(objectObjPic.getHolder().myProgressBarUpl, "progress", 95,100);
                    objectAnimator.setDuration(3000);
                    objectAnimator.start();
                } else if(value[0].equals("done")) {
                    objectObjPic.getHolder().myProgressBarUpl.setProgress(100);
                    objectObjPic.getHolder().myImageUpl.setVisibility(View.GONE);
                    objectObjPic.getHolder().myProgressBarUpl.setVisibility(View.GONE);
                    objectObjPic.getHolder().myImageUplLock.setVisibility(View.GONE);
                    //refresh();
                }
            }catch (Exception e){
                try {
                    objectObjPic.getHolder().myImageUpl.setVisibility(View.GONE);
                    objectObjPic.getHolder().myImageUplFailed.setVisibility(View.VISIBLE);
                    objectObjPic.getHolder().myImageUplFailed.setColorFilter(context.getResources().getColor(R.color.jerry_red), PorterDuff.Mode.SRC_ATOP);
                }catch (Exception ee){
                    ee.printStackTrace();
                }
            }

            super.onProgressUpdate(value);
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            publishProgress("start");
            connector = new Connector(context, upload_picture_url);
            connector.addPostParameter("objectId",      MCrypt2.encodeToString(String.valueOf(objectObject.getId())));
            connector.addPostParameter("pictureName",   MCrypt2.encodeToString(objectObjPic.getPicName()));
            publishProgress("one");
            connector.addPostParameter("pictureSource", objectObjPic.ImgSource(context));
            publishProgress("two");
            connector.send();
            publishProgress("three");
            connector.receive();
            publishProgress("four");
            connector.disconnect();
            String result = connector.getResult();
            publishProgress("done");
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
                    objectObjPic.setPicUrl(msg);
                    objectObjPic.setPicUri("");
                }else{
                    //error handling?
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            backgroundJobs += 1;
            retutnThreadCount += 1;
            if(!isUserMode()){
                refresh();
            }else{
                refreshUserMode();
            }
            super.onPostExecute(inputStream);
        }
        private String getPicArrayListJson(ArrayList<ObjectObjPic> pictureList){
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i < pictureList.size(); i++){
                jsonArray.put(pictureList.get(i).toJson());
            }
            return jsonArray.toString();
        }
        private String getDetailsArrayListJson(ArrayList<ObjectObjDetails> detailsList){
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i < detailsList.size(); i++){
                jsonArray.put(detailsList.get(i).toJson());
            }
            return jsonArray.toString();
        }
    }

    class HttpsRequestSaveObject extends AsyncTask<String, Void, InputStream> {
        private static final String save_object_url = "save_object.php";
        private static final String save_user_object_detail = "save_user_object_details.php";
        private Context context;
        Connector connector;

        public HttpsRequestSaveObject(Context ctx){
            context = ctx;
        }
        @Override
        protected InputStream doInBackground(String... strings) {
            if (!isUserMode()){
                connector = new Connector(context, save_object_url);
                connector.addPostParameter("objectObject", MCrypt2.encodeToString(objectObject.toJson()));
                connector.addPostParameter("detailsList",  MCrypt2.encodeToString(getDetailsArrayListJson(getObjectDetailsArrayList())));
                connector.addPostParameter("pictureList",  getPicArrayListJson(getObjectPicturesArrayList()));
                connector.send();
                connector.receive();
                connector.disconnect();
                String result = connector.getResult();
                result = result;
            }else{
                connector = new Connector(context, save_user_object_detail);
                connector.addPostParameter("objectObject",  MCrypt2.encodeToString(objectObject.toJson()));
                connector.addPostParameter("detailsList",   MCrypt2.encodeToString(getDetailsArrayListJson(getObjectObjDetailsToUpdateArrayList())));
                connector.addPostParameter("userId",        MCrypt2.encodeToString(myUser.getId().toString()));
                connector.addPostParameter("pictureList",   getUserModePicArrayListJson(getObjectPicturesArrayList()));
                connector.send();
                connector.receive();
                connector.disconnect();
                String result = connector.getResult();
                result = result;
            }
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if(!isUserMode()){
                try {
                    connector.decodeResponse();
                    JSONObject object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));
                    String save_status = object.getString("status");
                    if ((save_status.equals("1"))&&(objectObject.getId().equals(-1))) {
                       objectObject.setId(Integer.parseInt(object.getString("object_id")));
                       for(int i = 0; i< objectPicturesArrayList.size(); i++){
                           objectPicturesArrayList.get(i).setObjectId(objectObject.getId());
                       }
                    }
                    if(objectPicturesArrayList.size() > 0){
                        startPictureUpload();
                    }else{
                        refresh();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                getObjectObjDetailsToUpdateArrayList().removeAll(getObjectObjDetailsToUpdateArrayList());
                try {
                    connector.decodeResponse();
                    JSONObject object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));
                    String save_status = object.getString("status");
                    String msg = object.getString("msg");
                    if (save_status.equals("0")){
                    }else if(save_status.equals("1")){
                        boolean hasPictures = false;
                        for(int i=0; i<objectPicturesArrayList.size(); i++){
                            if(objectPicturesArrayList.get(i).getUserId().equals(myUser.getId())){
                                startUserModePictureUpload();
                                hasPictures = true;
                                break;
                            }
                        }
                        if (!hasPictures){
                            new HttpsRequestGetObjectDetails(context, objectObject).execute();
                        }
                    }else if(save_status.equals("2")){
                        setObjectLocked(true);
                        Toast.makeText(context, "Užrakinta. Redaguoja: " +  msg, Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                //refresh();
                //new HttpsRequestGetObjectDetails(context, objectObject).execute();
            }
            super.onPostExecute(inputStream);
        }
        private String getPicArrayListJson(ArrayList<ObjectObjPic> pictureList){
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i < pictureList.size(); i++){
                jsonArray.put(pictureList.get(i).toJson());
            }
            return jsonArray.toString();
        }
        private String getUserModePicArrayListJson(ArrayList<ObjectObjPic> pictureList){
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < getObjectObjDetailsToUpdateArrayList().size(); i++){
                for(int j = 0; j < pictureList.size(); j++){
                    if(getObjectObjDetailsToUpdateArrayList().get(i).getPosNr().equals(pictureList.get(j).getPosNr())) {
                        if ((pictureList.get(j).getUserId().equals(myUser.getId()))) {
                            jsonArray.put(pictureList.get(j).toJson());
                        }
                    }
                }
            }
            return jsonArray.toString();
        }
        private String getDetailsArrayListJson(ArrayList<ObjectObjDetails> detailsList){
            JSONArray jsonArray = new JSONArray();
            for(int i=0; i < detailsList.size(); i++){
                jsonArray.put(detailsList.get(i).toJson());
            }
            return jsonArray.toString();
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

        public ObjectObject getClickObject() { return clickObject; }
        public void setClickObject(ObjectObject clickObject) { this.clickObject = clickObject; }

        @Override
        protected InputStream doInBackground(String... strings) {
            connector = new Connector(context, get_object_details_url);
            connector.addPostParameter("object_id", Base64.encodeToString(MCrypt.encrypt(String.valueOf(clickObject.getId()).getBytes()), Base64.DEFAULT));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {

            //connector.decodeResponse();
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
                getObjectDetailsArrayList().removeAll(getObjectDetailsArrayList());
                getObjectDetailsArrayList().addAll(objDetailsArrayList);
                //setObjectDetailsArrayList(objDetailsArrayList);

                for(int i = 0; i < responseObjUser.length(); i++){
                    ObjectObjUser objectObjUser = new ObjectObjUser((JSONObject) responseObjUser.get(i));
                    objUserArrayList.add(objectObjUser);
                }
                //setObjectUserArrayList(objUserArrayList);

                for(int i = 0; i < responseObjPic.length(); i++){
                    ObjectObjPic objectObjPic = new ObjectObjPic((JSONObject) responseObjPic.get(i));
                    objPicsArrayList.add(objectObjPic);
                }

                getObjectPicturesArrayList().removeAll(getObjectPicturesArrayList());
                getObjectPicturesArrayList().addAll(objPicsArrayList);
                //setObjectPicturesArrayList(objPicsArrayList);

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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ((ActivityObjectEdit)context).setEmployeeArrayList(employeeArrayList);
            ((ActivityObjectEdit)context).setOwnerArrayList(ownerArrayList);

            oDate.setText(getClickObject().getDate());
            oName.setText(getClickObject().getObjectName());
            oCustomer.setText(getClickObject().getCustomerName());
            oAddress.setText(getClickObject().getObjectAddress());
            calculateCompletness();

            myAdapterObjectEdit.getMyObjectList().removeAll(myAdapterObjectEdit.getMyObjectList());
            myAdapterObjectEdit.getMyObjectList().addAll(objDetailsArrayList);
            /*myAdapterObjectEdit.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    refresh();
                }

            });*/
            View.OnLayoutChangeListener layoutChangeListener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    refresh();
                    recyclerView.removeOnLayoutChangeListener(this);
                }
            };
            recyclerView.addOnLayoutChangeListener(layoutChangeListener);
            myAdapterObjectEdit.notifyDataSetChanged();

            //refresh();
            super.onPostExecute(inputStream);
        }
    }
}