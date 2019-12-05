package com.autorave.chatapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autorave.chatapp.Templates.ChatInfo;
import com.autorave.chatapp.Activitys.ChatPage;
import com.autorave.chatapp.R;
import com.autorave.chatapp.SQLite.NameChangeDBHelper;
import com.autorave.chatapp.Templates.User;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ViewHolder> {

    NameChangeDBHelper nameChangeDBHelper;
    private Context mContext;
    private List<User> mContacts;
    private String lastMessage;
    public boolean bold = true;
    DatabaseReference reference;
    FirebaseUser firebaseUser;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        nameChangeDBHelper = new NameChangeDBHelper(mContext);

        final User user = mContacts.get(position);

        ArrayList<String> SQLData = (ArrayList)nameChangeDBHelper.getDataSQL();

        for (int i = 0; i < SQLData.size(); i++) {
            if (SQLData != null && SQLData.get(i).equals(user.getId())) {
                user.setUsername(SQLData.get(i-1));
            }
        }

        holder.mUserName.setText(user.getUsername());


        if (user.getImageURL().equals("default")) {
            holder.mUserImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(user.getImageURL()).into(holder.mUserImage);
        }

        lastMsg(user.getId(), holder.mLastMsg);

        if (user.getStatus().equals("online")) {
            holder.mStatus.setVisibility(View.VISIBLE);
            holder.mStatus.setImageResource(R.color.statusOnline);
        } else if (user.getStatus().equals("offline")) {
            holder.mStatus.setVisibility(GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatPage.class);
                intent.putExtra("userId", user.getId());
                mContext.startActivity(intent);
            }
        });

        //unread message notification
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ChatInfo chatInfo = snapshot.getValue(ChatInfo.class);

                    if(chatInfo.getSender().equals(user.getId()) &&!chatInfo.isIsseen())
                    {

                        holder.mUnreadMsg.setVisibility(View.VISIBLE);
                    } else {
                        holder.mUnreadMsg.setVisibility(GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mUserImage;
        private ImageView mStatus;
        private TextView mUserName;
        private TextView mLastMsg;
        private ImageView mUnreadMsg;

        public ViewHolder(View itemView) {

            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_chats_image);
            mStatus = itemView.findViewById(R.id.status_icon);
            mUserName = itemView.findViewById(R.id.chats_username);
            mLastMsg = itemView.findViewById(R.id.chats_last_message);
            mUnreadMsg = itemView.findViewById(R.id.unread_message);
        }
    }

    private void lastMsg(final String userId, final TextView mLastMsg) {
        if(bold){
            mLastMsg.setTypeface(null, Typeface.BOLD);
        }
        lastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatInfo chat = snapshot.getValue(ChatInfo.class);

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        lastMessage = chat.getMessage();
                    }
                }

                switch (lastMessage) {
                    case "default":
                        mLastMsg.setText("");
                        break;
                    default:
                        mLastMsg.setText(lastMessage);
                        break;
                }
                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
