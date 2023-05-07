package com.example.jerrysprendimai;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.ArrayList;


public class FragmentOrderPart2 extends Fragment {

    Context context;
    Button proceedButton, backButton;
    TextInputEditText textInput;
    EditText invisibleFocus;
    RecyclerView photoRecyclerView;
    LinearLayout takePhoto, addPhoto, deletePhoto, cancelPhotoEdit,addModeButtons, deletionModeButtons;
    MyAdapterOrderPicture myAdapterOrderPicture;

    public FragmentOrderPart2(Context context, MyAdapterOrderPicture myAdapterOrderPicture) {
        this.context = context;
        this.myAdapterOrderPicture = myAdapterOrderPicture;
    }

    public static FragmentOrderPart2 newInstance(Context context, MyAdapterOrderPicture myAdapterOrderPicture) {
        FragmentOrderPart2 fragment = new FragmentOrderPart2(context, myAdapterOrderPicture);
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // if (getArguments() != null) {
       //     mParam1 = getArguments().getString(ARG_PARAM1);
       //     mParam2 = getArguments().getString(ARG_PARAM2);
       // }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_order_part2, container, false);

        //----binding
        proceedButton       = fragmentView.findViewById(R.id.button_order_p2_continue);
        backButton          = fragmentView.findViewById(R.id.button_order_p2_back);
        textInput           = fragmentView.findViewById(R.id.oder_p2_textInput);
        invisibleFocus      = fragmentView.findViewById(R.id.order_invisibleFocusHolder);
        photoRecyclerView   = fragmentView.findViewById(R.id.order_photo_recycleView);
        takePhoto           = fragmentView.findViewById(R.id.oder_p2_take_photo_linearLayout);
        addPhoto            = fragmentView.findViewById(R.id.oder_p2_add_photo_linearLayout);
        deletePhoto         = fragmentView.findViewById(R.id.order_p2_delete_photo_linearLayout);
        cancelPhotoEdit     = fragmentView.findViewById(R.id.order_p2_cancel_linearLayout);
        addModeButtons      = fragmentView.findViewById(R.id.order_p2_add_photo_buttons_linear_layout);
        deletionModeButtons = fragmentView.findViewById(R.id.order_p2_delete_photo_buttons_linear_layout);


        proceedButton.setText(proceedButton.getText() + "   2 / 3");
        textInput.setText(((ActivityOrder1)context).getMyOrder().getMyText());
        backButton.setOnClickListener(v->{
            ((ActivityOrder1)context).buttonClickCallback(StateProgressBar.StateNumber.ONE, 0);
            //onBackPressed();
        });
        proceedButton.setOnClickListener(v->{
            ((ActivityOrder1)context).buttonClickCallback(StateProgressBar.StateNumber.THREE, 2);
        });

        ((ActivityOrder1)context).setDeletionMode(false);
        deletionModeButtons.setVisibility(View.GONE);
        ((ActivityOrder1)context).setToBeDeletedList(new ArrayList<>());

        //-----invisible focus holder dandling
        this.invisibleFocus.setInputType(InputType.TYPE_NULL);
        this.invisibleFocus.requestFocus();

        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!((ActivityOrder1)context).getMyOrder().getMyText().equals(textInput.getText().toString())){
                    ((ActivityOrder1)context).getMyOrder().setMyText(textInput.getText().toString());
                    checkAbleToProceed();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {        }
        });

        takePhoto.setOnClickListener(v->{
            ((ActivityOrder1)context).takePhotoClickCallback();
        });
        addPhoto.setOnClickListener(v->{
            ((ActivityOrder1)context).addPhotoClickCallback();
        });
        deletePhoto.setOnClickListener(v->{
            ((ActivityOrder1)context).deletePhotoClickCallback();
        });
        cancelPhotoEdit.setOnClickListener(v->{
            ((ActivityOrder1)context).cancelPhotoClickCallback();
        });

        //myAdapterOrderPicture = new MyAdapterOrderPicture(context, ((ActivityOrder1)context).getMyOrder().getMyPictureList(), ((ActivityOrder1)context).myUser);
        photoRecyclerView.setAdapter(myAdapterOrderPicture);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        checkAbleToProceed();

        return fragmentView;
    }

    public void checkAbleToProceed(){
        if((!((ActivityOrder1)context).getMyOrder().getMyText().isEmpty())||(((ActivityOrder1)context).getMyOrder().getMyPictureList().size() != 0)){
            proceedButton.setEnabled(true);
            proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button));
        }else{
            proceedButton.setEnabled(false);
            proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button_grey));
        }
    }
}