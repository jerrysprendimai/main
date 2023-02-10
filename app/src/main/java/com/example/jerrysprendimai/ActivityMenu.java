package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityMenu extends AppCompatActivity {
    final String user = "user";
    ObjectUser myUser;
    LinearLayout mainContainer;
    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.progressBar).setVisibility(View.GONE);
        Context context = this;

        this.mainContainer = findViewById(R.id.menu_main_containerView);
        this.gridLayout    = findViewById(R.id.menu_gridLayout);

        //---------------Read myUser object----------------------
        this.myUser = getIntent().getParcelableExtra("myUser");
        TextView userTextView = findViewById(R.id.main_menu_user_value);
        userTextView.setHint("");
        userTextView.setText(this.myUser.getUname());

        TextView userTypeTextView = findViewById(R.id.main_menu_user_type_value);
        userTypeTextView.setText(this.myUser.getUser_lv());
        userTypeTextView.setHint("");

        //--------------set user view visibility
        setUserLevelView();

        //------------Settings
        LinearLayout settingsLayout = (LinearLayout) findViewById(R.id.main_menu_settings);
        settingsLayout.setOnClickListener(v -> {
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivitySettings.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });

        //------------User_Show
        LinearLayout userLayout = (LinearLayout) findViewById(R.id.main_menu_user);
        userLayout.setOnClickListener(v -> {
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivityUserShow.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });

        //------------Object_Show
        LinearLayout objectLayout = (LinearLayout) findViewById(R.id.main_menu_work);
        objectLayout.setOnClickListener(v -> {
            disableWholeView(gridLayout);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Intent intent = new Intent(context, ActivityObjectShow.class);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void disableWholeView(ViewGroup gridView){
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View child = gridView.getChildAt(i);
            if((child instanceof CardView) && (getResources().getResourceEntryName(child.getId()).contains("CardView_main_menu"))){
                //child.setBackground(getDrawable(R.drawable.button_disabled));
                View linerLayout = ((CardView) child).getChildAt(0);
                linerLayout.setEnabled(false);
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public  void enableWholeView(ViewGroup gridView){
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View child = gridView.getChildAt(i);
            if((child instanceof CardView) && (getResources().getResourceEntryName(child.getId()).contains("CardView_main_menu"))){
                //child.setBackground(getDrawable(R.drawable.round_button));
                View linerLayout = ((CardView) child).getChildAt(0);
                linerLayout.setEnabled(true);
            }
        }
    }

    @Override
    protected void onResume() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        enableWholeView(gridLayout);
        super.onResume();
    }

    private void setUserLevelView() {
        if (this.myUser.getUser_lv().equals(user)){
            ((CardView) findViewById(R.id.CardView_main_menu_dealers)).setVisibility(View.GONE);
            ((CardView) findViewById(R.id.CardView_main_menu_user)).setVisibility(View.GONE);
        }else{
            ((CardView) findViewById(R.id.CardView_main_menu_dealers)).setVisibility(View.VISIBLE);
            ((CardView) findViewById(R.id.CardView_main_menu_user)).setVisibility(View.VISIBLE);
        }

    }
}