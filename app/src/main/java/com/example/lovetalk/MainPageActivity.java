package com.example.lovetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.example.lovetalk.chat.ChatListAdapter;
import com.example.lovetalk.chat.ChatObject;
import com.example.lovetalk.user_all_recycle.UserObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity {

    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    ArrayList<ChatObject> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Button m_logOut = findViewById(R.id.logOut);
        m_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });
        Button m_find_User = findViewById(R.id.find_user);
        m_find_User.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),FindUserActivity.class);
                startActivity(intent);
            }
        });
        getPermissions();
        initializeRecyclerView();
        getUserChatList();
    }


    private  void getUserChatList(){
        DatabaseReference mUserChatDb = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        mUserChatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(dataSnapshot.exists()){
                     for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                         ChatObject mChat = new ChatObject(childSnapshot.getKey());
                         boolean exists = false;
                         for(ChatObject mChatIterator : chatList){
                             if(mChatIterator.getChatId().equals(mChat.getChatId()))
                                 exists= true;
                         }
                         if (exists)
                             continue;
                         chatList.add(mChat);
                         mChatListAdapter.notifyDataSetChanged();
                     }
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void initializeRecyclerView(){
        chatList = new ArrayList<>();
        mChatList = findViewById(R.id.chatList);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(true);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mChatList.setLayoutManager(mChatLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }
    private void getPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_CONTACTS},1);
        }
    }
}
