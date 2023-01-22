package com.example.jerrysprendimai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterObjectAsignedUser extends RecyclerView.Adapter<MyAdapterObjectAsignedUser.MyViewHolder> {
    Context context;

    ArrayList<ObjectObjUser> myUserList;
    ArrayList<ObjectObjUser> myUserListFull;

    public MyAdapterObjectAsignedUser(Context context, ArrayList<ObjectObjUser> userList){
        this.context = context;
        this.myUserList = userList;
        this.myUserListFull = new ArrayList<>(this.myUserList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView myUserName;
        TextView myPosition;
        LinearLayout myRow;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            myPosition = itemView.findViewById(R.id.user_position_txt);
            myUserName = itemView.findViewById(R.id.user_uname_value);
            myRow   = itemView.findViewById(R.id.my_container);
        }
    }
        @NonNull
        @Override
        public MyAdapterObjectAsignedUser.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.my_row_object_assigned_user, parent, false);
            return new MyAdapterObjectAsignedUser.MyViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObjectObjUser objectObjUser = this.myUserList.get(position);

        holder.myPosition.setText(String.valueOf(position));
        holder.myUserName.setText(objectObjUser.getUname());

        holder.myRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return this.myUserList.size();
    }
}
