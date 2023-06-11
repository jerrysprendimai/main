package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;

public class ActivityDealerEdit extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    TextView nameLabel;
    ImageView saveIcon;
    TextInputEditText dealerName, dealerEmail, dealerRegDate, iconPlaceHolder;
    ObjectUser myUser;
    ObjectDealer myDealer;
    Integer backButtonCount;
    BottomNavigationView bottomNavigationView;
    boolean backPressed, needSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_edit);

        this.backButtonCount = 0;
        this.needSave = false;
        this.backPressed = false;

        nameLabel       = findViewById(R.id.dealer_edit_name);
        saveIcon        = findViewById(R.id.dealer_edit_savedStatus_img);
        dealerName      = findViewById(R.id.dealer_edit_name_editText);
        dealerEmail     = findViewById(R.id.dealer_edit_email);
        dealerRegDate   = findViewById(R.id.dealer_edit_regdate_value);
        //iconPlaceHolder = findViewById(R.id.dealer_edit_image_placehodler);

        this.myUser   = getIntent().getParcelableExtra("myUser");
        this.myDealer = getIntent().getParcelableExtra("myDealer");

        if(this.myDealer == null){
            this.myDealer = new ObjectDealer();
        }

        nameLabel.setText(this.myDealer.getName());
        saveIcon.setColorFilter(ContextCompat.getColor(this, R.color.jerry_green));
        dealerName.setText(this.myDealer.getName());
        dealerEmail.setText(this.myDealer.getEmail());
        dealerRegDate.setText(this.myDealer.getRegDate());

        Context context = this;
        dealerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!myDealer.getName().equals(dealerName.getText().toString())){
                    myDealer.setName(dealerName.getText().toString());
                    saveIcon.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                    nameLabel.setText(dealerName.getText().toString());
                    setNeedSave(true);
                }
            }
        });
        dealerEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(!myDealer.getEmail().equals(dealerEmail.getText().toString())){
                   myDealer.setEmail(dealerEmail.getText().toString());
                   saveIcon.setColorFilter(ContextCompat.getColor(context, R.color.jerry_yellow));
                   setNeedSave(true);
                }
            }
        });

        //-----------------Bottom Menu Hide/Show depending on the Keyboard----------------
        //KeyboardVisibilityEvent.setEventListener(this, this);
        this.bottomNavigationView = findViewById(R.id.save_cancel_buttons);
        this.bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean doBreak = false;
        switch (item.getItemId()) {
            case R.id.item_save:
                if(dealerName.getText().toString().equals("")){
                    dealerName.setError(getResources().getString(R.string.error));
                    doBreak = true;
                }
                if(dealerEmail.getText().toString().equals("")){
                    dealerEmail.setError(getResources().getString(R.string.error));
                }else if (!Patterns.EMAIL_ADDRESS.matcher(myDealer.getEmail()).matches()){
                    dealerEmail.setError(getResources().getString(R.string.error_email));
                    doBreak = true;
                }

                if(doBreak){
                    break;
                }
                new HttpsRequestSaveDealer(this, myDealer).execute();

                break;
            case R.id.item_cancel:
                onBackPressed();
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        setBackPressed(true);
        if ((backButtonCount.equals(0)) && (isNeedSave())) {
           Toast.makeText(this, getResources().getString(R.string.not_saved), Toast.LENGTH_SHORT).show();
           backButtonCount++;
        } else {
           setBackButtonCount(0);
           super.onBackPressed();
        }

    }

    public boolean isNeedSave() {        return needSave;    }
    public void setNeedSave(boolean needSave) {
        this.needSave = needSave;
    }
    public Integer getBackButtonCount() {        return backButtonCount;    }
    public void setBackButtonCount(Integer backButtonCount) {        this.backButtonCount = backButtonCount;    }
    public boolean isBackPressed() {        return backPressed;    }
    public void setBackPressed(boolean backPressed) {        this.backPressed = backPressed;    }

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

            if(isBackPressed()){
                setBackPressed(false);
                finish();
            }
            super.onPostExecute(inputStream);
        }
    }

    class HttpsRequestSaveDealer extends AsyncTask<String, Void, InputStream> {
        private static final String save_dealer_url = "save_dealer.php";
        private Context context;
        Connector connector;
        ObjectDealer objectDealer;

        public HttpsRequestSaveDealer(Context ctx, ObjectDealer objectDealer){
            context = ctx;
            this.objectDealer = objectDealer;
        }

        @Override
        protected InputStream doInBackground(String... strings) {

            connector = new Connector(context, save_dealer_url);
            connector.addPostParameter("objectDealer", MCrypt2.encodeToString(objectDealer.toJson()));
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
                    String dealerId  = responseObject.getString("dealerId");
                    String regDate = responseObject.getString("regDate");
                    regDate = HelperDate.get_date_display(regDate);

                    objectDealer.setRegDate(regDate);
                    objectDealer.setId(Integer.parseInt(dealerId));
                    ((ActivityDealerEdit) context).dealerRegDate.setText(regDate);
                    saveIcon.setColorFilter(ContextCompat.getColor(context, R.color.jerry_green));

                    setNeedSave(false);
                    Toast.makeText(context, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }else if(saveStatus.equals("2")){
                    ((ActivityUserEdit) context).uSavedStatusIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_green));
                    setNeedSave(false);
                    Toast.makeText(context, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }else if(saveStatus.equals("9")){

                }
                //setBackgroudnRunning(false);
                if(isBackPressed()){
                    setBackPressed(false);
                    finish();
                }

            }catch (Exception e){
                e.printStackTrace();
            }

            super.onPostExecute(inputStream);
        }

        private ArrayList<ObjectUser> getUserList(Connector conn){
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