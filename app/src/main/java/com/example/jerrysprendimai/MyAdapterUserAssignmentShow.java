package com.example.jerrysprendimai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterUserAssignmentShow extends RecyclerView.Adapter<MyAdapterUserAssignmentShow.MyViewHolder> {
    Context context;

    final String USER = "user";
    final String ADMIN = "admin";
    final String OWNER = "owner";

    ArrayList<ObjectUser> myUserList;
    ArrayList<ObjectUser> myUserListFull;
    ArrayList<ObjectObjUser> myAssignedUser;
    ObjectObject clickObject;
    ObjectUser myUser;

    public MyAdapterUserAssignmentShow(Context context, ArrayList<ObjectUser> userList, ObjectObject obj, ArrayList<ObjectObjUser> objUserArrayList, ObjectUser myUser){
        this.context = context;
        this.myUserList = userList;
        this.myUserListFull  = new ArrayList<>(this.myUserList);
        this.myAssignedUser = objUserArrayList;
        this.clickObject = obj;
        this.myUser = myUser;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView myUserName;
        TextView myPosition;
        LinearLayout myRow;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            myPosition = itemView.findViewById(R.id.user_position_txt);
            myUserName = itemView.findViewById(R.id.user_uname_value);
            checkBox   = itemView.findViewById(R.id.user_position_checkBox);
            myRow      = itemView.findViewById(R.id.my_container);
        }
    }
    @Override
    public MyAdapterUserAssignmentShow.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_user_assignment, parent, false);
        return new MyAdapterUserAssignmentShow.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapterUserAssignmentShow.MyViewHolder holder, int position){

        ObjectUser objectUser = this.myUserList.get(position);
        holder.myUserName.setText(objectUser.getUname());
        objectUser.setChecked(false);
        for(int i= 0; i < this.myAssignedUser.size(); i++){
            if(this.myAssignedUser.get(i).getUserId().equals(objectUser.getId())){
                holder.checkBox.setChecked(true);
                objectUser.setChecked(true);
                break;
            }
        }
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked == true){
                objectUser.setChecked(true);
            }else{
                objectUser.setChecked(false);
            }
        });

        if((myUser.getUser_lv().equals(ADMIN)) || (myUser.getUser_lv().equals(OWNER))){
            holder.myRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ( objectUser.getChecked() == true){
                        holder.checkBox.setChecked(false);
                    }else{
                        holder.checkBox.setChecked(true);
                    }
                }
            });
        }else if(myUser.getUser_lv().equals(USER)){
            holder.checkBox.setEnabled(false);
            holder.myRow.setEnabled(false);
        }

    }

    @Override
    public int getItemCount() {
        return this.myUserList.size();
    }
}
