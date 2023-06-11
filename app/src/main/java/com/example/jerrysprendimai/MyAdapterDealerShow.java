package com.example.jerrysprendimai;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterDealerShow extends RecyclerView.Adapter<MyAdapterDealerShow.MyViewHolder>{
    private final String dealerShow = "dealerShow";
    private final String dealerShowDialogP1 = "dealerShowDialogP1";
    private final String dealerShowP1 = "dealerShowP1";
    private final String dealerShowP3 = "dealerShowP3";
    private final String dealerShowEmailFilter = "dealerShowEmailFilter";

    Context context;

    List<ObjectDealer> myDealerList;
    List<ObjectDealer> myDealerListFull;
    ViewGroup parentView;
    String type;
    View.OnClickListener onClickListener;
    ArrayList<MyViewHolder> myViewHolderArrayList;
    Boolean infalteSmall;
    ObjectUser myUser;


    public MyAdapterDealerShow(Context context, List<ObjectDealer> myDealerList, List<ObjectDealer> myDealerListFull, String type, View.OnClickListener onClickListener, Boolean infalteSmall, ObjectUser myUser) {
        this.context = context;
        this.myDealerList = myDealerList;
        this.myDealerListFull = myDealerListFull;
        this.parentView = parentView;
        this.type = type;
        this.onClickListener = onClickListener;
        this.myViewHolderArrayList = new ArrayList<>();
        this.infalteSmall = infalteSmall;
        this.myUser = myUser;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dealerName, dealerEmail;
        ImageView checkedImage;
        LinearLayout myRow;
        boolean myHoldIndicator;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dealerName  = itemView.findViewById(R.id.dealer_name);
            dealerEmail = itemView.findViewById(R.id.dealer_email);
            checkedImage = itemView.findViewById(R.id.dealer_img_delete);
            myRow = itemView.findViewById(R.id.dealer_row);
        }
        public void setMyHoldIndicator(boolean value) {  this.myHoldIndicator = value; }
        public boolean isMyHoldIndicator() {
            return myHoldIndicator;
        }
    }

    @NonNull
    @Override
    public MyAdapterDealerShow.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = null;
        if(!infalteSmall){
          view = inflater.inflate(R.layout.my_row_dealer, parent, false);
        }else{
          view = inflater.inflate(R.layout.my_row_dealer_small, parent, false);
        }

        return new MyAdapterDealerShow.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterDealerShow.MyViewHolder holder, int position) {
        ObjectDealer objectDealer = myDealerList.get(holder.getAdapterPosition());
        objectDealer.setMyViewHolderUserShow(holder);

        //---------------hide delete indicator
        if(!holder.isMyHoldIndicator()){
            holder.checkedImage.setVisibility(View.GONE);
        }

        if(!myViewHolderArrayList.contains(holder)){
            myViewHolderArrayList.add(holder);
        }

        View.OnClickListener onRowClickListener = null;
        View.OnLongClickListener onLongClickListener = null;
        if(type.equals(dealerShow)){
            onRowClickListener = v-> {
                if(((ActivityDealerShow)context).isDeletionMode()){
                    if(!holder.isMyHoldIndicator()){
                        if( holder.checkedImage.getVisibility() == View.GONE){
                            holder.checkedImage.setVisibility(View.VISIBLE);
                            ((ActivityDealerShow) context).addToBeDeleted(position);
                        }else{
                            holder.checkedImage.setVisibility(View.GONE);
                            ((ActivityDealerShow) context).removeToBeDeleted(position);
                        }
                    }else{
                        holder.setMyHoldIndicator(false);
                    }
                }else{
                    ((ActivityDealerShow)context).lockView();
                    Intent intent = new Intent(context, ActivityDealerEdit.class);
                    intent.putExtra("myDealer", objectDealer);
                    intent.putExtra("myUser", myUser);
                    context.startActivity(intent);
                }
            };
            if((myUser.getUser_lv().equals("admin"))||(myUser.getUser_lv().equals("owner"))){
                onLongClickListener = v -> {
                    ((ActivityDealerShow)context).setDeletionMode(true);
                    ((ActivityDealerShow) context).setButtonDeleteDealer(true);
                    ((ActivityDealerShow) context).addToBeDeleted(position);
                    holder.checkedImage.setVisibility(View.VISIBLE);
                    holder.setMyHoldIndicator(true);
                    return false;
                };
            }

        }else if(type.equals(dealerShowDialogP1)){
            onRowClickListener = v-> {
                ((ActivityOrder1)context).dealerSelectedCallback(holder.getAdapterPosition());
            };
        }else if(type.equals(dealerShowP1)){
            onRowClickListener = onClickListener;
        }else if(type.equals(dealerShowEmailFilter)){
            onRowClickListener = v-> {
                ((ActivityEmailShow)context).dealerSelectedCallback(holder.getAdapterPosition());
            };
        }

        holder.dealerName.setText(objectDealer.getName());
        holder.dealerEmail.setText(objectDealer.getEmail());
        if(!type.equals(dealerShowP3)){
            holder.myRow.setOnClickListener(onRowClickListener);
            holder.myRow.setOnLongClickListener(onLongClickListener);
        }else{
            holder.myRow.setClickable(false);
            holder.myRow.setEnabled(false);
            holder.myRow.setBackgroundColor(context.getResources().getColor(R.color.jerry_grey_light));
        }

    }

    @Override
    public int getItemCount() {        return this.myDealerList.size();    }


}
