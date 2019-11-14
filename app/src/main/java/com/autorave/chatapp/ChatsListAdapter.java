package com.autorave.chatapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> {

    private Context mContext;
    private List<User> mContacts;

    public ChatsListAdapter(List<User> mContacts, Context mContext) {

        this.mContext = mContext;
        this.mContacts = mContacts;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_chats_item, parent, false);
        return new ChatsListAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = mContacts.get(position);
        holder.mUserName.setText(user.getUsername());
        holder.mUserImage.setImageResource(R.mipmap.ic_launcher);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, chatPage.class);
                intent.putExtra("userId", user.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mUserImage;
        private TextView mUserName;
        private TextView mLastMsg;

        public ViewHolder(View itemView) {

            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_chats_image);
            mUserName = itemView.findViewById(R.id.chats_username);
            mLastMsg = itemView.findViewById(R.id.chats_last_message);
        }
    }
}