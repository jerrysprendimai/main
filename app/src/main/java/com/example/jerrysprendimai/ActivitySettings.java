package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONObject;

import java.io.InputStream;

public class ActivitySettings extends AppCompatActivity implements KeyboardVisibilityEventListener, View.OnKeyListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String domain              = "https://www.jerry-sprendimai.eu";
    private static final String getConfig_url       = "get_config.php";
    private static final String testConnection_url  = "test_connection.php";

    BottomNavigationView saveCancelButtons;

    SQLiteDB dbHelper;
    Connector connector;

    TextInputEditText urlInput;
    TextInputEditText dbServerInput;
    TextInputEditText dbNameInput;
    TextInputEditText dbUserInput;
    TextInputEditText dbPasswordInput;

    Button btTestConnection;

    TextInputEditText admin1;
    TextInputEditText admin2;

    Button btGetSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //-----------------Internal Database Handling
        this.dbHelper = new SQLiteDB(this);

        //-----------------Editable Fields handling
        urlInput = findViewById(R.id.settings_URL);
        dbServerInput = findViewById(R.id.settings_dbServer);
        dbNameInput = findViewById(R.id.settings_dbName);
        dbUserInput = findViewById(R.id.settings_dbUser);
        dbPasswordInput = findViewById(R.id.settings_dbPasswd);

        btTestConnection = findViewById(R.id.btTestConnection);

        //-----------------Config
        admin1 = findViewById(R.id.settings_dbConfig_1);
        admin2 = findViewById(R.id.settings_dbConfig_2);

        btGetSettings = findViewById(R.id.btGetSettings);

        urlInput.setOnKeyListener(this::onKey);
        dbServerInput.setOnKeyListener(this::onKey);
        dbNameInput.setOnKeyListener(this::onKey);
        dbUserInput.setOnKeyListener(this::onKey);
        dbPasswordInput.setOnKeyListener(this::onKey);

        Context context = this;
        btTestConnection.setOnClickListener(view -> new HttpsRequest(context).execute(testConnection_url));

        btGetSettings.setOnClickListener(view -> new HttpsRequest(context).execute(getConfig_url));

        fillFieldValues();

        //-----------------Bottom Menu Hide/Show depending on the Keyboard----------------
        KeyboardVisibilityEvent.setEventListener(this, this::onVisibilityChanged);
        saveCancelButtons = findViewById(R.id.save_cancel_buttons);
        saveCancelButtons.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

    }

    public void fillFieldValues() {
        Cursor result = this.dbHelper.getData();
        if (result.getCount() == 0) {
            Toast.makeText(this, "Išsaugotų Duomenų Nėra", Toast.LENGTH_SHORT).show();

            boolean isInserted = this.dbHelper.addData(urlInput.getText().toString(),
                                                       dbServerInput.getText().toString(),
                                                       dbNameInput.getText().toString(),
                                                       dbUserInput.getText().toString(),
                                                       dbPasswordInput.getText().toString());
            return;
        }

        //StringBuffer buffer = new StringBuffer();
        result.moveToNext();
        String url = result.getString(1);
        urlInput.setText(result.getString(1));
        dbServerInput.setText(result.getString(2));
        dbNameInput.setText(result.getString(3));
        dbUserInput.setText(result.getString(4));
        dbPasswordInput.setText(result.getString(5));
    }

    @Override
    public void onVisibilityChanged(boolean b) {
        if(b){
            this.findViewById(R.id.save_cancel_buttons).setVisibility(View.GONE);
            this.findViewById(R.id.jerry_copyright).setVisibility(View.GONE);
        }else{
            this.findViewById(R.id.save_cancel_buttons).setVisibility(View.VISIBLE);
            this.findViewById(R.id.jerry_copyright).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
        //-----------------Hide Keyboard--------------------------
        //View view = this.getCurrentFocus();

        //-----------------Key "ENTER" + Key "BACK"---------------
        //if ((view != null) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
        //    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        //    //onClick(view);
        //}
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        //---------------Define NavigationString based on pressed button
        switch (item.getItemId()) {
            case R.id.item_save:
                boolean isInserted = this.dbHelper.addData(urlInput.getText().toString(),
                                                           dbServerInput.getText().toString(),
                                                           dbNameInput.getText().toString(),
                                                           dbUserInput.getText().toString(),
                                                           dbPasswordInput.getText().toString());
                if (isInserted == true) {
                    Toast.makeText(this, "Išsaugota", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Įvyko klaida <<< !!!ERROR!!! >>>", Toast.LENGTH_SHORT).show();
                }
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                break;
            case R.id.item_cancel:
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                break;
        }
        return false;
    }

    class HttpsRequest extends AsyncTask<String, Void, InputStream> {
        private Context context;
        String action_type;

        public HttpsRequest(Context ctx){
            context = ctx;
        }

        @Override
        protected InputStream doInBackground(String... params) {
            action_type = params[0];
            switch (action_type){
                case getConfig_url:
                    String tmp1 = "";
                    String tmp2 = "";
                    try {
                      tmp1 = Base64.encodeToString(MCrypt.encrypt(admin1.getText().toString().getBytes()), Base64.DEFAULT);
                      tmp2 = Base64.encodeToString(MCrypt.encrypt(admin2.getText().toString().getBytes()), Base64.DEFAULT);
                    }catch (Exception e){
                       e.printStackTrace();
                    }


                    connector = new Connector(context, getConfig_url);
                    //connector.addPostParameter("a1", Base64.encodeToString(MCrypt.encrypt(string1.getBytes()), Base64.DEFAULT));
                    //connector.addPostParameter("a1", Base64.encodeToString(MCrypt.encrypt(string2.getBytes()), Base64.DEFAULT));
                    connector.addPostParameter("a1", tmp1);
                    connector.addPostParameter("a2", tmp2);
                    connector.send();
                    connector.receive();
                    connector.disconnect();
                   break;
                case testConnection_url:
                    connector = new Connector(context, testConnection_url);
                    connector.send();
                    connector.receive();
                    connector.disconnect();

                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            admin1.setText("");
            admin2.setText("");

            //------------------------Hide Keyboard-----------------------------
            View view = ((ActivitySettings)context).getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            switch (action_type){
                case getConfig_url:
                    try {
                        JSONObject responseObject1 = (JSONObject) connector.getResultJsonArray().get(0);
                        JSONObject responseObject2 = (JSONObject) connector.getResultJsonArray().get(1);
                        String login_status = responseObject1.getString("status");
                        if (login_status.equals("1")) {
                            urlInput.setText(responseObject2.getString("domain"));
                            dbServerInput.setText(responseObject2.getString("servername"));
                            dbNameInput.setText(responseObject2.getString("dbname"));
                            dbUserInput.setText(responseObject2.getString("username"));
                            dbPasswordInput.setText(responseObject2.getString("password"));

                            MenuItem item = saveCancelButtons.getMenu().findItem(R.id.item_save);
                            ((ActivitySettings)context).onNavigationItemSelected(item);
                        }else{
                            Toast.makeText(context, "Duomenų gauti nepavyko!!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case testConnection_url:
                    try {
                        JSONObject object = (JSONObject) connector.getResultJsonArray().get(0);
                        String login_status = object.getString("status");
                        if (login_status.equals("1")) {
                            Toast.makeText(context, "Pavyko prisijungti", Toast.LENGTH_SHORT).show();
                        }else if(login_status.equals("0")){
                            Toast.makeText(context, "Prisijungti nepavyko!!!", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
            //super.onPostExecute(inputStream);
        }
    }
}