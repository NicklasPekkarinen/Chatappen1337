package com.autorave.chatapp.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autorave.chatapp.Templates.ChatInfo;
import com.autorave.chatapp.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    public static final int DISPLAY_LEFT = 0;
    public static final int DISPLAY_RIGHT = 1;

    private Context mContext;
    private List<ChatInfo> chat;
    private String imageUrl;


    private FirebaseUser firebaseUser;

    public ChatAdapter(Context mContext, List<ChatInfo> chat, String imageUrl){
        this.chat = chat;
        this.mContext = mContext;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == DISPLAY_RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_right, parent, false);
            return new ChatAdapter.ViewHolder(view);
        } else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, final int position) {

        ChatInfo chatInfo = chat.get(position);

        holder.showMessage.setText(chatInfo.getMessage());

        if(imageUrl.equals("default")){
            holder.profileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(imageUrl).into(holder.profileImage);
        }

        if(position == chat.size()-1){
          if(chatInfo.isIsseen()){
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
        //RelativeLayout msgLayout;

        public ViewHolder(View itemView) {
                super(itemView);

            showMessage = itemView.findViewById(R.id.show_message);
            profileImage = itemView.findViewById(R.id.profile_image);
            msgSeen = itemView.findViewById(R.id.message_seen);
            //msgLayout = itemView.findViewById(R.id.msg_layout);
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
