package com.example.jerrysprendimai;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyAdapterEmailAttachment extends RecyclerView.Adapter<MyAdapterEmailAttachment.MyViewHolder> {

    Context context;

    ArrayList<ObjectAttachment> myAttachmentList;
    ArrayList<ObjectAttachment> myAttachmentListFull;


    final String USER = "user";
    final String OWNER = "owner";
    final String ADMIN = "admin";

    public MyAdapterEmailAttachment(Context context, ArrayList<ObjectAttachment> myAttachmentList) {
        this.context = context;
        this.myAttachmentList =  myAttachmentList;
        this.myAttachmentListFull = new ArrayList<>(this.myAttachmentList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row_email_attachment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return this.myAttachmentList.size();
        //return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ObjectAttachment myAttachment = this.myAttachmentList.get(position);

        SpannableString content = new SpannableString(myAttachment.getName());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.fileName.setText(content);
        holder.myRow.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String url = "https://" + myAttachment.getUrl();
            url = url.replaceAll(" ", "%20");
            //String url = "http://docs.google.com/viewer?url=" + "https://" + myAttachment.getUrl();
            intent.setDataAndType(Uri.parse( url),  myAttachment.getMimeType());
            ((ActivityEmailRead)context).startActivity(intent);
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fileName;

        LinearLayout myRow;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fileName = itemView.findViewById(R.id.email_attachment_name);
            myRow = itemView.findViewById(R.id.my_container);

        }
    }
}
