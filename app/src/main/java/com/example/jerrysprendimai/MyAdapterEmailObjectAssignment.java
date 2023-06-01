package com.example.jerrysprendimai;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class MyAdapterEmailObjectAssignment extends RecyclerView.Adapter<MyAdapterEmailObjectAssignment.MyViewHolder> {


    final String USER = "user";
    final String ADMIN = "admin";
    final String OWNER = "owner";

    ArrayList<ObjectObject> myObjectList;
    ObjectObject myObject;
    Context context;
    ObjectUser myUser;
    ArrayList<RadioButton> radioButtons;

    public MyAdapterEmailObjectAssignment(Context context, ObjectUser myUser, ObjectObject myObject, ArrayList<ObjectObject> objectArrayList){
        this.context = context;
        this.myUser = myUser;
        this.myObject  = myObject;
        this.myObjectList = objectArrayList;
        this.radioButtons = new ArrayList<>();
    }
    public void addRadioButton(RadioButton radioButton){
        if(!this.radioButtons.contains(radioButton)){
            this.radioButtons.add(radioButton);
        }
    }
    public void checkRadioButton(int adapterPosition) {
        int count = 0;
        for(RadioButton radioButton: radioButtons){
            if(count == adapterPosition){
                radioButton.setChecked(true);
                count++;
                continue;
            }
            radioButton.setChecked(false);
            count++;
        }
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, customer;

        ImageView icon;
        RadioButton radioButton;
        LinearLayout myRow;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            customer   = itemView.findViewById(R.id.object_customer);
            name        = itemView.findViewById(R.id.object_name);
            icon        = itemView.findViewById(R.id.object_image_view);
            radioButton = itemView.findViewById(R.id.email_assigment_radiobutton);

            myRow       = itemView.findViewById(R.id.my_container);
        }
    }
    @Override
    public MyAdapterEmailObjectAssignment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_object_email_assign, parent, false);
        return new MyAdapterEmailObjectAssignment.MyViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull MyAdapterEmailObjectAssignment.MyViewHolder holder, int position){

        ObjectObject currentObject = myObjectList.get(holder.getAdapterPosition());

        holder.name.setText(currentObject.getObjectName());
        holder.customer.setText(currentObject.getCustomerName());
        holder.icon.setImageResource(context.getResources().getIdentifier(currentObject.getIcon(), "drawable", context.getApplicationInfo().packageName));
        holder.radioButton.setChecked(false);

        addRadioButton(holder.radioButton);
        if(myObject != null){
           if(currentObject.getId().equals(myObject.getId())){
               holder.radioButton.setChecked(true);
               ((ActivityEmailRead)context).setSelectedObjectId(currentObject.getId());
           }
        }

        holder.myRow.setOnClickListener(v->{
            //holder.radioButton.setChecked(true);
            checkRadioButton(holder.getAdapterPosition());
            ((ActivityEmailRead)context).setSelectedObjectId(currentObject.getId());
        });

        holder.radioButton.setOnClickListener(v -> {
            holder.myRow.setSoundEffectsEnabled(false);
            holder.myRow.performClick();
            holder.myRow.setSoundEffectsEnabled(true);
        });

    }



    @Override
    public int getItemCount() {
        return this.myObjectList.size();
    }
}
