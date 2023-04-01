package com.example.jerrysprendimai;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MyAdapterUserShow extends RecyclerView.Adapter<MyAdapterUserShow.MyViewHolder> implements View.OnClickListener, DialogInterface.OnClickListener{
    Context context;

    List<ObjectUser> myUserList;
    List<ObjectUser> myUserListFull;
    ViewGroup parentView;

    public MyAdapterUserShow(Context context, ViewGroup parentView, List<ObjectUser> userList) {
        this.context = context;
        this.myUserList = userList;
        this.myUserListFull = new ArrayList<>(this.myUserList);
        this.parentView = parentView;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView myUserName, myUserLevel, myUserFirstName, myUserLastName;
        CardView myCardViewIndicator;
        CardView myCardViewDeleteButton;
        ImageView myPersonCeckImg;
        LinearLayout myRow;
        boolean myHoldIndicator;
        TextView myPosition;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            myHoldIndicator = false;
            //---------------Element binding------------------------
            myPosition             = itemView.findViewById(R.id.user_position_txt);
            myCardViewIndicator    = itemView.findViewById(R.id.user_cardView_indicator);
            myUserName             = itemView.findViewById(R.id.user_uname_value);
            myUserLevel            = itemView.findViewById(R.id.user_ulevel_value);
            myUserFirstName        = itemView.findViewById(R.id.user_fname_value);
            myUserLastName         = itemView.findViewById(R.id.user_lname_value);
            myCardViewDeleteButton = itemView.findViewById(R.id.user_cardView_delete);
            myPersonCeckImg        = itemView.findViewById(R.id.user_image_view_check);

            myRow = itemView.findViewById(R.id.my_container);
        }

        public boolean isMyHoldIndicator() {
            return myHoldIndicator;
        }

        public void setMyHoldIndicator(boolean myHoldIndicator) {
            this.myHoldIndicator = myHoldIndicator;
        }
    }
    @NonNull
    @Override
    public MyAdapterUserShow.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_user, parent, false);
        return new MyAdapterUserShow.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapterUserShow.MyViewHolder holder, int position){
        boolean holdIndicator = false;
        //-----------------Clear Hint from value fields---------
        Field[] fields = holder.getClass().getDeclaredFields();
        for(Field field: fields){
            try{
                TextView textView = (TextView) field.get(holder);
                String stringId = context.getResources().getResourceEntryName(textView.getId());
                if (stringId.contains("value")){
                    textView.setHint("");
                }
            }catch(Exception e){
                e = e;
            }
        }
        //-----------------Read Object--------------------------
        ObjectUser objectUser = this.myUserList.get(position);
        objectUser.setMyViewHolderUserShow(holder);
        holder.myPosition.setText(String.valueOf(position));
        holder.myUserName.setText(objectUser.getUname());
        holder.myUserLevel.setText(objectUser.getUser_lv());
        holder.myUserFirstName.setText(objectUser.getFirst_name());
        holder.myUserLastName.setText(objectUser.getLast_name());

        holder.myCardViewDeleteButton.setOnClickListener(this::onClick);

        //---------------Check User level and color-code--------
        holder.myCardViewIndicator.setCardBackgroundColor(objectUser.getUserLevelIndicatorColor(context));

        //---------------hide delete indicator
        if(!holder.isMyHoldIndicator()){
            holder.myPersonCeckImg.setVisibility(View.GONE);
        }
        //---------------hide delete button for admin-----------
        /*if (objectUser.getUser_lv().equals("admin")){
            holder.myCardViewDeleteButton.setVisibility(View.GONE);
        }*/

        holder.myRow.setOnClickListener(v -> {
            ((ActivityUserShow)context).lockView();
            if (((ActivityUserShow) context).getDeletionMode().equals(true)){
                if(objectUser.getUname().equals("admin")){
                    return;
                }
                if(!holder.isMyHoldIndicator()){
                    if( holder.myPersonCeckImg.getVisibility() == View.GONE){
                      holder.myPersonCeckImg.setVisibility(View.VISIBLE);
                     ((ActivityUserShow) context).addToBeDeleted(position);
                    }else{
                      holder.myPersonCeckImg.setVisibility(View.GONE);
                      ((ActivityUserShow) context).removeToBeDeleted(position);
                    }
                }else{
                    holder.setMyHoldIndicator(false);
                }
            }else{
            //((UserShow) context).swipeRefreshCommit(true);
            parentView.findViewById(R.id.my_swipe_refresh);
            ObjectUser objectUser1 = myUserList.get(holder.getPosition());

            ObjectUser myUser = ((ActivityUserShow) context).getIntent().getParcelableExtra("myUser");

            Intent intent = new Intent(context, ActivityUserEdit.class);
            intent.putExtra("myUserEdit", objectUser1);
            intent.putExtra("myUser", myUser);
            context.startActivity(intent);
        }
        });
        /*holder.myRow.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((ActivityUserShow) context).setDeletionMode(true);
                holder.myPersonCeckImg.setVisibility(View.VISIBLE);
                return false;
            }
        });*/
        holder.myRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ActivityUserShow) context).setDeletionMode(true);
                ((ActivityUserShow) context).setButtonDeleteUser();
                ((ActivityUserShow) context).addToBeDeleted(position);
                holder.myPersonCeckImg.setVisibility(View.VISIBLE);
                holder.setMyHoldIndicator(true);
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.myUserList.size();
    }
}
