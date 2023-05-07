package com.example.jerrysprendimai;

import android.content.Context;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kofigyan.stateprogressbar.StateProgressBar;

import java.util.ArrayList;

public class FragmentOrderPart1 extends Fragment {

    Context context;
    Button proceedButton;
    LinearLayout addDealerButton, addObjectButton;
    RecyclerView recyclerViewDealer, recyclerViewObject;
    CardView cardViewDealer, cardViewObject;

    public FragmentOrderPart1(Context context) {
        this.context = context;
    }


    public static FragmentOrderPart1 newInstance(Context context) {
        FragmentOrderPart1 fragment = new FragmentOrderPart1(context);
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
        View fragmentView = inflater.inflate(R.layout.fragment_order_part1, container, false);

        //---------element binding
        proceedButton      = fragmentView.findViewById(R.id.button_order_p1_continue);
        addDealerButton    = fragmentView.findViewById(R.id.linearLayout_oder_p1_dealer_button);
        addObjectButton    = fragmentView.findViewById(R.id.linearLayout_oder_p1_object_button);
        recyclerViewDealer = fragmentView.findViewById(R.id.my_recycle_view_dealer);
        recyclerViewObject = fragmentView.findViewById(R.id.my_recycle_view_object);
        cardViewDealer     = fragmentView.findViewById(R.id.cardView_oder_p1_dealer);
        cardViewObject     = fragmentView.findViewById(R.id.cardView_oder_p1_object);

        proceedButton.setText(proceedButton.getText() + "   1 / 3");
        proceedButton.setEnabled(false);
        proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button_grey));

        addDealerButton.setOnClickListener(((ActivityOrder1)context).getDealerOnClickListener());
        addObjectButton.setOnClickListener(((ActivityOrder1)context).getObjectOnClickListener());

        cardViewObject.setOnClickListener(((ActivityOrder1)context).getObjectOnClickListener());
        cardViewDealer.setOnClickListener(((ActivityOrder1)context).getDealerOnClickListener());

        proceedButton.setOnClickListener(v->{
            ((ActivityOrder1)context).buttonClickCallback(StateProgressBar.StateNumber.TWO, 1);
        });

        return  fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(((ActivityOrder1)context).getMyAdapterDealer() != null){
            cardViewDealer.setVisibility(View.GONE);
            recyclerViewDealer.setVisibility(View.VISIBLE);
            recyclerViewDealer.setAdapter(((ActivityOrder1)context).getMyAdapterDealer());
            recyclerViewDealer.setLayoutManager(new LinearLayoutManager(context));
        }
        if(((ActivityOrder1)context).getMyAdapterObject() != null){
            cardViewObject.setVisibility(View.GONE);
            recyclerViewObject.setVisibility(View.VISIBLE);
            recyclerViewObject.setAdapter(((ActivityOrder1)context).getMyAdapterObject());
            recyclerViewObject.setLayoutManager(new LinearLayoutManager(context));
        }
        checkIsOkToProceed();
    }

    public void dealerSelectedCallback(int adapterPosition, MyAdapterDealerShow myAdapterDealer){
        cardViewDealer.setVisibility(View.GONE);
        recyclerViewDealer.setVisibility(View.VISIBLE);

        recyclerViewDealer.setAdapter(myAdapterDealer);
        recyclerViewDealer.setLayoutManager(new LinearLayoutManager(context));

        checkIsOkToProceed();
    }
    public void objectSelectedCallback(int adapterPosition, MyAdapterObjectShowP1 myAdapterObject) {
        cardViewObject.setVisibility(View.GONE);
        recyclerViewObject.setVisibility(View.VISIBLE);
        recyclerViewObject.setAdapter(myAdapterObject);
        recyclerViewObject.setLayoutManager(new LinearLayoutManager(context));

        checkIsOkToProceed();
    }

    private void checkIsOkToProceed() {
        if((((ActivityOrder1)context).getMyOrder().getMyDealer() != null)&&(((ActivityOrder1)context).getMyOrder().getMyObject() != null)){
            proceedButton.setEnabled(true);
            proceedButton.setBackground(getResources().getDrawable(R.drawable.round_button));
        }else{

        }
    }
}