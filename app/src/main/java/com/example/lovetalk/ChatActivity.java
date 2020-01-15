package com.example.lovetalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lovetalk.chat.MediaAdapter;
import com.example.lovetalk.chat.MessageAdapter;
import com.example.lovetalk.chat.ChatObject;
import com.example.lovetalk.chat.MessageObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChat,mMedia;
    private RecyclerView.Adapter mChatAdapter,mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager,mMediaLayoutManager;
    ArrayList<MessageObject> messageList;


    String chatID;
    DatabaseReference mChatdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");
        mChatdb =  FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
        Button  msend = findViewById(R.id.send);
        Button maddMedia = findViewById(R.id.add_media);
        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        maddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        initializeMessage();
        initializeMedia();
        getChatMessages();
    }
    private void getChatMessages(){
     mChatdb.addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             if(dataSnapshot.exists()){
                 String text ="",
                         creatorID ="";
                 if(dataSnapshot.child("text").getValue() != null)
                     text = dataSnapshot.child("text").getValue().toString();
                 if(dataSnapshot.child("creator").getValue() != null)
                     creatorID = dataSnapshot.child("creator").getValue().toString();


                 MessageObject mMessage = new MessageObject(dataSnapshot.getKey(),creatorID,text);
                 messageList.add(mMessage);
                 mChatLayoutManager.scrollToPosition(messageList.size()-1);
                 mChatAdapter.notifyDataSetChanged();
             }






         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });
    }

    private void initializeMessage(){
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.recycle_message);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(true);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }



    int PICK_IMAGE_INTENT = 1;
    ArrayList<String>mediaUriList = new ArrayList<>();

    private void initializeMedia(){
        messageList = new ArrayList<>();
        mMedia = findViewById(R.id.media_list);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(true);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mChatAdapter = new MediaAdapter(mediaUriList,getApplicationContext());
        mMedia.setAdapter(mMediaAdapter);
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture(s)"),PICK_IMAGE_INTENT);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_OK){
            if(requestCode== PICK_IMAGE_INTENT){
                if(data.getClipData()==null){
                    mediaUriList.add(data.getData().toString());
                }
                else {
                    for (int i = 0;i<data.getClipData().getItemCount();i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void sendMessage(){
        EditText mMessage = findViewById(R.id.message_write);
        if(!mMessage.getText().toString().isEmpty()){
            DatabaseReference newMessageDB = mChatdb.push();

            Map newMessageMap = new HashMap<>();
            newMessageMap.put("text",mMessage.getText().toString());
            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            newMessageDB.updateChildren(newMessageMap);
        }
        mMessage.setText(null);
    }
}
