package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
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

    boolean needSave, deletionMode, saveMode, fieldCheckError;
    FloatingActionButton oDeleteJobButton;
    LinearLayout oDeleteJobButtonLayout;
    Integer backButtonCount;
    MyAdapterObjectEdit adatpreWa;
    MyAdapterObjectEdit.MyViewHolder holderWa;
    String actionTypeWa;
    private int backgroundJobs = 10;
    private int newPicCount = 0;
    private int retutnThreadCount = 0;

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
        //this.backButtonCount = 0;
        setBackButtonCount(0);
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
            setBackButtonCount(0);
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
            setBackButtonCount(0);
            //--------sort list dscending
            Collections.sort(toBeDeletedList, new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return o2.compareTo(o1);
                }
            });
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
        LinearLayout retractableLayout     = findViewById(R.id.objectEdit_retractable_layout);
        LinearLayout retractableLayoutLine = findViewById(R.id.objectEdit_retractableLine);
        Button retractableButton           = findViewById(R.id.objectEdit_retractable_button);
        retractableButton.setOnClickListener(v -> {
            setBackButtonCount(0);
            hideSoftKeyboard();
            if(retractableLayout.getVisibility()==View.GONE){
                TransitionManager.beginDelayedTransition(retractableLayout, new AutoTransition());
                retractableLayout.setVisibility(View.VISIBLE);
                retractableButton.setBackgroundResource(R.drawable.ic_arrow_up_white);
            }else{
                //TransitionManager.beginDelayedTransition(retractableLayout, new AutoTransition());
                retractableLayout.setVisibility(View.GONE);
                retractableButton.setBackgroundResource(R.drawable.ic_arrow_down_white);
            }
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

            ObjectObjDetails newObjectObjDetails = new ObjectObjDetails();
            //newObjectObjDetails.setObjectId(objectObject.getId());
            objectDetailsArrayList.add(0, newObjectObjDetails);
            for(int i = 0; i < objectDetailsArrayList.size(); i++ ){
                objectDetailsArrayList.get(i).setPosNr(i);
            }
            for(int i = 0; i < objectPicturesArrayList.size(); i++){
                objectPicturesArrayList.get(i).setPosNr(objectPicturesArrayList.get(i).getPosNr()+1);
            }

            //---- on adde Job recalculate completness
            calculateCompletness();

            myAdapterObjectEdit.notifyDataSetChanged();
            oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_yellow));
            setNeedSave(true);
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
            int completeCount = 0;

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

            //-----------back button press handling: in case object not saved - show warning
            if ((backButtonCount.equals(0)) && (isNeedSave())) {
                Toast.makeText(this, getResources().getString(R.string.not_saved), Toast.LENGTH_SHORT).show();
                backButtonCount++;
            } else {
                //getIntent().removeExtra("myBaustelle");
                //backButtonCount = 0;
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
                    findViewById(R.id.objectEdit_objectName).setEnabled(false);
                    findViewById(R.id.objectEdit_customerName).setEnabled(false);
                    findViewById(R.id.objectEdit_objectAddress).setEnabled(false);
                    findViewById(R.id.objectEdit_job_recycle_view).setEnabled(false);
                    setSaveMode(true);

                    //---- Disable View Buttons while saving
                    for(int i = 0; i < objectDetailsArrayList.size();i++){
                        try {
                           objectDetailsArrayList.get(i).getHolder().oDCompleteJob.setEnabled(false);
                           objectDetailsArrayList.get(i).getHolder().oDAddFotoButton.setEnabled(false);
                           objectDetailsArrayList.get(i).getHolder().oDTakeFotoButton.setEnabled(false);
                           objectDetailsArrayList.get(i).getHolder().oDAddFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_grey_light));
                           objectDetailsArrayList.get(i).getHolder().oDTakeFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_grey_light));
                           objectDetailsArrayList.get(i).getHolder().oDRetractableButton.setEnabled(false);
                           objectDetailsArrayList.get(i).getHolder().oDRetractableButtonExtended.setEnabled(false);
                           objectDetailsArrayList.get(i).getHolder().oDRetractableButtonToTopExtended.setEnabled(false);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    for(int i = 0; i < objectPicturesArrayList.size();i++){
                        try {
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
        int oldValue = Integer.parseInt(objectObject.getCompleteness());
        objectObject.setCompleteness(new DecimalFormat("##.#").format(completness));
        oProgressbar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(objectObject.getCompleteness())))));
        oProgressBarLabel.setText(objectObject.getCompleteness()+"%");
        oJobs.setText(String.valueOf(total));
        oJobsDone.setText(String.valueOf(count));

        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(oProgressbar, "progress", oldValue,Integer.parseInt(String.valueOf(Math.round(Double.valueOf(objectObject.getCompleteness())))));
        objectAnimator.setDuration(400);
        objectAnimator.start();
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
    public boolean isFieldCheckError() {
        return fieldCheckError;
    }
    public void setFieldCheckError(boolean fieldCheckError) {   this.fieldCheckError = fieldCheckError;    }
    public boolean isSaveMode() {  return saveMode;  }
    public void setSaveMode(boolean saveMode) {  this.saveMode = saveMode;  }
    public Integer getBackButtonCount() {       return backButtonCount;    }
    public void setBackButtonCount(Integer backButtonCount) {       this.backButtonCount = backButtonCount;    }

    public void refresh(){
        if(newPicCount == retutnThreadCount ){
            findViewById(R.id.item_save).setEnabled(true);
            findViewById(R.id.item_cancel).setEnabled(true);
            findViewById(R.id.objectEdit_retractable_button).setEnabled(true);
            findViewById(R.id.objectEdit_add_job_button).setEnabled(true);
            findViewById(R.id.objectEdit_add_job_button).setBackground(getDrawable(R.drawable.round_button));
            findViewById(R.id.objectEdit_date).setEnabled(true);
            findViewById(R.id.objectEdit_objectName).setEnabled(true);
            findViewById(R.id.objectEdit_customerName).setEnabled(true);
            findViewById(R.id.objectEdit_objectAddress).setEnabled(true);
            findViewById(R.id.objectEdit_job_recycle_view).setEnabled(true);
            setSaveMode(false);

            //---- Enable View Buttons while saving
            for(int i = 0; i < objectDetailsArrayList.size();i++){
                try {
                    objectDetailsArrayList.get(i).getHolder().oDCompleteJob.setEnabled(true);
                    objectDetailsArrayList.get(i).getHolder().oDAddFotoButton.setEnabled(true);
                    objectDetailsArrayList.get(i).getHolder().oDTakeFotoButton.setEnabled(true);
                    objectDetailsArrayList.get(i).getHolder().oDAddFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_blue));
                    objectDetailsArrayList.get(i).getHolder().oDTakeFotoButton.setBackgroundColor(getResources().getColor(R.color.jerry_blue));
                    objectDetailsArrayList.get(i).getHolder().oDRetractableButton.setEnabled(true);
                    objectDetailsArrayList.get(i).getHolder().oDRetractableButtonExtended.setEnabled(true);
                    objectDetailsArrayList.get(i).getHolder().oDRetractableButtonToTopExtended.setEnabled(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            for(int i = 0; i < objectPicturesArrayList.size();i++){
                try {
                    objectPicturesArrayList.get(i).getHolder().myImage.setEnabled(true);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //myAdapterObjectEdit.notifyDataSetChanged();
            retutnThreadCount = 0;
            setNeedSave(false);
            oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_green));
            Toast.makeText( this, "Išsaugota", Toast.LENGTH_SHORT).show();
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
            refresh();
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
        private Context context;
        Connector connector;

        public HttpsRequestSaveObject(Context ctx){
            context = ctx;
        }
        @Override
        protected InputStream doInBackground(String... strings) {
            connector = new Connector(context, save_object_url);
            connector.addPostParameter("objectObject", MCrypt2.encodeToString(objectObject.toJson()));
            connector.addPostParameter("detailsList",  MCrypt2.encodeToString(getDetailsArrayListJson(getObjectDetailsArrayList())));
            connector.addPostParameter("pictureList",  getPicArrayListJson(getObjectPicturesArrayList()));
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
}