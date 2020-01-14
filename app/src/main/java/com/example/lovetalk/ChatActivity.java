package com.example.lovetalk;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.lovetalk.chat.MessageAdapter;
import com.example.lovetalk.chat.ChatObject;
import com.example.lovetalk.chat.MessageObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChat;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    ArrayList<MessageObject> messageList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initializeRecyclerView();
    }
    private void initializeRecyclerView(){
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.recycle_message);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(true);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }
}
