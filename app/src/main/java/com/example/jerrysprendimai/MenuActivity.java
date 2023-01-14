package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

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
        settingsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableWholeView(gridLayout);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, SettingsActivity.class);
                intent.putExtra("myUser", myUser);
                context.startActivity(intent);
            }
        });

        //------------User_Show
        LinearLayout userLayout = (LinearLayout) findViewById(R.id.main_menu_user);
        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableWholeView(gridLayout);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, UserShow.class);
                intent.putExtra("myUser", myUser);
                context.startActivity(intent);
            }
        });

    }

    public void disableWholeView(ViewGroup gridView){
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View child = gridView.getChildAt(i);
            if((child instanceof CardView) && (getResources().getResourceEntryName(child.getId()).contains("CardView_main_menu"))){
                child.setBackground(getDrawable(R.drawable.button_disabled));
                View linerLayout = ((CardView) child).getChildAt(0);
                linerLayout.setEnabled(false);
            }
        }
    }
    public  void enableWholeView(ViewGroup gridView){
        for (int i = 0; i < gridView.getChildCount(); i++) {
            View child = gridView.getChildAt(i);
            if((child instanceof CardView) && (getResources().getResourceEntryName(child.getId()).contains("CardView_main_menu"))){
                child.setBackground(getDrawable(R.drawable.round_button));
                View linerLayout = ((CardView) child).getChildAt(0);
                linerLayout.setEnabled(true);
            }
        }
    }

    @Override
    protected void onPostResume() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        enableWholeView(gridLayout);
        super.onPostResume();
    }

    private void setUserLevelView() {
        if (this.myUser.getUser_lv().equals("user")){
            ((CardView) findViewById(R.id.CardView_main_menu_dealers)).setVisibility(View.GONE);
            ((CardView) findViewById(R.id.CardView_main_menu_user)).setVisibility(View.GONE);
        }else{
            ((CardView) findViewById(R.id.CardView_main_menu_dealers)).setVisibility(View.VISIBLE);
            ((CardView) findViewById(R.id.CardView_main_menu_user)).setVisibility(View.VISIBLE);
        }

    }
}