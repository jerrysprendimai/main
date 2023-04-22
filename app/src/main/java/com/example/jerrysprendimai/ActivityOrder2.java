package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class ActivityOrder2 extends AppCompatActivity {

    Button proceedButton, backButton;
    TextInputEditText textInput;

    ObjectUser myUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oder2);

        //----binding
        proceedButton  = findViewById(R.id.button_order_p2_continue);
        backButton     = findViewById(R.id.button_order_p2_back);
        textInput      = findViewById(R.id.oder_p2_textInput);

        //---------------Read myUser object----------------------
        this.myUser = getIntent().getParcelableExtra("myUser");

        proceedButton.setText(proceedButton.getText() + "   2 / 3");

        backButton.setOnClickListener(v->{
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}