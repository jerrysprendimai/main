package com.example.jerrysprendimai;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
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
        /*View parent = getTopLevelParentView(v);
        TextView positionView = parent.findViewById(R.id.user_position_txt);
        String value = positionView.getText().toString();
        Integer position = Integer.parseInt(value);
        ObjectUser objectUser = myUserList.get(position);

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        deleteDialog.setMessage("'"+ objectUser.getUname() +"'? Löschen").setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //----------------delete button implementation---------
                        ((UserShow) context).swipeRefreshCommit(true);

                        ((UserShow) context).setWAUser(objectUser);

                        BackgroundWorker backgroundWorker = new BackgroundWorker(context);
                        backgroundWorker.execute("delete_user");
                    }
                }
        ).setNegativeButton("Nein", this).show();*/

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView myUserName, myUserLevel, myUserFirstName, myUserLastName;
        CardView myCardViewIndicator;
        CardView myCardViewDeleteButton;
        LinearLayout myRow;
        TextView myPosition;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            //---------------Element binding------------------------
            myPosition             = itemView.findViewById(R.id.user_position_txt);
            myCardViewIndicator    = itemView.findViewById(R.id.user_cardView_indicator);
            myUserName             = itemView.findViewById(R.id.user_uname_value);
            myUserLevel            = itemView.findViewById(R.id.user_ulevel_value);
            myUserFirstName        = itemView.findViewById(R.id.user_fname_value);
            myUserLastName         = itemView.findViewById(R.id.user_lname_value);
            myCardViewDeleteButton = itemView.findViewById(R.id.user_cardView_delete);

            myRow = itemView.findViewById(R.id.my_container);
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
        holder.myPosition.setText(String.valueOf(position));
        holder.myUserName.setText(objectUser.getUname());
        holder.myUserLevel.setText(objectUser.getUser_lv());
        holder.myUserFirstName.setText(objectUser.getFirst_name());
        holder.myUserLastName.setText(objectUser.getLast_name());

        holder.myCardViewDeleteButton.setOnClickListener(this::onClick);

        //---------------Check User level and color-code--------
        //holder.myCardViewIndicator.setCardBackgroundColor(objectUser.getUserLevelIndicatorColor(context));

        //---------------hide delete button for admin-----------
        if (objectUser.getUser_lv().equals("admin")){
            holder.myCardViewDeleteButton.setVisibility(View.GONE);
        }

        holder.myRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((UserShow) context).swipeRefreshCommit(true);
                parentView.findViewById(R.id.my_swipe_refresh);
                ObjectUser objectUser = myUserList.get(holder.getPosition());

                ObjectUser myUser = ((UserShow) context).getIntent().getParcelableExtra("myUser");

                /*Intent intent = new Intent(context, UserEdit.class);
                intent.putExtra("myUserEdit", objectUser);
                intent.putExtra("myUser", myUser);
                context.startActivity(intent);*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.myUserList.size();
    }
}
