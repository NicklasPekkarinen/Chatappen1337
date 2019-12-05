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

import com.autorave.chatapp.Activitys.Group;
import com.autorave.chatapp.Activitys.GroupChatPage;
import com.autorave.chatapp.Templates.GroupChatInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

class GroupChatsListAdapter extends RecyclerView.Adapter<GroupChatsListAdapter.ViewHolder> {

    private Context mContext;
    private List<Group> mGroups;
    private List<String> groupIds;
    private String lastMessage;

    public GroupChatsListAdapter(List<Group> mGroups, Context mContext) {
        this.mGroups = mGroups;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupChatsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_chats_item, parent, false);
        return new GroupChatsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupChatsListAdapter.ViewHolder holder, int position) {

        final Group group = mGroups.get(position);
        groupIds = new ArrayList<>();

        holder.mUserName.setText(group.getName());
        holder.mUserImage.setImageResource(R.mipmap.ic_launcher);

        for (int i = 0; i < group.getMembers().size(); i++) {
            groupIds.add(group.getMembers().get(i).getId());
        }

        lastMsg(groupIds, holder.mLastMsg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupChatPage.class);
                intent.putExtra("groupId", group.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView mUserImage;
        private ImageView mStatus;
        private TextView mUserName;
        private TextView mLastMsg;

        public ViewHolder(View itemView) {
            super(itemView);
            mUserImage = itemView.findViewById(R.id.user_chats_image);
            mStatus = itemView.findViewById(R.id.status_icon);
            mUserName = itemView.findViewById(R.id.chats_username);
            mLastMsg = itemView.findViewById(R.id.chats_last_message);
        }
    }

    private void lastMsg(final List<String> groupIds, final TextView mLastMsg) {
        lastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("GroupChats");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupChatInfo chat = snapshot.getValue(GroupChatInfo.class);

                    for (int i = 0; i < chat.getReceivers().size(); i++) {
                        if (chat.getReceivers().get(i).equals(firebaseUser.getUid()) && chat.getSender().equals(groupIds.get(i)) ||
                                chat.getReceivers().get(i).equals(groupIds.get(i)) && chat.getSender().equals(firebaseUser.getUid())) {
                            lastMessage = chat.getMessage();
                        }
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
