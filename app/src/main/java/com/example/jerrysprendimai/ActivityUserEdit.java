package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityUserEdit extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnKeyListener, BottomNavigationView.OnNavigationItemSelectedListener, KeyboardVisibilityEventListener {
    CardView uytpeIndicator;
    TextView uTypeLevelIndicatorText, uTypeLevelSwitchButtonText, uTopCaption;
    SwitchCompat uTypeButton;
    ImageView uSavedStatusIndicator;
    TextInputEditText uFirstName, uLastName, uUser, uPasswd, uRegDate, uLastLogin;

    boolean needSave;
    boolean backPressed, backgroudnRunning;
    BottomNavigationView bottomNavigationView;
    ProgressBar progressBar;

    final String user = "user";
    final String owner = "owner";
    final String admin = "admin";

    ObjectUser objectUser;
    ObjectUser myUser;

    Integer backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        //-----------------BackButton press counter
        this.backButtonCount = 0;
        this.needSave = false;
        this.backPressed = false;
        this.backgroudnRunning = false;

        //-----------------View element binding----------------
        this.uytpeIndicator             = findViewById(R.id.user_edit_utypeIndicator);
        this.uTypeLevelIndicatorText    = findViewById(R.id.user_edit_ulevel);
        this.uSavedStatusIndicator      = findViewById(R.id.user_edit_savedStatus_img);
        this.uTopCaption                = findViewById(R.id.user_edit_top_caption);
        this.uUser                      = findViewById(R.id.user_edit_uname_value);
        this.uPasswd                    = findViewById(R.id.user_edit_upasswd_value);
        this.uTypeButton                = findViewById(R.id.user_edit_utype_switchButton);
        this.uTypeLevelSwitchButtonText = findViewById(R.id.user_edit_utype_value);
        this.uFirstName                 = findViewById(R.id.user_edit_fname_value);
        this.uRegDate                   = findViewById(R.id.user_edit_regdate_value);
        this.uLastName                  = findViewById(R.id.user_edit_lname_value);
        this.uLastLogin                 = findViewById(R.id.user_edit_lastLogin_value);

        uSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_blue));
        this.myUser = getIntent().getParcelableExtra("myUser");
        setUserLevelView();

        try{
            this.objectUser = this.getIntent().getParcelableExtra("myUserEdit");
        }catch (Exception e){
            this.objectUser = new ObjectUser();
            this.objectUser.setUser_lv(getResources().getString(R.string.employee));
        }
        if (this.objectUser == null ){
            this.objectUser = new ObjectUser();
            this.objectUser.setUser_lv(getResources().getString(R.string.employee));
        }

        this.fillFieldValues();

        //--------------setting event listeners---------------
        //this.uTypeButton.setOnClickListener(this::onClick);
        this.uTypeButton.setOnCheckedChangeListener(this::onCheckedChanged);
        this.uUser.addTextChangedListener(this);
        this.uPasswd.addTextChangedListener(this);
        this.uFirstName.addTextChangedListener(this);
        this.uLastName.addTextChangedListener(this);
        this.uUser.setOnKeyListener(this::onKey);
        this.uPasswd.setOnKeyListener(this::onKey);
        this.uFirstName.setOnKeyListener(this::onKey);
        this.uLastName.setOnKeyListener(this::onKey);

        //-----------------ProgressBar----------------------------------------------------
        this.progressBar = findViewById(R.id.progressBar);

        //-----------------Bottom Menu Hide/Show depending on the Keyboard----------------
        KeyboardVisibilityEvent.setEventListener(this, this);
        this.bottomNavigationView = findViewById(R.id.save_cancel_buttons);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    private void fillFieldValues() {
        this.uTypeLevelIndicatorText.setText(this.objectUser.getUser_lv());
        this.uUser.setText(this.objectUser.getUname());
        this.uPasswd.setText(this.objectUser.getPasswd());
        this.uFirstName.setText(this.objectUser.getFirst_name());
        this.uLastName.setText(this.objectUser.getLast_name());
        this.uRegDate.setText(this.objectUser.getReg_date());
        this.uLastLogin.setText(this.objectUser.getLast_login());
        this.uTypeLevelSwitchButtonText.setText(this.objectUser.getUser_lv());
        this.uytpeIndicator.setCardBackgroundColor(this.objectUser.getUserLevelIndicatorColor(this));

        uSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_green));

        if(objectUser.getUser_lv().equals(getResources().getString(R.string.employee))){
            this.uTypeButton.setChecked(false);
        }else{
            this.uTypeButton.setChecked(true);
        }

        //------------------in case of admin user--------------
        if(objectUser.getUser_lv().equals(getResources().getString(R.string.admin))){
            this.uTypeButton.setVisibility(View.GONE);

            this.uUser.setFocusable(false);
            this.uUser.setFocusableInTouchMode(false);
            this.uUser.setClickable(false);
            this.uUser.setTextColor(ContextCompat.getColor(this, R.color.jerry_grey));

            this.uPasswd.setFocusable(false);
            this.uPasswd.setFocusableInTouchMode(false);
            this.uPasswd.setClickable(false);
            this.uPasswd.setTextColor(ContextCompat.getColor(this, R.color.jerry_grey));

            this.uFirstName.setFocusable(false);
            this.uFirstName.setFocusableInTouchMode(false);
            this.uFirstName.setClickable(false);
            this.uFirstName.setTextColor(ContextCompat.getColor(this, R.color.jerry_grey));

            this.uLastName.setFocusable(false);
            this.uLastName.setFocusableInTouchMode(false);
            this.uLastName.setClickable(false);
            this.uLastName.setTextColor(ContextCompat.getColor(this, R.color.jerry_grey));
        }
    }
    private void setUserLevelView() {
        if (this.myUser.getUser_lv().equals("user")){
            this.uUser.setFocusable(true);
            this.uUser.setFocusableInTouchMode(true);
            this.uUser.setClickable(true);
            //this.uUser.setTextColor(ContextCompat.getColor(this, R.color.jerry_grey));

            this.uTopCaption.setText(getResources().getString(R.string.my_profile));

            this.uTypeButton.setVisibility(View.GONE);
            this.uTypeLevelSwitchButtonText.setVisibility(View.GONE);
            this.uLastLogin.setVisibility(View.GONE);
        }else{
            this.uTypeLevelSwitchButtonText.setVisibility(View.VISIBLE);
            this.uTypeButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked == true){
            this.objectUser.setUser_lv(getResources().getString(R.string.owner));
            this.objectUser.setType("2");
        }else{
            this.objectUser.setUser_lv(getResources().getString(R.string.employee));
            this.objectUser.setType("3");
        }
        this.uSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_yellow));
        this.setNeedSave(true);
        this.uTypeLevelSwitchButtonText.setText(objectUser.getUser_lv());
        this.uTypeLevelIndicatorText.setText(objectUser.getUser_lv());
        this.uytpeIndicator.setCardBackgroundColor(objectUser.getUserLevelIndicatorColor(this));
    }

    public boolean isNeedSave() {
        return needSave;
    }
    public void setNeedSave(boolean needSave) {
        this.needSave = needSave;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Boolean textChanged = false;

        if (this.objectUser.getUname() != this.uUser.getText().toString()) {
            this.objectUser.setUname(this.uUser.getText().toString().trim());
            //this.uUser.setText(this.uUser.getText().toString().trim());
            textChanged = true;
        }
        if (this.objectUser.getFirst_name() != this.uFirstName.getText().toString()) {
            this.objectUser.setFirst_name(this.uFirstName.getText().toString().trim());
            //this.uFirstName.setText(this.uFirstName.getText().toString().trim());
            textChanged = true;
        }
        if (this.objectUser.getLast_name() != this.uLastName.getText().toString()) {
            this.objectUser.setLast_name(this.uLastName.getText().toString().trim());
            //this.uLastName.setText(this.uLastName.getText().toString().trim());
            textChanged = true;
        }
        if (this.objectUser.getPasswd() != this.uPasswd.getText().toString()){
            this.objectUser.setPasswd(this.uPasswd.getText().toString().trim());
            //this.uPasswd.setText(this.uPasswd.getText().toString().trim());
            textChanged = true;
        }
        if (textChanged) {
            this.uSavedStatusIndicator.setColorFilter(ContextCompat.getColor(this, R.color.jerry_yellow));
            this.setNeedSave(true);
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
    public boolean checkFieldValue(String value, TextInputEditText field){
        boolean returnValue = true;
        if(value.equals("")){
            returnValue = false;
            field.setError(getResources().getString(R.string.error));
        }else{
            field.setError(null);
        }
        return returnValue;
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                //---cehck if fields are not empty
                boolean fied1 = checkFieldValue(this.objectUser.getUname(),      this.uUser);
                boolean fied2 = checkFieldValue(this.objectUser.getPasswd(),     this.uPasswd);
                boolean fied3 = checkFieldValue(this.objectUser.getFirst_name(), this.uFirstName);
                boolean fied4 = checkFieldValue(this.objectUser.getLast_name(),  this.uLastName);
                if((fied1 != true)||(fied2 != true)||(fied3 != true)||(fied4 != true)){
                  return false;
                }
                //--save to DB
                if(needSave == true) {
                    setBackgroudnRunning(true);
                    new HttpsRequestSaveUser(this).execute();
                }
                break;
            case R.id.item_cancel:
                onBackPressed();
                break;
        }
        return false;
    }

    public Integer getBackButtonCount() {        return backButtonCount;    }
    public void setBackButtonCount(Integer backButtonCount) {        this.backButtonCount = backButtonCount;    }
    public boolean isBackPressed() {        return backPressed;    }
    public void setBackPressed(boolean backPressed) {        this.backPressed = backPressed;    }
    public boolean isBackgroudnRunning() {        return backgroudnRunning;    }
    public void setBackgroudnRunning(boolean backgroudnRunning) {        this.backgroudnRunning = backgroudnRunning;    }

    @Override
    public void onBackPressed() {
        setBackPressed(true);
        if(!isBackgroudnRunning()) {
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

    @Override
    protected void onResume() {
        setBackgroudnRunning(true);
        new HttpsRequestCheckSessionAlive(this).execute();
        super.onResume();
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

                }else{
                    //session and last activity deleted in DB, app will log-out
                    Toast.makeText(context, context.getResources().getString(R.string.session_expired), Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setBackgroudnRunning(false);
            if(isBackPressed()){
                setBackPressed(false);
                finish();
            }
            super.onPostExecute(inputStream);
        }
    }

    class HttpsRequestSaveUser extends AsyncTask<String, Void, InputStream> {
        private static final String save_user_url = "save_user.php";
        private Context context;
        Connector connector;

        public HttpsRequestSaveUser(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, save_user_url);
            connector.addPostParameter("objectUser", MCrypt2.encodeToString(objectUser.toJson()));
            connector.send();
            connector.receive();
            connector.disconnect();
            String result = connector.getResult();
            result = result;

            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            String msg = "Problema!!!";
            try {
                connector.decodeResponse();
                JSONObject responseObject = (JSONObject) connector.getResultJsonArray().get(0);
                String saveStatus = responseObject.getString("status");
                msg = responseObject.getString("msg");
                if (saveStatus.equals("1")) {
                    String userId  = responseObject.getString("userId");
                    String regDate = responseObject.getString("regDate");
                    regDate = HelperDate.get_date_display(regDate);
                    ((ActivityUserEdit) context).uSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_green));
                    ((ActivityUserEdit) context).objectUser.setReg_date(regDate);
                    ((ActivityUserEdit) context).uRegDate.setText(((ActivityUserEdit) context).objectUser.getReg_date());
                    ((ActivityUserEdit) context).objectUser.setId(Integer.parseInt(userId));

                    if((myUser.getUser_lv().equals(user))){
                        myUser.setFirst_name(objectUser.getFirst_name());
                        myUser.setLast_name(objectUser.getLast_name());
                        myUser.setUname(objectUser.getUname());
                        myUser.setPasswd(objectUser.getPasswd());
                        myUser.setPasswdDecoded(false);
                        //myUser.setPasswd(Base64.encodeToString(objectUser.getPasswd().getBytes(),  Base64.DEFAULT));
                        ActivityMenu.setMyUser(myUser);

                        //----check if single-sign-on needs to be updated
                        SQLiteSSO.compare(context, myUser);
                    }
                    setNeedSave(false);
                    Toast.makeText(context, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }else if(saveStatus.equals("2")){
                    ((ActivityUserEdit) context).uSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_green));
                    setNeedSave(false);
                    Toast.makeText(context, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }else if(saveStatus.equals("9")){
                    Toast.makeText(context, getResources().getString(R.string.user)+" '"+
                                                 objectUser.getUname()+"' "+
                                                 getResources().getString(R.string.exists), Toast.LENGTH_SHORT).show();
                }
                /*else if(saveStatus.equals("3")){
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }*/

                setBackgroudnRunning(false);
                if(isBackPressed()){
                    setBackPressed(false);
                    finish();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectUser>getUserList(Connector conn){
            ArrayList<ObjectUser> userArrayList = new ArrayList<>();
            try{
                ObjectUser objectUser;
                JSONArray responseObjects1 = (JSONArray) conn.getResultJsonArray();
                for (int i = 0; i < responseObjects1.length(); i++) {
                    objectUser = new ObjectUser((JSONObject) responseObjects1.get(i));

                    switch (objectUser.getType()){
                        case "1":
                            objectUser.setUser_lv(getResources().getString(R.string.admin));
                            break;
                        case "2":
                            objectUser.setUser_lv(getResources().getString(R.string.owner));
                            break;
                        case "3":
                            objectUser.setUser_lv(getResources().getString(R.string.employee));
                            break;
                    }
                    userArrayList.add(objectUser);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return userArrayList;
        }
    }
}