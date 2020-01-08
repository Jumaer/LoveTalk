package com.example.lovetalk.user_all_recycle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lovetalk.R;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListRecyclerViewHolder> {
    ArrayList<UserObject>userListAdapters;
    public UserListAdapter(ArrayList<UserObject>userListAdapters) {
        this.userListAdapters = userListAdapters;
    }

    @NonNull
    @Override
    public UserListRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View layout_view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null,false);
       RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
       layout_view.setLayoutParams(lp);
       UserListRecyclerViewHolder rcv = new UserListRecyclerViewHolder(layout_view);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListRecyclerViewHolder holder, int position) {
        holder.name.setText(userListAdapters.get(position).getName());
        holder.phone.setText(userListAdapters.get(position).getPhone_number());

    }

    @Override
    public int getItemCount() {
        return userListAdapters.size();
    }

    public class UserListRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView name,phone;
        public UserListRecyclerViewHolder(View v){
         super(v);
         name= v.findViewById(R.id.public_name);
         phone=v.findViewById(R.id.public_phone_number);

        }
    }
}
