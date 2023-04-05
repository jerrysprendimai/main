package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dcastalia.localappupdate.DownloadApk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

public class ActivityMain extends AppCompatActivity {
    static ActivityMain activityMain;
    static String objectId;
    private final String appUrl = "https://jerry-sprendimai.eu/misc/jerry.apk";
    String objId, txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((TextView) findViewById(R.id.jerry_version)).setHint("- " + ((TextView) findViewById(R.id.jerry_version)).getHint() + " " + BuildConfig.VERSION_NAME + " -");
        findViewById(R.id.jerry_version).setVisibility(View.VISIBLE);

        ActivityMain.setActivityMain(this);

        //----------reading values from Internal DB
        SQLiteDB dbHelper = new SQLiteDB(this);
        Cursor result = dbHelper.getData();
        //----check if values already exist
        result.moveToFirst();
        String url       = result.getString(1);
        String db_server = result.getString(2);
        String db_name   = result.getString(3);
        String db_user   = result.getString(4);
        String db_passwd = result.getString(5);
        if ((url.equals(""))&&(db_server.equals(""))&&(db_name.equals(""))&&(db_user.equals(""))&&(db_passwd.equals(""))){
            dbHelper.addDataInitial();
        }

        dbHelper.close();
        /*
        if(getIntent().getExtras() != null){
            String chatId = getIntent().getExtras().getString("chatObjID");
            getIntent().putExtra("chatObjID", chatId);
            //String title = getIntent().getExtras().getString("title");
            //String message = getIntent().getExtras().getString("body");
        }
        */

        //----------initialize singel sign on
        SQLiteSSO dbSSO = new SQLiteSSO(this);
        String[] ssoValues = dbSSO.getData();
        dbSSO.close();
        if ((!ssoValues[0].equals(""))&&(!ssoValues[1].equals(""))&&(!ssoValues[2].equals(""))){
            //loginUser.setText(ssoValues[1]);
            //loginPassword.setText(ssoValues[2]);
            //buttonLogin.setSoundEffectsEnabled(false);
            //buttonLogin.performClick();
            //buttonLogin.setSoundEffectsEnabled(true);
            new HttpsLoginRequest(this, ssoValues[1],ssoValues[2], getIntent().getExtras()).execute();
        }else{
            //---- start Login Activity
            Handler handler = new Handler();
            handler.postDelayed(() -> startActivity(new Intent(ActivityMain.this, ActivityLogin.class)), 1500);
        }
    }

    public static String getObjectId() {    return objectId;   }
    public static void setObjectId(String objectId) {        ActivityMain.objectId = objectId;    }
    public String getObjId() {        return objId;    }
    public void setObjId(String objId) {        this.objId = objId;    }
    public String getTxt() {        return txt;    }
    public void setTxt(String txt) {        this.txt = txt;    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /*String action = intent.getAction();
        String str = intent.toString();
        String tmp = intent.getStringExtra("objectId");
        String tmp2 = intent.getParcelableExtra("objectId");
        Bundle extras = intent.getExtras();
        if(extras !=null){
            String objID = extras.getString( "objectId" );
            String title = extras.getString( "title" );
            setObjId(extras.getString( "objectId" ) );
            setTxt(extras.getString( "title" ) );
            //startActivity(intent);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public static ActivityMain getActivityMain() {        return ActivityMain.activityMain;    }

    public static void setActivityMain(ActivityMain activityMain) {
        ActivityMain.activityMain = activityMain;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    class HttpsLoginRequest extends AsyncTask<String, Void, InputStream> {
        private static final String login_url       = "login.php";
        private Context context;
        private String uname, passwdord;
        private Bundle extras;
        Connector connector;

        public HttpsLoginRequest(Context context, String uname, String passwd, Bundle extras) {
            this.context = context;
            this.uname = uname;
            this.passwdord = passwd;
            this.extras = extras;
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

                user   = Base64.encodeToString(MCrypt.encrypt(uname.toLowerCase().getBytes()), Base64.DEFAULT);
                passwd = Base64.encodeToString(MCrypt.encrypt(passwdord.getBytes()), Base64.DEFAULT);

                connector = new Connector(context, login_url);
                connector.addPostParameter("user_name", user);
                connector.addPostParameter("password", passwd);
                //connector.addPostParameter("token", ActivityLogin.getMytoken());
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
                                ActivityCompat.requestPermissions(ActivityMain.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }

                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(ActivityMain.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                            DownloadApk downloadApk = new DownloadApk(ActivityMain.this);
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
                        /*try {
                            String value = new String(Base64.decode(myUser.getPasswd(), 0));
                            myUser.setPasswd(value);
                        }catch (Exception e){ }*/

                        intent.putExtra("myUser", myUser);
                        //----Notification click handling
                        if(extras != null){
                            String chatObjId = extras.getString( "chatObjID" );
                            intent.putExtra("chatObjID", chatObjId);
                            //extras.remove("chatObjID");
                            //extras.getString( "title" );
                        }
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(ActivityLogin.class);
                        stackBuilder.addNextIntent(intent);
                        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

                        //context.startActivity(intent);
                        pendingIntent.send();
                    }

                }else{
                    context.startActivity(new Intent(context, ActivityLogin.class));
                    //Toast.makeText(this.context, "Klaida", Toast.LENGTH_SHORT).show();
                    //((TextView) findViewById(R.id.login_error_msg)).setText(getResources().getString(R.string.no_login));
                    //((TextView) findViewById(R.id.login_error_msg)).setVisibility(View.VISIBLE);
                }
            }catch(Exception e){
                context.startActivity(new Intent(context, ActivityLogin.class));
                e.printStackTrace();
            }

            //super.onPostExecute(inputStream);
        }
    }
}