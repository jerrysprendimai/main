package com.example.jerrysprendimai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class ActivityPictureFullSizeView extends AppCompatActivity {

    ViewPager viewPager;
    int position;
    ArrayList<ObjectObjPic> myPictureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);
        //if(savedInstanceState == null){
            Intent intent = getIntent();
            myPictureList = intent.getParcelableArrayListExtra("myPictureList");
            position      = intent.getIntExtra("myPosition", 0);
        //}
        viewPager = findViewById(R.id.pictureView_viewPager);

        MyAdapterPictureFullSizeView fullsizeAdapter = new MyAdapterPictureFullSizeView(this, myPictureList);
        viewPager.setAdapter(fullsizeAdapter);
        viewPager.setCurrentItem(position,true);
    }
}