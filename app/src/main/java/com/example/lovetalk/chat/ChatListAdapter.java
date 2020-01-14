package com.example.lovetalk.chat;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovetalk.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListRecyclerViewHolder> {
    public Context context;
    ArrayList<ChatObject>ChatList;
    public ChatListAdapter(ArrayList<ChatObject>ChatList) {
        this.ChatList = ChatList;
    }

    @NonNull
    @Override
    public ChatListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_view.setLayoutParams(lp);
     ChatListRecyclerViewHolder rcv = new ChatListRecyclerViewHolder(layout_view);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListRecyclerViewHolder holder, final int position) {
        holder.mtitle.setText(ChatList.get(position).getChatId());



        holder.mlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }

    public class ChatListRecyclerViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mlayout;
        public TextView mtitle;
        public ChatListRecyclerViewHolder(View v){
            super(v);
mlayout = v.findViewById(R.id.layout);
mtitle= v.findViewById(R.id.title);
        }
    }
}

