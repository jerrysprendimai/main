package com.example.jerrysprendimai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapterDealerShow extends RecyclerView.Adapter<MyAdapterDealerShow.MyViewHolder>{
    private final String dealerShow = "dealerShow";
    private final String dealerShowDialogP1 = "dealerShowDialogP1";
    private final String dealerShowP1 = "dealerShowP1";
    Context context;

    List<ObjectDealer> myDealerList;
    List<ObjectDealer> myDealerListFull;
    ViewGroup parentView;
    String type;
    View.OnClickListener onClickListener;

    public MyAdapterDealerShow(Context context, List<ObjectDealer> myDealerList, List<ObjectDealer> myDealerListFull, String type, View.OnClickListener onClickListener) {
        this.context = context;
        this.myDealerList = myDealerList;
        this.myDealerListFull = myDealerListFull;
        this.parentView = parentView;
        this.type = type;
        this.onClickListener = onClickListener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dealerName, dealerEmail;
        LinearLayout myRow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dealerName  = itemView.findViewById(R.id.dealer_name);
            dealerEmail = itemView.findViewById(R.id.dealer_email);
            myRow = itemView.findViewById(R.id.dealer_row);
        }
    }

    @NonNull
    @Override
    public MyAdapterDealerShow.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_dealer, parent, false);
        return new MyAdapterDealerShow.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDealerShow.MyViewHolder holder, int position) {
        ObjectDealer objectDealer = myDealerList.get(holder.getAdapterPosition());

        View.OnClickListener onRowClickListener = null;
        if(type.equals(dealerShow)){
            onRowClickListener = v-> {

            };
        }else if(type.equals(dealerShowDialogP1)){
            onRowClickListener = v-> {
                ((ActivityOrder1)context).dealerSelectedCallback(holder.getAdapterPosition());
            };
        }else if(type.equals(dealerShowP1)){
            onRowClickListener = onClickListener;
        }

        holder.dealerName.setText(objectDealer.getName());
        holder.dealerEmail.setText(objectDealer.getEmail());
        holder.myRow.setOnClickListener(onRowClickListener);
    }

    @Override
    public int getItemCount() {        return this.myDealerList.size();    }


}
