package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class ActivityLogin extends AppCompatActivity {

    Integer backButtonCount = 0;
    TextInputEditText loginUser;
    TextInputEditText loginPassword;
    TextView settingsTxt;
    Button buttonLogin;
    Drawable background;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_error_msg).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        loginUser     = findViewById(R.id.loginUser);
        loginPassword = findViewById(R.id.loginPassword);
        buttonLogin   = findViewById(R.id.loginButton);

        //----------to remove
        loginUser.setText("admin");
        loginPassword.setText("admin");

        context = this;
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.loginUser).setEnabled(false);
                findViewById(R.id.loginPassword).setEnabled(false);
                findViewById(R.id.loginButton).setEnabled(false);
                background = ((Button) findViewById(R.id.loginButton)).getBackground();
                ((Button) findViewById(R.id.loginButton)).setBackground(getDrawable(R.drawable.button_disabled));
                new HttpsRequest(context).execute();
            }
        });

        //------------------Settings handling
        settingsTxt = (TextView)findViewById(R.id.login_settings);
        settingsTxt.setOnClickListener(this::onSettings);
    }

    private void onSettings(View view) {
        this.startActivity(new Intent((Context) this, ActivitySettings.class));
    }

    @Override
    protected void onResume() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.login_error_msg)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.login_error_msg)).setText("");

        findViewById(R.id.loginUser).setEnabled(true);
        findViewById(R.id.loginPassword).setEnabled(true);
        findViewById(R.id.loginButton).setEnabled(true);

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(backButtonCount >= 1){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Išeiti?", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
        //super.onBackPressed();
    }

    class HttpsRequest extends AsyncTask<String, Void, InputStream> {
        private static final String login_url       = "login.php";
        private Context context;
        Connector connector;

        public HttpsRequest(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            String user = "";
            String passwd = "";
            try {
                //---------Hide Keyboard
                View view = getWindow().getDecorView().findViewById(android.R.id.content);
                InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                user   = Base64.encodeToString(MCrypt.encrypt(loginUser.getText().toString().toLowerCase().getBytes()), Base64.DEFAULT);
                passwd = Base64.encodeToString(MCrypt.encrypt(loginPassword.getText().toString().getBytes()), Base64.DEFAULT);

                connector = new Connector(context, login_url);
                connector.addPostParameter("user_name", user);
                connector.addPostParameter("password", passwd);
                connector.send();
                connector.receive();
                connector.disconnect();

                String result = connector.getResult();
                result = result;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            try{
                findViewById(R.id.loginUser).setEnabled(true);
                findViewById(R.id.loginPassword).setEnabled(true);
                findViewById(R.id.loginButton).setEnabled(true);
                ((Button) findViewById(R.id.loginButton)).setBackground(background);


               connector.clearResponse();
               //String stringEncripted = connector.getResult();
               //String stringDecripted = MCrypt2.decryptSingle(stringEncripted);
               //JSONArray responseArray = new JSONArray(stringDecripted);
               //connector.setResultJsonArray(responseArray);

               JSONObject object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));
               //JSONObject object = (JSONObject) connector.getResultJsonArray().get(0);

               String login_status = object.getString("status");
                if (login_status.equals("1")) {
                    JSONArray userArray = (JSONArray) connector.getResultJsonArray().get(1);
                    JSONObject myObj = MCrypt.decryptJSONObject(userArray.getJSONObject(0));
                    //JSONObject myObj = userArray.getJSONObject(0);
                    ObjectUser myUser = new ObjectUser(myObj);

                    Intent intent = new Intent(this.context, ActivityMenu.class);
                    intent.putExtra("myUser", myUser);
                    context.startActivity(intent);

                }else{
                    Toast.makeText(this.context, "Klaida", Toast.LENGTH_SHORT).show();
                    ((TextView) findViewById(R.id.login_error_msg)).setText(getResources().getString(R.string.no_login));
                    ((TextView) findViewById(R.id.login_error_msg)).setVisibility(View.VISIBLE);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            //super.onPostExecute(inputStream);
        }
    }
}