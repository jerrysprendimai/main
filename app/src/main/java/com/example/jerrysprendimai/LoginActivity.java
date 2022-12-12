package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    Integer backButtonCount = 0;
    TextView settingsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViewById(R.id.login_error_msg).setVisibility(View.GONE);

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
}