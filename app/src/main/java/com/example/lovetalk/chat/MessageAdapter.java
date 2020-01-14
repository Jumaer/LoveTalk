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

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageRecyclerViewHolder> {
    public Context context;
    ArrayList<MessageObject> message;
    public MessageAdapter(ArrayList<MessageObject>message) {
        this.message = message;
    }

    @NonNull
    @Override
    public MessageRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_view.setLayoutParams(lp);
        MessageRecyclerViewHolder rcv = new MessageRecyclerViewHolder(layout_view);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageRecyclerViewHolder holder, final int position) {
  holder.mMessage.setText(message.get(position).getMessage());
        holder.mSender.setText(message.get(position).getSenderId());
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    public class MessageRecyclerViewHolder extends RecyclerView.ViewHolder {
         LinearLayout mlayout;
        TextView mMessage,mSender;

        MessageRecyclerViewHolder(View v){
            super(v);
         //   mlayout = v.findViewById(R.id.layout);
            mMessage = v.findViewById(R.id.message);
            mSender=v.findViewById(R.id.sender);

        }
    }
}

