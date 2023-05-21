package com.example.jerrysprendimai;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.ArrayList;

public class FragmentOrderPart3 extends Fragment {

    Context context;
    TextInputEditText text;
    RecyclerView recyclerViewDealer, recyclerViewObject, photoRecyclerView;;
    CardView cardViewDealer, cardViewObject;
    MyAdapterDealerShow myAdapterDealerShow;
    MyAdapterObjectShowP1 myAdapterObjectShow;
    MyAdapterOrderPicture myAdapterOrderPicture;
    Button sendButton;

    public FragmentOrderPart3(Context context) {
        this.context = context;
    }

    public static FragmentOrderPart3 newInstance(Context context) {
        FragmentOrderPart3 fragment = new FragmentOrderPart3(context);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_order_part3, container, false);

        //----binding
        recyclerViewDealer = fragmentView.findViewById(R.id.my_recycle_view_dealer);
        recyclerViewObject = fragmentView.findViewById(R.id.my_recycle_view_object);
        photoRecyclerView  = fragmentView.findViewById(R.id.order_photo_recycleView);
        cardViewDealer     = fragmentView.findViewById(R.id.cardView_oder_p3_dealer);
        cardViewObject     = fragmentView.findViewById(R.id.cardView_oder_p3_object);
        text               = fragmentView.findViewById(R.id.oder_p3_textInput);
        sendButton         = fragmentView.findViewById(R.id.button_order_p3_continue);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String txt = ((ActivityOrder1)context).getMyOrder().getMyText();
        text.setText(((ActivityOrder1)context).getMyOrder().getMyText());

        cardViewDealer.setVisibility(View.GONE);
        recyclerViewDealer.setVisibility(View.VISIBLE);
        myAdapterDealerShow = new MyAdapterDealerShow(context, ((ActivityOrder1)context).getMyDealer(), ((ActivityOrder1)context).getMyDealer(), "dealerShowP3", null, false);
        recyclerViewDealer.setAdapter(myAdapterDealerShow);
        recyclerViewDealer.setLayoutManager(new LinearLayoutManager(context));

        cardViewObject.setVisibility(View.GONE);
        recyclerViewObject.setVisibility(View.VISIBLE);
        myAdapterObjectShow = new MyAdapterObjectShowP1(context, null, ((ActivityOrder1)context).getMyObject(), ((ActivityOrder1)context).getMyUser(), null, "objectShowP3", false);
        recyclerViewObject.setAdapter(myAdapterObjectShow);
        recyclerViewObject.setLayoutManager(new LinearLayoutManager(context));

        myAdapterOrderPicture = new MyAdapterOrderPicture(context, ((ActivityOrder1)context).getMyOrder().getMyPictureList(), ((ActivityOrder1)context).getMyUser(), "pictureShowP3");
        photoRecyclerView.setAdapter(myAdapterOrderPicture);
        photoRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));

        sendButton.setOnClickListener(v->{
            ((ActivityOrder1)context).sendButtonCallabck();
        });
    }
}