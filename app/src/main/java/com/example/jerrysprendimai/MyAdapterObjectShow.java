package com.example.jerrysprendimai;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class MyAdapterObjectShow extends RecyclerView.Adapter<MyAdapterObjectShow.MyViewHolder>{
    Context context;

    List<ObjectObject> myObjectList;
    List<ObjectObject> myObjectListFull;
    ViewGroup parentView;

    public MyAdapterObjectShow(Context context, ViewGroup parentView, List<ObjectObject> objectList) {
        this.context = context;
        this.myObjectList = objectList;
        this.myObjectListFull = new ArrayList<>(this.myObjectList);
        this.parentView = parentView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_object, parent, false);
        return new MyViewHolder(view);
        //return null;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout objectLockLayout;
        LinearLayout myRow;
        LinearLayout bottomSheetContainer;
        TextView objectName;
        TextView objectCustomer;
        ProgressBar progressBar;
        TextView progressBarLabel;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            objectLockLayout     = itemView.findViewById(R.id.object_lock_layout);
            objectName           = itemView.findViewById(R.id.object_name);
            objectCustomer       = itemView.findViewById(R.id.object_customer);
            progressBar          = itemView.findViewById(R.id.object_progess_bar);
            progressBarLabel     = itemView.findViewById(R.id.object_progess_bar_label);
            bottomSheetContainer = itemView.findViewById(R.id.bottomSheetContainer);

            myRow = itemView.findViewById(R.id.my_container);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ObjectObject myObjectObject = this.myObjectList.get(position);

        //-------lock icon
        if(myObjectObject.getLockedByUserId().equals("0")){
            holder.objectLockLayout.setVisibility(View.GONE);
        }else{
            holder.objectLockLayout.setVisibility(View.VISIBLE);
        }

        //------object values
        holder.objectName.setText(myObjectObject.getObjectName());
        holder.objectCustomer.setText(myObjectObject.getCustomerName());
        holder.progressBar.setProgress(Integer.parseInt(String.valueOf(Math.round(Double.valueOf(myObjectObject.getCompleteness())))));
        holder.progressBarLabel.setText(myObjectObject.getCompleteness()+"%");

        if(myObjectObject.getCompleteness().equals("100.0")){
            holder.progressBarLabel.setTextColor(((ActivityObjectShow)context).getResources().getColor(R.color.jerry_green));
        }else{
            holder.progressBarLabel.setTextColor(((ActivityObjectShow)context).getResources().getColor(R.color.jerry_blue));
        }

        //---clear hints
        holder.objectName.setHint("");
        holder.objectCustomer.setHint("");

        //-----card click listener
        holder.myRow.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout, parentView, true);

            //---- bind bottomSheet values

            //---- fill bottomSheet values

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
            //bottomSheetDialog.setOnDismissListener(this::onBottomSheetDismiss);
        });

    }

    @Override
    public int getItemCount() {
        return this.myObjectList.size();
        //return 0;
    }
}
