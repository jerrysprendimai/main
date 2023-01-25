package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.util.ArrayList;

public class ActivityObjectEdit extends AppCompatActivity implements View.OnClickListener, KeyboardVisibilityEventListener, BottomNavigationView.OnNavigationItemSelectedListener, TextWatcher, View.OnKeyListener {

    ObjectObject objectObject;
    private ArrayList<ObjectObjUser> objectUserArrayList;
    private ArrayList<ObjectObjDetails> objectDetailsArrayList;
    private ArrayList<ObjectObjPic> objectPicturesArrayList;
    BottomNavigationView bottomNavigationView;
    ObjectUser myUser;

    EditText editInvisibleFocusHolder;
    ImageView oSavedStatusIndicator;
    TextView oId, oDate, oName, oCustomer, oAddress;

    boolean needSave;
    Integer backButtonCount;

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

        //-----------------View element binding----------------
        this.editInvisibleFocusHolder = findViewById(R.id.invisibleFocusHolder);
        this.oId                      = findViewById(R.id.objectEdit_id);
        this.oSavedStatusIndicator    = findViewById(R.id.objectEdit_savedStatus_img);
        this.oDate                    = findViewById(R.id.objectEdit_date);
        this.oName                    = findViewById(R.id.objectEdit_objectName);
        this.oCustomer                = findViewById(R.id.objectEdit_customerName);
        this.oAddress                 = findViewById(R.id.objectEdit_objectAddress);

        oSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_blue));

        this.fillFieldValues();

        //--------------setting event listeners---------------
        this.oName.addTextChangedListener(this);
        this.oCustomer.addTextChangedListener(this);
        this.oAddress.addTextChangedListener(this);
        this.oName.setOnKeyListener(this);
        this.oCustomer.setOnKeyListener(this);
        this.oAddress.setOnKeyListener(this);

        //-----------------Bottom Menu Hide/Show depending on the Keyboard----------------
        KeyboardVisibilityEvent.setEventListener(this, this);
        this.bottomNavigationView = findViewById(R.id.save_cancel_buttons);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);

        //ActivityObjectEdit.hideSoftKeyboard(this);

    }

    private void fillFieldValues() {
        try {
            this.oId.setText(this.objectObject.getId().toString());
            this.oDate.setText(this.objectObject.getDate());
            this.oName.setText(this.objectObject.getObjectName());
            this.oCustomer.setText(this.objectObject.getCustomerName());
            this.oAddress.setText(this.objectObject.getObjectAddress());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        editInvisibleFocusHolder.setInputType(InputType.TYPE_NULL);
        editInvisibleFocusHolder.requestFocus();
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
          inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            this.objectObject.setObjectName(this.oName.getText().toString());
        }
        if(!this.objectObject.getCustomerName().equals(this.oCustomer.getText().toString())){
            this.objectObject.setCustomerName(this.oCustomer.getText().toString());
        }
        if(!this.objectObject.getObjectAddress().equals(this.oAddress.getText().toString())){
            this.objectObject.setObjectAddress(this.oAddress.getText().toString());
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

    @Override
    public void onClick(View v) {

    }
}