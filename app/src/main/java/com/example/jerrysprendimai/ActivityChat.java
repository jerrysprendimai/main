package com.example.jerrysprendimai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class ActivityChat extends AppCompatActivity {

    private ObjectUser myUser;
    private ObjectObject myObject;
    private ArrayList<ObjectObjUser> objectUserArrayList;
    private ArrayList<ObjectObject> myObjectList;
    private ArrayList<ObjectObject> myObjectListOriginal;
    private ArrayList<String> myDisplayDates;

    private RecyclerView recyclerView;
    private EditText editMessageInput;
    private TextView txtChattingAbout;
    private ProgressBar progressBar;
    private ImageView imgToolBar, sendButton;

    private String chatRoomId;

    private MyAdapterMessage myAdapterMessage;
    private ArrayList<ObjectMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //----bind screen elements
        recyclerView     = findViewById(R.id.chat_recyclerView);
        editMessageInput = findViewById(R.id.chat_editText);
        txtChattingAbout = findViewById(R.id.chat_roomName);
        progressBar      = findViewById(R.id.chat_progressBar);
        imgToolBar       = findViewById(R.id.chat_room_image);
        sendButton       = findViewById(R.id.chat_sendButton);
        messages = new ArrayList<>();

        //---------------Read Intent values----------------------
        this.myUser   = getIntent().getParcelableExtra("myUser");
        this.myObject = getIntent().getParcelableExtra("objectObject");
        this.objectUserArrayList     = getIntent().getParcelableArrayListExtra("listUser");


        //----Initialize screen values
        myDisplayDates = new ArrayList<>();
        chatRoomId = myObject.getId().toString();
        imgToolBar.setImageResource(getResources().getIdentifier(myObject.getIcon(), "drawable", getApplicationInfo().packageName));
        txtChattingAbout.setText(myObject.getObjectName());


        //----Listeners
        sendButton.setOnClickListener(v->{
            FirebaseDatabase.getInstance().getReference("objects/" + chatRoomId).push().setValue(new ObjectMessage(myUser.getFirst_name(),
                                                                                                                        myUser.getUname(),
                                                                                                                        myUser.getId().toString(),
                                                                                                                        editMessageInput.getText().toString(),
                                                                                                                        HelperDate.get_current_date_disply(),
                                                                                                                        Calendar.getInstance().getTime().toString(),
                                                                                                                        String.valueOf(Calendar.getInstance().getTimeInMillis())));
            editMessageInput.setText("");
        });

        myAdapterMessage = new MyAdapterMessage(messages, this, myUser);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapterMessage);
        setUpChatRoom();


        /*
        FirebaseDatabase.getInstance().getReference("objects/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */
    }

    public boolean isDateDisplayed(String date){
        boolean value = true;

        if(!myDisplayDates.contains(date)){
            myDisplayDates.add(date);
            value = false;
        }

        return value;
    }

    private void setUpChatRoom(){
        attachMessageListener(chatRoomId);
    }

    private void attachMessageListener(String chatRoomId){
        FirebaseDatabase.getInstance().getReference("objects/" + chatRoomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    messages.add(dataSnapshot.getValue(ObjectMessage.class));
                }
                myAdapterMessage.notifyDataSetChanged();
                recyclerView.scrollToPosition(messages.size() - 1);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}