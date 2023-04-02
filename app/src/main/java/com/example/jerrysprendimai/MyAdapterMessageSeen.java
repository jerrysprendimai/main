package com.example.jerrysprendimai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapterMessageSeen extends RecyclerView.Adapter<MyAdapterMessageSeen.MyViewHolder> {

    HashMap<String, String> users;
    ArrayList<ObjectUser> employeeList;
    ArrayList<ObjectUser>ownerList;
    ArrayList<ObjectObjUser> objectUserArrayList;
    ArrayList<Integer> seenList;
    Context context;

    public MyAdapterMessageSeen(Context context, HashMap<String, String> users, ArrayList<ObjectUser> employeeList, ArrayList<ObjectUser>ownerList, ArrayList<ObjectObjUser> objectUserArrayList){
        this.context = context;
        this.users = users;
        this.employeeList = employeeList;
        this.ownerList = ownerList;
        this.objectUserArrayList = objectUserArrayList;

        this.seenList = new ArrayList<>();
        for (ObjectObjUser objectObjUser : objectUserArrayList){
            seenList.add(objectObjUser.getUserId());
        }
        for(ObjectUser objectUser: ownerList){
            if(!objectUser.getType().equals("1")){
                seenList.add(objectUser.getId());
            }
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_message_seen, parent, false);
        return new MyViewHolder(view);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Integer displayId = seenList.get(holder.getAdapterPosition());
        String userSeen = users.get(displayId.toString());

        /*int counter = 0;
        for(Map.Entry<String, String> entry : users.entrySet()){
            if(position == counter){
              userId = entry.getKey();
              userSeen = entry.getValue();
              break;
            }
            counter++;
        }*/
        String firstName = "";
        for(int i = 0; i < employeeList.size(); i++){
           if(employeeList.get(i).getId().equals(displayId)){
               firstName = employeeList.get(i).getFirst_name();
               break;
           }
         }
        if(firstName.isEmpty()){
            for(int i = 0; i < ownerList.size(); i++){
                if(ownerList.get(i).getId().equals(displayId)){
                    firstName = ownerList.get(i).getFirst_name();
                    break;
                }
            }
        }
        try {
            holder.firstName.setText(firstName);
            if (userSeen.equals("false")) {
                holder.seenIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_dark_grey));
            } else {
                holder.seenIndicator.setColorFilter(ContextCompat.getColor(context, R.color.jerry_blue));
            }
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return seenList.size();
        //return this.users.size();
        //return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView firstName;
        ImageView seenIndicator;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            firstName     = itemView.findViewById(R.id.message_seen_first_name);
            seenIndicator = itemView.findViewById(R.id.message_seen_indicator);
        }
    }
}
