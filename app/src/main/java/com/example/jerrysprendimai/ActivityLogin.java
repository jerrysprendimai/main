package com.example.jerrysprendimai;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dcastalia.localappupdate.DownloadApk;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public class ActivityLogin extends AppCompatActivity {

    private final String appUrl = "https://jerry-sprendimai.eu/misc/jerry.apk";

    Integer backButtonCount = 0;
    TextInputEditText loginUser;
    TextInputEditText loginPassword;
    TextView settingsTxt;
    Button buttonLogin;
    LinearLayout shiftContainer;
    Drawable background;
    Context context;
    EditText loginFocusHolder;

    int currentlyScrolled;
    int totalScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((TextView) findViewById(R.id.jerry_version)).setHint("- " + ((TextView) findViewById(R.id.jerry_version)).getHint() + " " + BuildConfig.VERSION_NAME + " -");
        findViewById(R.id.jerry_version).setVisibility(View.VISIBLE);

        //----resize window to push the Login button up
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        findViewById(R.id.login_error_msg).setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        loginFocusHolder = findViewById(R.id.login_invisibleFocusHolder);
        loginUser        = findViewById(R.id.loginUser);
        loginPassword    = findViewById(R.id.loginPassword);
        buttonLogin      = findViewById(R.id.loginButton);
        shiftContainer   = findViewById(R.id.shift_container);

        //----------to remove
        loginUser.setText("user");
        loginPassword.setText("user");

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
                new HttpsLoginRequest(context).execute();
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
        loginFocusHolder.setInputType(InputType.TYPE_NULL);
        loginFocusHolder.requestFocus();

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

    class HttpsLoginRequest extends AsyncTask<String, Void, InputStream> {
        private static final String login_url       = "login.php";
        private Context context;
        Connector connector;

        public HttpsLoginRequest(Context ctx){
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
               connector.decodeResponse();

               JSONObject object = MCrypt.decryptJSONObject((JSONObject) connector.getResultJsonArray().get(0));

               String login_status = object.getString("status");
               String version      = object.getString("version");
                if (login_status.equals("1")) {
                    //--check if new version is available
                    int versionCode = BuildConfig.VERSION_CODE;
                    String versionName = BuildConfig.VERSION_NAME;
                    if(!versionName.equals(version)){

                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_popup, null, false);
                        TextView text = dialogView.findViewById(R.id.popup_text);
                        text.setText(getResources().getString(R.string.new_version));
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) context, R.style.AlertDialogTheme);
                        builder.setView(dialogView);
                        builder.setPositiveButton("Ok", (dialog, which) -> {
                            //installtion permission
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (!getPackageManager().canRequestPackageInstalls()) {
                                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", getPackageName()))), 1234);
                                } else {
                                }
                            }
                            //Storage Permission
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ActivityLogin.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }

                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ActivityLogin.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }

                            String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
                            String fileName = "Jerry.apk";
                            destination += fileName;
                            Uri uri = Uri.parse("file://" + destination);
                            //Delete update file if exists
                            File file = new File(destination);
                            if (file.exists()){
                              file.delete();
                            }
                            DownloadApk downloadApk = new DownloadApk(ActivityLogin.this);
                            downloadApk.startDownloadingApk(appUrl, "Jerry.apk");
                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> {
                            onResume();
                            return;
                        });
                        builder.create();
                        builder.show();
                    }else{
                        JSONArray userArray = (JSONArray) connector.getResultJsonArray().get(1);
                        JSONObject myObj = MCrypt.decryptJSONObject(userArray.getJSONObject(0));
                        //JSONObject myObj = userArray.getJSONObject(0);
                        ObjectUser myUser = new ObjectUser(myObj);

                        Intent intent = new Intent(this.context, ActivityMenu.class);
                        intent.putExtra("myUser", myUser);
                        context.startActivity(intent);
                    }

                }else{
                    Toast.makeText(this.context, "Klaida", Toast.LENGTH_SHORT).show();
                    ((TextView) findViewById(R.id.login_error_msg)).setText(getResources().getString(R.string.no_login));
                    ((TextView) findViewById(R.id.login_error_msg)).setVisibility(View.VISIBLE);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            findViewById(R.id.loginUser).setEnabled(true);
            findViewById(R.id.loginPassword).setEnabled(true);
            findViewById(R.id.loginButton).setEnabled(true);
            ((Button) findViewById(R.id.loginButton)).setBackground(background);
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            //super.onPostExecute(inputStream);
        }
    }
}