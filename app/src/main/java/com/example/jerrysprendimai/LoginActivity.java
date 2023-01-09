package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    Integer backButtonCount = 0;
    TextInputEditText loginUser;
    TextInputEditText loginPassword;
    TextView settingsTxt;
    Button buttonLogin;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_error_msg).setVisibility(View.GONE);
        loginUser     = findViewById(R.id.loginUser);
        loginPassword = findViewById(R.id.loginPassword);
        buttonLogin   = findViewById(R.id.loginButton);

        context = this;
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpsRequest(context).execute();
            }
        });

        //------------------Settings handling
        settingsTxt = (TextView)findViewById(R.id.login_settings);
        settingsTxt.setOnClickListener(this::onSettings);
    }

    private void onSettings(View view) {
        this.startActivity(new Intent((Context) this, SettingsActivity.class));
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
                user   = Base64.encodeToString(MCrypt.encrypt(loginUser.getText().toString().getBytes()), Base64.DEFAULT);
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
               JSONObject object = (JSONObject) connector.getResultJsonArray().get(0);
               String login_status = object.getString("status");
                if (login_status.equals("1")) {
                    JSONArray userArray = (JSONArray) connector.getResultJsonArray().get(1);
                    JSONObject myObj = userArray.getJSONObject(0);
                    ObjectUser myUser = new ObjectUser(myObj);
                }else{
                    Toast.makeText(this.context, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }catch(Exception e){

            }

            //super.onPostExecute(inputStream);
        }
    }
}