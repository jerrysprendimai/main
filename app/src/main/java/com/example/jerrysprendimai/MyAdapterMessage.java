package com.example.jerrysprendimai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class MyAdapterMessage extends RecyclerView.Adapter<MyAdapterMessage.MessageHolder> {

    final String user = "user";
    final String owner = "owner";
    final String admin = "admin";

    private ArrayList <ObjectMessage> messages;
    private String senderImg, recyverImg;
    private Context context;
    private ObjectUser myUser;

    public MyAdapterMessage(ArrayList<ObjectMessage> messages, Context context, ObjectUser myUser) {
        this.messages = messages;
        this.context = context;
        this.myUser = myUser;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_row_message,parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        ObjectMessage objectMessage = messages.get(holder.getAdapterPosition());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(objectMessage.getMills()));

        holder.txtMessage.setText(objectMessage.getContent());
        holder.txtSenderName.setText(objectMessage.getFirstName());
        holder.txtTime.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + String.format("%02d",calendar.get(Calendar.MINUTE)));

        //holder.txtDate.setText(objectMessage.getDate());
        if(((ActivityChat) context).isDateToBeDiplayed(objectMessage.getDate())){
            holder.txtDateSeparator.setVisibility(View.VISIBLE);
            holder.txtDate.setVisibility(View.VISIBLE);
            holder.txtDate.setText(((ActivityChat) context).getDateToDisplay());
            ((ActivityChat) context).setDateToDisplay(objectMessage.getDate());
        }else{
            holder.txtDateSeparator.setVisibility(View.GONE);
            holder.txtDate.setVisibility(View.GONE);
        }
        if((position == 0)&&(holder.txtDateSeparator.getVisibility() == View.GONE)){
            holder.txtDateSeparator.setVisibility(View.VISIBLE);
            holder.txtDate.setVisibility(View.VISIBLE);
            holder.txtDate.setText(((ActivityChat) context).getDateToDisplay());
            ((ActivityChat) context).setDateToDisplay(objectMessage.getDate());
        }

        if(objectMessage.getUserLv().equals(owner)){
            holder.profImage.setColorFilter(context.getResources().getColor(R.color.jerry_blue));
        }else if(objectMessage.getUserLv().equals(admin)){
            holder.profImage.setColorFilter(context.getResources().getColor(R.color.jerry_green));
        }else{
            holder.profImage.setColorFilter(context.getResources().getColor(R.color.teal_700));
        }

        ConstraintLayout constraintLayout = holder.constraintLayout;
        if(objectMessage.getUserId().equals(myUser.getId().toString())){
            holder.txtMessage.setBackground(context.getResources().getDrawable(R.drawable.background_my_message));
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profile_cardView, ConstraintSet.LEFT);
            constraintSet.clear(R.id.txt_message_content, ConstraintSet.LEFT);
            constraintSet.connect(R.id.profile_cardView, ConstraintSet.RIGHT, R.id.ccLayout,ConstraintSet.RIGHT, 3);
            constraintSet.connect(R.id.txt_message_content, ConstraintSet.RIGHT,  R.id.profile_cardView,    ConstraintSet.LEFT, 3);

            constraintSet.clear(R.id.txt_time, ConstraintSet.LEFT);
            constraintSet.clear(R.id.txt_time, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.txt_time, ConstraintSet.LEFT, R.id.txt_message_content, ConstraintSet.LEFT, 3);
            constraintSet.connect(R.id.txt_time, ConstraintSet.TOP, R.id.txt_message_content, ConstraintSet.BOTTOM, 3);

            //constraintSet.clear(R.id.chat_date_line, ConstraintSet.LEFT);
            //constraintSet.connect(R.id.chat_date_line, ConstraintSet.TOP, R.id.chat_mainContainer, ConstraintSet.TOP, 5);

            constraintSet.clear(R.id.ccLayout,   ConstraintSet.TOP);
            constraintSet.connect(R.id.ccLayout, ConstraintSet.TOP, R.id.chat_date_line, ConstraintSet.BOTTOM, 3);


            constraintSet.applyTo(constraintLayout);
            holder.cardView.setVisibility(View.GONE);
            holder.txtSenderName.setVisibility(View.GONE);
        }else{
            holder.txtMessage.setBackground(context.getResources().getDrawable(R.drawable.background_message));
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.profile_cardView, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.txt_message_content, ConstraintSet.RIGHT);
            constraintSet.connect(R.id.profile_cardView, ConstraintSet.LEFT, R.id.ccLayout,ConstraintSet.LEFT, 0);
            constraintSet.connect(R.id.txt_message_content, ConstraintSet.LEFT, R.id.profile_cardView,ConstraintSet.RIGHT, 0);

            constraintSet.clear(R.id.txt_time, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.txt_time, ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.txt_time, ConstraintSet.RIGHT, R.id.txt_message_content, ConstraintSet.RIGHT, 3);
            constraintSet.connect(R.id.txt_time, ConstraintSet.TOP, R.id.txt_message_content, ConstraintSet.BOTTOM, 3);

            constraintSet.clear(R.id.chat_date_line, ConstraintSet.LEFT);
            constraintSet.connect(R.id.chat_date_line, ConstraintSet.TOP, R.id.chat_mainContainer, ConstraintSet.TOP, 3);

            constraintSet.clear(R.id.ccLayout,   ConstraintSet.TOP);
            constraintSet.connect(R.id.ccLayout, ConstraintSet.TOP, R.id.chat_date_line, ConstraintSet.BOTTOM, 3);

            constraintSet.applyTo(constraintLayout);
            holder.cardView.setVisibility(View.VISIBLE);
            holder.txtSenderName.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    class MessageHolder extends RecyclerView.ViewHolder{
        ConstraintLayout constraintLayout;
        TextView txtMessage, txtSenderName, txtTime, txtDateSeparator, txtDate;
        ImageView profImage;
        CardView cardView;

        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            constraintLayout = itemView.findViewById(R.id.ccLayout);
            txtMessage       = itemView.findViewById(R.id.txt_message_content);
            txtSenderName    = itemView.findViewById(R.id.message_senderName);
            txtDate          =  itemView.findViewById(R.id.chat_date);
            txtTime          = itemView.findViewById(R.id.txt_time);
            txtDateSeparator = itemView.findViewById(R.id.chat_date_line);
            profImage        = itemView.findViewById(R.id.senderImage);
            cardView         = itemView.findViewById(R.id.profile_cardView);
        }
    }
}
