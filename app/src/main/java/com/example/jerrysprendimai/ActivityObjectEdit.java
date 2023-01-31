package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class ActivityObjectEdit extends AppCompatActivity implements View.OnClickListener, KeyboardVisibilityEventListener, BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher, View.OnKeyListener {

    public static final int REQUEST_CODE = 101;

    private ArrayList<ObjectObjUser> objectUserArrayList;
    private ArrayList<ObjectObjDetails> objectDetailsArrayList;
    private ArrayList<ObjectObjPic> objectPicturesArrayList;
    BottomNavigationView bottomNavigationView;
    ObjectObject objectObject;
    ObjectUser myUser;
    ArrayList<Integer> toBeDeletedList;

    RecyclerView recyclerView;
    MyAdapterObjectEdit myAdapterObjectEdit;
    ScrollView scrollView;

    EditText editInvisibleFocusHolder;
    ImageView oSavedStatusIndicator;
    Button oAddJob;
    TextView oId, oNameLb, oDate, oName, oCustomer, oAddress, oJobs, oJobsDone, oProgressBarLabel;
    ProgressBar oProgressbar;

    boolean needSave, deletionMode;
    FloatingActionButton oDeleteJobButton;
    LinearLayout oDeleteJobButtonLayout;
    Integer backButtonCount;
    MyAdapterObjectEdit adatpreWa;
    MyAdapterObjectEdit.MyViewHolder holderWa;
    String actionTypeWa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_edit);

        this.myUser       = getIntent().getParcelableExtra("myUser");
        this.objectObject = getIntent().getParcelableExtra("objectObject");
        this.objectDetailsArrayList  = getIntent().getParcelableArrayListExtra("listDetails");
        this.objectUserArrayList     = getIntent().getParcelableArrayListExtra("listtUser");
        this.objectPicturesArrayList = getIntent().getParcelableArrayListExtra("listPictures");

        //-----------------BackButton press counter
        this.backButtonCount = 0;
        this.needSave = false;

        //-----------------Deletion Mode
        this.deletionMode = false;
        this.toBeDeletedList = new ArrayList<Integer>();

        //-----------------View element binding----------------
        this.editInvisibleFocusHolder = findViewById(R.id.invisibleFocusHolder);
        this.oId                      = findViewById(R.id.objectEdit_id);
        this.oNameLb                  = findViewById(R.id.objectEdit_name);
        this.oSavedStatusIndicator    = findViewById(R.id.objectEdit_savedStatus_img);
        this.oDate                    = findViewById(R.id.objectEdit_date);
        this.oName                    = findViewById(R.id.objectEdit_objectName);
        this.oCustomer                = findViewById(R.id.objectEdit_customerName);
        this.oAddress                 = findViewById(R.id.objectEdit_objectAddress);
        this.oJobs                    = findViewById(R.id.objectEdit_objectJobs);
        this.oJobsDone                = findViewById(R.id.objectEdit_objectJobsDone);
        this.oProgressbar             = findViewById(R.id.objectEdit_progess_bar);
        this.oProgressBarLabel        = findViewById(R.id.objectEdit_progess_bar_label);
        this.oAddJob                  = findViewById(R.id.objectEdit_add_job_button);
        this.scrollView               = findViewById(R.id.objectEdit_job_scroll_view);
        this.oDeleteJobButton         = findViewById(R.id.objectEdit_delete_job);
        this.oDeleteJobButtonLayout   = findViewById(R.id.objectEdit_delete_job_layout);

        fillFieldValues();
        buildRecyclerView();

        //--------------setting event listeners---------------
        this.oName.addTextChangedListener(this);
        this.oCustomer.addTextChangedListener(this);
        this.oAddress.addTextChangedListener(this);
        this.oName.setOnKeyListener(this);
        this.oCustomer.setOnKeyListener(this);
        this.oAddress.setOnKeyListener(this);

        //---- Date Picker handling
        Context context = this;
        this.oDate.setOnClickListener(v->{
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_date_picker, null, false);
            TextView dialogDateLabel = dialogView.findViewById(R.id.datePicker_date_label);
            CalendarView datePickerCalender = dialogView.findViewById(R.id.datePicker_calenderView);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, Integer.parseInt(DateHelper.get_YMD_from_date_display("year",        this.objectObject.getDate())));
            calendar.set(Calendar.MONTH, Integer.parseInt(DateHelper.get_YMD_from_date_display("month",      this.objectObject.getDate())) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(DateHelper.get_YMD_from_date_display("day", this.objectObject.getDate())));
            dialogDateLabel.setText(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
            long milliTime = calendar.getTimeInMillis();
            datePickerCalender.setDate(milliTime);
            datePickerCalender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month );
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    dialogDateLabel.setText(DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime()));
                    long milliTime = calendar.getTimeInMillis();
                    datePickerCalender.setDate(milliTime);
                }
            });
            AlertDialog.Builder datePickerDialog = new AlertDialog.Builder((Context) this, R.style.AlertDialogTheme);
            datePickerDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String dialogDate = dateFormat.format(datePickerCalender.getDate());
                    objectObject.setDate(dialogDate);
                    oDate.setText(dialogDate);

                    setNeedSave(true);
                    oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                }
            });
            datePickerDialog.setNegativeButton("Cancel", null);
            datePickerDialog.setView(dialogView);
            datePickerDialog.create();
            datePickerDialog.show();
        });
        //---- Add Job button handling
        this.oDeleteJobButton.setOnClickListener(v ->{
            //--------sort list dscending
            Collections.sort(toBeDeletedList, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return o2.compareTo(o1);
                }
            });
            for(int i=0; i<toBeDeletedList.size();i++){
                ObjectObjDetails objectObjDetailsToRemove = objectDetailsArrayList.get(toBeDeletedList.get(i));
                objectDetailsArrayList.remove(objectObjDetailsToRemove);
               //objectDetailsArrayList.remove(toBeDeletedList.get(i));
            }
            setDeletionMode(false);
            this.setSaveCancelVisibility(true);
            this.setDeleteButtonVisibility(false);
            this.toBeDeletedList = new ArrayList<Integer>();
            myAdapterObjectEdit.notifyDataSetChanged();
        });

        //---- retractable button/view handling
        LinearLayout retractableLayout     = findViewById(R.id.objectEdit_retractable_layout);
        LinearLayout retractableLayoutLine = findViewById(R.id.objectEdit_retractableLine);
        Button retractableButton           = findViewById(R.id.objectEdit_retractable_button);
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
        retractableLayoutLine.setSoundEffectsEnabled(false);
        retractableLayoutLine.setOnClickListener(v -> retractableButton.performClick());
        retractableLayoutLine.performClick();

        //---- Add Job Button handling
        oAddJob.setOnClickListener(v ->{
            setDeletionMode(false);
            this.setSaveCancelVisibility(true);
            this.setDeleteButtonVisibility(false);
            this.toBeDeletedList = new ArrayList<Integer>();

            ObjectObjDetails newObjectObjDetails = new ObjectObjDetails();
            objectDetailsArrayList.add(0, newObjectObjDetails);
            //myAdapterObjectEdit.setMyObjectList(objectDetailsArrayList);
            //MyAdapterObjectEdit.MyViewHolder newViewHolder = myAdapterObjectEdit.onCreateViewHolder(this.recyclerView, 0);
            //myAdapterObjectEdit.onBindViewHolder(newViewHolder, 0);

            myAdapterObjectEdit.notifyDataSetChanged();
            /*for(int i=0; i<objectDetailsArrayList.size()-1; i++) {
                //myAdapterObjectEdit.onBindViewHolder(myAdapterObjectEdit.getMyHolder(), i);
                myAdapterObjectEdit.notifyItemChanged(i);
            }*/
            oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_yellow));
        });

        //-----------------Save-Cancel Menu Hide/Show depending on the Keyboard----------------
        KeyboardVisibilityEvent.setEventListener(this, this);
        this.bottomNavigationView = findViewById(R.id.save_cancel_buttons);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);


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
            Integer completeCount = 0;

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
            this.oJobsDone.setText(String.valueOf(completeCount));
            this.oProgressBarLabel.setText(String.valueOf(this.objectObject.getCompleteness()) + "%");
            this.oProgressbar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(this.objectObject.getCompleteness())))));

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
        super.onResume();
        editInvisibleFocusHolder.setInputType(InputType.TYPE_NULL);
        editInvisibleFocusHolder.requestFocus();
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
            super.onBackPressed();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                //---cehck if fields are not empty
                //boolean fied1 = checkFieldValue(this.objectUser.getUname(),      this.uUser);
                //boolean fied2 = checkFieldValue(this.objectUser.getPasswd(),     this.uPasswd);
                //boolean fied3 = checkFieldValue(this.objectUser.getFirst_name(), this.uFirstName);
                //boolean fied4 = checkFieldValue(this.objectUser.getLast_name(),  this.uLastName);
                //if((fied1 != true)||(fied2 != true)||(fied3 != true)||(fied4 != true)){
                //    return false;
                //}
                //--save to DB
                //if(needSave == true) {
                //    new ActivityUserEdit.HttpsRequestSaveUser(this).execute();
                //}
                break;
            case R.id.item_cancel:
                //--todo cancel changes
                break;
        }
        return false;
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
        this.objectDetailsArrayList = objectDetailsArrayList;
    }

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
    public MyAdapterObjectEdit getAdatpreWa() {        return adatpreWa;    }
    public MyAdapterObjectEdit.MyViewHolder getHolderWa() {        return holderWa;    }
    public String getActionTypeWa() {  return actionTypeWa;    }
}