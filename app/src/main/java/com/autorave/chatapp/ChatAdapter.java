package com.autorave.chatapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int DISPLAY_LEFT = 0;
    public static final int DISPLAY_RIGHT = 1;

    private Context mContext;
    private List<ChatInfo> chat;

    private FirebaseUser firebaseUser;

    public ChatAdapter(Context context, List<ChatInfo> chat){
        this.chat = chat;
        this.mContext = context;

    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == DISPLAY_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_right, parent);
            return new ChatAdapter.ViewHolder(view);
        } else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_left, parent);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {

        ChatInfo chatInfo = chat.get(position);

        holder.showMessage.setText(chatInfo.getMessage());

    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
        public ImageView profileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
        }


    }

    @Override
    public int getItemViewType(int position){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chat.get(position).getSender().equals(firebaseUser.getUid())){
            return DISPLAY_RIGHT;
        } else{
            return DISPLAY_LEFT;
        }
    }

}
