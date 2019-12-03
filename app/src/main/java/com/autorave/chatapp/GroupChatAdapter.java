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

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

    public static final int DISPLAY_LEFT = 0;
    public static final int DISPLAY_RIGHT = 1;

    private Context mContext;
    private List<GroupChatInfo> chat;

    private FirebaseUser firebaseUser;

    public GroupChatAdapter(Context mContext, List<GroupChatInfo> chat){
        this.chat = chat;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public GroupChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == DISPLAY_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_right, parent, false);
            return new GroupChatAdapter.ViewHolder(view);
        } else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_left, parent, false);
            return new GroupChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatAdapter.ViewHolder holder, int position) {

        GroupChatInfo groupChatInfo = chat.get(position);

        holder.showMessage.setText(groupChatInfo.getMessage());

        if(position == chat.size()-1){
            if(groupChatInfo.isIsseen()){
                holder.msgSeen.setText("Seen");
            } else {
                holder.msgSeen.setText("Delivered");
            }
        } else {
            holder.msgSeen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
        public ImageView profileImage;
        public TextView msgSeen;


        public ViewHolder(View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            msgSeen = itemView.findViewById(R.id.message_seen);
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
