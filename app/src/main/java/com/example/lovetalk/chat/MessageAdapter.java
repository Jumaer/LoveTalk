package com.example.lovetalk.chat;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovetalk.R;
import com.stfalcon.frescoimageviewer.ImageViewer;

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
    public void onBindViewHolder(@NonNull final MessageAdapter.MessageRecyclerViewHolder holder, final int position) {
  holder.mMessage.setText(message.get(position).getMessage());
        holder.mSender.setText(message.get(position).getSenderId());

        if(message.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty())
            holder.mViewMedia.setVisibility(View.GONE);
        holder.mViewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), message.get(holder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    public class MessageRecyclerViewHolder extends RecyclerView.ViewHolder {
         LinearLayout mlayout;
        TextView mMessage,mSender;
        Button mViewMedia;

        MessageRecyclerViewHolder(View v){
            super(v);
         //   mlayout = v.findViewById(R.id.layout);
            mMessage = v.findViewById(R.id.message);
            mSender=v.findViewById(R.id.sender);
            mViewMedia = v.findViewById(R.id.view_media);

        }
    }
}

