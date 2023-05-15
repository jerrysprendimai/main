package com.example.jerrysprendimai;

import android.content.Context;
import android.graphics.Typeface;
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

public class MyAdapterEmailShow extends RecyclerView.Adapter<MyAdapterEmailShow.MyViewHolder> {

    Context context;

    List<ObjectOrder> myEmailList;
    List<ObjectOrder> myEmailListFull;
    ViewGroup parentView;

    final String USER = "user";
    final String OWNER = "owner";
    final String ADMIN = "admin";

    public MyAdapterEmailShow(Context context, List<ObjectOrder> myEmailList) {
        this.context = context;
        //this.parentView = parentView;
        this.myEmailList = myEmailList;
        this.myEmailListFull = new ArrayList<>(this.myEmailList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_email, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.myEmailList.size();
        //return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ObjectOrder myOrder = myEmailList.get(position);

        if(myOrder.getObjectID().equals(0)){
            holder.objectAssignedIndicator.setVisibility(View.VISIBLE);
        }else{
            holder.objectAssignedIndicator.setVisibility(View.GONE);
        }
        String[] fromArray = myOrder.getFrom().split("<");
        holder.emailSender.setHint(fromArray[0]);
        //holder.emailSender.setHint(myOrder.getFrom());
        holder.emailDate.setText(myOrder.getSentDate());
        holder.emailSubject.setHint(myOrder.getMyText());

        if(myOrder.isNotViewed()){
            holder.newIndicator.setVisibility(View.VISIBLE);
            holder.emailSender.setTypeface(null, Typeface.BOLD);
            holder.emailSubject.setTypeface(null, Typeface.BOLD);
            holder.emailSender.setText(fromArray[0]);
            holder.emailSubject.setText(myOrder.getMyText());
        }else{
            holder.newIndicator.setVisibility(View.GONE);
            holder.emailSender.setTypeface(null, Typeface.NORMAL);
            holder.emailSubject.setTypeface(null, Typeface.NORMAL);
            holder.emailSender.setText("");
            holder.emailSubject.setText("");
        }
        if(myOrder.isHasAttachments()){
            holder.attachmentIndicator.setVisibility(View.VISIBLE);
        }else{
            holder.attachmentIndicator.setVisibility(View.GONE);
        }
        if(myOrder.getType().equals("in")){
            holder.emailTypeIn.setVisibility(View.VISIBLE);
            holder.emailTypeOut.setVisibility(View.GONE);
        }else{
            holder.emailTypeIn.setVisibility(View.GONE);
            holder.emailTypeOut.setVisibility(View.VISIBLE);
        }

        holder.myRow.setOnClickListener(v->{

        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView newIndicator, objectAssignedIndicator, emailSender, emailDate, emailSubject, emailText;
        ImageView attachmentIndicator, emailTypeIn, emailTypeOut;

        LinearLayout myRow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            attachmentIndicator = itemView.findViewById(R.id.email_attachment_indicator);
            newIndicator = itemView.findViewById(R.id.email_new_indicator);
            objectAssignedIndicator = itemView.findViewById(R.id.email_not_assigned_indicator);
            emailSender  = itemView.findViewById(R.id.email_sender);
            emailDate    = itemView.findViewById(R.id.email_date);
            emailSubject = itemView.findViewById(R.id.email_subject);
            emailTypeIn  = itemView.findViewById(R.id.email_type_in);
            emailTypeOut  = itemView.findViewById(R.id.email_type_out);

            myRow = itemView.findViewById(R.id.my_container);

        }
    }
}
